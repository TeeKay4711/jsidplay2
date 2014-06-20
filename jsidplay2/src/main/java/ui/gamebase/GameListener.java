package ui.gamebase;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.function.BiPredicate;

import javafx.scene.Node;
import libsidplay.Player;
import libsidplay.sidtune.SidTuneError;
import ui.common.UIUtil;
import ui.download.ProgressListener;

public class GameListener extends ProgressListener {

	private String fileToRun;
	private final BiPredicate<File, File> FILE_TO_RUN_DETECTOR = (file,
			toAttach) -> fileToRun.length() == 0
			|| fileToRun.equals(file.getName());

	public GameListener(UIUtil util, Node node, Player player) {
		super(util, node);
	}

	@Override
	public void downloaded(File downloadedFile) {
		if (downloadedFile == null) {
			// download failed!
			return;
		}
		try {
			util.getPlayer().autostartURL(downloadedFile.toURI().toURL(),
					FILE_TO_RUN_DETECTOR);
		} catch (IOException | SidTuneError | URISyntaxException e) {
			System.err.println(e.getMessage());
		}
	}

	public void setFileToRun(String valueOf) {
		fileToRun = valueOf;
	}

}