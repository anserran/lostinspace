
package es.eucm.lostinspace.core;

import com.badlogic.gdx.Gdx;

import es.eucm.gleaner.tracker.Tracker;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class GameTracker {

	public static final String XML = "xml", XML_VALID = "xml_valid", XML_INVALID = "xml_invalid", DIE = "die",
		START_PHASE = "phase_start", RESTART = "restart", START = "start", ACTIONS = "actions", END_PHASE = "phase_end",
		GAME_END = "game_end", SCORE = "score", NAME = "name", GAME_START = "game_start";

	private Tracker tracker;

	private PhaseManager phaseManager;

	public GameTracker () {
		tracker = LostInSpace.gleanerTracker;
		phaseManager = LostInSpace.phaseManager;
	}

	/** Start tracking */
	public void startTracking () {
		String trackData[] = Gdx.files.internal("track.txt").readString().split(";");
		tracker.setAuthToken("anonymous");
		tracker.setServerURL(trackData[0]);
		tracker.startTracking(trackData[1]);
		startGame();
		flush();
	}

	/** Tracks an xml
	 * 
	 * @param xml the xml
	 * @param error if it has errors */
	public void xml (String xml, boolean error) {
		tracker.logic(XML, phaseManager.getCurrentPhaseId(), (error ? XML_INVALID : XML_VALID), xml);
		if (error) {
			flush();
		}
	}

	/** Sends all stored traces to the server */
	public void flush () {
		tracker.flush();
	}

	/** Record death
	 * 
	 * @param name name of the actor that died
	 * @param x x coordinate of the death
	 * @param y y coordinate of the death */
	public void die (String name, float x, float y) {
		tracker.death(phaseManager.getCurrentPhaseId(), name, x, y);
	}

	/** Record start phase
	 * 
	 * @param restart if it's a restart
	 * @param id phase id */
	public void startPhase (boolean restart, String id) {
		tracker.phaseStart(id);
		flush();
	}

	/** Record end phase
	 * 
	 * @param score score phase
	 * @param totalScore adding the score and subtracting the actions
	 * @param instructions the count of instructions sent in the phase */
	public void endPhase (int score, int totalScore, int[] instructions) {
		tracker.phaseEnd(phaseManager.getCurrentPhaseId());
		tracker.score(phaseManager.getCurrentPhaseId(), totalScore);
		for (int i = 0; i < instructions.length; i++) {
			tracker.logic(ACTIONS, phaseManager.getCurrentPhaseId(), LevelManager.Abilities.values()[i].toString(), instructions[i]);
		}
	}

	/** Start game */
	public void startGame () {
		tracker.gameStart();
	}

	/** End game */
	public void endGame () {
		tracker.score("end", PhaseScreen.levelManager.getTotalScore());
		tracker.gameEnd();
		int instructions[] = PhaseScreen.levelManager.getTotalInstructions();
		for (int i = 0; i < instructions.length; i++) {
			tracker.logic(ACTIONS, "end", LevelManager.Abilities.values()[i].toString(), instructions[i]);
		}
		flush();
	}

	/** Tracks nickname selection
	 * 
	 * @param nickname */
	public void name (String nickname) {
		tracker.logic(NAME, null, nickname, null);
	}
}
