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

        g.setColor(Color.RED);
        g.drawRect((int) attackBox.x - xLvlOffset, (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);

    }


    private void initAttackBox() {
        attackBox = new Rectangle2D.Float(x, y, (int) (22 * Game.SCALE), (int) (19 * Game.SCALE));
//        attackBoxOffsetX = (int) (Game.SCALE * 30);
    }


    private void updateMove(int[][] lvlData,Player player){
        if (firstUpdate) {
            firstUpdateCheck(lvlData);
        }

        if (inAir){
            updateInAir(lvlData);
        } else {
            switch (enemyState) {
                case IDLE:
                    enemyState = RUN;
                    break;
                case RUN:
                    if (canSeePlayer(lvlData,player))
                        turnTowardsPlayer(player);
                    if (isPlayerCloseForAttack(player))
                        newState(ATTACK);

                    move(lvlData);
                    break;
            }


        }}
    public void update(int[][] lvlData,Player player) {
        updateMove(lvlData,player);
        updateAnimationTick();
        updateAttackBox();
    }

    private void updateAttackBox() {

        attackBox.x = hitbox.x;
        attackBox.y = hitbox.y ;
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
