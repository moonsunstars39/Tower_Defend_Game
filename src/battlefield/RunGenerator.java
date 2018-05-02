package battlefield;

public class RunGenerator {

    public static void main(String[] args) {
        GameLevelGenerator gameLevelGenerator = new GameLevelGenerator();
        gameLevelGenerator.wirteBattlefield();
        gameLevelGenerator.readBattlefield();
        //gameLevelGenerator.saveGame();
        //gameLevelGenerator.loadGame();
    }
}
