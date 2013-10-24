
package es.eucm.lostinspace.core.parsers.scripts;

import com.badlogic.gdx.utils.Array;

public class ScriptStep {

	private Array<ScriptAction> actions;

	public ScriptStep () {
		actions = new Array<ScriptAction>();
	}

	public void start () {
		for (ScriptAction a : actions) {
			a.start();
		}
	}

	public boolean isDone () {
		for (ScriptAction a : actions) {
			if (!a.isDone()) {
				return false;
			}
		}
		return true;
	}

	public void add (ScriptAction scriptAction) {
		actions.add(scriptAction);
	}
}
