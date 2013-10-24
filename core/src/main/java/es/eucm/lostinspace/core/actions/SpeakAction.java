
package es.eucm.lostinspace.core.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import es.eucm.lostinspace.core.actors.AbstractActor;

public class SpeakAction extends AbstractAction {

	public static float TIME_PER_LETTER = 0.10f;

	public enum Position {
		top, right, bottom, left
	};

	private boolean done;

	private float time;

	private String text;

	private Position position;

	public SpeakAction () {

	}

	public Position getPosition () {
		return position;
	}

	public void setPosition (Position position) {
		this.position = position;
	}

	public String getText () {
		return text;
	}

	public void setText (String text) {
		this.text = text;
	}

	@Override
	public void setActor (Actor a) {
		if (a == null && this.actor instanceof AbstractActor) {
			((AbstractActor)this.actor).speak("", position);
		}
		done = false;
		time = text.length() * TIME_PER_LETTER;
		super.setActor(a);
	}

	@Override
	public boolean act (float delta) {
		if (time <= 0.0f) {
			return true;
		}
		time -= delta;

		if (!done) {
			if (actor instanceof AbstractActor) {
				((AbstractActor)this.actor).speak(text, position);
			}
			done = true;
		}

		return time <= 0.0f;
	}
}
