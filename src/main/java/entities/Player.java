package entities;

import levels.LevelManager;
import main.Game;
import utilz.LoadSave;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static main.Game.TILES_DEFAULT_SIZE;
import static main.Game.TILES_SIZE;
import static utilz.Constants.Directions.*;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.PlayerConstants.*;
import static utilz.HelpMethods.CanMoveHere;

public class Player extends Entity {

    private BufferedImage img  ;
    private BufferedImage[][] animations;
    private int aniSpeed = 10 , aniTick , aniIndex ;
    private int playerAction = IDLE;
    private boolean moving = false, attacking = false;
    private boolean up, down, left, right;
    private float playerSpeed = 2.0f;

    private LevelManager levelManager;
    private int[][] lvlData;



    public Player(float x, float y, int width, int height, LevelManager levelManager) {
        super(x, y,width,height);
        this.levelManager = levelManager;

        loadAnimations();
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
    }

    public void render(Graphics g){
        g.drawImage(animations[playerAction][aniIndex], (int) (x), (int) (y ), width, height, null);
        drawHitbox(g);
    }

    private void updatePos() {
        moving = false;
        if (!left && !right && !up && !down)
            return;

        float xSpeed = 0, ySpeed = 0;

        if (left && !right)
            xSpeed = -playerSpeed;
        else if (right && !left)
            xSpeed = playerSpeed;

        if (up && !down)
            ySpeed = -playerSpeed;
        else if (down && !up)
            ySpeed = playerSpeed;

		if (CanMoveHere(x + xSpeed, y + ySpeed, width, height, lvlData)) {
			this.x += xSpeed;
			this.y += ySpeed;
			moving = true;
//		}
    }}

    private void setAnimation() {
        int startAni = playerAction;

        if (moving)
            playerAction = RUN;
        else
            playerAction = IDLE;

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
