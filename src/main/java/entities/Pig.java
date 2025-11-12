package entities;

import main.Game;

import static utilz.Constants.Directions.LEFT;
import static utilz.Constants.EnemyConstants.*;
import static utilz.Constants.EnemyConstants.PIG;
import static utilz.HelpMethods.*;

public class Pig extends Enemy {

    public Pig(float x, float y) {
        super(x, y, PIG_WIDTH, PIG_HEIGHT, PIG);
        initHitbox(x,y,(int)(12* Game.SCALE),(int)(13*Game.SCALE));

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
    }

}
