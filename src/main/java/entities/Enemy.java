package entities;

import main.Game;

import java.awt.geom.Rectangle2D;

import static utilz.Constants.ANI_SPEED;
import static utilz.Constants.Directions.*;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.GRAVITY;
import static utilz.HelpMethods.*;
import static utilz.HelpMethods.IsSightClear;

public abstract class Enemy extends Entity {
    protected int  enemyState = IDLE, enemyType;
    protected boolean firstUpdate = true;

    protected int walkDir = LEFT;
    protected int tileY;
    protected float attackDistance = (float)(Game.TILES_SIZE * 0.5);

    protected boolean active = true;
    protected boolean attackChecked ;



    public Enemy(float x, float y, int width, int height, int enemyType) {
        super(x, y, width, height);
        this.enemyType = enemyType;
        this.walkSpeed= 0.35f * Game.SCALE;
        initHitbox( width, height);
        maxHealth = GetMaxHealth(enemyType);
        currentHealth = maxHealth;


    }


    protected void firstUpdateCheck(int[][] lvlData) {
        //the first animation logic is made into a new method ! for convenience
        if (!IsEntityOnFloor(hitbox, lvlData))
            inAir = true;
        firstUpdate = false;
    }

    protected void updateInAir(int[][] lvlData) {
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

    protected void move(int[][] lvlData) {
        float xSpeed = 0;
        //the move logic in the previous episode
        if (walkDir == LEFT)
            xSpeed = -walkSpeed;
        else
            xSpeed = walkSpeed;

        if (CanMoveHere(hitbox.x + xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData))
            if (IsFloor(hitbox, xSpeed, lvlData)) {
                hitbox.x += xSpeed;
                return;
            }
        //if cannot move to the tile next to bro ... just CHANGE direction bro !
        changeWalkDir();
    }

    protected void turnTowardsPlayer(Player player) {
        // aha ! turn towards the player .. just some simple logic comparing the x values
        if (player.hitbox.x > hitbox.x)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }

    protected void newState(int enemyState) {
        //changing the state
        this.enemyState = enemyState;
        //reseting the animation frames so that the new animation won't start halfway
        aniTick = 0;
        aniIndex = 0;
    }

    protected boolean canSeePlayer(int[][] lvlData, Player player) {
        //checks if the player tile y is the same as enemy tile Y
        int playerTileY = (int) (player.getHitbox().y / Game.TILES_SIZE);
        if (playerTileY == tileY)
            //if the plyer is in seeable range
            if (isPlayerInRange(player)) {
                //and if there is no obstacle in
                if (IsSightClear(lvlData, hitbox, player.hitbox, tileY))
                    return true;
            }

        return false;
    }

    protected boolean isPlayerInRange(Player player) {
        //check if the abs value is less than 5
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance * 5;
    }

    protected boolean isPlayerCloseForAttack(Player player) {
        // the same as checkRange
        int absValue = (int) Math.abs(player.hitbox.x - hitbox.x);
        return absValue <= attackDistance;
    }

    protected void checkEnemyHit( Rectangle2D.Float attackBox , Player player) {
        if (attackBox.intersects(player.hitbox))
            player.changeHealth(-GetEnemyDmg(enemyType));
        attackChecked = true;
    }

    protected void updateAnimationTick() {
        aniTick++;
        if (aniTick >= ANI_SPEED) {
            aniTick = 0;
            aniIndex++;
            if (aniIndex >= GetSpriteAmount(enemyType, enemyState)) {
                aniIndex = 0;
                switch (enemyState) {
                    case ATTACK, HIT -> enemyState = IDLE;
                    case DEAD -> active = false;
                }
            }
        }
    }




    protected void changeWalkDir() {
        if (walkDir == LEFT)
            walkDir = RIGHT;
        else
            walkDir = LEFT;
    }


    public int getEnemyState() {
        return enemyState;
    }


    public void hurt(int amount) {
        currentHealth -= amount;

        if (currentHealth <= 0 )
            newState(DEAD);
        else
            newState(HIT);
    }

    public boolean isActive(){
        return active;
    }

    public void resetEnemy() {
        hitbox.x = x;
        hitbox.y = y;
        firstUpdate = true;
        currentHealth = maxHealth;
        newState(IDLE);
        active = true;
        airSpeed = 0;
    }
}
