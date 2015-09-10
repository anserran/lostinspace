
package es.eucm.lostinspace.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Array;
import es.eucm.gleaner.tracker.Tracker;
import es.eucm.lostinspace.core.parsers.ActionsCreator;
import es.eucm.lostinspace.core.parsers.PhaseCreator;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.lostinspace.core.screens.TitleScreen;

public class LostInSpace extends Game {

    public static GameTracker tracker;
    public static final Pools pools = new Pools();

    public static PhaseManager phaseManager;

    public static Tracker gleanerTracker;
    public static LevelManager levelManager;

    public static PhaseScreen phaseScreen;

    private TitleScreen titleScreen;

    /**
     * credentials.get(0) -> Username.
     * credentials.get(1) -> Authorization token.
     */
    public static final Array<String> credentials = new Array<String>();

    public static final ActionsCreator actionsCreator = new ActionsCreator();

    public static final PhaseCreator phaseCreator = new PhaseCreator(actionsCreator);

    @Override
    public void create() {
        if(credentials.size > 1) {
            gleanerTracker.setAuthorization("Bearer " + credentials.get(1));
        }
        Gdx.gl.glClearColor(0.65f, 0.65f, 0.67f, 1.0f);
        phaseManager = new PhaseManager();
        levelManager = new LevelManager();
        tracker = new GameTracker();
        tracker.startTracking();
        phaseScreen = new PhaseScreen();
        titleScreen = new TitleScreen(this);
        this.setScreen(titleScreen);
    }

    public void startGame() {
        this.setScreen(phaseScreen);
    }

    public void continueGame() {
        Preferences pref = Gdx.app.getPreferences("lis");
        String phase = pref.getString("phase");
        phaseManager.setFirstPhase(phase);
        startGame();
        phaseManager.loadFirstPhase();
        levelManager.restoreLevels();

    }
}
