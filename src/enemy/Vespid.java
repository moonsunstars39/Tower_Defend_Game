package enemy;

import battlefield.Battlefield;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class Vespid extends Enemy {

    private static final int BACK_DURATION = 1800;
    private static final int BACK_DISTANCE = 300;

    private static final double WIDTH = 75;
    private static final double SIDE = 95;
    private static final double SIZE = 140;

    public Vespid() {
        super("vespid", 365, 1, 12.5, 2, 250, 720, 0.010);
    }

    @Override
    public void back() {
        ImageView imageView = getImageView();
        updateStatus("back");
        imageView.setFitWidth(BACK_DISTANCE);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    imageView.setX(WIDTH + SIDE * (getX() + 1));
                    setX(getX() + 1);
                    updateStatus("move");
                    imageView.setFitWidth(SIZE);
                    cancel();
                    timer.purge();
                    timer.cancel();
                    System.gc();
                });
            }
        };
        timer.schedule(timerTask, BACK_DURATION);
    }

    @Override
    public Enemy clone(Battlefield battlefield, double x, int y)  {
        Enemy enemy = new Vespid();
        enemy.setPosition(battlefield, x, y);
        return enemy;
    }
}
