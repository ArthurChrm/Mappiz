package application.editeur;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.Function;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.shape.Polygon;

public class Question_Editeur {

	private static String urlDossierDesThemes;
	private static String nomDuTheme;

	private String intitule;
	private String uRLImageQuestion;
	private int nbReponses;
	private int numBonneReponse;
	private ArrayList<Polygon> polygonesDesReponses = new ArrayList<Polygon>();

	public static void setUrlDossierDesThemes(String urlDossierDesThemes) {
		Question_Editeur.urlDossierDesThemes = urlDossierDesThemes;
	}

	public static String getUrlDossierDesThemes() {
		return urlDossierDesThemes;
	}

	public static void setNomDuTheme(String nomDuTheme) {
		Question_Editeur.nomDuTheme = nomDuTheme;
	}

	public static void ajouterUneQuestion(String pNomDuTheme) {
		nomDuTheme = pNomDuTheme;
		// ?");
		File theme = new File(urlDossierDesThemes + "/" + nomDuTheme);
		if (theme.exists()) {
			int nbQuestions = listerQuestionsTheme(nomDuTheme).length;
			File question = new File(urlDossierDesThemes + "/" + nomDuTheme + "/" + (nbQuestions + 1));
			if (question.exists())
				System.err.println("Le dossier \"" + question.getPath() + "\" existe d�j� !");
			else {
				if (!question.mkdirs()) {
					System.err.println("Le dossier \"" + question.getPath() + "\" n'a pas �t� cr��.");
				}
			}

		} else
			System.err.println("Le dossier du th�me n'existe pas !");
	}

	public static void supprimerUneQuestion(int numQuestionASupprimer) {
		File dossierQuestion = new File(urlDossierDesThemes + "/" + nomDuTheme + "/" + numQuestionASupprimer);

		Utile_Editeur.supprimerUnDossier(dossierQuestion);

		File dossierTheme = dossierQuestion.getParentFile();
		for (File current : listerQuestionsTheme(nomDuTheme)) {
			if (Integer.valueOf(current.getName()) > numQuestionASupprimer) {
				File dossierRenomme = new File(dossierTheme.getPath() + "/" + ((Integer.valueOf(current.getName()) - 1)));
				if (!current.renameTo(dossierRenomme)) {
					System.err.println("Le renommage du dossier \"" + current.getPath() + "\" en \"" + dossierRenomme.getPath() + "\" a �chou� !");
				}

			}
		}
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
			listeDossiersQuestions[i] = new File(urlDossierDesThemes + "/" + nomDuTheme + "/" + intListeDossiersQuestions.get(i));
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
			intitule = new String(Files.readAllBytes(Paths.get(urlDossierQuestion + "/intitule.txt")), StandardCharsets.UTF_8);

			// URL de l'image
			uRLImageQuestion = urlDossierDesThemes + "/" + nomDuTheme + "/" + numQuestion + "/image.png";

			// Contenu du fichier "coord.txt"
			String contenuCoord = new String(Files.readAllBytes(Paths.get(urlDossierQuestion + "/coord.txt")), StandardCharsets.UTF_8);
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
			numBonneReponse = Integer.valueOf(new String(Files.readAllBytes(Paths.get(urlDossierQuestion + "/numReponseCorrecte.txt")), StandardCharsets.UTF_8));

			System.out.println("* intitule : " + intitule);
			System.out.println("* uRLImageQuestion : " + uRLImageQuestion);
			System.out.println("* numBonneReponse : " + numBonneReponse);

			System.out.println("Chargement de la question " + numQuestion + " du th�me " + nomDuTheme + "... END");
		} catch (Exception e) {
			Utile_Editeur.enregistrerErreurDansLog(e);
			if (afficherMsgErreur) {
				String urlFichierLog = (new File(new File(".").getCanonicalPath() + "/erreurs.log")).getPath();
				String message = "Les informations sur la question " + numQuestion + " ne peuvent pas �tre lues par l'�diteur. Les fichiers ou dossiers du th�me sont peut-�tre corrompus. Vous devez revenir � l'�cran d'accueil.\n\nContactez les d�veloppeurs afin d'obtenir une aide � ce probl�me, en leur fournissant le contenu du fichier localis� � l'URL \"" + urlFichierLog + "\". Vous trouverez leurs addresses mail dans la partie \"En cas de probl�me\" du manuel utilisateur.";
				Utile_Editeur.afficherDialogueDInformation("", "Question illisible !", message, AlertType.ERROR, 550);
			}
			return false;
		}
		return true;

	}

}