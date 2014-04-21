package libsidutils;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import libsidplay.sidtune.SidTune;
import sidplay.ini.IniReader;

public class SidDatabase {
	public static final String SONGLENGTHS_FILE = "DOCUMENTS/Songlengths.txt";
	private static final Pattern TIME_VALUE = Pattern
			.compile("([0-9]{1,2}):([0-9]{2})(?:\\(.*)?");

	private final IniReader database;

	public SidDatabase(final InputStream input) {
		try {
			database = new IniReader(input);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private int parseTimeStamp(final String arg) {
		Matcher m = TIME_VALUE.matcher(arg);
		if (!m.matches()) {
			System.out.println("Failed to parse: " + arg);
			return 0;
		}

		return Integer.parseInt(m.group(1)) * 60 + Integer.parseInt(m.group(2));
	}

	public int getFullSongLength(final SidTune tune) {
		int length = 0;
		final String md5 = tune.getMD5Digest();
		for (int i = 1; i <= tune.getInfo().songs; i++) {
			length += length(md5, i);
		}
		return length;
	}

	public int length(final SidTune tune) {
		final int song = tune.getInfo().currentSong;
		if (song == 0) {
			return -1;
		}
		final String md5 = tune.getMD5Digest();
		if (md5 == null) {
			return 0;
		}
		return length(md5, song);
	}

	private int length(final String md5, final int song) {
		final String value = database.getPropertyString("Database", md5, null);
		if (value == null) {
			return 0;
		}

		String[] times = value.split(" ");
		return parseTimeStamp(times[song - 1]);
	}

}