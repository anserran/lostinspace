
package es.eucm.lostinspace.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import es.eucm.lostinspace.core.parsers.ActionsCreator;
import es.eucm.lostinspace.core.parsers.PhaseCreator;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.lostinspace.core.screens.TitleScreen;

public class LostInSpace extends Game {

	public static GameTracker tracker;
	public static final Pools pools = new Pools();

	public static PhaseManager phaseManager;

	public static LevelManager levelManager;

	public static PhaseScreen phaseScreen;

	private TitleScreen titleScreen;

	public static final ActionsCreator actionsCreator = new ActionsCreator();

	public static final PhaseCreator phaseCreator = new PhaseCreator(actionsCreator);

	@Override
	public void create () {
		Gdx.gl.glClearColor(0.65f, 0.65f, 0.67f, 1.0f);
		phaseManager = new PhaseManager();
		levelManager = new LevelManager();
		tracker = new GameTracker();
		tracker.startTracking();
		phaseScreen = new PhaseScreen();
		titleScreen = new TitleScreen(this);
		this.setScreen(titleScreen);
	}

	@Override
	public void render() {
		super.render();
		tracker.update(Gdx.graphics.getDeltaTime());
	}

	public void startGame () {
		this.setScreen(phaseScreen);
	}

	public void continueGame () {
		Preferences pref = Gdx.app.getPreferences("lis");
		String phase = pref.getString("phase");
		phaseManager.setFirstPhase(phase);
		startGame();
		phaseManager.loadFirstPhase();
		levelManager.restoreLevels();

	}
}
