package objects;

import static utilz.Constants.GRAVITY;
import static utilz.Constants.ObjectConstants.*;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.GetEntityYPosUnderRoofOrAboveFloor;

import levels.Level;
import main.Game;

public class GameContainer extends GameObject {

    protected float airSpeed=25;
    protected boolean inAir = false;
    protected int tileY;
    protected Level level ;

    public GameContainer(int x, int y, int objType, Level level) {
        super(x, y, objType);
        this.level = level;
        createHitbox();
    }

    private void createHitbox() {
        if (objType == BOX) {
            initHitbox(25, 18);

            xDrawOffset = (int) (7 * Game.SCALE);
            yDrawOffset = (int) (12 * Game.SCALE);

        } else {
            initHitbox(23, 25);
            xDrawOffset = (int) (8 * Game.SCALE);
            yDrawOffset = (int) (5 * Game.SCALE);
        }

        hitbox.y += yDrawOffset + (int) (Game.SCALE * 2);
        hitbox.x += xDrawOffset / 2;
    }

    public void update() {
        if (doAnimation)
            updateAnimationTick();
        updateInAir(level.getLevelData());
    }

    public void updateInAir(int[][] lvlData) {
        //the update part when the bro is flying is set into a different method as well
        //the falling logic is implemented... if he is done falling he goes to running
        if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
            hitbox.y += airSpeed;
            airSpeed += GRAVITY;
        } else {
            inAir = false;
            hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
            //this is the tile the player is in
            tileY = (int)(hitbox.y / Game.TILES_SIZE);
        }

    }
}
