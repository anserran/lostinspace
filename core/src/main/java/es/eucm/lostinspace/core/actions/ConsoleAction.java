
package es.eucm.lostinspace.core.actions;

import es.eucm.lostinspace.core.screens.PhaseScreen;

public class ConsoleAction extends AbstractAction {

	private String type;

	private String text;

	public String getType () {
		return type;
	}

	public void setType (String type) {
		this.type = type;
	}

	public String getText () {
		return text;
	}

	public void setText (String text) {
		this.text = text;
	}

	@Override
	public boolean act (float delta) {
		PhaseScreen.communicator.addMessage(type, text);
		return true;
	}

}
