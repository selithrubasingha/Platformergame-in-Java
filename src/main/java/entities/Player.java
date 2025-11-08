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

public class Player extends Entity {

    private BufferedImage img  ;
    private BufferedImage[][] animations;
    private int aniSpeed = 10 , aniTick , aniIndex ;
    private int playerAction = IDLE;
    private boolean moving = false, attacking = false;
    private boolean up, down, left, right;

    private LevelManager levelManager;



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

    public void render(Graphics g){
        g.drawImage(animations[playerAction][aniIndex], (int) (x), (int) (y ), width, height, null);
        drawHitbox(g);
    }

    private void updatePos() {
        moving = false;
        float nextX = x;
        float nextY = y;

        if (left && !right) {
            nextX -= 2;
            moving = true;
        } else if (right && !left) {
            nextX += 2;
            moving = true;
        }
        if (up && !down) {
            nextY -= 2;
            moving = true;
        } else if (down && !up) {
            nextY += 2;
            moving = true;
        }
        if (canMoveHere(nextX, y, width, height)) {
            x = nextX;
        }

        // 2. Check Y-axis movement (using the potentially updated X)
        if (canMoveHere(x, nextY, width, height)) {
            y = nextY;
        }
    }

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

    // In package entities; Player

    // Checks if the proposed bounding box area is clear of solid tiles
    private boolean canMoveHere(float x, float y, float width, float height) {

        // Convert the player's four corners from pixel coordinates to tile coordinates (row/col)

        // Top-Left corner's tile coordinates
        int tileX1 = (int) (x / TILES_SIZE);
        int tileY1 = (int) (y / TILES_SIZE);

        // Top-Right corner's tile coordinates
        int tileX2 = (int) ((x + width) / TILES_SIZE);
        int tileY2 = (int) (y / TILES_SIZE);

        // Bottom-Left corner's tile coordinates
        int tileX3 = (int) (x / TILES_SIZE);
        // Use (y + height - 1) to ensure the check is inside the player's final bounding box pixel
        int tileY3 = (int) ((y + height - 1) / TILES_SIZE);

        // Bottom-Right corner's tile coordinates
        int tileX4 = (int) ((x + width) / TILES_SIZE);
        int tileY4 = (int) ((y + height - 1) / TILES_SIZE);

        // Check all four corners against the level data
        if (levelManager.solid(tileX1, tileY1) > 0 ||
                levelManager.solid(tileX2, tileY2) > 0 ||
                levelManager.solid(tileX3, tileY3) > 0 ||
                levelManager.solid(tileX4, tileY4) > 0) {

            return false; // Collision detected (one of the corners is on a solid tile)
        }
        return true; // Safe to move
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
