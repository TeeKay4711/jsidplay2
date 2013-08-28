package ui.musiccollection;

import static sidplay.ConsolePlayer.playerRunning;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.metamodel.SingularAttribute;

import libpsid64.Psid64;
import libsidplay.sidtune.SidTune;
import libsidplay.sidtune.SidTuneError;
import libsidutils.PathUtils;
import ui.common.C64Tab;
import ui.common.TypeTextField;
import ui.common.dialog.YesNoDialog;
import ui.download.DownloadThread;
import ui.download.ProgressListener;
import ui.entities.PersistenceUtil;
import ui.entities.collection.HVSCEntry;
import ui.entities.collection.HVSCEntry_;
import ui.entities.collection.StilEntry_;
import ui.entities.collection.service.VersionService;
import ui.entities.config.SidPlay2Section;
import ui.events.favorites.IAddFavoritesTab;
import ui.events.favorites.IGetFavoritesTabs;
import ui.favorites.FavoritesTab;
import ui.filefilter.TuneFileFilter;
import ui.musiccollection.search.ISearchListener;
import ui.musiccollection.search.SearchInIndexThread;
import ui.musiccollection.search.SearchIndexCreator;
import ui.musiccollection.search.SearchIndexerThread;
import ui.musiccollection.search.SearchThread;
import ui.stil.STIL;

/**
 * Common view base for HVSC and CGSC collections. Loosely based on Rhythmbox,
 * which is probably based on iTunes. Display is divided to 2 vertical panels of
 * identical widths
 * 
 * - 1st shows file meta info in table, and composer's photo, scaled to 100% of
 * width. These take the whole vertical space.
 * 
 * - 2nd column show search bar, which is used to match song and artist name
 * 
 * - 2nd column displays list of artists.
 * 
 * - 2nd column displays list of songs matching search criteria and selected
 * artist. - currently playing symbol - artist name - song name - total song
 * length (?)
 * 
 * @author Ken H�ndel
 * @author Antti Lankila
 */
public class MusicCollection extends C64Tab implements ISearchListener {

	private class SearchCriteria<DECLARING_CLASS, JAVA_TYPE> {
		public SearchCriteria(SingularAttribute<DECLARING_CLASS, JAVA_TYPE> att) {
			this.attribute = att;
		}

		private SingularAttribute<DECLARING_CLASS, JAVA_TYPE> attribute;

		public SingularAttribute<DECLARING_CLASS, JAVA_TYPE> getAttribute() {
			return attribute;
		}

		@Override
		public String toString() {
			return getBundle().getString(
					attribute.getDeclaringType().getJavaType().getSimpleName()
							+ "." + attribute.getName());
		}
	}

	@FXML
	protected CheckBox autoConfiguration;
	@FXML
	private TableView<TuneInfo> tuneInfoTable;
	@FXML
	private ImageView photograph;
	@FXML
	protected TreeView<File> fileBrowser;
	@FXML
	private ComboBox<SearchCriteria<?, ?>> searchCriteria;
	@FXML
	private ComboBox<String> searchScope, searchResult;
	@FXML
	private Button startSearch, stopSearch, resetSearch, createSearchIndex;
	@FXML
	private TextField collectionDir;
	@FXML
	private TypeTextField stringTextField, integerTextField, longTextField,
			shortTextField;
	@FXML
	private ComboBox<Enum<?>> combo;
	@FXML
	private ContextMenu contextMenu;
	@FXML
	protected MenuItem showStil, convertToPSID64;
	@FXML
	protected Menu addToFavorites;

	private ObservableList<TuneInfo> tuneInfos = FXCollections
			.<TuneInfo> observableArrayList();
	private ObservableList<String> searchScopes = FXCollections
			.<String> observableArrayList();
	private ObservableList<String> searchResults = FXCollections
			.<String> observableArrayList();
	private ObservableList<SearchCriteria<?, ?>> searchCriterias = FXCollections
			.<SearchCriteria<?, ?>> observableArrayList();
	private ObservableList<Enum<?>> comboItems = FXCollections
			.<Enum<?>> observableArrayList();

	private MusicCollectionType type;
	private String collectionURL, dbName;

	private EntityManager em;
	private VersionService versionService;

	protected TuneFileFilter fileFilter = new TuneFileFilter();
	private SearchThread searchThread;
	private Object savedState;
	private Object searchForValue, recentlySearchedForValue;
	private SearchCriteria<?, ?> recentlySearchedCriteria;
	private boolean searchOptionsChanged;

	protected FavoritesTab favoritesToAddSearchResult;

	private DoubleProperty progress = new SimpleDoubleProperty();
	protected List<TreeItem<File>> currentlyPlayedTreeItems;

	public DoubleProperty getProgressValue() {
		return progress;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (getPlayer() == null) {
			// wait for second initialization, where properties have been set!
			return;
		}
		getConsolePlayer().getState().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> arg0,
					Number arg1, Number arg2) {
				if (arg2.intValue() == playerRunning
						&& getPlayer().getTune() != null) {
					Platform.runLater(new Runnable() {
						public void run() {
							// auto-expand current selected tune
							showNextHit(getPlayer().getTune().getInfo().file);
						}
					});
				}
			}
		});

		tuneInfoTable.setItems(tuneInfos);

		searchScope.setItems(searchScopes);
		searchScopes.addAll(getBundle().getString("FORWARD"), getBundle()
				.getString("BACKWARD"));
		searchScope.getSelectionModel().select(0);

		searchResult.setItems(searchResults);
		searchResults.addAll(getBundle().getString("SHOW_NEXT_MATCH"),
				getBundle().getString("ADD_TO_A_NEW_PLAYLIST"));
		searchResult.getSelectionModel().select(0);

		searchCriteria.setItems(searchCriterias);
		for (SingularAttribute<? extends Object, ?> singularAttribute : Arrays
				.asList(HVSCEntry_.path, HVSCEntry_.name, HVSCEntry_.title,
						HVSCEntry_.author, HVSCEntry_.released,
						HVSCEntry_.format, HVSCEntry_.playerId,
						HVSCEntry_.noOfSongs, HVSCEntry_.startSong,
						HVSCEntry_.clockFreq, HVSCEntry_.speed,
						HVSCEntry_.sidModel1, HVSCEntry_.sidModel2,
						HVSCEntry_.compatibility, HVSCEntry_.tuneLength,
						HVSCEntry_.audio, HVSCEntry_.sidChipBase1,
						HVSCEntry_.sidChipBase2, HVSCEntry_.driverAddress,
						HVSCEntry_.loadAddress, HVSCEntry_.loadLength,
						HVSCEntry_.initAddress, HVSCEntry_.playerAddress,
						HVSCEntry_.fileDate, HVSCEntry_.fileSizeKb,
						HVSCEntry_.tuneSizeB, HVSCEntry_.relocStartPage,
						HVSCEntry_.relocNoPages, StilEntry_.stilName,
						StilEntry_.stilAuthor, StilEntry_.stilTitle,
						StilEntry_.stilArtist, StilEntry_.stilComment)) {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			SearchCriteria<?, ?> criteria = new SearchCriteria(
					singularAttribute);
			searchCriterias.add(criteria);
		}
		searchCriteria.getSelectionModel().select(0);

		combo.setItems(comboItems);

		fileBrowser
				.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
					@Override
					public TreeCell<File> call(TreeView<File> arg0) {
						return new TreeCell<File>() {
							@Override
							protected void updateItem(File item, boolean empty) {
								super.updateItem(item, empty);
								if (!empty) {
									setText(item.getName());
									setGraphic(getTreeItem().getGraphic());
								}
							}
						};
					}
				});
		fileBrowser.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				TreeItem<File> selectedItem = fileBrowser.getSelectionModel()
						.getSelectedItem();
				if (event.getCode() == KeyCode.ENTER) {
					if (selectedItem != null
							&& !selectedItem.equals(fileBrowser.getRoot())
							&& selectedItem.getValue().isFile()) {
						final File file = selectedItem.getValue();
						playTune(file);
					}
				}

			}
		});
		fileBrowser.getSelectionModel().selectedItemProperty()
				.addListener(new ChangeListener<TreeItem<File>>() {

					@Override
					public void changed(
							ObservableValue<? extends TreeItem<File>> observable,
							TreeItem<File> oldValue, TreeItem<File> newValue) {
						if (newValue != null
								&& !newValue.equals(fileBrowser.getRoot())
								&& newValue.getValue().isFile()) {
							File tuneFile = newValue.getValue();
							try {
								SidTune sidTune = SidTune.load(tuneFile);
								showPhoto(sidTune);
								showTuneInfos(tuneFile, sidTune);
							} catch (IOException | SidTuneError e) {
								e.printStackTrace();
							}
						}
					}

				});
		fileBrowser.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				final TreeItem<File> selectedItem = fileBrowser
						.getSelectionModel().getSelectedItem();
				if (selectedItem != null && selectedItem.getValue().isFile()
						&& event.isPrimaryButtonDown()
						&& event.getClickCount() > 1) {
					playTune(selectedItem.getValue());
				}
			}
		});
		contextMenu.setOnShown(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				final TreeItem<File> selectedItem = fileBrowser
						.getSelectionModel().getSelectedItem();
				showStil.setDisable(selectedItem == null
						|| !((MusicCollectionTreeItem) selectedItem).hasSTIL());
				convertToPSID64.setDisable(selectedItem == null);

				addToFavorites.setDisable(true);
				getUiEvents().fireEvent(IGetFavoritesTabs.class,
						new IGetFavoritesTabs() {
							@Override
							public void setFavoriteTabs(
									List<FavoritesTab> tabs, String selected) {
								addToFavorites.getItems().clear();
								for (final FavoritesTab tab : tabs) {
									MenuItem item = new MenuItem(tab.getText());
									item.setOnAction(new EventHandler<ActionEvent>() {

										@Override
										public void handle(ActionEvent arg0) {
											tab.addFavorites(Collections
													.singletonList(selectedItem
															.getValue()));
										}
									});
									addToFavorites.getItems().add(item);
								}
								addToFavorites.setDisable(addToFavorites
										.getItems().size() == 0);
							}
						});
			}
		});

		final String initialRoot = type == MusicCollectionType.HVSC ? getConfig()
				.getSidplay2().getHvsc()
				: type == MusicCollectionType.CGSC ? getConfig().getSidplay2()
						.getCgsc() : null;
		if (initialRoot != null) {
			setRoot(new File(initialRoot));
		}
	}

	@FXML
	private void showSTIL() {
		TreeItem<File> selectedItem = fileBrowser.getSelectionModel()
				.getSelectedItem();
		STIL stil = new STIL();
		stil.setPlayer(getPlayer());
		stil.setConfig(getConfig());
		stil.setEntry(libsidutils.STIL.getSTIL(
				fileBrowser.getRoot().getValue(), selectedItem.getValue()));
		try {
			stil.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void convertToPSID64() {
		DirectoryChooser fileDialog = new DirectoryChooser();
		fileDialog.setInitialDirectory(((SidPlay2Section) (getConfig()
				.getSidplay2())).getLastDirectoryFolder());
		final File directory = fileDialog.showDialog(fileBrowser.getScene()
				.getWindow());
		if (directory != null) {
			getConfig().getSidplay2().setLastDirectory(
					directory.getAbsolutePath());
			Psid64 c = new Psid64(getConfig().getSidplay2().getTmpDir());
			c.convertFiles(fileBrowser.getRoot().getValue(),
					new File[] { fileBrowser.getSelectionModel()
							.getSelectedItem().getValue() }, directory);
		}
	}

	@FXML
	private void doAutoConfiguration() {
		String url = null;
		if (type == MusicCollectionType.HVSC) {
			url = getConfig().getOnline().getHvscUrl();
		} else if (type == MusicCollectionType.CGSC) {
			url = getConfig().getOnline().getCgscUrl();
		}
		if (url != null && autoConfiguration.isSelected()) {
			autoConfiguration.setDisable(true);
			try {
				DownloadThread downloadThread = new DownloadThread(getConfig(),
						new ProgressListener(progress) {

							@Override
							public void downloaded(final File downloadedFile) {
								Platform.runLater(new Runnable() {

									@Override
									public void run() {
										autoConfiguration.setDisable(false);
										if (downloadedFile != null) {
											setRoot(downloadedFile);
										}
									}
								});
							}
						}, new URL(url));
				downloadThread.start();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void searchCategory() {
		if (searchCriteria.getSelectionModel().getSelectedItem() != recentlySearchedCriteria) {
			searchOptionsChanged = true;
			recentlySearchedCriteria = searchCriteria.getSelectionModel()
					.getSelectedItem();
		}
		setSearchEditorVisible();
	}

	@FXML
	private void doStartSearch() {
		startSearch(false);
	}

	@FXML
	private void doStopSearch() {
		if (searchThread != null && searchThread.isAlive()) {
			searchThread.setAborted(true);
		}
	}

	@FXML
	private void doResetSearch() {
		savedState = null;
	}

	@FXML
	private void doCreateSearchIndex() {
		YesNoDialog dialog = new YesNoDialog();
		dialog.setTitle(getBundle().getString("CREATE_SEARCH_DATABASE"));
		dialog.setText(String.format(
				getBundle().getString("RECREATE_DATABASE"), dbName));
		dialog.getConfirmed().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
					startSearch(true);
				}
			}
		});
		try {
			dialog.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void doBrowse() {
		final DirectoryChooser fileDialog = new DirectoryChooser();
		fileDialog.setInitialDirectory(((SidPlay2Section) (getConfig()
				.getSidplay2())).getLastDirectoryFolder());
		File directory = fileDialog.showDialog(autoConfiguration.getScene()
				.getWindow());
		if (directory != null) {
			getConfig().getSidplay2().setLastDirectory(
					directory.getAbsolutePath());
			setRoot(directory);
		}
	}

	@FXML
	private void gotoURL() {
		// Open a browser URL

		// As an application we open the default browser
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (desktop.isSupported(Desktop.Action.BROWSE)) {
				try {
					desktop.browse(new URL(collectionURL).toURI());
				} catch (final IOException ioe) {
					ioe.printStackTrace();
				} catch (final URISyntaxException urie) {
					urie.printStackTrace();
				}
			}
		}
	}

	@FXML
	private void doSetValue() {
		setSearchValue();
		if (searchForValue != null
				&& !searchForValue.equals(recentlySearchedForValue)) {
			searchOptionsChanged = true;
			recentlySearchedForValue = searchForValue;
		}
		startSearch(false);
	}

	public String getCollectionURL() {
		return collectionURL;
	}

	public void setCollectionURL(String collectionURL) {
		this.collectionURL = collectionURL;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public MusicCollectionType getType() {
		return type;
	}

	public void setType(MusicCollectionType type) {
		this.type = type;
	}

	private void setSearchEditorVisible() {
		for (Node node : Arrays.asList(stringTextField, integerTextField,
				longTextField, shortTextField, combo)) {
			node.setVisible(false);
		}
		Class<?> type = getSelectedField().getJavaType();
		if (type == Long.class) {
			longTextField.setVisible(true);
		} else if (type == Integer.class || type == Date.class) {
			integerTextField.setVisible(true);
		} else if (type == Short.class) {
			shortTextField.setVisible(true);
		} else if (type == String.class) {
			stringTextField.setVisible(true);
		} else if (Enum.class.isAssignableFrom(type)) {
			comboItems.clear();
			@SuppressWarnings("unchecked")
			Class<? extends Enum<?>> en = (Class<? extends Enum<?>>) type;
			for (Enum<?> val : en.getEnumConstants()) {
				comboItems.add(val);
			}
			combo.setVisible(true);
			combo.getSelectionModel().select(0);
		}
	}

	private void setSearchValue() {
		Class<?> type = getSelectedField().getJavaType();
		if (type == Integer.class) {
			searchForValue = integerTextField.getValue();
		} else if (type == Long.class) {
			searchForValue = longTextField.getValue();
		} else if (type == Short.class) {
			searchForValue = shortTextField.getValue();
		} else if (type == String.class) {
			searchForValue = stringTextField.getValue();
		} else if (Enum.class.isAssignableFrom(type)) {
			searchForValue = combo.getSelectionModel().getSelectedItem();
		} else if (type == Date.class) {
			Calendar cal = Calendar.getInstance();
			cal.set((Integer) integerTextField.getValue(), 1, 1);
			searchForValue = cal.getTime();
		}
	}

	private SingularAttribute<?, ?> getSelectedField() {
		return ((SearchCriteria<?, ?>) searchCriteria.getSelectionModel()
				.getSelectedItem()).getAttribute();
	}

	protected void setRoot(final File rootFile) {
		if (rootFile.exists()) {
			em = Persistence.createEntityManagerFactory(
					PersistenceUtil.COLLECTION_DS,
					new PersistenceUtil(new File(rootFile.getParentFile(),
							dbName))).createEntityManager();

			versionService = new VersionService(em);

			collectionDir.setText(rootFile.getAbsolutePath());

			if (rootFile.exists()) {
				if (type == MusicCollectionType.HVSC) {
					getConfig().getSidplay2().setHvsc(
							rootFile.getAbsolutePath());
					File theRootFile = ((SidPlay2Section) getConfig()
							.getSidplay2()).getHvscFile();
					fileBrowser.setRoot(new MusicCollectionTreeItem(this,
							theRootFile, theRootFile));
				} else if (type == MusicCollectionType.CGSC) {
					getConfig().getSidplay2().setCgsc(
							rootFile.getAbsolutePath());
					File theRootFile = ((SidPlay2Section) getConfig()
							.getSidplay2()).getCgscFile();
					fileBrowser.setRoot(new MusicCollectionTreeItem(this,
							theRootFile, theRootFile));
				}
				fileBrowser.setCellFactory(new MusicCollectionCellFactory());
			}

		}
		doResetSearch();
	}

	protected void showPhoto(SidTune sidTune) {
		if (sidTune != null && sidTune.getImage() != null) {
			photograph.setImage(sidTune.getImage());
		} else {
			photograph.setImage(null);
		}
	}

	protected void showTuneInfos(File tuneFile, SidTune sidTune) {
		tuneInfos.clear();
		HVSCEntry entry = HVSCEntry.create(fileBrowser.getRoot().getValue(),
				tuneFile.getAbsolutePath(), tuneFile, sidTune);

		for (Field field : HVSCEntry_.class.getDeclaredFields()) {
			if (field.getName().equals(HVSCEntry_.id.getName())) {
				continue;
			}
			if (!(SingularAttribute.class.isAssignableFrom(field.getType()))) {
				continue;
			}
			TuneInfo tuneInfo = new TuneInfo();
			String name = getBundle().getString(
					HVSCEntry.class.getSimpleName() + "." + field.getName());
			tuneInfo.setName(name);
			try {
				SingularAttribute<?, ?> singleAttribute = (SingularAttribute<?, ?>) field
						.get(entry);
				Object value = ((Field) singleAttribute.getJavaMember())
						.get(entry);
				tuneInfo.setValue(String.valueOf(value != null ? value : ""));
			} catch (IllegalArgumentException | IllegalAccessException e) {
			}
			tuneInfos.add(tuneInfo);
		}

	}

	@Override
	public void searchStart() {
		startSearch.setDisable(true);
		stopSearch.setDisable(false);
		resetSearch.setDisable(true);
		createSearchIndex.setDisable(true);
	}

	@Override
	public void searchStop(final boolean canceled) {
		startSearch.setDisable(false);
		stopSearch.setDisable(true);
		resetSearch.setDisable(false);
		createSearchIndex.setDisable(false);

		// remember search state
		savedState = searchThread.getSearchState();
		progress.set(0);
	}

	@Override
	public void searchHit(final File current) {
		if (searchThread instanceof SearchIndexerThread) {
			// if search index is created, do not show the next result
			progress.set((progress.get() + 1) % 100);
			return;
		}
		switch (searchResult.getSelectionModel().getSelectedIndex()) {
		case 0:
			searchThread.setAborted(true);
			searchStop(true);

			showNextHit(current);
			break;

		case 1:
			favoritesToAddSearchResult.addFavorites(Collections
					.singletonList(current));
			break;

		default:
			break;
		}

	}

	protected void showNextHit(final File matchFile) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				TreeItem<File> rootItem = fileBrowser.getRoot();
				if (rootItem == null) {
					return;
				}
				List<TreeItem<File>> pathSegs = new ArrayList<TreeItem<File>>();
				pathSegs.add(rootItem);

				File rootFile = rootItem.getValue();
				String filePath = matchFile.getPath();
				TreeItem<File> curItem = rootItem;
				for (File file : PathUtils.getFiles(filePath, rootFile,
						fileFilter)) {
					for (TreeItem<File> childItem : curItem.getChildren()) {
						if (file.equals(childItem.getValue())) {
							pathSegs.add(childItem);
							curItem = childItem;
							childItem.setExpanded(true);
						}
					}
				}
				if (pathSegs.size() > 0) {
					currentlyPlayedTreeItems = pathSegs;
					TreeItem<File> selectedItem = fileBrowser
							.getSelectionModel().getSelectedItem();
					if (selectedItem == null
							|| !selectedItem.getValue().equals(
									getPlayer().getTune().getInfo().file)) {
						TreeItem<File> treeItem = pathSegs.get(pathSegs.size() - 1);
						fileBrowser.getSelectionModel().select(treeItem);
						fileBrowser.scrollTo(fileBrowser.getRow(treeItem));
					}
				}
			}
		});
	}

	protected void startSearch(boolean forceRecreate) {
		if (searchThread != null && searchThread.isAlive()) {
			return;
		}

		if (!new File(collectionDir.getText()).exists()) {
			return;
		}

		progress.set(0);

		/*
		 * validate database: version is inserted only after successful create
		 * completes.
		 */
		if (!forceRecreate) {
			if (!versionService.isExpectedVersion()) {
				forceRecreate = true;
			}
		}

		if (forceRecreate) {
			final File root = fileBrowser.getRoot().getValue();
			searchThread = new SearchIndexerThread(root);
			searchThread.addSearchListener(this);
			searchThread.addSearchListener(new SearchIndexCreator(fileBrowser
					.getRoot().getValue(), getConfig(), em));

			searchThread.start();
		} else {
			switch (searchResult.getSelectionModel().getSelectedIndex()) {
			case 1:
				// Add result to favorites?
				// Create new favorites tab
				getUiEvents().fireEvent(IAddFavoritesTab.class,
						new IAddFavoritesTab() {

							@Override
							public String getTitle() {
								return getBundle().getString("NEW_TAB");
							}

							@Override
							public void setFavorites(
									final FavoritesTab favorites) {
								favoritesToAddSearchResult = favorites;
							}
						});
				break;

			default:
				break;
			}

			setSearchValue();
			final SearchInIndexThread t = new SearchInIndexThread(em,
					searchScope.getSelectionModel().getSelectedIndex() != 1) {
				@Override
				public List<File> getFiles(String filePath) {
					return PathUtils.getFiles(filePath, fileBrowser.getRoot()
							.getValue(), fileFilter);
				}
			};
			t.addSearchListener(this);
			t.setField(getSelectedField());
			t.setFieldValue(searchForValue);
			t.setCaseSensitive(false);
			if (searchOptionsChanged) {
				doResetSearch();
				searchOptionsChanged = false;
			}
			searchThread = t;
			searchThread.setSearchState(savedState);
			searchThread.start();
		}

	}

	protected void playTune(final File file) {
		setPlayedGraphics(fileBrowser);
		try {
			getConsolePlayer().playTune(SidTune.load(file), null);
		} catch (IOException | SidTuneError e) {
			e.printStackTrace();
		}
	}

	public List<TreeItem<File>> getCurrentlyPlayedTreeItems() {
		return currentlyPlayedTreeItems;
	}
}