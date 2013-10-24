
package es.eucm.lostinspace.core.hud.help;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import es.eucm.lostinspace.core.LevelManager;
import es.eucm.lostinspace.core.screens.PhaseScreen;

public class LevelHelpPanel extends Table {

	private final Label example;
	private final Label summary;
	private final Label dtd;
	private final Label title;

	private int level;

	public LevelHelpPanel (LevelManager.Abilities a, int level, int width) {
		this.level = level;
		String[] explanations = a.getExplanations(level);
		title = new Label(PhaseScreen.i18n("Level ") + (level + 1) + PhaseScreen.i18n(" blocked"), HelpPanel.text);
		this.add(title).center().width(width);
		row();
		dtd = new Label(explanations[2], HelpPanel.code);
		dtd.setWrap(true);
		dtd.setVisible(false);
		this.add(dtd).center().width(width);
		row();
		summary = new Label("\n" + explanations[0], HelpPanel.text);
		summary.setWrap(true);
		summary.setVisible(false);
		this.add(summary).center().width(width);
		row();
		example = new Label("\n" + explanations[1], HelpPanel.code);
		example.setWrap(true);
		example.setVisible(false);
		this.add(example).center().width(width);
	}

	public void setVisible (boolean visible) {
		title.setText(PhaseScreen.i18n("Level ") + (level + 1) + (!visible ? PhaseScreen.i18n(" blocked") : ""));
		dtd.setVisible(visible);
		summary.setVisible(visible);
		example.setVisible(visible);
	}

}
