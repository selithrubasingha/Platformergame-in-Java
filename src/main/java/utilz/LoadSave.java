package utilz;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import entities.Pig;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class LoadSave {

    public static final String PLAYER_ATLAS = "player_sprites.png";
    public static final String LEVEL_ATLAS = "extendedsupermap.png";
    public static final String MENU_BUTTONS = "button_atlas.png";
    public static final String MENU_BACKGROUND = "menu_background.png";
    public static final String PAUSE_BACKGROUND = "pause_menu.png";
    public static final String SOUND_BUTTONS = "sound_button.png";
    public static final String URM_BUTTONS = "urm_buttons.png";
    public static final String VOLUME_BUTTONS = "volume_buttons.png";
    public static final String MENU_BACKGROUND_IMG = "menu_background_green.jpeg";
    public static final String PIG_SPRITE = "pig.png";

    // New constant for the TMX file name
    public static final String TMX_LEVEL = "supermap2.tmx";

    // Existing method to load PNG/Image files
    public static BufferedImage GetSpriteAtlas(String fileName){
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/"+fileName);

        try {
            img = ImageIO.read(is);
        } catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (is != null) is.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return img;
    }

    // NEW method to load collision data from the TMX file
    public static int[][] GetLevelDataFromTMX(String fileName, String collisionLayerName, int mapWidth, int mapHeight) {

        int[][] levelData = new int[mapHeight][mapWidth];

        try (InputStream is = LoadSave.class.getResourceAsStream("/" + fileName)) {

            // 1. Initialize XML Parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            // 2. Find all <layer> tags
            NodeList layerNodes = doc.getElementsByTagName("layer");

            for (int i = 0; i < layerNodes.getLength(); i++) {

                // Check if this is the target collision layer
                String layerName = layerNodes.item(i).getAttributes().getNamedItem("name").getNodeValue();

                if (layerName.equals(collisionLayerName)) {

                    // 3. Get the <data> node (containing the CSV)
                    NodeList dataNodes = ((org.w3c.dom.Element) layerNodes.item(i)).getElementsByTagName("data");
                    String csvData = dataNodes.item(0).getTextContent().trim();

                    // 4. Parse the CSV data string
                    String[] tiles = csvData.split(",");

                    // 5. Populate the 2D array
                    for (int j = 0; j < mapHeight; j++) {
                        for (int k = 0; k < mapWidth; k++) {

                            // The index in the 1D tiles array
                            int arrayIndex = j * mapWidth + k;

                            // Check for array bounds before parsing
                            if (arrayIndex >= tiles.length) {
                                System.err.println("TMX Parsing Error: Data index out of bounds.");
                                break;
                            }

                            int tileId = Integer.parseInt(tiles[arrayIndex].trim());

                            // Tiled uses 0 for empty space. Any tile ID > 0 is a solid tile.
                            levelData[j][k] = (tileId > 0) ? 1 : 0;
                        }
                    }
                    break; // Collision layer found, exit the loop
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading TMX file: " + e.getMessage());
            e.printStackTrace();
        }



        return levelData;
    }

    public static ArrayList<Float> GetPigSpawnsFromTMX(String fileName, String objectLayerName) {
        // We'll return an ArrayList of Floats: [x1, y1, x2, y2, ...]
        ArrayList<Float> pigSpawnCoords = new ArrayList<>();

        try (InputStream is = LoadSave.class.getResourceAsStream("/" + fileName)) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();

            // 1. Find all <objectgroup> tags
            NodeList objectGroupNodes = doc.getElementsByTagName("objectgroup");

            for (int i = 0; i < objectGroupNodes.getLength(); i++) {

                Element groupElement = (Element) objectGroupNodes.item(i);
                String groupName = groupElement.getAttribute("name");

                // 2. Check if this is the target "Spawns" layer
                if (groupName.equals(objectLayerName)) {

                    // 3. Get all <object> nodes within this group
                    NodeList objectNodes = groupElement.getElementsByTagName("object");

                    for (int j = 0; j < objectNodes.getLength(); j++) {
                        Element objectElement = (Element) objectNodes.item(j);

                        // 4. Filter objects by name="pig" (as set in your TMX)
                        String objectName = objectElement.getAttribute("name");

                        if (objectName.equals("pig")) {

                            // 5. Extract x and y coordinates
                            // TMX stores coordinates as attributes in the <object> tag
                            float x = Float.parseFloat(objectElement.getAttribute("x"));
                            float y = Float.parseFloat(objectElement.getAttribute("y"));

                            // Store the coordinates
                            pigSpawnCoords.add(x);
                            pigSpawnCoords.add(y);
                        }
                    }
                    break; // Spawns layer found, exit the object group loop
                }
            }

        } catch (Exception e) {
            System.err.println("Error loading Pig Spawns from TMX file: " + e.getMessage());
            e.printStackTrace();
        }

        return pigSpawnCoords;
    }

    // The placeholder function can now be removed or updated to use the new method
    // If you need a list of actual Pig objects, you'd process the coordinates here.
    // For simplicity, I've made the extractor the main public method.
    public static ArrayList<Float> GetPigSpawns() {
        // Use the new, robust method
        return GetPigSpawnsFromTMX(TMX_LEVEL, "Spawns");
    }

    public static ArrayList<Pig> GetPigs() {
        ArrayList<Pig> pigs = new ArrayList<>();
        ArrayList<Float> spawnCoords = GetPigSpawnsFromTMX(TMX_LEVEL, "Spawns");
        for (int i=0 ; i< spawnCoords.size();i++){
            float x = spawnCoords.get(i++);
            float y = spawnCoords.get(i);
            pigs.add(new Pig(x, y));
        }
        return pigs;
    }
}

