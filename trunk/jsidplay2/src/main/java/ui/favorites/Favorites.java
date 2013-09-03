package ui.favorites;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import libsidutils.PathUtils;
import sidplay.consoleplayer.State;
import ui.common.C64Tab;
import ui.entities.config.Configuration;
import ui.entities.config.FavoritesSection;
import ui.entities.config.SidPlay2Section;
import ui.filefilter.FavoritesExtension;
import ui.filefilter.TuneFileExtensions;

public class Favorites extends C64Tab {

	@FXML
	private Button add, remove, selectAll, deselectAll, load, save, saveAs;
	@FXML
	private TextField renameTab;
	@FXML
	private TabPane favoritesList;
	@FXML
	private RadioButton off, normal, randomOne, randomAll, repeatOff, repeatOne;

	private FavoritesTab currentlyPlayedFavorites;
	protected Random random = new Random();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		if (getPlayer() == null) {
			return;
		}
		getConsolePlayer().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> arg0,
					State arg1, State arg2) {
				if (arg2 == State.EXIT) {
					Platform.runLater(new Runnable() {
						public void run() {
							playNextTune();
						}
					});
				}
			}
		});
		List<? extends FavoritesSection> favorites = getConfig().getFavorites();
		for (FavoritesSection favorite : favorites) {
			addTab(favorite);
		}
		getConfig().getObservableFavorites().addListener(
				new ListChangeListener<FavoritesSection>() {

					@Override
					public void onChanged(Change<? extends FavoritesSection> change) {
						while (change.next()) {
							if (change.wasPermutated() || change.wasUpdated()) {
								continue;
							}
							if (change.wasAdded()) {
								List<? extends FavoritesSection> addedSubList = change.getAddedSubList();
								for (FavoritesSection favoritesSection : addedSubList) {
									addTab(favoritesSection);
								}
							}
						}
					}
				});
		favoritesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				// Save last selected tab
				if (newValue != null) {
					((Configuration) getConfig()).setCurrentFavorite(newValue.getText());
				}
			}
		});
		// Initially select last selected tab
		String currentFavorite = ((Configuration) getConfig()).getCurrentFavorite();
		if (currentFavorite != null) {
			for (Tab tab : favoritesList.getTabs()) {
				if (tab.getText().equals(currentFavorite)) {
					favoritesList.getSelectionModel().select(tab);
				}
			}
		}
	}

	@FXML
	private void addFavorites() {
		final FileChooser fileDialog = new FileChooser();
		fileDialog.setInitialDirectory(((SidPlay2Section) (getConfig()
				.getSidplay2())).getLastDirectoryFolder());
		fileDialog.getExtensionFilters().add(new ExtensionFilter(TuneFileExtensions.DESCRIPTION, TuneFileExtensions.EXTENSIONS));
		final List<File> files = fileDialog.showOpenMultipleDialog(favoritesList.getScene().getWindow());
		if (files != null && files.size() > 0) {
			File file = files.get(0);
			getConfig().getSidplay2().setLastDirectory(file.getParentFile().getAbsolutePath());
			FavoritesTab selectedTab = getSelectedTab();
			selectedTab.addFavorites(files);
			renameTab(selectedTab, PathUtils.getBaseNameNoExt(file.getParentFile()));
		}
	}

	@FXML
	private void removeFavorites() {
		getSelectedTab().removeSelectedFavorites();
	}

	@FXML
	private void selectAllFavorites() {
		getSelectedTab().selectAllFavorites();
	}

	@FXML
	private void clearSelection() {
		getSelectedTab().clearSelection();
	}

	@FXML
	private void loadFavorites() {
		final FileChooser fileDialog = new FileChooser();
		fileDialog.setInitialDirectory(((SidPlay2Section) (getConfig()
				.getSidplay2())).getLastDirectoryFolder());
		fileDialog.getExtensionFilters().add(new ExtensionFilter(FavoritesExtension.DESCRIPTION, FavoritesExtension.EXTENSION));
		final File file = fileDialog.showOpenDialog(favoritesList.getScene().getWindow());
		if (file != null) {
			getConfig().getSidplay2().setLastDirectory(file.getParentFile().getAbsolutePath());
			try {
				getSelectedTab().loadFavorites(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@FXML
	private void saveFavoritesAs() {
		final FileChooser fileDialog = new FileChooser();
		fileDialog.setInitialDirectory(((SidPlay2Section) (getConfig()
				.getSidplay2())).getLastDirectoryFolder());
		fileDialog.getExtensionFilters().add(new ExtensionFilter(FavoritesExtension.DESCRIPTION, FavoritesExtension.EXTENSION));
		final File file = fileDialog.showSaveDialog(favoritesList.getScene().getWindow());
		if (file != null) {
			getConfig().getSidplay2().setLastDirectory(file.getParentFile().getAbsolutePath());
			// then load the favorites
			try {
				getSelectedTab().saveFavorites(file);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	@FXML
	private void addTab() {
		List<FavoritesSection> favorites = ((Configuration) getConfig()).getFavorites();
		FavoritesSection favoritesSection = new FavoritesSection();
		favoritesSection.setName(getBundle().getString("NEW_TAB"));
		favorites.add(favoritesSection);
	}

	@FXML
	private void renameTab() {
		FavoritesTab selectedTab = getSelectedTab();
		String name = renameTab.getText();
		renameTab(selectedTab, name);
	}

	@FXML
	private void enablePlayback() {

	}

	@FXML
	private void enableRepeat() {

	}

	private FavoritesTab getSelectedTab() {
		return (FavoritesTab) favoritesList.getSelectionModel().getSelectedItem();
	}

	protected void addTab(final FavoritesSection favoritesSection) {
		final FavoritesTab newTab = new FavoritesTab();
		newTab.setText(favoritesSection.getName());
		newTab.restoreColumns(favoritesSection);
		newTab.setClosable(favoritesList.getTabs().size() != 0);
		newTab.setOnClosed(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				newTab.removeAllFavorites();
			}

		});

		// XXX JavaFX: better initialization support using constructor
		// arguments?
		newTab.setFavorites(this);
		newTab.setPlayer(getPlayer());
		newTab.setConsolePlayer(getConsolePlayer());
		newTab.setConfig(getConfig());
		newTab.initialize(null, null);

		favoritesList.getTabs().add(newTab);
	}

	private void renameTab(FavoritesTab selectedTab, String name) {
		selectedTab.setText(name);
		selectedTab.getFavoritesSection().setName(name);
	}

	protected void playNextTune() {
		if (repeatOne.isSelected()) {
			// repeat one tune
			getConsolePlayer().playTune(getPlayer().getTune(), null);
		} else if (randomAll.isSelected()) {
			// random all favorites tab
			favoritesList.getSelectionModel().select(Math.abs(random.nextInt(Integer.MAX_VALUE)) % favoritesList.getTabs().size());
			currentlyPlayedFavorites = (FavoritesTab) favoritesList.getSelectionModel().getSelectedItem();
			currentlyPlayedFavorites.playNextRandom();
		} else if (currentlyPlayedFavorites != null && randomOne.isSelected()) {
			// random one favorites tab
			currentlyPlayedFavorites.playNextRandom();
		} else if (currentlyPlayedFavorites != null && !off.isSelected() && repeatOff.isSelected()) {
			// normal playback
			currentlyPlayedFavorites.playNext(getPlayer().getTune().getInfo().file);
		}
	}

	public void setCurrentlyPlayedFavorites(
			FavoritesTab currentlyPlayedFavorites) {
		this.currentlyPlayedFavorites = currentlyPlayedFavorites;
	}
	
	public List<FavoritesTab> getFavoriteTabs() {
		List<FavoritesTab> result = new ArrayList<FavoritesTab>();
		for (Tab tab : favoritesList.getTabs()) {
			result.add((FavoritesTab) tab);
		}
		return result;
	}
}
