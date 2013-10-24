
package es.eucm.lostinspace.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import es.eucm.lostinspace.core.actors.AbstractActor;

public class SpaceContactListener implements ContactListener {
	@Override
	public void beginContact (Contact contact) {
		if (contact.getFixtureA() != null && contact.getFixtureB() != null) {
			AbstractActor a = (AbstractActor)contact.getFixtureA().getBody().getUserData();
			AbstractActor b = (AbstractActor)contact.getFixtureB().getBody().getUserData();
			if (a.isCheckCollissions() && a.isVisible() && b.isCheckCollissions() && b.isVisible()) {
				a.beginContact(b);
				b.beginContact(a);
				String aName = a.getName() != null ? a.getName() : a.getType();
				String bName = b.getName() != null ? b.getName() : b.getType();
				Gdx.app.log("Collisions", "Begin contact " + aName + " <-X-> " + bName);
			}
		}
	}

	@Override
	public void endContact (Contact contact) {
		if (contact.getFixtureA() != null && contact.getFixtureB() != null) {
			AbstractActor a = (AbstractActor)contact.getFixtureA().getBody().getUserData();
			AbstractActor b = (AbstractActor)contact.getFixtureB().getBody().getUserData();
			if (a.isCheckCollissions() && a.isVisible() && b.isCheckCollissions() && b.isVisible()) {
				a.endContact(b);
				b.endContact(a);
				String aName = a.getName() != null ? a.getName() : a.getType();
				String bName = b.getName() != null ? b.getName() : b.getType();
				Gdx.app.log("Collisions", "End contact " + aName + " <- -> " + bName);
			}
		}
	}

	@Override
	public void preSolve (Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve (Contact contact, ContactImpulse impulse) {
	}
}
