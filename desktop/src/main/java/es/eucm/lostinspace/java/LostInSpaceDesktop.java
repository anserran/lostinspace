package es.eucm.lostinspace.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.tools.xml.SaxXMLParser;

public class LostInSpaceDesktop {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "lostinspace";
		cfg.width = PhaseScreen.SQUARE_SIZE
				* (PhaseScreen.CONSOLE_COLUMNS + PhaseScreen.GRID_COLUMNS);
		cfg.height = PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS);
		PhaseScreen.xmlParser = new SaxXMLParser();
		cfg.vSyncEnabled = false;
		cfg.fullscreen = false;
		new LwjglApplication(new LostInSpace(), cfg);
	}
}
