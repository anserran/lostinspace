package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import es.eucm.lostinspace.core.AssetManager;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Ship extends AbstractActor {

	public static final String TYPE = "ship";

	public static final Color KATE_COLOR = new Color(0.3f, 0.0f, 0.0f, 1.0f);

	public static final String KATE = "kate";

	public static final String BEN = "ben";

	public static final String SUSAN = "susan";

	public static final String CAPTAIN = "captain";

	public static final String[] MAIN_CHARACTERS = new String[]{SUSAN, BEN, CAPTAIN};

	public static final int MARKER_WIDTH = 2;

	private static TextureRegion marker;

	private Label name;

	public static boolean isMainCharacter(String name) {
		for (String s : MAIN_CHARACTERS) {
			if (s.equals(name)) {
				return true;
			}
		}
		return false;
	}

	public Ship() {
		name = new Label("", new Label.LabelStyle(PhaseScreen.assetManager.getFont(AssetManager.NAME_FONT), Color.WHITE));
	}

	@Override
	public void reset() {
		super.reset();
		this.setInvincible(false);
		this.setDestroyer(true);
		this.setColor(Color.WHITE);
	}

	@Override
	public void setSprite(Sprite sprite) {
		super.setSprite(sprite);
		name.setText(this.getName().toUpperCase());
		name.setWidth(PhaseScreen.SQUARE_SIZE);
		name.setAlignment(Align.center);
		if (marker == null) {
			Texture t = PhaseScreen.assetManager.get("wall.png");
			int size = t.getWidth() / PhaseScreen.assetManager.getColumnsTexture("wall.png");
			marker = new TextureRegion((Texture) PhaseScreen.assetManager.get("wall.png"), size, size, size, size);
		}
	}


	public void setName(String name) {
		super.setName(name);
		if (CAPTAIN.equals(name)) {
			this.setColor(Color.BLACK);
		} else if (SUSAN.equals(name) || BEN.equals(name)) {
			this.setColor(Color.DARK_GRAY);
		} else if (KATE.equals(name)) {
			this.setColor(KATE_COLOR);
		} else {
			this.setColor(Color.WHITE);
		}
	}

	@Override
	public String getImage() {
		return "ship.png";
	}

	@Override
	public float getSpriteWidth() {
		return PhaseScreen.SQUARE_SIZE;
	}

	@Override
	public float getSpriteHeight() {
		return PhaseScreen.SQUARE_SIZE;
	}

	@Override
	public BodyType getBodyType() {
		return BodyType.CIRCLE;
	}

	@Override
	public float getCollisionWidth() {
		return PhaseScreen.SQUARE_SIZE * 0.75f;
	}

	@Override
	public float getCollisionHeight() {
		return PhaseScreen.SQUARE_SIZE * 0.75f;
	}

	@Override
	protected void setSpriteTransformations(Sprite sprite) {
		super.setSpriteTransformations(sprite);
		if (this.isInvincible()) {
			Color c = this.getColor();
			sprite.setColor(c.r, 1.0f, c.b, 0.5f);
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.draw(marker, getX() + getOriginX() - MARKER_WIDTH / 2, getY() + getOriginY(), MARKER_WIDTH, PhaseScreen.SQUARE_SIZE / 2);
		name.setPosition(getX(), getY() + PhaseScreen.SQUARE_SIZE);
		name.draw(batch, parentAlpha);
	}

	@Override
	public String getType() {
		return TYPE;
	}
}
