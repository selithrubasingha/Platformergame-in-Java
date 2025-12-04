package ui;

import gamestates.GameState;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static utilz.Constants.UI.*;
import static utilz.Constants.UI.Buttons.*;

public class MenuButton {

    private int xPos ,yPos , rowIndex, index;
    private int xOffsetCenter = B_WIDTH / 2;
    private GameState state;
    private BufferedImage[] imgs;
    private boolean mouseOver, mousePressed;
    private Rectangle bounds;

    //constructing with the positions !
    public MenuButton(int xPos , int yPos, int rowIndex, GameState state) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.rowIndex = rowIndex;
        this.state = state;
        //gotta load them images
        loadImgs();
        //is this like the clickable recatangle thing?
        initBounds();

    }

    private void initBounds() {
        bounds = new Rectangle(xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT);

    }

    private void loadImgs() {
        //each button has 3 states bro ...
        imgs = new BufferedImage[3];

        //the button states of the 3 buttons are in the
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.MENU_BUTTONS);
        for (int i = 0; i < imgs.length; i++)
            imgs[i] = temp.getSubimage(i * B_WIDTH_DEFAULT, rowIndex * B_HEIGHT_DEFAULT, B_WIDTH_DEFAULT, B_HEIGHT_DEFAULT);

    }
    public void draw(Graphics g) {
        //the xOffCenter is for conveniently centering the button i think
        g.drawImage(imgs[index], xPos - xOffsetCenter, yPos, B_WIDTH, B_HEIGHT, null);
    }

    public void update() {
        //when we hover ... the button turns kind of white in color
        index = 0;
        if (mouseOver)
            index = 1;
        if (mousePressed)
            index = 2;
    }

    //getters and setters
    public boolean isMouseOver() {
        return mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public void applyGamestate() {
        GameState.state = state;
    }

    //this reset if helps us reset to the normal phase once the mouse stops hovering over a button
    // or when a button is pressed
    public void resetBools() {
        mouseOver = false;
        mousePressed = false;
    }
    //getter for bounds
    public Rectangle getBounds() {
        return bounds;
    }

    public GameState getState() {
        return state;
    }
}
