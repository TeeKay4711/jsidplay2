package ui.common;

import java.util.ResourceBundle;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import libsidplay.Player;
import ui.entities.config.Configuration;
import ui.events.UIEventFactory;

public abstract class C64Tab extends Tab implements UIPart {

	private UIUtil util = new UIUtil();

	public C64Tab() {
		setContent((Node) util.parse(this));
	}

	protected ResourceBundle getBundle() {
		return util.getBundle();
	}

	protected UIEventFactory getUiEvents() {
		return util.getUiEvents();
	}

	public Player getPlayer() {
		return util.getPlayer();
	}

	public void setPlayer(Player player) {
		util.setPlayer(player);
	}

	public Configuration getConfig() {
		return util.getConfig();
	}

	public void setConfig(Configuration config) {
		util.setConfig(config);
	}

}
