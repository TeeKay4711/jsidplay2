package sidplay.ini.intf;

import libsidplay.common.CPUClock;
import resid_builder.resid.ChipModel;

public interface IEmulationSection {

	/**
	 * Getter of the default clock speed.
	 * 
	 * @return the default clock speed
	 */
	public CPUClock getDefaultClockSpeed();

	/**
	 * Setter of the default clock speed.
	 * 
	 * @param speed
	 *            default clock speed
	 */
	public void setDefaultClockSpeed(CPUClock speed);

	/**
	 * Getter of user the clock speed.
	 * 
	 * @return the user clock speed
	 */
	public CPUClock getUserClockSpeed();

	/**
	 * Setter of the user clock speed.
	 * 
	 * @param speed
	 *            user clock speed
	 */
	public void setUserClockSpeed(CPUClock speed);

	/**
	 * Getter of the default SID model.
	 * 
	 * @return the default SID model
	 */
	public ChipModel getDefaultSidModel();

	/**
	 * Setter of the default SID model.
	 * 
	 * @param model the default SID model
	 */
	public void setDefaultSidModel(ChipModel model);

	/**
	 * Getter of the user SID model.
	 * 
	 * @return the user SID model
	 */
	public ChipModel getUserSidModel();

	/**
	 * Setter of the user SID model.
	 * 
	 * @param model
	 *            user SID model
	 */
	public void setUserSidModel(ChipModel model);

	/**
	 * Getter of the chip to be used for MOS6581.
	 * 
	 * @return the chip to be used for MOS6581
	 */
	public int getHardsid6581();

	/**
	 * Setter of the chip to be used for MOS6581.
	 * 
	 * @param chip
	 *            the chip to be used for MOS6581
	 */
	public void setHardsid6581(int chip);

	/**
	 * Getter of the chip to be used for CSG8580.
	 * 
	 * @return the chip to be used for CSG8580
	 */
	public int getHardsid8580();

	/**
	 * Setter of the chip to be used for CSG8580.
	 * 
	 * @param chip
	 *            the chip to be used for CSG8580
	 */
	public void setHardsid8580(int chip);

	/**
	 * Is SID filter enabled?
	 * 
	 * @return filter enabled
	 */
	public boolean isFilter();

	/**
	 * Setter of the filter enable.
	 * 
	 * @param enable
	 *            the filter enable
	 */
	public void setFilter(boolean enable);

	/**
	 * Getter of the filter setting of MOS6581.
	 * 
	 * @return the filter setting of MOS6581
	 */
	public String getFilter6581();

	/**
	 * Setter of the filter setting of MOS6581.
	 * 
	 * @param filterName
	 *            filter setting of MOS6581
	 */
	public void setFilter6581(String filterName);

	/**
	 * Getter of the filter setting of CSG8580.
	 * 
	 * @return the filter setting of CSG8580
	 */
	public String getFilter8580();

	/**
	 * Setter of the filter setting of CSG8680.
	 * 
	 * @param filterName
	 *            filter setting of CSG8680
	 */
	public void setFilter8580(String filterName);

	/**
	 * Getter of the enable SID digi-boost.
	 * 
	 * @return the enable SID digi-boost
	 */
	public boolean isDigiBoosted8580();

	/**
	 * setter of the enable SID digi-boost.
	 * 
	 * @param boost
	 *            the enable SID digi-boost
	 */
	public void setDigiBoosted8580(boolean boost);

	/**
	 * Getter of the stereo SID base address.
	 * 
	 * @return the stereo SID base address
	 */
	public int getDualSidBase();

	/**
	 * Setter of the stereo SID base address.
	 * 
	 * @param base
	 *            stereo SID base address
	 */
	public void setDualSidBase(int base);

	/**
	 * Getter of the forced playback stereo mode.
	 * 
	 * @return the forced playback stereo mode
	 */
	public boolean isForceStereoTune();

	/**
	 * Setter of the forced playback stereo mode.
	 * 
	 * @param force
	 *            forced playback stereo mode
	 */
	public void setForceStereoTune(boolean force);

	/**
	 * Getter of the the stereo SID model.
	 * 
	 * @return the stereo SID model
	 */
	public ChipModel getStereoSidModel();

	/**
	 * Setter of the the stereo SID model.
	 * 
	 * @param model
	 *            the the stereo SID model
	 */
	public void setStereoSidModel(ChipModel model);

}