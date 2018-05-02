package battlefield;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.Serializable;
import java.util.*;

import enemy.*;
import tower.*;
import player.Player;

/**
 * This class represents
 *
 * @author Xinyi Shao
 */
public class Battlefield implements Serializable {

    private static final int GAME_DURATION = 300000;
    private static final int FUNDS_BONUS = 200;

    private static final int REFRESH_TIME = 33;
    private static final int ROW = 5;
    private static final int COL = 10;

    private Tower[][] towers = new Tower[ROW][COL];
    private ArrayList<Enemy>[] enemies = new ArrayList[ROW];
    private ArrayList<Enemy> enemyDatabase = new ArrayList<>();

    private Player player;
    private Group group;
    private int money;
    private Label moneyLabel;
    private int skillSlot;
    private ImageView skillTimes;
    private ImageView endGameImage;
    private HashMap<String, MediaPlayer> mediaPlayers;

    public Battlefield() {
        startGame();
    }

    public void initialization(Group group, Player player) {
        this.group = group;
        this.player = player;
        this.money = 0;
        moneyLabel = new Label();
        moneyLabel.setText("" + money);
        this.skillSlot = 0;
        setSkillTimes(0);
        endGameImage = new ImageView();

        mediaPlayers = new HashMap<>();
        mediaPlayers.put("vector", mediaPlayerHelper("tower", "vector"));
        mediaPlayers.put("type561", mediaPlayerHelper("tower", "type561"));
        mediaPlayers.put("m37", mediaPlayerHelper("tower", "m37"));
        mediaPlayers.put("svd", mediaPlayerHelper("tower", "svd"));
        mediaPlayers.put("tara", mediaPlayerHelper("enemy", "tara"));
        mediaPlayers.put("scouts", mediaPlayerHelper("enemy", "scouts"));
        mediaPlayers.put("brute", mediaPlayerHelper("enemy", "brute"));
        mediaPlayers.put("vespid", mediaPlayerHelper("enemy", "vespid"));
        mediaPlayers.put("prowler", mediaPlayerHelper("enemy", "prowler"));

        for (int i = 0; i < ROW; i++)
            enemies[i] = new ArrayList<>();
        enemyDatabase.add(new Tara());
        enemyDatabase.add(new Scouts());
        enemyDatabase.add(new Brute());
        enemyDatabase.add(new Vespid());
        enemyDatabase.add(new Prowler());

		/*Tara tara = new Tara();
		//tara.clone(this, 10, 0);
		//tara.clone(this, 11, 0);
		//tara.clone(this, 12, 0);
		Scouts scouts = new Scouts();
		//scouts.clone(this, 10, 1);
		//scouts.clone(this, 11, 1);
		//scouts.clone(this, 12, 1);
		Brute brute = new Brute();
		brute.clone(this, 10, 2);
		//brute.clone(this, 11, 2);
		//brute.clone(this, 12, 2);
		Vespid vespid = new Vespid();
		vespid.clone(this, 10, 3);
		//vespid.clone(this, 11, 3);
		//vespid.clone(this, 12, 3);
		Prowler prowler = new Prowler();
		prowler.clone(this, 10, 4);
		//prowler.clone(this, 11, 4);
		//prowler.clone(this, 12, 4);*/
    }

    public void startGame() {
        if (group != null && player != null) {

            Counter counter = new Counter(this);
            Timer timer = new Timer();

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        timer.purge();

                        if (gameOver()) {
                            String endGameImagePath = "";
                            if (won()) {
                                endGameImagePath = "victory";
                                player.increaseFunds(FUNDS_BONUS);
                                // TO DO:Unlock Tower
                            } else if (lost()) {
                                endGameImagePath = "game-over";
                            }

                            endGameImage.setImage(new Image(this.getClass().getClassLoader()
                                    .getResource(String.format("elements/%s.png", endGameImagePath)).toString()));
                            try {
                                group.getChildren().add(endGameImage);
                            } catch (IllegalArgumentException e) {
                            }
                            //
                            timer.purge();
                            timer.cancel();
                            System.gc();

                        } else {
                            counter.countOnce();
                            for (int i = 0; i < ROW; i++) {
                                //
                                for (int j = 0; j < COL; j++) {
                                    if (towers[i][j] != null && towers[i][j].getStatus().equals("stand"))
                                        towers[i][j].attack(timer);
                                    if (towers[i][j] != null && towers[i][j].getStatus().equals("attack")) {
                                        if (!mediaPlayers.get(towers[i][j].toString()).getStatus().equals(MediaPlayer.Status.PLAYING))
                                                mediaPlayers.get(towers[i][j].toString()).play();
                                    }
                                }
                                //
                                for (int k = 0; k < enemies[i].size(); k++) {
                                    if (enemies[i].get(k).getStatus().equals("move"))
                                        enemies[i].get(k).attack(timer);
                                    /*if (aenemies[i].get(k).getStatus().equals("attack")) {
                                        if (!mediaPlayers.get(enemies[i].get(k).toString()).getStatus().equals(MediaPlayer.Status.PLAYING))
                                            mediaPlayers.get(enemies[i].get(k).toString()).play();
                                    }*/
                                }
                            }
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask, 1000, REFRESH_TIME);
        }
    }

    private class Counter {
        int second = 0;
        int count = 0;
        int enemyBound = 2;

        Random random = new Random();
        Battlefield battlefield;

        Counter(Battlefield battlefield) {
            this.battlefield = battlefield;
        }

        void countOnce() {
            count++;
            oneSecond();
        }

        void oneSecond() {
            if (count % 30 == 0) {
                count = 0;
                second++;
                increaseMoney();

                if (second == GAME_DURATION / 1000) {
                    for (int i = 0; i < 30; i++)
                        enemyComing();
                    enemyDatabase.clear();
                }
                if (second == 240) {
                    for (int i = 0; i < 20; i++)
                        enemyComing();
                }
                if (second == 180) {
                    enemyBound = 5;
                    for (int i = 0; i < 15; i++)
                        enemyComing();
                }
                if (second == 120) {
                    enemyBound = 4;
                    for (int i = 0; i < 10; i++)
                        enemyComing();
                }
                if (second == 60) {
                    for (int i = 0; i < 5; i++)
                        enemyComing();
                }
                if (second % 10 == 0) {
                    increaseSkillSlot();
                    enemyComing();
                    if (second > 60)
                        enemyComing();
                    if (second > 120)
                        enemyComing();
                }
            }
        }

        void enemyComing() {
            if (!enemyDatabase.isEmpty()) {
                int randomEnemy = random.nextInt(enemyBound);
                int randomRow = random.nextInt(5);
                int size = enemies[randomRow].size();

                if (size != 0 && enemies[randomRow].get(size - 1).getX() >= 9) {
                    double position = enemies[randomRow].get(size - 1).getX()
                            + enemyDatabase.get(randomEnemy).getMoveSpeed() * 100;
                    enemyDatabase.get(randomEnemy).clone(battlefield, position, randomRow);
                } else {
                    enemyDatabase.get(randomEnemy).clone(battlefield, 10, randomRow);
                }
            }
        }
    }

    private boolean gameOver() {
        return won() || lost();
    }

    private boolean won() {
        if (!enemyDatabase.isEmpty())
            return false;
        for (int i = 0; i < ROW; i++) {
            if (!enemies[i].isEmpty())
                return false;
        }
        return true;
    }

    private boolean lost() {
        for (int i = 0; i < ROW; i++) {
            Enemy enemy = scoutEnemy(i, -1);
            if (enemy != null && enemy.getX() <= -0.5)
                return true;
        }
        return false;
    }

    public Tower scoutTower(int row, int col) {
        int temp = (col > COL) ? COL : col;
        for (int i = temp - 1; i >= 0; i--) {
            if (towers[row][i] != null)
                return towers[row][i];
        }
        return null;
    }

    public Tower getTower(int row, int col) {
        return towers[row][col];
    }

    public boolean canAddToTowers(Tower tower) {
        if (0 <= tower.getY() && tower.getY() <= ROW - 1 && 0 <= tower.getX() && tower.getX() <= COL - 1) {
            if (money >= tower.getCost()) {
                if (towers[tower.getY()][tower.getX()] == null) {
                    return true;
                } else {
                    // mediaPlayer.play();
                    return false;
                }
            } else {
                // mediaPlayer.play();
                return false;
            }

        } else {
            // mediaPlayer.play();
            return false;
        }
    }

    public void addToTowers(Tower tower) {
        if (canAddToTowers(tower)) {
            towers[tower.getY()][tower.getX()] = tower;
            decreaseMoney(tower.getCost());
        }
    }

    public void removeFromTowers(Tower tower) {
        if (towers[tower.getY()][tower.getX()] != null)
            towers[tower.getY()][tower.getX()] = null;
    }

    public Enemy scoutEnemy(int row, int x) {
        ArrayList<Enemy> enemy = enemies[row];
        Collections.sort(enemy);
        if (enemy.isEmpty())
            return null;
        for (Enemy target : enemy) {
            if (target.getX() >= x) {
                return target;
            }
        }
        return null;
    }

    public ArrayList<Enemy>[] getEnemies() {
        return enemies;
    }

    public void addToEnemies(Enemy enemy) {
        enemies[enemy.getY()].add(enemy);
    }

    public void removeFromEnemies(Enemy enemy) {
        if (enemies[enemy.getY()].contains(enemy))
            enemies[enemy.getY()].remove(enemy);
    }

    public void addToGroup(ImageView imageView) {
        group.getChildren().add(imageView);
    }

    public void removeFromGroup(ImageView imageView) {
        group.getChildren().remove(imageView);
    }

    public int getMoney() {
        return money;
    }

    public Label getMoneyLabel() {
        return moneyLabel;
    }

    public void increaseMoney() {
        money += 20;
        moneyLabel.setText("" + money);
    }

    public void decreaseMoney(int num) {
        money -= num;
        moneyLabel.setText("" + money);
    }

    public int getSkillSlot() {
        return skillSlot;
    }

    public void increaseSkillSlot() {
        if (skillSlot < 5) {
            skillSlot++;
            setSkillTimes(skillSlot);
        }
    }

    public void decreaseSkillSlot() {
        if (canUseSkill()) {
            skillSlot--;
            setSkillTimes(skillSlot);
        }
    }

    public boolean canUseSkill() {
        return skillSlot > 0;
    }

    public ImageView getSkillTimes() {
        return skillTimes;
    }

    public void setSkillTimes(int num) {
        String url = this.getClass().getClassLoader().getResource("elements/" + num + ".png").toString();
        Image image = new Image(url);
        if (skillTimes == null) {
            skillTimes = new ImageView(image);
        }
        skillTimes.setImage(image);
    }

    public ImageView getEndGameImageView() {
        return endGameImage;
    }

    private MediaPlayer mediaPlayerHelper(String type, String name) {
        String url = getClass().getClassLoader().getResource(type + "/" + name + "/" + "attack.mp3").toString();
        Media media = new Media(url);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.75);
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayers.put(name, mediaPlayerHelper(type, name));
            System.gc();
        });
        return mediaPlayer;
    }
}