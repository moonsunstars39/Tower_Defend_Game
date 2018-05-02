package Demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;

public class DemoTest {

    @Test
    public void GeneralTest() {
        Battlefield battlefield = new Battlefield();
        ArrayList[][] map = battlefield.getBattlefield();
        Tower pikachu = new Pikachu();
        Tower bulbasaur = new Bulbasaur();
        Enemy enemy = new Enemy();

        assertFalse(pikachu.isDead(10));
        assertEquals(40, pikachu.getHp());
        assertTrue(pikachu.isDead(50));
        assertEquals("B", bulbasaur.toString());

        assertFalse(enemy.isDead(10));
        assertEquals(40, enemy.getHp());
        assertTrue(enemy.isDead(50));
        assertEquals("E", enemy.toString());
    }
}
