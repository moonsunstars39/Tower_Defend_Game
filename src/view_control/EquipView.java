package view_control;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import player.Player;
import tower.Tower;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * EquipView is the view show all the towers
 * 
 *
 */
public class EquipView extends Group {

	private EquipView equipView = this;
	private Player player;
	private MediaPlayer mediaPlayer;
	private MediaPlayer bgm;

	public EquipView(Player inputPlayer) {
		player = inputPlayer;
		this.getStylesheets().add(getClass().getClassLoader().getResource("CSS/EquipViewStyle.css").toExternalForm());
		ImageView background = new ImageView(new Image(get("background/equip-background.png")));
		this.getChildren().add(background);
		// set scroll pane
		HBox towers = new HBox();
		towers.setSpacing(50);
		towers.setLayoutX(50);
		towers.setLayoutY(100);
		this.getChildren().add(towers);

		// background music
		bgm = new MediaPlayer(new Media(get("bgm/equipView.mp3")));
		bgm.setCycleCount(MediaPlayer.INDEFINITE);
		bgm.setVolume(0.3);
		bgm.setAutoPlay(true);

		// set index
		ArrayList<Tower> towerDatabase = player.getTowerDatabase();
		// Here show all the image of towers
		for (int i = 0; i < towerDatabase.size(); i++) {
			String towerName = towerDatabase.get(i).toString();
			Tower theTower = towerDatabase.get(i);
			Group indexTag = new Group();
			towers.getChildren().add(indexTag);
			Image indexTagImage = new Image(get("elements/index-tag.png"));
			ImageView indexTagBackground = new ImageView(indexTagImage);
			indexTag.getChildren().add(indexTagBackground);
			Image towerImage = new Image(get("tower/" + towerName + "/stand.gif"));
			ImageView tower = new ImageView(towerImage);
			tower.setFitWidth(200);
			tower.setFitHeight(200);
			tower.setX(10);
			indexTag.getChildren().add(tower);

			// add info
			VBox info = new VBox();
			info.setId("info");
			info.setAlignment(Pos.CENTER);
			String nameStr = theTower.toString();
			nameStr = nameStr.substring(0, 1).toUpperCase() + nameStr.substring(1);
			Label name = new Label("" + nameStr);
			Label damage = new Label("Damage : " + theTower.getDamage());
			Label armor = new Label("Armor : " + theTower.getArmor());
			Label hp = new Label("Health : " + theTower.getHp());
			Label range = new Label("Range : " + theTower.getRange());
			Label cost = new Label("Cost : " + theTower.getCost());
			info.getChildren().addAll(name, damage, armor, hp, range, cost);
			indexTag.getChildren().add(info);

			// set equipment
			ImageView equip = new ImageView();
			switch (theTower.getEquipment()) {
			case 0:
				equip.setImage(new Image(get("elements/empty-equip.png")));
				break;
			case 1:
				equip.setImage(new Image("tower/" + towerName + "/weapon1.png"));
				break;
			case 2:
				equip.setImage(new Image(get("tower/" + towerName + "/weapon2.png")));
				break;
			default:
			}

			equip.setX(25);
			equip.setY(375);
			indexTag.getChildren().add(equip);
			VBox equipMenu = new VBox();
			ImageView weapon1 = new ImageView(get("tower/" + towerName + "/weapon1.png"));
			ImageView weapon2 = new ImageView(get("tower/" + towerName + "/weapon2.png"));
			ImageView lock1 = new ImageView(get("elements/lock.png"));
			ImageView lock2 = new ImageView(get("elements/lock.png"));
			Group weapon1Group = new Group();
			Group weapon2Group = new Group();
			weapon1Group.getChildren().addAll(weapon1, lock1);
			weapon2Group.getChildren().addAll(weapon2, lock2);

			if (theTower.getEquipmentSlot()[0])
				weapon1Group.getChildren().remove(lock1);
			if (theTower.getEquipmentSlot()[1])
				weapon2Group.getChildren().remove(lock2);

			// the looks are set to upgrade the towers
			lock1.setOnMouseClicked(e -> {
				if (theTower.unlockEquipment(player, 1)) {
					indexTag.getChildren().remove(equipMenu);
					equip.setImage(new Image(get("tower/" + towerName + "/weapon1.png")));
					theTower.equip(1);
					weapon1Group.getChildren().remove(lock1);
					damage.setText("Damage : " + theTower.getDamage());
					armor.setText("Armor : " + theTower.getArmor());
					hp.setText("Health : " + theTower.getHp());
					range.setText("Range : " + theTower.getRange());
					cost.setText("Cost : " + theTower.getCost());
					clickSound();
				}
			});

			lock2.setOnMouseClicked(e -> {
				if (theTower.unlockEquipment(player, 2)) {
					indexTag.getChildren().remove(equipMenu);
					equip.setImage(new Image(get("tower/" + towerName + "/weapon2.png")));
					theTower.equip(2);
					weapon2Group.getChildren().remove(lock2);
					damage.setText("Damage : " + theTower.getDamage());
					armor.setText("Armor : " + theTower.getArmor());
					hp.setText("Health : " + theTower.getHp());
					range.setText("Range : " + theTower.getRange());
					cost.setText("Cost : " + theTower.getCost());
					clickSound();
				}
			});
			// weapon1 can be unlocked to upgrade the towers
			weapon1.setOnMouseClicked(e -> {
				if (theTower.getEquipmentSlot()[0]) {
					indexTag.getChildren().remove(equipMenu);
					equip.setImage(new Image(get("tower/" + towerName + "/weapon1.png")));
					theTower.equip(1);
					damage.setText("Damage : " + theTower.getDamage());
					armor.setText("Armor : " + theTower.getArmor());
					hp.setText("Health : " + theTower.getHp());
					range.setText("Range : " + theTower.getRange());
					cost.setText("Cost : " + theTower.getCost());
					clickSound();
				}
			});

			weapon2.setOnMouseClicked(e -> {
				if (theTower.getEquipmentSlot()[1]) {
					indexTag.getChildren().remove(equipMenu);
					equip.setImage(new Image(get("tower/" + towerName + "/weapon2.png")));
					theTower.equip(2);
					damage.setText("Damage : " + theTower.getDamage());
					armor.setText("Armor : " + theTower.getArmor());
					hp.setText("Health : " + theTower.getHp());
					range.setText("Range : " + theTower.getRange());
					cost.setText("Cost : " + theTower.getCost());
					clickSound();
				}
			});
			// Here is the equip menu
			equipMenu.getChildren().addAll(weapon1Group, weapon2Group);
			equipMenu.setLayoutX(25);
			equipMenu.setLayoutY(450);
			equip.setOnMouseClicked(e -> {
				clickSound();
				if (indexTag.getChildren().remove(equipMenu)) {
					equip.setImage(new Image(get("elements/empty-equip.png")));
					theTower.unload();
					damage.setText("Damage : " + theTower.getDamage());
					armor.setText("Armor : " + theTower.getArmor());
					hp.setText("Health : " + theTower.getHp());
					range.setText("Range : " + theTower.getRange());
					cost.setText("Cost : " + theTower.getCost());
				} else
					indexTag.getChildren().add(equipMenu);
			});

			// hover
			Label equipInfo = new Label();
			equipInfo.setId("equipInfo");
			lock1.setOnMouseEntered(e -> {
				equipInfo.setText(getInfo(theTower.toString(), 1));
				equipInfo.setLayoutX(e.getSceneX() + 2);
				equipInfo.setLayoutY(e.getSceneY() + 2);
				this.getChildren().add(equipInfo);
			});
			lock1.setOnMouseMoved(e -> {
				equipInfo.setLayoutX(e.getSceneX() + 2);
				equipInfo.setLayoutY(e.getSceneY() + 2);
			});
			lock1.setOnMouseExited(e -> {
				this.getChildren().remove(equipInfo);
			});
			weapon1.setOnMouseEntered(e -> {
				equipInfo.setText(getInfo(theTower.toString(), 1));
				equipInfo.setLayoutX(e.getSceneX() + 2);
				equipInfo.setLayoutY(e.getSceneY() + 2);
				this.getChildren().add(equipInfo);
			});
			weapon1.setOnMouseMoved(e -> {
				equipInfo.setLayoutX(e.getSceneX() + 2);
				equipInfo.setLayoutY(e.getSceneY() + 2);
			});
			weapon1.setOnMouseExited(e -> {
				this.getChildren().remove(equipInfo);
			});
			lock2.setOnMouseEntered(e -> {
				equipInfo.setText(getInfo(theTower.toString(), 2));
				equipInfo.setLayoutX(e.getSceneX() + 2);
				equipInfo.setLayoutY(e.getSceneY() - 102);
				this.getChildren().add(equipInfo);
			});
			lock2.setOnMouseMoved(e -> {
				equipInfo.setLayoutX(e.getSceneX() + 2);
				equipInfo.setLayoutY(e.getSceneY() - 102);
			});
			lock2.setOnMouseExited(e -> {
				this.getChildren().remove(equipInfo);
			});
			weapon2.setOnMouseEntered(e -> {
				equipInfo.setText(getInfo(theTower.toString(), 2));
				equipInfo.setLayoutX(e.getSceneX() + 2);
				equipInfo.setLayoutY(e.getSceneY() - 102);
				this.getChildren().add(equipInfo);
			});
			weapon2.setOnMouseMoved(e -> {
				equipInfo.setLayoutX(e.getSceneX() + 2);
				equipInfo.setLayoutY(e.getSceneY() - 102);
			});
			weapon2.setOnMouseExited(e -> {
				this.getChildren().remove(equipInfo);
			});
		}

		// set exit
		Button exit = new Button();
		exit.setId("exit");
		exit.setLayoutX(930);
		this.getChildren().add(exit);
		exit.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				clickSound();
				VBox parent = (VBox) equipView.getParent();
				parent.getChildren().clear();
				parent.getChildren().add(new CommandCenterView(player));
				bgm.stop();
			}
		});
	}

	// This is the toString method to transfer the informations
	private String get(String file) {
		return getClass().getClassLoader().getResource(file).toString();
	}

	// This is click sound method to make a sound when clicked
	private void clickSound() {
		Media media = new Media(get("sound/click.wav"));
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.play();
	}

	private String getInfo(String towerName, int weapon) {
		switch (towerName) {
		case "type561":
			return weapon == 1 ? "Mk211\nArmor-piercing Ammo\n+1 Damage\n"
					: "APCR\nHigh-Velocity ammo\n+6.5 Damage\n-2 Range";
		case "m37":
			return weapon == 1 ? "Type 3\nBallistic Plate\n+3 Armor\n" : "T4\nExoskeleton\n+250 HP\n";
		case "svd":
			return weapon == 1 ? "PM 5-25x56\nTelescopic Sight\n+5 Damage\n"
					: "VFL 6-24x56\nTelescopic Sight\n-50 cost\n";
		case "vector":
			return weapon == 1 ? "ITI Mars\nRed Dot Sight\n+1 Range\n"
					: "EOT 518\nHolographic Sight\n+10 Shield Armor\n";
		default:
			return null;
		}
	}
}
