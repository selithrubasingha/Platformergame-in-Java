package main;



import audio.AudioPlayer;
import gamestates.GameState;
import gamestates.*;
import gamestates.Menu;
import ui.AudioOptions;


import java.awt.*;

public class Game implements Runnable{

    private GameWindow gameWindow;
    private GamePanel gamePanel;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 170;
    private Playing playing;
    private Menu menu;
    private GameOptions gameOptions;
    private AudioOptions audioOptions ;
    private AudioPlayer audioPlayer;



    public final static int TILES_DEFAULT_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;
    public final static int ACTUAL_GAME_WIDTH = (TILES_IN_WIDTH+20)*TILES_SIZE;

    public Game(){
        initClasses();

        gamePanel = new GamePanel(this);
        gameWindow = new GameWindow(gamePanel);
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
        startGameLoop();

    }

    private void initClasses() {
        audioOptions = new AudioOptions(this);
        audioPlayer = new AudioPlayer();
        menu = new Menu(this);
        playing = new Playing(this);
        gameOptions = new GameOptions(this);

    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {


        switch (GameState.state){
            case PLAYING:
                playing.update();

                break;
            case MENU:
                menu.update();
                break;
            case OPTIONS:
                gameOptions.update();
                break;
            case QUIT:
            default:
                System.exit(0);
                break;

        }
    }

    public void render(Graphics g) {
    switch (GameState.state){
        case PLAYING:
            playing.draw(g);

            break;
        case MENU:
            menu.draw(g);
            break;
        case OPTIONS:
            gameOptions.draw(g);
            break;

    }}

    @Override
    public void run() {

        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {
            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {
                update();
                updates++;
                deltaU--;
            }

            if (deltaF >= 1) {
                gamePanel.repaint();
                frames++;
                deltaF--;
            }

            if (System.currentTimeMillis() - lastCheck >= 1000) {
                lastCheck = System.currentTimeMillis();
                System.out.println("FPS: " + frames + " | UPS: " + updates);
                frames = 0;
                updates = 0;

            }
        }


}

    public void windowFocusLost() {
        if (GameState.state == GameState.PLAYING)  {
            playing.windowFocusLost();
        }
    }
    public Menu getMenu() {
        return menu;
    }

    public Playing getPlaying() {
        return playing;
    }

    public AudioOptions getAudioOptions(){
        return audioOptions;
    }

    public GameOptions getGameOptions() {
        return gameOptions;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }
}


