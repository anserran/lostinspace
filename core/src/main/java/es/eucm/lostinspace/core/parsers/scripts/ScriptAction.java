package es.eucm.lostinspace.core.parsers.scripts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.lostinspace.core.actions.AbstractAction;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class ScriptAction {

	private AbstractAction action;
	private String target;
	private Actor actor;

	public ScriptAction(AbstractAction action, String target) {
		this.action = action;
		this.target = target;
	}

	public void start() {
		actor = PhaseScreen.map.findActor(target);
		if (actor != null) {
			actor.addAction(action);
		} else {
			Gdx.app.log( "ScriptAction", target + PhaseScreen.i18n(" not found between the current actors."));
		}
	}

	public boolean isDone() {
		return actor == null || actor.getActions().size == 0;
	}
}
