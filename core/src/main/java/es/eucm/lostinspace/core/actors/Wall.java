
package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Wall extends AbstractActor {

	public static final String TYPE = "wall";

	public static final int SIZE = 3;

	private int wallWidth;

	private int wallHeight;

	private int squareSize;

	public void reset () {
		super.reset();
		this.setDestroyer(true);
		this.setInvincible(true);
		this.setColor(Color.BLACK);
	}

	public int getWallWidth () {
		return wallWidth;
	}

	public void setWallWidth (int wallWidth) {
		this.wallWidth = wallWidth;
	}

	public int getWallHeight () {
		return wallHeight;
	}

	public void setWallHeight (int wallHeight) {
		this.wallHeight = wallHeight;
	}

	@Override
	public void setSprite (Sprite sprite) {
		super.setSprite(sprite);
		sprite.setSize(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE);
		squareSize = sprite.getTexture().getWidth() / SIZE;
	}

	@Override
	public void draw (SpriteBatch batch, float parentAlpha) {
		sprite.setColor(this.getColor());
		for (int i = 0; i < wallWidth; i++) {
			for (int j = 0; j < wallHeight; j++) {
				int gridX = (i == 0 ? 0 : (i == wallWidth - 1 ? SIZE - 1 : 1));
				int gridY = (j == wallHeight - 1 ? 0 : (j == 0 ? SIZE - 1 : 1));
				sprite.setRegion(gridX * squareSize, gridY * squareSize, squareSize, squareSize);
				sprite.setX(getX() + PhaseScreen.SQUARE_SIZE * i);
				sprite.setY(getY() + PhaseScreen.SQUARE_SIZE * j);
				sprite.draw(batch, parentAlpha);
			}
		}
	}

	@Override
	public String getImage () {
		return "wall.png";
	}

	@Override
	public float getSpriteWidth () {
		return PhaseScreen.SQUARE_SIZE * wallWidth;
	}

	@Override
	public float getSpriteHeight () {
		return PhaseScreen.SQUARE_SIZE * wallHeight;
	}

	@Override
	public BodyType getBodyType () {
		return BodyType.RECTANGLE;
	}

	@Override
	public float getCollisionWidth () {
		return PhaseScreen.SQUARE_SIZE * wallWidth;
	}

	@Override
	public float getCollisionHeight () {
		return PhaseScreen.SQUARE_SIZE * wallHeight;
	}

	@Override
	public String getType () {
		return TYPE;
	}
}
