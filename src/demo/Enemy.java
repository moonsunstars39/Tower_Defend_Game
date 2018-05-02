package Demo;

/**
 * This class represents enemy
 * @author xinyishao
 */
public class Enemy {
    private String name;
    public int hp;
    public int damage;
    private int amor;
    private int speed;

    public Enemy() {
        this.name = "E";
        this.hp = 50;
        this.damage = 20;
    }

    public boolean isDead(int damage) {
        hp -= damage;
        return hp <= 0;
    }

    public int getHp() {
        return hp;
    }

    @Override
    public String toString() {
        return name;
    }
}
