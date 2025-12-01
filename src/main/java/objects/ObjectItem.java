package objects;

import main.Game;

public class ObjectItem extends GameObject { // Renamed from Potion

    private float hoverOffset;
    private int maxHoverOffset, hoverDir = 1;

    public ObjectItem(int x, int y, int objType) {
        super(x, y, objType);
        doAnimation = true;

        // Initializes hitbox based on diagram width/height (12x10)
        // Using slightly smaller hitbox (e.g., 8x8) for better player pickup
        initHitbox(12, 10);

        // Using the draw offsets defined in ObjectConstants
        xDrawOffset = (int) (5 * Game.SCALE);
        yDrawOffset = (int) (2 * Game.SCALE);

        maxHoverOffset = (int) (10 * Game.SCALE);
    }

    public void update() {
        updateAnimationTick();
        updateHover();
    }

    private void updateHover() {
        hoverOffset += (0.075f * Game.SCALE * hoverDir);

        if (hoverOffset >= maxHoverOffset)
            hoverDir = -1;
        else if (hoverOffset < 0)
            hoverDir = 1;

        // Apply the hover offset to the hitbox's Y position
        hitbox.y = y + hoverOffset;
    }
}