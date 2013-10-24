
package es.eucm.lostinspace.core.hud.maphud;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Pool;

import es.eucm.lostinspace.core.AssetManager;
import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.actors.ImageActor;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.lostinspace.core.tweens.ActorTweenAccessor;

public class MapHud extends Group implements TweenCallback {

	/** Start screen */
	private ImageActor startScreen;

	/** Game over screen */
	private ImageActor gameOverScreen;

	/** Actor that appears between scenes */
	private ImageActor transition;

	/** Group holding in game scores */
	private Label score;

	/** Phase results */
	private PhaseResults phaseResults;

	/** Score style */
	private Label.LabelStyle scoreStyle;

	private String scoreString;

	/** Labels pool */
	private Pool<Label> labelsPool = new Pool<Label>() {
		@Override
		protected Label newObject () {
			return new Label("", scoreStyle);
		}
	};

	public MapHud () {
		startScreen = new ImageActor();
		startScreen.setTextureRegion("titlescreen.png");
		startScreen.setSize(PhaseScreen.GRID_COLUMNS * PhaseScreen.SQUARE_SIZE, PhaseScreen.GRID_ROWS * PhaseScreen.SQUARE_SIZE);
		gameOverScreen = LostInSpace.pools.obtain(ImageActor.class);
		gameOverScreen.setTextureRegion("endscreen.png");
		gameOverScreen.setVisible(false);
		gameOverScreen.setSize(PhaseScreen.GRID_COLUMNS * PhaseScreen.SQUARE_SIZE, PhaseScreen.GRID_ROWS * PhaseScreen.SQUARE_SIZE);
		gameOverScreen.setPosition(PhaseScreen.GRID_COLUMNS * PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE);
		phaseResults = new PhaseResults();
		phaseResults.setPosition(PhaseScreen.SQUARE_SIZE, PhaseScreen.GRID_ROWS * (PhaseScreen.SQUARE_SIZE));
		scoreString = PhaseScreen.i18n("Score:");
		scoreStyle = new Label.LabelStyle(PhaseScreen.assetManager.getFont(AssetManager.SCORE_FONT), Color.GREEN);
		score = new Label(scoreString + "\n0", scoreStyle);
		score.setPosition(PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_COLUMNS - 3), PhaseScreen.SQUARE_SIZE
			* (PhaseScreen.GRID_ROWS - 1));
		score.setAlignment(Align.left);
		score.setWidth(PhaseScreen.STAGE_WIDTH);
		initTransition();
		this.addActor(startScreen);
		this.addActor(gameOverScreen);
		this.addActor(phaseResults);
		this.addActor(score);
	}

	@Override
	public void act (float delta) {
		score.setText(scoreString + "\n" + PhaseScreen.levelManager.getTotalScore());
	}

	public void startGame () {
		Tween.to(startScreen, ActorTweenAccessor.POSITION, 0.5f).ease(TweenEquations.easeOutCubic)
			.target(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE).setCallback(new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				if (type == TweenCallback.COMPLETE) {
					Tween.to(startScreen, ActorTweenAccessor.X, 1.0f).target(PhaseScreen.SQUARE_SIZE * PhaseScreen.GRID_COLUMNS)
							.start(PhaseScreen.tweenManager);
				}
			}
		}).delay(0.5f).start(PhaseScreen.tweenManager);
	}

	public void showPhaseResults () {
		phaseResults.showResults();
		score.setVisible(false);
	}

	public void initTransition () {
		transition = LostInSpace.pools.obtain(ImageActor.class);
		transition.setTextureRegion("wall.png", 1, 1, 1, 1, false);
		transition.setSize(PhaseScreen.GRID_COLUMNS * PhaseScreen.SQUARE_SIZE, PhaseScreen.GRID_ROWS * PhaseScreen.SQUARE_SIZE);
		transition.setVisible(false);
		transition.setColor(Color.BLACK);
		addActor(transition);
	}

	public void startPhase () {
		score.setVisible(true);
		transition.setVisible(true);
		transition.setScale(1.0f, 1.0f);
		Tween.to(transition, ActorTweenAccessor.SCALE, 1.0f).target(0.0f, 0.0f).start(PhaseScreen.tweenManager).setCallback(this);
	}

	@Override
	public void onEvent (int type, BaseTween<?> source) {
		if (type == TweenCallback.COMPLETE) {
			transition.setVisible(false);
		}
	}

	/** End game animation */
	public void endGame () {
		this.score.setVisible(true);
		this.phaseResults.setVisible(false);
		gameOverScreen.setVisible(true);
		Tween.to(gameOverScreen, ActorTweenAccessor.X, 0.5f).ease(TweenEquations.easeOutCubic).target(PhaseScreen.SQUARE_SIZE)
			.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					if (type == TweenCallback.COMPLETE) {
						Tween.to(gameOverScreen, ActorTweenAccessor.POSITION, 1.0f).target(0.0f, 0.0f).start(PhaseScreen.tweenManager);
					}
				}
			}).delay(0.5f).start(PhaseScreen.tweenManager);
	}

	public void showAddScore (int score, float x, float y) {
		Label scoreLabel = labelsPool.obtain();
		scoreLabel.setColor(Color.WHITE);
		scoreLabel.setText("+" + score);
		scoreLabel.setPosition(x, y);
		this.addActor(scoreLabel);
		Tween.to(scoreLabel, ActorTweenAccessor.SCORE, 1.5f).setUserData(scoreLabel).target(y + PhaseScreen.SQUARE_SIZE / 2, 0.0f)
			.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int type, BaseTween<?> source) {
					((Actor) source.getUserData()).remove();
					labelsPool.free((Label) source.getUserData());
				}
			}).start(PhaseScreen.tweenManager);
	}
}
