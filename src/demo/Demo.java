package Demo;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class represents useless demo for sprint 1
 * We won't use this for our formal project, don't waste time to read it
 * @author xinyishao
 */
public class Demo {
    private static Battlefield battlefield = new Battlefield();
    private static ArrayList[][] map = battlefield.getBattlefield();

    private static int money = 12;

    public static void main(String[] args) {
        System.out.println("Welcome to Sprint 1 Demo");
        System.out.println("Please input by instruction to build your tower, this demo won't catch any input error");
        show();
        buildTower();
        show();
        System.out.println("enemy is coming");
        running(1);
        money += 14;
        if (lost()) {
            System.out.println("Game Over, you lost");
            return;
        }
        System.out.println("Prepare for next wave");
        show();
        buildTower();
        show();
        System.out.println("enemy is coming");
        running(2);
        if (lost())
            System.out.println("Game Over, you lost");
        else
            System.out.println("Congregations, you won!");
    }

    /**
     * Helper function while loop
     * @param waves
     */
    private static void running(int waves) {
        int i = 0;
        addEnemy();
        waves--;
        while (enemyDown()) {
            move(i);
            show();
            i++;
            if (lost())
                break;
            if (i % 2 == 0 && waves > 0) {
                addEnemy();
                waves--;
            }
        }
        clear();
    }

    /**
     * Make the map be fake dynamic
     * @param num
     */
    private static void move(int num) {
        for (int i = 0; i < 5; i++) {
            // Check from left side
            for (int j = 8; j > 0; j--) {
                switch (map[i][j].get(0).toString()) {
                    case "P":
                        if (j < 9 && num % 2 == 0 && find(map[i][j + 1], "E") == -1)
                            map[i][j + 1].add(0, "~");
                        continue;

                    case "~":
                        if ( map[i][j + 1].get(0).toString().equals("E")) {
                            Enemy enemy = (Enemy) map[i][j + 1].get(0);
                            if (enemy.isDead(10))
                                map[i][j + 1].remove(0);
                            System.out.println("enemy HP: " + enemy.getHp() + " attacked by P");
                        } else {
                            map[i][j + 1].add(0, "~");

                        }
                        map[i][j].remove("~");
                        continue;
                }
            }
            // Check from right side
            for (int j = 1; j < 10; j++) {
                switch (map[i][j].get(0).toString()) {
                    case "E":
                        Enemy enemy = (Enemy) map[i][j].get(0);
                        Tower tower;
                        // ~
                        if (map[i][j - 1].get(0).toString().equals("~")
                                && find(map[i][j - 1], "P") == -1 && find(map[i][j - 1], "B") == -1) {
                            map[i][j - 1].remove("~");
                            if (!enemy.isDead(10)) {
                                map[i][j].remove(0);
                                map[i][j - 1].add(0, enemy);
                            } else {
                                map[i][j].remove(0);
                            }
                            System.out.println("enemy HP: " + enemy.getHp() + " attacked by P");
                        }
                        // P
                        else if (find(map[i][j - 1], "P") != -1) {
                            tower = (Pikachu) map[i][j - 1].get(find(map[i][j - 1], "P"));
                            if (num % 2 == 0) {
                                if (enemy.isDead(10)) {
                                    map[i][j].remove(0);
                                }
                                System.out.println("enemy HP: " + enemy.getHp() + " melee attacked by P");
                            }
                            if (tower.isDead(20)) {
                                map[i][j - 1].remove(find(map[i][j - 1], "P"));
                                if (find(map[i][j-1], "~") != -1) {
                                    map[i][j-1].remove("~");
                                    if (enemy.isDead(10)) {
                                        map[i][j].remove(enemy);
                                    }
                                }
                                if (!enemy.isDead(0)) {
                                    map[i][j].remove(0);
                                    map[i][j - 1].add(0, enemy);
                                }
                            }
                            System.out.println("Pikachu HP: " + tower.getHp());
                        }
                        // B
                        else if (find(map[i][j - 1], "B") != -1) {
                            tower = (Bulbasaur) map[i][j - 1].get(find(map[i][j - 1], "B"));
                            if (enemy.isDead(25))
                                map[i][j].remove(0);
                            if (tower.isDead(20)) {
                                map[i][j - 1].remove(find(map[i][j - 1], "B"));
                                if (find(map[i][j-1], "~") != -1) {
                                    map[i][j-1].remove("~");
                                    if (enemy.isDead(10)) {
                                        map[i][j].remove(enemy);
                                    }
                                }
                                if (!enemy.isDead(0)) {
                                    map[i][j].remove(0);
                                    map[i][j - 1].add(0, enemy);
                                }
                            }
                            System.out.println("enemy HP: " + enemy.getHp() + " attacked by B");
                            System.out.println("Bulbasaur HP: " + tower.getHp());
                        }
                        // _
                        else {
                            if (find(map[i][j - 1], "P") == -1 && find(map[i][j - 1], "B") == -1) {
                                if (find(map[i][j - 1], "~") != -1) {
                                    map[i][j - 1].remove("~");
                                    if (!enemy.isDead(10)) {
                                        map[i][j].remove(0);
                                        map[i][j - 1].add(0, enemy);
                                    } else {
                                        map[i][j].remove(0);
                                    }
                                    System.out.println("enemy HP: " + enemy.getHp() + " attacked by P");
                                } else {
                                    map[i][j].remove(0);
                                    map[i][j - 1].add(0, enemy);
                                }
                            }
                        }
                }
            }
        }
    }

    /**
     * Helper function to build tower
     */
    private static void buildTower() {
        Scanner scanner = new Scanner(System.in);
        Tower temp;
        int row, col;

        System.out.println("Build towers to defend enemies? (Any key to build tower, \"done\" to start defend)");
        String cmd = scanner.nextLine().trim().toLowerCase();
        while (!cmd.equals("done")) {
            System.out.println("You have " + money);
            System.out.println("Pikachu or Bulbasaur? (p or b)");
            cmd = scanner.nextLine().trim().toLowerCase();
            if (cmd.equals("p") && money >= 2) {
                temp = new Pikachu();

                System.out.println("Which row? (1-5)");
                row = scanner.nextInt() - 1;
                scanner.nextLine();
                System.out.println("Which column? (1-8)");
                col = scanner.nextInt();
                scanner.nextLine();
                if (!map[row][col].get(0).toString().equals("_")) {
                    System.out.println("There already exists a tower");
                    continue;
                }
                map[row][col].add(0, temp);
                money -= 2;
                show();
            } else if (cmd.equals("b") && money >= 3) {
                temp = new Bulbasaur();
                System.out.println("Which row? (1-5)");
                row = scanner.nextInt() - 1;
                scanner.nextLine();
                System.out.println("Which column? (1-8)");
                col = scanner.nextInt();
                scanner.nextLine();
                if (!map[row][col].get(0).toString().equals("_")) {
                    System.out.println("There already exists a tower");
                    continue;
                }
                map[row][col].add(0, temp);
                money -= 3;
                show();
            } else {
                System.out.println("You don't have enough money");
            }

            System.out.println("Build towers to defend enemies? (Any key to build tower, \"done\" to start defend)");
            cmd = scanner.nextLine().trim().toLowerCase();
        }
    }

    /**
     * Add enemy
     */
    private static void addEnemy() {
        Enemy[] enemies = {new Enemy(), new Enemy(), new Enemy(), new Enemy(), new Enemy()};
        for (int i = 0; i < 5; i++) {
            map[i][9].add(0, enemies[i]);
            System.out.println("enemy initial HP: " + enemies[i].getHp());
        }
    }

    /**
     * Kill all the enemy
     * @return
     */
    private static boolean enemyDown() {
        for (int i = 0; i < 5; i++) {
            for (int j = 1; j < 10; j++) {
                if (map[i][j].get(0).toString().equals("E"))
                    return true;
            }
        }
        return false;
    }

    /**
     *
     * @return true if player lost
     */
    private static boolean lost() {
        for (int i = 0; i < 5; i++)
            if (map[i][0].get(0).toString().equals("E")) {
                return true;
            }
        return false;
    }

    /**
     * clear the bullet(~) on the map
     */
    private static void clear() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < findAll(map[i][j], "~"); k++)
                    map[i][j].remove("~");
            }
        }
    }

    /**
     * find object
     * @param list
     * @param str
     * @return
     */
    private static int find(ArrayList list, String str) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).toString().equals(str))
                return i;
        }
        return -1;
    }

    /**
     * calculate the number of object
     * @param list
     * @param str
     * @return
     */
    private static int findAll(ArrayList list, String str) {
        int n = 0;
        for (Object o : list) {
            if (o.toString().equals(str))
                n++;
        }
        return n;
    }

    /**
     * print 2d array
     */
    private static void show() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 10; j++) {
                System.out.print(map[i][j].get(0).toString() + " ");
            }
            System.out.println();
        }
    }
}
