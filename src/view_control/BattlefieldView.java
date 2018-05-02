package view_control;

import java.util.ArrayList;

import battlefield.Battlefield;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import player.Player;
import tower.Tower;

public class BattlefieldView extends Group {
	Group battlefieldView = this;
	boolean popIsShown = false;
	private Player player;
	private Battlefield battlefield;
	private boolean placing;
	private Tower placingTower;
	private ImageView followingImage;
	private int mission;
	private Tower selected;
	private MediaPlayer mediaPlayer;
	private MediaPlayer bgm;

	public BattlefieldView(Player inputPlayer, int mission) {
		selected = null;
		this.mission = mission;
		placing = false;
		this.player = inputPlayer;
		this.getStylesheets()
				.add(getClass().getClassLoader().getResource("CSS/BattlefieldViewStyle.css").toExternalForm());
		ImageView background = new ImageView();
		switch (this.mission) {
		case 1:
			background.setImage(new Image(get("background/airport.png")));
			bgm = new MediaPlayer(new Media(get("bgm/level1.mp3")));
			break;
		case 2:
			background.setImage(new Image(get("background/forest.png")));
			bgm = new MediaPlayer(new Media(get("bgm/level2.mp3")));
			break;
		case 3:
			background.setImage(new Image(get("background/street.png")));
			bgm = new MediaPlayer(new Media(get("bgm/level3.mp3")));
		}
		bgm.setCycleCount(MediaPlayer.INDEFINITE);
		bgm.setVolume(0.05);
		bgm.setAutoPlay(true);

		background.setFitWidth(1080);
		background.setFitHeight(720);
		this.getChildren().add(background);
		battlefield = new Battlefield();
		battlefield.initialization(battlefieldView, player);

		// set grid
		Group grid = new Group();
		// TODO:should set as false
		grid.setVisible(false);
		grid.setTranslateX(0);
		grid.setTranslateY(150);
		this.getChildren().add(grid);
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 10; col++) {
				Rectangle cell = new Rectangle(95 + col * 95, row * 95, 90, 90);
				grid.getChildren().add(cell);
				cell.setId("cell");
			}
		}

		// draw grid
		for (int row = 0; row < 6; row++) {
			Line rowLine = new Line(92.5, row * 95 - 2.5, 1042.5, row * 95 - 2.5);
			rowLine.setId("grid");
			grid.getChildren().add(rowLine);
			for (int col = 1; col < 12; col++) {
				Line colLine = new Line(col * 95 - 2.5, -2.5, col * 95 - 2.5, 472.5);
				colLine.setId("grid");
				grid.getChildren().add(colLine);
			}
		}

		// set exit button
		Button exit = new Button();
		exit.setId("exit");
		exit.setLayoutX(930);
		this.getChildren().add(exit);

		// set a pop-up window for check
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (popIsShown)
					return;
				clickSound();
				popIsShown = true;
				Group pop = new Group();
				ImageView popBackground = new ImageView(new Image(get("elements/exit-check.png")));
				pop.setLayoutX(340);
				pop.setLayoutY(285);
				// set yes
				Button exitYes = new Button();
				exitYes.setId("exitYes");
				exitYes.setLayoutX(50);
				exitYes.setLayoutY(50);
				// yes handler
				exitYes.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						clickSound();
						VBox parent = (VBox) battlefieldView.getParent();
						parent.getChildren().clear();
						parent.getChildren().add(new CommandCenterView(player));
						bgm.stop();
					}
				});
				// set no
				Button exitNo = new Button();
				exitNo.setId("exitNo");
				exitNo.setLayoutX(250);
				exitNo.setLayoutY(50);
				// no handler
				exitNo.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						clickSound();
						popIsShown = false;
						battlefieldView.getChildren().remove(pop);
					}
				});
				pop.getChildren().addAll(popBackground, exitYes, exitNo);
				battlefieldView.getChildren().add(pop);
			}
		});

		// top bar
		ArrayList<Tower> towerDatabase = player.getTowerDatabase();
		HBox topBar = new HBox();
		topBar.setId("topBar");
		topBar.setLayoutX(160);
		this.getChildren().add(topBar);
		for (int i = 0; i < towerDatabase.size(); i++) {
			Tower theTower = towerDatabase.get(i);
			String towerName = towerDatabase.get(i).toString();
			Group tagGroup = new Group();
			ImageView tagBg = new ImageView(get("elements/top-bar-tag.png"));
			tagBg.setFitWidth(100);
			tagBg.setFitHeight(120);
			Image towerImage = new Image(get("tower/" + towerName + "/stand.gif"));
			ImageView tower = new ImageView(towerImage);
			tower.setFitWidth(100);
			tower.setFitHeight(100);
			tower.setX(5);
			Label cost = new Label("" + theTower.getCost());
			cost.setId("cost");
			tagGroup.getChildren().addAll(tagBg, tower, cost);
			topBar.getChildren().add(tagGroup);

			// handler
			tagGroup.setOnMouseClicked(event -> {
				if (!placing && event.getButton() == MouseButton.PRIMARY) {
					if (battlefield.getMoney() >= theTower.getCost()) {
						placing = !placing;
						followingImage = new ImageView(towerImage);
						followingImage.setX(event.getSceneX() - 50);
						if (event.getSceneY() - 50 > 0)
							followingImage.setY(event.getSceneY() - 50);
						else
							followingImage.setY(0);
						followingImage.setFitWidth(100);
						followingImage.setFitHeight(100);
						this.getChildren().add(followingImage);
						placingTower = theTower;
					} else
						errorSound();
				}
			});
		}

		// Skill
		Group skill = new Group();
		ImageView skillImage = new ImageView(get("elements/null-skill.png"));
		skillImage.setFitWidth(160);
		skillImage.setFitHeight(80);
		skill.setLayoutX(90);
		skill.setLayoutY(630);
		battlefieldView.getChildren().add(skill);
		skill.setOnMouseClicked(e -> {
			if (selected != null) {
				if (battlefield.getSkillSlot() > 0) {
					selected.skill();
				}
			}
		});
		ImageView skillTimes = battlefield.getSkillTimes();
		skillTimes.setX(80);
		skillTimes.setFitWidth(80);
		skillTimes.setFitHeight(80);
		skill.getChildren().addAll(skillTimes, skillImage);

		this.setOnMouseMoved(event -> {
			if (placing) {
				grid.setVisible(false);
				if (event.getSceneX() - 50 > 0)
					followingImage.setX(event.getSceneX() - 50);
				if (event.getSceneY() - 50 > 0)
					followingImage.setY(event.getSceneY() - 50);
			}
		});

		this.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getX() > 92.5 && event.getX() < 1042.5
					&& event.getY() > 150 && event.getY() < 625) {
				if (placing) {
					// TODO:should set as false
					grid.setVisible(false);
					int x = (int) (event.getX() / 95) - 1;
					int y = (int) ((event.getY() - 47.5) / 95) - 1;
					battlefield.addToTowers(placingTower.clone(battlefield, x, y));
					placing = !placing;
					this.getChildren().remove(followingImage);
				}
				if (!placing) {
					if (selected != null) {
						selected.unselected();
						selected = null;
						skillImage.setImage(new Image(get("elements/null-skill.png")));
					}
					int x = (int) (event.getX() / 95) - 1;
					int y = (int) ((event.getY() - 47.5) / 95) - 1;
					selected = battlefield.getTower(y, x);
					if (selected != null) {
						selected.selected();
						skillImage.setImage(new Image(get("tower/" + selected.toString() + "/skill.png")));
					}
				}
			}

			if (event.getButton() == MouseButton.SECONDARY) {
				if (placing) {
					grid.setVisible(false);
					placing = !placing;
					this.getChildren().remove(followingImage);
				} else {
					if (selected != null) {
						selected.unselected();
						selected = null;
						skillImage.setImage(new Image(get("elements/null-skill.png")));
					}
				}
			}
		});

		// add money
		ImageView charger = new ImageView(get("elements/charger.png"));
		charger.setFitWidth(160);
		charger.setFitHeight(80);
		charger.setY(20);
		Label money = battlefield.getMoneyLabel();
		money.setId("money");
		this.getChildren().addAll(charger, money);
		battlefield.startGame();

		ImageView endGameImage = battlefield.getEndGameImageView();
		endGameImage.setOnMouseClicked(e -> {
			VBox parent = (VBox) battlefieldView.getParent();
			parent.getChildren().clear();
			parent.getChildren().add(new CommandCenterView(player));
			bgm.stop();
		});
	}

	private void clickSound() {
		Media media = new Media(get("sound/click.wav"));
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
	}

	private void errorSound() {
		Media media = new Media(get("sound/hover.wav"));
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
	}

	private String get(String file) {
		return getClass().getClassLoader().getResource(file).toString();
	}
}