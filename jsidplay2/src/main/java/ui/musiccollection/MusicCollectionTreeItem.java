package ui.musiccollection;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import libsidplay.Player;
import libsidutils.STIL.STILEntry;
import ui.JSIDPlay2Main;
import ui.filefilter.TuneFileFilter;

public class MusicCollectionTreeItem extends TreeItem<File> {

	private static final Image stilIcon = new Image(JSIDPlay2Main.class
			.getResource("icons/stil.png").toString());

	private static final Image noStilIcon = new Image(JSIDPlay2Main.class
			.getResource("icons/stil_no.png").toString());

	private final FileFilter fFileFilter = new TuneFileFilter();
	private boolean hasLoadedChildren;
	private boolean isLeaf;
	private boolean hasSTIL;

	private Player player;
	private STILEntry stilEntry;

	public MusicCollectionTreeItem(Player player, File file) {
		super(file);
		this.player = player;
		this.isLeaf = file.isFile();
		if (isLeaf && player != null) {
			this.stilEntry = player.getStilEntry(file);
			this.hasSTIL = stilEntry != null;
		}
	}

	@Override
	public boolean isLeaf() {
		return isLeaf;
	}

	@Override
	public ObservableList<TreeItem<File>> getChildren() {
		if (hasLoadedChildren == false) {
			loadChildren();
		}
		return super.getChildren();
	}

	public boolean hasSTIL() {
		return hasSTIL;
	}

	public STILEntry getStilEntry() {
		return stilEntry;
	}

	private void loadChildren() {
		hasLoadedChildren = true;
		Collection<MusicCollectionTreeItem> children = new ArrayList<MusicCollectionTreeItem>();
		File[] listFiles = getValue().listFiles(fFileFilter);
		if (listFiles != null) {
			Arrays.stream(listFiles).sorted(
					(a, b) -> {
						Integer aw = a.isFile() ? 1 : 0;
						Integer bw = b.isFile() ? 1 : 0;
						if (aw.equals(bw)) {
							return a.getName()
									.toLowerCase(Locale.ENGLISH)
									.compareTo(
											b.getName().toLowerCase(
													Locale.ENGLISH));
						}
						return aw.compareTo(bw);
					});
			for (File file : listFiles) {
				MusicCollectionTreeItem childItem = new MusicCollectionTreeItem(
						player, file);
				children.add(childItem);
				if (childItem.hasSTIL()) {
					childItem.setGraphic(new ImageView(stilIcon));
				} else {
					childItem.setGraphic(new ImageView(noStilIcon));
				}
			}
		}
		super.getChildren().setAll(children);
	}

}
