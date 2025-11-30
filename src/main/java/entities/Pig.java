package entities;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.Directions.RIGHT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.EnemyConstants.PIG;
import static utilz.HelpMethods.*;

public class Pig extends Enemy {

    //attackBox
    private Rectangle2D.Float attackBox;
    private int attackBoxOffsetX;

    public Pig(float x, float y) {
        super(x, y, PIG_WIDTH, PIG_HEIGHT, PIG);
        initHitbox(x,y,(int)(12* Game.SCALE),(int)(13*Game.SCALE));
        initAttackBox();
    }

    public void drawAttackBox(Graphics g, int xLvlOffset) {
        // For debugging the attack box

        g.setColor(Color.green);
        g.drawRect((int) attackBox.x - xLvlOffset, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);

    }


    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (15 * Game.SCALE), (int) (19 * Game.SCALE));
//        attackBoxOffsetX = (int) (Game.SCALE * 30);
    }


    private void updateBehavior(int[][] lvlData,Player player){
        //first animation
        if (firstUpdate) {
            firstUpdateCheck(lvlData);
        }
        //"ahasenan ikmanta watiyan" logic
        if (inAir){
            updateInAir(lvlData);
            //watilanan enemy state check
        } else {
            switch (enemyState) {
                //if IDLE --> run
                case IDLE:
                    enemyState = RUN;
                    break;
                // if bro sees the player , run towards... if bro is close ...ATTACK
                case RUN:
                    if (canSeePlayer(lvlData,player)) {
                        turnTowardsPlayer(player);
                        if (isPlayerCloseForAttack(player))
                            newState(ATTACK);
                    }

                    //THE ACTUAL MOVE LOGIC HERE
                    move(lvlData);
                    break;
                //attack logic ...
                case ATTACK:
                    if (aniIndex == 0)
                        attackChecked = false;

                    if (aniIndex == 2 && !attackChecked)
                        checkEnemyHit(attackBox,player);
                    break;
                case HIT:
                    break;
            }


        }}



    public void update(int[][] lvlData,Player player) {
        updateBehavior(lvlData,player);
        updateAnimationTick();
        updateAttackBox();
    }

    private void updateAttackBox() {

        if (walkDir == RIGHT)
            attackBox.x = hitbox.x + hitbox.width + (int) (Game.SCALE * 1);
        else if (walkDir == LEFT)
            attackBox.x = hitbox.x - hitbox.width + (int) (Game.SCALE * 5);

        attackBox.y = hitbox.y + (Game.SCALE * 10)-20;

    }

    public int flipX() {
        if (walkDir == RIGHT) {
            PIG_DRAWOFFSET_X=8;
            return width;
        }
        else {
            PIG_DRAWOFFSET_X=15;
            return 0;
        }
    }

    public int flipW() {
        if (walkDir == RIGHT)
            return -1;
        else
            return 1;

    }


}
