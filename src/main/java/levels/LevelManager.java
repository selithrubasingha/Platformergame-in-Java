package levels;

import gamestates.GameState;
import main.Game;
import utilz.LoadSave;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static main.Game.*;

public class LevelManager {

    private Game game ;
    private BufferedImage levelSprites;
    private Level levelOne;

    private ArrayList<Level> levels;
    public int lvlIndex = 0;



    private final String COLLISION_LAYER_NAME = "Collisions";

    public LevelManager(Game game){
        this.game = game;
        importOutsideSprites();
        levels = new ArrayList<>();
        buildAllLevels();



        ;

    }

    private void buildAllLevels() {
        BufferedImage[] allLevels = LoadSave.GetAllLevels();
//        for (BufferedImage img : allLevels)
//            levels.add(new Level(LoadSave.GetLevelDataFromTMX(
//                    LoadSave.TMX_LEVEL,
//                    COLLISION_LAYER_NAME,
//                    TILES_IN_WIDTH+20,
//                    TILES_IN_HEIGHT
//            )));

        for (int i = 0; i < allLevels.length; i++) {
            // Construct the path for the TMX file.
            // Use (i + 1) because the file names start at "1.tmx" while the index starts at 0.
            String tmxFilePath = "TMXlvls/" + (i + 1) + ".tmx";

            // The Level constructor uses the BufferedImage from the array and the TMX path string
            levels.add(new Level(
                    LoadSave.GetLevelDataFromTMX(
                            tmxFilePath, // Replaces LoadSave.TMX_LEVEL
                            COLLISION_LAYER_NAME,
                            TILES_IN_WIDTH + 20,
                            TILES_IN_HEIGHT
                    ),lvlIndex,allLevels[i]
            ));
        }

        }


    private void importOutsideSprites() {
        levelSprites = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
    }

    public void draw(Graphics g, int xLvlOffset){
        g.drawImage(getCurrentLevel().getImg(), 0-xLvlOffset,0,    (int) (levelSprites.getWidth() * SCALE),
                (int) (getCurrentLevel().getImg().getHeight() * SCALE),null);
    }

    public void update(){

    }

    public Level getCurrentLevel() {
        return levels.get(lvlIndex);
    }

    public int getAmountOfLevels() {
        return levels.size();
    }

    public void loadNextLevel() {
        lvlIndex++;
        if (lvlIndex >= levels.size()) {
            lvlIndex = 0;
            System.out.println("No more levels! Game Completed!");
            GameState.state = GameState.MENU;
        }

        Level newLevel = levels.get(lvlIndex);
        game.getPlaying().getEnemyManager().loadEnemies(newLevel,lvlIndex);
        game.getPlaying().getPlayer().loadLvlData(newLevel.getLevelData());
        game.getPlaying().setMaxLvlOffset(newLevel.getLvlOffset());
        game.getPlaying().getObjectManager().loadObjects(newLevel);
    }

}

