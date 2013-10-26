
package es.eucm.lostinspace.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;

import es.eucm.ead.tools.xml.XMLNode;
import es.eucm.lostinspace.core.LostInSpace;
import es.eucm.lostinspace.core.actors.AbstractActor;
import es.eucm.lostinspace.core.actors.ImageActor;
import es.eucm.lostinspace.core.parsers.PhaseCreator;

public class TitleScreen implements Screen {

	private LostInSpace game;

	private Stage stage;

	private PhaseCreator phaseCreator;

	public TitleScreen (LostInSpace game) {
		phaseCreator = LostInSpace.phaseCreator;
		this.game = game;
	}

	@Override
	public void render (float delta) {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void show () {
		stage = new Stage(PhaseScreen.STAGE_WIDTH, PhaseScreen.STAGE_HEIGHT, true);
		Gdx.input.setInputProcessor(stage);
		String text = PhaseScreen.assetManager.getTextFile("phases/title.xml");
		XMLNode n = PhaseScreen.xmlParser.parse(text);
		phaseCreator.createPhase(n, stage.getRoot());
		// Start button
		ImageActor startButton = (ImageActor)stage.getRoot().findActor("start");
		startButton.setHoverEffect(true);
		startButton.addListener(new InputListener() {
			@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				game.startGame();
				return false;
			}
		});
		// Start button
		ImageActor continueButton = (ImageActor)stage.getRoot().findActor("continue");
		continueButton.setHoverEffect(true);
		continueButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				game.continueGame();
				return false;
			}
		});
	}

	@Override
	public void hide () {
		for (Actor a : stage.getRoot().getChildren()) {
			if (a instanceof AbstractActor) {
				((AbstractActor)a).dispose();
			}
		}
		stage.dispose();
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	}
}
