package application.affichage_score;

import java.io.IOException;

import application.accueil.AccueilController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ScoreController {

	// D�claration des contr�les
	@FXML
	private Label lblScore;

	@FXML
	private Button btnQuitter;

	@FXML
	private Button retourAccueil;

	@FXML
	private Label lblMessageRelatifAuScore;

	@FXML
	private Label lblNbQuestionsCorrectementRepondues;

	// D�claration des variables
	private Stage stage;
	private int scoreFinal;
	private int nbQuestions;

	@FXML
	private void initialize() {

	}

	@FXML
	private void clicBtnRetournerALAccueil(ActionEvent event) throws IOException {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/accueil/InterfaceAccueil.fxml"));
			Parent root = (Parent) loader.load();
			AccueilController controller = (AccueilController) loader.getController();
			stage.setTitle("Mappiz");
			controller.setStage(stage);

			// Cr�ation de la sc�ne
			Scene scene = new Scene(root, 1200, 600);
			scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());

			// Param�trage du Stage principal de l'application (un
			// seul pour
			// toute
			// l'application)
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void clicBtnQuitter() {
		// Arr�t du programme
		Platform.exit();
		System.exit(0);
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public void setDatas(int score, int nbQuestions) throws IOException {
		scoreFinal = score;
		this.nbQuestions = nbQuestions;
		afficherScore();
	}

	private void afficherScore() {
		double note = 0;
		lblScore.setText(String.valueOf(scoreFinal) + "/" + (nbQuestions * 10));
		note = (double) scoreFinal / (nbQuestions * 10);
		System.out.println("score : " + scoreFinal);
		System.out.println("nb de questions : " + nbQuestions);
		System.out.println(note);
		if (note == 0) {
			lblMessageRelatifAuScore.setText("M�me ma grand-m�re ferait mieux !");
		} else if (note > 0 && note <= 0.25) {
			lblMessageRelatifAuScore.setText("A�e...");
		} else if (note > 0.25 && note <= 0.5) {
			lblMessageRelatifAuScore.setText("Pas mal !");
		} else if (note > 0.5 && note <= 0.75) {
			lblMessageRelatifAuScore.setText("Bien jou� !");
		} else if (note > 0.75 && note < 1) {
			lblMessageRelatifAuScore.setText("Bravo !");
		} else {
			lblMessageRelatifAuScore.setText("Parfait !");
		}

		int nbReponsesCorrectes = scoreFinal / 10;
		if (note > 0) {
			lblNbQuestionsCorrectementRepondues.setText("Vous avez correctement r�pondu � " + (scoreFinal / 10)
					+ " question" + (nbReponsesCorrectes > 1 ? "s" : "") + " sur un total de " + nbQuestions + ".");
		} else {
			lblNbQuestionsCorrectementRepondues
					.setText("Vous avez r�pondu incorrectement � l'ensemble des " + nbQuestions + " questions.");
		}

	}

}