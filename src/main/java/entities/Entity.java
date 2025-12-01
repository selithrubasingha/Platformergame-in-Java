package entities;

import main.Game;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Entity {

    protected float x,y;
    protected int width, height;
    protected Rectangle2D.Float hitbox;
    protected int  aniTick , aniIndex ;
    protected int state;
    protected float airSpeed;
    protected boolean inAir = false;
    protected int maxHealth;
    protected int currentHealth;
    protected Rectangle2D.Float attackBox;
    protected float walkSpeed;


    public Entity(float x, float y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

    }


    protected  void drawHitbox(Graphics g, int xlvlOffset) {
        // For debugging the hitbox

        g.setColor(Color.PINK);
        g.drawRect((int) hitbox.x - xlvlOffset, (int) hitbox.y, (int) hitbox.width, (int) hitbox.height);

    }
// New, cleaner drawHitbox method

    protected void initHitbox( float width, float height) {
        hitbox = new Rectangle2D.Float(x, y, (int) (width * Game.SCALE), (int) (height * Game.SCALE));
    }

    public Rectangle2D.Float getHitbox() {
        return hitbox;
    }

    public int getState() {
        return state;
    }

    protected void drawAttackBox(Graphics g, int xLvlOffset) {
        g.setColor(Color.red);
        g.drawRect((int) (attackBox.x - xLvlOffset), (int) attackBox.y, (int) attackBox.width, (int) attackBox.height);
    }


    public int getAniIndex() {
        return aniIndex;
    }





}
