package test;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

import battlefield.Battlefield;
import enemy.*;
import tower.*;

public class Test extends Application{

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Group root = new Group();
        //Battlefield battlefield = new Battlefield(root);


        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.show();
    }
}
