
package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Grid extends Actor {

	public static final String ID = "grid";

	private TextureRegion textureRegion;

	public Grid () {
		this.setName(ID);
		textureRegion = new TextureRegion((Texture)PhaseScreen.assetManager.get("grid.png"));
	}

	@Override
	public void draw (SpriteBatch batch, float parentAlpha) {
		batch.draw(textureRegion, 0, 0, PhaseScreen.STAGE_WIDTH, PhaseScreen.STAGE_HEIGHT);
	}
}
