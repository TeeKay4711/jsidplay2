package ui.emulationsettings;

import javafx.util.StringConverter;

import java.util.Optional;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import libsidplay.Player;
import libsidplay.common.ChipModel;
import libsidplay.common.Emulation;
import libsidplay.common.Event;
import libsidplay.player.State;
import libsidplay.sidtune.SidTune;
import sidplay.ini.intf.IEmulationSection;
import sidplay.ini.intf.IFilterSection;
import ui.common.C64Window;
import ui.entities.config.EmulationSection;
import ui.entities.config.FilterSection;

public class EmulationSettings extends C64Window {

	protected final class EmulationChange implements ChangeListener<State> {
		@Override
		public void changed(ObservableValue<? extends State> observable,
				State oldValue, State newValue) {
			if (newValue == State.RUNNING) {
				Platform.runLater(() -> {
					updateSettingsForTune(util.getPlayer().getTune());
				});
			}
		}
	}

	private static final int STEP = 3;

	@FXML
	private ComboBox<Object> sid1Model, sid2Model, sid3Model, sid1Emulation,
			sid2Emulation, sid3Emulation;
	@FXML
	private ComboBox<ChipModel> defaultModel;
	@FXML
	private ComboBox<Emulation> defaultEmulation;
	@FXML
	private ComboBox<String> stereoMode, mainFilter, secondFilter, thirdFilter,
			sidToRead;
	@FXML
	private TextField baseAddress, thirdAddress;
	@FXML
	private CheckBox boosted8580;
	@FXML
	private Slider mainVolume, secondVolume, thirdVolume, mainBalance,
			secondBalance, thirdBalance;
	@FXML
	private LineChart<Number, Number> mainFilterCurve, secondFilterCurve,
			thirdFilterCurve;

	private StringConverter<Double> tickLabelFormatter;
	
	private ObservableList<Object> sid1Emulations, sid2Emulations,
			sid3Emulations, sid1Models, sid2Models, sid3Models;
	private ObservableList<ChipModel> defaultModels;
	private ObservableList<Emulation> defaultEmulations;
	private ObservableList<String> stereoModes, sidReads, mainFilters,
			secondFilters, thirdFilters;

	private ChangeListener<State> emulationChange;

	private boolean duringInitialization;

	public EmulationSettings(Player player) {
		super(player);
	}

	@FXML
	private void initialize() {
		duringInitialization = true;

		tickLabelFormatter = new StringConverter<Double>() {
			
			@Override
			public String toString(Double d) {
				double rounded = (double)Math.round(d * 10) / 10;
				return String.format("%.1f", rounded);
			}
			
			@Override
			public Double fromString(String string) {
				return Double.parseDouble(string);
			}
		};
		
		EmulationSection emulationSection = util.getConfig().getEmulation();

		mainFilters = FXCollections.<String> observableArrayList();
		mainFilter.setItems(mainFilters);
		secondFilters = FXCollections.<String> observableArrayList();
		secondFilter.setItems(secondFilters);
		thirdFilters = FXCollections.<String> observableArrayList();
		thirdFilter.setItems(thirdFilters);

		mainBalance.setValue(util.getConfig().getAudio().getMainBalance());
		mainBalance.setLabelFormatter(tickLabelFormatter);
		mainBalance.valueProperty().addListener(
				(observable, oldValue, newValue) -> {
					float balance = newValue.floatValue();
					util.getConfig().getAudio().setMainBalance(balance);
					util.getPlayer().configureSIDBuilder(
							b -> b.setBalance(0, util.getConfig().getAudio()));
				});
		secondBalance
				.setValue(util.getConfig().getAudio().getSecondBalance());
		secondBalance.setLabelFormatter(tickLabelFormatter);
		secondBalance.valueProperty().addListener(
				(observable, oldValue, newValue) -> {
					float balance = newValue.floatValue();
					util.getConfig().getAudio().setSecondBalance(balance);
					util.getPlayer().configureSIDBuilder(
							b -> b.setBalance(1, util.getConfig().getAudio()));
				});
		thirdBalance
				.setValue(util.getConfig().getAudio().getThirdBalance());
		thirdBalance.setLabelFormatter(tickLabelFormatter);
		thirdBalance.valueProperty().addListener(
				(observable, oldValue, newValue) -> {
					float balance = newValue.floatValue();
					util.getConfig().getAudio().setThirdBalance(balance);
					util.getPlayer().configureSIDBuilder(
							b -> b.setBalance(2, util.getConfig().getAudio()));
				});

		mainVolume.setValue(util.getConfig().getAudio().getMainVolume());
		mainVolume
				.valueProperty()
				.addListener(
						(observable, oldValue, newValue) -> {
							float volumeDb = newValue.floatValue();
							util.getConfig().getAudio().setMainVolume(volumeDb);
							util.getPlayer().configureSIDBuilder(
									(b) -> b.setVolume(0, util.getConfig()
											.getAudio()));
						});
		secondVolume.setValue(util.getConfig().getAudio().getSecondVolume());
		secondVolume.valueProperty()
				.addListener(
						(observable, oldValue, newValue) -> {
							float volumeDb = newValue.floatValue();
							util.getConfig().getAudio()
									.setSecondVolume(volumeDb);
							util.getPlayer().configureSIDBuilder(
									(b) -> b.setVolume(1, util.getConfig()
											.getAudio()));
						});
		thirdVolume.setValue(util.getConfig().getAudio().getThirdVolume());
		thirdVolume.valueProperty()
				.addListener(
						(observable, oldValue, newValue) -> {
							float volumeDb = newValue.floatValue();
							util.getConfig().getAudio()
									.setThirdVolume(volumeDb);
							util.getPlayer().configureSIDBuilder(
									(b) -> b.setVolume(2, util.getConfig()
											.getAudio()));
						});

		stereoModes = FXCollections.<String> observableArrayList();
		stereoModes.addAll(util.getBundle().getString("AUTO"), util.getBundle()
				.getString("FAKE_STEREO"),
				util.getBundle().getString("STEREO"), util.getBundle()
						.getString("3SID"));
		stereoMode.setItems(stereoModes);

		int dualSidBase = emulationSection.getDualSidBase();
		baseAddress.setText(String.format("0x%4x", dualSidBase));
		int thirdSidBase = emulationSection.getThirdSIDBase();
		thirdAddress.setText(String.format("0x%4x", thirdSidBase));

		sidReads = FXCollections.<String> observableArrayList();
		sidReads.addAll(util.getBundle().getString("FIRST_SID"), util
				.getBundle().getString("SECOND_SID"), util.getBundle()
				.getString("THIRD_SID"));
		sidToRead.setItems(sidReads);
		sidToRead.getSelectionModel()
				.select(emulationSection.getSidNumToRead());

		sid1Emulations = FXCollections.<Object> observableArrayList();
		sid1Emulations.addAll(util.getBundle().getString("AUTO"),
				Emulation.RESID, Emulation.RESIDFP);
		sid1Emulation.setItems(sid1Emulations);
		Emulation userEmulation = emulationSection.getUserEmulation();
		sid1Emulation.getSelectionModel().select(
				userEmulation != null ? userEmulation : util.getBundle()
						.getString("AUTO"));
		sid2Emulations = FXCollections.<Object> observableArrayList();
		sid2Emulations.addAll(util.getBundle().getString("AUTO"),
				Emulation.RESID, Emulation.RESIDFP);
		sid2Emulation.setItems(sid2Emulations);
		Emulation stereoEmulation = emulationSection.getStereoEmulation();
		sid2Emulation.getSelectionModel().select(
				stereoEmulation != null ? stereoEmulation : util.getBundle()
						.getString("AUTO"));
		sid3Emulations = FXCollections.<Object> observableArrayList();
		sid3Emulations.addAll(util.getBundle().getString("AUTO"),
				Emulation.RESID, Emulation.RESIDFP);
		sid3Emulation.setItems(sid3Emulations);
		Emulation thirdEmulation = emulationSection.getThirdEmulation();
		sid3Emulation.getSelectionModel().select(
				thirdEmulation != null ? thirdEmulation : util.getBundle()
						.getString("AUTO"));

		sid1Models = FXCollections.<Object> observableArrayList();
		sid1Models.addAll(util.getBundle().getString("AUTO"),
				ChipModel.MOS6581, ChipModel.MOS8580);
		sid1Model.setItems(sid1Models);
		ChipModel userSidModel = emulationSection.getUserSidModel();
		sid1Model.getSelectionModel().select(
				userSidModel != null ? userSidModel : util.getBundle()
						.getString("AUTO"));
		sid2Models = FXCollections.<Object> observableArrayList();
		sid2Models.addAll(util.getBundle().getString("AUTO"),
				ChipModel.MOS6581, ChipModel.MOS8580);
		sid2Model.setItems(sid2Models);
		ChipModel stereoSidModel = emulationSection.getStereoSidModel();
		sid2Model.getSelectionModel().select(
				stereoSidModel != null ? stereoSidModel : util.getBundle()
						.getString("AUTO"));
		sid3Models = FXCollections.<Object> observableArrayList();
		sid3Models.addAll(util.getBundle().getString("AUTO"),
				ChipModel.MOS6581, ChipModel.MOS8580);
		sid3Model.setItems(sid3Models);
		ChipModel thirdSidModel = emulationSection.getThirdSIDModel();
		sid3Model.getSelectionModel().select(
				thirdSidModel != null ? thirdSidModel : util.getBundle()
						.getString("AUTO"));
		defaultModels = FXCollections.<ChipModel> observableArrayList();
		defaultModels.addAll(ChipModel.MOS6581, ChipModel.MOS8580);
		defaultModel.setItems(defaultModels);
		ChipModel defautSidModel = emulationSection.getDefaultSidModel();
		defaultModel.getSelectionModel().select(defautSidModel);

		defaultEmulations = FXCollections.<Emulation> observableArrayList();
		defaultEmulations.addAll(Emulation.RESID, Emulation.RESIDFP);
		defaultEmulation.setItems(defaultEmulations);
		Emulation defaultSidEmulation = emulationSection.getDefaultEmulation();
		defaultEmulation.getSelectionModel().select(defaultSidEmulation);

		boosted8580.setSelected(emulationSection.isDigiBoosted8580());

		emulationChange = new EmulationChange();
		util.getPlayer().stateProperty().addListener(emulationChange);

		Platform.runLater(() -> {
			updateSettingsForTune(util.getPlayer().getTune());
			duringInitialization = false;
		});
	}

	private void updateSettingsForTune(SidTune tune) {
		addFilters(tune, 0, mainFilters, mainFilter);
		addFilters(tune, 1, secondFilters, secondFilter);
		addFilters(tune, 2, thirdFilters, thirdFilter);
		enableStereoSettings(tune);
	}

	private void enableStereoSettings(SidTune tune) {
		EmulationSection emulationSection = util.getConfig().getEmulation();
		boolean second = SidTune.isSIDUsed(emulationSection, tune, 1);
		boolean third = SidTune.isSIDUsed(emulationSection, tune, 2);
		int sidBase = SidTune.getSIDAddress(emulationSection, tune, 0);
		int dualSidBase = SidTune.getSIDAddress(emulationSection, tune, 1);
		boolean isForcedStereo = second && emulationSection.isForceStereoTune();
		boolean isFakeStereo = isForcedStereo && dualSidBase == sidBase;
		boolean isForced3Sid = third && emulationSection.isForce3SIDTune();
		if (isFakeStereo) {
			stereoMode.getSelectionModel().select(
					util.getBundle().getString("FAKE_STEREO"));
		} else if (isForced3Sid) {
			stereoMode.getSelectionModel().select(
					util.getBundle().getString("3SID"));
		} else if (isForcedStereo) {
			stereoMode.getSelectionModel().select(
					util.getBundle().getString("STEREO"));
		} else {
			stereoMode.getSelectionModel().select(
					util.getBundle().getString("AUTO"));
		}
		// stereo, only:
		mainBalance.setDisable(!second);
		secondVolume.setDisable(!second);
		secondBalance.setDisable(!second);
		sid2Emulation.setDisable(!second);
		sid2Model.setDisable(!second);
		secondFilter.setDisable(!second);
		secondFilterCurve.setDisable(!second);
		// 3-SID, only:
		thirdVolume.setDisable(!third);
		thirdBalance.setDisable(!third);
		sid3Emulation.setDisable(!third);
		sid3Model.setDisable(!third);
		thirdFilter.setDisable(!third);
		thirdFilterCurve.setDisable(!third);
		// fake stereo, only:
		sidToRead.setDisable(!(isFakeStereo));
		// forced stereo or forced 3-SID, only:
		baseAddress.setDisable(!(isForcedStereo || isForced3Sid));
		// forced 3-SID, only:
		thirdAddress.setDisable(!isForced3Sid);
	}

	@Override
	public void doClose() {
		util.getPlayer().stateProperty().removeListener(emulationChange);
	}

	@FXML
	private void setSid1Emulation() {
		Object selectedItem = sid1Emulation.getSelectionModel()
				.getSelectedItem();
		EmulationSection emulationSection = util.getConfig().getEmulation();
		if (selectedItem.equals(util.getBundle().getString("AUTO"))) {
			emulationSection.setUserEmulation(null);
		} else {
			emulationSection.setUserEmulation((Emulation) selectedItem);
		}
		setSid1Model();
	}

	@FXML
	private void setSid2Emulation() {
		Object selectedItem = sid2Emulation.getSelectionModel()
				.getSelectedItem();
		EmulationSection emulationSection = util.getConfig().getEmulation();
		if (selectedItem.equals(util.getBundle().getString("AUTO"))) {
			emulationSection.setStereoEmulation(null);
		} else {
			emulationSection.setStereoEmulation((Emulation) selectedItem);
		}
		setSid2Model();
	}

	@FXML
	private void setSid3Emulation() {
		Object selectedItem = sid3Emulation.getSelectionModel()
				.getSelectedItem();
		EmulationSection emulationSection = util.getConfig().getEmulation();
		if (selectedItem.equals(util.getBundle().getString("AUTO"))) {
			emulationSection.setThirdEmulation(null);
		} else {
			emulationSection.setThirdEmulation((Emulation) selectedItem);
		}
		setSid3Model();
	}

	@FXML
	private void setDefaultEmulation() {
		Emulation selectedItem = defaultEmulation.getSelectionModel()
				.getSelectedItem();
		EmulationSection emulationSection = util.getConfig().getEmulation();
		emulationSection.setDefaultEmulation(selectedItem);
		// default emulation has an impact on all emulation settings
		setSid1Emulation();
		setSid2Emulation();
		setSid3Emulation();
	}

	@FXML
	private void setSid1Model() {
		Object selectedItem = sid1Model.getSelectionModel().getSelectedItem();
		EmulationSection emulationSection = util.getConfig().getEmulation();
		if (selectedItem.equals(util.getBundle().getString("AUTO"))) {
			emulationSection.setUserSidModel(null);
		} else {
			emulationSection.setUserSidModel((ChipModel) selectedItem);
		}
		addFilters(util.getPlayer().getTune(), 0, mainFilters, mainFilter);
		updateChipModels();
	}

	@FXML
	private void setSid2Model() {
		Object selectedItem = sid2Model.getSelectionModel().getSelectedItem();
		EmulationSection emulationSection = util.getConfig().getEmulation();
		if (selectedItem.equals(util.getBundle().getString("AUTO"))) {
			emulationSection.setStereoSidModel(null);
		} else {
			emulationSection.setStereoSidModel((ChipModel) selectedItem);
		}
		addFilters(util.getPlayer().getTune(), 1, secondFilters, secondFilter);
		updateChipModels();
	}

	@FXML
	private void setSid3Model() {
		Object selectedItem = sid3Model.getSelectionModel().getSelectedItem();
		EmulationSection emulationSection = util.getConfig().getEmulation();
		if (selectedItem.equals(util.getBundle().getString("AUTO"))) {
			emulationSection.setThirdSIDModel(null);
		} else {
			emulationSection.setThirdSIDModel((ChipModel) selectedItem);
		}
		addFilters(util.getPlayer().getTune(), 2, thirdFilters, thirdFilter);
		updateChipModels();
	}

	@FXML
	private void setDefaultModel() {
		ChipModel selectedItem = (ChipModel) defaultModel.getSelectionModel()
				.getSelectedItem();
		EmulationSection emulationSection = util.getConfig().getEmulation();
		emulationSection.setDefaultSidModel(selectedItem);
		// default chip model has an impact on all chip model settings
		setSid1Model();
		setSid2Model();
		setSid3Model();
	}

	@FXML
	private void setBaseAddress() {
		Integer dualSidBase = Integer.decode(baseAddress.getText());
		util.getConfig().getEmulation().setDualSidBase(dualSidBase);

		enableStereoSettings(util.getPlayer().getTune());
		updateChipModels();
	}

	@FXML
	private void setThirdAddress() {
		Integer decode = Integer.decode(thirdAddress.getText());
		util.getConfig().getEmulation().setThirdSIDBase(decode);

		enableStereoSettings(util.getPlayer().getTune());
		updateChipModels();
	}

	@FXML
	private void setStereoMode() {
		String selectedItem = stereoMode.getSelectionModel().getSelectedItem();

		String thirdSid = util.getBundle().getString("3SID");
		String stereo = util.getBundle().getString("STEREO");
		String fakeStereo = util.getBundle().getString("FAKE_STEREO");

		if (selectedItem.equals(fakeStereo)) {
			util.getConfig().getEmulation().setForceStereoTune(true);
			util.getConfig().getEmulation().setForce3SIDTune(false);
			util.getConfig().getEmulation().setDualSidBase(0xd400);
			baseAddress.setText("0xd400");
		} else if (selectedItem.equals(thirdSid)) {
			util.getConfig().getEmulation().setForceStereoTune(true);
			util.getConfig().getEmulation().setForce3SIDTune(true);
			util.getConfig().getEmulation().setDualSidBase(0xd420);
			util.getConfig().getEmulation().setThirdSIDBase(0xd440);
			baseAddress.setText("0xd420");
			thirdAddress.setText("0xd440");
		} else if (selectedItem.equals(stereo)) {
			util.getConfig().getEmulation().setForceStereoTune(true);
			util.getConfig().getEmulation().setForce3SIDTune(false);
			util.getConfig().getEmulation().setDualSidBase(0xd420);
			baseAddress.setText("0xd420");
		} else {
			util.getConfig().getEmulation().setForceStereoTune(false);
			util.getConfig().getEmulation().setForce3SIDTune(false);
		}
		enableStereoSettings(util.getPlayer().getTune());
		updateChipModels();
		// stereo mode changes has an impact on all filter curves
		drawFilterCurve(mainFilter.getSelectionModel().getSelectedItem(), 0);
		drawFilterCurve(secondFilter.getSelectionModel().getSelectedItem(), 1);
		drawFilterCurve(thirdFilter.getSelectionModel().getSelectedItem(), 2);
	}

	@FXML
	private void setSidToRead() {
		int sidNumToRead = sidToRead.getSelectionModel().getSelectedIndex();
		util.getConfig().getEmulation().setSidNumToRead(sidNumToRead);
	}

	@FXML
	private void setDigiBoost() {
		boolean boost8580Enabled = boosted8580.isSelected();
		util.getConfig().getEmulation().setDigiBoosted8580(boost8580Enabled);
		util.getPlayer().configureSIDs(
				(num, sid) -> sid.input(boost8580Enabled ? sid
						.getInputDigiBoost() : 0));
	}

	@FXML
	private void setMainFilter() {
		String selectedItem = mainFilter.getSelectionModel().getSelectedItem();
		setFilter(0, selectedItem);
		updateChipModels();
		drawFilterCurve(selectedItem, 0);
	}

	@FXML
	private void setSecondFilter() {
		String selectedItem = secondFilter.getSelectionModel()
				.getSelectedItem();
		setFilter(1, selectedItem);
		updateChipModels();
		drawFilterCurve(selectedItem, 1);
	}

	@FXML
	private void setThirdFilter() {
		String selectedItem = thirdFilter.getSelectionModel().getSelectedItem();
		setFilter(2, selectedItem);
		updateChipModels();
		drawFilterCurve(selectedItem, 2);
	}

	/**
	 * Set filter name of the specified SID number according to the current
	 * emulation and chip model
	 * 
	 * @param sidNum
	 *            SID chip number
	 * @param filterName
	 *            filter name
	 */
	private void setFilter(int sidNum, String filterName) {
		IEmulationSection emulationSection = util.getConfig().getEmulation();

		boolean filterDisabled = "".equals(filterName);
		emulationSection.setFilterEnable(sidNum, !filterDisabled);

		SidTune tune = util.getPlayer().getTune();
		Emulation emulation = Emulation.getEmulation(emulationSection, tune,
				sidNum);
		ChipModel model = ChipModel
				.getChipModel(emulationSection, tune, sidNum);
		emulationSection.setFilterName(sidNum, emulation, model,
				!"".equals(filterName) ? filterName : null);
	}

	/**
	 * Update SID configuration on-the-fly.
	 */
	private void updateChipModels() {
		if (!duringInitialization) {
			util.getPlayer().getC64().getEventScheduler()
					.scheduleThreadSafe(new Event("Update SIDs") {
						@Override
						public void event() throws InterruptedException {
							util.getPlayer().createOrUpdateSIDs();
						}
					});
		}
	}

	/**
	 * Draw filter curve of the specified SID number and filter name
	 * 
	 * @param filterName
	 *            filter name
	 * @param num
	 *            SID chip number
	 */
	private void drawFilterCurve(final String filterName, int num) {
		EmulationSection emulationSection = util.getConfig().getEmulation();
		LineChart<Number, Number> filterCurve;
		switch (num) {
		case 0:
			filterCurve = mainFilterCurve;
			break;
		case 1:
			filterCurve = secondFilterCurve;
			break;
		case 2:
			filterCurve = thirdFilterCurve;
			break;
		default:
			throw new RuntimeException("Maximum supported SIDS exceeded!");
		}
		XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();
		series.setName(util.getBundle().getString("FILTERCURVE_TITLE"));
		filterCurve.getData().clear();

		SidTune tune = util.getPlayer().getTune();

		boolean second = SidTune.isSIDUsed(emulationSection, tune, 1);
		boolean third = SidTune.isSIDUsed(emulationSection, tune, 2);

		Optional<FilterSection> optFilter = util.getConfig().getFilter()
				.stream().filter(f -> f.getName().equals(filterName))
				.findFirst();
		if (optFilter.isPresent()) {
			FilterSection filter = optFilter.get();
			// stereo curve or 3-SID curve currently not used?
			if (!((num == 1 && !second) || (num == 2 && !third))) {
				for (int i = 0; i < resid_builder.resid.FilterModelConfig.FC_MAX; i += STEP) {
					if (filter.isReSIDFilter6581()
							|| filter.isReSIDFilter8580()) {
						double data = resid_builder.resid.FilterModelConfig
								.estimateFrequency(filter, i);
						series.getData()
								.add(new XYChart.Data<Number, Number>(i,
										(int) data));
					} else if (filter.isReSIDfpFilter6581()
							|| filter.isReSIDfpFilter8580()) {
						double data = resid_builder.residfp.FilterModelConfig
								.estimateFrequency(filter, i);
						series.getData()
								.add(new XYChart.Data<Number, Number>(i,
										(int) data));
					}
				}
			}
		}
		filterCurve.getData().add(series);
	}

	/**
	 * Add filters according to the current emulation and chip model of the
	 * currently played tune.
	 * 
	 * @param tune
	 *            currently played tune
	 * @param num
	 *            SID chip number
	 * @param filters
	 *            resulting filter list to add matching filter names to
	 * @param filter
	 *            combo box to select currently selected filter
	 */
	private void addFilters(final SidTune tune, int num,
			ObservableList<String> filters, ComboBox<String> filter) {
		EmulationSection emulationSection = util.getConfig().getEmulation();

		boolean filterEnable = emulationSection.isFilterEnable(num);

		Emulation emulation = Emulation.getEmulation(emulationSection, tune,
				num);
		ChipModel model = ChipModel.getChipModel(emulationSection, tune, num);
		String filterName = filterEnable ? emulationSection.getFilterName(num,
				emulation, model) : null;

		filters.clear();
		filters.add("");
		for (IFilterSection filterSection : util.getConfig().getFilter()) {
			if (emulation.equals(Emulation.RESIDFP)) {
				if (filterSection.isReSIDfpFilter6581()
						&& model == ChipModel.MOS6581) {
					filters.add(filterSection.getName());
				} else if (filterSection.isReSIDfpFilter8580()
						&& model == ChipModel.MOS8580) {
					filters.add(filterSection.getName());
				}
			} else {
				if (filterSection.isReSIDFilter6581()
						&& model == ChipModel.MOS6581) {
					filters.add(filterSection.getName());
				} else if (filterSection.isReSIDFilter8580()
						&& model == ChipModel.MOS8580) {
					filters.add(filterSection.getName());
				}
			}
		}
		if (filterEnable) {
			filter.getSelectionModel().select(filterName);
		} else {
			filter.getSelectionModel().select(0);
		}
	}

}