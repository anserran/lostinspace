
package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

import es.eucm.lostinspace.core.actions.AbstractAction;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Trigger extends AbstractActor {

	public static final String TYPE = "trigger";

	private Array<Effect> effects;

	private boolean diesAfterContact;

	/** If the trigger is hidden in the map (but still there) */
	private boolean hidden;

	public Trigger () {
		effects = new Array<Effect>();
	}

	@Override
	public void reset () {
		super.reset();
		this.effects.clear();
		this.setDestroyer(false);
		this.setInvincible(true);
		this.setDiesAfterContact(true);
	}

	public boolean isHidden () {
		return hidden;
	}

	public void setHidden (boolean hidden) {
		this.hidden = hidden;
	}

	@Override
	public String getImage () {
		return "trigger.png";
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
		return BodyType.CIRCLE;
	}

	@Override
	public float getCollisionWidth () {
		return PhaseScreen.SQUARE_SIZE * 0.75f;
	}

	@Override
	public float getCollisionHeight () {
		return PhaseScreen.SQUARE_SIZE * 0.75f;
	}

	public void setDiesAfterContact (boolean diesAfterContact) {
		this.diesAfterContact = diesAfterContact;
	}

	public boolean isDiesAfterContact () {
		return diesAfterContact;
	}

	public void addActionEffect (Effect e) {
		effects.add(e);
	}

	@Override
	public void setSprite (Sprite sprite) {
		super.setSprite(sprite);
		int size = sprite.getTexture().getWidth() / 2;
		sprite.setRegion(0, 0, size, size);
	}

	@Override
	protected void setSpriteTransformations (Sprite sprite) {
		super.setSpriteTransformations(sprite);
		sprite.setColor(isHidden() ? Color.CLEAR : this.getColor());
	}

	@Override
	public void beginContact (AbstractActor b) {
		// If it's not a bullet
		if (!"bullet".equals(b.getName())) {
			super.beginContact(b);
			for (Effect e : effects) {
				e.execute();
			}
			if (isDiesAfterContact()) {
				this.destroy();
			}
		}
	}

	public static class Effect {
		private String targetName;
		private AbstractAction action;

		public Effect (String targetName, AbstractAction action) {
			this.targetName = targetName;
			this.action = action;
		}

		public void execute () {
			Actor a = PhaseScreen.map.findActor(targetName);
			if (a != null) {
				a.addAction(action);
			}
		}
	}

	@Override
	public String getType () {
		return TYPE;
	}
}
