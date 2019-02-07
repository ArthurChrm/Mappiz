package application.questions;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import application.Question;
import application.Utile;
import application.accueil.AccueilController;
import application.affichage_score.ScoreController;
import application.selection_theme.SelectionThemeController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

public class QuestionController {

	// Déclaration des contrôles
	@FXML
	private GridPane gridPaneQuestionEtImg;

	@FXML
	private Pane imgPane;

	@FXML
	private Button btnValiderLaReponse;

	@FXML
	private Button btnPasserQuestion;

	@FXML
	private Button btnAbandonnerLaPartie;

	@FXML
	private Label lblProgression;

	@FXML
	private ProgressBar progression;

	@FXML
	private Label lblScore;

	@FXML
	private Label lblIntituleQuestion;

	@FXML
	private Label lblNomDuTheme;

	// Déclaration des variables
	private Stage stage;

	private String nomDuTheme;

	private int questionActuelle = 0;

	private int score = 0; // Score du joueur

	private Media sound;

	private Question question;

	private int nbQuestions;

	private Point2D pointDeReponse; // Point ayant pour coordonnées la réponse
									// du joueur sur l'image.

	private Polygon polygoneReponseDonnee;

	private boolean affichageDeLaReponse;

	private ArrayList<Polygon> polygonesDesReponse;

	Canvas canvas;

	@FXML
	private void initialize() throws IOException {
		question = new Question();
		pointDeReponse = null;
		polygonesDesReponse = new ArrayList<Polygon>();

		imgPane.layout();
	}

	@FXML
	public void clicImage(MouseEvent event) throws FileNotFoundException {
		// Récupération des coordonnées du curseur lors du clic
		if (!affichageDeLaReponse) {
			// TODO : afficher un pointer.
			// S'il y a plusieurs propositions de réponse, mais que
			// l'utilisateur n'en
			// sélectionne aucune, on ne prend pas se réponse en compte.
			if (question.getNbReponses() > 1) {
				pointDeReponse = null;
				for (Polygon polygone : getBonsPolygones(question.getPolygonesDesReponses())) {
					if (polygone.contains(event.getX(), event.getY())) {
						pointDeReponse = new Point2D(event.getX(), event.getY());
					}
				}
			} else if (question.getNbReponses() == 1) {
				pointDeReponse = new Point2D(event.getX(), event.getY());
			}
			if (pointDeReponse != null) {

				// Afficher le marqueur sur le Pane, à l'endroit où
				// l'utilisateur a cliqué.
				imgPane.getChildren().removeIf(n -> n instanceof Canvas);
				canvas = new Canvas(imgPane.getWidth(), imgPane.getHeight());

				GraphicsContext gc = canvas.getGraphicsContext2D();
				Image original = new Image(getClass().getResourceAsStream("marqueur.png"));
				gc.drawImage(original, event.getX() - 16, event.getY() - 64);

				imgPane.getChildren().add(canvas);
			}
		}
	}

	private ArrayList<Polygon> getBonsPolygones(ArrayList<Polygon> polygonesDesReponses) {
		double widthPaneImage = imgPane.getWidth();
		double heightPaneImage = imgPane.getHeight();

		ArrayList<Polygon> bonsPolygones = new ArrayList<Polygon>();
		for (Polygon polygone : polygonesDesReponses) {
			Polygon polygoneBonsCoordonnees = new Polygon();

			for (int i = 0; i < polygone.getPoints().size(); i += 2) {
				double x = polygone.getPoints().get(i);
				double y = polygone.getPoints().get(i + 1);
				polygoneBonsCoordonnees.getPoints().addAll(x * widthPaneImage, y * heightPaneImage);
			}
			bonsPolygones.add(polygoneBonsCoordonnees);
		}

		return bonsPolygones;
	}

	private Polygon getPolygoneDeBonneReponse() {
		double widthPaneImage = imgPane.getWidth();
		double heightPaneImage = imgPane.getHeight();

		Polygon polygone = question.getPolygonesDesReponses().get(question.getNumBonneReponse() - 1);
		Polygon polygoneBonsCooordonnees = new Polygon();

		for (int i = 0; i < polygone.getPoints().size(); i += 2) {
			double x = polygone.getPoints().get(i);
			double y = polygone.getPoints().get(i + 1);
			polygoneBonsCooordonnees.getPoints().addAll(x * widthPaneImage, y * heightPaneImage);
		}

		return polygoneBonsCooordonnees;
	}

	@FXML
	private void clicButtonValiderLaReponse(ActionEvent event) throws IOException {
		if (!affichageDeLaReponse) {
			etudierLaReponse(true);
		} else {
			btnPasserQuestion.setDisable(false);
			affichageDeLaReponse = false;
			btnValiderLaReponse.setText("VALIDER LA RÉPONSE");
			afficherLaQuestionSuivante();
		}
	}

	@FXML
	private void passerLaQuestion(ActionEvent event) throws IOException {
		btnPasserQuestion.setDisable(true);
		btnValiderLaReponse.setText("POURSUIVRE");
		affichageDeLaReponse = true;
		afficherLesPolygones(true, true);
	}

	private void etudierLaReponse(boolean donnerDesPointsSiReponseCorrecte) throws IOException {

		if (pointDeReponse != null) {
			// On récupère le polygone de la réponse donnée par le joueur.
			for (Polygon polygone : getBonsPolygones(question.getPolygonesDesReponses())) {
				if (polygone.contains(pointDeReponse)) {
					polygoneReponseDonnee = polygone;
					break;
				}
			}

			// Si la réponse donnée est correcte...
			if (getPolygoneDeBonneReponse().contains(pointDeReponse)) {
				System.out.println("Correct.");
				// S'il reste des questions à poser...
				// Jouer un son de victoire
				if (AccueilController.sonActif) {
					sound = new Media(new File("./bonneReponse.mp3").toURI().toString());
					MediaPlayer mediaPlayer = new MediaPlayer(sound);
					mediaPlayer.play();
				}
				if (donnerDesPointsSiReponseCorrecte) {
					mAJScore(10);
				}
				afficherLaQuestionSuivante();
			} else { // Si la réponse est incorrecte...
				System.out.println("Incorrect.");
				// Jouer un son d'échec
				if (AccueilController.sonActif) {
					sound = new Media(new File("./mauvaiseReponse.mp3").toURI().toString());
					MediaPlayer mediaPlayer = new MediaPlayer(sound);
					mediaPlayer.play();
				}
				btnPasserQuestion.setDisable(true);
				btnValiderLaReponse.setText("POURSUIVRE");
				affichageDeLaReponse = true;
				afficherLesPolygones(true, false);
			}
		} else {
			Utile.afficherDialogueDInformation("", "", "Vous devez donner une réponse, ou alors passer la question.",
					AlertType.INFORMATION);
		}
	}

	private void afficherLesPolygones(boolean afficherLaBonneReponse, boolean passerLaQuestion) {
		imgPane.getChildren().removeIf(n -> n instanceof Canvas);
		imgPane.getChildren().removeIf(n -> n instanceof Polygon);
		// On affiche seulement tous les polygones des propositions de réponse
		// si la
		// question n'est pas à réponse unique (sinon, le joueur à déjà la
		// réponse), à
		// moins qu'il s'agisse de vouloir afficher la réponse.

		if ((question.getNbReponses() == 1 && afficherLaBonneReponse) || passerLaQuestion) {
			Polygon polygoneAAfficher = getPolygoneDeBonneReponse();
			polygoneAAfficher.setFill(Color.GREEN);
			polygoneAAfficher.setOpacity(0.3);
			polygoneAAfficher.setStroke(Color.BLACK);
			imgPane.getChildren().add(polygoneAAfficher);
		} else if (question.getNbReponses() > 1) {
			for (Polygon polygoneAAfficher : getBonsPolygones(question.getPolygonesDesReponses())) {
				if (afficherLaBonneReponse) {
					// On affiche tous les autres polygones en gris
					polygoneAAfficher.setFill(Color.GREY);
					polygoneAAfficher.setOpacity(0.2);
					polygoneAAfficher.setStroke(Color.BLACK);
					if (polygonesEgaux(polygoneAAfficher, getPolygoneDeBonneReponse())) {
						// On l'affiche en vert pour indiquer qu'il faillait
						// cliquer dessus
						polygoneAAfficher.setFill(Color.GREEN);
						polygoneAAfficher.setOpacity(0.5);
						polygoneAAfficher.setStroke(Color.BLACK);
					}
					if (polygonesEgaux(polygoneAAfficher, polygoneReponseDonnee)) {
						// On l'affiche en rouge pour montrer que cette réponse
						// est incorrecte
						polygoneAAfficher.setFill(Color.RED);
						polygoneAAfficher.setOpacity(0.5);
						polygoneAAfficher.setStroke(Color.BLACK);
					}
				} else {
					// On l'affiche en vert pour indiquer qu'il faillait cliquer
					// dessus
					polygoneAAfficher.setFill(Color.GREY);
					polygoneAAfficher.setOpacity(0.3);
					polygoneAAfficher.setStroke(Color.BLACK);
				}
				imgPane.getChildren().add(polygoneAAfficher);
			}
		}

		imgPane.layout();
	}

	private void mAJProgressionVisuelle() throws IOException {
		// On met à jour le Label
		lblProgression.setText("Progression : " + questionActuelle + "/" + nbQuestions);
		// On met à jour la valeur de la barre de progression
		progression.setProgress(((double) questionActuelle / (double) nbQuestions));
	}

	private void mAJScore(int augmentation) {
		score += augmentation;
	}

	public void afficherLaQuestionSuivante() throws IOException {
		if (questionActuelle < nbQuestions) {
			questionActuelle++;

			pointDeReponse = null;
			polygoneReponseDonnee = null;

			mAJProgressionVisuelle();
			lblScore.setText(Integer.toString(score));

			if (!question.load(questionActuelle, true)) {
				retournerALAccueil();
			}

			// On affiche l'intitulé de la question.
			lblIntituleQuestion.setText(question.getIntitule());

			// On affiche l'image de la question.
			Image image = new Image("file:" + question.getuRLImageQuestion());
			BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true,
					false);
			imgPane.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT,
					BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bSize)));

			// On affiche les polygones des propositions de réponse.
			afficherLesPolygones(false, false);
		} else {
			try {
				FXMLLoader loader = new FXMLLoader(
						getClass().getResource("/application/affichage_score/InterfaceScore.fxml"));
				Parent root = (Parent) loader.load();
				ScoreController controller = (ScoreController) loader.getController();
				controller.setStage(stage);
				controller.setDatas(score, nbQuestions);

				// Création de la scène
				Scene scene = new Scene(root, 1200, 600);

				stage.setTitle("Mappiz - Fin de partie");
				stage.setScene(scene);
				stage.show();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void clicButtonAbandonnerLaPartie(ActionEvent event) throws IOException {

		// Revenir à l'écran de séléction des thèmes revient à abandonner la
		// partie, on va demander à l'utilisateur si c'est bien ce qu'il veut
		// faire via une boite de dialogue

		if (demanderConfirmationPourAbandon()) {
			retournerALAccueil();
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		stage.setOnCloseRequest(event -> {
			if (stage.getTitle().startsWith("Mappiz - ") && !stage.getTitle().equals("Mappiz - Fin de partie")
					&& !demanderConfirmationPourAbandon()) {
				event.consume();
			}
		});
	}

	private boolean demanderConfirmationPourAbandon() {
		return Utile.afficherDialogueDeConfirmation("", "Abandonner ?",
				"Souhaitez-vous vraiment abandonner la partie et quitter ?", "Oui", "Non", AlertType.CONFIRMATION);
	}

	public void setDatas(String nomDuTheme) throws IOException {
		this.nomDuTheme = nomDuTheme;
		Question.setNomDuTheme(nomDuTheme);
		lblNomDuTheme.setText(nomDuTheme);
		nbQuestions = Question.listerQuestionsTheme(nomDuTheme).length;

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					afficherLaQuestionSuivante();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private boolean polygonesEgaux(Polygon poly1, Polygon poly2) {
		if (poly1.getPoints().size() != poly2.getPoints().size()) {
			return false;
		}
		for (int i = 0; i < poly1.getPoints().size(); i++) {
			if (Double.compare(poly1.getPoints().get(i), poly2.getPoints().get(i)) != 0) {
				return false;
			}
		}
		return true;
	}

	private void retournerALAccueil() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/accueil/InterfaceAccueil.fxml"));
			Parent root = (Parent) loader.load();
			AccueilController controller = (AccueilController) loader.getController();
			controller.setStage(stage);

			// Création de la scène
			Scene scene = new Scene(root, 1200, 600);
			scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

			stage.setTitle("Mappiz");
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}