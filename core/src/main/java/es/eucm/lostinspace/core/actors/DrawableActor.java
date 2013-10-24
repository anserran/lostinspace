package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class DrawableActor extends Actor {

	private Drawable drawable;

	public DrawableActor(Drawable drawable) {
		this.drawable = drawable;
	}
}
