package application.selection_theme;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import application.Question;
import application.Theme;
import application.Utile;
import application.accueil.AccueilController;
import application.questions.QuestionController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class SelectionThemeController {

	// Déclaration des contrôles
	@FXML
	private Button btnJouerACeTheme;

	@FXML
	private Button btnRetour;

	@FXML
	private ListView<ThemeItemListe> listVThemes;

	public static ArrayList<String> listeQuestion = new ArrayList<String>();
	public static ArrayList<Image> listeImages = new ArrayList<Image>();
	public static String nomDuTheme = "";

	private Stage stage;

	@FXML
	private void initialize() {
		listVThemes.setCellFactory(listVThemes -> new ListCell<ThemeItemListe>() {
			private ImageView imageView = new ImageView();

			@Override
			public void updateItem(ThemeItemListe theme, boolean empty) {
				super.updateItem(theme, empty);

				if (empty) {
					setText(null);
					setGraphic(null);
				} else {
					if (theme.getUrlMiniature() != "") {
						Image miniature = new Image("file:" + theme.getUrlMiniature());
						imageView.setImage(miniature);
					} else {
						Image imageParDefaut = new Image(getClass().getResourceAsStream("pasDImage.jpg"));
						imageView.setImage(imageParDefaut);
					}
					imageView.setFitWidth(60);

					imageView.setPreserveRatio(true);
					setText(theme.getNomDuTheme());
					setGraphic(imageView);
					// On applique un tooltip à la ligne du thème, qui affiche la description du
					// thème.
					Tooltip tooltip = new Tooltip();
					if (!theme.getDescription().equals("")) {
						tooltip.setText(theme.getDescription());
					} else {
						tooltip.setText("(aucune description)");
					}
					tooltip.setStyle(
							"-fx-text-fill: black; -fx-background-color: #bdc3c7; -fx-background-radius: 3px; -fx-padding: 0.3em;");
					setTooltip(tooltip);
				}
			}
		});

		listVThemes.setItems(Theme.getObservableArrayList());
	}

	@FXML
	private void clicButtonRetour(ActionEvent event) throws IOException {
		retournerALAccueil();
	}

	@FXML
	private void clicButtonJouerACeTheme(ActionEvent event) throws IOException {
		// On va récupérer le thème séléctionné par le jouer dans le menu
		// si le jouer n'a rien séléctionné la séléction sera null, on va gérer
		// ce cas en affichant un message d'erreur
		if (listVThemes.getSelectionModel().getSelectedIndex() == -1) {
			Utile.afficherDialogueDInformation("", "Sélection incorrecte !",
					"Vous devez sélectionner un thème dans la liste.", AlertType.ERROR);
		} else {
			// On va récupérer les questions du thème séléctionné
			nomDuTheme = listVThemes.getSelectionModel().getSelectedItem().getNomDuTheme();

			if (Theme.existe(nomDuTheme)) {
				if (Theme.testerLisibiliteDUnTheme(nomDuTheme)) {
					// On va charger et afficher la scène suivante, c'est à dire le jeu
					// en lui-même
					stage.setTitle("Mappiz - " + nomDuTheme);
					FXMLLoader loader = new FXMLLoader(
							getClass().getResource("/application/questions/InterfaceQuestion.fxml"));
					Parent root = (Parent) loader.load();
					QuestionController controller = (QuestionController) loader.getController();
					controller.setStage(stage); // or what you want to do
					controller.setDatas(nomDuTheme);

					// création d'une Scene à partie de la Scene parent
					Scene sceneCreationTheme = new Scene(root, 1200, 600);

					stage.setScene(sceneCreationTheme);
					stage.show();
				}
			} else {
				Utile.afficherDialogueDInformation("", "Thème inexistant",
						"Le thème \"" + nomDuTheme
								+ "\" n'existe pas. Il a peut-être été supprimé par un autre utilisateur.",
						AlertType.ERROR);
				int indexDuTheme = listVThemes.getSelectionModel().getSelectedIndex();
				listVThemes.getItems().remove(indexDuTheme);
				// S'il n'y a plus aucun thème, on retourne à l'écran d'accueil
				if (Theme.listerLesThemes().length == 0) {
					retournerALAccueil();
				}
			}

		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private void retournerALAccueil() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/accueil/InterfaceAccueil.fxml"));
			Parent root = (Parent) loader.load();
			AccueilController controller = (AccueilController) loader.getController();
			stage.setTitle("Mappiz");
			controller.setStage(stage);

			// Création de la scène
			Scene scene = new Scene(root, 1200, 600);
			scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

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

}