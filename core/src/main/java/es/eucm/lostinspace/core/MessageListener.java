
package es.eucm.lostinspace.core;

import com.badlogic.gdx.Gdx;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class MessageListener {

	public void msg (String message) {
		Gdx.app.log("MessageListener", message);
		PhaseScreen.communicator.addMessage("error", PhaseScreen.i18n(message));
	}
}
