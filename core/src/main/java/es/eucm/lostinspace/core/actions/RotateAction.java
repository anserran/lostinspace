package es.eucm.lostinspace.core.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class RotateAction extends AbstractAction{

	private float degrees;

	private float speed = 360.0f;

	private float finalRotation;

	private float signum;

	private float rotation;

	public RotateAction(){

	}

	public RotateAction(float degrees){
		this();
		setDegrees(degrees);
	}

	public void setDegrees(float degrees) {
		this.degrees = degrees;
	}

	@Override
	public void setActor(Actor a) {
		super.setActor(a);
		if ( a != null ){
			signum = Math.signum(degrees);
			finalRotation = a.getRotation() + degrees;
			rotation = Math.abs(degrees);
		}
	}

	@Override
	public boolean act(float delta) {
		if ( rotation <= 0 ){
			return true;
		}
		float deltaRot = Math.min(speed * delta, rotation);
		rotation -= deltaRot;
		actor.setRotation(actor.getRotation() + signum * deltaRot);

		if ( rotation <= 0 ){
			actor.setRotation(finalRotation);
			return true;
		}
		return false;
	}

}
