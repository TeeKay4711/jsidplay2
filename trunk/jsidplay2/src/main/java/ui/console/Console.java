package ui.console;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import ui.common.C64Tab;
import ui.events.UIEvent;

public class Console extends C64Tab implements Initializable {
	private static final String STYLE_ERROR_CONSOLE = "errorConsole";
	private static final String STYLE_OUTPUT_CONSOLE = "outputConsole";

	@FXML
	private ConsoleOutput out;
	@FXML
	private ConsoleOutput err;

	@Override
	public String getBundleName() {
		return getClass().getName();
	}

	@Override
	public URL getFxml() {
		return getClass().getResource(getClass().getSimpleName() + ".fxml");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (getPlayer() == null) {
			// wait for second initialization, where properties have been set!
			return;
		}
		out.initialize(location, resources);
		out.getTitledPane().setText(getBundle().getString("OUT"));
		out.getConsole().getStyleClass().add(STYLE_OUTPUT_CONSOLE);
		err.initialize(location, resources);
		err.getTitledPane().setText(getBundle().getString("ERR"));
		err.getConsole().getStyleClass().add(STYLE_ERROR_CONSOLE);
		System.setOut(out.getPrintStream(System.out));
		System.setErr(err.getPrintStream(System.err));
	}

	public void notify(UIEvent evt) {
	}

}