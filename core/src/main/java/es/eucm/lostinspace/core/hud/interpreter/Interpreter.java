
package es.eucm.lostinspace.core.hud.interpreter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

import es.eucm.lostinspace.core.AssetManager;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Interpreter extends TextArea {

	private boolean error;

	public Interpreter () {
		super("", new InterpreterStyle());
		PhaseScreen.stage.setKeyboardFocus(this);
		this.addListener(new InputListener() {
			@Override
			public boolean keyDown (InputEvent event, int keycode) {
				setError(false);
				return false;
			}
		});
	}

	public void setEnable (boolean enable) {
		super.setEnable(enable);
		this.setColor(enable ? Color.WHITE : Color.LIGHT_GRAY);
	}

	public void setError (boolean error) {
		this.error = error;
		this.getStyle().fontColor = (error ? Color.RED : Color.BLACK);
	}

	public boolean isError () {
		return error;
	}

	public static class InterpreterStyle extends TextField.TextFieldStyle {

		public InterpreterStyle () {
			font = PhaseScreen.assetManager.getFont(AssetManager.CONSOLE_FONT);
			fontColor = Color.BLACK;
			background = new NinePatchDrawable(new NinePatch((Texture)PhaseScreen.assetManager.get("textbox.png"), 20, 20, 20, 20));

			background.setLeftWidth(10);
			background.setRightWidth(20);
			background.setTopHeight(5);
			background.setBottomHeight(font.getLineHeight());

			Pixmap p = new Pixmap(2, (int)font.getLineHeight(), Pixmap.Format.RGB888);
			p.setColor(Color.LIGHT_GRAY);
			p.fill();
			this.selection = new SpriteDrawable(new Sprite(new Texture(p)));

			p.setColor(Color.BLACK);
			p.fill();
			this.cursor = new SpriteDrawable(new Sprite(new Texture(p)));
			p.dispose();
		}
	}
}
