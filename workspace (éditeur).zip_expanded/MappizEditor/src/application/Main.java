package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import application.accueil.AccueilController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {

			// On crée le fichier des paramètres s'il n'existe pas
			creerFichierDesParametres();

			File paramsFile = new File("params.csv");

			Parametre paramsActuels = null;
			// Scanner permettant de lire le fichier contenant les couleurs (une
			// par ligne).
			Scanner s = new Scanner(paramsFile);
			// Tant qu'il y a une ligne à lire...
			while (s.hasNextLine()) {
				// On stock le contenu de la ligne
				String line = s.nextLine();
				// Si la ligne n'est pas vide...
				if (!line.isEmpty()) {
					// On découpe la ligne en sous-chaînes, avec le caractère de
					// délimitation ","
					String[] data = line.split("\\|");
					// On crée un objet de type Couleur afin de stocker les
					// informations
					paramsActuels = new Parametre(data[0], Integer.valueOf(data[1]), Integer.valueOf(data[2]));
				}
			}
			// On termine la lecture du fichier
			s.close();

			Theme.setUrlDossierDesThemes(paramsActuels.urlDossierDesThemes);
			Theme.setNbMaxDeQuestions(paramsActuels.nbMaxDeQuestions);
			Theme.setNbMaxDeReponses(paramsActuels.nbMaxDeReponses);

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/accueil/InterfaceAccueil.fxml"));
			Parent root = (Parent) loader.load();
			AccueilController controller = (AccueilController) loader.getController();
			controller.setStage(primaryStage);

			// Création de la scène
			Scene scene = new Scene(root, 1200, 600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			// Paramètrage du Stage principal de l'application (un seul pour
			// toute
			// l'application)
			primaryStage.setWidth(1300);
			primaryStage.setHeight(700);
			primaryStage.setMinWidth(900);
			primaryStage.setMinHeight(500);
			primaryStage.setScene(scene);
			primaryStage.show();

			/*
			 * La fonction centerOnScreen(); ne centre pas correctement la
			 * fenêtre dans l'écran Il est donc nécessaire de calculer
			 * manuellement les coordonnées x et y de la fenêtre (APRES avoir
			 * appelé primaryStage.show();)
			 */
			Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
			primaryStage.setX((primScreenBounds.getWidth() - primaryStage.getWidth()) / 2);
			primaryStage.setY((primScreenBounds.getHeight() - primaryStage.getHeight()) / 2);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void creerFichierDesParametres() {
		// Si le fichier des paramètres n'existe pas, on le crée
		File paramsFile = new File("params.csv");
		if (!paramsFile.exists()) {
			try {
				File dossierParDefautDesThemes = new File(new File(".").getCanonicalPath() + "\\themes_mappiz");
				if (!dossierParDefautDesThemes.exists())
					dossierParDefautDesThemes.mkdir();

				// On créée le fichier
				paramsFile.createNewFile();
				String paramsParDefaut = dossierParDefautDesThemes.getPath() + "|20|8";
				Files.write(paramsFile.toPath(), paramsParDefaut.getBytes(), StandardOpenOption.WRITE);
			} catch (IOException e) {
				System.err.println("Erreur lors de la création ou de l'écriture dans le fichier \"params.csv\".");
				e.printStackTrace();
			}
		}
	}

}
