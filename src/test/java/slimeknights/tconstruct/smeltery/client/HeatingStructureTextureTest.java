package slimeknights.tconstruct.smeltery.client;

import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class HeatingStructureTextureTest {
  private static final String TEXTURE_PATH = "/assets/tconstruct/textures/gui/heating_structure.png";
  private static final int MAX_TEXTURE_BYTES = 512 * 1024;
  // Side inventory slot tile is drawn from (0, 238) 22x18 (4px heat bar + 18x18 slot), so its 16x16 interior is (5, 239)
  private static final int SIDE_SLOT_INTERIOR_X = 5;
  private static final int SIDE_SLOT_INTERIOR_Y = 239;
  // First player inventory slot is at (7, 137) 18x18, so its 16x16 interior is (8, 138)
  private static final int INTACT_SLOT_INTERIOR_X = 8;
  private static final int INTACT_SLOT_INTERIOR_Y = 138;
  private static final int SLOT_INTERIOR_SIZE = 16;

  @Test
  void sideInventorySlotInteriorMatchesIntactSlot() throws IOException {
    BufferedImage image;
    try (InputStream stream = HeatingStructureTextureTest.class.getResourceAsStream(TEXTURE_PATH)) {
      assertThat(stream).as("Heating structure texture resource").isNotNull();
      byte[] png = stream.readNBytes(MAX_TEXTURE_BYTES + 1);
      assertThat(png.length).as("Encoded texture size").isLessThanOrEqualTo(MAX_TEXTURE_BYTES);
      ImageReader reader = ImageIO.getImageReadersByFormatName("PNG").next();
      try (var input = new MemoryCacheImageInputStream(new ByteArrayInputStream(png))) {
        reader.setInput(input, true, true);
        assertThat(reader.getWidth(0)).as("Atlas width").isEqualTo(256);
        assertThat(reader.getHeight(0)).as("Atlas height").isEqualTo(256);
        image = reader.read(0);
      } finally {
        reader.dispose();
      }
    }
    assertThat(image).as("Decoded heating structure texture").isNotNull();

    for (int dy = 0; dy < SLOT_INTERIOR_SIZE; dy++) {
      for (int dx = 0; dx < SLOT_INTERIOR_SIZE; dx++) {
        assertThat(image.getRGB(SIDE_SLOT_INTERIOR_X + dx, SIDE_SLOT_INTERIOR_Y + dy))
          .as("Side slot interior pixel (%d, %d)", dx, dy)
          .isEqualTo(image.getRGB(INTACT_SLOT_INTERIOR_X + dx, INTACT_SLOT_INTERIOR_Y + dy));
      }
    }
  }
}
