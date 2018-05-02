package view_control;

import javafx.application.Application;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * This class is the main class where start the game
 * 
 *
 */
public class Main extends Application {

	static final int width = 1060;
	static final int height = 710;

	private VBox mainView;

	public static void main(String[] args) {
		launch(args);
	}

	VBox window;

	@Override
	/**
	 * This is the start method that set the scene of the game
	 */
	public void start(Stage primaryStage) throws Exception {
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		window = new VBox();
		Scene scene = new Scene(window, width, height);
		// set cursor
		Image cursorImage = new Image(get("elements/cursor.png"));
		ImageCursor cursor = new ImageCursor(cursorImage, 16, 16);
		scene.setCursor(cursor);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
		mainView = new MainView();// have a Main View
		MediaPlayer movie = new MediaPlayer(new Media(get("movie/op.flv")));
		MediaView movieView = new MediaView(movie);
		setViewTo(movieView);
		movie.play();
		movie.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				setViewTo(mainView);
			}

		});
	}

	// change view
	private void setViewTo(Node newView) {
		window.getChildren().clear();
		window.getChildren().add((Node) newView);
	}

	private String get(String file) {
		return getClass().getClassLoader().getResource(file).toString();
	}
}
