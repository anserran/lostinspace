package es.eucm.lostinspace.core.tweens;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ActorTweenAccessor implements TweenAccessor<Actor> {

	public static final int X = 0, Y = 1, ROTATION = 2, SCALE_X = 3, SCALE_Y = 4, ALPHA = 5, COLOR = 6, POSITION = 7,
			RED = 8, GREEN = 9, BLUE = 10, SCALE = 11, DESTRUCTION = 12, TELEPORT = 13, HELP = 14, SCORE = 15;

	@Override
	public int getValues(Actor target, int tweenType, float[] returnValues) {
		int count = 0;
		switch (tweenType) {
			case X:
				returnValues[0] = target.getX();
				count = 1;
				break;
			case Y:
				returnValues[0] = target.getY();
				count = 1;
				break;
			case ROTATION:
				returnValues[0] = target.getRotation();
				count = 1;
				break;
			case SCALE_X:
				returnValues[0] = target.getScaleX();
				count = 1;
				break;
			case SCALE_Y:
				returnValues[0] = target.getScaleY();
				count = 1;
				break;
			case ALPHA:
				returnValues[0] = target.getColor().a;
				count = 1;
				break;
			case COLOR:
				Color c = target.getColor();
				returnValues[0] = c.r;
				returnValues[1] = c.g;
				returnValues[2] = c.b;
				returnValues[3] = c.a;
				count = 4;
				break;
			case POSITION:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				count = 2;
				break;
			case RED:
				returnValues[0] = target.getColor().r;
				count = 1;
				break;
			case BLUE:
				returnValues[0] = target.getColor().b;
				count = 1;
				break;
			case GREEN:
				returnValues[0] = target.getColor().g;
				count = 1;
				break;
			case SCALE:
				returnValues[0] = target.getScaleX();
				returnValues[1] = target.getScaleY();
				count = 2;
				break;
			case DESTRUCTION:
				returnValues[0] = target.getScaleX();
				returnValues[1] = target.getScaleY();
				returnValues[2] = target.getColor().a;
				count = 3;
				break;
			case TELEPORT:
				returnValues[0] = target.getScaleX();
				returnValues[1] = target.getScaleY();
				returnValues[2] = target.getRotation();
				returnValues[3] = target.getX();
				returnValues[4] = target.getY();
				count = 5;
				break;
			case HELP:
				returnValues[0] = target.getScaleX();
				returnValues[1] = target.getRotation();
				count = 2;
				break;
			case SCORE:
				returnValues[0] = target.getY();
				returnValues[1] = target.getColor().a;
				count = 2;
				break;
		}
		return count;
	}

	@Override
	public void setValues(Actor target, int tweenType, float[] newValues) {
		Color c;
		switch (tweenType) {
			case X:
				target.setX(newValues[0]);
				break;
			case Y:
				target.setY(newValues[0]);
				break;
			case ROTATION:
				target.setRotation(newValues[0]);
				break;
			case SCALE_X:
				target.setScaleX(newValues[0]);
				break;
			case SCALE_Y:
				target.setScaleY(newValues[0]);
				break;
			case ALPHA:
				c = target.getColor();
				target.setColor(c.r, c.g, c.b, newValues[0]);
				break;
			case COLOR:
				target.setColor(newValues[0], newValues[1], newValues[2], newValues[3]);
				break;
			case POSITION:
				target.setPosition(newValues[0], newValues[1]);
				break;
			case RED:
				c = target.getColor();
				target.setColor(newValues[0], c.g, c.b, c.a);
				break;
			case BLUE:
				c = target.getColor();
				target.setColor(c.r, c.g, newValues[0], c.a);
				break;
			case GREEN:
				c = target.getColor();
				target.setColor(c.r, newValues[0], c.b, c.a);
				break;
			case SCALE:
				target.setScale(newValues[0], newValues[1]);
				break;
			case DESTRUCTION:
				target.setScaleX(newValues[0]);
				target.setScaleY(newValues[1]);
				target.getColor().a = newValues[2];
				break;
			case TELEPORT:
				target.setScaleX(newValues[0]);
				target.setScaleY(newValues[1]);
				target.setRotation(newValues[2]);
				target.setX(newValues[3]);
				target.setY(newValues[4]);
				break;
			case HELP:
				target.setScale(newValues[0], newValues[0]);
				target.setRotation(newValues[1]);
				break;
			case SCORE:
				target.setY(newValues[0]);
				c = target.getColor();
				target.setColor(c.r, c.g, c.b, newValues[1]);
				break;
		}
	}
}
