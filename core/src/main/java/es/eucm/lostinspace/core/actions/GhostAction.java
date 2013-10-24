package es.eucm.lostinspace.core.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.lostinspace.core.actors.AbstractActor;

public class GhostAction extends AbstractAction {

	private float time;

	private boolean invincible;
	
	private boolean oldInvincible;

	private float reaminingTime;

	public GhostAction() {
	}

	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}

	public void setTime(float time) {
		this.time = time;
	}

	@Override
	public void setActor(Actor a) {
		// Disable invincible
		if (a == null && actor instanceof AbstractActor) {
			((AbstractActor) actor).setInvincible(oldInvincible);
		}
		reaminingTime = time;
		super.setActor(a);
		if (a != null && a instanceof AbstractActor) {
			oldInvincible = ((AbstractActor) a).isInvincible();
			((AbstractActor) a).setInvincible(invincible || oldInvincible);
		}
	}

	@Override
	public boolean act(float delta) {
		if (reaminingTime <= 0) {
			return true;
		}
		reaminingTime -= delta;
		return reaminingTime <= 0;
	}
}
