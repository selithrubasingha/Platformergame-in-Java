package gamestates;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PausedOverlay;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

import static main.Game.*;

public class Playing extends State implements Statemethods {
    private Player player;
    private LevelManager levelManager;
    private EnemyManager enemyManager;
    private ObjectManager objectManager;
    private boolean paused = false;
    private PausedOverlay pausedOverlay;
    private GameOverOverlay gameOverOverlay;
    private LevelCompletedOverlay levelCompletedOverlay;

    private int xLvlOffset;
    private int leftBorder = (int) (0.2 * GAME_WIDTH);
    private int rightBorder = (int) (0.8 * GAME_WIDTH);
    private int maxLvlOffsetX;

    private boolean gameOver ;
    private boolean lvlCompleted;
    private boolean playerDying = false ;

    public Playing(Game game) {
        super(game);
        initClasses();

        loadStartLevel();
        calcLvlOffset();
    }

    private void calcLvlOffset() {
        maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
    }

    private void initClasses() {
        levelManager = new LevelManager(game);
        enemyManager = new EnemyManager(this);
        objectManager = new ObjectManager(this);
        this.player = new Player(200, 200, (int) (78 * SCALE), (int) (58 * SCALE), levelManager,this);
        player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
        pausedOverlay = new PausedOverlay(this);
        gameOverOverlay = new GameOverOverlay(this);
        levelCompletedOverlay = new LevelCompletedOverlay(this);
    }

    public void loadNextLevel() {
        resetAll();
        levelManager.loadNextLevel();
        player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
    }

    private void loadStartLevel() {
        enemyManager.loadEnemies(levelManager.getCurrentLevel(),levelManager.getCurrentLevel().getLvlIndex());
        objectManager.loadObjects(levelManager.getCurrentLevel());


    }

    public Player getPlayer() {
        return player;
    }


    public void windowFocusLost() {
        player.resetDirBooleans();
    }

    @Override
    public void update() {
        if (paused) {
            pausedOverlay.update();
        } else if (lvlCompleted) {
            levelCompletedOverlay.update();
        }else if (gameOver){
            gameOverOverlay.update();
        }else if(playerDying){
            player.update();
        } else{
            levelManager.update();
            player.update();
            objectManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
            checkCloseToBorder();
        }
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
        enemyManager.draw(g,xLvlOffset);
        objectManager.draw(g,xLvlOffset);

        if (paused){
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
            pausedOverlay.draw(g);
        }
        else if (gameOver)
            gameOverOverlay.draw(g);
        else if (lvlCompleted)
            levelCompletedOverlay.draw(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!gameOver)
            if (e.getButton() == MouseEvent.BUTTON1)
                player.setAttacking(true);
    }


    @Override
    public void mousePressed(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pausedOverlay.mousePressed(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mousePressed(e);
        }else {
            gameOverOverlay.mousePressed(e);
        }
    }




    @Override
    public void mouseReleased(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pausedOverlay.mouseReleased(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mouseReleased(e);
        }else {
            gameOverOverlay.mouseReleased(e);
        }

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (!gameOver) {
            if (paused)
                pausedOverlay.mouseMoved(e);
            else if (lvlCompleted)
                levelCompletedOverlay.mouseMoved(e);
        }else {
            gameOverOverlay.mouseMoved(e);
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver)
            gameOverOverlay.keyPressed(e);
        else{
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

    }


    @Override
    public void keyReleased(KeyEvent e) {
        if (!gameOver)
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

    public void resetAll() {

        //to do reset player , enemy lvl etc.
        gameOver = false;
        paused = false;
        lvlCompleted = false;
        playerDying = false ;
        player.resetAll();
        enemyManager.resetAllEnemies();
    }

    public void checkEnemyHit(Rectangle2D.Float attackBox) {
        enemyManager.checkEnemyHit(attackBox);
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public void setMaxLvlOffset(int lvlOffset) {
        this.maxLvlOffsetX = lvlOffset;
    }

    public void setLevelCompleted(boolean levelCompleted) {
        this.lvlCompleted = levelCompleted;
    }

    public ObjectManager getObjectManager() {
        return objectManager;
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void checkObjectTouched(Rectangle2D.Float hitbox) {
        objectManager.checkObjectTouched(hitbox);
    }

    public void checkObjectHit(Rectangle2D.Float attackBox) {
        objectManager.checkObjectHit(attackBox);
    }

    public void setPlayerDying(boolean playerDying) {
        this.playerDying = playerDying;
    }
}
