package gamestates;

import entities.Player;
import levels.LevelManager;
import main.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static main.Game.*;

public class Playing extends State implements Statemethods{
    private Player player;
    private LevelManager levelManager;

    public Playing(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        this.player = new Player(200,200, (int) (78 * SCALE), (int) (58 * SCALE),levelManager);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
    }

    public Player getPlayer() {
        return player;
    }


    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    @Override
    public void update() {
        player.update();

    }

    @Override
    public void draw(Graphics g) {
        levelManager.draw(g);
        player.render(g);

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1)
            player.setAttacking(true);
    }



    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()){

            case KeyEvent.VK_A:
                player.setLeft(true);

                break;

            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(true);

        }
    }



    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()){
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(false);

    }
}}
