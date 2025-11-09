package entities;

import levels.LevelManager;
import main.Game;
import utilz.LoadSave;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static main.Game.TILES_DEFAULT_SIZE;
import static main.Game.TILES_SIZE;
import static utilz.Constants.Directions.*;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.*;

public class Player extends Entity {

    private BufferedImage img  ;
    private BufferedImage[][] animations;
    private int aniSpeed = 25 , aniTick , aniIndex ;
    private int playerAction = IDLE;
    private boolean moving = false, attacking = false;
    private boolean up,down,jump, left, right;
    private float playerSpeed = 2f;

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    private LevelManager levelManager;
    private int[][] lvlData;
    private float xDrawOffset = 23 * Game.SCALE;
    private float yDrawOffset = 14 * Game.SCALE;
    private float airSpeed = 0f;
    private float gravity = 0.04f * Game.SCALE;
    private float jumpSpeed = -3.25f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
    private boolean inAir = false;



    public Player(float x, float y, int width, int height, LevelManager levelManager) {
        super(x, y,width,height);
        this.levelManager = levelManager;
        loadAnimations();
        initHitbox(x, y, 21 * Game.SCALE, 28 * Game.SCALE);
    }

    private void loadAnimations() {

        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

        animations = new BufferedImage[10][11];
        for (int j = 0; j < animations.length; j++)
            for (int i = 0; i < animations[j].length; i++)
                animations[j][i] = img.getSubimage(i * 78, j * 58, 78, 58);

    }

    public void update() {
        updateAnimationTick();
        setAnimation();
        updatePos();


    }
    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;

        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
    }

    public void render(Graphics g){
        g.drawImage(animations[playerAction][aniIndex], (int) (hitbox.x - xDrawOffset), (int) (hitbox.y - yDrawOffset), width, height, null);
        drawHitbox(g);
    }

    private void updatePos() {
        moving = false;

        if (jump) jump();
        if (!left && !right && !inAir)
            return;

        float xSpeed = 0;

        if (left )
            xSpeed-=playerSpeed;
        else if (right)
            xSpeed += playerSpeed;

        if(!inAir){
            if (!IsEntityOnFloor(hitbox, lvlData)){
                inAir = true;
            }
        }


        if (inAir){

            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += gravity;
                updateXPos(xSpeed);
            }else{
                hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
                if (airSpeed >0)
                    resetInAir();
                else
                    airSpeed = fallSpeedAfterCollision;
                updateXPos(xSpeed);
            }

        }else{
            updateXPos(xSpeed);

        }
        moving = true;
    }



    private void jump() {
        if (!inAir){
            inAir = true;
            airSpeed = jumpSpeed;
        }else return;
    }

    private void resetInAir() {
        inAir = false;
        airSpeed = 0;

    }


    private void updateXPos(float xSpeed) {
        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
                hitbox.x += xSpeed;
        }else{
            hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
        }
    }



    private void setAnimation() {
        int startAni = playerAction;

        if (moving)
            playerAction = RUN;
        else
            playerAction = IDLE;

        if (inAir)
            playerAction = JUMP;

        if (attacking)
            playerAction = ATTACK;

        if (startAni != playerAction)
            resetAniTick();
    }

    private void resetAniTick() {
        aniTick=0;
        aniIndex=0;
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= aniSpeed) {
            aniTick=0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(playerAction)){
                attacking = false;
                aniIndex=0;}
        }
    }



    public boolean isRight() {
        return right;
    }

    public boolean isLeft() {
        return left;
    }

    public boolean isDown() {
        return down;
    }

    public boolean isUp() {
        return up;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public void resetDirBooleans() {
        left = false;
        right = false;
        up = false;
        down = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }
}
