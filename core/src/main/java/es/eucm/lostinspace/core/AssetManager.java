
package es.eucm.lostinspace.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import es.eucm.lostinspace.core.screens.PhaseScreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssetManager extends com.badlogic.gdx.assets.AssetManager {

	public static final String COMM_FONT = "arial-15.fnt";
	public static final String NAME_FONT = "courier-14-bold.fnt";
	public static final String CONSOLE_FONT = "courier-14-bold.fnt";
	public static final String HELP_FONT = "arial-15.fnt";
	public static final String SCORE_FONT = "berlin-32.fnt";
	public static final String SPEAK_FONT = "arial-15.fnt";

	private static final String DEFAULT_LANGUAGE = "EN";
	private String imagesPath;
	private String fontPath;
	private Map<String, String> strings;
	private JsonReader jsonReader;
	private String lang;

	/** Map holding how many columns (images) has each texture */
	private final Map<String, Integer> textureColumns;

	public AssetManager () {
		jsonReader = new JsonReader();
		strings = new HashMap<String, String>();
		textureColumns = new HashMap<String, Integer>();
	}

	public void setImagesPath (String imagesPath) {
		this.imagesPath = imagesPath;
	}

	@Override
	public synchronized <T> T get (String fileName) {
		return super.get(imagesPath + fileName);
	}

	public synchronized BitmapFont getFont (String fileName) {
		return super.get(fontPath + fileName, BitmapFont.class);
	}

	public String getTextFile (String path) {
		FileHandle fh = Gdx.files.internal(path);

		if (fh != null && fh.exists()) {
			String text = "";
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(fh.reader());
				String line;
				while ((line = reader.readLine()) != null) {
					text += (line + "\n");
				}
			} catch (Exception e) {
				Gdx.app.log("AssetHandler", "Error reading text file " + path + ": " + e.getMessage());
			} finally {
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					Gdx.app.log("AssetHandler", "Error reading text file " + path + ": " + e.getMessage());
				}
			}
			return text;
		}
		return null;
	}

	public void loadLanguage (String lang) {
		strings.clear();
		if (!lang.equals(DEFAULT_LANGUAGE)) {
			JsonValue value = jsonReader.parse(Gdx.files.internal("strings/strings-" + lang + ".json"));
			JsonValue child = value.child();
			while (child != null) {
				strings.put(child.name(), child.asString());
				child = child.next();
			}
		}
	}

	public void loadAssets () {
		String[] images = new String[] {"grid.png", "placeholder.png", "bullet.png", "textbox.png", "ship.png", "wall.png",
			"warp.png", "hud.png", "comm.png", "rocks.png", "levelups.png", "trigger.png", "titlescreen.png", "endscreen.png",
			"helpbg.png", "balloon.png", "logo.png", "title.png", "xmledition.png"};
		int[] columns = new int[] {16, 1, 1, 1, 1, 3, 1, 10, 5, 2, 5, 2, 1, 1, 1, 3, 2, 14, 3};

		if (images.length != columns.length) {
			Gdx.app.log("AssetManager", "Some image is missing its columns");
		}
		if (PhaseScreen.debug) {
			for (int i = 0; i < images.length; i++) {
				Gdx.app.log("AssetManager", images[i] + ": " + (i < columns.length ? columns[i] : "undefined"));
			}
		}

		String[] fonts = new String[] {CONSOLE_FONT, COMM_FONT, NAME_FONT, HELP_FONT, SCORE_FONT};
		Gdx.app.log("Assets", "Loading assets");
		String imagePath = "data/";
		fontPath = "fonts/";
		setImagesPath(imagePath);
		for (String i : images) {
			load(imagePath + i, Texture.class);
		}
		// Add images columns
		for (int i = 0; i < columns.length; i++) {
			textureColumns.put(images[i], columns[i]);
		}

		for (String f : fonts) {
			load(fontPath + f, BitmapFont.class);
		}
		finishLoading();
		// loadLanguage(DEFAULT_LANGUAGE);
		loadLanguage("ES");
		Gdx.app.log("Assets", "Done.");
	}

	public String i18n (String text) {
		String translation = strings.get(text);
		if (translation == null && !DEFAULT_LANGUAGE.equals(lang)) {
			// Gdx.app.log("AssetManager", text + " has no translation in " + lang);
		}
		return translation == null ? text : translation;
	}

	public int getColumnsTexture (String texture) {
		return textureColumns.containsKey(texture) ? textureColumns.get(texture) : 1;
	}
}
