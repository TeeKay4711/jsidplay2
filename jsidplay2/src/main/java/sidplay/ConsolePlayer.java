package sidplay;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer.Info;

import libsidplay.Player;
import libsidplay.common.CPUClock;
import libsidplay.common.ChipModel;
import libsidplay.common.Emulation;
import libsidplay.common.Engine;
import libsidplay.sidtune.SidTune;
import libsidplay.sidtune.SidTuneError;
import libsidutils.PathUtils;
import libsidutils.SidDatabase;
import sidplay.audio.Audio;
import sidplay.audio.JavaSound;
import sidplay.audio.JavaSound.Device;
import sidplay.consoleplayer.ConsoleIO;
import sidplay.consoleplayer.TimeConverter;
import sidplay.consoleplayer.VerboseValidator;
import sidplay.ini.IniConfig;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

@Parameters(resourceBundle = "sidplay.consoleplayer.ConsolePlayer")
public class ConsolePlayer {
	@Parameter(names = { "--help", "-h" }, descriptionKey = "USAGE", help = true)
	private Boolean help = Boolean.FALSE;

	@Parameter(names = "--cpuDebug", hidden = true, descriptionKey = "DEBUG")
	private Boolean cpuDebug = Boolean.FALSE;

	@Parameter(names = { "--audio", "-a" }, descriptionKey = "DRIVER")
	private Audio audio = Audio.SOUNDCARD;

	@Parameter(names = { "--bufferSize", "-B" }, descriptionKey = "BUFFER_SIZE")
	private int bufferSize  = 2500;

	@Parameter(names = { "--deviceIndex", "-A" }, descriptionKey = "DEVICEINDEX")
	private Integer deviceIdx;

	@Parameter(names = { "--engine", "-E" }, descriptionKey = "ENGINE")
	private Engine engine = Engine.EMULATION;

	@Parameter(names = { "--defaultEmulation", "-e" }, descriptionKey = "DEFAULT_EMULATION")
	private Emulation defaultEmulation = Emulation.RESID;

	@Parameter(names = { "--recordingFilename", "-r" }, descriptionKey = "RECORDING_FILENAME")
	private String recordingFilename = "jsidplay2";

	@Parameter(names = { "--startSong", "-o" }, descriptionKey = "START_SONG")
	private Integer song = null;

	@Parameter(names = { "--loop", "-l" }, descriptionKey = "LOOP")
	private Boolean loop = Boolean.FALSE;

	@Parameter(names = { "--single", "-s" }, descriptionKey = "SINGLE")
	private Boolean single = Boolean.FALSE;

	@Parameter(names = { "--frequency", "-f" }, descriptionKey = "FREQUENCY")
	private Integer frequency = 48000;

	@Parameter(names = { "--dualSID", "-d" }, descriptionKey = "DUAL_SID")
	private Boolean dualSID = Boolean.FALSE;

	@Parameter(names = { "--thirdSID", "-D" }, descriptionKey = "THIRD_SID")
	private Boolean thirdSID = Boolean.FALSE;

	@Parameter(names = { "--forceClock", "-c" }, descriptionKey = "FORCE_CLOCK")
	private CPUClock forceClock = null;

	@Parameter(names = { "--defaultClock", "-k" }, descriptionKey = "DEFAULT_CLOCK")
	private CPUClock defaultClock = CPUClock.PAL;

	@Parameter(names = { "--disableFilter", "-i" }, descriptionKey = "DISABLE_FILTER")
	private Boolean disableFilter = Boolean.FALSE;

	@Parameter(names = { "--disableStereoFilter", "-j" }, descriptionKey = "DISABLE_STEREO_FILTER")
	private Boolean disableStereoFilter = Boolean.FALSE;

	@Parameter(names = { "--disable3rdSidFilter", "-J" }, descriptionKey = "DISABLE_3RD_SID_FILTER")
	private Boolean disable3rdSIDFilter = Boolean.FALSE;

	@Parameter(names = { "--forceModel", "-m" }, descriptionKey = "FORCE_MODEL")
	private ChipModel forceModel = null;

	@Parameter(names = { "--defaultModel", "-u" }, descriptionKey = "DEFAULT_MODEL")
	private ChipModel defaultModel = ChipModel.MOS6581;

	@Parameter(names = { "--startTime", "-t" }, descriptionKey = "START_TIME", converter = TimeConverter.class)
	private Integer startTime = 0;

	@Parameter(names = { "--defaultLength", "-g" }, descriptionKey = "DEFAULT_LENGTH", converter = TimeConverter.class)
	private Integer defaultLength = 0;

	@Parameter(names = { "--enableSidDatabase", "-n" }, descriptionKey = "ENABLE_SID_DATABASE", arity = 1)
	private Boolean enableSidDatabase = Boolean.TRUE;

	@Parameter(names = { "--verbose", "-v" }, descriptionKey = "VERBOSE", validateWith = VerboseValidator.class)
	private Integer verbose = 0;

	@Parameter(names = { "--quiet", "-q" }, descriptionKey = "QUIET")
	private Boolean quiet = Boolean.FALSE;

	@Parameter(description = "filename")
	private List<String> filenames = new ArrayList<String>();

	private ConsolePlayer(String[] args) {
		try {
			JCommander commander = new JCommander(this, args);
			commander.setProgramName(getClass().getName());
			commander.setCaseSensitiveOptions(true);
			if (help || filenames.size() != 1) {
				commander.usage();
				exit(1);
			}
		} catch (ParameterException e) {
			System.err.println(e.getMessage());
			exit(1);
		}
		// Cannot loop while recording audio files
		if (isRecording()) {
			loop = false;
		}
		final IniConfig config = new IniConfig(true);
		config.getSidplay2().setLoop(loop);
		config.getSidplay2().setSingle(single);
		config.getSidplay2().setDefaultPlayLength(defaultLength);
		config.getSidplay2().setEnableDatabase(enableSidDatabase);
		config.getAudio().setAudio(audio);
		config.getAudio().setFrequency(frequency);
		config.getAudio().setBufferSize(bufferSize);
		config.getEmulation().setEngine(engine);
		config.getEmulation().setDefaultEmulation(defaultEmulation);
		config.getEmulation().setForceStereoTune(dualSID);
		config.getEmulation().setForce3SIDTune(thirdSID);
		config.getEmulation().setUserClockSpeed(forceClock);
		config.getEmulation().setDefaultClockSpeed(defaultClock);
		config.getEmulation().setUserSidModel(forceModel);
		config.getEmulation().setDefaultSidModel(defaultModel);
		config.getEmulation().setFilter(!disableFilter);
		config.getEmulation().setStereoFilter(!disableStereoFilter);
		config.getEmulation().setThirdSIDFilter(!disable3rdSIDFilter);

		final Player player = new Player(config);
		try {
			final SidTune tune = SidTune.load(new File(filenames.get(0)));
			player.setTune(tune);
			tune.setSelectedSong(song);
			player.setRecordingFilenameProvider((theTune) -> {
				File file = new File(recordingFilename);
				String filename = new File(file.getParentFile(), PathUtils
						.getBaseNameNoExt(file.getName())).getAbsolutePath();
				if (theTune.getInfo().getSongs() > 1) {
					filename += String.format("-%02d", theTune.getInfo()
							.getCurrentSong());
				}
				return filename;
			});
			player.setDebug(cpuDebug);
			player.getTimer().setStart(startTime);

			// check song length
			if (defaultLength == 0) {
				setSIDDatabase(player);
				int length = player.getSidDatabaseInfo(db -> db
						.getSongLength(tune));
				if (isRecording()
						&& (!config.getSidplay2().isEnableDatabase() || length == 0)) {
					System.err
							.println("ERROR: unknown song length in record mode"
									+ " (please use option --defaultLength or configure song length database)");
					exit(1);
				}
			}
			ConsoleIO consoleIO = new ConsoleIO(config, filenames.get(0));
			player.setMenuHook(obj -> consoleIO.menu(obj, verbose, quiet,
					System.out));
			player.setInteractivityHook(obj -> consoleIO.decodeKeys(obj));

			player.startC64();
		} catch (IOException | SidTuneError e) {
			System.err.println(e.getMessage());
			exit(1);
		}

		// Set SOUNDCARD audio device
		if (deviceIdx != null) {
			JavaSound js = (JavaSound) Audio.SOUNDCARD.getAudioDriver();
			ObservableList<Device> devices = JavaSound.getDevices();
			try {
				Info info = devices.get(deviceIdx).getInfo();
				js.setAudioDevice(info);
			} catch (IndexOutOfBoundsException | LineUnavailableException e) {
				int deviceIdx = 0;
				for (Device device : JavaSound.getDevices()) {
					System.err.printf("device %d = %s (%s)\n", (deviceIdx++),
							device.getInfo().getName(), device.getInfo()
									.getDescription());
				}
				System.err.println(e.getMessage());
				exit(1);
			}
		}
	}

	private void setSIDDatabase(final Player player) {
		String hvscRoot = player.getConfig().getSidplay2().getHvsc();
		if (hvscRoot != null) {
			File file = new File(hvscRoot, SidDatabase.SONGLENGTHS_FILE);
			try (FileInputStream input = new FileInputStream(file)) {
				player.setSidDatabase(new SidDatabase(input));
			} catch (IOException e) {
				// silently ignored!
			}
		}
	}

	private boolean isRecording() {
		return audio == Audio.WAV || audio == Audio.MP3
				|| audio == Audio.LIVE_WAV || audio == Audio.LIVE_MP3;
	}

	private void exit(int rc) {
		try {
			System.out.println("Press <enter> to exit the player!");
			System.in.read();
			System.exit(rc);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

	public static void main(final String[] args) {
		new ConsolePlayer(args);
	}

}
