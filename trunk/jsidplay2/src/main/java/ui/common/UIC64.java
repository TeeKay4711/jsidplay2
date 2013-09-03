package ui.common;

import libsidplay.Player;
import sidplay.ConsolePlayer;
import ui.entities.config.Configuration;

public class UIC64 {

	private Configuration config;
	private Player player;
	private ConsolePlayer consolePlayer;

	protected Configuration getConfig() {
		return config;
	}

	protected void setConfig(Configuration config) {
		this.config = config;
	}
	
	protected Player getPlayer() {
		return player;
	}

	protected void setPlayer(Player player) {
		this.player = player;
	}

	protected ConsolePlayer getConsolePlayer() {
		return consolePlayer;
	}

	protected void setConsolePlayer(ConsolePlayer consolePlayer) {
		this.consolePlayer = consolePlayer;
	}

}
