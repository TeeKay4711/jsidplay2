/**
 *                                  description
 *                                  -----------
 *  begin                : Sat Jul 8 2000
 *  copyright            : (C) 2000 by Simon White
 *  email                : s_a_white@email.com
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 * @author Ken H�ndel
 *
 */
package sidplay.audio;

import java.io.File;

import resid_builder.resid.ISIDDefs.SamplingMethod;

public class AudioConfig {
	protected int frameRate = 48000;
	protected int channels = 1;
	protected int bufferFrames = 4096;
	private SamplingMethod samplingMethod;
	private File tuneFile;
	private int songCount;
	private int currentSong;
	private String outputFilename;

	protected AudioConfig() {
	}

	/**
	 * This instance represents the requested audio configuration
	 * 
	 * @param frameRate
	 * @param channels
	 */
	public AudioConfig(int frameRate, int channels,
			SamplingMethod samplingMethod) {
		this.frameRate = frameRate;
		this.channels = channels;
		this.samplingMethod = samplingMethod;
	}

	/**
	 * Return currently requested framerate.
	 * 
	 * @return framerate
	 */
	public int getFrameRate() {
		return frameRate;
	}

	/**
	 * Return the desired size of buffer used at one time. This is often smaller
	 * than the whole buffer because doing this allows us to stay closer in sync
	 * with the audio production.
	 * 
	 * @return size of one chunk
	 */
	public int getChunkFrames() {
		return 1024 < bufferFrames ? 1024 : bufferFrames;
	}

	/**
	 * Return size of audio buffer in frames.
	 * 
	 * @return
	 */
	public int getBufferFrames() {
		return bufferFrames;
	}

	/**
	 * SID sampling method
	 * 
	 * @return
	 */
	public SamplingMethod getSamplingMethod() {
		return samplingMethod;
	}

	/**
	 * Get filename of the tune.
	 * 
	 * @return filename of the tune
	 */
	public final File getTuneFile() {
		return tuneFile;
	}

	/**
	 * Set filename of the tune.
	 * 
	 * @param filename
	 *            tune filename
	 */
	public final void setTuneFilename(final File file) {
		tuneFile = file;
	}

	/**
	 * Get song count of the tune.
	 * 
	 * @return song count
	 */
	public final int getSongCount() {
		return songCount;
	}

	/**
	 * Set song count of the tune.
	 * 
	 * @param count
	 *            number of songs
	 */
	public final void setSongCount(final int count) {
		songCount = count;
	}

	/**
	 * Get the current song number.
	 * 
	 * @return current song
	 */
	public final int getCurrentSong() {
		return currentSong;
	}

	/**
	 * Set the current song number.
	 * 
	 * @param current
	 *            the current song number
	 */
	public final void setCurrentSong(final int current) {
		currentSong = current;
	}

	/**
	 * Get an output filename proposal.
	 * 
	 * @return output filename
	 */
	public final String getOutputFilename() {
		return outputFilename;
	}

	/**
	 * Set an output filename proposal.
	 * 
	 * @param filename
	 *            output filename proposal
	 */
	public final void setOutputfilename(final String filename) {
		outputFilename = filename;
	}
}
