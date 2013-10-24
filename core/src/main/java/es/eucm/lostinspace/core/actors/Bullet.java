package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Bullet extends AbstractActor {

	private static final int SCALE = PhaseScreen.SQUARE_SIZE / 10;

	public static final String TYPE = "bullet";

	private Vector2 direction;

	private float speed = PhaseScreen.SQUARE_SIZE * 5;

	public Bullet() {
		// Bullets doesn't receive a name. They're never going to be referenced
	}

	@Override
	public void reset() {
		super.reset();
		this.setName("bullet");
		this.setDestroyer(true);
		this.setInvincible(false);
		this.setColor(Color.BLACK);
	}

	public void setDirection(Vector2 direction) {
		this.direction = direction;
	}

	public void act(float delta) {
		this.setX(this.getX() + delta * speed * direction.x);
		this.setY(this.getY() + delta * speed * direction.y);
		// Remove bullet if it's out of the map
		if (this.getX() < 0 || this.getX() > PhaseScreen.STAGE_WIDTH || this.getY() < 0 || this.getY() > PhaseScreen.STAGE_HEIGHT) {
			this.remove();
		}
		super.act(delta);
	}

	@Override
	public String getImage() {
		return "bullet.png";
	}

	@Override
	public float getSpriteWidth() {
		return SCALE;
	}

	@Override
	public float getSpriteHeight() {
		return SCALE;
	}

	@Override
	public BodyType getBodyType() {
		return BodyType.CIRCLE;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public float getCollisionWidth() {
		return SCALE;
	}

	@Override
	public float getCollisionHeight() {
		return SCALE;
	}

	@Override
	public void beginDestroy() {
		endDestroy();
	}

}
