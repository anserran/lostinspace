
package es.eucm.lostinspace.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import es.eucm.ead.tools.java.xml.SaxXMLParser;
import es.eucm.gleaner.tracker.JerseyTracker;
import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class LostInSpaceDesktop {
	public static void main (String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "lostinspace";
		cfg.useGL20 = true;
		cfg.width = PhaseScreen.SQUARE_SIZE * (PhaseScreen.CONSOLE_COLUMNS + PhaseScreen.GRID_COLUMNS);
		cfg.height = PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS);
		PhaseScreen.xmlParser = new SaxXMLParser();
		cfg.vSyncEnabled = false;
		cfg.fullscreen = false;
		// cfg.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
		LostInSpace.gleanerTracker = new JerseyTracker();
		new LwjglApplication(new LostInSpace(), cfg);
	}
}
