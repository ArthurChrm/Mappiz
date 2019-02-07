package application;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import application.selection_theme.ThemeItemListe;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert.AlertType;

public class Theme {

	private static String urlDossierDesThemes;
	private static int nbMaxDeQuestions;
	private static int nbMaxDeReponses;
	private static String description;
	private final static ObservableList<ThemeItemListe> data = FXCollections.observableArrayList();

	public static void setUrlDossierDesThemes(String urlDossierDesThemes) {
		Theme.urlDossierDesThemes = urlDossierDesThemes;
	}

	public static String getUrlDossierDesThemes() {
		return urlDossierDesThemes;
	}

	public static int getNbMaxDeQuestions() {
		return nbMaxDeQuestions;
	}

	public static void setNbMaxDeQuestions(int nbMaxDeQuestions) {
		Theme.nbMaxDeQuestions = nbMaxDeQuestions;
	}

	public static int getNbMaxDeReponses() {
		return nbMaxDeReponses;
	}

	public static void setNbMaxDeReponses(int nbMaxDeReponses) {
		Theme.nbMaxDeReponses = nbMaxDeReponses;
	}

	public static String getDescription() {
		return description;
	}

	public static void ajouterUnTheme(String nomDuTheme) {

		File file = new File(urlDossierDesThemes + "/" + nomDuTheme);
		if (file.exists())
			System.err.println("Le dossier du th�me \"" + nomDuTheme + "\" existe d�j� !");
		else {
			if (!file.mkdirs()) {
				System.err.println("Le dossier du th�me \"" + nomDuTheme + "\" n'a pas �t� cr�� !");
			}
		}
	}

	public static String[] listerLesThemes() {
		// Cette m�thode va renvoyer une liste de String avec le nom des th�mes
		// install�s
		File dossierDesThemes = new File(urlDossierDesThemes);
		String[] listeDesThemes = new String[dossierDesThemes.listFiles(File::isDirectory).length];
		int numDossier = 0;
		for (File dossier : dossierDesThemes.listFiles(File::isDirectory)) {
			listeDesThemes[numDossier] = dossier.getName();
			numDossier++;
		}
		return listeDesThemes;

	}

	public static void renommer(String nom, String nouveauNom) {
		File theme = new File(urlDossierDesThemes + "/" + nom);
		File themeRenomme = new File(urlDossierDesThemes + "/" + nouveauNom);
		if (theme.renameTo(themeRenomme))
			System.err.println("Renommage du dossier du th�me \"" + nom + "\" en \"" + nouveauNom + "\" a �chou� !");
	}

	public static void load(String nomDuTheme) {
		try {
			description = new String(Files.readAllBytes(Paths.get(urlDossierDesThemes + "/" + nomDuTheme + "/description.txt")), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean supprimerUnTheme(String nomDuTheme) {
		File dossierDesThemes = new File(urlDossierDesThemes);
		for (File theme : dossierDesThemes.listFiles(File::isDirectory)) {
			if (theme.getName().equals(nomDuTheme)) {
				return Utile.supprimerUnDossier(theme);
			}
		}
		return false;
	}

	public static boolean existe(String nomDuTheme) {
		return existe(nomDuTheme, null);
	}

	public static boolean existe(String nomDuTheme, String themeAIgnorer) {
		ArrayList<String> listeDesThemes = new ArrayList(Arrays.asList(Theme.listerLesThemes()));
		if (themeAIgnorer != null) {
			listeDesThemes.remove(themeAIgnorer);
			return listeDesThemes.contains(nomDuTheme);
		} else {
			return listeDesThemes.contains(nomDuTheme);
		}
	}

	public static ObservableList<ThemeItemListe> getObservableArrayList() {
		data.clear();
		for (String theme : listerLesThemes()) {
			File miniature = new File(urlDossierDesThemes + "/" + theme + "/miniature.png");
			load(theme);
			if (miniature.exists()) {
				data.add(new ThemeItemListe(miniature.getPath(), theme, description));
			} else {
				data.add(new ThemeItemListe("", theme, description));
			}
		}
		description = "";
		return data;
	}

	public static boolean testerLisibiliteDUnTheme(String nomDuTheme) throws IOException {
		Question.setUrlDossierDesThemes(getUrlDossierDesThemes());
		Question.setNomDuTheme(nomDuTheme);
		File[] dossiersQuestions = Question.listerQuestionsTheme(nomDuTheme);
		int nbQuestions = dossiersQuestions.length;

		for (int i = 1; i <= nbQuestions; i++) {
			{
				Question question = new Question();
				if (!question.load(i, false)) {
					String urlFichierLog = (new File(new File(".").getCanonicalPath() + "/erreurs.log")).getPath();
					String message = "Vous ne pouvez pas modifier ce th�me, car les informations qui y sont associ�es ne peuvent pas �tre lues par l'�diteur. Les fichiers ou dossiers du th�me sont peut-�tre corrompus.\n\nContactez les d�veloppeurs afin d'obtenir une aide � ce probl�me, en leur fournissant le contenu du fichier localis� � l'URL \"" + urlFichierLog + "\". Vous trouverez leurs addresses mail dans la partie \"En cas de probl�me\" du manuel utilisateur.";
					Utile.afficherDialogueDInformation("", "Th�me illisible !", message, AlertType.ERROR, 550);
					return false;
				}
			}
		}
		return true;

	}

}
