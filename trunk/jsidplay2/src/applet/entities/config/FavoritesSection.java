package applet.entities.config;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import applet.config.annotations.ConfigDescription;
import applet.config.annotations.ConfigSectionName;
import applet.config.annotations.ConfigTransient;
import applet.config.annotations.ConfigTypeName;
import applet.entities.collection.HVSCEntry;

@Entity
@ConfigTypeName(bundleKey = "FAVORITE")
public class FavoritesSection {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ConfigTransient
	private Integer id;

	@XmlTransient
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ConfigDescription(bundleKey = "FAVORITES_NAME_DESC", toolTipBundleKey = "FAVORITES_NAME_TOOLTIP")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ConfigDescription(bundleKey = "FAVORITES_SELECTED_ROW_FROM_DESC", toolTipBundleKey = "FAVORITES_SELECTED_ROW_FROM_TOOLTIP")
	private Integer selectedRowFrom = -1;

	public Integer getSelectedRowFrom() {
		return selectedRowFrom;
	}

	public void setSelectedRowFrom(Integer selectedRowFrom) {
		this.selectedRowFrom = selectedRowFrom;
	}

	@ConfigDescription(bundleKey = "FAVORITES_SELECTED_ROW_TO_DESC", toolTipBundleKey = "FAVORITES_SELECTED_ROW_TO_TOOLTIP")
	private Integer selectedRowTo = -1;

	public Integer getSelectedRowTo() {
		return selectedRowTo;
	}

	public void setSelectedRowTo(Integer selectedRowTo) {
		this.selectedRowTo = selectedRowTo;
	}

	@OneToMany(cascade = CascadeType.ALL)
	private List<FavoriteColumn> columns;

	@ConfigSectionName(bundleKey = "FAVORITES_COLUMNS")
	public List<FavoriteColumn> getColumns() {
		if (columns == null) {
			columns = new ArrayList<FavoriteColumn>();
		}
		return columns;
	}

	public void setColumns(List<FavoriteColumn> columns) {
		this.columns = columns;
	}

	@XmlElement(name = "favorite")
	@OneToMany(cascade = CascadeType.ALL)
	private List<HVSCEntry> favorites;

	public void setFavorites(List<HVSCEntry> favorites) {
		this.favorites = favorites;
	}

	@XmlTransient
	@ConfigSectionName(bundleKey = "HVSC_ENTRIES")
	public List<HVSCEntry> getFavorites() {
		if (favorites == null) {
			favorites = new ArrayList<HVSCEntry>();
		}
		return favorites;
	}

}