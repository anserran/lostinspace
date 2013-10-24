package es.eucm.lostinspace.core;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import es.eucm.ead.tools.xml.XMLNode;
import es.eucm.lostinspace.core.actions.AbstractAction;
import es.eucm.lostinspace.core.actors.AbstractActor;
import es.eucm.lostinspace.core.actors.Rock;
import es.eucm.lostinspace.core.actors.Ship;
import es.eucm.lostinspace.core.parsers.ActionsCreator;
import es.eucm.lostinspace.core.parsers.Parser;
import es.eucm.lostinspace.core.parsers.PhaseCreator;
import es.eucm.lostinspace.core.parsers.scripts.ScriptStep;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class PhaseManager /*implements XMLParser.ErrorHandler*/ {

	public static final int IN_TITLE = 0, IN_CUTSCENE = 3, IN_PHASE = 1, IN_RESULTS = 2;

	public static final int EXIT_POINTS = 500;

	public static final int ROCK_POINTS = 100;

	public static final int SHIP_POINTS = 200;

	public Array<String> idsForExits;

	private PhaseCreator phaseCreator;

	private ActionsCreator actionsCreator;

	private String nextPhaseId;

	private String currentPhaseId;

	private boolean restartPhase;

	private boolean nextPhase;

	private Array<ScriptStep> scriptSteps;

	private ScriptStep currentStep;

	private XMLNode currentPhaseDef;

	/**
	 * If the player is in the title screen
	 */
	private int state;

	/**
	 * Time for the restart
	 */
	private float restartTime;

	/**
	 * Error message
	 */
	private String errorMessage;

	public PhaseManager() {
		idsForExits = new Array<String>();
		actionsCreator = LostInSpace.actionsCreator;
		phaseCreator = LostInSpace.phaseCreator;
		state = IN_TITLE;
	}

	/**
	 * Loads the first phase
	 */
	public void loadFirstPhase() {
		PhaseScreen.mapHud.startGame();
		loadPhase("phase1");
		PhaseScreen.levelManager.upLevel(LevelManager.Abilities.ACTIONS);
		PhaseScreen.levelManager.upLevel(LevelManager.Abilities.MOVE);
		PhaseScreen.communicator.addMessage(null, null);
	}

	/**
	 * Loads the phase with the given id
	 *
	 * @param id the phase id
	 */
	public void loadPhase(String id) {
		PhaseScreen.communicator.addMessage(null, "");
		LostInSpace.tracker.startPhase(id.equals(currentPhaseId), id);
		state = IN_PHASE;
		if (id.equals("end")) {
			endGame();
		} else {
			idsForExits.clear();
			if (currentPhaseDef == null || !currentPhaseId.equals(id)) {
				String text = PhaseScreen.assetManager.getTextFile("phases/" + id + ".xml");
				currentPhaseDef = PhaseScreen.xmlParser.parse(text);
			}
			String names[] = currentPhaseDef.getAttributeValue("names").split(",");
			setNextPhaseId(currentPhaseDef.getAttributeValue("next"));
			for (String n : names) {
				idsForExits.add(n);
			}

			scriptSteps = phaseCreator.createPhase(currentPhaseDef, PhaseScreen.map);
			if (scriptSteps != null && scriptSteps.size > 0) {
				state = IN_CUTSCENE;
				currentStep = scriptSteps.removeIndex(0);
				currentStep.start();
				PhaseScreen.console.setEnable(false);
			} else {
				PhaseScreen.console.setEnable(true);
			}
			PhaseScreen.mapHud.startPhase();
		}
		this.currentPhaseId = id;
	}

	public void endGame() {
		PhaseScreen.mapHud.endGame();
		LostInSpace.tracker.endGame();
	}

	/**
	 * Detects if the phase has finished
	 *
	 * @param delta seconds since last update
	 */
	public void act(float delta) {
		if (currentStep != null && currentStep.isDone()) {
			if (scriptSteps.size > 0) {
				currentStep = scriptSteps.removeIndex(0);
				currentStep.start();
			} else {
				currentStep = null;
				state = IN_PHASE;
				PhaseScreen.console.setEnable(true);
			}
		}

		if (state != IN_TITLE) {
			if (state == IN_PHASE && idsForExits.size == 0) {
				state = IN_RESULTS;
				PhaseScreen.mapHud.showPhaseResults();
				PhaseScreen.console.setEnable(false);
			}

			if (nextPhase) {
				loadPhase(nextPhaseId);
				nextPhase = false;
			} else if (restartPhase) {
				if (restartTime <= 0.0f) {
					loadPhase(currentPhaseId);
					PhaseScreen.levelManager.rollbackResults();
					restartPhase = false;
				} else {
					restartTime -= delta;
				}
			}
		}
	}

	/**
	 * Some actor reached an exit
	 *
	 * @param actor the actor
	 * @return if the actor have to be teleported
	 */
	public boolean exit(Actor actor) {
		if (actor != null) {
			String name = actor.getName();
			if (idsForExits.removeValue(name, false)) {
				if (state == IN_PHASE && Ship.isMainCharacter(name)) {
					PhaseScreen.levelManager.addPoints(EXIT_POINTS, actor.getX() + actor.getOriginX(), actor.getY() + actor.getOriginY());
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the id for the next phase
	 *
	 * @param nextPhaseId next phase id
	 */
	public void setNextPhaseId(String nextPhaseId) {
		this.nextPhaseId = nextPhaseId;
	}

	/**
	 * Loads next phase
	 */
	public void nextPhase() {
		nextPhase = true;
		int score = PhaseScreen.levelManager.getScore();
		int instructions[] = PhaseScreen.levelManager.getInstructions();
		PhaseScreen.levelManager.commitResults();
		// Total score after commit
		int totalScore = PhaseScreen.levelManager.getTotalScore();
		LostInSpace.tracker.endPhase(score, totalScore, instructions);
	}

	/**
	 * Restarts the phase
	 */
	public void restartPhase() {
		restartPhase = true;
		restartTime = 1.0f;
	}

	/**
	 * Parse commands and executes them
	 *
	 * @param xml the xml with the commands
	 * @return if commands were correct
	 */
	public boolean sendCommands(String xml) {
		if ("".equals(xml)){
			return false;
		}
		boolean error;
		try {
			XMLNode node = null;
			try {
				clearErrors();
				node = PhaseScreen.xmlParser.parse(xml);
				error = this.hasErrors();
			} catch (Exception e) {
				error = true;
			}
			if (node == null) {
				error = true;
			}
			// Tittle screen
			else if (state == IN_TITLE) {
				if (node.getNodeName().equals("start")) {
					String nickname = node.getAttributeValue("name");
					if (nickname != null && (nickname.equals("yournickname") || nickname.equals("anserran"))) {
						PhaseScreen.communicator.addMessage("error", PhaseScreen.i18n("Invalid nickname. Try another one. Remember: <start name=\"yournickname\"/>"));
						error = true;
					} else {
						PhaseScreen.console.clearHistory();
						LostInSpace.tracker.name(nickname);
						loadFirstPhase();
					}
				} else {
					error = true;
				}
			} else {
				// Phases screen
				// Remove invalid attributes
				PhaseScreen.levelManager.cleanNode(node);

				AbstractAction action = actionsCreator.createActions(node);
				PhaseScreen.levelManager.addInstruction(actionsCreator.getInstructionsCount());
				if (action == null) {
					error = true;
				}

				if (!error) {
					String target = getValidTarget(node.getAttributeValue(Parser.IDREF));

					if (target != null) {
						Actor actor = PhaseScreen.map.findActor(target);
						if (actor != null) {
							actor.addAction(action);
						} else {
							PhaseScreen.msg(target + PhaseScreen.i18n(" is not here now."));
						}
					} else {
						PhaseScreen.msg(PhaseScreen.i18n("We don't control anybody named ") + target);
					}
				}
			}

		} catch (Exception e) {
			error = true;
		}
		if (error) {
			if (state == IN_PHASE && hasErrors()) {
				PhaseScreen.communicator.addMessage("error", PhaseScreen.i18n(errorMessage));
			} else if (state == IN_TITLE) {
				PhaseScreen.communicator.addMessage("error", PhaseScreen.i18n("Invalid XML. Introduce <start name=\"yournickname\"/> (e.g. <start name=\"anserran\"/>)"));
			}
		}
		LostInSpace.tracker.xml(xml, error);
		return !error;
	}


	public String getValidTarget(String target) {
		return target != null && (idsForExits.contains(target.toLowerCase(), false) || Ship.isMainCharacter(target.toLowerCase())) ? target.toLowerCase() : Ship.CAPTAIN;
	}

	/**
	 * Warn the phase manager about the destruction of an actor
	 *
	 * @param actor
	 */
	public void actorDestroyed(AbstractActor actor) {
		if (state == IN_PHASE && actor.getType().equals(Rock.TYPE)) {
			PhaseScreen.levelManager.addPoints(ROCK_POINTS, actor.getX() + actor.getOriginX(), actor.getY() + actor.getOriginY());
		}

		if (actor.getType().equals(Ship.TYPE)) {
			for (String s : Ship.MAIN_CHARACTERS) {
				if (s.equals(actor.getName())) {
					this.restartPhase();
					LostInSpace.tracker.die(actor.getName(), actor.getX() + actor.getOriginX(), actor.getY() + actor.getOriginY());
					return;
				}
			}
			PhaseScreen.levelManager.addPoints(SHIP_POINTS, actor.getX() + actor.getOriginX(), actor.getY() + actor.getOriginY());
		}
	}

	public String getCurrentPhaseId() {
		return currentPhaseId;
	}

	/**
	 * Clears the xml parser errors
	 */
	private void clearErrors() {
		this.errorMessage = null;
	}

	/**
	 * Checks if there are xml parser errors
	 *
	 * @return if there are errors
	 */
	private boolean hasErrors() {
		return this.errorMessage != null;
	}


	public void error(String message) {
		this.errorMessage = message;
	}
}
