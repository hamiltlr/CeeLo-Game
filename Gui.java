// CHANGE LOG //
/*
 * 10/16/2013 5:54PM Lee - Added skeleton for game logic
 * 10/16/2013 6:10PM Lee - Added println statements to check logic, added some
 * 	comments.
 * 10/17/2013 9:30AM Lee - Added text, need to revise later, added id to p1Label
 * 10/21/2013 2:00PM Lee/Josiah - Added scoring logic
 * 10/25/2013 10:30AM Lee/Josiah - Separated logic into Game class
 * 10/28/2013 6:54PM Lee - Added new round button label and reset point labels 
 * 						   between rounds
 * 10/29/2013 11:30AM Lee/Josiah - Added die rolling animation
 */

package hamilton;

import java.io.IOException;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * File: Gui.java The class that handles the GUI layout and events.
 * 
 * @author Josiah Hamilton, Lee Hamilton
 */
public class Gui extends Application {

	Game ceelo = new Game();

	Label p1Label;
	Label p2Label;
	Label statusLabel;

	final ImageView[] images = new ImageView[3];

	TranslateTransition translate;

	@Override
	public void start(final Stage primaryStage) {
		final Button buttonRoll = new Button("Roll Dice");

		buttonRoll.setMinHeight(38);
		buttonRoll.setMinWidth(154);
		buttonRoll.setMaxHeight(38);
		buttonRoll.setMaxWidth(76);

		HBox boxPlayers = new HBox();
		HBox boxDice = new HBox();
		final VBox root = new VBox();

		p1Label = new Label("Player 1\nWins: " + ceelo.getPlayer1Wins());
		p1Label.setId("p1Label");

		p2Label = new Label("Player 2\nWins: " + ceelo.getPlayer2Wins());

		statusLabel = new Label("Player 1 starts!");

		boxPlayers.getChildren().addAll(p1Label, p2Label);
		boxPlayers.setId("boxPlayers");

		boxDice.setId("boxDice");

		for (int i = 0; i < 3; i++) {
			images[i] = new ImageView(new Image(
					Gui.class.getResourceAsStream(ceelo.getTops()[i] + ".png")));
		}

		boxDice.getChildren().addAll(images);

		buttonRoll.setOnAction(new EventHandler<ActionEvent>() {
			/**
			 * The action when the Roll button is clicked.
			 * 
			 * @param event
			 */
			@Override
			public void handle(ActionEvent event) {
				//If the button says Quit, the button should close the window
				if (buttonRoll.getText().equals("Quit")) {
					primaryStage.close();
					
				} else if (buttonRoll.getText().equals("Next round!")) {
					buttonRoll.setText("Roll Dice");
					ceelo.newRound();
					changePlayerLabels();

					statusLabel.setText("");

					p1Label.setText("Player 1\nWins: " + ceelo.getPlayer1Wins());
					p2Label.setText("Player 2\nWins: " + ceelo.getPlayer2Wins());
				} else {
					ceelo.rollDice();

					animateDice();

					for (int i = 0; i < 3; i++) {
						images[i].setImage(new Image(Gui.class
								.getResourceAsStream(ceelo.getTops()[i]
										+ ".png")));
					}

					try {
						Clip sound = AudioSystem.getClip();
						sound.open(AudioSystem.getAudioInputStream(this
								.getClass().getResource("roll.wav")));
						sound.start();
					} catch (LineUnavailableException
							| UnsupportedAudioFileException | IOException e) {
						e.printStackTrace();
					}

					p1Label.setText("Player 1\nWins: " + ceelo.getPlayer1Wins());
					p2Label.setText("Player 2\nWins: " + ceelo.getPlayer2Wins());

					if (ceelo.getPseudoScoreP1() == 50) {
						//indicates the player rolled a 4-5-6
						p1Label.setText(p1Label.getText() + "\n4-5-6");
						
					} else if (ceelo.getPseudoScoreP1() >= 1
							&& ceelo.getPseudoScoreP1() <= 6) {
						// ^ indicates that the pseudoscore was in the "point"
						// range
						p1Label.setText(p1Label.getText() + "\nPoint: "
								+ ceelo.getPseudoScoreP1());
						
					} else if (ceelo.getPseudoScoreP1() >= 7
							&& ceelo.getPseudoScoreP1() <= 42) {
						// ^indicates the the pseudoscore was in the "trips"
						// range
						p1Label.setText(p1Label.getText() + "\nTrips: "
								+ ceelo.getPseudoScoreP1() / 7);
						
					} else if (ceelo.getPseudoScoreP1() == -1) {
						//^indicates the player rolled a 1-2-3
						p1Label.setText(p1Label.getText() + "\n1-2-3");
					}

					
					// PLAYER 2
					if (ceelo.getPseudoScoreP2() == 50) {
						//indicates the player rolled a 4-5-6
						p2Label.setText(p2Label.getText() + "\n4-5-6");
						
					} else if (ceelo.getPseudoScoreP2() >= 1
							&& ceelo.getPseudoScoreP2() <= 6) {
						// ^ indicates that the pseudoscore was in the "point"
						// range
						p2Label.setText(p2Label.getText() + "\nPoint: "
								+ ceelo.getPseudoScoreP2());
						
					} else if (ceelo.getPseudoScoreP2() >= 7
							&& ceelo.getPseudoScoreP2() <= 42) {
						// ^indicates the the pseudoscore was in the "trips"
						// range
						p2Label.setText(p2Label.getText() + "\nTrips: "
								+ ceelo.getPseudoScoreP2() / 7);
						
					} else if (ceelo.getPseudoScoreP2() == -1) {
						//^indicates the player rolled a 1-2-3
						p2Label.setText(p2Label.getText() + "\n1-2-3");
					}

					statusLabel.setText(ceelo.getStatus());

					if (ceelo.isEndOfRound()) {
						if (ceelo.getPlayer1Wins() >= 5) {
							// Player 1 wins the game!
							Label winLabel = new Label("Player 1 wins!");
							winLabel.setStyle("-fx-font-size:24; -fx-font-weight: bold;");
							root.getChildren().add(2, winLabel);

							buttonRoll.setText("Quit");
							ceelo.cleanUp();
							
						} else if (ceelo.getPlayer2Wins() >= 5) {
							// Player 1 wins the game!
							Label winLabel = new Label("Player 2 wins!");
							winLabel.setStyle("-fx-font-size:24; -fx-font-weight: bold;");

							root.getChildren().add(2, winLabel);

							buttonRoll.setText("Quit");
							ceelo.cleanUp();
							
						} else {
							buttonRoll.setText("Next round!");
						}
						
					} else {
						changePlayerLabels();
					}
				}
			}
		});

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			/**
			 * If the user exits the window before the game is over, clean up
			 * the Game class.
			 * 
			 * @param event
			 */
			@Override
			public void handle(WindowEvent event) {
				ceelo.cleanUp();
			}
		});

		root.getChildren().addAll(boxPlayers, statusLabel, boxDice, buttonRoll);

		boxPlayers.setAlignment(Pos.TOP_CENTER);
		boxDice.setAlignment(Pos.CENTER);
		root.setAlignment(Pos.CENTER);

		root.getStylesheets().add(
				Gui.class.getResource("style.css").toExternalForm());

		Scene scene = new Scene(root, 300, 350);
		primaryStage.setResizable(false);

		primaryStage.setTitle("Cee-Lo");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void changePlayerLabels() {
		if (ceelo.isPlayer1Turn()) {
			p1Label.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
			p2Label.setStyle("-fx-font-weight: normal; -fx-font-size: 15;");
		} else {
			p1Label.setStyle("-fx-font-weight: normal; -fx-font-size: 15;");
			p2Label.setStyle("-fx-font-weight: bold; -fx-font-size: 20;");
		}
	}

	private void showWinScreen(String player, Stage stage) {
		VBox root = new VBox();
		Label winDisplay = new Label(player + " wins!");
		root.getChildren().add(winDisplay);
		Scene scene = new Scene(root, 300, 350);
		scene.getStylesheets().add(
				Gui.class.getResource("style.css").toExternalForm());
		stage.setScene(scene);
	}

	private void animateDice() {
		for (int i = 0; i < images.length; i++) {
			translate = new TranslateTransition(Duration.millis(500.0),
					images[i]);

			translate.setFromX(-360.0);
			translate.setFromY(images[i].getY());

			translate.setToX(images[i].getX());
			translate.setToY(images[i].getY());

			translate.play();
		}

	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}


