package view_control;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import player.Player;
import tower.M37;
import tower.Svd;
import tower.Vector;

public class CommandCenterView extends BorderPane {

	private BorderPane commandCenterView = this;
	private ArrayList<ImageView> pathNode = new ArrayList<ImageView>();

	private static final int[] missionsX = { 50, 300, 500, 350, 150 };
	private static final int[] missionsY = { 25, 500, 300, 520, 600 };

	private Player player;
	private int mission;
	private MediaPlayer bgm;
	private MediaPlayer mediaPlayer;

	public CommandCenterView(Player inputPlayer) {
		mission = 1;
		this.player = inputPlayer;

		this.getStylesheets()
				.add(getClass().getClassLoader().getResource("CSS/CommandCenterViewStyle.css").toExternalForm());
		this.setId("root");

		// background music
		bgm = new MediaPlayer(new Media(get("bgm/commandCenterView.mp3")));
		bgm.setVolume(0.1);
		bgm.setCycleCount(MediaPlayer.INDEFINITE);
		bgm.setAutoPlay(true);

		// create buttons and set on the right
		VBox buttons = new VBox();
		Button start = new Button();
		Button equip = new Button();
		Button menu = new Button();
		Button enemy = new Button();
		start.setId("start");
		equip.setId("equip");
		menu.setId("menu");
		enemy.setId("enemy");
		buttons.getChildren().addAll(start, equip, enemy, menu);
		buttons.setSpacing(20);
		buttons.setAlignment(Pos.CENTER_RIGHT);
		this.setRight(buttons);

		// add hover effect to each button
		start.setOnMouseEntered(new ButtonHover());
		equip.setOnMouseEntered(new ButtonHover());
		menu.setOnMouseEntered(new ButtonHover());
		enemy.setOnMouseEntered(new ButtonHover());

		// set event handler
		start.setOnAction(event -> {
			clickSound();
			changeView(new BattlefieldView(player, mission));
		});
		equip.setOnAction(event -> {
			clickSound();
			changeView(new EquipView(player));
		});
		menu.setOnAction(event -> {
			clickSound();
			changeView(new MainView());
		});
		enemy.setOnAction(event -> {
			clickSound();
			try {
				changeView(new EnemyView(player));
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | InstantiationException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		});

		// Create map
		Group map = new Group();
		ImageView mapBackground = new ImageView(new Image(get("elements/map.png")));
		mapBackground.setFitWidth(720);
		mapBackground.setFitHeight(720);
		map.getChildren().add(mapBackground);
		this.setLeft(map);

		// Set stroke line through each node
		for (int i = 0; i < missionsX.length - 1; i++) {
			if (!player.getGameLevel(i + 2))
				continue;
			Line line = new Line();
			line.setStartX(missionsX[i] + 50);
			line.setEndX(missionsX[i + 1] + 50);
			line.setStartY(missionsY[i] + 50);
			line.setEndY(missionsY[i + 1] + 50);
			line.getStrokeDashArray().addAll(15d, 15d);
			line.setId("line");
			map.getChildren().add(line);
		}

		// create nodes
		for (int i = 0; i < missionsX.length; i++) {
			if (!player.getGameLevel(i + 1))
				continue;
			final int mission = i;
			ImageView node = new ImageView(new Image(get("elements/path-node.png")));
			if (i == 0)
				node.setImage(new Image(get("elements/path-node-selected.png")));
			node.setFitWidth(100);
			node.setFitHeight(100);
			node.setOnMouseClicked(e -> {
				Media media = new Media(get("sound/node-click.wav"));
				MediaPlayer mediaPlayer = new MediaPlayer(media);
				mediaPlayer.play();
				for (int j = 0; j < pathNode.size(); j++) {
					pathNode.get(j).setImage(new Image(get("elements/path-node.png")));
					ImageView imageView = (ImageView) e.getSource();
					imageView.setImage(new Image(get("elements/path-node-selected.png")));
				}
				this.mission = mission + 1;
			});
			node.setLayoutX(missionsX[i]);
			node.setLayoutY(missionsY[i]);
			pathNode.add(node);
		}
		map.getChildren().addAll(pathNode);

		// set funds
		StackPane funds = new StackPane();
		ImageView fundsImage = new ImageView(new Image(get("elements/funds.png")));
		fundsImage.setFitWidth(230);
		fundsImage.setFitHeight(50);
		Text fundsText = new Text("0");
		fundsText.setText("" + player.getFunds());
		fundsText.setId("fundsText");
		fundsText.setTranslateX(-20);
		funds.getChildren().addAll(fundsImage, fundsText);
		funds.setAlignment(Pos.CENTER_RIGHT);
		this.setTop(funds);
	}

	// hover effect
	private class ButtonHover implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent e) {
			Media media = new Media(get("sound/hover.wav"));
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.play();
		}
	}

	private void changeView(Node view) {
		VBox parent = (VBox) commandCenterView.getParent();
		parent.getChildren().clear();
		parent.getChildren().add(view);
		bgm.stop();
	}

	// play when clicked button
	private void clickSound() {
		Media media = new Media(get("sound/click.wav"));
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
	}

	private String get(String file) {
		return getClass().getClassLoader().getResource(file).toString();
	}
}