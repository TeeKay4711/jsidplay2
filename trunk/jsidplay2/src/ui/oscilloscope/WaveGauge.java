package ui.oscilloscope;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TitledPane;
import libsidplay.common.SIDEmu;
import resid_builder.ReSID;
import resid_builder.resid.SID;
import resid_builder.resid.WaveformGenerator;

public final class WaveGauge extends SIDGauge implements Initializable {

	@FXML
	private TitledPane border;
	@FXML
	private Canvas area;

	@Override
	protected Canvas getArea() {
		return area;
	}

	@Override
	protected TitledPane getBorder() {
		return border;
	}

	@Override
	public void sample(SIDEmu sidemu) {
		if (!(sidemu instanceof ReSID)) {
			accumulate((byte) 0);
			return;
		}

		SID sid = ((ReSID) sidemu).sid();
		final WaveformGenerator wave = sid.voice[getVoice()].wave;
		int sampleValue = wave.readOSC() & 0xff;
		accumulate(sampleValue / 255f);
	}

	@Override
	public void updateGauge(SIDEmu sidemu) {
		super.updateGauge();
		if (sidemu != null) {
			final byte wf = sidemu.readInternalRegister(4 + 7 * getVoice());
			final byte filt = sidemu.readInternalRegister(0x17);
			setText(String.format(localizer.getString("WAVE") + " %X %s%s%s%s",
					wf >> 4 & 0xf, (wf & 2) != 0 ? "S" : "",
					(wf & 4) != 0 ? "R" : "", (wf & 8) != 0 ? "T" : "",
					(filt & 1 << getVoice()) != 0 ? "F" : ""));
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
	}
}