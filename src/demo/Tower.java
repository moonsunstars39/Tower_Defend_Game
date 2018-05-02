package Demo;

/**
 * This class represents abstract tower class
 * @author xinyishao
 */
public abstract class Tower {
    private String name;
    private int hp;
    private int damage;

    public Tower(String name, int hp, int damage) {
        this.name = name;
        this.hp = hp;
        this.damage = damage;
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
