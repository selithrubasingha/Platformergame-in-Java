package gamestates;

import main.Game;
import ui.MenuButton;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Menu extends State implements Statemethods{

    //think of button as an abstraction from now on.
    //ya''l have 3 button so an array of 3
    private MenuButton[] buttons = new MenuButton[3];
    private BufferedImage backgroundImg;
    //the heck menu width? ahhh understnadable though
    private int menuX, menuY, menuWidth, menuHeight;
    //why green though
    private BufferedImage backGroundImg_Green;

    public Menu(Game game){
        super(game);
        loadButtons();
        loadBackground();
        //oh my god it's the background image
        backGroundImg_Green = utilz.LoadSave.GetSpriteAtlas(utilz.LoadSave.MENU_BACKGROUND_IMG);

    }

    private void loadBackground() {
        //all this for a fucking background ? isn't this too much ?
        backgroundImg = utilz.LoadSave.GetSpriteAtlas(utilz.LoadSave.MENU_BACKGROUND);
        menuWidth = (int) (backgroundImg.getWidth() * Game.SCALE);
        menuHeight = (int) (backgroundImg.getHeight() * Game.SCALE);
        menuX = Game.GAME_WIDTH / 2 - menuWidth / 2;
        menuY = (int) (45 * Game.SCALE);


    }

    private void loadButtons() {
        //for the buttons , play , options and quit
        buttons[0] = new MenuButton(Game.GAME_WIDTH / 2, (int) (150 * Game.SCALE), 0, GameState.PLAYING);
        buttons[1] = new MenuButton(Game.GAME_WIDTH / 2, (int) (220 * Game.SCALE), 1, GameState.OPTIONS);
        buttons[2] = new MenuButton(Game.GAME_WIDTH / 2, (int) (290 * Game.SCALE), 2, GameState.QUIT);
    }

    @Override
    public void update() {
        //updating the buttons... buttons are also being updated jsut like in the game
        for (MenuButton mb : buttons)
            mb.update();

    }

    @Override
    public void draw(Graphics g) {
        //the background the menu box and the buttons
        g.drawImage(backGroundImg_Green,0,0,Game.GAME_WIDTH,Game.GAME_HEIGHT,null);
        g.drawImage(backgroundImg, menuX, menuY, menuWidth, menuHeight, null);

        for (MenuButton mb : buttons)
            mb.draw(g);

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (MenuButton mb : buttons) {
            if (isIn(e, mb)) {
                mb.setMousePressed(true);
                //all the clicky logic is in the input classes! that is the beauty of OOP
            }
        }





    }

    private void resetButtons() {
        //he he a function for a small for loop ??
        for (MenuButton mb : buttons)
            mb.resetBools();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (MenuButton mb : buttons) {
            //if mouse is over the button
            if (isIn(e, mb)) {
                //if mouse is pressed
                if (mb.isMousePressed())
                    //do what the heck the button does
                    mb.applyGamestate();
                break;
            }
        }
        //reset bools ! to avoid the buttons going haywire
        resetButtons();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (MenuButton mb : buttons)
            //after this is done... the rest is up to the input outputs
            mb.setMouseOver(false);

        for (MenuButton mb : buttons)
            if (isIn(e, mb)) {
                mb.setMouseOver(true);
                break;
            }

    }

    //I don't think this method  is nessesary ...
    // when the enter key is presed this aslo sends us to the
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ENTER){
            GameState.state = GameState.PLAYING;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
