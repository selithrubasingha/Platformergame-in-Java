package ui;

import gamestates.GameState;
import gamestates.Playing;
import gamestates.Statemethods;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static utilz.Constants.UI.PauseButtons.SOUND_SIZE;
import static utilz.Constants.UI.URMButtons.URM_SIZE;
import static utilz.Constants.UI.VolumeButtons.SLIDER_WIDTH;
import static utilz.Constants.UI.VolumeButtons.VOLUME_HEIGHT;

public class PausedOverlay  {

    //We need the playing state
    // and also we need to initialize all the buttons !
    private Playing playing;
    private AudioOptions audioOptions;
    private BufferedImage backgroundImg;
    private int bgX, bgY, bgW, bgH;
    private UrmButton menuB, replayB, unpauseB;


    public PausedOverlay(Playing playing) {
        //assigning the playing state object
        this.playing = playing;
        //loading background and button creation
        loadBackground();
        audioOptions = playing.getGame().getAudioOptions();

        createUrmButtons();



    }




    private void createUrmButtons() {
        //the 3 buttons are in a row , so the y cooedinate is equal while
        // the x coordinates are different
        int menuX = (int) (313 * Game.SCALE);
        int replayX = (int) (387 * Game.SCALE);
        int unpauseX = (int) (462 * Game.SCALE);
        int bY = (int) (325 * Game.SCALE);

        //BOOM creating the  buttons ... the row index is given according to the
        menuB = new UrmButton(menuX, bY, URM_SIZE, URM_SIZE, 2);
        replayB = new UrmButton(replayX, bY, URM_SIZE, URM_SIZE, 1);
        unpauseB = new UrmButton(unpauseX, bY, URM_SIZE, URM_SIZE, 0);

    }


    private void loadBackground() {
        //the backgroung for the pause menu
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PAUSE_BACKGROUND);
        //the size baby !
        bgW = (int) (backgroundImg.getWidth() * Game.SCALE);
        bgH = (int) (backgroundImg.getHeight() * Game.SCALE);
        bgX = Game.GAME_WIDTH / 2 - bgW / 2;
        bgY = (int) (25 * Game.SCALE);
    }

    public void update() {
        //Not like plain html .. we need to update all the buttons at 120 fps


        menuB.update();
        replayB.update();
        unpauseB.update();
        audioOptions.update();

    }


    public void draw(Graphics g) {
        //you need to draw too bruh .. but the draw and update logic is in the button classes
        //background
        g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);


        menuB.draw(g);
        replayB.draw(g);
        unpauseB.draw(g);

        audioOptions.draw(g);


    }


    public void mouseClicked(MouseEvent e) {

    }


    public void mousePressed(MouseEvent e) {
        //linking the logic to mousepressed ... --> go to button class

        if (isIn(e, menuB))
            menuB.setMousePressed(true);
        else if (isIn(e, replayB))
            replayB.setMousePressed(true);
        else if (isIn(e, unpauseB))
            unpauseB.setMousePressed(true);
        else
            audioOptions.mousePressed(e);


    }


    public void mouseReleased(MouseEvent e) {
        if (isIn(e, menuB)) {
            if (menuB.isMousePressed()) {
                //go to menu !
                playing.setGamestate(GameState.MENU);
                playing.resetAll();
            }
        } else if (isIn(e, replayB)) {
            if (replayB.isMousePressed()) {
                //he he replay is not yet implemented
                playing.resetAll();
                playing.unpauseGame();
            }
        } else if (isIn(e, unpauseB)) {
            if (unpauseB.isMousePressed()) {
                //unpause!
                playing.unpauseGame();
            }
        }else
            audioOptions.mouseReleased(e);

        //the resetting is done here...we need to reset everything
        //consistently to avoid wierd UI glitches
        menuB.resetBools();
        replayB.resetBools();
        unpauseB.resetBools();

    }


    public void mouseMoved(MouseEvent e) {


        menuB.setMouseOver(false);
        replayB.setMouseOver(false);
        unpauseB.setMouseOver(false);

        if (isIn(e, menuB))
            menuB.setMouseOver(true);
        else if (isIn(e, replayB))
            replayB.setMouseOver(true);
        else if (isIn(e, unpauseB))
            unpauseB.setMouseOver(true);
        else
            audioOptions.mouseMoved(e);

    }

    public void mouseDragged(MouseEvent e) {
        audioOptions.mouseDragged(e);

    }

    private boolean isIn(MouseEvent e, PauseButton b) {
        return b.getBounds().contains(e.getX(), e.getY());
    }



}
