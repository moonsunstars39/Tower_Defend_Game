package enemy;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import battlefield.Battlefield;
import tower.Tower;

/**
 * This class represents
 * @author Xinyi Shao
 */
public abstract class Enemy implements Serializable, Comparable<Enemy>{

    private static final double WIDTH = 75;
    private static final double HEIGHT = 115;
    private static final double SIDE = 95;
    private static final double SIZE = 140;

    private String name;
    private double hp;
    private double armor;
    private double damage;
    private double moveSpeed;
    private int range;
    private int fireRate;
    private int dieDuration;

    private String status;
    protected Battlefield battlefield;
    private ImageView imageView;
    protected MediaPlayer mediaPlayer;
    private double x;
    private int y;

    public Enemy(String name, double hp, double armor, double damage, int range,
                 int fireRate, int dieDuration, double moveSpeed) {
        this.name = name;
        this.hp = hp;
        this.armor = armor;
        this.damage = damage;
        this.fireRate = fireRate;
        this.range = range;
        this.dieDuration = dieDuration;
        this.moveSpeed = moveSpeed;

        updateStatus("move");
    }

    public void attack(Timer timer) {
        Tower target = scout();

        if (isDead())
            return;
        if (target == null ) {
            if (!status.equals("move"))
                updateStatus("move");
            x = (x <= -0.5) ? x : x - moveSpeed;
            x = new BigDecimal(x).setScale(3, BigDecimal.ROUND_CEILING).doubleValue();
            imageView.setX(WIDTH + SIDE * x);
        }
        else {
            updateStatus("attack");
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(()->{
                        Tower target = scout();
                        if (isDead() || status.equals("back")) {
                            cancel();
                        }
                        else if (target == null) {
                            updateStatus("move");
                            cancel();
                        }
                        else {
                            target.decreaseHp(damage);
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask,0, fireRate);
        }
    }

    private Tower scout() {
        //
        Tower tower = battlefield.scoutTower(y, (int)(Math.ceil(x)));
        //
        if (tower != null && x - tower.getX() > range)
            return null;
        return tower;
    }

    private void dead() {
        if (isDead()) {
            battlefield.removeFromEnemies(this);
            updateStatus("die");

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(()-> {
                        battlefield.removeFromGroup(imageView);
                        cancel();
                        timer.purge();
                        timer.cancel();
                        System.gc();
                    });
                }
            };
            timer.schedule(timerTask, dieDuration);
        }
    }

    protected boolean isDead() {
        return hp <= 0;
    }

    public abstract void back();

    @Override
    public int compareTo(Enemy enemy) {
         if (this.getX() - enemy.getX() > 0)
             return 1;
         else if (this.getX() - enemy.getX() < 0)
             return -1;
         else {
             if (this.getHp() - enemy.getHp() < 0)
                 return -1;
             else if (this.getHp() - enemy.getHp() > 0)
                 return 1;
             else
                 return 0;
         }
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
        //System.out.println(name + " Enemy hp: "+ hp);
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

    public void increaseFireRate(double num) {
        fireRate += num;
    }

    public void decreaseFireRate(double num) {
        fireRate -= num;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public String getStatus() {
        return status;
    }

    protected void updateStatus(String status) {
        try {
            this.status = status;
            String url = this.getClass().getClassLoader().getResource
                    ("enemy/" + name + "/"  + status + ".gif").toString();

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

    public ImageView getImageView() {
        return imageView;
    }

    protected void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    protected void setPosition(Battlefield battlefield, double x, int y) {
        this.battlefield = battlefield;
        this.x = x;
        this.y = y;

        double realX = WIDTH + SIDE * x;
        double realY = HEIGHT + SIDE * y;
        imageView.setX(realX);
        imageView.setY(realY);

        if (battlefield != null) {
            this.battlefield.addToEnemies(this);
            this.battlefield.addToGroup(imageView);
        }
    }

    public abstract Enemy clone(Battlefield battlefield, double x, int y);
}
