package es.eucm.lostinspace.core.screens;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import es.eucm.lostinspace.core.AssetManager;
import es.eucm.lostinspace.core.LevelManager;
import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.MessageListener;
import es.eucm.lostinspace.core.PhaseManager;
import es.eucm.lostinspace.core.Pools;
import es.eucm.lostinspace.core.SpaceContactListener;
import es.eucm.lostinspace.core.actions.GhostAction;
import es.eucm.lostinspace.core.actions.MoveAction;
import es.eucm.lostinspace.core.actions.RotateAction;
import es.eucm.lostinspace.core.actions.ShootAction;
import es.eucm.lostinspace.core.actions.SpeakAction;
import es.eucm.lostinspace.core.actors.Grid;
import es.eucm.lostinspace.core.actors.ImageActor;
import es.eucm.lostinspace.core.actors.Map;
import es.eucm.lostinspace.core.hud.Console;
import es.eucm.lostinspace.core.hud.communicator.Communicator;
import es.eucm.lostinspace.core.hud.maphud.MapHud;
import es.eucm.lostinspace.core.parsers.ActionsCreator;
import es.eucm.lostinspace.core.tweens.ActorTweenAccessor;
import es.eucm.lostinspace.core.util.XmlPrettifier;
import es.eucm.tools.xml.XMLParser;

public class PhaseScreen implements Screen {

	public static final int GRID_COLUMNS = 11;

	public static final int GRID_ROWS = 9;

	public static final int CONSOLE_COLUMNS = 5;

	public static final int TOTAL_COLUMNS = GRID_COLUMNS + CONSOLE_COLUMNS;

	public static int SQUARE_SIZE = 66;

	public static final int STAGE_WIDTH = TOTAL_COLUMNS * SQUARE_SIZE;

	public static final int STAGE_HEIGHT = GRID_ROWS * SQUARE_SIZE;

	public static XMLParser xmlParser;

	public static Stage stage;

	public static Group map;

	public static MapHud mapHud;

	public static PhaseManager phaseManager;

	public static XmlPrettifier xmlPrettifier = new XmlPrettifier(50);

	public static boolean debug = true;

	public static AssetManager assetManager;

	public static World world;

	public static LevelManager levelManager;

	private Box2DDebugRenderer box2Drenderer;

	private static MessageListener messageListener;

	public static Communicator communicator;

	public static Console console;

	public static TweenManager tweenManager;

	public static Pools pools;
	private boolean skipCutscene;

	public PhaseScreen() {
		skipCutscene = false;
		phaseManager = LostInSpace.phaseManager;
		// Color to clear the background
		pools = LostInSpace.pools;
		// Tween manager
		tweenManager = new TweenManager();
		Tween.registerAccessor(Actor.class, new ActorTweenAccessor());
		Tween.setCombinedAttributesLimit(5);
		Tween.setWaypointsLimit(1);

		messageListener = new MessageListener();
		assetManager = new AssetManager();
		assetManager.loadAssets();
		// Init levels

		communicator = new Communicator();
		levelManager = LostInSpace.levelManager;
		initPhysicWorld();
		stage = new Stage(new StretchViewport(STAGE_WIDTH, STAGE_HEIGHT));
		map = new Group();
		Map realMap = new Map();
		realMap.addActor(map);
		realMap.setX(CONSOLE_COLUMNS * SQUARE_SIZE);
		mapHud = new MapHud();
		mapHud.setX(CONSOLE_COLUMNS * SQUARE_SIZE);
		console = new Console();
		communicator.setY(PhaseScreen.SQUARE_SIZE * 6);
		communicator
				.addMessage(
                        "info",
                        PhaseScreen
                                .i18n("Write <start name=\"nickname\"/>, replacing nickname with your name. Click the thunder to send it."));

		stage.addActor(new Grid());
		stage.addActor(realMap);
		ImageActor consoleBg = pools.obtain(ImageActor.class);
		consoleBg.setTextureRegion("helpbg.png");
		consoleBg.setSize(
				PhaseScreen.SQUARE_SIZE * PhaseScreen.CONSOLE_COLUMNS,
				PhaseScreen.SQUARE_SIZE * PhaseScreen.GRID_ROWS);
		stage.addActor(consoleBg);
		stage.addActor(mapHud);
		stage.addActor(communicator);
		stage.addActor(console);

		// Debug
		if (debug) {
			addDebug();
		}

		stage.addCaptureListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				switch (keycode) {
				case Input.Keys.SPACE:
					skipCutscene();
					break;
				}
				return super.keyDown(event, keycode);
			}
		});

	}

	private void addDebug() {

		box2Drenderer = new Box2DDebugRenderer();
		stage.addCaptureListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				Actor player = stage.getRoot().findActor("captain");
				if (player != null) {
					switch (keycode) {
					case Input.Keys.UP:
						ActionsCreator.Speed s = (Gdx.input
								.isKeyPressed(Input.Keys.SHIFT_RIGHT) ? ActionsCreator.Speed.HIGH
								: ActionsCreator.Speed.NORMAL);
						player.addAction(new MoveAction(1, s.getSpeedValue()));
						break;
					case Input.Keys.DOWN:
						player.addAction(new MoveAction(-1));
						break;
					case Input.Keys.RIGHT:
						player.addAction(new RotateAction(-90));
						break;
					case Input.Keys.LEFT:
						player.addAction(new RotateAction(90));
						break;
					case Input.Keys.CONTROL_RIGHT:
						player.addAction(new ShootAction());
						break;
					case Input.Keys.CONTROL_LEFT:
						GhostAction b = new GhostAction();
						b.setInvincible(true);
						b.setTime(5.0f);
						player.addAction(b);
						break;
					case Input.Keys.F3:
						SpeakAction a = new SpeakAction();
						a.setText("Hello world! Hello world! Hello world! Hello world! Hello world!");
						a.setPosition(SpeakAction.Position.left);
						player.addAction(a);
						break;
					}
					return true;
				}
				return super.keyDown(event, keycode);
			}
		});

		stage.addCaptureListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				switch (keycode) {
				case Input.Keys.F1:
					console.sendCommands();
					break;
				case Input.Keys.F5:
					phaseManager.restartPhase();
					break;
				case Input.Keys.F6:
					phaseManager.nextPhase();
					break;
				case Input.Keys.F4:
					debug = !debug;
					break;
				case Input.Keys.ESCAPE:
					phaseManager.loadFirstPhase();
					break;
				case Input.Keys.F7:
					levelManager.maxLevels();
					break;
				}
				return true;
			}
		});

	}

	@Override
	public void dispose() {
		world.dispose();
		stage.dispose();
		assetManager.dispose();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		float step = Math.min(delta, 1 / 30f);
		if (skipCutscene && phaseManager.isInCutscene()) {
			while (phaseManager.isInCutscene()) {
				tweenManager.update(step);
				phaseManager.act(step);
				stage.act(step);
				world.step(step, 10, 10);
			}
			skipCutscene = false;
		} else {
			tweenManager.update(step);
			phaseManager.act(step);
			stage.act(step);
			world.step(step, 10, 10);
		}
		stage.draw();
		if (debug) {
			box2Drenderer.render(world, stage.getCamera().combined);
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	public static <T> T obtain(Class<T> clazz) {
		return pools.obtain(clazz);
	}

	public static void free(Object o) {
		pools.free(o);
	}

	public void initPhysicWorld() {
		world = new World(new Vector2(0.0f, 0.0f), true);
		world.setContactListener(new SpaceContactListener());
	}

	public static String i18n(String s) {
		return assetManager.i18n(s);
	}

	public static void msg(String msg) {
		messageListener.msg(msg);
	}

	public void skipCutscene() {
		if (phaseManager.isInCutscene()) {
			skipCutscene = true;
		}
	}

}
