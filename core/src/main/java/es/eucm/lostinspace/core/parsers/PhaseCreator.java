
package es.eucm.lostinspace.core.parsers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

import es.eucm.lostinspace.core.LevelManager;
import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.actions.AbstractAction;
import es.eucm.lostinspace.core.actors.AbstractActor;
import es.eucm.lostinspace.core.actors.ImageActor;
import es.eucm.lostinspace.core.actors.LevelUp;
import es.eucm.lostinspace.core.actors.Rock;
import es.eucm.lostinspace.core.actors.Ship;
import es.eucm.lostinspace.core.actors.Trigger;
import es.eucm.lostinspace.core.actors.Wall;
import es.eucm.lostinspace.core.actors.WormHole;
import es.eucm.lostinspace.core.parsers.scripts.ScriptAction;
import es.eucm.lostinspace.core.parsers.scripts.ScriptStep;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.tools.xml.XMLNode;

public class PhaseCreator extends Parser {

	public static final String TYPE = "t", X = "x", Y = "y", NAME = "n", ROTATION = "r", WIDTH = "w", HEIGHT = "h", SIZE = "s",
		VISIBLE = "v", HIDDEN = "hidden", IMAGE = "i", TX = "tx", TY = "ty", TW = "tw", TH = "th";

	public static final String[] COMMON_IATT = new String[] {X, Y, NAME, ROTATION, VISIBLE};

	public static final String[] WALL_RATT = new String[] {WIDTH, HEIGHT};
	public static final String[] ROCK_RATT = new String[] {TYPE, SIZE};

	public static final String[] LEVELUP_RATT = new String[] {TYPE};
	public static final String[] TRIGGER_IATT = new String[] {HIDDEN};

	// Values
	public static final String[] ROCK_TYPE = new String[] {"black", "white"};

	public static final String[] LEVELUP_TYPE = new String[] {"actions", "move", "rotate", "shoot", "wait"};

	private ActionsCreator actionsCreator;

	public PhaseCreator (ActionsCreator actionsCreator) {
		this.actionsCreator = actionsCreator;
		this.requiredAtts.put("wall", WALL_RATT);
		this.requiredAtts.put("rock", ROCK_RATT);
		this.requiredAtts.put("levelup", LEVELUP_RATT);
		this.impliedAtts.put("trigger", TRIGGER_IATT);
		this.attsValues.put(VISIBLE, BOOLEAN_TYPE);
		this.attsValues.put(HIDDEN, BOOLEAN_TYPE);
	}

	/** Creates the phase defined in the given node
	 * 
	 * @param root the node
	 * @return the start script */
	public Array<ScriptStep> createPhase (XMLNode root, Group map) {
		Array<ScriptStep> steps = new Array<ScriptStep>();
		// Remove actors from previous phase
		for (Actor a : map.getChildren()) {
			if (a instanceof AbstractActor) {
				((AbstractActor)a).dispose();
			}
		}
		map.clear();

		// Read new actors and add them to the map
		for (XMLNode n : root.getChildren()) {
			if (n.getNodeName().equals("start")) {
				createStartActions(n, steps);
			} else if (checkAttributes(n, EMPTY, COMMON_IATT, false) && checkAttributes(n, false)) {
				Actor a = createActor(n);
				if (a != null) {
					map.addActor(a);
				}
			}
		}
		return steps;
	}

	/** Creates an actor
	 * 
	 * @param n the node defining the actor
	 * @return the actor */
	private Actor createActor (XMLNode n) {
		String name = n.getNodeName();
		Actor a = null;
		if (name.equals("ship")) {
			a = createShip(n);
		} else if (name.equals("exit")) {
			a = createExit(n);
		} else if (name.equals("rock")) {
			a = createRock(n);
		} else if (name.equals("wall")) {
			a = createWall(n);
		} else if (name.equals("trigger")) {
			a = createTrigger(n);
		} else if (name.equals("levelup")) {
			a = createLevelUp(n);
		} else if (name.equals("img")) {
			a = createImage(n);
		}

		if (a == null) {
			error(name + PhaseScreen.i18n(" is not a valid node name."));
		} else {
			addTransformations(a, n);
		}
		return a;
	}

	/** @param n the node
	 * @return a level up */
	private Actor createLevelUp (XMLNode n) {
		String type = getAttribute(n, TYPE);
		LevelManager.Abilities a = LevelManager.Abilities.getAbility(type);
		if (a == null) {
			error(type + PhaseScreen.i18n(" is not a valid ability"));
		} else {
			LevelUp levelUp = PhaseScreen.obtain(LevelUp.class);
			levelUp.setAbility(a);
			return levelUp;
		}
		return null;
	}

	/** @param node the node
	 * @return a trigger */
	private Actor createTrigger (XMLNode node) {
		Trigger trigger = PhaseScreen.obtain(Trigger.class);
		boolean hidden = getBoolean(node, HIDDEN);
		trigger.setHidden(hidden);
		// Process trigger effects
		for (XMLNode n : node.getChildren()) {
			if (n.getNodeName().equals("activate") && checkAttributes(n, true)) {
				for (XMLNode a : n.getChildren()) {
					if (a.getNodeName().equals("actions")) {
						String targetName = getAttribute(a, IDREF);
						AbstractAction actions = actionsCreator.createActions(a);
						trigger.addActionEffect(new Trigger.Effect(targetName, actions));
					} else {
						error(PhaseScreen.i18n("Effects can only have 'actions' children"));
					}
				}
			}
		}
		return trigger;
	}

	/** @param n the node
	 * @return a wall */
	private Actor createWall (XMLNode n) {
		int width = getInteger(n, WIDTH, -100, 100);
		int height = getInteger(n, HEIGHT, -100, 100);
		Wall wall = PhaseScreen.obtain(Wall.class);
		wall.setWallWidth(width);
		wall.setWallHeight(height);
		return wall;
	}

	/** @param n the node
	 * @return a rock */
	private Actor createRock (XMLNode n) {
		int size = getInteger(n, SIZE, 1, PhaseScreen.GRID_COLUMNS);
		Rock rock = PhaseScreen.obtain(Rock.class);
		rock.setRadius(size);
		String type = getAttribute(n, TYPE);
		int index = contains(type, ROCK_TYPE);
		if (index < 0) {
			error(type + PhaseScreen.i18n(" is not a valid value for attribute ") + type);
		} else {
			// Set invincible if it's a black rock
			rock.setType(type);
		}
		return rock;
	}

	/** @param n the node
	 * @return an exit */
	private Actor createExit (XMLNode n) {
		return PhaseScreen.obtain(WormHole.class);
	}

	/** @param n the node
	 * @return a ship */
	private Actor createShip (XMLNode n) {
		return PhaseScreen.obtain(Ship.class);
	}

	/** @param n n the node
	 * @return an actor image */
	private Actor createImage (XMLNode n) {
		ImageActor actor = LostInSpace.pools.obtain(ImageActor.class);
		String img = getAttribute(n, IMAGE);
		Integer u = getInteger(n, TX, 0, 100);
		Integer v = getInteger(n, TY, 0, 100);
		Integer uw = getInteger(n, TW, 0, 100);
		Integer vh = getInteger(n, TH, 0, 100);
		Integer width = getInteger(n, WIDTH, 1, 100);
		Integer height = getInteger(n, HEIGHT, 1, 100);
		actor.setSize(width * PhaseScreen.SQUARE_SIZE, height * PhaseScreen.SQUARE_SIZE);
		actor.setTextureRegion(img, u, v, uw, vh, false);
		return actor;
	}

	/** Adds the necessary transformations to the actor, based on the data of the node. Position, rotation, actions
	 * 
	 * @param a the actor
	 * @param n the node */
	private void addTransformations (Actor a, XMLNode n) {
		int x = getInteger(n, X, -100, 100);
		int y = getInteger(n, Y, -100, 100);
		boolean visible = true;
		if (n.getAttributes().containsKey(VISIBLE)) {
			visible = getBoolean(n, VISIBLE);
		}
		int rotation = getInteger(n, ROTATION, 0, 360);
		String name = getAttribute(n, NAME);
		if (name != null) {
			a.setName(name);
		}
		a.setRotation(rotation);
		a.setPosition(PhaseScreen.SQUARE_SIZE * x, PhaseScreen.SQUARE_SIZE * y);
		a.setVisible(visible);
		addActions(a, n);
	}

	/** Adds the initial actions to the actor
	 * 
	 * @param a the actor
	 * @param node the node with the actions */
	private void addActions (Actor a, XMLNode node) {
		for (XMLNode n : node.getChildren()) {
			if (n.getNodeName().equals("actions")) {
				AbstractAction action = actionsCreator.createActions(n);
				a.addAction(action);
			}
		}
	}

	/** Creates the start script define by the node
	 * 
	 * @param node the node
	 * @return a list with the start script */
	private void createStartActions (XMLNode node, Array<ScriptStep> steps) {
		steps.clear();
		for (XMLNode n : node.getChildren()) {
			ScriptStep step = new ScriptStep();
			// Sequence actions
			if (n.getNodeName().equals("actions")) {
				AbstractAction a = actionsCreator.createActions(n);
				if (a != null) {
					step.add(new ScriptAction(a, n.getAttributeValue(IDREF)));
				} else {
					Gdx.app.log("PhaseCreator", n.toString() + PhaseScreen.i18n(" returned a null action"));
				}
				// Parallel actions
			} else if (n.getNodeName().equals("parallel")) {
				for (XMLNode n1 : n.getChildren()) {
					AbstractAction a = actionsCreator.createActions(n1);
					if (a != null) {
						step.add(new ScriptAction(a, n1.getAttributeValue(IDREF)));
					} else {
						Gdx.app.log("PhaseCreator", n.toString() + PhaseScreen.i18n(" returned a null action"));
					}
				}
			} else {
				error(PhaseScreen.i18n(n.getNodeName() + " is not a valid node in start actions."));
			}
			steps.add(step);
		}
	}

}
