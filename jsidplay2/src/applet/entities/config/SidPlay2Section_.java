package applet.entities.config;

import applet.favorites.PlaybackType;
import applet.favorites.RepeatType;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2012-11-25T00:35:37.693+0100")
@StaticMetamodel(SidPlay2Section.class)
public class SidPlay2Section_ {
	public static volatile SingularAttribute<SidPlay2Section, Integer> version;
	public static volatile SingularAttribute<SidPlay2Section, Boolean> enableDatabase;
	public static volatile SingularAttribute<SidPlay2Section, Integer> playLength;
	public static volatile SingularAttribute<SidPlay2Section, Integer> recordLength;
	public static volatile SingularAttribute<SidPlay2Section, PlaybackType> playbackType;
	public static volatile SingularAttribute<SidPlay2Section, RepeatType> repeatType;
	public static volatile SingularAttribute<SidPlay2Section, String> HVMEC;
	public static volatile SingularAttribute<SidPlay2Section, String> demos;
	public static volatile SingularAttribute<SidPlay2Section, String> mags;
	public static volatile SingularAttribute<SidPlay2Section, String> cgsc;
	public static volatile SingularAttribute<SidPlay2Section, String> hvsc;
	public static volatile SingularAttribute<SidPlay2Section, Boolean> single;
	public static volatile SingularAttribute<SidPlay2Section, Boolean> enableProxy;
	public static volatile SingularAttribute<SidPlay2Section, String> proxyHostname;
	public static volatile SingularAttribute<SidPlay2Section, Integer> proxyPort;
	public static volatile SingularAttribute<SidPlay2Section, String> lastDirectory;
	public static volatile SingularAttribute<SidPlay2Section, String> tmpDir;
	public static volatile SingularAttribute<SidPlay2Section, Integer> frameX;
	public static volatile SingularAttribute<SidPlay2Section, Integer> frameY;
	public static volatile SingularAttribute<SidPlay2Section, Integer> frameWidth;
	public static volatile SingularAttribute<SidPlay2Section, Integer> frameHeight;
}
