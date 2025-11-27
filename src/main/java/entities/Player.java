package entities;

import gamestates.Playing;
import levels.LevelManager;
import main.Game;
import utilz.LoadSave;


import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

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
    private float playerSpeed = 1.0f* Game.SCALE;

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

    //Status Bar UI
    private BufferedImage statusBarImg;

    private int statusBarWidth = (int) (192 * Game.SCALE);
    private int statusBarHeight = (int) (58 * Game.SCALE);
    private int statusBarX = (int) (10 * Game.SCALE);
    private int statusBarY = (int) (10 * Game.SCALE);

    private int healthBarWidth = (int) (150 * Game.SCALE);
    private int healthBarHeight = (int) (4 * Game.SCALE);
    private int healthBarXStart = (int) (34 * Game.SCALE);
    private int healthBarYStart = (int) (14 * Game.SCALE);

    private int maxHealth = 10;
    private int currentHealth = maxHealth;
    private int healthWidth = healthBarWidth;

    //AttackBox
    private Rectangle2D.Float attackBox;

    private int flipX = 0;
    private int flipW = 1;

    private boolean attackChecked;
    private Playing playing;

    



    public Player(float x, float y, int width, int height, LevelManager levelManager) {
        super(x, y,width,height);
        this.levelManager = levelManager;
        loadAnimations();
        initHitbox(x, y, (int)21 * Game.SCALE, (int)28 * Game.SCALE);
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (20 * Game.SCALE), (int) (40 * Game.SCALE));
    }

    private void loadAnimations() {

        BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);

        animations = new BufferedImage[10][11];
        for (int j = 0; j < animations.length; j++)
            for (int i = 0; i < animations[j].length; i++)
                animations[j][i] = img.getSubimage(i * 78, j * 58, 78, 58);

        statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);

    }

    public void update() {
        updateHealthBar();
        updateAttackBox();
        
        updateAnimationTick();
        setAnimation();
        updatePos();


    }

    private void updateAttackBox() {
        if (right)
            attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 10);
        else if (left)
            attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 10);

        attackBox.y = hitbox.y + (Game.SCALE * 10)-20;
    }

    private void updateHealthBar() {
        healthWidth = (int) ((currentHealth / (float) maxHealth) * healthBarWidth);
    }

    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;

        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
    }

    public void render(Graphics g, int xLvlOffset){
        g.drawImage(animations[playerAction][aniIndex],
                (int) (hitbox.x - xDrawOffset)-xLvlOffset+(flipX),
                (int) (hitbox.y - yDrawOffset),
                width*flipW,
                height,
                null);
        drawHitbox(g,xLvlOffset);
        drawAttackBox(g,xLvlOffset);

        drawUI(g);
    }

    private void drawAttackBox(Graphics g, int xLvlOffset) {
        g.setColor(Color.blue);
        g.drawRect((int) attackBox.x - xLvlOffset, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
    }

    private void drawUI(Graphics g) {
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        g.setColor(Color.red);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
        //healthBarXStart :  the offset from image to the actual bar

    }

    private void updatePos() {
        moving = false;

        if (jump) jump();
//        if (!left && !right && !inAir)
//            return;
        if (!inAir)
            if ((!left && !right) || (right && left))
                return;

        float xSpeed = 0;

        if (left ){
            xSpeed-=playerSpeed;
            flipX = width;
            flipW = -1;
            xDrawOffset=40* Game.SCALE;
        }
        else if (right) {
            xSpeed += playerSpeed;
            flipX = 0;
            flipW =1;
            xDrawOffset=23* Game.SCALE;
        }
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

    public void changeHealth(int value){
        currentHealth += value;

        if (currentHealth <= 0)
            currentHealth = 0;
            //gaem over
        else if (currentHealth >= maxHealth)
            currentHealth = maxHealth;
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
