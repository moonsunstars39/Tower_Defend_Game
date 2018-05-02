package player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import tower.*;

public class Player implements Serializable {

    private ArrayList<Tower> towerDatabase;
    private HashMap<Integer, Boolean> gameLevel;
    private int funds;

    public Player() {
        gameLevel = new HashMap<>();
        towerDatabase = new ArrayList<>();
        funds = 0;

        towerDatabase.add(new Vector());
        towerDatabase.add(new Type561());
        towerDatabase.add(new M37());
        towerDatabase.add(new Svd());
        gameLevel.put(1, true);
        gameLevel.put(2, true);
        gameLevel.put(3, true);
    }

    public ArrayList<Tower> getTowerDatabase() {
        return towerDatabase;
    }

    public boolean containsTower(Tower tower) {
        return towerDatabase.contains(tower);
    }

    public void addTower(Tower tower) {
        if (!containsTower(tower))
            towerDatabase.add(tower);
    }

    public HashMap<Integer, Boolean> getGameLevelDatabase() {
        return gameLevel;
    }

    public void setGameLevelDatabase(HashMap<Integer, Boolean> gameLevel) {
        this.gameLevel = gameLevel;
    }

    public boolean getGameLevel(int gamelevel) {
        if (!gameLevel.containsKey(gamelevel))
            return false;
        return gameLevel.get(gamelevel);
    }

    public void unlockGameLevel(int gamelevel) {
        gameLevel.put(gamelevel, true);
    }

    public int getFunds() {
        return funds;
    }

    public void increaseFunds(int amount) {
        funds += amount;
    }

    public void decreaseFunds(int amount) {
        funds -= amount;
    }
}
