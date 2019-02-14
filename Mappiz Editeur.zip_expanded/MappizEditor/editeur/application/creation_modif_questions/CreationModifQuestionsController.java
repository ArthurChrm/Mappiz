package application.creation_modif_questions;

import javafx.scene.shape.Polygon;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

import application.Question_Editeur;
import application.Theme_Editeur;
import application.Utile_Editeur;
import application.accueil.AccueilController_Editeur;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CreationModifQuestionsController {

	@FXML
	private ListView<String> listVReponses;

	@FXML
	private Button btnRecommencerLePolygone;

	@FXML
	private Button btnSupprimerLaReponse;

	@FXML
	private Button btnAjouterUneReponse;

	@FXML
	private Button btnParcourirImage;

	@FXML
	private Button btnRetour;

	@FXML
	private Button btnSupprimerLaQuestion;

	@FXML
	private Button btnAjouterUneQuestion;

	@FXML
	private Button btnTerminer;

	@FXML
	private TextField textFIntituleQuestion;

	@FXML
	private TextField textFURLDeLImage;

	@FXML
	private CheckBox checkBoxBonneReponse;

	@FXML
	private Pane paneImageQuestion;

	@FXML
	private ButtonBar btnBarQuestions;

	@FXML
	private ScrollPane scrollPQuestions;

	private Stage stage;

	private int mode;

	private String nomDuTheme = "";

	private int nbReponses = 0;
	private int nbQuestions = 0;
	private int questionSelectionnee = 1; // Commence � 1, pour �tre associ� aux
											// textes
	// affich�s sur les boutons
	private String lettres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private int numeroBonneReponse = 1;
	private int nbPolygonesTraces = 0;
	private String urlImageQuestion = "";
	private int numeroReponseSelectionnee = 1;
	private Boolean premiereSelectionDeLancement = true;
	private Boolean premierAjoutDeLancement = true;
	private boolean afficherErreurPolygoneInvalide = true;
	private boolean nePlusAfficherErreurPolygoneInvalide = false;

	// Liste des polygones associ�s aux r�ponses
	private ArrayList<Polygon> polygonesDesReponses;

	@FXML
	private void initialize() throws IOException {
		Utile_Editeur.setNbCaracteresMaxDansUnChamp(textFIntituleQuestion, 100);

		// On param�tre l'image de fond des boutons "Ajouter une question" et
		// "Supprimer une question"
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		// Mettre l'image de fond du bouton "ajouter"
		Image image_ajouter = new Image(this.getClass().getResourceAsStream("img_ajouter.png"));
		btnAjouterUneQuestion.setBackground(new Background(new BackgroundImage(image_ajouter, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bSize)));
		// Mettre l'image de fond du bouton "supprimer"
		Image image_supprimer = new Image(this.getClass().getResourceAsStream("img_supprimer.png"));
		btnSupprimerLaQuestion.setBackground(new Background(new BackgroundImage(image_supprimer, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bSize)));
		// Instanciation d'une nouvelle liste de polygones
		polygonesDesReponses = new ArrayList<Polygon>();

		// Si la r�ponse s�lectionn�e a chang�...
		listVReponses.getSelectionModel().selectedItemProperty().addListener(event -> {
			numeroReponseSelectionnee = listVReponses.getSelectionModel().getSelectedIndex() + 1;
			// On met � jour l'affichage de la CheckBox
			checkBoxBonneReponse.setSelected(numeroReponseSelectionnee == numeroBonneReponse);

			Polygon polygoneReponse = polygonesDesReponses.get(numeroReponseSelectionnee - 1);
			btnRecommencerLePolygone.setDisable(!(polygoneReponse.getPoints().size() >= 6));

			afficherLePolygone(listVReponses.getSelectionModel().getSelectedIndex() + 1);
		});

	}

	public void setDatas(int mode, String nomDuTheme, String description, String urlMiniatureDuTheme) throws IOException {
		this.mode = mode;
		// Toutes les informations pass�es en param�tres depuis l'interface de
		// cr�ation du th�me sont affect�es aux variables de la classe actuelle.
		this.nomDuTheme = nomDuTheme;

		Question_Editeur.setUrlDossierDesThemes(Theme_Editeur.getUrlDossierDesThemes());

		if (mode == 0) {
			System.out.println("Mode cr�ation.");
			// On cr�e le dossier de la premi�re question
			Question_Editeur.ajouterUneQuestion(nomDuTheme);
			// Ajout d'une question, car il y en a au moins une
			ajouterUneQuestion();
			// Ajout d'une premi�re proposition de r�ponse "A", car il y en a au
			// moins une par question
			ajouterUneReponse();
			selectionnerUneQuestion(1, true, false);
			// S�lectionner la premi�re question
			// Cocher la CheckBox de bonne r�ponse, pour mettre la premi�re
			// r�ponse
			// en tant que bonne r�ponse par d�faut
			checkBoxBonneReponse.setSelected(true);
			// D�sactiver le bouton de suppression de r�ponse, car au d�marrage,
			// il n'y a qu'une r�ponse et elle ne peut pas �tre supprim�e
			btnSupprimerLaReponse.setDisable(true);

			btnRecommencerLePolygone.setDisable(true);
		}
		if (mode == 1) {
			System.out.println("Mode modification.");

			Question_Editeur.setNomDuTheme(nomDuTheme);
			premierAjoutDeLancement = false;
			File[] dossiersQuestions = Question_Editeur.listerQuestionsTheme(nomDuTheme);
			nbQuestions = dossiersQuestions.length;
			for (int i = 1; i <= nbQuestions; i++) {
				Button btnQuestion = new Button(String.valueOf(i));
				btnQuestion.setOnAction(event -> {
					Button btn = (Button) event.getSource();
					if (Integer.parseInt(btn.getText()) != questionSelectionnee)
						try {
							selectionnerUneQuestion(Integer.parseInt(btn.getText()), false, false);
						} catch (NumberFormatException | IOException e) {
							e.printStackTrace();
						}
				});
				btnBarQuestions.getButtons().add(btnQuestion);
			}
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					try {
						selectionnerUneQuestion(1, false, false);
						afficherContenuQuestion(questionSelectionnee, false);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
	}

	public void setStage(Stage stage) {
		this.stage = stage;

		stage.setOnCloseRequest(event -> {
			if (stage.getTitle().startsWith("Mappiz Editor - Cr�ation d'un th�me (") || stage.getTitle().startsWith("Mappiz Editor - Modification d'un th�me (")) {
				if (questionValide()) {
					try {
						enregistrerFichiersQuestion();

						String dossierDuTheme = Theme_Editeur.getUrlDossierDesThemes() + "/" + nomDuTheme;

						// On demande une confirmation � l'utilisateur
						String phrase = "Souhaitez-vous vraiment terminer la " + (mode == 0 ? "cr�ation" : "modification") + " de ce th�me et quitter l'application ?\nIl est enregistr� dans le dossier " + (new File(dossierDuTheme).getPath()) + ".";

						// S'il souhaite quitter...
						if (!Utile_Editeur.afficherDialogueDeConfirmation("", "Termin� ?", phrase, "Oui", "Non")) {
							event.consume();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					event.consume();
					String phrase = "Le th�me ne peut �tre enregistr� que s'il est valide.\nQue voulez-vous faire ?";
					if (!Utile_Editeur.afficherDialogueDeConfirmation("", "Que voulez-vous faire ?", phrase, "Poursuivre la " + (mode == 0 ? "cr�ation" : "modification") + " du th�me", "Supprimer le th�me et quitter", 550)) {
						// S'il souhaite supprimer le th�me...
						if (Utile_Editeur.afficherDialogueDeConfirmation("", "Supprimer le th�me ?", "Souhaitez-vous vraiment supprimer le th�me nomm� \"" + nomDuTheme + "\" ?", "Oui", "Non")) {
							if (!Theme_Editeur.supprimerUnTheme(nomDuTheme)) {
								File dossierDuThemeASupprimer = new File(Theme_Editeur.getUrlDossierDesThemes() + "/" + nomDuTheme);
								if (dossierDuThemeASupprimer.exists()) {
									String phrase1 = "La suppression du th�me a �chou�. Vous pouvez essayer de le supprimer manuellement, en supprimant le dossier situ� � l'URL \"" + dossierDuThemeASupprimer.getPath() + "\".\nSouhaitez-vous que l'explorateur de fichiers pour tenter de supprimer le dossier ?";
									if (Utile_Editeur.afficherDialogueDeConfirmation("", "�chec de la suppression !", phrase1, "Oui", "Non")) {
										try {
											Runtime.getRuntime().exec("explorer.exe /select," + dossierDuThemeASupprimer.getPath());
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								} else {
									Utile_Editeur.afficherDialogueDInformation("", "Erreur lors de la suppression !", "Une erreur s'est produit lors de la suppression du th�me.", AlertType.ERROR);
								}
							}
							Platform.exit();
							System.exit(0);
						}
					}
				}
			}
		});
	}

	@FXML
	private void clickBtnParcourirImage(ActionEvent event) throws IOException {
		// On r�cup�re le stage de la fen�tre actuelle
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

		// On invite l'utilisateur � s�lectionner une image
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choisir une image...");
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.gif"));

		// On r�cup�re le fichier s�lectionn�
		File file = fileChooser.showOpenDialog(stage);
		urlImageQuestion = file.toPath().toString();

		// On r�cup�re l'image et on l'affiche dans le panel
		Image image = new Image("file:" + file.toPath().toString());
		BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
		paneImageQuestion.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bSize)));

		// On affiche l'URL de l'image dans le champs de l'URL
		textFURLDeLImage.setText(file.getAbsolutePath());

	}

	@FXML
	private void clickBtnAjouterUneReponse() {
		ajouterUneReponse();
	}

	private void ajouterUneReponse() {
		if (reponseValide(true) && nbReponses < Theme_Editeur.getNbMaxDeReponses()) {
			// Ajout du polygone associ� � la r�ponse
			Polygon polygon = new Polygon();
			polygonesDesReponses.add(polygon);
			// Ajout de la r�ponse dans la ListView
			listVReponses.getItems().add(Character.toString(lettres.charAt(nbReponses)));
			nbReponses++;
			// On s�lectionne la r�ponse
			listVReponses.getSelectionModel().select(nbReponses - 1);
			// On g�re l'activation ou non des boutons "ajouter" et "supprimer"
			if (nbReponses == Theme_Editeur.getNbMaxDeReponses())
				btnAjouterUneReponse.setDisable(true);
			btnSupprimerLaReponse.setDisable(false);
		}
	}

	@FXML
	private void clickBtnSupprimerLaReponse() {

		Polygon polygoneActuel = polygonesDesReponses.get(numeroReponseSelectionnee - 1);
		if (polygoneActuel.getPoints().size() >= 6) {
			nbPolygonesTraces--;
		}

		// Suppression du polygone associ� � la r�ponse
		polygonesDesReponses.remove(numeroReponseSelectionnee - 1);

		listVReponses.getItems().remove(listVReponses.getSelectionModel().getSelectedIndex());
		nbReponses--;

		// Remettre dans l'ordre les lettres affich�es dans la liste
		for (int i = 0; i < nbReponses; i++) {
			listVReponses.getItems().set(i, Character.toString(lettres.charAt(i)));
		}
		// Si on a supprim� la r�ponse correcte, on d�coche la CheckBox
		if (numeroReponseSelectionnee == numeroBonneReponse) {
			checkBoxBonneReponse.setSelected(false);
			numeroBonneReponse = -1;
		}
		listVReponses.getSelectionModel().select(numeroReponseSelectionnee - 1);
		if (numeroReponseSelectionnee == 0)
			listVReponses.getSelectionModel().select(0);
		btnSupprimerLaReponse.setDisable(nbReponses == 1);
		btnAjouterUneReponse.setDisable(false);
		afficherErreurPolygoneInvalide = true;
		listVReponses.setDisable(false);
	}

	@FXML
	private void clickBtnAjouterUneQuestion() throws IOException {
		// On enregistre la question dans le syst�me de dossiers
		// / fichiers
		ajouterUneQuestion();
	}

	@FXML
	private void clickBtnSupprimerLaQuestion() throws IOException {
		supprimerUneQuestion();
	}

	private void supprimerUneQuestion() throws IOException {
		if (nbQuestions > 1) {
			btnBarQuestions.getButtons().remove(questionSelectionnee - 1);
			nbQuestions--;
			Question_Editeur.supprimerUneQuestion(questionSelectionnee);
			// Renum�roter les boutons
			int compteur = 1;
			for (Node myNode : btnBarQuestions.getButtons())
				if (myNode instanceof Button) {
					((Button) myNode).setText(String.valueOf(compteur));
					compteur++;
				}
			if (questionSelectionnee == nbQuestions + 1) { // Si on a supprim�
															// la derni�re
															// question
				selectionnerUneQuestion(nbQuestions, false, true); // alors on
																	// sel�ctionne
																	// la
				// question pr�c�dente
			} else if (questionSelectionnee == 1) {
				selectionnerUneQuestion(1, false, true);
			} else {
				selectionnerUneQuestion(questionSelectionnee, false, true);
			}
		} else {
			Utile_Editeur.afficherDialogueDInformation("", "Suppression impossible !", "Vous ne pouvez pas supprimer cette question, car le th�me doit contenir au moins une question pour �tre valide !", AlertType.ERROR);
		}
	}

	private void ajouterUneQuestion() throws IOException {

		if (!premierAjoutDeLancement) {
			boolean questionValide = questionValide();
			if (questionValide) {
				if (nbQuestions < Theme_Editeur.getNbMaxDeQuestions()) {
					Question_Editeur.ajouterUneQuestion(nomDuTheme);
					nbQuestions++;
					Button btnQuestion = new Button(String.valueOf(nbQuestions));
					btnQuestion.setOnAction(event -> {
						Button btn = (Button) event.getSource();
						if (Integer.parseInt(btn.getText()) != questionSelectionnee)
							try {
								selectionnerUneQuestion(Integer.parseInt(btn.getText()), false, false);
							} catch (NumberFormatException | IOException e) {
								e.printStackTrace();
							}
					});
					btnBarQuestions.getButtons().add(btnQuestion);
					selectionnerUneQuestion(nbQuestions, true, false);
					scrollPQuestions.layout();
					scrollPQuestions.setHvalue(1);
				} else {
					String phrase = "Vous ne pouvez plus ajouter de question, car vous avez atteint le nombre maximal de " + Theme_Editeur.getNbMaxDeQuestions() + " questions fix� dans les param�tres de l'application !";
					Utile_Editeur.afficherDialogueDInformation("", "Ajout impossible !", phrase, AlertType.ERROR);
				}
			}
		} else {
			nbQuestions++;
			Button btnQuestion = new Button(String.valueOf(nbQuestions));
			btnQuestion.setOnAction(event -> {
				Button btn = (Button) event.getSource();
				if (Integer.parseInt(btn.getText()) != questionSelectionnee)
					try {
						selectionnerUneQuestion(Integer.parseInt(btn.getText()), false, false);
					} catch (NumberFormatException | IOException e) {
						e.printStackTrace();
					}
			});
			btnBarQuestions.getButtons().add(btnQuestion);
			premierAjoutDeLancement = false;
		}

	}

	private void selectionnerUneQuestion(int numQuestionASelectionner, boolean ajout, boolean suppression) throws IOException {
		// le param�tre numQuestion commence � 1

		if (premiereSelectionDeLancement) {
			// On s�lectionne la question num�rot�e "numQuestion"
			questionSelectionnee = numQuestionASelectionner;
			for (Node myNode : btnBarQuestions.getButtons())
				if (myNode instanceof Button)
					if (btnBarQuestions.getButtons().indexOf(myNode) == questionSelectionnee - 1)
						((Button) myNode).setDefaultButton(true);
					else
						((Button) myNode).setDefaultButton(false);
			premiereSelectionDeLancement = false;
		} else {
			if (suppression || questionValide()) {
				try {
					if (!suppression)
						enregistrerFichiersQuestion();
				} catch (IOException e) {
					e.printStackTrace();
				}
				afficherContenuQuestion(numQuestionASelectionner, ajout);
				// On s�lectionne la question num�rot�e "numQuestion"
				questionSelectionnee = numQuestionASelectionner;
				for (Node myNode : btnBarQuestions.getButtons())
					if (myNode instanceof Button)
						if (btnBarQuestions.getButtons().indexOf(myNode) == questionSelectionnee - 1) {
							((Button) myNode).setDefaultButton(true);
						} else {
							((Button) myNode).setDefaultButton(false);
						}
			}
		}

	}

	@FXML
	private void clickCheckBoxBonneReponse(ActionEvent event) {
		if (((CheckBox) event.getSource()).isSelected()) {
			numeroBonneReponse = listVReponses.getSelectionModel().getSelectedIndex() + 1;
		} else {
			numeroBonneReponse = -1;
		}
	}

	private boolean questionValide() {
		boolean questionValide = true;
		String phraseDErreur = "La question n'est pas valide.";

		// S'il n'y a pas d'intitul�
		if (textFIntituleQuestion.getText().isEmpty()) {
			phraseDErreur = "Vous devez donner un intitul� � la question !";
			questionValide = false;
		}
		// Si l'image sp�cifi�e n'existe pas
		else if (textFURLDeLImage.getText().isEmpty()) {
			phraseDErreur = "Vous devez ajouter une image � la question !";
			questionValide = false;
		} else if (!reponseValide(false)) {
			phraseDErreur = "Les segments du polygone ne doivent pas se croiser. Poursuivez le tra�age du polygone de sorte � �liminer les intersections, ou recommencez le tra�age en cliquant sur \"Refaire le tra�age\".";
			questionValide = false;
		}
		// S'il n'y aucune r�ponse correcte
		else if (numeroBonneReponse == -1) {
			phraseDErreur = "Aucune proposition de r�ponse n'a �t� indiqu�e comme �tant correcte !";
			questionValide = false;
		} else if (nbPolygonesTraces < nbReponses) {
			int nbPolygonesNonTraces = nbReponses - nbPolygonesTraces;
			phraseDErreur = "Au moins un polygone de proposition de r�ponse n'a pas �t� trac� !";
			if (nbPolygonesNonTraces > 1) {
				phraseDErreur = nbPolygonesNonTraces + " polygones de proposition de r�ponse n'ont pas �t� trac�s !";
			} else if (nbPolygonesNonTraces == 1) {
				phraseDErreur = "Un polygone de proposition de r�ponse n'a pas �t� trac� !";
			}
			questionValide = false;
		}
		if (!questionValide) {
			Utile_Editeur.afficherDialogueDInformation("", "Question incorrecte !", phraseDErreur, AlertType.ERROR);
		}
		return questionValide;
	}

	private boolean reponseValide(boolean afficherDialogue) {
		if (!afficherErreurPolygoneInvalide) {
			if (afficherDialogue) {
				String phrase = "Les segments du polygone ne doivent pas se croiser. Poursuivez le tra�age du polygone de sorte � �liminer les intersections, ou recommencez le tra�age en cliquant sur \"Refaire le tra�age\".";
				Utile_Editeur.afficherDialogueDInformation("", "Polygone invalide !", phrase, AlertType.ERROR);
			}
			return false;
		}
		return true;
	}

	@FXML
	private void clickPaneImageQuestion(MouseEvent event) {
		// S'il y a une image...
		if (!textFURLDeLImage.getText().isEmpty()) {
			Polygon polygoneReponse = polygonesDesReponses.get(numeroReponseSelectionnee - 1);

			int widthPaneImage = (int) paneImageQuestion.getWidth();
			int heightPaneImage = (int) paneImageQuestion.getHeight();

			polygoneReponse.getPoints().addAll(event.getX() / widthPaneImage, event.getY() / heightPaneImage);

			if (polygoneReponse.getPoints().size() == 6) {
				nbPolygonesTraces++;
				btnRecommencerLePolygone.setDisable(false);
			}

			afficherLePolygone(numeroReponseSelectionnee);
			if (polygoneReponse.getPoints().size() >= 6) {
				if (!polygoneValide(polygoneReponse)) {
					listVReponses.setDisable(true);
					if (afficherErreurPolygoneInvalide) {
						if (!nePlusAfficherErreurPolygoneInvalide) {
							String phrase = "Les segments du polygone ne doivent pas se croiser. Poursuivez le tra�age du polygone de sorte � �liminer les intersections, ou recommencez le tra�age en cliquant sur \"Refaire le tra�age\".";
							Utile_Editeur.afficherDialogueDInformationAvecCheckBox("", "Polygone invalide !", phrase, AlertType.ERROR, "Ne plus afficher", param -> {
								nePlusAfficherErreurPolygoneInvalide = param;
							});
						}
						afficherErreurPolygoneInvalide = false;
					}
				} else {
					afficherErreurPolygoneInvalide = true;
					listVReponses.setDisable(false);
				}
			}
		} else {
			Utile_Editeur.afficherDialogueDInformation("", "Question incorrecte !", "Avant de tracer un polygone de proposition de r�ponse, vous devez ajouter une image � la question. Pour cela, cliquez sur le bouton \"...\".", AlertType.ERROR);
		}
	}

	private void enregistrerFichiersQuestion() throws IOException {
		System.out.println("Enregistrement... START");

		String dossierQuestion = Theme_Editeur.getUrlDossierDesThemes() + "/" + nomDuTheme + "/" + questionSelectionnee;
		File fileImageSource = new File(urlImageQuestion);
		File fileImageDestination = new File(dossierQuestion + "\\image.png");

		Path pathImageSource = fileImageSource.toPath();
		Path pathImageDestination = fileImageDestination.toPath();

		// Si l'image a chang�...
		if (!pathImageSource.equals(pathImageDestination)) {
			Files.copy(pathImageSource, pathImageDestination, StandardCopyOption.REPLACE_EXISTING);
		}

		// On enregistre l'intitul� de la question
		// On cr��e le fichier
		Utile_Editeur.enregistrerFichier(dossierQuestion + "/intitule.txt", textFIntituleQuestion.getText());

		// On enregistre le num�ro de la r�ponse correcte
		Utile_Editeur.enregistrerFichier(dossierQuestion + "/numReponseCorrecte.txt", String.valueOf(numeroBonneReponse));

		String polygonesStr = "";
		for (Polygon monPolygone : polygonesDesReponses) {
			String points = "";
			for (int i = 0; i < monPolygone.getPoints().size(); i += 2) {
				double x = monPolygone.getPoints().get(i);
				double y = monPolygone.getPoints().get(i + 1);
				points += x + "," + y + "|";
			}
			polygonesStr += "$" + points;
		}

		Utile_Editeur.enregistrerFichier(dossierQuestion + "/coord.txt", polygonesStr);
		System.out.println("Enregistrement... END");
	}

	private void afficherContenuQuestion(int numQuestion, boolean ajout) throws IOException {
		// Si la question vient d'�tre cr��e, on affiche des choses vides
		if (ajout) {
			// On agit sur les controles utilisateur
			textFIntituleQuestion.setText("");
			textFURLDeLImage.setText("");
			for (int i = listVReponses.getItems().size() - 1; i > 0; i--)
				listVReponses.getItems().remove(i);
			paneImageQuestion.setBackground(null);
			// On efface tous les polygones affich�s sur l'image
			paneImageQuestion.getChildren().removeIf(n -> n instanceof Polygon);
			// On s�lectionne la premi�re r�ponse en tant que r�ponse correcte
			checkBoxBonneReponse.setSelected(true);
			// On active les boutons "ajouter" et "supprimer la r�ponse"
			btnAjouterUneReponse.setDisable(false);
			btnSupprimerLaReponse.setDisable(true);
			// On d�sactive le bouton "Effacer le tra�age"
			btnRecommencerLePolygone.setDisable(true);

			// On agit sur les variables
			nbReponses = 1;
			numeroBonneReponse = 1;
			nbPolygonesTraces = 0;
			urlImageQuestion = "";
			polygonesDesReponses = new ArrayList<Polygon>();

			// On cr��e le polygone par d�faut
			polygonesDesReponses.add(new Polygon());
		} else {
			Question_Editeur question = new Question_Editeur();
			Question_Editeur.setUrlDossierDesThemes(Theme_Editeur.getUrlDossierDesThemes());
			if (question.load(numQuestion, true)) {
				// On agit sur les variables
				numeroReponseSelectionnee = 1;
				nbReponses = question.getNbReponses();
				numeroBonneReponse = question.getNumBonneReponse();
				afficherErreurPolygoneInvalide = true;

				urlImageQuestion = question.getuRLImageQuestion();
				polygonesDesReponses = question.getPolygonesDesReponses();
				nbPolygonesTraces = polygonesDesReponses.size();

				// On agit sur les controles utilisateur
				textFIntituleQuestion.setText(new File(question.getIntitule()).getPath());
				textFURLDeLImage.setText(question.getuRLImageQuestion());
				listVReponses.getSelectionModel().clearSelection();
				listVReponses.getItems().clear();
				listVReponses.setDisable(false);

				for (int i = 0; i < question.getNbReponses(); i++) {
					// Ajout de la r�ponse dans la ListView
					listVReponses.getItems().add(Character.toString(lettres.charAt(i)));
				}
				// Afficher l'image de la question
				Image image = new Image("file:" + question.getuRLImageQuestion());
				BackgroundSize bSize = new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, false);
				paneImageQuestion.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, bSize)));
				// On active les boutons "ajouter" et "supprimer la r�ponse"
				btnAjouterUneReponse.setDisable(nbReponses == Theme_Editeur.getNbMaxDeReponses());
				btnSupprimerLaReponse.setDisable(nbReponses == 1);
				// On met � jour l'activation du bouton "effacer le tra�age"
				Polygon polygoneReponse1 = polygonesDesReponses.get(0);
				btnRecommencerLePolygone.setDisable(!(polygoneReponse1.getPoints().size() >= 6));
				// On efface tous les polygones affich�s sur l'image, et on
				// affiche
				// celui de la r�ponse A
				listVReponses.getSelectionModel().select(0);
			} else {
				retournerALAccueil();
			}
		}

	}

	private void afficherLePolygone(int numReponse) {
		if (numReponse - 1 != -1) {
			// On efface tous les polygones affich�s sur l'image
			paneImageQuestion.getChildren().removeIf(n -> n instanceof Polygon);

			double widthPaneImage = paneImageQuestion.getWidth();
			double heightPaneImage = paneImageQuestion.getHeight();

			Polygon polygoneAAfficher = polygonesDesReponses.get(numReponse - 1);
			Polygon polygoneBonsCoordonnees = new Polygon();

			for (int i = 0; i < polygoneAAfficher.getPoints().size(); i += 2) {
				double x = polygoneAAfficher.getPoints().get(i);
				double y = polygoneAAfficher.getPoints().get(i + 1);
				polygoneBonsCoordonnees.getPoints().addAll(x * widthPaneImage, y * heightPaneImage);
			}

			polygoneBonsCoordonnees.setFill(Color.TRANSPARENT);
			polygoneBonsCoordonnees.setStroke(Color.BLACK);

			paneImageQuestion.getChildren().add(polygoneBonsCoordonnees);
		}
	}

	@FXML
	private void clickBtnRecommencerLePolygone() {
		ObservableList<Double> pointsASupprimer = polygonesDesReponses.get(numeroReponseSelectionnee - 1).getPoints();
		Polygon polygoneActuel = polygonesDesReponses.get(numeroReponseSelectionnee - 1);
		polygoneActuel.getPoints().removeAll(pointsASupprimer);
		nbPolygonesTraces--;
		afficherLePolygone(numeroReponseSelectionnee);

		afficherErreurPolygoneInvalide = true;
		listVReponses.setDisable(false);
		btnRecommencerLePolygone.setDisable(true);
	}

	@FXML
	private void clickBtnTerminer() {
		if (questionValide()) {
			try {
				enregistrerFichiersQuestion();

				String dossierDuTheme = Theme_Editeur.getUrlDossierDesThemes() + "/" + nomDuTheme;

				// On demande une confirmation � l'utilisateur
				String phrase = "Souhaitez-vous vraiment terminer la " + (mode == 0 ? "cr�ation" : "modification") + " de ce th�me ?\nIl est enregistr� dans le dossier " + (new File(dossierDuTheme).getPath()) + ".";

				// S'il souhaite quitter...
				if (Utile_Editeur.afficherDialogueDeConfirmation("", "Termin� ?", phrase, "Oui", "Non")) {
					retournerALAccueil();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private boolean polygoneValide(Polygon polygoneATester) {
		int widthPaneImage = (int) paneImageQuestion.getWidth();
		int heightPaneImage = (int) paneImageQuestion.getHeight();
		ArrayList<Line2D> lignes = new ArrayList<Line2D>();
		double x1 = 0;
		double y1 = 0;
		double x2 = 0;
		double y2 = 0;

		boolean b = false;
		for (int i = 0; i < polygoneATester.getPoints().size(); i += 2) {
			if (!b) {
				x1 = polygoneATester.getPoints().get(i) * widthPaneImage;
				y1 = polygoneATester.getPoints().get(i + 1) * heightPaneImage;
				b = true;
			} else {
				x2 = polygoneATester.getPoints().get(i) * widthPaneImage;
				y2 = polygoneATester.getPoints().get(i + 1) * heightPaneImage;

				Line2D ligne = new Line2D.Double(x1, y1, x2, y2);
				lignes.add(ligne);
				b = false;
			}
		}

		x1 = polygoneATester.getPoints().get(0) * widthPaneImage;
		y1 = polygoneATester.getPoints().get(1) * heightPaneImage;
		x2 = polygoneATester.getPoints().get(polygoneATester.getPoints().size() - 2) * widthPaneImage;
		y2 = polygoneATester.getPoints().get(polygoneATester.getPoints().size() - 1) * heightPaneImage;
		Line2D ligne = new Line2D.Double(x1, y1, x2, y2);
		lignes.add(ligne);

		for (Line2D ligne1 : lignes) {
			for (Line2D ligne2 : lignes) {
				// Point de d�part du premier segment
				Point p1 = new Point((int) ligne1.getX1(), (int) ligne1.getY1());
				// Point d'arriv�e du premier segment
				Point q1 = new Point((int) ligne1.getX2(), (int) ligne1.getY2());
				// Point de d�part du second segment
				Point p2 = new Point((int) ligne2.getX1(), (int) ligne2.getY1());
				// Point d'arriv�e du second segment
				Point q2 = new Point((int) ligne2.getX2(), (int) ligne2.getY2());

				boolean intersection = Line2D.linesIntersect(p1.getX(), p1.getY(), q1.getX(), q1.getY(), p2.getX(), p2.getY(), q2.getX(), q2.getY());
				if (!partagentPointDExtremite(p1, q1, p2, q2)) {
					if (intersection) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean partagentPointDExtremite(Point p1, Point q1, Point p2, Point q2) {
		return p1.equals(p2) || p1.equals(q2) || q1.equals(p2) || q1.equals(q2);
	}

	private void retournerALAccueil() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/accueil/InterfaceAccueil.fxml"));
			Parent root = (Parent) loader.load();
			AccueilController_Editeur controller = (AccueilController_Editeur) loader.getController();
			controller.setStage(stage);

			// Cr�ation de la sc�ne
			Scene scene = new Scene(root, 1200, 600);

			// Param�trage du Stage principal de l'application (un
			// seul pour
			// toute
			// l'application)
			stage.setTitle("Mappiz Editor");
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}