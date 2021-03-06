package ui.entities.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.input.KeyCode;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import libsidplay.components.keyboard.KeyTableEntry;
import libsidplay.config.IConfig;
import ui.console.Console;
import ui.videoscreen.Video;

@Entity
@Access(AccessType.PROPERTY)
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Configuration implements IConfig {

	private final List<FilterSection> INITIAL_FILTERS;
	{
		//
		// ReSID 1.0 beta (Dag Lem):
		//
		INITIAL_FILTERS = new ArrayList<FilterSection>();
		FilterSection dbFilterSection;
		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterLight8580");
		dbFilterSection.setFilter8580CurvePosition(13400);
		INITIAL_FILTERS.add(dbFilterSection);
		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterAverage8580");
		dbFilterSection.setFilter8580CurvePosition(12500);
		INITIAL_FILTERS.add(dbFilterSection);
		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterDark8580");
		dbFilterSection.setFilter8580CurvePosition(11700);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterLightest6581");
		dbFilterSection.setFilter6581CurvePosition(0.1f);
		INITIAL_FILTERS.add(dbFilterSection);
		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterLighter6581");
		dbFilterSection.setFilter6581CurvePosition(0.3f);
		INITIAL_FILTERS.add(dbFilterSection);
		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterLight6581");
		dbFilterSection.setFilter6581CurvePosition(0.4f);
		INITIAL_FILTERS.add(dbFilterSection);
		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterAverage6581");
		dbFilterSection.setFilter6581CurvePosition(0.5f);
		INITIAL_FILTERS.add(dbFilterSection);
		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterDark6581");
		dbFilterSection.setFilter6581CurvePosition(0.6f);
		INITIAL_FILTERS.add(dbFilterSection);
		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterDarker6581");
		dbFilterSection.setFilter6581CurvePosition(0.7f);
		INITIAL_FILTERS.add(dbFilterSection);
		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterDarkest6581");
		dbFilterSection.setFilter6581CurvePosition(0.9f);
		INITIAL_FILTERS.add(dbFilterSection);
		//
		// ReSIDfp (Antti S. Lankila):
		//
		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterTrurl8580R5_1489");
		dbFilterSection.setK(5.7f);
		dbFilterSection.setB(20f);
		dbFilterSection.setVoiceNonlinearity(1.0f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterTrurl8580R5_3691");
		dbFilterSection.setK(6.55f);
		dbFilterSection.setB(20f);
		dbFilterSection.setVoiceNonlinearity(1.0f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterReSID6581");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1537690.1305811733f);
		dbFilterSection.setOffset(76322577.13241087f);
		dbFilterSection.setSteepness(1.0072175060266852f);
		dbFilterSection.setMinimumfetresistance(19114.018733380854f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterAlankila6581R4AR_3789");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.9613160610660189f);
		dbFilterSection.setBaseresistance(1147036.4394268463f);
		dbFilterSection.setOffset(274228796.97550374f);
		dbFilterSection.setSteepness(1.0066634233403395f);
		dbFilterSection.setMinimumfetresistance(16125.154840564108f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterAlankila6581R3_3984_1");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1522171.922983084f);
		dbFilterSection.setOffset(21729926.667291082f);
		dbFilterSection.setSteepness(1.004994802537475f);
		dbFilterSection.setMinimumfetresistance(14299.149638099827f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterAlankila6581R3_3984_2");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1613349.4942964897f);
		dbFilterSection.setOffset(67122945.35403329f);
		dbFilterSection.setSteepness(1.005727808339829f);
		dbFilterSection.setMinimumfetresistance(15462.006399118263f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterLordNightmare6581R3_4285");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.55e6f);
		dbFilterSection.setOffset(2.5e8f);
		dbFilterSection.setSteepness(1.006f);
		dbFilterSection.setMinimumfetresistance(1.9e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterLordNightmare6581R3_4485");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1399768.3253307983f);
		dbFilterSection.setOffset(553018906.8926692f);
		dbFilterSection.setSteepness(1.0051493199361266f);
		dbFilterSection.setMinimumfetresistance(11961.908870403166f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterLordNightmare6581R4_1986S");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1250736.2235895505f);
		dbFilterSection.setOffset(1521187976.8735676f);
		dbFilterSection.setSteepness(1.005543646897986f);
		dbFilterSection.setMinimumfetresistance(8581.78418415723f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterZrX6581R3_0384");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.9e6f);
		dbFilterSection.setOffset(8.5e7f);
		dbFilterSection.setSteepness(1.0058f);
		dbFilterSection.setMinimumfetresistance(2e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterZrX6581R3_1984");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.83e6f);
		dbFilterSection.setOffset(2.6e9f);
		dbFilterSection.setSteepness(1.0064f);
		dbFilterSection.setMinimumfetresistance(2.5e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterZrx6581R3_3684");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.65e6f);
		dbFilterSection.setOffset(1.2e10f);
		dbFilterSection.setSteepness(1.006f);
		dbFilterSection.setMinimumfetresistance(1e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterZrx6581R3_3985");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.5e6f);
		dbFilterSection.setOffset(1.8e8f);
		dbFilterSection.setSteepness(1.0065f);
		dbFilterSection.setMinimumfetresistance(1.8e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterZrx6581R4AR_2286");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.3e6f);
		dbFilterSection.setOffset(1.9e8f);
		dbFilterSection.setSteepness(1.0066f);
		dbFilterSection.setMinimumfetresistance(1.8e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterTrurl6581R3_0784");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.3e6f);
		dbFilterSection.setOffset(3.7e8f);
		dbFilterSection.setSteepness(1.0066f);
		dbFilterSection.setMinimumfetresistance(1.8e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterTrurl6581R3_0486S");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1164920.4999651583f);
		dbFilterSection.setOffset(12915042.165290257f);
		dbFilterSection.setSteepness(1.0058853753357735f);
		dbFilterSection.setMinimumfetresistance(12914.5661141159f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterTrurl6581R3_3384");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.16e6f);
		dbFilterSection.setOffset(9.9e6f);
		dbFilterSection.setSteepness(1.0058f);
		dbFilterSection.setMinimumfetresistance(1.48e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterTrurl6581R3_4885");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(840577.4520801408f);
		dbFilterSection.setOffset(1909158.8633669745f);
		dbFilterSection.setSteepness(1.0068865662510837f);
		dbFilterSection.setMinimumfetresistance(14858.140079688419f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterTrurl6581R4AR_3789");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.08e6f);
		dbFilterSection.setOffset(1.8e6f);
		dbFilterSection.setSteepness(1.006f);
		dbFilterSection.setMinimumfetresistance(1.0e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterTrurl6581R4AR_4486");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.1e6f);
		dbFilterSection.setOffset(8e6f);
		dbFilterSection.setSteepness(1.0052f);
		dbFilterSection.setMinimumfetresistance(1.7e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterNata6581R3_2083");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1590881.4720854638f);
		dbFilterSection.setOffset(32349044.218055427f);
		dbFilterSection.setSteepness(1.005583147929036f);
		dbFilterSection.setMinimumfetresistance(16357.252396224501f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterGrue6581R4AR_3488");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1.45e6f);
		dbFilterSection.setOffset(1.75e8f);
		dbFilterSection.setSteepness(1.0055f);
		dbFilterSection.setMinimumfetresistance(1e4f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterKruLLo");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1585454.0607309118f);
		dbFilterSection.setOffset(97702791.79673694f);
		dbFilterSection.setSteepness(1.0053036856077033f);
		dbFilterSection.setMinimumfetresistance(14497.859765307807f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterEnigma6581R3_4885");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1090649.9265298771f);
		dbFilterSection.setOffset(264401456.3944572f);
		dbFilterSection.setSteepness(1.0145069244334666f);
		dbFilterSection.setMinimumfetresistance(19890.786352277173f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);

		dbFilterSection = new FilterSection();
		dbFilterSection.setName("FilterEnigma6581R3_1585");
		dbFilterSection.setAttenuation(0.5f);
		dbFilterSection.setNonlinearity(3.3e6f);
		dbFilterSection.setVoiceNonlinearity(0.96f);
		dbFilterSection.setBaseresistance(1119380.8539989102f);
		dbFilterSection.setOffset(257709531.3343812f);
		dbFilterSection.setSteepness(1.0073881870085593f);
		dbFilterSection.setMinimumfetresistance(20269.79137269368f);
		dbFilterSection.setResonanceFactor(1.0f);
		INITIAL_FILTERS.add(dbFilterSection);
	}

	private final List<ViewEntity> INITIAL_VIEWS;
	{
		INITIAL_VIEWS = new ArrayList<ViewEntity>();
		INITIAL_VIEWS.add(new ViewEntity(Console.ID));
		INITIAL_VIEWS.add(new ViewEntity(Video.ID));
	}

	private final List<KeyTableEntity> INITIAL_KEYCODES;
	{
		INITIAL_KEYCODES = new ArrayList<KeyTableEntity>();
		List<KeyTableEntity> keyCodes = Arrays
				.asList(new KeyTableEntity(KeyCode.A.getName(), KeyTableEntry.A),
						new KeyTableEntity(KeyCode.BACK_SLASH.getName(),
								KeyTableEntry.ARROW_LEFT),
						new KeyTableEntity(KeyCode.DIGIT1.getName(),
								KeyTableEntry.ONE),
						new KeyTableEntity(KeyCode.DIGIT2.getName(),
								KeyTableEntry.TWO),
						new KeyTableEntity(KeyCode.DIGIT3.getName(),
								KeyTableEntry.THREE),
						new KeyTableEntity(KeyCode.DIGIT4.getName(),
								KeyTableEntry.FOUR),
						new KeyTableEntity(KeyCode.DIGIT5.getName(),
								KeyTableEntry.FIVE),
						new KeyTableEntity(KeyCode.DIGIT6.getName(),
								KeyTableEntry.SIX),
						new KeyTableEntity(KeyCode.DIGIT7.getName(),
								KeyTableEntry.SEVEN),
						new KeyTableEntity(KeyCode.DIGIT8.getName(),
								KeyTableEntry.EIGHT),
						new KeyTableEntity(KeyCode.DIGIT9.getName(),
								KeyTableEntry.NINE),
						new KeyTableEntity(KeyCode.DIGIT0.getName(),
								KeyTableEntry.ZERO),
						new KeyTableEntity(KeyCode.OPEN_BRACKET.getName(),
								KeyTableEntry.PLUS),
						new KeyTableEntity(KeyCode.CLOSE_BRACKET.getName(),
								KeyTableEntry.MINUS),
						new KeyTableEntity(KeyCode.POUND.getName(),
								KeyTableEntry.POUND),
						new KeyTableEntity(KeyCode.HOME.getName(),
								KeyTableEntry.CLEAR_HOME),
						new KeyTableEntity(KeyCode.BACK_SPACE.getName(),
								KeyTableEntry.INS_DEL),

						new KeyTableEntity(KeyCode.Q.getName(), KeyTableEntry.Q),
						new KeyTableEntity(KeyCode.W.getName(), KeyTableEntry.W),
						new KeyTableEntity(KeyCode.E.getName(), KeyTableEntry.E),
						new KeyTableEntity(KeyCode.R.getName(), KeyTableEntry.R),
						new KeyTableEntity(KeyCode.T.getName(), KeyTableEntry.T),
						new KeyTableEntity(KeyCode.Y.getName(), KeyTableEntry.Y),
						new KeyTableEntity(KeyCode.U.getName(), KeyTableEntry.U),
						new KeyTableEntity(KeyCode.I.getName(), KeyTableEntry.I),
						new KeyTableEntity(KeyCode.O.getName(), KeyTableEntry.O),
						new KeyTableEntity(KeyCode.P.getName(), KeyTableEntry.P),
						new KeyTableEntity(KeyCode.SEMICOLON.getName(),
								KeyTableEntry.AT),
						new KeyTableEntity(KeyCode.PLUS.getName(),
								KeyTableEntry.STAR),
						new KeyTableEntity(KeyCode.LESS.getName(),
								KeyTableEntry.ARROW_UP),

						new KeyTableEntity(KeyCode.ESCAPE.getName(),
								KeyTableEntry.RUN_STOP),
						new KeyTableEntity(KeyCode.A.getName(), KeyTableEntry.A),
						new KeyTableEntity(KeyCode.S.getName(), KeyTableEntry.S),
						new KeyTableEntity(KeyCode.D.getName(), KeyTableEntry.D),
						new KeyTableEntity(KeyCode.F.getName(), KeyTableEntry.F),
						new KeyTableEntity(KeyCode.G.getName(), KeyTableEntry.G),
						new KeyTableEntity(KeyCode.H.getName(), KeyTableEntry.H),
						new KeyTableEntity(KeyCode.J.getName(), KeyTableEntry.J),
						new KeyTableEntity(KeyCode.K.getName(), KeyTableEntry.K),
						new KeyTableEntity(KeyCode.L.getName(), KeyTableEntry.L),
						new KeyTableEntity(KeyCode.BACK_QUOTE.getName(),
								KeyTableEntry.COLON),
						new KeyTableEntity(KeyCode.QUOTE.getName(),
								KeyTableEntry.SEMICOLON),
						new KeyTableEntity(KeyCode.SLASH.getName(),
								KeyTableEntry.EQUALS),
						new KeyTableEntity(KeyCode.ENTER.getName(),
								KeyTableEntry.RETURN),

						new KeyTableEntity(KeyCode.Z.getName(), KeyTableEntry.Z),
						new KeyTableEntity(KeyCode.X.getName(), KeyTableEntry.X),
						new KeyTableEntity(KeyCode.C.getName(), KeyTableEntry.C),
						new KeyTableEntity(KeyCode.V.getName(), KeyTableEntry.V),
						new KeyTableEntity(KeyCode.B.getName(), KeyTableEntry.B),
						new KeyTableEntity(KeyCode.N.getName(), KeyTableEntry.N),
						new KeyTableEntity(KeyCode.M.getName(), KeyTableEntry.M),
						new KeyTableEntity(KeyCode.COMMA.getName(),
								KeyTableEntry.COMMA),
						new KeyTableEntity(KeyCode.PERIOD.getName(),
								KeyTableEntry.PERIOD), new KeyTableEntity(
								KeyCode.MINUS.getName(), KeyTableEntry.SLASH),
						new KeyTableEntity(KeyCode.DOWN.getName(),
								KeyTableEntry.CURSOR_UP_DOWN),
						new KeyTableEntity(KeyCode.UP.getName(),
								KeyTableEntry.CURSOR_UP_DOWN, true),
						new KeyTableEntity(KeyCode.RIGHT.getName(),
								KeyTableEntry.CURSOR_LEFT_RIGHT),
						new KeyTableEntity(KeyCode.LEFT.getName(),
								KeyTableEntry.CURSOR_LEFT_RIGHT, true),

						new KeyTableEntity(KeyCode.SPACE.getName(),
								KeyTableEntry.SPACE, true),

						new KeyTableEntity(KeyCode.F1.getName(),
								KeyTableEntry.F1), new KeyTableEntity(
								KeyCode.F3.getName(), KeyTableEntry.F3),
						new KeyTableEntity(KeyCode.F5.getName(),
								KeyTableEntry.F5), new KeyTableEntity(
								KeyCode.F7.getName(), KeyTableEntry.F7));
		INITIAL_KEYCODES.addAll(keyCodes);
	}

	private final List<FavoritesSection> INITIAL_FAVORITES;
	{
		INITIAL_FAVORITES = new ArrayList<FavoritesSection>();
		INITIAL_FAVORITES.add(new FavoritesSection());
	}

	private Integer id;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlTransient
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	private SidPlay2Section sidplay2 = new SidPlay2Section();

	public void setSidplay2Section(SidPlay2Section sidplay2) {
		this.sidplay2 = sidplay2;
	}

	@Embedded
	@Override
	public SidPlay2Section getSidplay2Section() {
		return sidplay2;
	}

	private OnlineSection online = new OnlineSection();

	@Embedded
	public OnlineSection getOnlineSection() {
		return online;
	}

	public void setOnlineSection(OnlineSection online) {
		this.online = online;
	}

	private C1541Section c1541 = new C1541Section();

	public void setC1541Section(C1541Section c1541) {
		this.c1541 = c1541;
	}

	@Override
	@Embedded
	public C1541Section getC1541Section() {
		return c1541;
	}

	private PrinterSection printer = new PrinterSection();

	public void setPrinterSection(PrinterSection printer) {
		this.printer = printer;
	}

	@Embedded
	@Override
	public PrinterSection getPrinterSection() {
		return printer;
	}

	private JoystickSection joystickSection = new JoystickSection();

	public void setJoystickSection(JoystickSection joystick) {
		this.joystickSection = joystick;
	}

	@Embedded
	public JoystickSection getJoystickSection() {
		return joystickSection;
	}

	private AudioSection audioSection = new AudioSection();

	public void setAudioSection(AudioSection audio) {
		this.audioSection = audio;
	}

	@Embedded
	@Override
	public AudioSection getAudioSection() {
		return audioSection;
	}

	private EmulationSection emulationSection = new EmulationSection();

	public void setEmulationSection(EmulationSection emulation) {
		this.emulationSection = emulation;
	}

	@Embedded
	@Override
	public EmulationSection getEmulationSection() {
		return emulationSection;
	}

	private String currentFavorite;

	public void setCurrentFavorite(String currentFavorite) {
		this.currentFavorite = currentFavorite;
	}

	public String getCurrentFavorite() {
		return currentFavorite;
	}

	protected List<FavoritesSection> favorites = INITIAL_FAVORITES;

	private ObservableList<FavoritesSection> observableFavorites;

	public void setFavorites(List<FavoritesSection> favorites) {
		this.favorites = favorites;
	}

	@OneToMany(cascade = CascadeType.ALL)
	public List<FavoritesSection> getFavorites() {
		if (favorites == null) {
			favorites = new ArrayList<FavoritesSection>();
		}
		return getObservableFavorites();
	}

	@Transient
	public ObservableList<FavoritesSection> getObservableFavorites() {
		if (observableFavorites == null) {
			observableFavorites = FXCollections
					.<FavoritesSection> observableArrayList(favorites);
			Bindings.bindContent(favorites, observableFavorites);
		}
		return observableFavorites;
	}

	protected List<ViewEntity> views = INITIAL_VIEWS;

	private ObservableList<ViewEntity> observableViews;

	@OneToMany(cascade = CascadeType.ALL)
	public List<ViewEntity> getViews() {
		if (observableViews == null) {
			observableViews = FXCollections
					.<ViewEntity> observableArrayList(views);
			Bindings.bindContent(views, observableViews);
		}
		return observableViews;
	}

	public void setViews(List<ViewEntity> views) {
		this.views = views;
	}

	private List<FilterSection> filter = INITIAL_FILTERS;

	public void setFilterSection(List<FilterSection> filter) {
		this.filter = filter;
	}

	@OneToMany(cascade = CascadeType.ALL)
	@Override
	public List<FilterSection> getFilterSection() {
		if (filter == null) {
			filter = new ArrayList<FilterSection>();
		}
		return filter;
	}

	private List<KeyTableEntity> keyCodeMap = INITIAL_KEYCODES;

	public void setKeyCodeMap(List<KeyTableEntity> keyCodeMap) {
		this.keyCodeMap = keyCodeMap;
	}

	@OneToMany(cascade = CascadeType.ALL)
	public List<KeyTableEntity> getKeyCodeMap() {
		return keyCodeMap;
	}

	public KeyTableEntry getKeyTabEntry(String key) {
		for (KeyTableEntity keyCode : keyCodeMap) {
			if (keyCode.getKeyCodeName().equals(key)) {
				return keyCode.getEntry();
			}
		}
		return null;
	}

}
