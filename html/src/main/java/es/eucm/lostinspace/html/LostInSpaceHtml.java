
package es.eucm.lostinspace.html;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.tools.xml.GwtXMLParser;

public class LostInSpaceHtml extends GwtApplication {
	@Override
	public GwtApplicationConfiguration getConfig () {
		GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(480, 320);
		cfg.width = PhaseScreen.SQUARE_SIZE * (PhaseScreen.CONSOLE_COLUMNS + PhaseScreen.GRID_COLUMNS);
		cfg.height = PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS);
		return cfg;
	}

	@Override
	public ApplicationListener getApplicationListener () {
		PhaseScreen.xmlParser = new GwtXMLParser();
		return new LostInSpace();
	}
}
