package application.editeur.accueil;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Scanner;

import application.accueil.AccueilController;
import application.editeur.Parametre_Editeur;
import application.editeur.Theme_Editeur;
import application.editeur.Utile_Editeur;
import application.editeur.creation_theme.CreationThemeController;
import application.editeur.selection_theme.SelectionThemeController_Editeur;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

public class AccueilController_Editeur {

	// D�claration des contr�les
	@FXML
	private Button btnModifierTheme;

	@FXML
	private Button btnAjouterTheme;

	@FXML
	private Button btnQuitter;
	
	@FXML
	private Button btnRetour;
	
	@FXML
	private ImageView imageVLogoUniv;

	private Stage stage;

	@FXML
	private void initialize() {
		// Affichage d'une image sur le bouton Options

		// Chargement du logo par le code, car lorsque le programme est export�
		// en jar
		// puis ex�cut�, l'image ne s'affiche pas
		Image logoUniv = new Image(getClass().getResourceAsStream("logo_LeMansUniv.png"));
		imageVLogoUniv.setImage(logoUniv);

		// On v�rifier que le dossier des th�mes existe bien
		File dossierDesThemes = new File(Theme_Editeur.getUrlDossierDesThemes());
		while (!dossierDesThemes.exists()) {
			String phrase = "Le dossier contenant les th�mes est introuvable. Il a �t� sp�cifi� comme �tant localis� � l'URL \"" + dossierDesThemes.getPath() + "\".\nPour continuer � utiliser l'application.editeur, vous devez s�lectionner un dossier existant � partir de la fen�tre qui s'ouvrira apr�s avoir cliqu� sur \"OK\".";
			Utile_Editeur.afficherDialogueDInformation("", "Dossier des th�mes introuvable !", phrase, AlertType.ERROR, 550);

			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setInitialDirectory(new File("."));
			File dossierSelectionne = directoryChooser.showDialog(stage);

			if (!(dossierSelectionne == null)) {
				enregistrerParametres(dossierSelectionne.getPath(), Theme_Editeur.getNbMaxDeQuestions(), Theme_Editeur.getNbMaxDeReponses());
			}
			dossierDesThemes = new File(Theme_Editeur.getUrlDossierDesThemes());
		}

		btnModifierTheme.setDisable(Theme_Editeur.listerLesThemes().length == 0);

	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	@FXML
	private void clickLogoUniv() {
		if (Utile_Editeur.afficherDialogueDeConfirmation("", "Site de Le Mans Universit�", "Souhaitez-vous consulter le site de Le Mans Universit� ?", "Oui", "Non")) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.univ-lemans.fr/fr/index.html"));
				} catch (IOException | URISyntaxException e) {
					Utile_Editeur.afficherDialogueDInformation("", "Erreur !", "Une erreur est survenue lors de l'ouverture de votre navigateur Internet.", AlertType.ERROR);
				}
			}
		}
	}

	@FXML
	private void clickModifierUnTheme(ActionEvent event) throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/editeur/selection_theme/InterfaceSelectionTheme.fxml"));
		Parent root = (Parent) loader.load();
		SelectionThemeController_Editeur controller = (SelectionThemeController_Editeur) loader.getController();
		controller.setStage(stage);

		stage.setTitle("Mappiz Editor - Modification d'un th�me");
		stage.setWidth(1300);
		stage.setHeight(700);
		stage.setResizable(false);

		Scene sceneSelectionTheme = new Scene(root, 1200, 600);
		stage.setScene(sceneSelectionTheme);
		stage.show();

		/*
		 * La fonction centerOnScreen(); ne centre pas correctement la fen�tre
		 * dans l'�cran Il est donc n�cessaire de calculer manuellement les
		 * coordonn�es x et y de la fen�tre (APRES avoir appel�
		 * primaryStage.show();)
		 */
		Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
		stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
		stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);

	}

	@FXML
	private void clickAjouterUnTheme(ActionEvent event) throws IOException {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/editeur/creation_theme/InterfaceCreationTheme.fxml"));
		Parent root = (Parent) loader.load();
		CreationThemeController controller = (CreationThemeController) loader.getController();
		controller.setStage(stage); // or what you want to do

		stage.setWidth(1300);
		stage.setHeight(700);
		stage.setResizable(false);

		// cr�ation d'une Scene � partir de la Scene parent
		Scene sceneCreationTheme = new Scene(root, 1200, 600);

		stage.setTitle("Mappiz Editor - Cr�ation d'un th�me");
		stage.setScene(sceneCreationTheme);
		stage.show();
	}

	@FXML
	private void clickOptions(ActionEvent event) throws IOException {
		try {

			File paramsFile = new File("params.csv");

			Parametre_Editeur paramsActuels = null;
			// Scanner permettant de lire le fichier contenant les couleurs (une
			// par ligne).
			Scanner s = new Scanner(paramsFile);
			// Tant qu'il y a une ligne � lire...
			while (s.hasNextLine()) {
				// On stock le contenu de la ligne
				String line = s.nextLine();
				// Si la ligne n'est pas vide...
				if (!line.isEmpty()) {
					// On d�coupe la ligne en sous-cha�nes, avec le caract�re de
					// d�limitation ","
					String[] data = line.split("\\|");
					paramsActuels = new Parametre_Editeur(data[0], Integer.valueOf(data[1]), Integer.valueOf(data[2]));
				}
			}
			// On termine la lecture du fichier
			s.close();

			// On instancie une nouvelle bo�te de dialogue
			Dialog<Parametre_Editeur> dialog = new Dialog<>();

			// On sp�cifie le titre, l'ent�te et le fait qu'elle ne puisse pas
			// �tre
			// redimensionn�e
			dialog.setTitle("Mappiz Editor");
			dialog.setHeaderText("Param�tres");
			dialog.setResizable(false);
			dialog.getDialogPane().setPrefWidth(500);
			dialog.getDialogPane().setMinWidth(500);
			dialog.getDialogPane().setMaxWidth(500);

			// On cr�e les noeuds que contiendra la bo�te de dialogue
			Label labelDossierThemes = new Label("Dossier des th�mes : ");
			Label labelNbMaxDeQuestions = new Label("Nombre max. de questions par th�me : ");
			Label labelNbMaxDeReponses = new Label("Nombre max. de r�ponses par question : ");
			// TextField contenant le nom de la couleur
			TextField textFDossierThemes = new TextField(paramsActuels.urlDossierDesThemes);
			textFDossierThemes.setEditable(false);
			// Button permettant d'ouvrir une bo�te de dialogue de s�lection de
			// dossier
			Button btnParcourir = new Button("...");
			Spinner<Integer> spinnerNbMaxDeQuestions = new Spinner<Integer>();
			spinnerNbMaxDeQuestions.setValueFactory(new IntegerSpinnerValueFactory(5, 200, paramsActuels.nbMaxDeQuestions));
			spinnerNbMaxDeQuestions.setMaxWidth(60);

			Spinner<Integer> spinnerNbMaxDeReponses = new Spinner<Integer>();
			spinnerNbMaxDeReponses.setValueFactory(new IntegerSpinnerValueFactory(1, 26, paramsActuels.nbMaxDeReponses));
			spinnerNbMaxDeReponses.setMaxWidth(60);

			// G�rer la s�lection d'un dossier
			btnParcourir.setOnAction(e -> {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				File dossierSelectionne = directoryChooser.showDialog(stage);

				if (!(dossierSelectionne == null)) {
					textFDossierThemes.setText(dossierSelectionne.getPath());
				}
			});

			// GridPane contenant tous les noeuds de saisie d'informations sur
			// la nouvelle couleur
			GridPane gridPane = new GridPane();
			gridPane.setHgap(5);
			gridPane.setVgap(5);

			ColumnConstraints column = new ColumnConstraints();
			column.setPercentWidth(48);
			gridPane.getColumnConstraints().add(column);

			column = new ColumnConstraints();
			column.setPercentWidth(40);
			gridPane.getColumnConstraints().add(column);
			column = new ColumnConstraints();

			column.setPercentWidth(7);
			gridPane.getColumnConstraints().add(column);

			column = new ColumnConstraints();
			column.setPercentWidth(5);
			gridPane.getColumnConstraints().add(column);

			// On ajoute les noeud au GridPane
			gridPane.add(labelDossierThemes, 0, 0);
			gridPane.add(textFDossierThemes, 1, 0, 2, 1);
			gridPane.add(btnParcourir, 3, 0);
			gridPane.add(labelNbMaxDeQuestions, 0, 1);
			gridPane.add(spinnerNbMaxDeQuestions, 1, 1);
			gridPane.add(labelNbMaxDeReponses, 0, 2);
			gridPane.add(spinnerNbMaxDeReponses, 1, 2);

			gridPane.setHalignment(labelDossierThemes, HPos.RIGHT);
			gridPane.setHalignment(labelNbMaxDeQuestions, HPos.RIGHT);
			gridPane.setHalignment(labelNbMaxDeReponses, HPos.RIGHT);

			// On ajoute le GridPane � la bo�te de dialogue
			dialog.getDialogPane().setContent(gridPane);

			// On cr�e un bouton de validation "Ajouter", et un bouton
			// d'annulation
			// "Annuler", que l'on ajoute � la bo�te de dialogue
			ButtonType buttonTypeOk = new ButtonType("Appliquer", ButtonData.OK_DONE);
			ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
			dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
			dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

			dialog.setResultConverter(new Callback<ButtonType, Parametre_Editeur>() {
				@Override
				public Parametre_Editeur call(ButtonType b) {
					if (b == buttonTypeOk) {
						return new Parametre_Editeur(textFDossierThemes.getText(), spinnerNbMaxDeQuestions.getValue(), spinnerNbMaxDeReponses.getValue());
					}
					return null;
				}
			});
			// On r�cup�re la couleur � ajouter
			Optional<Parametre_Editeur> parametreOpt = dialog.showAndWait();
			// Si une couleur a �t� retourn�e...
			if (parametreOpt.isPresent()) {
				Parametre_Editeur parametre = parametreOpt.get();

				enregistrerParametres(parametre.urlDossierDesThemes, parametre.nbMaxDeQuestions, parametre.nbMaxDeReponses);
				btnModifierTheme.setDisable(Theme_Editeur.listerLesThemes().length == 0);
			}
		} catch (IOException e) {
			System.err.println("Erreur lors de l'�criture dans le fichier \"params.csv\".");
			e.printStackTrace();
		}
	}

	@FXML
	private void clickQuitter() {
		Platform.exit();
		System.exit(0);
	}
	
	private void enregistrerParametres(String urlDossierDesThemes, int nbMaxDeQuestionsParTheme, int nbMaxDeReponsesParQuestion) {
		File paramsFile = new File("params.csv");
		String params = urlDossierDesThemes + "|" + nbMaxDeQuestionsParTheme + "|" + nbMaxDeReponsesParQuestion;

		try {
			if (paramsFile.exists())
				paramsFile.delete();
			paramsFile.createNewFile();
			Files.write(paramsFile.toPath(), (params).getBytes(), StandardOpenOption.WRITE);
		} catch (IOException e) {
			System.err.println("Erreur lors de l'engistrement du fichier " + paramsFile);
			e.printStackTrace();
		}

		Theme_Editeur.setUrlDossierDesThemes(urlDossierDesThemes);
		Theme_Editeur.setNbMaxDeQuestions(nbMaxDeQuestionsParTheme);
	}
	
	@FXML
	private void retourAccueil() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/accueil/InterfaceAccueil.fxml"));
		Parent root = (Parent) loader.load();
		AccueilController controller = (AccueilController) loader.getController();
		controller.setStage(stage);

		stage.setWidth(1300);
		stage.setHeight(700);
		stage.setResizable(false);

		// cr�ation d'une Scene � partir de la Scene parent
		Scene sceneCreationTheme = new Scene(root, 1200, 600);
		sceneCreationTheme.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

		stage.setTitle("Mappiz");
		stage.setScene(sceneCreationTheme);
		
		stage.show();
	}

}