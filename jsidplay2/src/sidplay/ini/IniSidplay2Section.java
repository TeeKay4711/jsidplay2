package sidplay.ini;

/**
 * SIDPlay2 section of the INI file.
 * 
 * @author Ken H�ndel
 * 
 */
public class IniSidplay2Section extends IniSection {

	/**
	 * SIDPlay2 section of the INI file.
	 * 
	 * @param ini
	 *            INI file reader
	 */
	protected IniSidplay2Section(final IniReader ini) {
		super(ini);
	}

	/**
	 * Get INI file version.
	 * 
	 * @return INI file version
	 */
	public final int getVersion() {
		return iniReader.getPropertyInt("SIDPlay2", "Version",
				IniConfig.REQUIRED_CONFIG_VERSION);
	}

	/**
	 * Getter of the enable of the Songlengths database.
	 * 
	 * @return Is the Songlengths database enabled?
	 */
	public final boolean isEnableDatabase() {
		return iniReader.getPropertyBool("SIDPlay2", "EnableDatabase", true);
	}

	/**
	 * Setter of the enable of the Songlengths database.
	 * 
	 * @param enable
	 *            the enable of the Songlengths database
	 */
	public final void setEnableDatabase(final boolean enable) {
		iniReader.setProperty("SIDPlay2", "EnableDatabase", enable);
	}

	/**
	 * Getter of the default play length.
	 * 
	 * @return default play length
	 */
	public final int getPlayLength() {
		return iniReader.getPropertyTime("SIDPlay2", "Default Play Length",
				3 * 60 + 30);
	}

	/**
	 * Setter of the default play length.
	 * 
	 * @param length
	 *            default play length
	 */
	public final void setPlayLength(final int playLength) {
		iniReader.setProperty("SIDPlay2", "Default Play Length", String.format(
				"%02d:%02d", (playLength / 60), (playLength % 60)));
	}

	/**
	 * Getter of the record length.
	 * 
	 * @return the record length
	 */
	public final int getRecordLength() {
		return iniReader.getPropertyTime("SIDPlay2", "Default Record Length",
				3 * 60 + 30);
	}

	/**
	 * Getter of the HVMEC collection directory.
	 * 
	 * @return the HVMEC collection directory
	 */
	public final String getHVMEC() {
		return iniReader.getPropertyString("SIDPlay2", "HVMEC Dir", null);
	}

	/**
	 * Setter of the HVMEC directory.
	 * 
	 * @param hvmec
	 *            the HVMEC directory
	 */
	public final void setHVMEC(final String hvmec) {
		iniReader.setProperty("SIDPlay2", "HVMEC Dir", hvmec);
	}

	/**
	 * Getter of the CGSC collection directory.
	 * 
	 * @return the CGSC collection directory
	 */
	public final String getDemos() {
		return iniReader.getPropertyString("SIDPlay2", "DEMOS Dir", null);
	}

	/**
	 * Setter of the Demos directory.
	 * 
	 * @param demos
	 *            the Demos directory
	 */
	public final void setDemos(final String demos) {
		iniReader.setProperty("SIDPlay2", "DEMOS Dir", demos);
	}

	/**
	 * Getter of the Mags directory.
	 * 
	 * @return the Mags directory
	 */
	public final String getMags() {
		return iniReader.getPropertyString("SIDPlay2", "MAGS Dir", null);
	}

	/**
	 * Setter of the Mags directory.
	 * 
	 * @param mags
	 *            the Mags directory
	 */
	public final void setMags(final String mags) {
		iniReader.setProperty("SIDPlay2", "MAGS Dir", mags);
	}

	/**
	 * Getter of the CGSC collection directory.
	 * 
	 * @return the CGSC collection directory
	 */
	public final String getCgsc() {
		return iniReader.getPropertyString("SIDPlay2", "CGSC Dir", null);
	}

	/**
	 * Setter of the CGSC collection directory.
	 * 
	 * @param cgsc
	 *            the CGSC collection directory
	 */
	public final void setCgsc(final String cgsc) {
		iniReader.setProperty("SIDPlay2", "CGSC Dir", cgsc);
	}

	/**
	 * Getter of the HVSC collection directory.
	 * 
	 * @return the HVSC collection directory
	 */
	public final String getHvsc() {
		return iniReader.getPropertyString("SIDPlay2", "HVSC Dir", null);
	}

	/**
	 * Setter of the HVSC collection directory.
	 * 
	 * @param hvsc
	 *            the HVSC collection directory
	 */
	public final void setHvsc(final String hvsc) {
		iniReader.setProperty("SIDPlay2", "HVSC Dir", hvsc);
	}

	/**
	 * Do we play a single song per tune?
	 * 
	 * @return play a single song per tune
	 */
	public final boolean isSingle() {
		return iniReader.getPropertyBool("SIDPlay2", "SingleTrack", false);
	}

	/**
	 * setter to play a single song per tune.
	 * 
	 * @param singleSong
	 *            play a single song per tune
	 */
	public final void setSingle(final boolean singleSong) {
		iniReader.setProperty("SIDPlay2", "SingleTrack", singleSong);
	}

	/**
	 * Getter of the download URL for SOASC MOS6581R2.
	 * 
	 * @return the download URL for SOASC MOS6581R2
	 */
	public final String getSoasc6581R2() {
		return iniReader.getPropertyString("SIDPlay2", "SOASC_6581R2", null);
	}

	/**
	 * Setter of the download URL for SOASC MOS6581R2.
	 * 
	 * @param soasc6581R2
	 *            the download URL for SOASC MOS6581R2
	 */
	public final void setSoasc6581R2(final String soasc6581R2) {
		iniReader.setProperty("SIDPlay2", "SOASC_6581R2", soasc6581R2);
	}

	/**
	 * Getter of the download URL for SOASC MOS6581R4.
	 * 
	 * @return the download URL for SOASC MOS6581R4
	 */
	public final String getSoasc6581R4() {
		return iniReader.getPropertyString("SIDPlay2", "SOASC_6581R4", null);
	}

	/**
	 * Setter of the download URL for SOASC MOS6581R4.
	 * 
	 * @param soascr6581R4
	 *            the download URL for SOASC MOS6581R4
	 */
	public final void setSoasc6581R4(final String soasc6581R4) {
		iniReader.setProperty("SIDPlay2", "SOASC_6581R2", soasc6581R4);
	}

	/**
	 * Getter of the download URL for SOASC CSG8580R5.
	 * 
	 * @return the download URL for SOASC CSG8580R5
	 */
	public final String getSoasc8580R5() {
		return iniReader.getPropertyString("SIDPlay2", "SOASC_8580R5", null);
	}

	/**
	 * Setter of the download URL for SOASC CSG8580R5.
	 * 
	 * @param soasc8580R5
	 *            the download URL for SOASC CSG8580R5
	 */
	public final void setSoasc8580R5(final String soasc8580R5) {
		iniReader.setProperty("SIDPlay2", "SOASC_8580R5", soasc8580R5);
	}

	/**
	 * Do we enable proxy for SOASC downloads?
	 * 
	 * @return enable proxy for SOASC downloads
	 */
	public final boolean isEnableProxy() {
		return iniReader.getPropertyBool("SIDPlay2", "EnableProxy", false);
	}

	/**
	 * Setter to enable proxy for SOASC downloads.
	 * 
	 * @param enable
	 *            enable proxy for SOASC downloads
	 */
	public final void setEnableProxy(final boolean enable) {
		iniReader.setProperty("SIDPlay2", "EnableProxy", enable);
	}

	/**
	 * Getter of the proxy hostname for SOASC downloads.
	 * 
	 * @return the proxy hostname for SOASC downloads
	 */
	public final String getProxyHostname() {
		return iniReader.getPropertyString("SIDPlay2", "ProxyHostname", null);
	}

	/**
	 * Setter of the proxy hostname for SOASC downloads.
	 * 
	 * @param hostname
	 *            the proxy hostname for SOASC downloads
	 */
	public final void setProxyHostname(final String hostname) {
		iniReader.setProperty("SIDPlay2", "ProxyHostname", hostname);
	}

	/**
	 * Getter of the proxy port for SOASC downloads.
	 * 
	 * @return the proxy port for SOASC downloads
	 */
	public final int getProxyPort() {
		return iniReader.getPropertyInt("SIDPlay2", "ProxyPort", 80);
	}

	/**
	 * Setter of the proxy port for SOASC downloads.
	 * 
	 * @param port
	 *            the proxy port for SOASC downloads
	 */
	public final void setProxyPort(final int port) {
		iniReader.setProperty("SIDPlay2", "ProxyPort", port);
	}

	/**
	 * Getter of the last accessed directory in the file browser.
	 * 
	 * @return the last accessed directory in the file browser
	 */
	public final String getLastDirectory() {
		return iniReader.getPropertyString("SIDPlay2", "Last Directory", null);
	}

	/**
	 * Setter of the last accessed directory in the file browser.
	 * 
	 * @param lastDir
	 *            the last accessed directory in the file browser
	 */
	public final void setLastDirectory(final String lastDir) {
		iniReader.setProperty("SIDPlay2", "Last Directory", lastDir);
	}

	/**
	 * Getter of the temporary directory for JSIDPlay2.
	 * 
	 * Default is <homeDir>/.jsidplay2
	 * 
	 * @return the temporary directory for JSIDPlay2
	 */
	public final String getTmpDir() {
		return iniReader.getPropertyString(
				"SIDPlay2",
				"Temp Dir",
				System.getProperty("user.home")
						+ System.getProperty("file.separator") + ".jsidplay2");
	}

	/**
	 * Setter of the temporary directory for JSIDPlay2.
	 * 
	 * @param path
	 *            the temporary directory for JSIDPlay2
	 */
	public final void setTmpDir(final String path) {
		iniReader.setProperty("SIDPlay2", "Temp Dir", path);
	}

	/**
	 * Getter of the application window X position.
	 * 
	 * @return the application window X position
	 */
	public final int getFrameX() {
		return iniReader.getPropertyInt("SIDPlay2", "Frame X", -1);
	}

	/**
	 * Setter of the application window X position.
	 * 
	 * @param x
	 *            application window X position
	 */
	public final void setFrameX(final int x) {
		iniReader.setProperty("SIDPlay2", "Frame X", x);
	}

	/**
	 * Getter of the application window Y position.
	 * 
	 * @return the application window Y position
	 */
	public final int getFrameY() {
		return iniReader.getPropertyInt("SIDPlay2", "Frame Y", -1);
	}

	/**
	 * Setter of the application window Y position.
	 * 
	 * @param y
	 *            application window Y position
	 */
	public final void setFrameY(final int y) {
		iniReader.setProperty("SIDPlay2", "Frame Y", y);
	}

	/**
	 * Getter of the application window width.
	 * 
	 * @return the application window width
	 */
	public final int getFrameWidth() {
		return iniReader.getPropertyInt("SIDPlay2", "Frame Width", -1);
	}

	/**
	 * Setter of the application window width.
	 * 
	 * @param width
	 *            application window width
	 */
	public final void setFrameWidth(final int width) {
		iniReader.setProperty("SIDPlay2", "Frame Width", width);
	}

	/**
	 * Getter of the application window height.
	 * 
	 * @return the application window height
	 */
	public final int getFrameHeight() {
		return iniReader.getPropertyInt("SIDPlay2", "Frame Height", -1);
	}

	/**
	 * Setter of the application window height.
	 * 
	 * @param height
	 *            application window height
	 */
	public final void setFrameHeight(final int height) {
		iniReader.setProperty("SIDPlay2", "Frame Height", height);
	}

}