package application;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

public class Utile {

	public static boolean afficherDialogueDeConfirmation(String titre, String enTete, String phrase,
			String reponsePositive, String reponseNegative, AlertType typeAlerte) {
		return afficherDialogueDeConfirmation(titre, enTete, phrase, reponsePositive, reponseNegative, typeAlerte, -1);
	}

	public static boolean afficherDialogueDeConfirmation(String titre, String enTete, String phrase,
			String reponsePositive, String reponseNegative, AlertType typeAlerte, int largeurMin) {
		ButtonType btnReponsePositive = new ButtonType(reponsePositive, ButtonBar.ButtonData.YES);
		ButtonType btnReponseNegative = new ButtonType(reponseNegative, ButtonBar.ButtonData.NO);
		Alert alerte = new Alert(typeAlerte, phrase, btnReponsePositive, btnReponseNegative);
		alerte.setTitle(titre.isEmpty() ? "Mappiz" : titre);
		alerte.setHeaderText(enTete);
		if (largeurMin > -1)
			alerte.getDialogPane().setMinWidth(largeurMin);
		Optional<ButtonType> resultat = alerte.showAndWait();

		// On retourne une valeur booléenne indiquant si l'utilisateur a cliqué
		// sur le bouton de réponse négative ("Non", "Annuler", ...)
		return (resultat.orElse(btnReponseNegative) == btnReponsePositive);
	}

	public static void afficherDialogueDInformation(String titre, String enTete, String message, AlertType typeAlerte) {
		afficherDialogueDInformation(titre, enTete, message, typeAlerte, -1);
	}

	public static void afficherDialogueDInformation(String titre, String enTete, String message, AlertType typeAlerte,
			int largeurMin) {
		Alert alerte = new Alert(typeAlerte);
		alerte.setTitle(titre.isEmpty() ? "Mappiz" : titre);
		alerte.setHeaderText(enTete);
		alerte.setContentText(message);
		if (largeurMin > -1)
			alerte.getDialogPane().setMinWidth(largeurMin);
		alerte.showAndWait();
	}

	public static void afficherDialogueDInformationAvecCheckBox(String titre, String enTete, String message,
			AlertType typeAlerte, String texteCheckBox, Consumer<Boolean> optOutAction) {
		Alert alerte = new Alert(typeAlerte);
		alerte.getDialogPane().applyCss();
		Node graphic = alerte.getDialogPane().getGraphic();
		alerte.setDialogPane(new DialogPane() {
			@Override
			protected Node createDetailsButton() {
				CheckBox optOut = new CheckBox();
				optOut.setText(texteCheckBox);
				optOut.setOnAction(e -> optOutAction.accept(optOut.isSelected()));
				return optOut;
			}
		});
		alerte.getDialogPane().getButtonTypes().add(new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE));
		alerte.getDialogPane().setContentText(message);
		alerte.getDialogPane().setExpandableContent(new Group());
		alerte.getDialogPane().setExpanded(true);
		alerte.getDialogPane().setGraphic(graphic);
		alerte.setTitle(titre);
		alerte.setHeaderText(enTete);
		alerte.setResizable(false);
		alerte.showAndWait().filter(t -> t == ButtonType.OK).isPresent();
	}

	public static void enregistrerErreurDansLog(Exception exception) {
		try {
			File fichierLog = new File(new File(".").getCanonicalPath() + "/erreurs.log");
			if (!fichierLog.exists())
				fichierLog.createNewFile();
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date date = new Date();
			String stringDateActuelle = dateFormat.format(date);
			StringWriter erreurs = new StringWriter();
			exception.printStackTrace(new PrintWriter(erreurs));
			String contenu = stringDateActuelle + System.lineSeparator() + erreurs + System.lineSeparator();
			Files.write(fichierLog.toPath(), (contenu).getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			System.err.println("Erreur lors de l'engistrement du fichier LOG.");
			e.printStackTrace();
		}
	}

}