
package es.eucm.lostinspace.core.actions;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public abstract class AbstractAction extends Action {

	private AbstractAction nextAction;

	// Aux var
	private Array<Action> loopActions;

	public AbstractAction () {
		loopActions = new Array<Action>();
	}

	public void setNextAction (AbstractAction nextAction) {
		this.nextAction = nextAction;
	}

	public AbstractAction getNextAction () {
		return nextAction;
	}

	@Override
	public void restart () {
		nextAction = null;
	}

	public void setActor (Actor a) {
		// We don't free the action if it is in a loop, it's still in use
		if (a == null && isInLoop()) {
			AbstractAction nextAction = this.getNextAction();
			this.restart();
			this.setNextAction(nextAction);
			this.actor.addAction(nextAction);
			return;
		}

		// The action has ended, let's fire the next action
		if (this.actor != null && a == null && nextAction != null) {
			this.actor.addAction(nextAction);
		}

		// To avoid null exception with actions that are linked directly to themselves
		if (!(a == null && nextAction == this)) {
			super.setActor(a);
		}
	}

	public boolean isInLoop () {
		loopActions.clear();
		AbstractAction currentAction = this.getNextAction();
		while (currentAction != null && currentAction != this && !loopActions.contains(currentAction, true)) {
			loopActions.add(currentAction);
			currentAction = currentAction.getNextAction();
		}
		return currentAction == this;
	}

}
