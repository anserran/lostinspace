
package es.eucm.lostinspace.core.hud.maphud;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.lostinspace.core.AssetManager;
import es.eucm.lostinspace.core.LevelManager;
import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.actors.ImageActor;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.lostinspace.core.tweens.ActorTweenAccessor;

public class PhaseResults extends Group {

	private Texture background;

	private boolean nextClicked;

	private Label[] abilitiesName;
	private Label[] abilitiesCount;
	private Label totalCount;
	private Label score;
	private Label totalActions;
	private Label totalScore;

	public PhaseResults () {
		Pixmap p = new Pixmap(10, 10, Pixmap.Format.RGBA8888);
		p.setColor(0.7f, 0.72f, 0.7f, 0.95f);
		p.fill();
		background = new Texture(p);
		p.dispose();

		// Add button
		nextClicked = false;
		ImageActor nextPhaseButton = LostInSpace.pools.obtain(ImageActor.class);
		nextPhaseButton.setTextureRegion("hud.png", 3, 0, 2, 1, true);
		nextPhaseButton.setBounds((PhaseScreen.GRID_COLUMNS - 4) * PhaseScreen.SQUARE_SIZE, 0, PhaseScreen.SQUARE_SIZE * 2,
			PhaseScreen.SQUARE_SIZE);
		nextPhaseButton.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (!nextClicked) {
					changeNextPhase();
				}
				nextClicked = true;
				return false;
			}
		});
		this.addLabels();
		this.addActor(nextPhaseButton);
	}

	private void addLabels () {
		Label.LabelStyle textStyle = new Label.LabelStyle(PhaseScreen.assetManager.getFont(AssetManager.SCORE_FONT), Color.BLACK);
		Label.LabelStyle mutliplierStyle = new Label.LabelStyle(PhaseScreen.assetManager.getFont(AssetManager.SCORE_FONT),
			Color.GRAY);
		Label.LabelStyle actionStyle = new Label.LabelStyle(PhaseScreen.assetManager.getFont(AssetManager.CONSOLE_FONT),
			Color.BLACK);
		Label wellDone = new Label(PhaseScreen.i18n("Well done!"), textStyle);
		float gridY = 4;
		wellDone.setAlignment(Align.left);
		wellDone.setPosition(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - gridY));
		this.addActor(wellDone);

		gridY++;

		Label scoreLabel = new Label(PhaseScreen.i18n("Score"), textStyle);
		scoreLabel.setAlignment(Align.left);
		scoreLabel.setPosition(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - gridY));
		this.addActor(scoreLabel);
		score = new Label(PhaseScreen.i18n("100"), mutliplierStyle);
		score.setAlignment(Align.left);
		score.setPosition(PhaseScreen.SQUARE_SIZE * 3, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - gridY));
		this.addActor(score);

		gridY++;

		Label totalActionsLabel = new Label(PhaseScreen.i18n("Actions"), textStyle);
		totalActionsLabel.setAlignment(Align.left);
		totalActionsLabel.setPosition(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - gridY));
		this.addActor(totalActionsLabel);
		totalActions = new Label(PhaseScreen.i18n("-50 x 100"), mutliplierStyle);
		totalActions.setAlignment(Align.left);
		totalActions.setPosition(PhaseScreen.SQUARE_SIZE * 3, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - gridY));
		this.addActor(totalActions);

		gridY++;

		Label totalScoreLabel = new Label(PhaseScreen.i18n("Total"), textStyle);
		totalScoreLabel.setAlignment(Align.left);
		totalScoreLabel.setPosition(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - gridY));
		this.addActor(totalScoreLabel);
		totalScore = new Label(PhaseScreen.i18n("2890456"), mutliplierStyle);
		totalScore.setAlignment(Align.left);
		totalScore.setPosition(PhaseScreen.SQUARE_SIZE * 3, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - gridY));
		this.addActor(totalScore);

		Label actionsLabel = new Label(PhaseScreen.i18n("Commands"), textStyle);
		actionsLabel.setAlignment(Align.left);
		actionsLabel.setPosition(PhaseScreen.SQUARE_SIZE * 5, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - 3));
		this.addActor(actionsLabel);

		gridY = 3.5f;

		int i = 0;
		abilitiesName = new Label[LevelManager.Abilities.values().length];
		abilitiesCount = new Label[LevelManager.Abilities.values().length];
		for (LevelManager.Abilities a : LevelManager.Abilities.values()) {
			Label ability = new Label(a.toString(), actionStyle);
			ability.setAlignment(Align.left);
			ability.setPosition(PhaseScreen.SQUARE_SIZE * 5.5f, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - gridY));
			this.addActor(ability);
			abilitiesName[i] = ability;
			Label count = new Label("x 10", mutliplierStyle);
			count.setAlignment(Align.left);
			count.setPosition(PhaseScreen.SQUARE_SIZE * 6.5f, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - gridY - 0.1f));
			this.addActor(count);
			gridY += 0.75f;
			abilitiesCount[i++] = count;
		}
		gridY += 0.25f;
		totalCount = new Label(PhaseScreen.i18n("= 100"), mutliplierStyle);
		totalCount.setAlignment(Align.left);
		totalCount.setPosition(PhaseScreen.SQUARE_SIZE * 6, PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - gridY));
		this.addActor(totalCount);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		batch.draw(background, this.getX(), this.getY(), PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_COLUMNS - 2),
			PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS - 2));
		super.draw(batch, parentAlpha);
	}

	public void showResults () {
		nextClicked = false;
		Tween.to(this, ActorTweenAccessor.Y, 0.5f).target(PhaseScreen.SQUARE_SIZE).start(PhaseScreen.tweenManager);
		int[] instructions = PhaseScreen.levelManager.getInstructions();
		int total = 0;
		for (int i = 0; i < instructions.length; i++) {
			int count = instructions[i];
			abilitiesName[i].setVisible(count > 0);
			abilitiesCount[i].setVisible(count > 0);
			abilitiesCount[i].setText("x " + count);
			total += count;
		}
		totalCount.setText("= " + total + PhaseScreen.i18n(" actions"));
		score.setText(PhaseScreen.levelManager.getScore() + "");
		totalScore.setText(PhaseScreen.levelManager.getTotalScore() - total * LevelManager.COMMAND_POINTS + "");
		totalActions.setText("-( " + total + " x " + LevelManager.COMMAND_POINTS + " )");
	}

	private void changeNextPhase () {
		Tween.to(this, ActorTweenAccessor.Y, 0.5f).target(PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_COLUMNS + 1))
			.start(PhaseScreen.tweenManager);
		PhaseScreen.phaseManager.nextPhase();
	}
}
