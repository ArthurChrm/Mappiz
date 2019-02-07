package application.accueil;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.Scanner;

import javax.swing.GroupLayout.Alignment;

import org.omg.CORBA.Environment;

import application.Theme;
import application.Utile;
import application.selection_theme.SelectionThemeController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;

public class AccueilController {

	// Déclaration des contrôles
	@FXML
	private Button btnJouer;

	@FXML
	private Button btnQuitter;

	@FXML
	private Button btnOptions;

	@FXML
	private Button btnSon;

	@FXML
	private ImageView logoUniv;

	private Stage stage;

	// Déclaration des variables
	public static Boolean sonActif;

	@FXML
	private void initialize() {
		sonActif = true;

		// Affichage d'une image sur le bouton Options
		Image imageOptions = new Image(getClass().getResourceAsStream("img_options.png"));
		btnOptions.setGraphic(new ImageView(imageOptions));

		// Affichage d'une image sur le bouton Activation/Désactivation du son
		Image imageSon = new Image(getClass().getResourceAsStream("img_son_actif.png"));
		btnSon.setGraphic(new ImageView(imageSon));

		// Chargement du logo par le code, car lorsque le programme est exporté
		// en jar
		// puis exécuté, l'image ne s'affiche pas
		logoUniv.setImage(new Image(getClass().getResourceAsStream("logo_LeMansUniv.png")));

		// On vérifier que le dossier des thèmes existe bien
		File dossierDesThemes = new File(Theme.getUrlDossierDesThemes());
		while (!dossierDesThemes.exists()) {
			String phrase = "Le dossier contenant les thèmes est introuvable. Il a été spécifié comme étant localisé à l'URL \""
					+ dossierDesThemes.getPath()
					+ "\".\nPour pouvoir continuer à utiliser l'application, vous devez sélectionner un dossier contenant les thèmes auxquels vous souhaitez jouer.";
			if (Utile.afficherDialogueDeConfirmation("", "Dossier des thèmes introuvable !", phrase,
					"Sélectionner un dossier", "Quitter", AlertType.ERROR, 550)) {
				DirectoryChooser directoryChooser = new DirectoryChooser();
				directoryChooser.setInitialDirectory(new File("."));
				File dossierSelectionne = directoryChooser.showDialog(stage);

				if (!(dossierSelectionne == null)) {
					enregistrerParametres(dossierSelectionne.getPath(), sonActif);
				}
				dossierDesThemes = new File(Theme.getUrlDossierDesThemes());
			} else {
				Platform.exit();
				System.exit(0);
			}

		}

		if (Theme.listerLesThemes().length == 0) {
			btnJouer.setDisable(true);
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Aucun thème disponible");
			alert.setHeaderText("Erreur");
			alert.setContentText("Aucun thème n'est présent dans le dossier des thèmes. Vous ne pouvez pas jouer.");

			alert.showAndWait();
		}
	}

	@FXML
	private void clickLogoUniv() {
		if (Utile.afficherDialogueDeConfirmation("", "Site de Le Mans Université",
				"Souhaitez-vous consulter le site de Le Mans Université ?", "Oui", "Non", AlertType.CONFIRMATION)) {
			if (Desktop.isDesktopSupported()) {
				try {
					Desktop.getDesktop().browse(new URI("http://www.univ-lemans.fr/fr/index.html"));
				} catch (IOException | URISyntaxException e) {
					Utile.afficherDialogueDInformation("", "Erreur !",
							"Une erreur est survenue lors de l'ouverture de votre navigateur Internet.",
							AlertType.ERROR);
				}
			}
		}
	}

	@FXML
	private void clickSon() {
		setSon(!sonActif);
	}

	private void setSon(boolean activer) {
		Image imageSon;
		if (activer) {
			System.out.println("Son activé.");
			sonActif = true;
			imageSon = new Image(getClass().getResourceAsStream("img_son_actif.png"));
			btnSon.setGraphic(new ImageView(imageSon));
		} else {
			System.out.println("Son désactivé.");
			sonActif = false;
			imageSon = new Image(getClass().getResourceAsStream("img_son_mute.png"));
			btnSon.setGraphic(new ImageView(imageSon));
		}
	}

	@FXML
	private void clickJouer(ActionEvent event) throws IOException {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/application/selection_theme/InterfaceSelectionTheme.fxml"));
			Parent root = (Parent) loader.load();
			SelectionThemeController controller = (SelectionThemeController) loader.getController();
			stage.setTitle("Mappiz");
			controller.setStage(stage);

			// Création de la scène
			Scene scene = new Scene(root, 1200, 600);

			// Paramètrage du Stage principal de l'application (un
			// seul pour
			// toute
			// l'application)
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void clicParametres() {

		try {

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
					paramsActuels = new Parametre(data[0], (Integer.valueOf(data[1]) == 1 ? true : false));
				}
			}
			// On termine la lecture du fichier
			s.close();

			// On instancie une nouvelle boîte de dialogue
			Dialog<Parametre> dialog = new Dialog<>();

			// On spécifie le titre, l'entête et le fait qu'elle ne puisse pas
			// être
			// redimensionnée
			dialog.setTitle("Mappiz");
			dialog.setHeaderText("Paramètres");
			dialog.setResizable(false);
			dialog.getDialogPane().setPrefWidth(500);
			dialog.getDialogPane().setMinWidth(500);
			dialog.getDialogPane().setMaxWidth(500);

			// On crée les noeuds que contiendra la boîte de dialogue
			Label labelDossierThemes = new Label("Dossier des thèmes : ");
			Label labelNbMaxDeQuestions = new Label("Volume coupé par défaut : ");
			// TextField contenant le nom de la couleur
			TextField textFDossierThemes = new TextField(paramsActuels.urlDossierDesThemes);
			textFDossierThemes.setEditable(false);
			// Button permettant d'ouvrir une boîte de dialogue de sélection de
			// dossier
			Button btnParcourir = new Button("...");
			CheckBox checkBCoupageDuSon = new CheckBox("Couper le son par défaut au démarrage du jeu.");
			checkBCoupageDuSon.setSelected(paramsActuels.couperLeSonParDefaut);

			// Gérer la sélection d'un dossier
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
			column.setPercentWidth(35);
			gridPane.getColumnConstraints().add(column);

			column = new ColumnConstraints();
			column.setPercentWidth(60);
			gridPane.getColumnConstraints().add(column);

			column = new ColumnConstraints();
			column.setPercentWidth(5);
			gridPane.getColumnConstraints().add(column);

			gridPane.setHalignment(labelDossierThemes, HPos.RIGHT);

			// On ajoute les noeud au GridPane
			gridPane.add(labelDossierThemes, 0, 0);
			gridPane.add(textFDossierThemes, 1, 0);
			gridPane.add(btnParcourir, 2, 0);
			gridPane.add(checkBCoupageDuSon, 0, 1, 2, 1);

			// On ajoute le GridPane à la boîte de dialogue
			dialog.getDialogPane().setContent(gridPane);

			// On crée un bouton de validation "Ajouter", et un bouton
			// d'annulation
			// "Annuler", que l'on ajoute à la boîte de dialogue
			ButtonType buttonTypeOk = new ButtonType("Appliquer", ButtonData.OK_DONE);
			ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonData.CANCEL_CLOSE);
			dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
			dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

			dialog.setResultConverter(new Callback<ButtonType, Parametre>() {
				@Override
				public Parametre call(ButtonType b) {
					if (b == buttonTypeOk) {
						return new Parametre(textFDossierThemes.getText(), checkBCoupageDuSon.isSelected());
					}
					return null;
				}
			});
			// On récupère la couleur à ajouter
			Optional<Parametre> parametreOpt = dialog.showAndWait();
			// Si une couleur a été retournée...
			if (parametreOpt.isPresent()) {
				Parametre parametre = parametreOpt.get();

				enregistrerParametres(parametre.urlDossierDesThemes, parametre.couperLeSonParDefaut);
				btnJouer.setDisable(Theme.listerLesThemes().length == 0);
			}
		} catch (IOException e) {
			System.err.println("Erreur lors de l'écriture dans le fichier \"params.csv\".");
			e.printStackTrace();
		}
	}

	@FXML
	private void clickQuitter() {
		// Arrêt du programme
		Platform.exit();
		System.exit(0);
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setDatas(boolean couperLeSonParDefaut) {
		setSon(!couperLeSonParDefaut);
	}

	private void enregistrerParametres(String urlDossierDesThemes, boolean couperLeSonParDefaut) {
		File paramsFile = new File("params.csv");
		String params = urlDossierDesThemes + "|" + (couperLeSonParDefaut ? 1 : 0);

		try {
			if (paramsFile.exists())
				paramsFile.delete();
			paramsFile.createNewFile();
			Files.write(paramsFile.toPath(), (params).getBytes(), StandardOpenOption.WRITE);
		} catch (IOException e) {
			System.err.println("Erreur lors de l'engistrement du fichier " + paramsFile);
			e.printStackTrace();
		}

		Theme.setUrlDossierDesThemes(urlDossierDesThemes);
	}

}