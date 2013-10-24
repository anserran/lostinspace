package es.eucm.lostinspace.core.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class MoveAction extends AbstractAction {

	private int distance;

	private float speed;

	private float destX;

	private float destY;

	private float module;

	private Vector2 direction;


	public MoveAction(){
		direction = new Vector2();
	}

	public MoveAction(int distance){
		this(distance, PhaseScreen.SQUARE_SIZE * 3);
	}

	public MoveAction(int distance, float speed ){
		this();
		setDistance(distance);
		setSpeed(speed);
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public void setActor(Actor actor) {
		super.setActor(actor);
		if ( actor == null ){
			return;
		}
		// Initialization
		float initX = actor.getX();
		float initY = actor.getY();
		direction.set(distance, 0.0f);
		direction.rotate(actor.getRotation());
		direction.nor();
		module = Math.abs(distance * PhaseScreen.SQUARE_SIZE);
		destX = initX + direction.x * module;
		destY = initY + direction.y * module;

	}

	@Override
	public boolean act(float delta) {
		if ( module <= 0 ){
			return true;
		}

		float dist = Math.min( delta * speed, module);
		module -= dist;

		this.actor.setX(direction.x * dist + actor.getX());
		this.actor.setY(direction.y * dist + actor.getY());

		if ( module <= 0 ){
			this.actor.setX(destX);
			this.actor.setY(destY);
			return true;
		}

		return false;
	}
}
