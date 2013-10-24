package es.eucm.lostinspace.core.hud.interpreter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.Timer;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class TextArea extends TextField {

	private static final String TAB_STRING = "     ";

	private IntArray lineDivisions;
	private int firstLineShowing = 0;
	protected int cursorLine = 0;

	private int linesShowing = 0;

	private boolean enable = true;

	public TextArea(String text, Skin skin) {
		super(text, skin);
	}

	public TextArea(String text, Skin skin, String styleName) {
		super(text, skin, styleName);
	}

	public TextArea(String text, TextFieldStyle style) {
		super(text, style);
	}

	@Override
	protected void blink() {
		if ( enable ){
			super.blink();
		}
		else {
			cursorOn = false;
		}
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
		if (!enable){
			this.setText("");
		}
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		TextFieldStyle style = this.getStyle();
		final BitmapFont font = style.font;
		final Color fontColor = style.fontColor;
		final Drawable selection = style.selection;
		final Drawable cursorPatch = style.cursor;
		final Drawable background = style.background;

		Color color = getColor();
		float x = getX();
		float y = getY();
		float width = getWidth();
		float height = getHeight();

		float textY = height + font.getDescent();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		float bgLeftWidth = 0;
		if (background != null) {
			background.draw(batch, x, y, width, height);
			bgLeftWidth = background.getLeftWidth();
			textY -= background.getTopHeight();
		}

		calculateOffsets();

		linesShowing = (int) Math.floor((textY - background.getBottomHeight())
				/ font.getLineHeight());

		Stage stage = getStage();
		boolean focused = stage != null && stage.getKeyboardFocus() == this;
		if (focused && hasSelection && selection != null) {
			int i = firstLineShowing;
			int marginHeight = 0;
			int minIndex = Math.min(cursor, selectionStart);
			int maxIndex = Math.max(cursor, selectionStart);
			while (i < lineDivisions.size - 1
					&& marginHeight + font.getLineHeight() < getHeight()) {

				int lineStart = lineDivisions.get(i);
				int lineEnd = lineDivisions.get(i + 1);

				if (!((minIndex < lineStart && minIndex < lineEnd
						&& maxIndex < lineStart && maxIndex < lineEnd) || (minIndex > lineStart
						&& minIndex > lineEnd && maxIndex > lineStart && maxIndex > lineEnd))) {

					int start = Math.max(lineDivisions.get(i), minIndex);
					int end = Math.min(lineDivisions.get(i + 1), maxIndex);

					float selectionX = glyphPositions.get(start)
							- glyphPositions.get(lineDivisions.get(i));
					float selectionWidth = glyphPositions.get(end)
							- glyphPositions.get(start);

					selection.draw(batch, x + selectionX + bgLeftWidth, y
							+ textY - textBounds.height - font.getDescent()
							- marginHeight, selectionWidth,
							font.getLineHeight());
				}

				marginHeight += font.getLineHeight();
				i++;
			}
		}

		if (displayText.length() == 0) {
			if (!focused && messageText != null) {
				if (style.messageFontColor != null) {
					font.setColor(style.messageFontColor.r,
							style.messageFontColor.g, style.messageFontColor.b,
							style.messageFontColor.a * parentAlpha);
				} else
					font.setColor(0.7f, 0.7f, 0.7f, parentAlpha);
				font.draw(batch, messageText, x + bgLeftWidth, y + textY);
			}
		} else {

			font.setColor(fontColor.r, fontColor.g, fontColor.b, fontColor.a
					* parentAlpha);
			int i = firstLineShowing;
			int marginHeight = 0;
			while (i < lineDivisions.size - 1
					&& marginHeight + font.getLineHeight() < getHeight()) {
				font.draw(batch, displayText, x + bgLeftWidth, y + textY
						- marginHeight, lineDivisions.get(i),
						lineDivisions.get(i + 1));
				marginHeight += font.getLineHeight();
				i++;
			}
		}
		if (focused) {
			blink();
			if (cursorLine >= firstLineShowing
					&& cursorLine <= linesShowing + firstLineShowing) {
				float cursorX = glyphPositions.get(cursor)
						- (cursorLine > 0 ? glyphPositions.get(lineDivisions
						.get(cursorLine)) : 0);

				if (cursorOn && cursorPatch != null) {
					cursorPatch.draw(
							batch,
							x + bgLeftWidth + cursorX,
							y + textY - textBounds.height - font.getDescent()
									- (cursorLine - firstLineShowing)
									* font.getLineHeight(),
							cursorPatch.getMinWidth(), font.getLineHeight());
				}
			}
		}
	}

	public void setText(String text) {
		if (text == null) throw new IllegalArgumentException("text cannot be null.");

		BitmapFont font = style.font;

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (font.containsCharacter(c) || c == ENTER_ANDROID) buffer.append(c);
		}

		this.text = buffer.toString();
		updateDisplayText();
		cursor = 0;
		clearSelection();

		textBounds.set(font.getBounds(displayText));
		textBounds.height -= font.getDescent() * 2;
		font.computeGlyphAdvancesAndPositions(displayText, glyphAdvances, glyphPositions);
		updateDisplayText();
	}

	protected void updateDisplayText() {
		super.updateDisplayText();
		for (int i = 0; i < displayText.length(); i++) {
			if (displayText.charAt(i) == ENTER_ANDROID
					|| displayText.charAt(i) == ENTER_DESKTOP) {
				glyphAdvances.insert(i, 0);
				glyphPositions.insert(i + 1, glyphPositions.get(i));
			}
		}
		if (lineDivisions == null) {
			lineDivisions = new IntArray();
		}
		lineDivisions.clear();
		lineDivisions.add(0);
		float width = getWidth() - style.background.getRightWidth();
		int lastSpace = -1;
		float lastWidth = 0;
		if (width > 0.0f) {
			int i = 0;
			float x = 0;
			while (i < glyphPositions.size - 1) {
				while (x < width && i < glyphPositions.size - 1) {
					if (displayText.charAt(i) == ' ') {
						lastSpace = i;
						lastWidth = x;
						x += glyphAdvances.items[i];
					} else if (displayText.charAt(i) == ENTER_ANDROID
							|| displayText.charAt(i) == ENTER_DESKTOP) {
						lineDivisions.add(i + 1);
						lastSpace = -1;
						x = 0;
					} else {
						x += glyphAdvances.items[i];
					}
					i++;
				}

				if (x < width) {
					// last line
				} else if (lastSpace == -1) {
					x = 0;
					i--;
					lineDivisions.add(i);
				} else {
					x = width - lastWidth;
					lineDivisions.add(lastSpace + 1);
				}
				lastSpace = -1;
				lastWidth = 0.0f;
			}
		}
		lineDivisions.add(glyphPositions.size - 1);

		updateCursorLine();
	}

	public void updateCursorLine() {
		cursorLine = 0;
		while (cursorLine < lineDivisions.size - 2
				&& lineDivisions.get(cursorLine + 1) <= cursor) {
			cursorLine++;
		}
		showCursor();
	}

	protected void setCursorPosition(float x, float y) {
		lastBlink = 0;
		cursorOn = false;

		if (lineDivisions.size == 2 && lineDivisions.get(0) == 0
				&& lineDivisions.get(1) == 0) {
			cursorLine = 0;
			cursor = 0;
			return;
		}

		int height = (int) (getHeight() - (style.background == null ? 0
				: style.background.getTopHeight()));
		cursorLine = (int) Math
				.floor((height - y) / style.font.getLineHeight())
				+ this.firstLineShowing;
		cursorLine = Math.max(0,
				Math.min(cursorLine, Math.max(lineDivisions.size - 2, 0)));

		x -= this.style.background != null ? style.background.getLeftWidth()
				: 0;
		int start = lineDivisions.size < 3 ? 0 : lineDivisions.get(cursorLine);
		int end = lineDivisions.size < 3 ? glyphPositions.size - 1
				: lineDivisions.get(cursorLine + 1);
		int i = start;
		boolean found = false;
		while (i <= end && !found) {
			if (glyphPositions.items[i] - glyphPositions.items[start] > x) {
				found = true;
			} else {
				i++;
			}
		}
		cursor = Math.max(0, i - 1);
		showCursor();
	}

	protected void initialize() {
		addListener(inputListener = new TextAreaBasisListener(keyRepeatTask));
	}

	public void moveCursorLine(int line) {
		if (line != cursorLine && line < getLines() && line >= 0) {
			float cursorX = glyphPositions.get(cursor)
					- glyphPositions.get(lineDivisions.get(cursorLine));
			cursorLine = line;
			cursor = lineDivisions.get(cursorLine);
			while (cursor < glyphPositions.size
					&& cursor < lineDivisions.get(cursorLine + 1) - 1
					&& glyphPositions.get(cursor)
					- glyphPositions.get(lineDivisions.get(cursorLine)) <= cursorX) {
				cursor++;
			}

			if (cursor < lineDivisions.get(cursorLine + 1) - 1) {
				cursor--;
			}
			updateCursorLine();
		}
	}

	private void showCursor() {
		if (cursorLine != firstLineShowing) {
			int step = cursorLine >= firstLineShowing ? 1 : -1;
			while (firstLineShowing > cursorLine
					|| firstLineShowing + linesShowing - 1 < cursorLine) {
				firstLineShowing += step;
			}
		}
	}

	public class TextAreaBasisListener extends TextFieldBasisListener {

		private KeyRepeatTask keyRepeatTask;

		public TextAreaBasisListener(KeyRepeatTask keyRepeatTask) {
			this.keyRepeatTask = keyRepeatTask;
		}

		@Override
		public boolean keyDown(InputEvent event, int keycode) {
			if (enable) {
				super.keyDown(event, keycode);
				boolean repeat = false;
				boolean ctrl = Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)
						|| Gdx.input.isKeyPressed(Keys.CONTROL_RIGHT);
				boolean shift = Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)
						|| Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT);

				if (keycode == Keys.HOME) {
					if (ctrl) {
						cursor = 0;
					} else {
						cursor = lineDivisions.get(cursorLine);
					}

				}
				if (keycode == Keys.END) {
					if (ctrl || cursorLine + 1 >= lineDivisions.size - 1) {
						cursor = text.length();
					} else {
						cursor = lineDivisions.get(cursorLine + 1) - 1;
					}

				}

				if (keycode == Keys.DOWN) {
					if (shift) {
						if (!hasSelection) {
							selectionStart = cursor;
							hasSelection = true;
						}
					} else {
						clearSelection();
					}
					moveCursorLine(ctrl ? getLines() - 1 : cursorLine + 1);
					repeat = true;

				}

				if (keycode == Keys.UP) {
					if (shift) {
						if (!hasSelection) {
							selectionStart = cursor;
							hasSelection = true;
						}
					} else {
						clearSelection();
					}
					moveCursorLine(ctrl ? 0 : cursorLine - 1);
					repeat = true;

				}

				if (keycode == Keys.ENTER) {
					addCharSequence("\n");
				}

				if (repeat
						&& (!keyRepeatTask.isScheduled() || keyRepeatTask.keycode != keycode)) {
					keyRepeatTask.keycode = keycode;
					keyRepeatTask.cancel();
					Timer.schedule(keyRepeatTask, keyRepeatInitialTime,
							keyRepeatTime);
				}

				updateCursorLine();
			}

			return true;
		}

		public boolean keyTyped(InputEvent event, char character) {
			if (enable) {
				final BitmapFont font = style.font;

				Stage stage = getStage();
				if (stage != null && stage.getKeyboardFocus() == TextArea.this) {
					if (character == BACKSPACE && (cursor > 0 || hasSelection)) {
						if (!hasSelection) {
							text = text.substring(0, cursor - 1)
									+ text.substring(cursor);
							updateDisplayText();
							cursor--;
							renderOffset = 0;
						} else {
							delete();
						}
					}
					if (character == DELETE) {
						if (cursor < text.length() || hasSelection) {
							if (!hasSelection) {
								text = text.substring(0, cursor)
										+ text.substring(cursor + 1);
								updateDisplayText();
							} else {
								delete();
							}
						}
						return true;
					}
					if (character != ENTER_DESKTOP && character != ENTER_ANDROID) {
						if (filter != null
								&& !filter.acceptChar(TextArea.this, character))
							return true;
					}

					if (font.containsCharacter(character) || character == TAB) {
						CharSequence c = character + "";
						if (character == TAB) {
							c = TAB_STRING;
						}
						addCharSequence(c);
					}
					if (listener != null)
						listener.keyTyped(TextArea.this, character);
					return true;
				} else
					return false;
			}
			return false;
		}

		private void addCharSequence(CharSequence c) {
			if (!hasSelection) {
				text = text.substring(0, cursor) + c
						+ text.substring(cursor, text.length());
				cursor += c.length();

				updateDisplayText();
			} else {
				int minIndex = Math.min(cursor, selectionStart);
				int maxIndex = Math.max(cursor, selectionStart);

				text = (minIndex > 0 ? text.substring(0, minIndex) : "")
						+ (maxIndex < text.length() ? text.substring(maxIndex,
						text.length()) : "");
				cursor = minIndex;
				text = text.substring(0, cursor) + c
						+ text.substring(cursor, text.length());
				updateDisplayText();
				cursor += c.length();
				clearSelection();
			}
			showCursor();
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
								 int pointer, int button) {
			PhaseScreen.stage.setScrollFocus(TextArea.this);
			return super.touchDown(event, x, y, pointer, button);
		}

		@Override
		public boolean scrolled(InputEvent event, float x, float y, int amount) {
			firstLineShowing += amount;
			firstLineShowing = Math.max(0,
					Math.min(firstLineShowing, getLines() - linesShowing));
			return true;
		}

		@Override
		public boolean handle(Event e) {
			if (e instanceof InputEvent) {
				if (((InputEvent) e).getType() == InputEvent.Type.scrolled) {
					return super.handle(e);
				}
			}
			return super.handle(e);
		}

	}

	public int getLines() {
		return lineDivisions.size - 1;
	}

	public void setSelection(int selectionStart, int selectionEnd) {
		super.setSelection(selectionStart, selectionEnd);
		updateCursorLine();
	}

	protected void paste() {
		String content = clipboard.getContents();
		if (content != null) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < content.length(); i++) {
				char c = content.charAt(i);
				if (style.font.containsCharacter(c) || c == ENTER_DESKTOP
						|| c == ENTER_ANDROID)
					builder.append(c);
			}
			content = builder.toString();

			if (!hasSelection) {
				text = text.substring(0, cursor) + content
						+ text.substring(cursor, text.length());
				updateDisplayText();
				cursor += content.length();
			} else {
				int minIndex = Math.min(cursor, selectionStart);
				int maxIndex = Math.max(cursor, selectionStart);

				text = (minIndex > 0 ? text.substring(0, minIndex) : "")
						+ (maxIndex < text.length() ? text.substring(maxIndex,
						text.length()) : "");
				cursor = minIndex;
				text = text.substring(0, cursor) + content
						+ text.substring(cursor, text.length());
				updateDisplayText();
				cursor = minIndex + content.length();
				clearSelection();
			}

		}
	}

}

