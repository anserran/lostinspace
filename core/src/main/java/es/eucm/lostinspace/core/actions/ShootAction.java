package es.eucm.lostinspace.core.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.lostinspace.core.actors.Bullet;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class ShootAction extends AbstractAction {

	private boolean done;

	private float time;

	@Override
	public void setActor(Actor a) {
		super.setActor(a);
		done = false;
		time = 1.0f;
	}

	@Override
	public boolean act(float delta) {
		if (time <= 0.0f) {
			return true;
		}
		time -= delta;

		if (!done) {
			done = true;

			Bullet b = PhaseScreen.obtain(Bullet.class);
			Vector2 bulletDirection = new Vector2(actor.getWidth() / 1.5f, 0.0f);
			bulletDirection.rotate(actor.getRotation());
			b.setPosition(actor.getX() + PhaseScreen.SQUARE_SIZE / 2 + bulletDirection.x, actor.getY() + PhaseScreen.SQUARE_SIZE / 2 + bulletDirection.y);
			bulletDirection.nor();
			b.setDirection(bulletDirection);
			actor.getParent().addActor(b);
		}

		return time <= 0.0f;
	}
}
