package entities;

import main.Game;

import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.EnemyConstants.PIG;

public class Pig extends Enemy {

    public Pig(float x, float y) {
        super(x, y, PIG_WIDTH, PIG_HEIGHT, PIG);
        initHitbox(x,y,(int)(12* Game.SCALE),(int)(13*Game.SCALE));

    }

}
