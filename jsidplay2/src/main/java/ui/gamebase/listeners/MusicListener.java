package ui.gamebase.listeners;

import java.io.File;
import java.io.IOException;

import javafx.scene.Node;
import libsidplay.Player;
import libsidplay.sidtune.SidTune;
import libsidplay.sidtune.SidTuneError;
import ui.common.UIUtil;
import ui.download.ProgressListener;

public class MusicListener extends ProgressListener {

	private Player player;

	public MusicListener(UIUtil util, Node node, Player player) {
		super(util, node);
	}

	@Override
	public void downloaded(final File downloadedFile) {
		if (downloadedFile == null) {
			return;
		}
		downloadedFile.deleteOnExit();
		// play tune
		try {
			player.playTune(SidTune.load(downloadedFile), null);
		} catch (IOException | SidTuneError e) {
			e.printStackTrace();
		}
	}
}