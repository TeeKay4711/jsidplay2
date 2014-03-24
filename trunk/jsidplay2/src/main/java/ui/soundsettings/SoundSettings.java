package ui.soundsettings;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import resid_builder.resid.ISIDDefs.SamplingMethod;
import sidplay.audio.CmpMP3File;
import sidplay.consoleplayer.DriverSettings;
import sidplay.consoleplayer.Emulation;
import sidplay.consoleplayer.Output;
import ui.common.C64Stage;

public class SoundSettings extends C64Stage {

	@FXML
	protected TextField mp3, proxyHost, proxyPort;
	@FXML
	private CheckBox proxyEnable;
	@FXML
	protected ComboBox<String> soundDevice;
	@FXML
	private ComboBox<Integer> hardsid6581, hardsid8580, samplingRate;
	@FXML
	private ComboBox<SamplingMethod> samplingMethod;
	@FXML
	protected RadioButton playMP3, playEmulation;
	@FXML
	private Button mp3Browse;

	private ObservableList<resid_builder.resid.ISIDDefs.SamplingMethod> samplingMethods = FXCollections
			.<resid_builder.resid.ISIDDefs.SamplingMethod> observableArrayList();

	private ObservableList<String> soundDevices = FXCollections
			.<String> observableArrayList();

	private boolean duringInitialization;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		duringInitialization = true;
		soundDevice.setItems(soundDevices);
		soundDevices.addAll(getBundle().getString("SOUNDCARD"), getBundle()
				.getString("HARDSID4U"), getBundle().getString("WAV_RECORDER"),
				getBundle().getString("MP3_RECORDER"),
				getBundle().getString("COMPARE_TO_MP3"));
		DriverSettings driverSettings = getConsolePlayer().getDriverSettings();
		Output out = driverSettings.getOutput();
		Emulation sid = driverSettings.getEmulation();
		if (out == Output.OUT_SOUNDCARD && sid == Emulation.EMU_RESID) {
			soundDevice.getSelectionModel().select(0);
		} else if (out == Output.OUT_NULL && sid == Emulation.EMU_HARDSID) {
			soundDevice.getSelectionModel().select(1);
		} else if (out == Output.OUT_LIVE_WAV && sid == Emulation.EMU_RESID) {
			soundDevice.getSelectionModel().select(2);
		} else if (out == Output.OUT_LIVE_MP3 && sid == Emulation.EMU_RESID) {
			soundDevice.getSelectionModel().select(3);
		} else if (out == Output.OUT_COMPARE && sid == Emulation.EMU_RESID) {
			soundDevice.getSelectionModel().select(4);
		} else {
			soundDevice.getSelectionModel().select(0);
		}
		hardsid6581.getSelectionModel().select(
				Integer.valueOf(getConfig().getEmulation().getHardsid6581()));
		hardsid8580.getSelectionModel().select(
				Integer.valueOf(getConfig().getEmulation().getHardsid8580()));
		samplingRate.getSelectionModel().select(
				Integer.valueOf(getConfig().getAudio().getFrequency()));
		samplingMethod.setItems(samplingMethods);
		samplingMethods
				.addAll(SamplingMethod.DECIMATE, SamplingMethod.RESAMPLE);
		samplingMethod.getSelectionModel().select(
				getConfig().getAudio().getSampling());
		mp3.setText(getConfig().getAudio().getMp3File());
		playMP3.setSelected(getConfig().getAudio().isPlayOriginal());
		playEmulation.setSelected(!getConfig().getAudio().isPlayOriginal());

		proxyEnable.setSelected(getConsolePlayer().getConfig().getSidplay2()
				.isEnableProxy());
		proxyHost.setText(getConsolePlayer().getConfig().getSidplay2()
				.getProxyHostname());
		proxyHost.setEditable(proxyEnable.isSelected());
		proxyPort.setText(String.valueOf(getConsolePlayer().getConfig()
				.getSidplay2().getProxyPort()));
		proxyPort.setEditable(proxyEnable.isSelected());

		duringInitialization = false;
	}

	@FXML
	private void setSoundDevice() {
		switch (soundDevice.getSelectionModel().getSelectedIndex()) {
		case 0:
			setOutputDevice(Output.OUT_SOUNDCARD, Emulation.EMU_RESID);
			break;

		case 1:
			setOutputDevice(Output.OUT_NULL, Emulation.EMU_HARDSID);
			break;

		case 2:
			setOutputDevice(Output.OUT_LIVE_WAV, Emulation.EMU_RESID);
			break;

		case 3:
			setOutputDevice(Output.OUT_LIVE_MP3, Emulation.EMU_RESID);
			break;
		case 4:
			setOutputDevice(Output.OUT_COMPARE, Emulation.EMU_RESID);
			break;

		}
		restart();
	}

	@FXML
	private void setSid6581() {
		getConfig().getEmulation().setHardsid6581(
				hardsid6581.getSelectionModel().getSelectedItem());
		restart();
	}

	@FXML
	private void setSid8580() {
		getConfig().getEmulation().setHardsid8580(
				hardsid8580.getSelectionModel().getSelectedItem());
		restart();
	}

	@FXML
	private void setSamplingRate() {
		getConfig().getAudio().setFrequency(
				samplingRate.getSelectionModel().getSelectedItem());
		restart();
	}

	@FXML
	private void setSamplingMethod() {
		getConfig().getAudio().setSampling(
				samplingMethod.getSelectionModel().getSelectedItem());
		getConsolePlayer().updateSidEmulation();
	}

	@FXML
	private void playEmulatedSound() {
		setPlayOriginal(false);
	}

	@FXML
	private void playRecordedSound() {
		setPlayOriginal(true);
	}

	@FXML
	private void setRecording() {
		getConfig().getAudio().setMp3File(mp3.getText());
	}

	@FXML
	private void doBrowse() {
		final FileChooser fileDialog = new FileChooser();
		final FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"MP3 file (*.mp3)", "*.mp3");
		fileDialog.getExtensionFilters().add(extFilter);
		final File file = fileDialog.showOpenDialog(mp3.getScene().getWindow());
		if (file != null) {
			mp3.setText(file.getAbsolutePath());
			getConfig().getAudio().setMp3File(mp3.getText());
			restart();
		}
	}

	@FXML
	private void setEnableProxy() {
		proxyHost.setEditable(proxyEnable.isSelected());
		proxyPort.setEditable(proxyEnable.isSelected());
		getConfig().getSidplay2().setEnableProxy(proxyEnable.isSelected());
	}

	@FXML
	private void setProxyHost() {
		getConfig().getSidplay2().setProxyHostname(proxyHost.getText());
	}

	@FXML
	private void setProxyPort() {
		getConfig().getSidplay2().setProxyPort(
				proxyPort.getText().length() > 0 ? Integer.valueOf(proxyPort
						.getText()) : 80);
	}

	protected void restart() {
		// replay last tune
		if (!duringInitialization) {
			getConsolePlayer().playTune(getPlayer().getTune(), null);
		}
	}

	private void setOutputDevice(final Output device, final Emulation emu) {
		getConsolePlayer().getDriverSettings().setOutput(device);
		getConsolePlayer().getDriverSettings().setEmulation(emu);
	}

	protected void setPlayOriginal(final boolean playOriginal) {
		getConfig().getAudio().setPlayOriginal(playOriginal);
		if (getConsolePlayer().getDriverSettings().getDevice() instanceof CmpMP3File) {
			((CmpMP3File) getConsolePlayer().getDriverSettings().getDevice())
					.setPlayOriginal(playOriginal);
		}
	}

}
