package tower;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import battlefield.Battlefield;
import player.Player;
import enemy.Enemy;

/**
 * This class represents
 *
 * @author Xinyi Shao
 */
public abstract class Tower implements Serializable {

    private static final double WIDTH = 75;
    private static final double HEIGHT = 115;
    private static final double SIDE = 95;
    private static final double SIZE = 140;

    private String name;
    private double hp;
    private double armor;
    private double damage;
    private int range;
    private int fireRate;
    private int cost;

    private boolean[] equipmentSlot;
    private int equipment;

    private String status;
    protected Battlefield battlefield;
    protected ImageView imageView;
    protected ImageView selected;
    protected MediaPlayer mediaPlayer;
    private int x;
    private int y;

    public Tower(String name, double hp, double armor, double damage, int range, int fireRate, int cost) {
        this.name = name;
        this.hp = hp;
        this.armor = armor;
        this.damage = damage;
        this.range = range;
        this.fireRate = fireRate;
        this.cost = cost;

        this.equipmentSlot = new boolean[2];
        this.equipment = 0;

        updateStatus("stand");
    }

    public void attack(Timer timer) {
        Enemy target = scout();

        if (isDead())
            return;
        if (target == null) {
            if (!status.equals("stand"))
                updateStatus("stand");
        } else {
            updateStatus("attack");
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {

                        Enemy target = scout();
                        if (isDead() || status.equals("skill")) {
                            cancel();
                        }
                        else if (target == null) {
                            updateStatus("stand");
                            cancel();
                        }
                        else {
                            target.decreaseHp(damage);
                            // sound
                            /*MediaPlayer mediaPlayer = new MediaPlayer(new Media(
                                    getClass().getClassLoader().getResource("tower/" + name + "/attack.mp3").toString()));
                            mediaPlayer.setVolume(0.5);
                            mediaPlayer.play();*/
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, fireRate);
        }
    }

    protected Enemy scout() {
        Enemy enemy = battlefield.scoutEnemy(y, x);
        //
        if (enemy != null && (enemy.getX() - x - 1 <= -0.5 || enemy.getX() - x - 1 >= range))
            return null;
        return enemy;
    }

    public abstract void skill();

    protected abstract void dead();

    protected boolean isDead() {
        return hp <= 0;
    }

    @Override
    public String toString() {
        return name;
    }

    public double getHp() {
        return hp;
    }

    public void increaseHp(double num) {
        hp += num;
    }

    public void decreaseHp(double num) {
        hp -= num - armor;
        //System.out.println("Tower hp: " + hp);
        dead();
    }

    public double getArmor() {
        return armor;
    }

    public void increaseArmor(double num) {
        armor += num;
    }

    public void decreaseArmor(double num) {
        armor -= num;
    }

    public double getDamage() {
        return damage;
    }

    public void increaseDamage(double num) {
        damage += num;
    }

    public void decreaseDamage(double num) {
        damage -= num;
    }

    public int getRange() {
        return range;
    }

    public void increaseRange(int num) {
        range = (range + num > 9) ? 9 : (range + num);
    }

    public void decreaseHp(int num) {
        range = (range - num > 0) ? (range - num) : 1;
    }

    public int getCost() {
        return cost;
    }

    public void increaseCost(int num) {
        cost += num;
    }

    public void decreaseCost(int num) {
        cost = (cost - num <= 0) ? 0 : cost - num;
    }

    public boolean[] getEquipmentSlot() {
        return equipmentSlot;
    }

    public boolean unlockEquipment(Player player, int equipment) {
        equipment--; //
        if (!equipmentSlot[equipment] && player.getFunds() >= 100) {
            player.decreaseFunds(100);
            equipmentSlot[equipment] = true;
            equip(equipment + 1); //
            return true;
        } else {
            mediaPlayer = new MediaPlayer(
                    new Media(this.getClass().getClassLoader().getResource("sound/error.wav").toString()));
            mediaPlayer.setVolume(0.5);
            mediaPlayer.play();
            return false;
        }
    }

    public int getEquipment() {
        return equipment;
    }

    public void setEquipment(int equipment) {
        this.equipment = equipment;
    }

    public abstract void equip(int equipment);

    public abstract void unload();

    public String getStatus() {
        return status;
    }

    protected void updateStatus(String status) {
        try {
            this.status = status;
            String url = this.getClass().getClassLoader().getResource("tower/" + name + "/" + status + ".gif")
                    .toString();
            Image image = new Image(url);
            if (imageView == null) {
                imageView = new ImageView(image);
                imageView.setFitHeight(SIZE);
                imageView.setFitWidth(SIZE);
            }
            imageView.setImage(image);

        } catch (NullPointerException e) {
            System.out.println("Cannot find image: " + status);
        }
    }

    public void selected() {
        String url = this.getClass().getClassLoader().getResource("elements/selected.png").toString();
        Image image = new Image(url);
        selected = new ImageView(image);
        selected.setFitHeight(SIZE);
        selected.setFitWidth(SIZE);
        double realX = WIDTH + SIDE * x;
        double realY = HEIGHT + SIDE * y;
        selected.setX(realX);
        selected.setY(realY);

        if (battlefield != null)
            battlefield.addToGroup(selected);
    }

    public void unselected() {
        if (battlefield != null && selected != null)
            battlefield.removeFromGroup(selected);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    protected void setPosition(Battlefield battlefield, int x, int y) {
        this.battlefield = battlefield;
        this.x = x;
        this.y = y;

        double realX = WIDTH + SIDE * x;
        double realY = HEIGHT + SIDE * y;
        imageView.setX(realX);
        imageView.setY(realY);

        if (battlefield != null && battlefield.canAddToTowers(this)) {
            this.battlefield.addToTowers(this);
            this.battlefield.addToGroup(imageView);
        }
    }

    public abstract Tower clone(Battlefield battlefield, int x, int y);
}
