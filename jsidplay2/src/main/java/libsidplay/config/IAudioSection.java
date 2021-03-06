package libsidplay.config;

import libsidplay.common.SamplingMethod;
import libsidplay.common.SamplingRate;
import sidplay.audio.Audio;

public interface IAudioSection {

	static final Audio DEFAULT_AUDIO = Audio.SOUNDCARD;
	static final int DEFAULT_DEVICE = 0;
	static final SamplingRate DEFAULT_SAMPLING_RATE = SamplingRate.MEDIUM;
	static final SamplingMethod DEFAULT_SAMPLING = SamplingMethod.DECIMATE;
	static final boolean DEFAULT_PLAY_ORIGINAL = false;
	static final float DEFAULT_MAIN_VOLUME = 0.f;
	static final float DEFAULT_SECOND_VOLUME = 0.f;
	static final float DEFAULT_THIRD_VOLUME = 0.f;
	static final float DEFAULT_MAIN_BALANCE = .5f;
	static final float DEFAULT_SECOND_BALANCE = .5f;
	static final float DEFAULT_THIRD_BALANCE = .5f;
	static final int DEFAULT_BUFFER_SIZE = 2500;

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

	int getDevice();

	void setDevice(int device);

	/**
	 * Getter of the Playback/Recording frequency.
	 * 
	 * @return Playback/Recording frequency
	 */
	SamplingRate getSamplingRate();

	/**
	 * Setter of the sampling rate.
	 * 
	 * @param sampligRate
	 *            sampling rate
	 */
	void setSamplingRate(SamplingRate sampligRate);

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

	default float getVolume(int sidNum) {
		switch (sidNum) {
		case 0:
			return getMainVolume();
		case 1:
			return getSecondVolume();
		case 2:
			return getThirdVolume();
		default:
			throw new RuntimeException("Maximum supported SIDS exceeded!");
		}
	}

	default float getBalance(int sidNum) {
		switch (sidNum) {
		case 0:
			return getMainBalance();
		case 1:
			return getSecondBalance();
		case 2:
			return getThirdBalance();
		default:
			throw new RuntimeException("Maximum supported SIDS exceeded!");
		}
	}
}