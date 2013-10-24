package es.eucm.lostinspace.core.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;

public class VisibleAction extends AbstractAction {

	private boolean visible;

	private int count;

	public VisibleAction() {
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void setActor(Actor a) {
		super.setActor(a);
		count = 2;
	}

	@Override
	public boolean act(float delta) {
		count--;
		if ( count == 0 ){
			this.actor.setVisible(visible);
		}
		return count <= 0;
	}
}
