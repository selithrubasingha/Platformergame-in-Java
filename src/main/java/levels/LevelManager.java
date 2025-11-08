package levels;

import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Game.*;

public class LevelManager {

    private Game game ;
    private BufferedImage levelSprites;
    private Level levelOne;



    private final String COLLISION_LAYER_NAME = "Collisions";

    public LevelManager(Game game){
        this.game = game;
        levelSprites = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);

        levelOne = new Level(LoadSave.GetLevelDataFromTMX(
                LoadSave.TMX_LEVEL,
                COLLISION_LAYER_NAME,
                TILES_IN_WIDTH,
                TILES_IN_HEIGHT
        ));

    }

    public void draw(Graphics g){
        g.drawImage(levelSprites,0,0,    (int) (levelSprites.getWidth() * SCALE),
                (int) (levelSprites.getHeight() * SCALE),null);
    }

    public void update(){

    }


    public Level getCurrentLevel() {
        return levelOne;
    }
}
