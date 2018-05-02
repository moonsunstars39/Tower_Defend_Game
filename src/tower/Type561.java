package tower;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import battlefield.Battlefield;
import enemy.Enemy;

public class Type561 extends Tower {

	private double skillDamage = 100;

	private static final int SKILL_DURATION = 1620;
    private static final int GRENADE_DURATION = 1620;
	private static final int DIE_DURATION = 1400;
    private static final double DIE_WIDTH = 160;

	private static final double WIDTH = 75;
	private static final double HEIGHT = 115;
	private static final double SIDE = 95;
	private static final double SIZE = 140;

	private MediaPlayer mediaPlayer;

	public Type561() {
		super("type561", 150, 2, 14, 6, 700, 200);
	}

	@Override
	public void skill() {
		if (getStatus().equals("stand") || getStatus().equals("attack")) {
            battlefield.decreaseSkillSlot();

            Timer timer = new Timer();

            int realX;
            if (getStatus().equals("stand")) {
                realX = (this.getX() + 6 > 9) ? 9 : this.getX() + 6;
            } else {
                Enemy enemy = scout();
                realX = (int) enemy.getX();
            }

            String url = getClass().getClassLoader().getResource("tower/" + toString() + "/" + "skill.mp3").toString();
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setVolume(1);
            mediaPlayer.play();

            updateStatus("skill");

            grenade(timer, realX);

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        grenadeDamage(realX);
                        updateStatus("stand");
                        cancel();
                        timer.purge();
                        timer.cancel();
                        System.gc();
                    });
                }
            };
            timer.schedule(timerTask, SKILL_DURATION);
		}
	}

	private void grenade(Timer timer, int x) {
		String url = this.getClass().getClassLoader().getResource("tower/type561/grenade.gif").toString();
		Image image = new Image(url);
		double realX = WIDTH + SIDE * (x - 1);
		double realY = HEIGHT + SIDE * (this.getY() - 1);
		ImageView grenade = new ImageView(image);
		grenade.setX(realX);
		grenade.setY(realY);
		grenade.setFitHeight(SIDE * 3);
		grenade.setFitWidth(SIDE * 3);

		battlefield.addToGroup(grenade);
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> {
					battlefield.removeFromGroup(grenade);
					cancel();
				});
			}
		};
		timer.schedule(timerTask, GRENADE_DURATION);

	}

	private void grenadeDamage(int x) {
		ArrayList<Enemy>[] enemies = battlefield.getEnemies();
		for (int i = -1; i < 2; i++) {
			if (0 <= this.getY() + i && this.getY() + i <= 4) {
				ArrayList<Enemy> enemiesSlot = enemies[this.getY() + i];
				int size = enemiesSlot.size();
				for (int j = 0; j < enemiesSlot.size(); j++) {
					if (x - 1 <= enemiesSlot.get(j).getX() && enemiesSlot.get(j).getX() <= x + 1)
						enemiesSlot.get(j).decreaseHp(skillDamage);
					if (size != enemiesSlot.size()) {
						j--;
						size--;
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
	                increaseDamage(2);
	                setEquipment(equipment);
	                break;
	            case 2:
	                unload();
	                increaseDamage(6.5);
	                increaseRange(-2);
	                setEquipment(equipment);
	                break;
	        }
	    }

	    @Override
	    public void unload() {
	        int equipment = getEquipment();
	        switch (equipment) {
	            case 1:	
	                decreaseDamage(2);
	                setEquipment(0);
	                break;
	            case 2:
	            	decreaseDamage(6.5);
	            	increaseRange(2);
	                setEquipment(0);
	                break;
	        }
	    }


	@Override
	public Tower clone(Battlefield battlefield, int x, int y) {
		Tower tower = new Type561();
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
