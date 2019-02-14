package application.selection_theme;

public class ThemeItemListe_Editeur {

	String urlMiniature;
	String nomDuTheme;
	String description;

	public ThemeItemListe_Editeur(String urlMiniature, String nomDuTheme, String description) {
		this.urlMiniature = urlMiniature;
		this.nomDuTheme = nomDuTheme;
		this.description = description;
	}

	public String getUrlMiniature() {
		return urlMiniature;
	}

	public String getNomDuTheme() {
		return nomDuTheme;
	}

	public String getDescription() {
		return description;
	}

}