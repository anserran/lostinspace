
package es.eucm.lostinspace.core.actors;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Pool;

import es.eucm.lostinspace.core.AssetManager;
import es.eucm.lostinspace.core.actions.AbstractAction;
import es.eucm.lostinspace.core.actions.SpeakAction;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.lostinspace.core.tweens.ActorTweenAccessor;

public abstract class AbstractActor extends Group implements TweenCallback, Pool.Poolable {

	public enum BodyType {
		CIRCLE, RECTANGLE
	}

	protected Sprite sprite;

	private boolean invincible;

	private boolean destroyer;

	private boolean destroyed;

	private boolean checkCollissions = true;

	private Sprite speakBalloon;

	private Label speakText;

	private boolean speaking;

	protected Body b;

	public AbstractActor () {
		speakBalloon = new Sprite((Texture)PhaseScreen.assetManager.get("balloon.png"));
		speakBalloon.setOrigin(PhaseScreen.SQUARE_SIZE * 1.5f, PhaseScreen.SQUARE_SIZE / 2);
		speakText = new Label("", new Label.LabelStyle(PhaseScreen.assetManager.getFont(AssetManager.SPEAK_FONT), Color.WHITE));
		speakText.setWrap(true);
		speakText.setAlignment(Align.center);
		speakText.setWidth(PhaseScreen.SQUARE_SIZE * 2.5f);
	}

	public boolean isCheckCollissions () {
		return checkCollissions;
	}

	public void setCheckCollissions (boolean checkCollissions) {
		this.checkCollissions = checkCollissions;
	}

	public boolean isDestroyer () {
		return destroyer;
	}

	public boolean isInvincible () {
		return invincible;
	}

	public void setInvincible (boolean invincible) {
		this.invincible = invincible;
	}

	public void setDestroyer (boolean destroyer) {
		this.destroyer = destroyer;
	}

	public void setSprite (Sprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public void reset () {
		this.clearActions();
		this.setBounds(0, 0, 0, 0);
		this.setScale(1.0f, 1.0f);
		this.setRotation(0.0f);
		this.setOrigin(0, 0);
		this.setColor(Color.WHITE);
		this.setName(null);
		destroyed = false;
		checkCollissions = true;
		invincible = false;
		speaking = false;
		this.setVisible(true);
	}

	@Override
	public void addAction (Action action) {
		if (getActions().size == 1) {
			AbstractAction firstAction = ((AbstractAction)getActions().get(0));
			AbstractAction currentAction = firstAction;
			while (currentAction.getNextAction() != null && currentAction.getNextAction() != firstAction
				&& currentAction.getNextAction() != action) {
				currentAction = currentAction.getNextAction();
			}

			// We are trying to add an action already the queue, remove the queue
			if (currentAction.getNextAction() == action) {
				this.getActions().clear();
			}
			// Actor was executing a looped action, remove everything
			else if (currentAction.getNextAction() == firstAction) {
				this.getActions().clear();
				super.addAction(action);
			} else {
				currentAction.setNextAction((AbstractAction)action);
			}
		} else {
			super.addAction(action);
		}
	}

	/** Initializes the actor
	 * 
	 * @param g */
	public void setParent (Group g) {
		super.setParent(g);
		// If it's creation
		if (g != null) {
			destroyed = false;
			createPhysicBody();
			createSprite();
			setSize(getSpriteWidth(), getSpriteHeight());
			setOrigin(getSpriteWidth() / 2, getSpriteHeight() / 2);
		} else {
			PhaseScreen.free(this);
		}
	}

	public void act (float delta) {
		if (destroyed) {
			clear();
			remove();
			return;
		}
		super.act(delta);
		updateBody();
	}

	@Override
	public void draw (SpriteBatch batch, float parentAlpha) {
		setSpriteTransformations(sprite);
		sprite.draw(batch, parentAlpha);
		if (speaking) {
			speakBalloon.draw(batch, parentAlpha);
			speakText.draw(batch, parentAlpha);
		}
		super.draw(batch, parentAlpha);
	}

	/** Sets extra transformations for the sprite before being drawn
	 * 
	 * @param sprite the sprite */
	protected void setSpriteTransformations (Sprite sprite) {
		sprite.setColor(this.getColor());
		sprite.setPosition(getX(), getY());
		sprite.setOrigin(getOriginX(), getOriginY());
		sprite.setRotation(getRotation());
		sprite.setScale(getScaleX(), getScaleY());
	}

	/** Creates the physic body for the actor */
	private void createPhysicBody () {
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.allowSleep = false;
		def.bullet = true;
		def.position.x = getX();
		def.position.y = getY();
		b = PhaseScreen.world.createBody(def);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		fixtureDef.density = 1.0f;
		fixtureDef.shape = getBodyShape();

		b.setUserData(this);
		b.createFixture(fixtureDef);
		updateBody();
	}

	/** Create the sprite */
	protected void createSprite () {
		TextureRegion textureRegion = new TextureRegion((Texture)PhaseScreen.assetManager.get(getImage()));
		Sprite sprite = new Sprite(textureRegion);
		sprite.setSize(getSpriteWidth(), getSpriteHeight());
		setSprite(sprite);
	}

	public void updateBody () {
		if (b != null) {
			Actor parent = getParent();
			float deltaX = 0;
			float deltaY = 0;
			while (parent != null) {
				deltaX += parent.getX();
				deltaY += parent.getY();
				parent = parent.getParent();
			}
			b.setTransform(deltaX + getX() + getWidth() / 2, deltaY + getY() + getHeight() / 2, (float)Math.toRadians(getRotation()));
		}
	}

	/** Returns the physic shape for collissions
	 * 
	 * @return */
	protected Shape getBodyShape () {
		Shape shape = null;
		switch (getBodyType()) {
		case CIRCLE:
			shape = new CircleShape();
			shape.setRadius(getCollisionWidth() / 2);
			break;
		case RECTANGLE:
			shape = new PolygonShape();
			((PolygonShape)shape).setAsBox(getCollisionWidth() / 2, getCollisionHeight() / 2);
			break;
		}
		return shape;
	}

	@Override
	public boolean remove () {
		dispose();
		return super.remove();
	}

	/** Disposes the actor. Deletes the physic body */
	public void dispose () {
		if (b != null) {
			PhaseScreen.world.destroyBody(b);
			b = null;
		}
	}

	/** The actor collided and it's destroyed */
	public void destroy () {
		this.setCheckCollissions(false);
		this.clearActions();
		this.beginDestroy();
		PhaseScreen.phaseManager.actorDestroyed(this);
	}

	public void speak (String text, SpeakAction.Position position) {
		if ("".equals(text)) {
			this.speaking = false;
		} else {
			this.speaking = true;
			speakText.setText(text);
			switch (position) {
			case right:
				speakBalloon.setPosition(getX() + PhaseScreen.SQUARE_SIZE, getY());
				speakBalloon.setRotation(0);
				speakText.setPosition(getX() + PhaseScreen.SQUARE_SIZE * 1.25f, getY() + PhaseScreen.SQUARE_SIZE / 2);
				break;
			case left:
				speakBalloon.setRotation(180);
				speakBalloon.setPosition(getX() - PhaseScreen.SQUARE_SIZE * 3, getY());
				speakText.setPosition(getX() - PhaseScreen.SQUARE_SIZE * 2.75f, getY() + PhaseScreen.SQUARE_SIZE / 2);
				break;
			}
		}
	}

	public void teleport (float x, float y) {
		this.setCheckCollissions(false);
		this.clearActions();
		beginTeleport(x, y);
	}

	/** Begins teleport */
	public void beginTeleport (float x, float y) {
		Tween.to(this, ActorTweenAccessor.TELEPORT, 0.5f).target(0.1f, 0.1f, 1480.0f, x, y).start(PhaseScreen.tweenManager)
			.setCallback(this);
	}

	/** Begins the destruction of the actor */
	public void beginDestroy () {
		Tween.to(this, ActorTweenAccessor.DESTRUCTION, 0.5f).target(1.5f, 1.5f, 0.0f).start(PhaseScreen.tweenManager)
			.setCallback(this);
	}

	/** Ends the destruction of the actor */
	public void endDestroy () {
		this.destroyed = true;
		Gdx.app.log("AbstractActor", this.getName() + " destroyed.");
	}

	public void beginContact (AbstractActor b) {
		if (b.isDestroyer() && !isInvincible()) {
			destroy();
		}
	}

	public void endContact (AbstractActor b) {

	}

	@Override
	public void onEvent (int type, BaseTween<?> source) {
		if (type == TweenCallback.COMPLETE) {
			endDestroy();
		}
	}

	// Methdos required in initialization

	/** Returns the texture name for this actor
	 * 
	 * @return */
	public abstract String getImage ();

	public abstract float getSpriteWidth ();

	public abstract float getSpriteHeight ();

	public abstract BodyType getBodyType ();

	public abstract String getType ();

	/** Returns the collision width
	 * 
	 * @return */
	public abstract float getCollisionWidth ();

	/** Returns the collision height
	 * 
	 * @return */
	public abstract float getCollisionHeight ();

}
