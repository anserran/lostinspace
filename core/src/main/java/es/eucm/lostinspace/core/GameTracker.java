package es.eucm.lostinspace.core;

import com.badlogic.gdx.Gdx;

import es.eucm.gleaner.tracker.Tracker;
import es.eucm.gleaner.tracker.storage.NetStorage;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class GameTracker {

	public static final String XML = "xml", XML_VALID = "xml_valid",
			XML_INVALID = "xml_invalid", DIE = "die",
			START_PHASE = "phase_start", RESTART = "restart", START = "start",
			ACTIONS = "actions", END_PHASE = "phase_end",
			GAME_END = "game_end", SCORE = "score", NAME = "name",
			GAME_START = "game_start";

	private Tracker tracker;

	private PhaseManager phaseManager;

	public GameTracker() {
		String trackData[] = Gdx.files.internal("track.txt").readString()
				.split(";");
		tracker = new Tracker(new NetStorage(Gdx.net, trackData[0],
				trackData[1]));
		phaseManager = LostInSpace.phaseManager;
	}

	/** Start tracking */
	public void startTracking() {
		tracker.start();
	}

	/**
	 * Tracks an xml
	 *
	 * @param xml
	 *            the xml
	 * @param error
	 *            if it has errors
	 */
	public void xml(String xml, boolean error) {
		tracker.trace(XML, phaseManager.getCurrentPhaseId(),
				(error ? XML_INVALID : XML_VALID));
		if (error) {
			flush();
		}
	}

	/** Sends all stored traces to the server */
	public void flush() {
		tracker.requestFlush();
	}

	/**
	 * Record death
	 *
	 * @param name
	 *            name of the actor that died
	 * @param x
	 *            x coordinate of the death
	 * @param y
	 *            y coordinate of the death
	 */
	public void die(String name, float x, float y) {
		tracker.trace("die", phaseManager.getCurrentPhaseId(), name);
	}

	/**
	 * Record start phase
	 *
	 * @param restart
	 *            if it's a restart
	 * @param id
	 *            phase id
	 */
	public void startPhase(boolean restart, String id) {
		tracker.zone(id);
		flush();
	}

	/**
	 * Record end phase
	 *
	 * @param score
	 *            score phase
	 * @param totalScore
	 *            adding the score and subtracting the actions
	 * @param instructions
	 *            the count of instructions sent in the phase
	 */
	public void endPhase(int score, int totalScore, int[] instructions) {
		tracker.var(phaseManager.getCurrentPhaseId() + "-score", score);
		tracker.var("score", totalScore);
		for (int i = 0; i < instructions.length; i++) {
			tracker.trace(ACTIONS, phaseManager.getCurrentPhaseId(),
					LevelManager.Abilities.values()[i].toString(),
					instructions[i] + "");
		}
	}

	/** End game */
	public void endGame() {
		tracker.var("totalScore", PhaseScreen.levelManager.getTotalScore());
		int instructions[] = PhaseScreen.levelManager.getTotalInstructions();
		for (int i = 0; i < instructions.length; i++) {
			tracker.trace(ACTIONS, "end",
					LevelManager.Abilities.values()[i].toString(),
					instructions[i] + "");
		}
		flush();
	}

	/**
	 * Tracks nickname selection
	 *
	 * @param nickname
	 */
	public void name(String nickname) {
		tracker.var(NAME, nickname);
	}

	public void update(float deltaTime) {
		tracker.update(deltaTime);
	}
}
