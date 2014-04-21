package ui.oscilloscope;

import sidplay.ConsolePlayer;
import ui.common.C64Window;
import ui.entities.config.Configuration;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TitledPane;
import libsidplay.Player;
import libsidplay.common.SIDEmu;

public final class VolumeGauge extends SIDGauge {

	@FXML
	private TitledPane border;
	@FXML
	private Canvas area;

	public VolumeGauge(C64Window window, ConsolePlayer consolePlayer,
			Player player, Configuration config) {
		super(window, consolePlayer, player, config);
	}

	@Override
	protected Canvas getArea() {
		return area;
	}

	@Override
	protected TitledPane getTitledPane() {
		return border;
	}

	@Override
	public void sample(SIDEmu sidemu) {
		int sample = sidemu.readInternalRegister(0x18) & 0xf;
		accumulate(sample / 15f);
	}
}