package es.eucm.lostinspace.core.hud.communicator;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.actors.ImageActor;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Communicator extends Group {

	private Portrait p;

	private Message m;

	private Texture texture;

	private Array<String> textHistory;

	private Array<String> typeHistory;

	private int historyPointer;

	private boolean newMessages;

	public Communicator() {
		p = new Portrait();
		p.setBounds(0, 0, PhaseScreen.SQUARE_SIZE * 2, PhaseScreen.SQUARE_SIZE * 2);
		//this.addActor(p);

		m = new Message();
		m.setBounds(0, PhaseScreen.SQUARE_SIZE * 2, PhaseScreen.SQUARE_SIZE * 3, PhaseScreen.SQUARE_SIZE * 2);
		this.addActor(m);

		// Load background
		texture = PhaseScreen.assetManager.get("comm.png");

		// Buttons
		Texture hudTexture = PhaseScreen.assetManager.get("hud.png");
		int width = hudTexture.getWidth();

		int squareSize = width / PhaseScreen.assetManager.getColumnsTexture("hud.png");
		// Prev order button
		ImageActor prevActor = LostInSpace.pools.obtain(ImageActor.class);
		prevActor.setTextureRegion(new TextureRegion(hudTexture, 0, 0, squareSize, squareSize));
		prevActor.setSize(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE);
		prevActor.setY(-PhaseScreen.SQUARE_SIZE);
		prevActor.setX(PhaseScreen.SQUARE_SIZE * (PhaseScreen.CONSOLE_COLUMNS - 2));
		prevActor.setHoverEffect(true);
		prevActor.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				prevMessage();
				return false;
			}
		});
		// Next order button
		ImageActor nextActor = LostInSpace.pools.obtain(ImageActor.class);
		nextActor.setTextureRegion(new TextureRegion(hudTexture, squareSize * 2, 0, squareSize, squareSize));
		nextActor.setSize(PhaseScreen.SQUARE_SIZE, PhaseScreen.SQUARE_SIZE);
		nextActor.setY(- PhaseScreen.SQUARE_SIZE);
		nextActor.setHoverEffect(true);
		nextActor.setX(PhaseScreen.SQUARE_SIZE * (PhaseScreen.CONSOLE_COLUMNS - 1));
		nextActor.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				nextMessage();
				return false;
			}
		});
		this.addActor(nextActor);
		this.addActor(prevActor);

		textHistory = new Array<String>();
		typeHistory = new Array<String>();
		historyPointer = -1;
	}

	public void addMessage(String type, String text) {
		type = type == null ? "" : type;
		typeHistory.add(type);
		textHistory.add(text);
		if (!newMessages) {
			historyPointer = textHistory.size - 1;
		}
		newMessages = true;
	}

	public void nextMessage() {
		historyPointer = Math.min(historyPointer + 1, textHistory.size - 1);
		updateMessage();
	}

	public void prevMessage() {
		historyPointer = Math.max(historyPointer - 1, 0);
		updateMessage();
	}

	public void updateMessage() {
		if (historyPointer >= 0 && historyPointer < textHistory.size) {
			p.setType(typeHistory.get(historyPointer));
			m.setText(typeHistory.get(historyPointer), textHistory.get(historyPointer));
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(texture, this.getX(), this.getY(), PhaseScreen.SQUARE_SIZE * 5, PhaseScreen.SQUARE_SIZE * 2);
		super.draw(batch, parentAlpha);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (newMessages) {
			updateMessage();
			newMessages = false;
		}
	}
}
