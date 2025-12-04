package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utilz.LoadSave;
import static utilz.Constants.UI.VolumeButtons.*;

//hehe extends the paused button guess the developer was lazy
public class VolumeButton extends PauseButton {

    private BufferedImage[] imgs;
    private BufferedImage slider;
    private int index = 0;
    private boolean mouseOver, mousePressed;
    private int buttonX, minX, maxX;
    private float floatValue = 0f;

    //this constructor arguments are the actual slider bounds ... not the volume bounds
    public VolumeButton(int x, int y, int width, int height) {
        //when the pause button first starts we need to put the button of the slider in the MIIDLE !!
        super(x + width / 2, y, VOLUME_WIDTH, height);
        //AHHH the bounds is hald the width to teh left and to the right
        bounds.x -= VOLUME_WIDTH / 2;

        buttonX = x + width / 2;
        //this reassigns the width and x to the slider !!!  the super is for the actual volume button
        //but this part is for the slider component
        //we're gonna draw these two later in draw method
        this.x = x;
        this.width = width;
        // this is for the sliding limits
        minX = x + VOLUME_WIDTH / 2;
        maxX = x + width - VOLUME_WIDTH / 2;
        loadImgs();
    }

    private void loadImgs() {
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.VOLUME_BUTTONS);
        imgs = new BufferedImage[3];
        for (int i = 0; i < imgs.length; i++)
            imgs[i] = temp.getSubimage(i * VOLUME_DEFAULT_WIDTH, 0, VOLUME_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);

        slider = temp.getSubimage(3 * VOLUME_DEFAULT_WIDTH, 0, SLIDER_DEFAULT_WIDTH, VOLUME_DEFAULT_HEIGHT);

    }

    public void update() {
        index = 0;
        if (mouseOver)
            index = 1;
        if (mousePressed)
            index = 2;

    }

    public void draw(Graphics g) {

        g.drawImage(slider, x, y, width, height, null);
        // buttonX - VOLUME_WIDTH / 2 is so that the button is at the middle of the slider when it first starts
        g.drawImage(imgs[index], buttonX - VOLUME_WIDTH / 2, y, VOLUME_WIDTH, height, null);

    }

    public void changeX(int x) {
        // updating the buttonX , it's position
        if (x < minX)
            buttonX = minX;
        else if (x > maxX)
            buttonX = maxX;
        else
            //boom ! giving buttonX is set to current X
            buttonX = x;

        updateFloatValue();

        //you may be thinking " why the - VOLUME_WIDTH / 2 ??"
        // it's because we can't just update the drawing of the button , we need to update the
        //hitbox of the button as well
        bounds.x = buttonX - VOLUME_WIDTH / 2;

    }

    private void updateFloatValue() {
        float range = maxX - minX;
        float value = buttonX - minX;
        floatValue = value / range;
    }

    public void resetBools() {
        mouseOver = false;
        mousePressed = false;
    }

    public boolean isMouseOver() {
        return mouseOver;
    }

    public void setMouseOver(boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public void setMousePressed(boolean mousePressed) {
        this.mousePressed = mousePressed;
    }

    public float getFloatValue() {
        return floatValue;
    }
}