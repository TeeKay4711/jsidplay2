/**
 *                           Sid Builder Classes
 *                           -------------------
 *  begin                : Sat May 6 2001
 *  copyright            : (C) 2001 by Simon White
 *  email                : s_a_white@email.com
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 * @author Ken Händel
 *
 */
package libsidplay.common;

import sidplay.ini.intf.IAudioSection;


/**
 * @author Ken Händel
 * 
 *         Inherit this class to create a new SID emulations for libsidplay2.
 */
public abstract class SIDBuilder {
	public void start() {};
	public abstract SIDEmu lock(EventScheduler context, SIDEmu device, ChipModel model);
	public abstract void unlock(SIDEmu device);
	public abstract void setBalance(int i, IAudioSection audio);
	public abstract void setMixerVolume(int sidNum, IAudioSection audio);
	public abstract int getNumDevices();
}
