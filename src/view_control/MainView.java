package view_control;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import player.Player;
import tower.M37;
import tower.Svd;
import tower.Vector;
/**
 * This is the MainView Class that  handle the beginning view 
 * of the game.
 *
 */
public class MainView extends VBox {

	VBox mainView = this;
	
	public MainView() {
		this.getStylesheets().add(getClass().getClassLoader().getResource("CSS/MainViewStyle.css").toExternalForm());
		this.setId("root");
		// Top: Title
		Image title = new Image(get("elements/title.png"));
		ImageView titleView = new ImageView(title);
		titleView.setPreserveRatio(true);
		titleView.setFitHeight(240);

		// Center: Buttons
		VBox buttons = new VBox();
		buttons.setAlignment(Pos.CENTER);
		Button newGame = new Button("New Game");
		Button loadGame = new Button("Load Game");
		Button exit = new Button("Exit");
		buttons.getChildren().addAll(newGame, loadGame, exit);
		buttons.setSpacing(20);
		this.setAlignment(Pos.CENTER);
		this.getChildren().addAll(titleView, buttons);
		this.setSpacing(200);

		// set event handler
		newGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Player player = new Player();

				// *** Test Only ***
				player.unlockGameLevel(2);
				player.unlockGameLevel(3);
				player.increaseFunds(500);

				clickSound();
				VBox parent = (VBox) mainView.getParent();
				parent.getChildren().clear();
				parent.getChildren().add(new CommandCenterView(player));
			}
		});
		// load game is set to call the saved game
		loadGame.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				clickSound();
				VBox parent = (VBox) mainView.getParent();
				parent.getChildren().clear();
				// TODO:Read saved file
				parent.getChildren().add(new CommandCenterView(new Player()));
			}
		});
		// exit is set to exit to the main view
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				clickSound();
				Timer t = new Timer();
				t.schedule(new Task(), 500);
			}

			class Task extends TimerTask {
				@Override
				public void run() {
					Platform.exit();
				}
			}
		});
	}

	// play sound when button clicked
	private void clickSound() {
		Media media = new Media(get("sound/click.wav"));
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
	}

	private String get(String file) {
		return getClass().getClassLoader().getResource(file).toString();
	}

}
