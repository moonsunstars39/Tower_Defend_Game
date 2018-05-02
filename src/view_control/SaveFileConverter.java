package view_control;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import player.Player;
import tower.Tower;


public class SaveFileConverter implements Serializable {
    private HashMap<String, boolean[]> towerDatabase;
    private HashMap<String, Integer> equipmentDatabase;
    private HashMap<Integer, Boolean> gameLevel;
    private int funds;

    public SaveFileConverter(Player player) {
        gameLevel = player.getGameLevelDatabase();
        funds = player.getFunds();

        for (Tower tower: player.getTowerDatabase()) {
            towerDatabase.put(tower.toString(), tower.getEquipmentSlot());
            equipmentDatabase.put(tower.toString(), tower.getEquipment());
        }
    }

    public HashMap<String, boolean[]> getTowerDatabase() {
        return towerDatabase;
    }

    public HashMap<String, Integer> getEquipmentDatabase() {
        return equipmentDatabase;
    }

    public HashMap<Integer, Boolean> getGameLevel() {
        return gameLevel;
    }

    public int getFunds() {
        return funds;
    }
}
