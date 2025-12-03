package objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Player;
import gamestates.Playing;
import levels.Level;
import main.Game;
import utilz.LoadSave;
import static utilz.Constants.ObjectConstants.*; // Imports new constants
import static utilz.Constants.Projectiles.CANNON_BALL_HEIGHT;
import static utilz.Constants.Projectiles.CANNON_BALL_WIDTH;
import static utilz.HelpMethods.*;
import static utilz.HelpMethods.IsProjectileHittingLevel;


public class ObjectManager {

    private Playing playing;
    private BufferedImage[][] itemImgs, containerImgs; // Renamed potionImgs to itemImgs
    private BufferedImage[] cannonImgs;
    private BufferedImage cannonBallImg ;
    private ArrayList<ObjectItem> items = new ArrayList<>(); // Renamed potions to items
    private ArrayList<GameContainer> containers = new ArrayList<>();
    private ArrayList<Cannon> cannons;
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    public ObjectManager(Playing playing) {
        this.playing = playing;
        loadImgs();
//        loadObjects(playing.getLevelManager().getCurrentLevel());

        items.add(new ObjectItem(300,300,HEART));
        items.add(new ObjectItem(400,300,DIAMOND));

    }

    public void checkObjectTouched(Rectangle2D.Float hitbox) {
        for (ObjectItem item : items) // Iterate over items
            if (item.isActive()) {
                if (hitbox.intersects(item.getHitbox())) {
                    item.setActive(false);
                    applyEffectToPlayer(item);
                }
            }
    }

    public void applyEffectToPlayer(ObjectItem item) { // Accepts ObjectItem
        if (item.getObjType() == DIAMOND) // Check for DIAMOND (e.g., score increase)
            playing.getPlayer().changeHealth(DIAMOND_VALUE); // Assuming changeScore exists
        else // Must be HEART (e.g., health increase)
            playing.getPlayer().changeHealth(HEART_VALUE);
    }

    public void checkObjectHit(Rectangle2D.Float attackbox) {
        for (GameContainer gc : containers)
            if (gc.isActive()) {
                if (gc.getHitbox().intersects(attackbox)) {
                    gc.setAnimation(true);
                    int type = DIAMOND; // Default drop is Diamond (0)
                    if (gc.getObjType() == BARREL)
                        type = HEART; // Barrel drops Heart (1)

                    // Create ObjectItem (Diamond or Heart) instead of Potion
                    items.add(new ObjectItem((int) (gc.getHitbox().x + gc.getHitbox().width / 2),
                            (int) (gc.getHitbox().y - gc.getHitbox().height / 2), type));
                    return;
                }
            }
    }

    public void loadObjects(Level newLevel) {
        items = newLevel.GetItems(newLevel.getLvlIndex()); // Assuming Level class has getItems()
        containers = newLevel.GetContainers(newLevel.getLvlIndex(),newLevel);
        cannons = newLevel.getCannons();
        projectiles.clear();
    }

    private void loadImgs() {
        // --- Item Images (Assuming the sprite sheet contains rows for Diamond and Heart) ---
        BufferedImage itemSprite = LoadSave.GetSpriteAtlas(LoadSave.ITEM_ATLAS); // New constant for item atlas
        itemImgs = new BufferedImage[2][10]; // 2 rows (Diamond, Heart), max 10 frames (Diamond idle)

        // Assuming row 0 is Heart (8 frames) and row 1 is Diamond (10 frames)
        // Adjust the loop range to the maximum frames (10 for Diamond)
        for (int j = 0; j < itemImgs.length; j++)
            for (int i = 0; i < itemImgs[j].length; i++)
                // Using new default dimensions for Item: 12x10
                itemImgs[j][i] = itemSprite.getSubimage(ITEM_WIDTH_DEFAULT * i,
                        ITEM_HEIGHT_DEFAULT * j,
                        ITEM_WIDTH_DEFAULT,
                        ITEM_HEIGHT_DEFAULT);

        // --- Container Images (Unchanged) ---
        BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
        containerImgs = new BufferedImage[2][8];

        cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.CANNON_BALL);

        for (int j = 0; j < containerImgs.length; j++)
            for (int i = 0; i < containerImgs[j].length; i++)
                containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);

        cannonImgs = new BufferedImage[5];
        BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CANNON_ATLAS);

        for (int i = 0; i < cannonImgs.length; i++)
            cannonImgs[i] = temp.getSubimage(i * 44, 0, 44, 28);
    }

    public void update(int[][] lvlData, Player player) {
        for (ObjectItem item : items) // Update items
            if (item.isActive())
                item.update();

        for (GameContainer gc : containers)
            if (gc.isActive())
                gc.update();

        updateCannons(lvlData, player);
        updateProjectiles(lvlData , player);
    }

    private void updateProjectiles(int[][] lvlData, Player player) {
        for (Projectile p : projectiles)
            if (p.isActive()) {
                p.updatePos();
                if (p.getHitbox().intersects(player.getHitbox())) {
                    player.changeHealth(-25);
                    p.setActive(false);
                } else if (IsProjectileHittingLevel(p, lvlData))
                    p.setActive(false);
            }
    }

    private boolean isPlayerInRange(Cannon c, Player player) {
        int absValue = (int) Math.abs(player.getHitbox().x - c.getHitbox().x);
        return absValue <= Game.TILES_SIZE * 5;
    }

    private boolean isPlayerInfrontOfCannon(Cannon c, Player player) {
        if (c.getObjType() == CANNON_LEFT) {
            if (c.getHitbox().x > player.getHitbox().x)
                return true;

        } else if (c.getHitbox().x < player.getHitbox().x)
            return true;
        return false;
    }

    private void updateCannons(int[][] lvlData, Player player) {
        for (Cannon c : cannons) {

            if (!c.doAnimation)
                if (c.getTileY() == player.getTileY())
                    if (isPlayerInRange(c, player))
                        if (isPlayerInfrontOfCannon(c, player))
                            if (CanCannonSeePlayer(lvlData, player.getHitbox(), c.getHitbox(), c.getTileY())) {
                                c.setAnimation(true);

                            }


            c.update();
            if (c.getAniIndex() == 2 && c.getAniTick() == 0)
                shootCannon(c);
        }
    }

    private void shootCannon(Cannon c) {

        int dir = 1;
        if (c.getObjType() == CANNON_LEFT)
            dir = -1;

        projectiles.add(new Projectile((int) c.getHitbox().x, (int) c.getHitbox().y, dir));
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawItems(g, xLvlOffset); // Draw items
        drawContainers(g, xLvlOffset);
        drawCannons(g, xLvlOffset);
        drawProjectiles(g,xLvlOffset);

    }

    private void drawProjectiles(Graphics g, int xLvlOffset) {
        for (Projectile p : projectiles)
            if (p.isActive())
                g.drawImage(cannonBallImg, (int) (p.getHitbox().x - xLvlOffset), (int) (p.getHitbox().y), CANNON_BALL_WIDTH, CANNON_BALL_HEIGHT, null);

    }

    private void drawCannons(Graphics g, int xLvlOffset) {
        for (Cannon c : cannons) {
            int x = (int) (c.getHitbox().x - xLvlOffset);
            int width = CANNON_WIDTH;

            if (c.getObjType() == CANNON_RIGHT) {
                x += width;
                width *= -1;
            }

            g.drawImage(cannonImgs[c.getAniIndex()], x, (int) (c.getHitbox().y), width, CANNON_HEIGHT, null);
        }

    }

    private void drawContainers(Graphics g, int xLvlOffset) {
        for (GameContainer gc : containers)
            if (gc.isActive()) {
                int type = 0;
                if (gc.getObjType() == BARREL)
                    type = 1;
                g.drawImage(containerImgs[type][gc.getAniIndex()], (int) (gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset), (int) (gc.getHitbox().y - gc.getyDrawOffset()), CONTAINER_WIDTH,
                        CONTAINER_HEIGHT, null);
            }
    }

    private void drawItems(Graphics g, int xLvlOffset) { // Renamed drawPotions to drawItems
        for (ObjectItem item : items) // Draw items
            if (item.isActive()) {
                int type = 0;
                // Since DIAMOND is 0 and HEART is 1, we use the constant values directly
                if (item.getObjType() == HEART)
                    type = 1;
                // Note: If your sprite sheet has HEART on row 0 and DIAMOND on row 1,
                // you will need to adjust this logic (e.g., type = item.getObjType() itself).

                g.drawImage(itemImgs[type][item.getAniIndex()],
                        (int) (item.getHitbox().x - item.getxDrawOffset() - xLvlOffset),
                        (int) (item.getHitbox().y - item.getyDrawOffset()),
                        ITEM_WIDTH, ITEM_HEIGHT, // Use new ITEM dimensions
                        null);
            }
    }

    public void resetAllObjects() {
        for (ObjectItem item : items) // Reset items
            item.reset();

        for (GameContainer gc : containers)
            gc.reset();
    }

}