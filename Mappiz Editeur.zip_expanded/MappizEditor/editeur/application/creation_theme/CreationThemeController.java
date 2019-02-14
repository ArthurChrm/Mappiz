package application.creation_theme;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

import application.Question_Editeur;
import application.Theme_Editeur;
import application.Utile_Editeur;
import application.accueil.AccueilController_Editeur;
import application.creation_modif_questions.CreationModifQuestionsController;
import application.selection_theme.SelectionThemeController_Editeur;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class CreationThemeController {

	@FXML
	private Pane paneImageTheme;

	@FXML
	private Button btnRetour;

	@FXML
	private Label labelAucuneImage;

	@FXML
	private TextField textFNomDuTheme;

	@FXML
	private Label labelTitre;

	@FXML
	private TextArea textADescription;

	@FXML
	private TextField textFURLDeLImage;

	@FXML
	private BorderPane borderPane;

	@FXML
	private Stage stage;

	private String nomDuTheme;
	private int mode;

	@FXML
	private void initialize() {
		Utile_Editeur.setNbCaracteresMaxDansUnChamp(textFNomDuTheme, 100);
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		stage.setOnCloseRequest(event -> {
			if (stage.getTitle().startsWith("Mappiz Editor - Création d'un thème") || stage.getTitle().startsWith("Mappiz Editor - Modification d'un thème")) {
				if (Utile_Editeur.afficherDialogueDeConfirmation("", "Quitter ?", "Souhaitez-vous vraiment annuler la " + (mode == 0 ? "création" : "modification") + " de ce thème et quitter l'application ?", "Oui", "Non")) {
					Platform.exit();
					System.exit(0);
				} else {
					event.consume();
				}
			}
		});
	}

	@FXML
	private void clickBtnParcourirImage(ActionEvent event) {
		// On invite l'utilisateur à sélectionner une image
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choisir une image...");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif"));

		// On récupère le fichier sélectionné
		File file = fileChooser.showOpenDialog(stage);

		// On affiche l'image sélectionnée, dans le panel
		afficherMiniature(file);
	}

	@FXML
	private void clickSuivant(ActionEvent event) throws IOException {
		boolean nomValide = true;

		if (textFNomDuTheme.getText().isEmpty()) {
			Utile_Editeur.afficherDialogueDInformation("", "Information manquante !", "Vous devez donner un nom au thème.", AlertType.WARNING);
			nomValide = false;
		} else {
			if ((Theme_Editeur.existe(textFNomDuTheme.getText()) && mode == 0) || (Theme_Editeur.existe(textFNomDuTheme.getText(), nomDuTheme) && mode == 1)) {
				Utile_Editeur.afficherDialogueDInformation("", "Nom déjà utilisé !", "Un autre thème porte déjà ce nom. Vous devez en donner un autre.", AlertType.WARNING);
				nomValide = false;
			}
		}
		if (nomValide) {
			try {
				String description = textADescription.getText();
				String urlMiniatureDuTheme = textFURLDeLImage.getText();

				// On créer le dossier du thème, dans le dossier spécifié dans
				// le fichier "params.ini"
				if (mode == 0) {
					nomDuTheme = textFNomDuTheme.getText();
					Theme_Editeur.ajouterUnTheme(nomDuTheme);
					stage.setTitle(stage.getTitle() + " (" + nomDuTheme + ")");
				} else if (mode == 1) {
					if (!nomDuTheme.equals(textFNomDuTheme.getText())) {

						Theme_Editeur.renommer(nomDuTheme, textFNomDuTheme.getText());
						nomDuTheme = textFNomDuTheme.getText();
						stage.setTitle("Mappiz Editor - Modification d'un thème (" + nomDuTheme + ")");
					}
				}

				Utile_Editeur.enregistrerFichier(Theme_Editeur.getUrlDossierDesThemes() + "/" + nomDuTheme + "/description.txt", textADescription.getText());

				if (new File(textFURLDeLImage.getText()).exists()) {
					String urlDossierDuTheme = Theme_Editeur.getUrlDossierDesThemes() + "/" + nomDuTheme;
					File fileImageSource = new File(textFURLDeLImage.getText());
					File fileImageDestination = new File(urlDossierDuTheme + "/" + "miniature.png");

					Path pathImageSource = fileImageSource.toPath();
					Path pathImageDestination = fileImageDestination.toPath();

					// Si l'image a changé...
					if (!pathImageSource.equals(pathImageDestination)) {
						Files.copy(pathImageSource, pathImageDestination, StandardCopyOption.REPLACE_EXISTING);
					}
				}

				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/creation_modif_questions/InterfaceCreationModifQuestions.fxml"));
				Parent root = (Parent) loader.load();
				CreationModifQuestionsController controller = (CreationModifQuestionsController) loader.getController();
				controller.setStage(stage);

				// On appelle la fonction "setDatas" de la classe
				// CreationQuestionsController, à partir
				// du contrôleur de la création des questions.
				// Cela permet de transmettre les informations saisies sur
				// l'interface actuelle, au contrôleur de l'interface de
				// création des questions.
				controller.setDatas(mode, nomDuTheme, description, urlMiniatureDuTheme);

				Scene sceneCreationDesQuestions = new Scene(root, 1200, 600);
				stage.setScene(sceneCreationDesQuestions);
				stage.show();

				/*
				 * La fonction centerOnScreen(); ne centre pas correctement la
				 * fenêtre dans l'écran Il est donc nécessaire de calculer
				 * manuellement les coordonnées x et y de la fenêtre (APRES
				 * avoir appelé primaryStage.show();)
				 */
				Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
				stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2);
				stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2);

			} catch (IOException e) {
				System.err.println("Erreur load InterfaceExercice.fxml");
				e.printStackTrace();
			}
		}
	}

	public void setDatas(int mode, String nomDuTheme) throws IOException {
		this.nomDuTheme = nomDuTheme;
		this.mode = mode;

		textFNomDuTheme.setText(nomDuTheme);
		if (mode == 1) {
			labelTitre.setText("Modification d'un thème");
			// On charge les informations principales du thème (la description)
			Theme_Editeur.load(nomDuTheme);
			// On affiche la description du thème dans la TextArea
			textADescription.setText(Theme_Editeur.getDescription());
			// On affiche la miniature du thème dans le Pane et l'URL de la
			// miniature dans le TextField
			File miniature = new File(Theme_Editeur.getUrlDossierDesThemes() + "/" + nomDuTheme + "/miniature.png");
			if (miniature.exists() && miniature.isFile()) {
				afficherMiniature(miniature);
			}
		}
	}

	@FXML
	private void clickBtnRetour() {
		// On demande une confirmation à l'utilisateur
		if (Utile_Editeur.afficherDialogueDeConfirmation("", "Annuler ?", "Souhaitez-vous vraiment annuler la " + (mode == 0 ? "création" : "modification") + " de ce thème ?", "Oui", "Non")) {
			try {
				if (mode == 0) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/accueil/InterfaceAccueil.fxml"));
					Parent root = (Parent) loader.load();
					AccueilController_Editeur controller = (AccueilController_Editeur) loader.getController();
					stage.setTitle("Mappiz Editor");
					controller.setStage(stage);

					// Création de la scène
					Scene scene = new Scene(root, 1200, 600);

					// Paramètrage du Stage principal de l'application (un
					// seul pour
					// toute
					// l'application)
					stage.setScene(scene);
					stage.show();
				} else {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/selection_theme/InterfaceSelectionTheme.fxml"));
					Parent root = (Parent) loader.load();
					SelectionThemeController_Editeur controller = (SelectionThemeController_Editeur) loader.getController();
					stage.setTitle("Mappiz Editor");
					controller.setStage(stage);

					// Création de la scène
					Scene scene = new Scene(root, 1200, 600);

					// Paramètrage du Stage principal de l'application (un
					// seul pour
					// toute
					// l'application)
					stage.setScene(scene);
					stage.show();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void afficherMiniature(File fileImage) {
		// On récupère l'image et on l'affiche dans le panel
		Image image = new Image("file:" + fileImage.getPath());
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		paneImageTheme.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bSize)));

		// On cache le label indiquant qu'il n'y a aucune image mise en aperçu
		labelAucuneImage.setVisible(false);

		// On affiche l'URL de l'image dans le champs de l'URL
		textFURLDeLImage.setText(fileImage.getPath());
	}

}