package objects;

import levels.Level;
import main.Game;

import static utilz.Constants.GRAVITY;
import static utilz.Constants.ObjectConstants.BOX;
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
        createHitbox();
    }

    public void update() {
        if (doAnimation)
            updateAnimationTick();
        updateInAir(level.getLevelData());
    }

    public int getTileY() {
        return tileY;
    }

    public int getAniTick() {
        return aniTick;
    }

    private void createHitbox() {

        initHitbox(27, 23);

        xDrawOffset = (int) (16 * Game.SCALE);
        yDrawOffset = (int) (6 * Game.SCALE);



        hitbox.y += yDrawOffset + (int) (Game.SCALE * 2);
        hitbox.x += xDrawOffset / 2;
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
