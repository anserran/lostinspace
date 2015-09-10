
package es.eucm.lostinspace.core;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Logger;
import es.eucm.gleaner.tracker.Tracker;
import es.eucm.gleaner.tracker.model.traces.Events;
import es.eucm.lostinspace.core.screens.PhaseScreen;

import java.util.Map;

public class GameTracker {

    public static final String XML = "xml", XML_VALID = "xml_valid", XML_INVALID = "xml_invalid", DIE = "die",
            START_PHASE = "phase_start", RESTART = "restart", START = "start", ACTIONS = "actions", END_PHASE = "phase_end",
            GAME_END = "game_end", SCORE = "score", NAME = "nickname", USERNAME = "username", PHASE = "phase", INACTIVITY = "inactivity",
            GAME_START = "game_start";

    private Tracker tracker;

    private PhaseManager phaseManager;

    private int validXml, invalidXml, phaseValidXml, phaseInvalidXml;

    public GameTracker() {
        tracker = LostInSpace.gleanerTracker;
        phaseManager = LostInSpace.phaseManager;
    }

    /**
     * Start tracking
     */
    public void startTracking() {
        String trackData[] = Gdx.files.internal("track.txt").readString().split(";");
        tracker.setAuthorization2("a:");
        tracker.setServerURL(trackData[0]);
        tracker.startTracking(trackData[1]);
        startGame();
        flush();
    }


    /**
     * Tracks an xml
     *
     * @param xml   the xml
     * @param error if it has errors
     */
    public void xml(String xml, boolean error) {
        if (error) {
            tracker.var(XML_INVALID + "_" + phaseManager.getCurrentPhaseId(), ++phaseInvalidXml);
            tracker.var(XML_INVALID, ++invalidXml);
        } else {
            tracker.var(XML_VALID + "_" + phaseManager.getCurrentPhaseId(), ++phaseValidXml);
            tracker.var(XML_VALID, ++validXml);
        }
        flush();
    }

    /**
     * Sends all stored traces to the server
     */
    public void flush() {
        tracker.flush();
    }

    /**
     * Record death
     *
     * @param name name of the actor that died
     * @param x    x coordinate of the death
     * @param y    y coordinate of the death
     */
    public void die(String name, float x, float y) {
        tracker.death(phaseManager.getCurrentPhaseId(), name, x, y);
        flush();
    }

    /**
     * Record start phase
     *
     * @param restart if it's a restart
     * @param id      phase id
     */
    public void startPhase(boolean restart, String id) {
        phaseInvalidXml = 0;
        phaseValidXml = 0;
        tracker.phaseStart(id);
        tracker.zone(id);
        int phaseNumber = phaseManager.getPhaseNumber(id);
        if (phaseNumber != -1) {
            tracker.var(PHASE, phaseNumber);
        }
        flush();
    }

    /**
     * Record end phase
     *
     * @param score        score phase
     * @param totalScore   adding the score and subtracting the actions
     * @param instructions the count of instructions sent in the phase
     */
    public void endPhase(int score, int totalScore, int[] instructions) {
        tracker.var(SCORE, totalScore);
        tracker.var(SCORE + "_" + phaseManager.getCurrentPhaseId(), score);
        flush();
    }

    /**
     * Start game
     */
    public void startGame() {
        tracker.gameStart();
    }

    /**
     * End game
     */
    public void endGame() {
        tracker.var(SCORE, PhaseScreen.levelManager.getTotalScore());
        tracker.gameEnd();
        flush();
    }

    /**
     * Tracks nickname selection
     *
     * @param nickname
     */
    public void name(String nickname) {
        tracker.var(NAME, nickname);
        if (LostInSpace.credentials.size > 0) {
            tracker.var(USERNAME, LostInSpace.credentials.first());
        }
        flush();
    }

    /**
     * Tracks the time spent without sending a command.
     *
     * @param milis
     */
    public void inactivity(float milis) {
        tracker.var(INACTIVITY, milis);
        flush();
    }
}
