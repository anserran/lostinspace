package es.eucm.lostinspace.core.hud.help;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import es.eucm.lostinspace.core.AssetManager;
import es.eucm.lostinspace.core.LevelManager;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class HelpPanel extends Group {

	public static Label.LabelStyle text;

	public static Label.LabelStyle code;

	private Label level;

	private LevelHelpPanel[] levelPanels;

	private LevelManager.Abilities a;

	public HelpPanel(LevelManager.Abilities a) {
		if (text == null) {
			text = new Label.LabelStyle(PhaseScreen.assetManager.getFont(AssetManager.HELP_FONT), Color.BLACK);
			code = new Label.LabelStyle(PhaseScreen.assetManager.getFont(AssetManager.CONSOLE_FONT), Color.BLACK);
		}
		this.a = a;
		Table t = new Table();
		t.setTouchable(Touchable.enabled);
		t.center();
		t.setBackground(getBackground());
		t.setSize(PhaseScreen.STAGE_WIDTH, PhaseScreen.STAGE_HEIGHT);
		level = new Label(PhaseScreen.i18n("Level ") + "1/" + (a.getMaxLevel() + 1), text);
		t.add(level).left();
		t.row();
		levelPanels = new LevelHelpPanel[a.getMaxLevel() + 1];
		for (int i = 0; i <= a.getMaxLevel(); i++) {
			int width = PhaseScreen.STAGE_WIDTH / (a.getMaxLevel() + 1);
			levelPanels[i] = new LevelHelpPanel(a, i, width);
			t.add(levelPanels[i]).width(width).top();
		}
		this.addActor(t);
		t.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				setVisible(false);
				return false;
			}
		});
	}

	public void updateLevel(int newLevel) {
		level.setText(PhaseScreen.i18n("Level ") + ( newLevel + 1 ) +"/" + (a.getMaxLevel() + 1));
		for (int i = 0; i <= a.getMaxLevel(); i++) {
			levelPanels[i].setVisible(i <= newLevel);
		}
	}

	public Drawable getBackground(){
		Texture t = PhaseScreen.assetManager.get("wall.png");
		int size = t.getWidth() / PhaseScreen.assetManager.getColumnsTexture("wall.png");
		Sprite s = new Sprite(t, size, size, size, size);
		s.setColor(1.0f, 1.0f, 1.0f, .9f);
		return new SpriteDrawable(s);
	}
}
