package ui.diskcollection;

import java.awt.Desktop;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.DirectoryChooser;
import libsidplay.Player;
import libsidutils.PathUtils;
import sidplay.ConsolePlayer;
import sidplay.consoleplayer.MediaType;
import ui.common.C64Stage;
import ui.common.UIPart;
import ui.common.UIUtil;
import ui.directory.Directory;
import ui.download.DownloadThread;
import ui.download.ProgressListener;
import ui.entities.config.Configuration;
import ui.entities.config.SidPlay2Section;
import ui.filefilter.DiskFileFilter;
import ui.filefilter.DocsFileFilter;
import ui.filefilter.ScreenshotFileFilter;
import ui.filefilter.TapeFileFilter;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;

public class DiskCollection extends Tab implements UIPart {

	private static final String HVMEC_DATA = "DATA";
	private static final String HVMEC_CONTROL = "CONTROL";

	@FXML
	private CheckBox autoConfiguration;
	@FXML
	private Directory directory;
	@FXML
	private ImageView screenshot;
	@FXML
	private TreeView<File> fileBrowser;
	@FXML
	private ContextMenu contextMenu;
	@FXML
	private MenuItem start, attachDisk;
	@FXML
	private TextField collectionDir;

	private UIUtil util;

	private ObjectProperty<DiscCollectionType> type;

	public DiscCollectionType getType() {
		return type.get();
	}

	public void setType(DiscCollectionType type) {
		this.type.set(type);
	}

	private String downloadUrl;

	private final FileFilter screenshotsFileFilter = new ScreenshotFileFilter();
	protected final FileFilter diskFileFilter = new DiskFileFilter();
	private final FileFilter fileBrowserFileFilter = new FileFilter() {

		private final TapeFileFilter tapeFileFilter = new TapeFileFilter();
		private final DocsFileFilter docsFileFilter = new DocsFileFilter();

		@Override
		public boolean accept(File file) {
			if (type.get() == DiscCollectionType.HVMEC && file.isDirectory()
					&& file.getName().equals(HVMEC_CONTROL)) {
				return false;
			}
			return diskFileFilter.accept(file) || tapeFileFilter.accept(file)
					|| docsFileFilter.accept(file);
		}
	};

	public DiskCollection(C64Stage c64Stage, ConsolePlayer consolePlayer,
			Player player, Configuration config) {
		util = new UIUtil(c64Stage, consolePlayer, player, config, this);
		setContent((Node) util.parse());
	}

	@FXML
	private void initialize() {
		directory.getAutoStartFileProperty().addListener(
				(observable, oldValue, newValue) -> {
					attachAndRunDemo(fileBrowser.getSelectionModel()
							.getSelectedItem().getValue(), newValue);
				});
		fileBrowser.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					if (newValue != null && newValue.getValue().isFile()) {
						File file = newValue.getValue();
						showScreenshot(file);
						directory.loadPreview(extract(file));
					}
				});
		fileBrowser.setOnKeyPressed((event) -> {
			TreeItem<File> selectedItem = fileBrowser.getSelectionModel()
					.getSelectedItem();
			if (event.getCode() == KeyCode.ENTER && selectedItem != null) {
				if (selectedItem.getValue().isFile()) {
					File file = selectedItem.getValue();
					if (file.isFile()) {
						attachAndRunDemo(selectedItem.getValue(), null);
					}
				}
			}
		});
		fileBrowser
				.setOnMousePressed((event) -> {
					final TreeItem<File> selectedItem = fileBrowser
							.getSelectionModel().getSelectedItem();
					if (selectedItem != null
							&& selectedItem.getValue().isFile()
							&& event.isPrimaryButtonDown()
							&& event.getClickCount() > 1) {
						attachAndRunDemo(fileBrowser.getSelectionModel()
								.getSelectedItem().getValue(), null);
					}
				});
		contextMenu.setOnShown((event) -> {
			TreeItem<File> selectedItem = fileBrowser.getSelectionModel()
					.getSelectedItem();
			boolean disable = selectedItem == null
					|| !selectedItem.getValue().isFile();
			start.setDisable(disable);
			attachDisk.setDisable(disable);
		});
		type = new SimpleObjectProperty<>();
		type.addListener((observable, oldValue, newValue) -> {
			String initialRoot;
			switch (type.get()) {
			case HVMEC:
				this.downloadUrl = util.getConfig().getOnline().getHvmecUrl();
				initialRoot = util.getConfig().getSidplay2().getHVMEC();
				break;

			case DEMOS:
				this.downloadUrl = util.getConfig().getOnline().getDemosUrl();
				initialRoot = util.getConfig().getSidplay2().getDemos();
				break;

			case MAGS:
				this.downloadUrl = util.getConfig().getOnline()
						.getMagazinesUrl();
				initialRoot = util.getConfig().getSidplay2().getMags();
				break;

			default:
				throw new RuntimeException("Illegal disk collection type : "
						+ type);
			}
			if (initialRoot != null) {
				setRootFile(new File(initialRoot));
			}
		});
	}

	@FXML
	private void doAutoConfiguration() {
		if (autoConfiguration.isSelected()) {
			autoConfiguration.setDisable(true);
			try {
				DownloadThread downloadThread = new DownloadThread(
						util.getConfig(), new ProgressListener(util,
								fileBrowser) {

							@Override
							public void downloaded(final File downloadedFile) {
								Platform.runLater(() -> {
									autoConfiguration.setDisable(false);
									if (downloadedFile != null) {
										setRootFile(downloadedFile);
									}
								});
							}
						}, new URL(downloadUrl));
				downloadThread.start();
			} catch (MalformedURLException e2) {
				e2.printStackTrace();
			}
		}
	}

	@FXML
	private void attachDisk() {
		File selectedFile = fileBrowser.getSelectionModel().getSelectedItem()
				.getValue();
		util.getConsolePlayer().insertMedia(extract(selectedFile), null,
				MediaType.DISK);
	}

	@FXML
	private void start() {
		attachAndRunDemo(fileBrowser.getSelectionModel().getSelectedItem()
				.getValue(), null);
	}

	@FXML
	private void doBrowse() {
		DirectoryChooser fileDialog = new DirectoryChooser();
		SidPlay2Section sidplay2 = (SidPlay2Section) util.getConfig()
				.getSidplay2();
		fileDialog.setInitialDirectory(sidplay2.getLastDirectoryFolder());
		File directory = fileDialog.showDialog(autoConfiguration.getScene()
				.getWindow());
		if (directory != null) {
			sidplay2.setLastDirectory(directory.getAbsolutePath());
			setRootFile(directory);
		}
	}

	protected void setRootFile(final File rootFile) {
		if (rootFile.exists()) {
			collectionDir.setText(rootFile.getAbsolutePath());

			final File theRootFile = new TFile(rootFile);
			fileBrowser.setRoot(new DiskCollectionTreeItem(theRootFile,
					theRootFile, fileBrowserFileFilter));

			if (type.get() == DiscCollectionType.HVMEC) {
				util.getConfig().getSidplay2()
						.setHVMEC(rootFile.getAbsolutePath());
			} else if (type.get() == DiscCollectionType.DEMOS) {
				util.getConfig().getSidplay2()
						.setDemos(rootFile.getAbsolutePath());
			} else if (type.get() == DiscCollectionType.MAGS) {
				util.getConfig().getSidplay2()
						.setMags(rootFile.getAbsolutePath());
			}
		}
	}

	protected void attachAndRunDemo(File file, final File autoStartFile) {
		if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".pdf")) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().open(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				System.err.println("Awt Desktop is not supported!");
			}
		} else {
			File extractedFile = extract(file);
			if (diskFileFilter.accept(file)) {
				util.getConsolePlayer().insertMedia(extractedFile,
						autoStartFile, MediaType.DISK);
			} else {
				util.getConsolePlayer().insertMedia(extractedFile,
						autoStartFile, MediaType.TAPE);
			}
			if (autoStartFile == null) {
				resetAndLoadDemo(extractedFile);
			}
		}
	}

	private void resetAndLoadDemo(final File file) {
		final String command;
		if (diskFileFilter.accept(file)) {
			// load from disk
			command = "LOAD\"*\",8,1\rRUN\r";
		} else {
			// load from tape
			command = "LOAD\rRUN\r";
		}
		util.setPlayingTab(this);
		util.getConsolePlayer().playTune(null, command);
	}

	protected void showScreenshot(final File file) {
		Image image = createImage(file);
		if (image != null) {
			screenshot.setImage(image);
		}
	}

	private Image createImage(final File file) {
		try {
			File screenshot = findScreenshot(file);
			if (screenshot != null && screenshot.exists()) {
				try (TFileInputStream is = new TFileInputStream(screenshot)) {
					return new Image(is);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private File findScreenshot(File file) {
		TreeItem<File> rootItem = fileBrowser.getRoot();
		if (rootItem == null) {
			return null;
		}
		String parentPath = type.get() == DiscCollectionType.HVMEC ? file
				.getParentFile().getPath().replace(HVMEC_DATA, HVMEC_CONTROL)
				: file.getParentFile().getPath();
		List<File> parentFiles = PathUtils.getFiles(parentPath,
				rootItem.getValue(), null);
		if (parentFiles.size() > 0) {
			File parentFile = parentFiles.get(parentFiles.size() - 1);
			for (File photoFile : parentFile.listFiles()) {
				if (!photoFile.isDirectory()
						&& screenshotsFileFilter.accept(photoFile)) {
					return photoFile;
				}
			}
		}
		return null;
	}

	protected File extract(File file) {
		if (file.getName().endsWith(".gz")) {
			File dst = new File(util.getConfig().getSidplay2().getTmpDir(),
					PathUtils.getBaseNameNoExt(file));
			dst.deleteOnExit();
			try (InputStream is = new GZIPInputStream(
					new TFileInputStream(file))) {
				TFile.cp(is, dst);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return dst;
		} else {
			File dst = new File(util.getConfig().getSidplay2().getTmpDir(),
					file.getName());
			dst.deleteOnExit();
			try {
				new TFile(file).cp(dst);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return dst;
		}
	}

}
