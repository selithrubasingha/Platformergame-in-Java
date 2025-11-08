package levels;

import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;

import static main.Game.*;

public class LevelManager {

    private Game game ;
    private BufferedImage levelSprites;
    private int[][] levelData;


    private final String COLLISION_LAYER_NAME = "Collisions";

    public LevelManager(Game game){
        this.game = game;
        levelSprites = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);

        levelData = LoadSave.GetLevelDataFromTMX(
                LoadSave.TMX_LEVEL,
                COLLISION_LAYER_NAME,
                TILES_IN_WIDTH,
                TILES_IN_HEIGHT
        );
    }

    public void draw(Graphics g){
        g.drawImage(levelSprites,0,0,    (int) (levelSprites.getWidth() * SCALE),
                (int) (levelSprites.getHeight() * SCALE),null);
    }

    public void update(){

    }

    public int solid(int tileX, int tileY) {
        if (tileX < 0 || tileY < 0 || tileY >= TILES_IN_HEIGHT|| tileX >= TILES_IN_WIDTH) {
            return 1; // Return '1' (Solid) if the entity is attempting to move outside the map
        }
        // Return the value: 1 for Wall/Solid, 0 for Floor/Passable
        return levelData[tileY][tileX];
    }
}
