package tower;

import javafx.application.Platform;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import battlefield.Battlefield;
import enemy.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;


public class Svd extends Tower {

    private double skillDamage = 300;

    private static final int SKILL_DURATION = 1040;
    private static final int DIE_DURATION = 1100;
    private static final double DIE_WIDTH = 180;

    private static final double SIZE = 140;

    private MediaPlayer mediaPlayer;

    public Svd() {
        super("svd", 100, 1, 30, 9, 1500, 400);
    }

    @Override
    public void skill() {
        if (getStatus().equals("stand") || getStatus().equals("attack")) {
            battlefield.decreaseSkillSlot();
            updateStatus("skill");

            String url = getClass().getClassLoader().getResource("tower/" + toString() + "/" + "skill.mp3").toString();
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(1);
            mediaPlayer.play();

            ImageView imageView = getImageView();
            imageView.setFitWidth(SIZE * 10);
            Timer timer = new Timer();
            TimerTask shielded = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        snipeDamage();
                        imageView.setFitWidth(SIZE);
                        updateStatus("stand");
                        cancel();
                        timer.purge();
                        timer.cancel();
                        System.gc();
                    });
                }
            };
            timer.schedule(shielded, SKILL_DURATION);
        }
    }

    private void snipeDamage() {
        ArrayList<Enemy> enemiesSlot = battlefield.getEnemies()[this.getY()];
        int size = enemiesSlot.size();
        for (int i = 0; i < enemiesSlot.size(); i++) {
            enemiesSlot.get(i).decreaseHp(skillDamage);
            if (size != enemiesSlot.size()) {
                i--;
                size--;
            }
        }
    }

    @Override
    protected void dead() {
        if (isDead()) {
            battlefield.removeFromTowers(this);
            updateStatus("die");
            imageView.setFitWidth(DIE_WIDTH); //
            if (selected != null)
                battlefield.removeFromGroup(selected);

            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        imageView.setFitWidth(SIZE);
                        battlefield.removeFromGroup(imageView);
                        cancel();
                        timer.purge();
                        timer.cancel();
                        System.gc();
                    });
                }
            };
            timer.schedule(timerTask, DIE_DURATION);
        }
    }

    @Override
    public void equip(int equipment) {
        switch (equipment) {
            case 1:
                unload();
                setEquipment(equipment);
                increaseDamage(5);
                break;
            case 2:
                unload();
                setEquipment(equipment);
                decreaseCost(50);
                break;
        }
    }

    @Override
    public void unload() {
        int equipment = getEquipment();
        switch (equipment) {
            case 1:
                setEquipment(0);
                decreaseDamage(5);
                break;
            case 2:
                setEquipment(0);
                increaseCost(50);
                break;
        }
    }

    @Override
    public Tower clone(Battlefield battlefield, int x, int y) {
        Tower tower = new Svd();
        tower.equip(getEquipment());
        tower.setPosition(battlefield, x, y);
        return tower;
    }

    public double getSkillDamage() {
        return skillDamage;
    }

    public void increaseSkillDamage(double num) {
        skillDamage += num;
    }
}
