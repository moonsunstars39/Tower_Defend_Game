package battlefield;

import javafx.application.Application;
import javafx.stage.Stage;
import player.Player;
import tower.Tower;
import tower.Type561;
import view_control.SaveFileConverter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class GameLevelGenerator {
    private static final String FILE_NAME = "GameLevel-3";
    private Battlefield battlefield = new Battlefield();
    private Player player = new Player();

    public void wirteBattlefield() {

        try {
            //String path = getClass().getClassLoader().getResource()
            FileOutputStream fileOutput = new FileOutputStream(FILE_NAME);
            ObjectOutputStream out = new ObjectOutputStream(fileOutput);

            out.writeObject(battlefield);

            out.close();
        } catch (IOException e) {
            System.out.println("Cannot write file");
            e.printStackTrace();
        }
    }

    // ****************************
    // Cope paste code through here, and change FIlE_NAME
    public void readBattlefield() {
        try {
            //String path = getClass().getClassLoader().getResource(FILE_NAME).toString();
            //System.out.println(path);
            FileInputStream fileInput = new FileInputStream(FILE_NAME);
            ObjectInputStream in = new ObjectInputStream(fileInput);

            Battlefield battlefield = (Battlefield) in.readObject();

            in.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Cannot find file");
            e.printStackTrace();
        }
    }

    public void saveGame() {
        try {
            FileOutputStream fileOutput = new FileOutputStream("save");
            ObjectOutputStream out = new ObjectOutputStream(fileOutput);

            SaveFileConverter saveFileConverter = new SaveFileConverter(player);
            out.writeObject(saveFileConverter);

            out.close();
        } catch (IOException e) {
            System.out.println("Cannot write file");
            e.printStackTrace();
        }
    }

    public void loadGame() {
        try {
            FileInputStream fileInput = new FileInputStream("save");
            ObjectInputStream in = new ObjectInputStream(fileInput);

            SaveFileConverter saveFileConverter = (SaveFileConverter) in.readObject();
            player.increaseFunds(saveFileConverter.getFunds());
            player.setGameLevelDatabase(saveFileConverter.getGameLevel());
            ArrayList<Tower> towerDatabase = player.getTowerDatabase();
            towerDatabase.clear();
            HashMap<String, boolean[]> saveTowerDatabase = saveFileConverter.getTowerDatabase();
            HashMap<String, Integer> saveEquipmentDatabase = saveFileConverter.getEquipmentDatabase();
            /*for (String towerName: saveTowerDatabase.keySet()) {
                switch(towerName) {
                    Tower tower;
                    case "type561":
                        tower = new Type561();
                        tower.initializeEquipment(saveTowerDatabase.);
                        towerDatabase.put()
                        break;
                    case "vector":

                        break;
                    case "m37":

                        break;
                    case "svd":

                        break;
                }
            }*/

            in.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Cannot find file");
            e.printStackTrace();
        }
    }
}
