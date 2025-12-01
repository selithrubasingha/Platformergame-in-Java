package levels;

import entities.Pig;
import main.Game;
import objects.GameContainer;
import objects.ObjectItem;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static main.Game.*;
import static main.Game.TILES_SIZE;
import static utilz.HelpMethods.GetPlayerSpawn;
import static utilz.LoadSave.*;

public class Level {
    private int[][] lvlData;
    private BufferedImage img;
    private ArrayList<Pig> pigs;
    private ArrayList<ObjectItem> items;
    private ArrayList<GameContainer> containers;
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
        createObjectItems();
        createContainers();
        calcLvlOffsets();
        calcPlayerSpawn();
    }

    private void createContainers() {
        this.containers = GetContainers(lvlIndex,this);
        System.out.println(containers.toArray().length);


    }

    private void createObjectItems() {

        this.items = GetItems(lvlIndex);
        System.out.println(items.toArray().length);
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
        this.pigs = GetPigs(lvlIndex);
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

    public static ArrayList<ObjectItem> GetItems(int lvlIndex) {
        ArrayList<ObjectItem> items = new ArrayList<>();
        // Data format: [x1, y1, type1, x2, y2, type2, ...]
        ArrayList<Float> itemData = GetItemSpawnsFromTMX(
                "TMXlvls/" + (lvlIndex + 1) + ".tmx",
                "Items" // Layer name from TMX file
        );

        // Iterate through the list 3 elements at a time (x, y, type)
        for (int i = 0; i < itemData.size(); i += 3) {
            float x = itemData.get(i);
            float y = itemData.get(i + 1);
            int type = itemData.get(i + 2).intValue(); // Convert Float to int for type

            items.add(new ObjectItem((int) x, (int) y, type));
        }
        return items;
    }

    public static ArrayList<GameContainer> GetContainers(int lvlIndex,Level level) {
        ArrayList<GameContainer> containers = new ArrayList<>();
        // Data format: [x1, y1, type1, x2, y2, type2, ...]
        ArrayList<Float> containerData = GetContainerSpawnsFromTMX(
                "TMXlvls/" + (lvlIndex + 1) + ".tmx",
                "Containers" // Layer name from TMX file
        );

        // Iterate through the list 3 elements at a time (x, y, type)
        for (int i = 0; i < containerData.size(); i += 3) {
            float x = containerData.get(i);
            float y = containerData.get(i + 1);
            int type = containerData.get(i + 2).intValue(); // Convert Float to int for type

            containers.add(new GameContainer((int) x, (int) y, type,level));
        }
        return containers;
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

    public ArrayList<ObjectItem> getItems() {
        return items;
    }

    public ArrayList<GameContainer> getContainers() {
        return containers;
    }
}
