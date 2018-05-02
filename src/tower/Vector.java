package tower;

import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

import battlefield.Battlefield;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Vector extends Tower {

    private double skillArmor = 7;

    private static final int SKILL_DURATION = 5000;
    private static final int DIE_DURATION = 660;
    private static final double DIE_WIDTH = 180;

    private static final double SIZE = 140;

    private MediaPlayer mediaPlayer;

    public Vector() {
        super("vector", 200, 3, 7, 3, 200, 100);
    }

    @Override
    public void skill() {
        if (getStatus().equals("stand") || getStatus().equals("attack")) {
            battlefield.decreaseSkillSlot();
            updateStatus("shielding");
            increaseArmor(skillArmor);

            String url = getClass().getClassLoader().getResource("tower/" + toString() + "/" + "skill.mp3").toString();
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(1);
            mediaPlayer.play();

            Timer timer = new Timer();
            TimerTask shielding = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        updateStatus("shielded");
                        cancel();
                    });
                }
            };
            timer.schedule(shielding, 500);

            TimerTask shielded = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        updateStatus("stand");
                        decreaseArmor(skillArmor);
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
                increaseRange(1);
                break;
            case 2:
                unload();
                increaseSkillArmor(10);
                setEquipment(equipment);
                break;
        }
    }

    @Override
    public void unload() {
        int equipment = getEquipment();
        switch (equipment) {
            case 1:
            	increaseRange(-1);
                setEquipment(0);
                break;
            case 2:
            	increaseSkillArmor(-10);
                setEquipment(0);
                break;
        }
    }

    @Override
    public Tower clone(Battlefield battlefield, int x, int y) {
        Tower tower = new Vector();
        tower.equip(getEquipment());
        tower.setPosition(battlefield, x, y);
        return tower;
    }

    public double getSkillArmor() {
        return skillArmor;
    }

    public void increaseSkillArmor(double num) {
        skillArmor += num;
    }
}
