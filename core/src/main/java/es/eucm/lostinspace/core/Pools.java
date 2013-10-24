
package es.eucm.lostinspace.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.utils.Pool;
import es.eucm.lostinspace.core.actions.*;
import es.eucm.lostinspace.core.actors.*;

import java.util.HashMap;
import java.util.Map;

public class Pools {

	private Map<Class<?>, Pool> pools;

	public Pools () {
		pools = new HashMap<Class<?>, Pool>();
		pools.put(Bullet.class, new Pool() {
			@Override
			protected Object newObject () {
				return new Bullet();
			}
		});
		pools.put(LevelUp.class, new Pool() {
			@Override
			protected Object newObject () {
				return new LevelUp();
			}
		});
		pools.put(Rock.class, new Pool() {
			@Override
			protected Object newObject () {
				return new Rock();
			}
		});
		pools.put(Ship.class, new Pool() {
			@Override
			protected Object newObject () {
				return new Ship();
			}
		});
		pools.put(Trigger.class, new Pool() {
			@Override
			protected Object newObject () {
				return new Trigger();
			}
		});
		pools.put(Wall.class, new Pool() {
			@Override
			protected Object newObject () {
				return new Wall();
			}
		});
		pools.put(WormHole.class, new Pool() {
			@Override
			protected Object newObject () {
				return new WormHole();
			}
		});
		pools.put(ImageActor.class, new Pool() {
			@Override
			protected Object newObject () {
				return new ImageActor();
			}
		});
		// Actions
		pools.put(MoveAction.class, new Pool() {
			@Override
			protected Object newObject () {
				Action a = new MoveAction();
				a.setPool(this);
				return a;
			}
		});
		pools.put(RotateAction.class, new Pool() {
			@Override
			protected Object newObject () {
				Action a = new RotateAction();
				a.setPool(this);
				return a;
			}
		});
		pools.put(ShootAction.class, new Pool() {
			@Override
			protected Object newObject () {
				Action a = new ShootAction();
				a.setPool(this);
				return a;
			}
		});
		pools.put(SpeakAction.class, new Pool() {
			@Override
			protected Object newObject () {
				Action a = new SpeakAction();
				a.setPool(this);
				return a;
			}
		});
		pools.put(VisibleAction.class, new Pool() {
			@Override
			protected Object newObject () {
				Action a = new VisibleAction();
				a.setPool(this);
				return a;
			}
		});
		pools.put(GhostAction.class, new Pool() {
			@Override
			protected Object newObject () {
				Action a = new GhostAction();
				a.setPool(this);
				return a;
			}
		});
	}

	public <T> T obtain (Class<T> clazz) {
		Pool pool = pools.get(clazz);
		if (pool != null) {
			T object = (T)pool.obtain();
			if (object instanceof Pool.Poolable) {
				((Pool.Poolable)object).reset();
			}
			return object;
		} else {
			Gdx.app.log("Pools", "No pool for class " + clazz);
			return null;
		}
	}

	public void free (Object o) {
		Pool pool = pools.get(o.getClass());
		if (pool != null) {
			pool.free(o);
		} else {
			Gdx.app.log("Pools", "No pool for class " + o.getClass());
		}
	}

}
