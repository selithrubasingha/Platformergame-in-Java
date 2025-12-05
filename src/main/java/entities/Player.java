package entities;

import audio.AudioPlayer;
import gamestates.Playing;
import levels.LevelManager;
import main.Game;
import utilz.LoadSave;


import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.GRAVITY;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.CanMoveHere;
import static utilz.HelpMethods.*;

public class Player extends Entity {

    private BufferedImage img  ;
    private BufferedImage[][] animations;

    private boolean moving = false, attacking = false;
    private boolean jump, left, right;

    public void setJump(boolean jump) {
        this.jump = jump;
    }

    private LevelManager levelManager;
    private int[][] lvlData;
    private float xDrawOffset = 23 * Game.SCALE;
    private float yDrawOffset = 14 * Game.SCALE;

    private float jumpSpeed = -3.25f * Game.SCALE;
    private float fallSpeedAfterCollision = 0.5f * Game.SCALE;

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

    private int powerBarWidth = (int) (104 * Game.SCALE);
    private int powerBarHeight = (int) (2 * Game.SCALE);
    private int powerBarXStart = (int) (44 * Game.SCALE);
    private int powerBarYStart = (int) (34 * Game.SCALE);
    private int powerWidth = powerBarWidth;
    private int powerMaxValue = 200;
    private int powerValue = powerMaxValue;


    private int healthWidth = healthBarWidth;

    //AttackBox

    private int flipX = 0;
    private int flipW = 1;

    private boolean attackChecked;
    private Playing playing;

    private int tileY = 0;

    private boolean powerAttackActive;
    private int powerAttackTick;
    private int powerGrowSpeed = 15;
    private int powerGrowTick;


    



    public Player(float x, float y, int width, int height, LevelManager levelManager, Playing playing) {
        super(x, y,width,height);
        this.playing = playing;
        this.levelManager = levelManager;
        this.state = IDLE;
        this.maxHealth = 100;
        this.currentHealth = maxHealth;
        this.walkSpeed = 1.0f* Game.SCALE;
        loadAnimations();
        initHitbox( (int)21 , (int)28 );
        initAttackBox();
    }

    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (27 * Game.SCALE), (int) (40 * Game.SCALE));
    }

    public void setSpawn(Point spawn) {
        this.x = spawn.x;
        this.y = spawn.y;
        hitbox.x = x;
        hitbox.y = y;
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
        updatePowerBar();
        if (currentHealth <= 0) {
            if (state != DEAD) {
                state = DEAD;
                aniTick = 0;
                aniIndex = 0;
                playing.setPlayerDying(true);
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
            } else if (aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
                playing.setGameOver(true);
                playing.getGame().getAudioPlayer().stopSong();
                playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);
            } else
                updateAnimationTick();

            return;
        }


        updateAttackBox();
        

        updatePos();
        if (moving) {
            checkPotionTouched();

            tileY = (int) (hitbox.y / Game.TILES_SIZE);
            if (powerAttackActive) {
                powerAttackTick++;
                if (powerAttackTick >= 35) {
                    powerAttackTick = 0;
                    powerAttackActive = false;
                }
            }
        }
        if (attacking || powerAttackActive)
            checkAttack();
        updateAnimationTick();
        setAnimation();


    }


    private void checkPotionTouched() {
        playing.checkPotionTouched(hitbox);
    }

    private void updatePowerBar() {
        powerWidth = (int) ((powerValue / (float) powerMaxValue) * powerBarWidth);

        powerGrowTick++;
        if (powerGrowTick >= powerGrowSpeed) {
            powerGrowTick = 0;
            changePower(1);
        }
    }

    private void changePower(int value) {
        powerValue += value;
        if (powerValue >= powerMaxValue)
            powerValue = powerMaxValue;
        else if (powerValue <= 0)
            powerValue = 0;
    }

    private void checkObjectTouched() {
        playing.checkObjectTouched(hitbox);
    }

    private void checkAttack() {
        if (attackChecked || aniIndex != 1)
            return;

        attackChecked = true;

        if (powerAttackActive)
            attackChecked = false;

        playing.checkEnemyHit(attackBox);
        playing.checkObjectHit(attackBox);
        playing.getGame().getAudioPlayer().playAttackSound();

    }

    private void updateAttackBox() {
        if (right || (powerAttackActive && flipW == 1))
            attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 10);
        else if (left || (powerAttackActive && flipW == -1))
            attackBox.x = hitbox.x - hitbox.width - (int) (Game.SCALE * 18);

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
        g.drawImage(animations[state][aniIndex],
                (int) (hitbox.x - xDrawOffset)-xLvlOffset+(flipX),
                (int) (hitbox.y - yDrawOffset),
                width*flipW,
                height,
                null);


        drawUI(g);
    }



    private void drawUI(Graphics g) {
        //background
        g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
        //health bar
        g.setColor(Color.red);
        g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
        //healthBarXStart :  the offset from image to the actual bar

        //power bar
        g.setColor(Color.yellow);
        g.fillRect(powerBarXStart + statusBarX, powerBarYStart + statusBarY, powerWidth, powerBarHeight);

    }

    private void updatePos() {
        moving = false;

        if (jump) jump();

        if (!inAir)
            if (!powerAttackActive)
                if ((!left && !right) || (right && left))
                    return;

        float xSpeed = 0;

        if (left ){
            xSpeed-= walkSpeed;
            flipX = width;
            flipW = -1;
            xDrawOffset=40* Game.SCALE;
        }
        else if (right) {
            xSpeed += walkSpeed;
            flipX = 0;
            flipW =1;
            xDrawOffset=23* Game.SCALE;
        }

        if (powerAttackActive) {
            if (!left && !right) {
                if (flipW == -1)
                    xSpeed = -walkSpeed;
                else
                    xSpeed = walkSpeed;
            }

            xSpeed *= 3;
        }

        if(!inAir){
            if (!IsEntityOnFloor(hitbox, lvlData)){
                inAir = true;
            }
        }


        if (inAir && !powerAttackActive){

            if (CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
                hitbox.y += airSpeed;
                airSpeed += GRAVITY;
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
            playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
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
        int startAni = state;

        if (moving)
            state = RUN;
        else
            state = IDLE;

        if (inAir)
            state = JUMP;

        if (powerAttackActive) {
            state = ATTACK;
            aniIndex = 1;
            aniTick = 0;
            return;
        }

        if (attacking) {
            state = ATTACK;
            if (startAni != ATTACK) {
                aniIndex = 0;
                aniTick = 0;
                return;
            }
        }
        if (startAni != state)
            resetAniTick();
    }

    private void resetAniTick() {
        aniTick=0;
        aniIndex=0;
    }

    private void updateAnimationTick() {
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick=0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(state)){
                attacking = false;
                attackChecked = false;
                aniIndex=0;
            }
        }
    }



    public boolean isRight() {
        return right;
    }

    public boolean isLeft() {
        return left;
    }



    public void setRight(boolean right) {
        this.right = right;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }


    public void resetDirBooleans() {
        left = false;
        right = false;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public void resetAll() {
        resetDirBooleans();
        inAir = false;
        attacking = false;
        moving = false;
        airSpeed = 0f;
        state = IDLE;
        currentHealth = maxHealth;

        hitbox.x = x;
        hitbox.y = y;

        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
    }

    public int getTileY() {
        return tileY;
    }

    public void powerAttack() {
        if (powerAttackActive)
            return;
        if (powerValue >= 60) {
            powerAttackActive = true;
            changePower(-60);
        }

    }
}
