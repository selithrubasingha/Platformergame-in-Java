package utilz;

import main.Game;

public class Constants {

    public static final float GRAVITY = 0.04f * Game.SCALE;
    public static final int ANI_SPEED = 25;

    public static class EnemyConstants {

        public static final int PIG = 0;

        // Animation States and their integer identifiers (Indices 0-7 from your image)
        public static final int ATTACK = 0;
        public static final int DEAD = 1;
        public static final int FALL = 2;
        public static final int GROUND = 3;
        public static final int HIT = 4;
        public static final int IDLE = 5;
        public static final int JUMP = 6;
        public static final int RUN = 7;

        // Assuming a standard TILE_SIZE for sprite manipulation
        public static final int PIG_WIDTH_DEFAULT = 34; // Example width
        public static final int PIG_HEIGHT_DEFAULT = 28;
        public static final int PIG_WIDTH = (int) (PIG_WIDTH_DEFAULT * Game.SCALE);
        public static final int PIG_HEIGHT = (int) (PIG_HEIGHT_DEFAULT * Game.SCALE);

        public static int PIG_DRAWOFFSET_X = (int) (15 * Game.SCALE);
        public static final int PIG_DRAWOFFSET_Y = (int) (13 * Game.SCALE);



        // --- Frame Counts based on the provided image data ---

        // Method to return the number of sprite frames for a given action
        public static int GetSpriteAmount(int enemy_type, int enemy_state) {

            switch (enemy_type) {
                case PIG:
                    switch (enemy_state) {
                        case IDLE:
                            // Index 5: IDLE (11 frames)
                            return 11;
                        case RUN:
                            // Index 7: RUN (6 frames)
                            return 6;
                        case ATTACK:
                            // Index 0: ATTACK (5 frames)
                            return 5;
                        case DEAD:
                            // Index 1: DEAD (4 frames)
                            return 4;
                        case HIT:
                            // Index 4: HIT (2 frames)
                            return 2;
                        case FALL:
                        case GROUND:
                        case JUMP:
                            // Indices 2, 3, 6: FALL, GROUND, JUMP (1 frame each)
                            return 1;
                        default:
                            // Safety fallback
                            return 1;
                    }


            }
            return 0;
        }

        public static int GetMaxHealth(int enemy_type){
            switch(enemy_type){
                case PIG :
                    return 10;
                default:
                    return 1;
            }
        }

        public static int GetEnemyDmg(int enemy_type){
            switch (enemy_type){
                case PIG:
                    return 15;
                default:
                    return 0 ;
            }
        }


    }

    public static class UI {
        public static class Buttons {
            public static final int B_WIDTH_DEFAULT = 140;
            public static final int B_HEIGHT_DEFAULT = 56;
            public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * Game.SCALE);
            public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * Game.SCALE);
        }

        public static class PauseButtons {
            public static final int SOUND_SIZE_DEFAULT = 42;
            public static final int SOUND_SIZE = (int) (SOUND_SIZE_DEFAULT * Game.SCALE);
        }

        public static class URMButtons {
            public static final int URM_DEFAULT_SIZE = 56;
            public static final int URM_SIZE = (int) (URM_DEFAULT_SIZE * Game.SCALE);

        }

        public static class VolumeButtons {
            public static final int VOLUME_DEFAULT_WIDTH = 28;
            public static final int VOLUME_DEFAULT_HEIGHT = 44;
            public static final int SLIDER_DEFAULT_WIDTH = 215;

            public static final int VOLUME_WIDTH = (int) (VOLUME_DEFAULT_WIDTH * Game.SCALE);
            public static final int VOLUME_HEIGHT = (int) (VOLUME_DEFAULT_HEIGHT * Game.SCALE);
            public static final int SLIDER_WIDTH = (int) (SLIDER_DEFAULT_WIDTH * Game.SCALE);
        }
    }
    // Constants for player movement directions
    public static class Directions {
        public static final int LEFT = 0;
        public static final int UP = 1;
        public static final int RIGHT = 2;
        public static final int DOWN = 3;
    }

    // Constants for all player animation states in your new game
    public static class PlayerConstants {
        // New Animation States and their integer identifiers
        public static final int ATTACK = 0;
        public static final int DEAD = 1;
        public static final int DOOR_IN = 2;
        public static final int DOOR_OUT = 3;
        public static final int FALL = 4;
        public static final int GROUND = 5;
        public static final int HIT = 6;
        public static final int IDLE = 7;
        public static final int JUMP = 8;
        public static final int RUN = 9;


        // Method to return the number of sprite frames for a given action
        public static int GetSpriteAmount(int player_action) {
            switch (player_action) {
                case IDLE:
                    // 11 frames (from your data)
                    return 11;
                case DOOR_IN:
                case DOOR_OUT:
                case RUN:
                    // 8 frames (from your data)
                    return 8;
                case DEAD:
                    // 4 frames (from your data)
                    return 4;
                case ATTACK:
                    // 3 frames (from your data)
                    return 3;
                case HIT:
                    // 2 frames (from your data)
                    return 2;
                case FALL:
                case GROUND:
                case JUMP:
                default:
                    // 1 frame (from your data, or as a default fallback)
                    return 1;
            }
        }
    }

}