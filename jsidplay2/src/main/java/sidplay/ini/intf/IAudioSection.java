package sidplay.ini.intf;

import libsidplay.common.SamplingMethod;
import sidplay.audio.Audio;

public interface IAudioSection {

	/**
	 * Getter of the audio to be used.
	 * 
	 * @return the audio to be used
	 */
	Audio getAudio();

	/**
	 * Setter of the audio to be used.
	 * 
	 * @param emulation
	 *            audio to be used
	 */
	void setAudio(Audio audio);

	/**
	 * Getter of the SID driver to play SIDs.
	 * 
	 * @return SID driver to play SIDs
	 */
	String getSidDriver();

	/**
	 * Setter of the SID driver to play SIDs.
	 * 
	 * @param sidDriver
	 *            SID driver to play SIDs
	 */
	void setSidDriver(final String sidDriver);

	int getDevice();

	void setDevice(int device);

	/**
	 * Getter of the Playback/Recording frequency.
	 * 
	 * @return Playback/Recording frequency
	 */
	int getFrequency();

	/**
	 * Setter of the Playback/Recording frequency.
	 * 
	 * @param freq
	 *            Playback/Recording frequency
	 */
	void setFrequency(int freq);

	/**
	 * Getter of the sampling method.
	 * 
	 * @return the sampling method
	 */
	SamplingMethod getSampling();

	/**
	 * Setter of the sampling method.
	 * 
	 * @param method
	 *            the sampling method
	 */
	void setSampling(SamplingMethod method);

	/**
	 * Do we play the recording?
	 * 
	 * @return play the recording
	 */
	boolean isPlayOriginal();

	/**
	 * Setter to play the recorded tune.
	 * 
	 * @param original
	 *            Play recorded (original) or emulated tune
	 */
	void setPlayOriginal(boolean original);

	/**
	 * Getter of the recorded tune filename.
	 * 
	 * @return the recorded tune filename
	 */
	String getMp3File();

	/**
	 * Setter of the recorded tune filename.
	 * 
	 * @param recording
	 *            the recorded tune filename
	 */
	void setMp3File(String recording);

	/**
	 * Getter of the main SID volume setting.
	 * 
	 * @return the main SID volume setting
	 */
	float getMainVolume();

	/**
	 * Setter of the main SID volume setting.
	 * 
	 * @param volume
	 *            the main SID volume setting
	 */
	void setMainVolume(float volume);

	/**
	 * Getter of the second SID volume setting.
	 * 
	 * @return the second SID volume setting
	 */
	float getSecondVolume();

	/**
	 * Setter of the second SID volume setting.
	 * 
	 * @param volume
	 *            the second SID volume setting
	 */
	void setSecondVolume(float volume);

	/**
	 * Getter of the third SID volume setting.
	 * 
	 * @return the third SID volume setting
	 */
	float getThirdVolume();

	/**
	 * Setter of the third SID setting.
	 * 
	 * @param volume
	 *            the third SID volume setting
	 */
	void setThirdVolume(float volume);

	/**
	 * Getter of the main SID balance setting (0 - left, 1 - right speaker).
	 * 
	 * @return the main SID balance setting
	 */
	float getMainBalance();

	/**
	 * Setter of the main SID balance setting (0 - left, 1 - right speaker).
	 * 
	 * @param volume
	 *            the main SID balance setting
	 */
	void setMainBalance(float balance);

	/**
	 * Getter of the second SID balance setting (0 - left, 1 - right speaker).
	 * 
	 * @return the second SID balance setting
	 */
	float getSecondBalance();

	/**
	 * Setter of the second SID balance setting (0 - left, 1 - right speaker).
	 * 
	 * @param volume
	 *            the second SID balance setting
	 */
	void setSecondBalance(float balance);

	/**
	 * Getter of the third SID balance setting (0 - left, 1 - right speaker).
	 * 
	 * @return the third SID balance setting
	 */
	float getThirdBalance();

	/**
	 * Setter of the third SID balance setting (0 - left, 1 - right speaker).
	 * 
	 * @param volume
	 *            the third SID balance setting
	 */
	void setThirdBalance(float balance);

	/**
	 * Getter of the output buffer size.
	 * 
	 * @return size of the output buffer
	 */
	int getBufferSize();

	/**
	 * Setter of the output buffer size.
	 * 
	 * @param bufferSize
	 *            output buffer size
	 */
	void setBufferSize(int bufferSize);
}