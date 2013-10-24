
package es.eucm.lostinspace.core.hud.communicator;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import es.eucm.lostinspace.core.AssetManager;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Message extends Group {

	private Label text;

	private Label name;

	public Message () {
		BitmapFont font = PhaseScreen.assetManager.getFont(AssetManager.COMM_FONT);

		text = new Label("", new Label.LabelStyle(font, Color.WHITE));
		text.setAlignment(Align.left, Align.left);
		this.addActor(text);
		text.setWrap(true);
		text.setWidth(PhaseScreen.SQUARE_SIZE * 4.9f);
		text.setY(-(PhaseScreen.SQUARE_SIZE / 2 + font.getLineHeight() * 2.5f));
		text.setX(PhaseScreen.SQUARE_SIZE * 0.1f);

		name = new Label("", new Label.LabelStyle(font, Color.BLACK));
		name.setAlignment(Align.left, Align.left);
		this.addActor(name);
		name.setWrap(true);
		name.setWidth(PhaseScreen.SQUARE_SIZE * 4.9f);
		name.setY(-font.getLineHeight());
		name.setX(PhaseScreen.SQUARE_SIZE * 0.1f);
	}

	public void setText (String character, String text) {
		this.name.setText(character == null ? "CONSOLE" : character.toUpperCase());
		this.text.setText(text);
	}
}
