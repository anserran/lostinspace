package es.eucm.lostinspace.core.parsers;

import es.eucm.ead.tools.xml.XMLNode;
import es.eucm.lostinspace.core.LevelManager;
import es.eucm.lostinspace.core.actions.*;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class ActionsCreator extends Parser {


	public static final String LOOP = "loop",
			DISTANCE = "distance", SPEED = "speed", DIRECTION = "direction",
			TIME = "time", FOREVER = "forever", VALUE = "value", POSITION = "p", INVINCIBLE = "invincible", LEVEL = "l";


	public static final String[] ACTIONS_IATT = new String[]{IDREF, LOOP, FOREVER};

	public static final String[] MOVE_IATT = new String[]{DISTANCE,
			SPEED};
	public static final String[] ROTATE_RATT = new String[]{DIRECTION};
	public static final String[] ROTATE_IATT = new String[]{SPEED};

	public static final String[] GHOST_RATT = new String[]{TIME};
	public static final String[] GHOST_IATT = new String[]{INVINCIBLE};

	public static final String[] VISIBILITY_RATT = new String[]{VALUE};
	public static final String[] SPEAK_IATT = new String[]{POSITION};

	public static final String[] CONSOLE_RATT = new String[]{LEVEL};

	public static final String[] DIRECTION_TYPE = new String[]{"cw", "ccw"};

	public static final String[] SPEED_TYPE = new String[]{"slow", "normal",
			"high"};
	public static final String[] POSITION_TYPE = new String[]{"top", "right", "bottom", "left"};
	public static final String[] LEVEL_TYPE = new String[]{"info", "error", "warning"};


	/**
	 * Instructions count of the last processed xml
	 */
	private int[] instructionsCount;

	public ActionsCreator() {
		instructionsCount = new int[LevelManager.Abilities.values().length];
		this.impliedAtts.put("actions", ACTIONS_IATT);
		this.impliedAtts.put("move", MOVE_IATT);
		this.requiredAtts.put("rotate", ROTATE_RATT);
		this.impliedAtts.put("rotate", ROTATE_IATT);
		this.requiredAtts.put("ghost", GHOST_RATT);
		this.impliedAtts.put("ghost", GHOST_IATT);
		this.requiredAtts.put("visibility", VISIBILITY_RATT);
		this.impliedAtts.put("speak", SPEAK_IATT);
		this.requiredAtts.put("console", CONSOLE_RATT);
		// Attribute values
		this.attsValues.put(SPEED, SPEED_TYPE);
		this.attsValues.put(DIRECTION, DIRECTION_TYPE);
		this.attsValues.put(FOREVER, BOOLEAN_TYPE);
		this.attsValues.put(VALUE, BOOLEAN_TYPE);
		this.attsValues.put(INVINCIBLE, BOOLEAN_TYPE);
		this.attsValues.put(POSITION, POSITION_TYPE);
		this.attsValues.put(LEVEL, LEVEL_TYPE);
	}

	/**
	 * Resets instructions count
	 */
	private void resetCount() {
		for (int i = 0; i < instructionsCount.length; i++) {
			instructionsCount[i] = 0;
		}
	}

	/**
	 * @return the instructions count of the last processed xml
	 */
	public int[] getInstructionsCount() {
		return instructionsCount;
	}

	/**
	 * Creates the actions for the given node, with the loop
	 *
	 * @param node the node
	 * @return the first action in the sequence
	 */
	public AbstractAction createActions(XMLNode node) {
		resetCount();
		if (node.getNodeName().equals("actions") && checkAttributes(node, true)) {
			AbstractAction firstAction = null;
			AbstractAction lastAction = null;
			Boolean forever = this.getBoolean(node, FOREVER);
			Integer loops = (forever == null || forever ? Integer.valueOf(0) : this.getInteger(node, LOOP, 0, 10));
			if (loops != null && forever != null) {
				for (int i = 0; i <= loops; i++) {
					// Generate actions
					AbstractAction action = generateActions(node);
					if (action != null) {
						// Set first action if not set
						if (firstAction == null) {
							firstAction = action;
						}
						// Update the last action in the queue
						if (lastAction != null) {
							lastAction.setNextAction(action);
						}
						lastAction = action;
						while (lastAction.getNextAction() != null) {
							lastAction = lastAction.getNextAction();
						}
					} else {
						break;
					}
				}
				// If forever, link the last effect with the first, and the actions will go on forever
				if (forever && lastAction != null) {
					lastAction.setNextAction(firstAction);
				}
				return firstAction;
			}
		} else {
			error(PhaseScreen.i18n("All actions must have 'actions' as node root"));
		}
		return null;
	}

	/**
	 * Generate an actions queue from the node
	 *
	 * @param node an "actions" node
	 * @return the first action of the list
	 */
	private AbstractAction generateActions(XMLNode node) {
		instructionsCount[LevelManager.Abilities.ACTIONS.ordinal()]++;
		AbstractAction firstAction = null;
		AbstractAction currentAction = null;
		for (XMLNode n : node.getChildren()) {
			AbstractAction a = null;
			if (checkAttributes(n, true) && checkNoChildren(n)) {
				if (n.getNodeName().equals("move")) {
					a = generateMove(n);
				} else if (n.getNodeName().equals("rotate")) {
					a = generateRotate(n);
				} else if (n.getNodeName().equals("ghost")) {
					a = generateGhost(n);
				} else if (n.getNodeName().equals("shoot")) {
					a = generateShoot(n);
				} else if (n.getNodeName().equals("speak")) {
					a = generateSpeak(n);
				} else if (n.getNodeName().equals("visibility")) {
					a = generateVisibility(n);
				} else if (n.getNodeName().equals("console")) {
					a = generateConsole(n);
				} else {
					a = null;
					error(n.getNodeName()
							+ PhaseScreen.i18n(" is not a valid node name in actions."));
				}
			}

			if ( a == null ){
				return null;
			}
			else {
				if (firstAction == null) {
					firstAction = a;
				}
				if (currentAction != null) {
					currentAction.setNextAction(a);
				}
				currentAction = a;
			}
		}
		return firstAction;
	}

	/**
	 * @param n the node
	 * @return a console action
	 */
	private AbstractAction generateConsole(XMLNode n) {
		ConsoleAction a = new ConsoleAction();
		a.setText(n.getNodeText());
		a.setType(getAttribute(n, LEVEL));
		return a;
	}

	/**
	 * @param n the node
	 * @return a visibility effect
	 */
	private AbstractAction generateVisibility(XMLNode n) {
		Boolean visible = getBoolean(n, VALUE);
		if ( visible == null ){
			return null;
		}
		VisibleAction a = PhaseScreen.obtain(VisibleAction.class);
		a.setVisible(visible);
		return a;
	}

	/**
	 * @param n the node
	 * @return a speak effect
	 */
	private AbstractAction generateSpeak(XMLNode n) {
		String text = n.getNodeText();
		int positionType = contains(getAttribute(n, POSITION), POSITION_TYPE);
		if (positionType == -1 ){
			return null;
		}
		SpeakAction.Position position = SpeakAction.Position.values()[positionType == -2 ? 1 : positionType];
		SpeakAction a = PhaseScreen.obtain(SpeakAction.class);
		a.setText(text);
		a.setPosition(position);
		return a;
	}


	/**
	 * @param n the node
	 * @return a move action
	 */
	private AbstractAction generateMove(XMLNode n) {
		instructionsCount[LevelManager.Abilities.MOVE.ordinal()]++;
		int speedType = contains(getAttribute(n, SPEED), SPEED_TYPE);
		Integer distance = getInteger(n, DISTANCE, 1, 100);
		if ( distance == null || speedType == -1 ){
			return null;
		}
		Speed speed = Speed.values()[speedType == -2 ? 1 : speedType];
		MoveAction a = PhaseScreen.obtain(MoveAction.class);
		a.setSpeed(speed.getSpeedValue());
		a.setDistance(distance);
		return a;
	}

	/**
	 * @param n the node
	 * @return a rotate action
	 */
	private AbstractAction generateRotate(XMLNode n) {
		instructionsCount[LevelManager.Abilities.ROTATE.ordinal()]++;
		int rotationType = contains(getAttribute(n, DIRECTION), DIRECTION_TYPE);
		if (rotationType == -1 ){
			return null;
		}
		RotateAction a = PhaseScreen.obtain(RotateAction.class);
		a.setDegrees(rotationType == 0 ? -90 : 90);
		return a;
	}

	/**
	 * @param n the node
	 * @return generates a wait action
	 */
	private AbstractAction generateGhost(XMLNode n) {
		instructionsCount[LevelManager.Abilities.GHOST.ordinal()]++;
		Integer time = getInteger(n, TIME, 0, 10);
		Boolean invincible = true;
		if (getAttribute(n, INVINCIBLE) != null) {
			invincible = getBoolean(n, INVINCIBLE);
		}
		if ( time == null || invincible == null ){
			return null;
		}
		GhostAction a = PhaseScreen.obtain(GhostAction.class);
		a.setTime(time);
		a.setInvincible(invincible);
		return a;
	}

	/**
	 * @param n the node defining the action
	 * @return Generates a shoot action
	 */
	private AbstractAction generateShoot(XMLNode n) {
		instructionsCount[LevelManager.Abilities.SHOOT.ordinal()]++;
		return PhaseScreen.obtain(ShootAction.class);
	}

	@Override
	public void error(String error) {
		super.error(error);
		PhaseScreen.communicator.addMessage("error", error);
	}

	/**
	 * Enum for move speed
	 */
	public enum Speed {
		SLOW, NORMAL, HIGH;

		public String toString() {
			switch (this) {
				case SLOW:
					return "slow";
				case NORMAL:
					return "normal";
				case HIGH:
					return "high";
			}
			return super.toString();
		}

		public float getSpeedValue() {
			switch (this) {
				case SLOW:
					return PhaseScreen.SQUARE_SIZE;
				case HIGH:
					return PhaseScreen.SQUARE_SIZE * 7;
				default:
					return PhaseScreen.SQUARE_SIZE * 2;
			}
		}
	}
}
