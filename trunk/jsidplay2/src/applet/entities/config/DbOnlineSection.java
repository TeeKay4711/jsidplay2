package applet.entities.config;

import javax.persistence.Embeddable;

import sidplay.ini.intf.IOnlineSection;
import applet.JSIDPlay2;

@Embeddable
public class DbOnlineSection implements IOnlineSection {

	private String hvscUrl = JSIDPlay2.DEPLOYMENT_URL
			+ "online/hvsc/C64Music.zip";

	@Override
	public String getHvscUrl() {
		return hvscUrl;
	}

	public void setHvscUrl(String hvscUrl) {
		this.hvscUrl = hvscUrl;
	}

	private String cgscUrl = JSIDPlay2.DEPLOYMENT_URL + "online/cgsc/CGSC.zip";

	@Override
	public String getCgscUrl() {
		return cgscUrl;
	}

	public void setCgscurl(String cgscUrl) {
		this.cgscUrl = cgscUrl;
	}

	private String hvmecUrl = JSIDPlay2.DEPLOYMENT_URL
			+ "online/hvmec/HVMEC.zip";

	@Override
	public String getHvmecUrl() {
		return hvmecUrl;
	}

	public void setHvmecUrl(String hvmecUrl) {
		this.hvmecUrl = hvmecUrl;
	}

	private String demosUrl = JSIDPlay2.DEPLOYMENT_URL
			+ "online/demos/Demos.zip";

	@Override
	public String getDemosUrl() {
		return demosUrl;
	}

	public void setDemosUrl(String demosUrl) {
		this.demosUrl = demosUrl;
	}

	private String magazinesUrl = JSIDPlay2.DEPLOYMENT_URL
			+ "online/mags/C64Magazines.zip";

	@Override
	public String getMagazinesUrl() {
		return magazinesUrl;
	}

	public void setMagazinesUrl(String magazinesUrl) {
		this.magazinesUrl = magazinesUrl;
	}

	private String gamebaseUrl = JSIDPlay2.DEPLOYMENT_URL
			+ "online/gamebase/gb64.jar";

	@Override
	public String getGamebaseUrl() {
		return gamebaseUrl;
	}

	public void setGamebaseUrl(String gamebaseUrl) {
		this.gamebaseUrl = gamebaseUrl;
	}

	private String soasc6581R2;

	@Override
	public String getSoasc6581R2() {
		return soasc6581R2;
	}

	@Override
	public void setSoasc6581R2(String soasc6581r2) {
		soasc6581R2 = soasc6581r2;
	}

	private String soasc6581R4;

	@Override
	public String getSoasc6581R4() {
		return soasc6581R4;
	}

	@Override
	public void setSoasc6581R4(String soasc6581r4) {
		soasc6581R4 = soasc6581r4;
	}

	private String soasc8580R5;

	@Override
	public String getSoasc8580R5() {
		return soasc8580R5;
	}

	@Override
	public void setSoasc8580R5(String soasc8580r5) {
		soasc8580R5 = soasc8580r5;
	}

}
