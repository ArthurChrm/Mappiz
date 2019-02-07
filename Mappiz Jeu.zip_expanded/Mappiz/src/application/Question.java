package application;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.shape.Polygon;

public class Question {

	private static String urlDossierDesThemes;
	private static String nomDuTheme;

	private String intitule;
	private String uRLImageQuestion;
	private int nbReponses;
	private int numBonneReponse;
	private ArrayList<Polygon> polygonesDesReponses = new ArrayList<Polygon>();

	public static void setUrlDossierDesThemes(String urlDossierDesThemes) {
		Question.urlDossierDesThemes = urlDossierDesThemes;
	}

	public static String getUrlDossierDesThemes() {
		return urlDossierDesThemes;
	}

	public static void setNomDuTheme(String nomDuTheme) {
		Question.nomDuTheme = nomDuTheme;
	}

	public static File[] listerQuestionsTheme(String nomDuTheme) {
		File dossierTheme = new File(urlDossierDesThemes + "/" + nomDuTheme);
		if (!dossierTheme.exists()) {
			System.err.println("Le dossier du th�me n'existe pas ! URL : " + dossierTheme.getPath());
			return null;
		}
		File[] listeDossiersQuestions = dossierTheme.listFiles(File::isDirectory);
		ArrayList<String> strListeDossiersQuestions = new ArrayList<String>();
		for (File file : listeDossiersQuestions) {
			strListeDossiersQuestions.add(file.getName());
		}
		ArrayList<Integer> intListeDossiersQuestions = new ArrayList<Integer>();
		for (String name : strListeDossiersQuestions) {
			try {
				intListeDossiersQuestions.add(Integer.valueOf(name));
			} catch (Exception e) {
			}
		}
		Collections.sort(intListeDossiersQuestions);
		for (File f : listeDossiersQuestions)
			f = null;
		for (int i = 0; i < intListeDossiersQuestions.size(); i++) {
			listeDossiersQuestions[i] = new File(
					urlDossierDesThemes + "/" + nomDuTheme + "/" + intListeDossiersQuestions.get(i));
		}
		return listeDossiersQuestions;
	}

	// Getters

	public String getIntitule() {
		return intitule;
	}

	public String getuRLImageQuestion() {
		return uRLImageQuestion;
	}

	public int getNbReponses() {
		return nbReponses;
	}

	public int getNumBonneReponse() {
		return numBonneReponse;
	}

	public ArrayList<Polygon> getPolygonesDesReponses() {
		return polygonesDesReponses;
	}

	// Fonction permettant de r�cup�rer les informations de la question
	// (numQuestion commence � 1)
	public boolean load(int numQuestion, boolean afficherMsgErreur) throws IOException {
		try {
			System.out.println("Chargement de la question " + numQuestion + " du th�me " + nomDuTheme + "... START");
			polygonesDesReponses.clear();

			String urlDossierQuestion = urlDossierDesThemes + "/" + nomDuTheme + "/" + numQuestion;

			// Intitul� de la question
			intitule = new String(Files.readAllBytes(Paths.get(urlDossierQuestion + "/intitule.txt")),
					StandardCharsets.UTF_8);

			// URL de l'image
			uRLImageQuestion = urlDossierDesThemes + "/" + nomDuTheme + "/" + numQuestion + "/image.png";

			// Contenu du fichier "coord.txt"
			String contenuCoord = new String(Files.readAllBytes(Paths.get(urlDossierQuestion + "/coord.txt")),
					StandardCharsets.UTF_8);
			// On r�cup�re les points des polygones sous forme de tableau
			String[] polygones = contenuCoord.split("\\$");
			// On r�cup�re le nombre de r�ponses � partir du tableau
			nbReponses = polygones.length - 1; // -1 car la premi�re valeur est
												// vide
												// (avant le premier "$")

			for (int i = 1; i < polygones.length; i++) {
				Polygon monPolygone = new Polygon();
				String polygone = polygones[i];
				String[] points = polygone.split("\\|");
				for (int j = 0; j < points.length; j++) {
					String point = points[j];
					String[] valeurs = point.split("\\,");
					if (valeurs.length == 2)
						monPolygone.getPoints().addAll(Double.valueOf(valeurs[0]), Double.valueOf(valeurs[1]));
				}
				polygonesDesReponses.add(monPolygone);
			}

			// Num�ro de la bonne r�ponse
			numBonneReponse = Integer
					.valueOf(new String(Files.readAllBytes(Paths.get(urlDossierQuestion + "/numReponseCorrecte.txt")),
							StandardCharsets.UTF_8));

			System.out.println("* intitule : " + intitule);
			System.out.println("* uRLImageQuestion : " + uRLImageQuestion);
			System.out.println("* numBonneReponse : " + numBonneReponse);

			System.out.println("Chargement de la question " + numQuestion + " du th�me " + nomDuTheme + "... END");
		} catch (Exception e) {
			Utile.enregistrerErreurDansLog(e);
			if (afficherMsgErreur) {
				String urlFichierLog = (new File(new File(".").getCanonicalPath() + "/erreurs.log")).getPath();
				String message = "Les informations sur la question " + numQuestion
						+ " ne peuvent pas �tre lues par l'application. Les fichiers ou dossiers du th�me sont peut-�tre corrompus. Vous ne pouvez donc pas continuer la partie.\n\nContactez les d�veloppeurs afin d'obtenir une aide � ce probl�me, en leur fournissant le contenu du fichier localis� � l'URL \""
						+ urlFichierLog
						+ "\". Vous trouverez leurs addresses mail dans la partie \"En cas de probl�me\" du manuel utilisateur.";
				Utile.afficherDialogueDInformation("", "Question illisible !", message, AlertType.ERROR, 550);
			}
			return false;
		}
		return true;

	}

}