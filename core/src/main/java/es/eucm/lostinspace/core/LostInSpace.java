
package es.eucm.lostinspace.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

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

	private PhaseScreen phaseScreen;

	private TitleScreen titleScreen;

	public static final ActionsCreator actionsCreator = new ActionsCreator();

	public static final PhaseCreator phaseCreator = new PhaseCreator(actionsCreator);

	@Override
	public void create () {
		Gdx.gl.glClearColor(0.65f, 0.65f, 0.67f, 1.0f);
		phaseManager = new PhaseManager();
		tracker = new GameTracker();
		tracker.startTracking();
		phaseScreen = new PhaseScreen();
		titleScreen = new TitleScreen(this);
		this.setScreen(titleScreen);
	}

	public void startGame () {
		this.setScreen(phaseScreen);
	}
}
