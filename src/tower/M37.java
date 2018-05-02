package tower;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import battlefield.Battlefield;
import enemy.Enemy;

public class M37 extends Tower {

    private double skillDamage = 200;

    private static final int SKILL_DURATION = 1500;
    private static final int DIE_DURATION = 1300;
    private static final double DIE_WIDTH = 200;

    private static final double SIZE = 140;

    private MediaPlayer mediaPlayer;

    public M37() {
        super("m37", 800, 7, 6, 2, 1500, 250);
    }

    @Override
    public void attack(Timer timer) {
        Enemy[] targets = scoutM37();
        boolean hasTarget = hasTarget(targets);

        if (isDead())
            return;
        if (!hasTarget) {
            if (!getStatus().equals("stand"))
                updateStatus("stand");
        } else {
            updateStatus("attack");
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {

                        Enemy[] targets = scoutM37();
                        boolean hasTarget = hasTarget(targets);

                        if (isDead() || getStatus().equals("skill")) {
                            cancel();
                        } else if (!hasTarget) {
                            updateStatus("stand");
                            cancel();
                        } else {
                            for (int i = 0; i < 3; i++) {
                                if (targets[i] != null)
                                    targets[i].decreaseHp(getDamage());
                            }
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 1500);
        }
    }

    private Enemy[] scoutM37() {
        Enemy[] enemies = new Enemy[3];
        for (int i = 0; i < 3; i++) {
            if (0 <= this.getY() + i - 1 && this.getY() + i - 1 <= 4) {
                Enemy enemy = battlefield.scoutEnemy(this.getY() + i - 1, this.getX());
                if (enemy != null && enemy.getX() - this.getX() - 1 > -0.5) {
                    switch (i) {
                        case 0:
                        case 2:
                            if (enemy.getX() - this.getX() - 1 >= this.getRange() - 1)
                                enemies[i] = null;
                            else
                                enemies[i] = enemy;
                            continue;
                        case 1:
                            if (enemy.getX() - this.getX() - 1 >= this.getRange())
                                enemies[i] = null;
                            else
                                enemies[i] = enemy;
                            continue;
                    }
                } else
                    enemies[i] = null;
            }
        }
        return enemies;
    }

    private boolean hasTarget(Enemy[] enemies) {
        for (int i = 0; i < 3; i++) {
            if (enemies[i] != null)
                return true;
        }
        return false;
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

            Timer timer = new Timer();
            TimerTask shielded = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        updateStatus("stand");
                        cancel();
                        timer.purge();
                        timer.cancel();
                        System.gc();
                    });
                }
            };
            timer.schedule(shielded, SKILL_DURATION);

            pushBack();
        }
    }

    private void pushBack() {
        ArrayList<Enemy>[] enemies = battlefield.getEnemies();
        for (int i = -1; i < 2; i++) {
            if (0 <= this.getY() + i && this.getY() + i <= 4) {
                ArrayList<Enemy> enemiesSlot = enemies[this.getY() + i];
                int size = enemiesSlot.size();
                for (int j = 0; j < size; j++) {
                    if (this.getX() <= enemiesSlot.get(j).getX() && enemiesSlot.get(j).getX() <= this.getX() + 1) {
                        enemiesSlot.get(j).decreaseHp(skillDamage);
                        if (size != enemiesSlot.size()) {
                            j--;
                            size--;
                        }
                        try {
                            enemiesSlot.get(j).back();
                        } catch (ArrayIndexOutOfBoundsException e) {

                        }
                    }
                }
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
                increaseArmor(3);
                setEquipment(equipment);

                break;
            case 2:
                unload();
                setEquipment(equipment);
                increaseHp(250);
                break;
        }
    }

    @Override
    public void unload() {
        int equipment = getEquipment();
        switch (equipment) {
            case 1:
                setEquipment(0);
                decreaseArmor(3);
                break;
            case 2:
                setEquipment(0);
                decreaseHp(250);
                break;
        }
    }

    @Override
    public Tower clone(Battlefield battlefield, int x, int y) {
        Tower tower = new M37();
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
