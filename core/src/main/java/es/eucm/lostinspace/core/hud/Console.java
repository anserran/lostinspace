package es.eucm.lostinspace.core.hud;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.lostinspace.core.LevelManager;
import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.actors.ImageActor;
import es.eucm.lostinspace.core.hud.help.HelpButton;
import es.eucm.lostinspace.core.hud.help.HelpPanel;
import es.eucm.lostinspace.core.hud.interpreter.Interpreter;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Console extends Group {

	private final Interpreter interpreter;

	private Array<String> history;

	private int currentIndex;

	private boolean enable;


	public Console() {
		enable = true;
		// XML history
		history = new Array<String>();

		interpreter = new Interpreter();
		interpreter.setWidth(PhaseScreen.SQUARE_SIZE * (PhaseScreen.CONSOLE_COLUMNS - 1));
		interpreter.setHeight(PhaseScreen.SQUARE_SIZE * 4);

		Texture hudTexture = PhaseScreen.assetManager.get("hud.png");
		int width = hudTexture.getWidth();

		int squareSize = width / PhaseScreen.assetManager.getColumnsTexture("hud.png");

		// Prev order button
		ImageActor prevActor = LostInSpace.pools.obtain(ImageActor.class);
		prevActor.setTextureRegion(new TextureRegion(hudTexture, 0, 0, squareSize, squareSize));
		prevActor.setSize(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE);
		prevActor.setY(PhaseScreen.SQUARE_SIZE * 3);
		prevActor.setX(PhaseScreen.SQUARE_SIZE * (PhaseScreen.CONSOLE_COLUMNS - 1));
		prevActor.setHoverEffect(true);
		prevActor.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				prevCommands();
				return false;
			}
		});
		// Prettify button
		ImageActor prettyActor = LostInSpace.pools.obtain(ImageActor.class);
		prettyActor.setTextureRegion(new TextureRegion(hudTexture, squareSize, 0, squareSize, squareSize));
		prettyActor.setSize(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE);
		prettyActor.setY(PhaseScreen.SQUARE_SIZE);
		prettyActor.setX(PhaseScreen.SQUARE_SIZE * (PhaseScreen.CONSOLE_COLUMNS - 1));
		prettyActor.setHoverEffect(true);
		prettyActor.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				prettify();
				return false;
			}
		});
		// Next order button
		ImageActor nextActor = LostInSpace.pools.obtain(ImageActor.class);
		nextActor.setTextureRegion(new TextureRegion(hudTexture, squareSize * 2, 0, squareSize, squareSize));
		nextActor.setSize(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE);
		nextActor.setY(PhaseScreen.SQUARE_SIZE * 2);
		nextActor.setHoverEffect(true);
		nextActor.setX(PhaseScreen.SQUARE_SIZE * (PhaseScreen.CONSOLE_COLUMNS - 1));
		nextActor.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				nextCommands();
				return false;
			}
		});
		// Send button
		ImageActor sendActor = LostInSpace.pools.obtain(ImageActor.class);
		sendActor.setTextureRegion(new TextureRegion(hudTexture, squareSize * 5, 0, squareSize, squareSize));
		sendActor.setSize(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE);
		sendActor.setHoverEffect(true);
		sendActor.setX(PhaseScreen.SQUARE_SIZE * (PhaseScreen.CONSOLE_COLUMNS - 1));
		sendActor.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				sendCommands();
				return false;
			}
		});

		this.addActor(interpreter);
		this.addActor(prevActor);
		this.addActor(prettyActor);
		this.addActor(nextActor);
		this.addActor(sendActor);
		addHelpButtons();
	}

	private void addHelpButtons() {
		int x = 0;
		for (LevelManager.Abilities a : LevelManager.Abilities.values()) {
			HelpPanel help = new HelpPanel(a);
			HelpButton button = new HelpButton(a, help);
			button.setPosition(x, PhaseScreen.SQUARE_SIZE * 4);
			x += PhaseScreen.SQUARE_SIZE;
			button.setSize(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE);
			addActor(button);
			addActor(help);
		}
	}

	public void sendCommands() {
		if (enable && !interpreter.isError()) {
			String xml = interpreter.getText();
			boolean ok = PhaseScreen.phaseManager.sendCommands(xml);
			if (ok) {
				interpreter.setText("");
				if (history.size > 0) {
					String lastXml = history.peek();
					if (!lastXml.equals(xml)) {
						history.add(xml);
					}
				} else {
					history.add(xml);
				}
				currentIndex = history.size;
			} else {
				interpreter.setError(true);
			}
		}
	}

	public void prevCommands() {
		if (enable) {
			currentIndex = Math.max(0, currentIndex - 1);
			if (history.size > currentIndex) {
				interpreter.setText(history.get(currentIndex));
				interpreter.setError(false);
			}
		}
	}

	public void nextCommands() {
		if (enable) {
			currentIndex = Math.max(0, Math.min(history.size - 1, currentIndex + 1));
			if (history.size > currentIndex) {
				interpreter.setText(history.get(currentIndex));
				interpreter.setError(false);
			}
		}
	}

	public void prettify() {
		if (enable) {
			String text = interpreter.getText();
			try {
				text = PhaseScreen.xmlPrettifier.getPrettyXml(text);
			} catch (Exception e) {
				PhaseScreen.msg(PhaseScreen.i18n("It's impossible to prettify an invalid XML."));
				interpreter.setError(true);
			}
			interpreter.setText(text);
		}
	}


	public void setEnable(boolean enable) {
		interpreter.setEnable(enable);
		this.enable = enable;
	}

	/**
	 * Cleras the commands history
	 */
	public void clearHistory() {
		history.clear();
	}

}
