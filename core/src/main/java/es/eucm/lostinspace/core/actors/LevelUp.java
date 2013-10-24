
package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.graphics.g2d.Sprite;

import es.eucm.lostinspace.core.LevelManager;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class LevelUp extends AbstractActor {

	public static final String TYPE = "levelup";

	private LevelManager.Abilities ability;

	@Override
	public void reset () {
		super.reset();
		this.setInvincible(false);
		this.setDestroyer(false);
	}

	public LevelManager.Abilities getAbility () {
		return ability;
	}

	public void setAbility (LevelManager.Abilities ability) {
		this.ability = ability;
	}

	@Override
	public String getImage () {
		return "levelups.png";
	}

	@Override
	public float getSpriteWidth () {
		return PhaseScreen.SQUARE_SIZE;
	}

	@Override
	public float getSpriteHeight () {
		return PhaseScreen.SQUARE_SIZE;
	}

	@Override
	public BodyType getBodyType () {
		return BodyType.RECTANGLE;
	}

	@Override
	public float getCollisionWidth () {
		return PhaseScreen.SQUARE_SIZE * 0.9f;
	}

	@Override
	public float getCollisionHeight () {
		return PhaseScreen.SQUARE_SIZE * 0.9f;
	}

	@Override
	public void beginContact (AbstractActor b) {
		// If it is one of the main characters
		if (b.getName() != null && (b.getName().equals("captain") || b.getName().equals("susan") || b.getName().equals("ben"))) {
			this.destroy();
			PhaseScreen.levelManager.upLevel(ability);
		}
	}

	@Override
	public void setSprite (Sprite sprite) {
		super.setSprite(sprite);
		int size = sprite.getTexture().getWidth() / 5;
		sprite.setRegion(ability.ordinal() * size, 0, size, size);
	}

	@Override
	public String getType () {
		return TYPE;
	}
}
