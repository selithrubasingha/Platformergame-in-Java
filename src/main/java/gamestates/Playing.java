package gamestates;

import entities.Player;
import levels.LevelManager;
import main.Game;
import ui.PausedOverlay;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static main.Game.*;

public class Playing extends State implements Statemethods {
    private Player player;
    private LevelManager levelManager;
    private boolean paused = false;
    private PausedOverlay pausedOverlay;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * GAME_WIDTH);
    private int rightBorder = (int) (0.8 * GAME_WIDTH);
    private int lvlTilesWide = ACTUAL_GAME_WIDTH / TILES_SIZE;
    private int maxTilesOffset = lvlTilesWide - TILES_IN_WIDTH;
    private int maxLvlOffsetX = maxTilesOffset * TILES_SIZE;

    public Playing(Game game) {
        super(game);
        initClasses();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        this.player = new Player(200, 200, (int) (78 * SCALE), (int) (58 * SCALE), levelManager);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        pausedOverlay = new PausedOverlay(this);
    }

    public Player getPlayer() {
        return player;
    }


    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    @Override
    public void update() {
        if (!paused){
        player.update();
        levelManager.update();
        checkCloseToBorder();
        }
        if (paused)
            pausedOverlay.update();

    }

    private void checkCloseToBorder() {
        int playerX = (int) player.getHitbox().x;
        int diff = playerX - xLvlOffset;

        if (diff > rightBorder)
            xLvlOffset += diff - rightBorder;
        else if (diff < leftBorder)
            xLvlOffset += diff - leftBorder;

        if (xLvlOffset > maxLvlOffsetX)
            xLvlOffset = maxLvlOffsetX;
        else if (xLvlOffset < 0)
            xLvlOffset = 0;
    }

    @Override
    public void draw(Graphics g) {

        levelManager.draw(g,xLvlOffset);
        player.render(g,xLvlOffset);

        if (paused){
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pausedOverlay.draw(g);}

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1)
            player.setAttacking(true);
    }


    @Override
    public void mousePressed(MouseEvent e) {
        if (paused)
            pausedOverlay.mousePressed(e);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (paused)
            pausedOverlay.mouseReleased(e);

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (paused)
            pausedOverlay.mouseMoved(e);

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {

            case KeyEvent.VK_A:
                player.setLeft(true);

                break;

            case KeyEvent.VK_D:
                player.setRight(true);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(true);
                break;
            case KeyEvent.VK_ESCAPE:
                paused = !paused;
                break;

        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A:
                player.setLeft(false);
                break;
            case KeyEvent.VK_D:
                player.setRight(false);
                break;
            case KeyEvent.VK_SPACE:
                player.setJump(false);

        }
    }

    public void pauseGame() {
        paused =true;
    }



    public void unpauseGame() {
        paused = false;
    }


    public void mouseDragged(MouseEvent e) {
        if (paused)
            pausedOverlay.mouseDragged(e);
    }
}
