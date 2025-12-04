package objects;

import levels.Level;
import main.Game;

import static utilz.Constants.GRAVITY;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;

public class Cannon extends GameObject {

    private int tileY;
    protected float airSpeed=25;
    protected boolean inAir = false;
    protected Level level ;

    public Cannon(int x, int y, int objType , Level level) {
        super(x, y, objType);
        this.level = level;
        tileY = y / Game.TILES_SIZE;
        initHitbox(44, 28);
        hitbox.x -= (int) (4 * Game.SCALE);
        hitbox.y += (int) (6 * Game.SCALE);
    }

    public void update() {
        if (doAnimation)
            updateAnimationTick();
    }

    public int getTileY() {
        return tileY;
    }

    public int getAniTick() {
        return aniTick;
    }


}
