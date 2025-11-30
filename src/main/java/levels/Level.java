package levels;

import entities.Pig;
import main.Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static main.Game.*;
import static main.Game.TILES_SIZE;
import static utilz.HelpMethods.GetPlayerSpawn;
import static utilz.LoadSave.GetPigSpawnsFromTMX;
import static utilz.LoadSave.TMX_LEVEL;

public class Level {
    private int[][] lvlData;
    private BufferedImage img;
    private ArrayList<Pig> pigs;
    private int lvlTilesWide;
    private int maxTilesOffset;
    private int maxLvlOffsetX;
    private Point playerSpawn;
    private int lvlIndex;




    public Level(int[][] lvlData, int lvlIndex, BufferedImage img) {
        this.lvlData = lvlData;
        this.lvlIndex = lvlIndex;
        this.img = img;
        createEnemies();
        calcLvlOffsets();
        calcPlayerSpawn();
    }

    private void calcPlayerSpawn() {
        playerSpawn = GetPlayerSpawn(img);
    }

    private void calcLvlOffsets() {
        lvlTilesWide = ACTUAL_GAME_WIDTH / TILES_SIZE;
        maxTilesOffset = lvlTilesWide - TILES_IN_WIDTH;
        maxLvlOffsetX = maxTilesOffset * TILES_SIZE;
    }

    private void createEnemies() {
        pigs = GetPigs(lvlIndex);
    }



    public static ArrayList<Pig> GetPigs(int lvlIndex) {
        // assigning an array list
        ArrayList<Pig> pigs = new ArrayList<>();
        // boom!!  get the coordinates of the pig spawn places
        ArrayList<Float> spawnCoords = GetPigSpawnsFromTMX( "TMXlvls/" + (lvlIndex + 1) + ".tmx", "Spawns");
        // using this for loop we create pigs with the x coordinates provided and boom !
        // create an Pig object array list
        for (int i=0 ; i< spawnCoords.size();i++){
            float x = spawnCoords.get(i++);
            float y = spawnCoords.get(i);
            pigs.add(new Pig(x, y));
        }
        return pigs;
    }

    public int getSpriteIndex(int x, int y) {
        return lvlData[y][x];
    }

    public int[][] getLevelData() {
        return lvlData;
    }

    public BufferedImage getImg() {
        return img;
    }

    public int getLvlOffset() {
        return maxLvlOffsetX;
    }

    public Point getPlayerSpawn() {
        return playerSpawn;
    }

    public int getLvlIndex() {
        return lvlIndex;
    }
}
