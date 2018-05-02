package view_control;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import player.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * EnemyView is the view show all the enemys
 * 
 * @author Lei
 *
 */
public class EnemyView extends Group {

	private Player player;
	final private static String[] enemys = { "brute", "prowler", "scouts", "tara", "vespid" };
	private MediaPlayer mediaPlayer;
	private MediaPlayer bgm;

	public EnemyView(Player inputPlayer) throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException, ClassNotFoundException {
		player = inputPlayer;
		this.getStylesheets().add(getClass().getClassLoader().getResource("CSS/EnemyViewStyle.css").toExternalForm());

		// background music
		bgm = new MediaPlayer(new Media(get("bgm/enemyView.mp3")));
		bgm.setVolume(0.1);
		bgm.setCycleCount(MediaPlayer.INDEFINITE);
		bgm.setAutoPlay(true);

		// background
		ImageView background = new ImageView(get("background/enemy-background.png"));
		this.getChildren().add(background);

		HBox enemyTag = new HBox();
		enemyTag.setLayoutX(10);
		enemyTag.setLayoutY(100);
		enemyTag.setSpacing(10);
		this.getChildren().add(enemyTag);
		// The gif of all the enemys
		for (int i = 0; i < enemys.length; i++) {
			Group tag = new Group();
			ImageView tagBg = new ImageView(get("elements/enemy-tag.png"));
			ImageView enemy = new ImageView(get("enemy/" + enemys[i] + "/move.gif"));
			enemy.setFitWidth(200);
			enemy.setFitHeight(200);

			VBox info = new VBox();
			info.setId("info");
			String className = "enemy." + enemys[i].substring(0, 1).toUpperCase() + enemys[i].substring(1);
			Class enemyClass = null;
			enemyClass = Class.forName(className);
			Method getDamage = enemyClass.getMethod("getDamage");
			Method getArmor = enemyClass.getMethod("getArmor");
			Method getHp = enemyClass.getMethod("getHp");
			Method getRange = enemyClass.getMethod("getRange");
			Method getName = enemyClass.getMethod("toString");
			String nameStr = (String) getName.invoke(enemyClass.newInstance());
			nameStr = nameStr.substring(0, 1).toUpperCase() + nameStr.substring(1);
			Label name = new Label("      " + nameStr);
			Label damage = new Label("Damage : " + getDamage.invoke(enemyClass.newInstance()));
			Label armor = new Label("Armor : " + getArmor.invoke(enemyClass.newInstance()));
			Label hp = new Label("Health : " + getHp.invoke(enemyClass.newInstance()));
			Label range = new Label("Range : " + getRange.invoke(enemyClass.newInstance()));
			info.getChildren().addAll(name, damage, armor, hp, range);
			tag.getChildren().addAll(tagBg, enemy, info);
			enemyTag.getChildren().add(tag);
		}

		// set exit
		Button exit = new Button();
		exit.setId("exit");
		exit.setLayoutX(930);
		this.getChildren().add(exit);
		exit.setOnAction(e -> {
			clickSound();
			VBox parent = (VBox) this.getParent();
			parent.getChildren().clear();
			parent.getChildren().add(new CommandCenterView(player));
			bgm.stop();
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
}
