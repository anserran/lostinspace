
package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Rock extends AbstractActor {

	public static final String TYPE = "rock";

	private int radius;
	private String type;
	private float rotationSpeed;
	private float extraRot = 0.0f;

	@Override
	public void reset () {
		super.reset();
		this.setInvincible(false);
		this.setDestroyer(true);
		rotationSpeed = (float)Math.random() * 200.0f - 100.0f;
		extraRot = 0.0f;
	}

	public int getRadius () {
		return radius;
	}

	public void setRadius (int radius) {
		this.radius = radius;
	}

	@Override
	public String getImage () {
		return "rocks.png";
	}

	@Override
	public float getSpriteWidth () {
		return PhaseScreen.SQUARE_SIZE * radius;
	}

	@Override
	public float getSpriteHeight () {
		return PhaseScreen.SQUARE_SIZE * radius;
	}

	@Override
	public BodyType getBodyType () {
		return BodyType.CIRCLE;
	}

	@Override
	public float getCollisionWidth () {
		return PhaseScreen.SQUARE_SIZE * radius * 0.9f;
	}

	@Override
	public float getCollisionHeight () {
		return PhaseScreen.SQUARE_SIZE * radius * 0.9f;
	}

	public void setType (String type) {
		this.type = type;
		setInvincible(type.equals("black"));
	}

	@Override
	public void act (float delta) {
		super.act(delta);
		extraRot += rotationSpeed * delta;
	}

	@Override
	protected void setSpriteTransformations (Sprite sprite) {
		super.setSpriteTransformations(sprite);
		sprite.setRotation(this.getRotation() + extraRot);
	}

	@Override
	public void beginDestroy () {
		super.beginDestroy();
		this.setColor(1.0f, 0.7f, 0.7f, 1.0f);
	}

	@Override
	public void setSprite (Sprite sprite) {
		super.setSprite(sprite);
		int size = sprite.getTexture().getWidth() / 2;
		int x = 0;
		if (type.equals("white")) {
			x = size;
		}
		sprite.setRegion(x, 0, size, size);
	}

	@Override
	public String getType () {
		return TYPE;
	}
}
