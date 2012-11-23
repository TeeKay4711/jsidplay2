package libsidplay.sidtune;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import lowlevel.ID3V2Decoder;

/**
 * Special MP3 tune implementation. This is not a program present in the C64
 * memory, it is just a file played back by the jump3r library. However this C64
 * emulator requires tunes so this is a dummy to meet that requirement.
 * 
 * @author Ken
 * 
 */
public class MP3Tune extends SidTune {

	@Override
	public int placeProgramInMemory(byte[] c64buf) {
		// No program to load
		return -1;
	}

	@Override
	public void save(String destFileName, boolean overWriteFlag)
			throws IOException {
		// Saving is not possible
	}

	@Override
	public ArrayList<String> identify() {
		ArrayList<String> names = new ArrayList<String>();
		// The player is called jump3r ;-)
		names.add("jump3r");
		return names;
	}

	@Override
	public long getInitDelay() {
		// MP3 can play immediately
		return 0;
	}

	public static final SidTune load(final File f) throws SidTuneError {
		final MP3Tune s = new MP3Tune();
		// fill out some minimal information of an MP3 tune
		s.info.dataFileLen = (int) f.length();
		s.info.file = f;
		s.info.startSong = 1;
		s.info.songs = 1;
		ID3V2Decoder decoder = new ID3V2Decoder();
		try {
			decoder.read(new RandomAccessFile(f, "r"));
			s.info.infoString[0] = decoder.getTitle();
			s.info.infoString[1] = decoder.getAlbumInterpret();
			s.info.infoString[2] = decoder.getAlbum();
			s.info.startSong = Integer.valueOf(decoder.getTrack());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return s;
	}

}
