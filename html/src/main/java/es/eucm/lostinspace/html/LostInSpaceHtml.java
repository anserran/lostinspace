
package es.eucm.lostinspace.html;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.storage.client.Storage;
import es.eucm.gleaner.tracker.GwtTracker;
import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.tools.xml.GwtXMLParser;

public class LostInSpaceHtml extends GwtApplication {
    @Override
    public GwtApplicationConfiguration getConfig() {
        GwtApplicationConfiguration cfg = new GwtApplicationConfiguration(480, 320);
        cfg.width = PhaseScreen.SQUARE_SIZE * (PhaseScreen.CONSOLE_COLUMNS + PhaseScreen.GRID_COLUMNS);
        cfg.height = PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS);
        return cfg;
    }

    @Override
    public ApplicationListener getApplicationListener() {
        PhaseScreen.xmlParser = new GwtXMLParser();
        LostInSpace.gleanerTracker = new GwtTracker(false);
        LostInSpace lostInSpace = new LostInSpace();
        Storage stockStore = Storage.getLocalStorageIfSupported();
        if (stockStore != null) {
            for (int i = 0; i < stockStore.getLength(); i++) {
                String key = stockStore.key(i);
                if (key.equals("ngStorage-user")) {
                    LostInSpace.credentials.clear();
                    String storageRawValue = stockStore.getItem(key);
                    JSONObject jsonValue = JSONParser.parseStrict(storageRawValue).isObject();
                    LostInSpace.credentials.add(jsonValue.get("username").isString().stringValue());
                    LostInSpace.credentials.add(jsonValue.get("token").isString().stringValue());
                    break;
                }
            }
        }
        return lostInSpace;
    }
}
