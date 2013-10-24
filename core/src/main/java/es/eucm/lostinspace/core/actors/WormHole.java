
package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class WormHole extends Trigger {

	private float rotationSpeed = 50.0f;
	private float extraRot = 0.0f;

	public static final String TYPE = "exit";

	public void reset () {
		super.reset();
		extraRot = 0.0f;
		this.setInvincible(true);
		this.setDestroyer(false);
		this.setDiesAfterContact(false);
	}

	@Override
	public void setSprite (Sprite sprite) {
		super.setSprite(sprite);
		int size = sprite.getTexture().getWidth();
		sprite.setRegion(0, 0, size, size);
	}

	@Override
	public String getImage () {
		return "warp.png";
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
	public void beginContact (AbstractActor b) {
		super.beginContact(b);
		if (PhaseScreen.phaseManager.exit(b)) {
			b.teleport(this.getX(), this.getY());
		}
	}

	@Override
	public String getType () {
		return TYPE;
	}
}
