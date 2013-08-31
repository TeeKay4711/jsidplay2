package ui.console;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import ui.common.C64VBox;

public class ConsoleOutput extends C64VBox implements Initializable {

	@FXML
	protected TextArea console;
	@FXML
	private TitledPane titledPane;

	@FXML
	private void clearConsole() {
		console.clear();
	}

	public PrintStream getPrintStream(final OutputStream original) {
		return new PrintStream(new OutputStream() {
			public synchronized void write(final byte[] b, final int off,
					final int len) throws IOException {
				final String str = new String(b, off, len);
				original.write(b, off, len);
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						append(str);
					}

				});
			}

			@Override
			public void write(int ch) throws IOException {
				append(String.valueOf((char) ch));
			}

			protected void append(String str) {
				console.setText(console.getText() + str);
			}
		});
	}

	public TitledPane getTitledPane() {
		return titledPane;
	}

	public TextArea getConsole() {
		return console;
	}

}