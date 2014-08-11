
package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Pool.Poolable;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class ImageActor extends Actor implements Poolable {

	private Color HOVER_COLOR = new Color(0.9f, 0.9f, 0.9f, 1.0f);

	private Sprite sprite;

	private boolean hoverEffect;

	public void setTextureRegion (String texture) {
		setTextureRegion(texture, false);
	}

	public void setTextureRegion (String texture, boolean hoverEffect) {
		setTextureRegion(new TextureRegion((Texture)PhaseScreen.assetManager.get(texture)), hoverEffect);
	}

	public void setTextureRegion (TextureRegion textureRegion) {
		setTextureRegion(textureRegion, false);
	}

	public void setTextureRegion (TextureRegion textureRegion, boolean hoverEff) {
		this.sprite = new Sprite(textureRegion);
		this.setHoverEffect(hoverEff);
		this.addListener(new InputListener() {
			@Override
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if (hoverEffect) {
					setColor(HOVER_COLOR);
				}
			}

			@Override
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				if (hoverEffect) {
					setColor(Color.WHITE);
				}
			}
		});
	}

	public void setTextureRegion (String texture, int x, int y, int width, int height, boolean hoverEffect) {
		setTextureRegion(texture, hoverEffect);
		Texture t = sprite.getTexture();
		int textureColumns = PhaseScreen.assetManager.getColumnsTexture(texture);
		int size = t.getWidth() / textureColumns;
		sprite.setRegion(x * size, y * size, width * size, height * size);

	}

	public void setHoverEffect (boolean hoverEffect) {
		this.hoverEffect = hoverEffect;
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		sprite.setColor(this.getColor());
		sprite.setBounds(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		sprite.setScale(this.getScaleX(), this.getScaleY());
		sprite.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
		sprite.draw(batch, parentAlpha);
	}

	@Override
	public void reset () {

	}
}
