package application.editeur;

public class Parametre_Editeur {
	public String urlDossierDesThemes;
	public Integer nbMaxDeQuestions;
	public Integer nbMaxDeReponses;

	public Parametre_Editeur(String urlDossierDesThemes, Integer nbMaxDeQuestions, Integer nbMaxDeReponses) {
		this.urlDossierDesThemes = urlDossierDesThemes;
		this.nbMaxDeQuestions = nbMaxDeQuestions;
		this.nbMaxDeReponses = nbMaxDeReponses;
	}

}