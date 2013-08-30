/**
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package libsidplay.sidtune;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.image.Image;
import de.schlichtherle.truezip.file.TFileInputStream;

/**
 * @author Ken H�ndel
 * 
 */
public abstract class SidTune {
	/**
	 * Also PSID file format limit.
	 */
	protected static final int SIDTUNE_MAX_SONGS = 256;

	protected static final int SIDTUNE_MAX_CREDIT_STRINGS = 10;

	public enum Speed {
		VBI(0), CIA_1A(60);

		private int val;

		Speed(int n) {
			this.val = n;
		}

		public int speedValue() {
			return val;
		}
	}

	/**
	 * Possible clock speeds of a SidTune.
	 */
	public enum Clock {
		UNKNOWN, PAL, NTSC, ANY
	}

	/**
	 * Possible models the SidTunes were meant to play on.
	 */
	public enum Model {
		UNKNOWN, MOS6581, MOS8580, ANY
	}

	/**
	 * SID types the SidTune may be compatible with.
	 */
	public enum Compatibility {
		PSIDv3, PSIDv2, PSIDv1, RSID, RSID_BASIC
	}

	protected SidTuneInfo info = new SidTuneInfo();

	protected final Speed songSpeed[] = new Speed[SIDTUNE_MAX_SONGS];

	/** Known SID names. MUS loader scans for these. */
	private static final String defaultMusNames[] = new String[] { ".mus",
			".str" };

	/**
	 * Constructor
	 */
	protected SidTune() {
		Arrays.fill(songSpeed, Speed.VBI);
	}

	/**
	 * Loads a file into a SidTune.
	 * 
	 * @param f
	 *            The file to load.
	 * 
	 * @return A SidTune instance of the specified file to load.
	 * 
	 * @throws IOException
	 * @throws SidTuneError
	 */
	public static SidTune load(final File f) throws IOException, SidTuneError {
		if (f == null) {
			return null;
		}
		if (f.getName().toLowerCase().endsWith(".mp3")) {
			return MP3Tune.load(f);
		}
		String fileName = f.getAbsolutePath();
		// ancient .mus and whatnot support.
		final byte[] fileBuf1 = loadFile(f);
		if (fileBuf1 == null || fileBuf1.length == 0) {
			/* no file found? return error. */
			return null;
		}

		SidTune s = PSid.load(fileBuf1);
		if (s != null) {
			s.info.dataFileLen = fileBuf1.length;
			s.info.file = f;
			return s;
		}

		s = Prg.load(fileName, fileBuf1);
		if (s != null) {
			s.info.dataFileLen = fileBuf1.length;
			s.info.file = f;
			return s;
		}

		s = P00.load(fileName, fileBuf1);
		if (s != null) {
			s.info.dataFileLen = fileBuf1.length;
			s.info.file = f;
			return s;
		}

		s = T64.load(fileName, fileBuf1);
		if (s != null) {
			s.info.dataFileLen = fileBuf1.length;
			s.info.file = f;
			return s;
		}

		/* load MUS */
		s = Mus.load(fileBuf1, null);
		if (s != null) {
			s.info.dataFileLen = fileBuf1.length;
			s.info.file = f;

			File stereoTune = getStereoTune(f);
			if (stereoTune != null) {
				final byte[] fileBuf2 = loadFile(stereoTune);
				if (fileBuf2 != null && fileBuf2.length > 0) {
					s = Mus.load(fileBuf1, fileBuf2);
					if (s != null) {
						s.info.dataFileLen = fileBuf1.length;
						s.info.file = f;
					}
				}
			}
			return s;
		}

		return null;
	}

	/**
	 * Get stereo music file by naming convention. Couples are *.mus/*.str or
	 * *_a.mus/*_b.mus .
	 * 
	 * @param f
	 *            file to get the stereo tune for.
	 * @return stereo file
	 */
	public static File getStereoTune(final File f) {
		final String fileName = f.getAbsolutePath();
		final File[] childs = f.getParentFile().listFiles();
		/* Try to load via .MUS / .STR naming convention */
		for (final String extension : defaultMusNames) {
			final String fileName2 = fileName.replaceFirst("\\.\\w+$",
					extension);
			for (int i = 0; i < childs.length; i++) {
				if (!fileName.equalsIgnoreCase(fileName2)
						&& childs[i].getAbsolutePath().equalsIgnoreCase(
								fileName2) && childs[i].exists()) {
					return childs[i];
				}
			}
		}

		// try to load a MUS stereo tune by _a.mus / _b.mus naming
		// convention.
		if (fileName.toLowerCase().endsWith("_a.mus")) {
			final String fileName2 = fileName.toLowerCase().replace("_a.mus",
					"_b.mus");
			for (int i = 0; i < childs.length; i++) {
				if (!fileName.equalsIgnoreCase(fileName2)
						&& childs[i].getAbsolutePath().equalsIgnoreCase(
								fileName2) && childs[i].exists()) {
					return childs[i];
				}
			}
		} else if (fileName.toLowerCase().endsWith("_b.mus")) {
			final String fileName2 = fileName.toLowerCase().replace("_b.mus",
					"_a.mus");
			for (int i = 0; i < childs.length; i++) {
				if (!fileName.equalsIgnoreCase(fileName2)
						&& childs[i].getAbsolutePath().equalsIgnoreCase(
								fileName2) && childs[i].exists()) {
					return childs[i];
				}
			}
		}
		return null;
	}

	/**
	 * Loads an InputStream into a SidTune.
	 * 
	 * @param stream
	 *            The InputStream to load.
	 * 
	 * @return A SidTune of the specified InputStream.
	 * 
	 * @throws IOException
	 *             If the stream cannot be read.
	 * @throws SidTuneError
	 */
	public static SidTune load(final InputStream stream) throws IOException,
			SidTuneError {
		// ancient .mus and whatnot support.
		final int maxLength = 65536;
		final byte[] fileBuf1 = new byte[65536];
		int count, len = 0;
		while (len < maxLength
				&& (count = stream.read(fileBuf1, len, maxLength - len)) >= 0) {
			len += count;
		}

		/* Avoid Arrays.copyOf(), not available on dalvik */
		byte[] buffer = new byte[len];
		for (int i = 0; i < len; i++) {
			buffer[i] = fileBuf1[i];
		}

		SidTune s = PSid.load(buffer);
		if (s != null) {
			return s;
		}

		s = Mus.load(buffer, null);
		if (s != null) {
			return s;
		}

		s = Prg.load(null, buffer);
		if (s != null) {
			return s;
		}

		return null;
	}

	/**
	 * Select sub-song (0 = default starting song) and return active song number
	 * out of [1,2,..,SIDTUNE_MAX_SONGS].
	 * 
	 * @param selectedSong
	 *            The chosen song.
	 * 
	 * @return The active song number.
	 */
	public int selectSong(final int selectedSong) {
		int song = selectedSong;
		if (selectedSong == 0 || selectedSong > info.songs) {
			song = info.startSong;
		}
		info.currentSong = song;
		return song;
	}

	/**
	 * Retrieve sub-song specific information. Beware! Still member-wise copy!
	 * 
	 * @return Sub-song specific information about the currently loaded tune.
	 */
	public final SidTuneInfo getInfo() {
		return info;
	}

	/**
	 * Copy program into C64 memory.
	 * 
	 * @param c64buf
	 * @return
	 * @throws SidTuneError
	 */
	public abstract int placeProgramInMemory(final byte[] c64buf);

	/**
	 * @param destFileName
	 *            Destination for the file.
	 * @param overWriteFlag
	 *            true = Overwrite existing file, false = Default<BR>
	 *            One could imagine an "Are you sure ?"-checkbox before
	 *            overwriting any file.
	 * @throws IOException
	 */
	public abstract void save(final String destFileName,
			final boolean overWriteFlag) throws IOException;

	/**
	 * Identify the player ID of a tune
	 * 
	 * @return the player IDs as a list
	 */
	public abstract ArrayList<String> identify();

	/**
	 * Does not affect status of object, and therefore can be used to load
	 * files. Error string is put into info.statusString, though.
	 * 
	 * @param f
	 *            The file to load.
	 * 
	 * @return The data of the loaded file.
	 * 
	 * @throws FileNotFoundException
	 *             if the file could not be found.
	 */
	private static byte[] loadFile(final File f) throws IOException {
		InputStream is;
		try {
			Class.forName("de.schlichtherle.truezip.file.TFileInputStream");
			is = new TFileInputStream(f);
		} catch (ClassNotFoundException e) {
			// skip ZIP support, if console player version without dependencies!
			is = new FileInputStream(f);
		}
		try (InputStream stream = is) {
			final int length = Math.min(65536, (int) f.length());
			final byte[] data = new byte[length];
			int count, pos = 0;
			while (pos < length
					&& (count = stream.read(data, pos, length - pos)) >= 0) {
				pos += count;
			}
			return data;
		}
	}

	/**
	 * Convert 32-bit PSID-style speed word to internal tables.
	 * 
	 * @param speed
	 *            The speed to convert.
	 */
	protected void convertOldStyleSpeedToTables(long speed) {
		for (int s = 0; s < SIDTUNE_MAX_SONGS; s++) {
			int i = s > 31 ? 31 : s;
			if ((speed & (1 << i)) != 0) {
				songSpeed[s] = Speed.CIA_1A;
			} else {
				songSpeed[s] = Speed.VBI;
			}
		}
	}

	/**
	 * Converts Petscii to Ascii.
	 * 
	 * @param petscii
	 *            The Petscii encoded data.
	 * @param startOffset
	 *            The offset to begin converting the Petscii data to Ascii.
	 * 
	 * @return The Petscii data converted to ASCII.
	 */
	protected static String convertPetsciiToAscii(final byte[] petscii,
			final int startOffset) {
		StringBuilder result = new StringBuilder();
		for (int idx = startOffset; idx < petscii.length; idx++) {
			final short out = _sidtune_CHRtab[petscii[idx] & 0xff];
			result.append((char) out);
		}
		return result.toString();
	}

	/**
	 * Petscii to Ascii conversion table.<BR>
	 * 
	 * CHR$ conversion table (0x01 = no output)
	 */
	private static final short _sidtune_CHRtab[] = { 0x0, 0x1, 0x1, 0x1, 0x1,
			0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x0d, 0x1, 0x1, 0x1, 0x1,
			0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1,
			0x1, 0x20, 0x21, 0x1, 0x23, 0x24, 0x25, 0x26, 0x27, 0x28, 0x29,
			0x2a, 0x2b, 0x2c, 0x2d, 0x2e, 0x2f, 0x30, 0x31, 0x32, 0x33, 0x34,
			0x35, 0x36, 0x37, 0x38, 0x39, 0x3a, 0x3b, 0x3c, 0x3d, 0x3e, 0x3f,
			0x40, 0x41, 0x42, 0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4a,
			0x4b, 0x4c, 0x4d, 0x4e, 0x4f, 0x50, 0x51, 0x52, 0x53, 0x54, 0x55,
			0x56, 0x57, 0x58, 0x59, 0x5a, 0x5b, 0x24, 0x5d, 0x20, 0x20,
			/* alternative: CHR$(92=0x5c) => ISO Latin-1(0xa3) */
			0x2d, 0x23, 0x7c, 0x2d, 0x2d, 0x2d, 0x2d, 0x7c, 0x7c, 0x5c, 0x5c,
			0x2f, 0x5c, 0x5c, 0x2f, 0x2f, 0x5c, 0x23, 0x5f, 0x23, 0x7c, 0x2f,
			0x58, 0x4f, 0x23, 0x7c, 0x23, 0x2b, 0x7c, 0x7c, 0x26, 0x5c,
			/* 0x80-0xFF */
			0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1,
			0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x1,
			0x1, 0x1, 0x1, 0x1, 0x1, 0x1, 0x20, 0x7c, 0x23, 0x2d, 0x2d, 0x7c,
			0x23, 0x7c, 0x23, 0x2f, 0x7c, 0x7c, 0x2f, 0x5c, 0x5c, 0x2d, 0x2f,
			0x2d, 0x2d, 0x7c, 0x7c, 0x7c, 0x7c, 0x2d, 0x2d, 0x2d, 0x2f, 0x5c,
			0x5c, 0x2f, 0x2f, 0x23, 0x2d, 0x23, 0x7c, 0x2d, 0x2d, 0x2d, 0x2d,
			0x7c, 0x7c, 0x5c, 0x5c, 0x2f, 0x5c, 0x5c, 0x2f, 0x2f, 0x5c, 0x23,
			0x5f, 0x23, 0x7c, 0x2f, 0x58, 0x4f, 0x23, 0x7c, 0x23, 0x2b, 0x7c,
			0x7c, 0x26, 0x5c, 0x20, 0x7c, 0x23, 0x2d, 0x2d, 0x7c, 0x23, 0x7c,
			0x23, 0x2f, 0x7c, 0x7c, 0x2f, 0x5c, 0x5c, 0x2d, 0x2f, 0x2d, 0x2d,
			0x7c, 0x7c, 0x7c, 0x7c, 0x2d, 0x2d, 0x2d, 0x2f, 0x5c, 0x5c, 0x2f,
			0x2f, 0x23 };

	public String getMD5Digest() {
		return null;
	}

	public int getSongSpeedArray() {
		int speed = 0;
		for (int i = 0; i < 32; ++i) {
			if (songSpeed[i] != SidTune.Speed.VBI) {
				speed |= 1 << i;
			}
		}
		return speed;
	}

	/**
	 * Gets the speed of the selected song.
	 * 
	 * @param selected
	 *            The song to get the speed of.
	 * 
	 * @return The speed of the selected song.
	 */
	public Speed getSongSpeed(int selected) {
		return songSpeed[selected - 1];
	}

	/**
	 * Return delay in C64 clocks before song init is done.
	 */
	public abstract long getInitDelay();

	public Image getImage() {
		return null;
	}

}
