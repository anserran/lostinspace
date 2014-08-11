
package es.eucm.lostinspace.core.hud.help;

import aurelienribon.tweenengine.Tween;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

import es.eucm.lostinspace.core.LevelManager;
import es.eucm.lostinspace.core.screens.PhaseScreen;
import es.eucm.lostinspace.core.tweens.ActorTweenAccessor;

public class HelpButton extends Group {
	private LevelManager.Abilities ability;

	private static TextureRegion empty;

	private static Texture marker;

	private Sprite sprite;

	private int currentLevel;

	private HelpPanel helpPanel;

	public HelpButton (LevelManager.Abilities ability, HelpPanel help) {
		this.helpPanel = help;
		help.setVisible(false);
		this.ability = ability;
		if (empty == null) {
			Texture t = PhaseScreen.assetManager.get("wall.png");
			int size = t.getWidth() / PhaseScreen.assetManager.getColumnsTexture("wall.png");
			empty = new TextureRegion(t, size, size, size, size);
		}
		Texture t = PhaseScreen.assetManager.get("levelups.png");
		int size = t.getWidth() / PhaseScreen.assetManager.getColumnsTexture("levelups.png");
		sprite = new Sprite(t, size * ability.ordinal(), 0, size, size);
		currentLevel = PhaseScreen.levelManager.getCurrentLevel(ability);
		this.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				if (currentLevel >= 0) {
					helpPanel.toFront();
					helpPanel.setVisible(!helpPanel.isVisible());
				}
				return false;
			}
		});
	}

	@Override
	public void act (float delta) {
		super.act(delta);
		int newLevel = PhaseScreen.levelManager.getCurrentLevel(ability);
		if (newLevel != currentLevel) {
			this.toFront();
			if (currentLevel < newLevel) {
				updateLevel(newLevel);
			}
			currentLevel = newLevel;
		}
	}

	private void updateLevel (int newLevel) {
		Tween.to(this, ActorTweenAccessor.HELP, 2.0f).waypoint(1.2f, 720.0f).target(1.0f, 0.0f).start(PhaseScreen.tweenManager);
		helpPanel.updateLevel(newLevel);
	}

	@Override
	public void draw (Batch batch, float parentAlpha) {
		if (PhaseScreen.levelManager.getCurrentLevel(ability) == -1) {
			batch.setColor(Color.DARK_GRAY);
			batch.draw(empty, this.getX(), this.getY(), this.getWidth(), this.getHeight());
			batch.setColor(Color.WHITE);
		} else {
			sprite.setPosition(this.getX(), this.getY());
			sprite.setScale(this.getScaleX(), this.getScaleY());
			sprite.setRotation(this.getRotation());
			sprite.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
			sprite.draw(batch);
		}
		super.draw(batch, parentAlpha);
	}
}
