
package es.eucm.lostinspace.core.hud.communicator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Portrait extends Actor {

	private Texture texture;

	public Portrait () {
		texture = PhaseScreen.assetManager.get("placeholder.png");
	}

	@Override
	public void draw (SpriteBatch batch, float parentAlpha) {
		batch.draw(texture, getX(), getY(), getWidth(), getHeight());
	}

	public void setType (String name) {

	}
}
