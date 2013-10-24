package es.eucm.lostinspace.core.actors;

import com.badlogic.gdx.scenes.scene2d.Group;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class Map extends Group {

	public Map(){
		Wall wLeft = PhaseScreen.obtain(Wall.class);
		wLeft.setWallWidth(1);
		wLeft.setWallHeight(PhaseScreen.GRID_ROWS);
		wLeft.setX(-PhaseScreen.SQUARE_SIZE);
		this.addActor(wLeft);
		Wall wRight = PhaseScreen.obtain(Wall.class);
		wRight.setWallWidth(1);
		wRight.setWallHeight(PhaseScreen.GRID_ROWS);
		wRight.setX(PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_COLUMNS ));
		this.addActor(wRight);
		Wall wTop = PhaseScreen.obtain(Wall.class);
		wTop.setY(PhaseScreen.SQUARE_SIZE * (PhaseScreen.GRID_ROWS ));
		wTop.setWallWidth(PhaseScreen.GRID_COLUMNS);
		wTop.setWallHeight(1);
		this.addActor(wTop);
		Wall wBottom = PhaseScreen.obtain(Wall.class);
		wBottom.setWallWidth(PhaseScreen.GRID_COLUMNS);
		wBottom.setWallHeight(1);
		wBottom.setY(-PhaseScreen.SQUARE_SIZE);
		this.addActor(wBottom);
	}

}
