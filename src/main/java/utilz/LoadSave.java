package utilz;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class LoadSave {

    public static final String PLAYER_ATLAS = "player_sprites.png";
    public static final String LEVEL_ATLAS = "map.png";
    // New constant for the TMX file name
    public static final String TMX_LEVEL = "supermap.tmx";

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
}

