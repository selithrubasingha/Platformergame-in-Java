package objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Playing;
import levels.Level;
import utilz.LoadSave;
import static utilz.Constants.ObjectConstants.*; // Imports new constants

public class ObjectManager {

    private Playing playing;
    private BufferedImage[][] itemImgs, containerImgs; // Renamed potionImgs to itemImgs
    private ArrayList<ObjectItem> items = new ArrayList<>(); // Renamed potions to items
    private ArrayList<GameContainer> containers = new ArrayList<>();

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

        for (int j = 0; j < containerImgs.length; j++)
            for (int i = 0; i < containerImgs[j].length; i++)
                containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);
    }

    public void update() {
        for (ObjectItem item : items) // Update items
            if (item.isActive())
                item.update();

        for (GameContainer gc : containers)
            if (gc.isActive())
                gc.update();
    }

    public void draw(Graphics g, int xLvlOffset) {
        drawItems(g, xLvlOffset); // Draw items
        drawContainers(g, xLvlOffset);
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