package sidplay.ini;

import sidplay.ini.intf.IConsoleSection;

/**
 * Console section of the INI file.
 * 
 * @author Ken Händel
 * 
 */
public class IniConsoleSection extends IniSection implements IConsoleSection {
	protected IniConsoleSection(IniReader iniReader) {
		super(iniReader);
	}

	/**
	 * Getter of the top left character of the console output.
	 * 
	 * @return the top left character of the console output
	 */
	@Override
	public final char getTopLeft() {
		return getChar(
				iniReader.getPropertyString("Console", "Char Top Left", "'+'"),
				'+');
	}

	@Override
	public void setTopLeft(char topLeft) {
		iniReader.setProperty("Console", "Char Top Left",
				String.valueOf((int) topLeft));
	}

	/**
	 * Getter of the top right character of the console output.
	 * 
	 * @return the top right character of the console output
	 */
	@Override
	public final char getTopRight() {
		return getChar(
				iniReader.getPropertyString("Console", "Char Top Right", "'+'"),
				'+');
	}

	@Override
	public void setTopRight(char topRight) {
		iniReader.setProperty("Console", "Char Top Right",
				String.valueOf((int) topRight));
	}

	/**
	 * Getter of the bottom left character of the console output.
	 * 
	 * @return the bottom left character of the console output
	 */
	@Override
	public final char getBottomLeft() {
		return getChar(iniReader.getPropertyString("Console",
				"Char Bottom Left", "'+'"), '+');
	}

	@Override
	public void setBottomLeft(char bottomLeft) {
		iniReader.setProperty("Console", "Char Bottom Left",
				String.valueOf((int) bottomLeft));
	}

	/**
	 * Getter of the bottom right character of the console output.
	 * 
	 * @return the bottom right character of the console output
	 */
	@Override
	public final char getBottomRight() {
		return getChar(iniReader.getPropertyString("Console",
				"Char Bottom Right", "'+'"), '+');
	}

	@Override
	public void setBottomRight(char bottomRight) {
		iniReader.setProperty("Console", "Char Bottom Right",
				String.valueOf((int) bottomRight));
	}

	/**
	 * Getter of the vertical character of the console output.
	 * 
	 * @return the vertical character of the console output
	 */
	@Override
	public final char getVertical() {
		return getChar(
				iniReader.getPropertyString("Console", "Char Vertical", "'|'"),
				'|');
	}

	@Override
	public void setVertical(char vertical) {
		iniReader.setProperty("Console", "Char Vertical",
				String.valueOf((int) vertical));
	}

	/**
	 * Getter of the horizontal character of the console output.
	 * 
	 * @return the horizontal character of the console output
	 */
	@Override
	public final char getHorizontal() {
		return getChar(
				iniReader.getPropertyString("Console", "Char Horizontal", "'-'"),
				'-');
	}

	@Override
	public void setHorizontal(char horizontal) {
		iniReader.setProperty("Console", "Char Horizontal",
				String.valueOf((int) horizontal));
	}

	/**
	 * Getter of the junction left character of the console output.
	 * 
	 * @return the junction left character of the console output
	 */
	@Override
	public final char getJunctionLeft() {
		return getChar(iniReader.getPropertyString("Console",
				"Char Junction Left", "'-'"), '-');
	}

	@Override
	public void setJunctionLeft(char junctionLeft) {
		iniReader.setProperty("Console", "Char Junction Left",
				String.valueOf((int) junctionLeft));
	}

	/**
	 * Getter of the junction right character of the console output.
	 * 
	 * @return the junction right character of the console output
	 */
	@Override
	public final char getJunctionRight() {
		return getChar(iniReader.getPropertyString("Console",
				"Char Junction Right", "'-'"), '-');
	}

	@Override
	public void setJunctionRight(char junctionRight) {
		iniReader.setProperty("Console", "Char Junction Right",
				String.valueOf((int) junctionRight));
	}

	private static char getChar(final String str, final char defaultChar) {
		char c = 0;
		if (str.length() == 0) {
			return defaultChar;
		}
		// Check if we have an actual Character
		if (str.charAt(0) == '\'') {
			if (str.charAt(2) != '\'') {
				throw new RuntimeException("Invalid character notation: " + str);
			} else {
				c = str.charAt(1);
			}
		} // Nope is number
		else {
			c = (char) Integer.parseInt(str);
		}

		// Clip off special characters
		if (c >= 32) {
			return c;
		}
		return defaultChar;
	}

}