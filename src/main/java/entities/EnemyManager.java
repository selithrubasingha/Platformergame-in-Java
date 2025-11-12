package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Playing;
import utilz.LoadSave;

import static utilz.Constants.EnemyConstants.*;

public class EnemyManager {

    private Playing playing;
    private BufferedImage[][] pigArr;
    private ArrayList<Pig> pigs = new ArrayList<>();

    public EnemyManager(Playing playing) {
        this.playing = playing;
        loadEnemyImgs();
        addEnemies();
    }

    private void addEnemies() {
        pigs = LoadSave.GetPigs();
        System.out.println("size of crabs: " + pigs.size());
    }

    public void update(int[][] lvlData) {
        for (Pig c : pigs)
            c.update(lvlData);
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawPigs(g, xLvlOffset);
    }

    private void drawPigs(Graphics g, int xLvlOffset) {
        for (Pig c : pigs){
            g.drawImage(pigArr[c.getEnemyState()][c.getAniIndex()], (int) c.getHitbox().x - xLvlOffset-PIG_DRAWOFFSET_X, (int) c.getHitbox().y-PIG_DRAWOFFSET_Y, PIG_WIDTH, PIG_HEIGHT, null);
            c.drawHitbox(g, xLvlOffset);}


    }

    private void loadEnemyImgs() {
        pigArr = new BufferedImage[8][11];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.PIG_SPRITE);
        for (int j = 0; j < pigArr.length; j++)
            for (int i = 0; i < pigArr[j].length; i++)
                pigArr[j][i] = temp.getSubimage(i * PIG_WIDTH_DEFAULT, j * PIG_HEIGHT_DEFAULT, PIG_WIDTH_DEFAULT, PIG_HEIGHT_DEFAULT);
    }
}
