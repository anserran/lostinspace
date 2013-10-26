
package es.eucm.lostinspace.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import es.eucm.ead.tools.xml.XMLNode;
import es.eucm.lostinspace.core.parsers.ActionsCreator;
import es.eucm.lostinspace.core.parsers.Parser;
import es.eucm.lostinspace.core.screens.PhaseScreen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LevelManager {

	public static final int COMMAND_POINTS = 10;

	/** Accumulated totalLevels */
	private int totalLevels[];

	/** Levels acquired in the current phase */
	private int levels[];

	/** Current phase score */
	private int score;

	/** Accumulated score */
	private int totalScore;

	/** Current phase instructions */
	private int instructions[];

	/** Accumulated instructions */
	private int totalInstructions[];

	/** Auxiliary var */
	private List<XMLNode> nodesToRemove;

	/** App preferences */
	private Preferences pref;

	public LevelManager () {
		totalLevels = new int[Abilities.values().length];
		levels = new int[Abilities.values().length];
		score = 0;
		totalScore = 0;
		instructions = new int[Abilities.values().length];
		totalInstructions = new int[Abilities.values().length];
		resetLevels();

		nodesToRemove = new ArrayList<XMLNode>();
		pref = Gdx.app.getPreferences("lis");
	}

	/** Reset all abilities totalLevels */
	public void resetLevels () {
		for (int i = 0; i < Abilities.values().length; i++) {
			totalLevels[i] = -1;
			levels[i] = 0;
		}
	}

	/** Sets all abilities to maximum (intended for debug) */
	public void maxLevels () {
		for (int i = 0; i < Abilities.values().length; i++) {
			totalLevels[i] = Abilities.values()[i].getMaxLevel();
			levels[i] = 0;
		}
	}

	/** Returns the current level for the given ability
	 * 
	 * @param a the ability
	 * @return the current level */
	public int getCurrentLevel (Abilities a) {
		return Math.min(a.getMaxLevel(), totalLevels[a.ordinal()] + levels[a.ordinal()]);
	}

	/** Ups one level
	 * 
	 * @param a the ability */
	public void upLevel (Abilities a) {
		if (totalLevels[a.ordinal()] + levels[a.ordinal()] < a.getMaxLevel()) {
			levels[a.ordinal()]++;
			PhaseScreen.communicator.addMessage("info", PhaseScreen.i18n("Part recovered! Click on it to see how to use it."));
		}
	}

	/** Reset phase results */
	public void rollbackResults () {
		for (int i = 0; i < levels.length; i++) {
			levels[i] = 0;
		}
		score = 0;
		for (int i = 0; i < instructions.length; i++) {
			instructions[i] = 0;
		}
	}

	/** Accumulate phase totalLevels and total totalLevels */
	public void commitResults () {
		for (int i = 0; i < levels.length; i++) {
			totalLevels[i] += levels[i];
		}
		totalScore += score;
		int total = 0;
		for (int i = 0; i < instructions.length; i++) {
			totalInstructions[i] += instructions[i];
			total += instructions[i];
		}
		totalScore -= total * COMMAND_POINTS;
		storeLevels();
		rollbackResults();
	}

	/** Adds points to the score
	 * 
	 * @param points */
	public void addPoints (int points, float x, float y) {
		score += points;
		PhaseScreen.mapHud.showAddScore(points, x, y);
	}

	/** Adds one to the ability's counter
	 * 
	 * @param a the ability */
	public void addInstruction (Abilities a) {
		instructions[a.ordinal()]++;
	}

	/** @return the phase score */
	public int getScore () {
		return score;
	}

	/** @return the total score accumulated plus the current phase score */
	public int getTotalScore () {
		return score + totalScore;
	}

	/** @return the instructions used in the current phase */
	public int[] getInstructions () {
		return instructions;
	}

	/** Removes unlocked attributes from the node and its children
	 * 
	 * @param node the node
	 * @return returns true if this node must be deleted from parent, because the ability has not been unlocked */
	public boolean cleanNode (XMLNode node) {
		Abilities ability = Abilities.getAbility(node.getNodeName());
		if (ability != null) {
			int currentLevel = this.getCurrentLevel(ability);
			// The ability is unlocked, remove from parent
			if (currentLevel == -1) {
				PhaseScreen.communicator.addMessage("warning",
					PhaseScreen.i18n("We need to find more parts to use ") + node.getNodeName());
				return true;
			}
			nodesToRemove.clear();
			for (XMLNode n : node.getChildren()) {
				if (cleanNode(n)) {
					nodesToRemove.add(n);
				}
			}
			for (XMLNode n : nodesToRemove) {
				node.getChildren().remove(n);
			}
			ability.clean(node, currentLevel, this);
		}
		return false;
	}

	/** Add instructions
	 * 
	 * @param instructionsCount an array with the count of instructions */
	public void addInstruction (int[] instructionsCount) {
		for (int i = 0; i < instructionsCount.length; i++) {
			this.instructions[i] += instructionsCount[i];
		}
	}

	public int[] getTotalInstructions () {
		return totalInstructions;
	}

	public void storeLevels () {
		pref.putInteger("ts", totalScore);
		for (int i = 0; i < totalLevels.length; i++) {
			pref.putInteger("l" + i, totalLevels[i]);
		}
		for (int i = 0; i < totalInstructions.length; i++) {
			pref.putInteger("i" + i, totalInstructions[i]);
		}
	}

	public void restoreLevels () {
		totalScore = pref.getInteger("ts", 0);
		for (int i = 0; i < totalLevels.length; i++) {
			totalLevels[i] = pref.getInteger("l" + i, 0);
		}
		for (int i = 0; i < totalInstructions.length; i++) {
			totalInstructions[i] = pref.getInteger("i" + i, 0);
		}
	}

	public enum Abilities {
		ACTIONS, MOVE, ROTATE, SHOOT, GHOST;

		private static final String[] ACTIONS_ATT = new String[] {"", Parser.IDREF, ActionsCreator.LOOP, ActionsCreator.FOREVER};
		private static final String[] MOVE_ATT = new String[] {"", ActionsCreator.DISTANCE, ActionsCreator.SPEED};
		private static final String[] ROTATE_ATT = new String[] {ActionsCreator.DIRECTION};
		private static final String[] SHOOT_ATT = new String[] {""};
		private static final String[] GHOST_ATT = new String[] {ActionsCreator.TIME};

		public int getMaxLevel () {
			switch (this) {
			case ACTIONS:
				return 3;
			case MOVE:
				return 2;
			case ROTATE:
				return 0;
			case SHOOT:
				return 0;
			case GHOST:
				return 0;
			}
			return 0;
		}

		public String[] getAttributes () {
			switch (this) {
			case ACTIONS:
				return ACTIONS_ATT;
			case MOVE:
				return MOVE_ATT;
			case ROTATE:
				return ROTATE_ATT;
			case SHOOT:
				return SHOOT_ATT;
			case GHOST:
				return GHOST_ATT;
			}
			return null;
		}

		public String toString () {
			switch (this) {
			case ACTIONS:
				return "actions";
			case MOVE:
				return "move";
			case ROTATE:
				return "rotate";
			case SHOOT:
				return "shoot";
			case GHOST:
				return "ghost";
			}
			return null;
		}

		public String getConsoleExplanation () {
			String result = "";
			switch (this) {
			case ACTIONS:
				result = "With this ability, you can group other abilities.";
				break;
			case MOVE:
				result = "With this ability, you can move the ship.";
				break;
			case ROTATE:
				result = "With this ability, you can rotate the ship.";
				break;
			case SHOOT:
				result = "With this ability, you can shoot bullets.";
				break;
			case GHOST:
				result = "With this ability, you can make the ship invincible temporarily.";
				break;
			}
			return PhaseScreen.i18n(result);
		}

		public String[] getExplanations (int level) {
			String text = "";
			String example = "";
			String dtd = "";
			switch (this) {
			case ACTIONS:
				switch (level) {
				case 0:
					text = "Actions ability allows to combine several actions to execute them in order. "
						+ "For example, to move the ship two spaces introduce:";
					example = "<actions>\n     <move/>\n     <move/>\n</actions>";
					dtd = "<!ELEMENT actions (move|rotate|ghost|shoot|speak)* >";
					break;
				case 1:
					text = "With this attribute, you can select whose ship will receive the actions. "
						+ "You can only control Captain's, Susan's and Ben's ship. If the attribute is not specifed, Captain's ship receives the actions. "
						+ "For example, to rotate Susan introduce:";
					example = "<actions idref=\"susan\">\n     <rotate direction=\"cw\"/>\n</actions>";
					dtd = "<!ELEMENT actions (move|rotate|ghost|shoot|speak)* >\n" + "<!ATTLIST actions\n"
						+ "     idref IDREF #IMPLIED >\n";
					break;
				case 2:
					text = "With this attribute, you set how many times the actions must be executed. "
						+ "For example, to move and rotate Ben's ship twice use:";
					example = "<actions idref=\"ben\" loop=\"2\">\n     <move/>\n     <rotate direction=\"cw\"/>\n</actions>";
					dtd = "<!ELEMENT actions (move|rotate|ghost|shoot|speak)* >\n" + "<!ATTLIST actions\n"
						+ "     idref IDREF #IMPLIED\n" + "     loop CDATA #IMPLIED >\n";
					break;
				case 3:
					text = "With this attribute, the actions will be executed in order in an infinite loop, until a new action addresses the ship."
						+ "For example, to infinitely rotate Captain's ship use:";
					example = "<actions forever=\"true\">\n     <rotate direction=\"cw\"/>\n</actions>";
					dtd = "<!ELEMENT actions (move|rotate|ghost|shoot|speak)* >\n" + "<!ATTLIST actions\n"
						+ "     idref IDREF #IMPLIED\n" + "     loop CDATA #IMPLIED\n" + "     forever CDATA #IMPLIED >";
					break;
				}
				break;
			case MOVE:
				switch (level) {
				case 0:
					text = "With this ability, you can move the ship in the current direction. "
						+ "For example, to move the ship 1 square:";
					example = "<actions>\n     <move/>\n</actions>";
					dtd = "<!ELEMENT move EMPTY>";
					break;
				case 1:
					text = "With this attribute, you can set the squares the ship should move. "
						+ "For example, to move the ship 3 squares:";
					example = "<actions>\n     <move distance=\"3\"/>\n</actions>";
					dtd = "<!ELEMENT move EMPTY>\n" + "<!ATTLIST move\n" + "     distance CDATA #IMPLIED >";
					break;
				case 2:
					text = "With this attribute, you can set the speed of movement. "
						+ " For example, to move the ship a distance of 10 in high speed:";
					example = "<actions>\n     <move distance=\"3\" speed=\"high\"/>\n</actions>";
					dtd = "<!ELEMENT move EMPTY>\n" + "<!ATTLIST move\n" + "     distance CDATA #IMPLIED\n"
						+ "     speed (slow|normal|high) #IMPLIED>";
				}
				break;
			case ROTATE:
				switch (level) {
				case 0:
					text = "With this ability, you can rotate the ship. You can rotate it clockwise (cw) and counterclockwise (ccw)"
						+ "For example, to rotate the the ship clockwise, use: ";
					example = "<actions>\n     <rotate direction=\"cw\"/>\n</actions>";
					dtd = "<!ELEMENT rotate EMPTY>\n" + "<!ATTLIST rotate\n" + "     direction (cw|ccw) #REQUIRED >";
					break;
				}
				break;
			case SHOOT:
				switch (level) {
				case 0:
					text = "Wit this ability, you can shoot bullets. To shoot a bullet introduce: ";
					example = "<actions>\n     <shoot/>\n</actions>";
					dtd = "<!ELEMENT shoot EMPTY>";
					break;
				}
				break;
			case GHOST:
				switch (level) {
				case 0:
					text = "This ability makes the ship invincible for some time. "
						+ "For example, to make the ship invincible for 3 seconds:";
					example = "<actions>\n     <ghost time=\"3\"/>\n</actions>";
					dtd = "<!ELEMENT ghost EMPTY>\n" + "<!ATTLIST ghost\n" + "     time CDATA #REQUIRED\n"
						+ "     invincible (true|false) \"true\" >";
					break;
				}
				break;
			}
			return new String[] {PhaseScreen.i18n(text), PhaseScreen.i18n(example), dtd};
		}

		/** Removes the attributes depending on the level of the ability
		 * 
		 * @param node the node
		 * @param currentLevel the current level */
		public void clean (XMLNode node, int currentLevel, LevelManager listener) {
			Map<String, String> map = node.getAttributes();
			String[] atts = this.getAttributes();
			for (int i = atts.length - 1; i > currentLevel; i--) {
				if (map.containsKey(atts[i])) {
					map.remove(atts[i]);
					PhaseScreen.msg(PhaseScreen.i18n("You can't use the ability associated with attribute ") + atts[i]);
				}
			}
		}

		public static Abilities getAbility (String tag) {
			if (tag.equals("actions")) {
				return ACTIONS;
			} else if (tag.equals("move")) {
				return MOVE;
			} else if (tag.equals("rotate")) {
				return ROTATE;
			} else if (tag.equals("shoot")) {
				return SHOOT;
			} else if (tag.equals("ghost")) {
				return GHOST;
			}
			return null;
		}
	}

}
