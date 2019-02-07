package application;

public class Parametre {
	public String urlDossierDesThemes;
	public Integer nbMaxDeQuestions;
	public Integer nbMaxDeReponses;

	public Parametre(String urlDossierDesThemes, Integer nbMaxDeQuestions, Integer nbMaxDeReponses) {
		this.urlDossierDesThemes = urlDossierDesThemes;
		this.nbMaxDeQuestions = nbMaxDeQuestions;
		this.nbMaxDeReponses = nbMaxDeReponses;
	}

}