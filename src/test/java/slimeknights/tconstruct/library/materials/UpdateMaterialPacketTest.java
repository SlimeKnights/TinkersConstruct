package slimeknights.tconstruct.library.materials;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.Color;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.Collection;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateMaterialPacketTest extends BaseMcTest {

  public static final MaterialId MATERIAL_ID_1 = MaterialFixture.MATERIAL_1.getIdentifier();
  public static final MaterialId MATERIAL_ID_2 = MaterialFixture.MATERIAL_2.getIdentifier();

  @Test
  void testGenericEncodeDecode() {
    IMaterial material1 = new Material(MATERIAL_ID_1, 1, 2, true, Color.fromInt(0x123456), false);
    IMaterial material2 = new Material(MATERIAL_ID_2, 3, 4, false, Color.fromInt(0xFFFFFF), true);
    Collection<IMaterial> materials = ImmutableList.of(material1, material2);

    // send a packet over the buffer
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    UpdateMaterialsPacket packetToEncode = new UpdateMaterialsPacket(materials);
    packetToEncode.encode(buffer);
    UpdateMaterialsPacket decoded = new UpdateMaterialsPacket(buffer);

    // parse results
    Collection<IMaterial> parsed = decoded.getMaterials();
    assertThat(parsed).hasSize(2);

    // material 1
    Iterator<IMaterial> iterator = parsed.iterator();
    IMaterial parsedMat = iterator.next();
    assertThat(parsedMat.getIdentifier()).isEqualTo(MATERIAL_ID_1);
    assertThat(parsedMat.getTier()).isEqualTo(1);
    assertThat(parsedMat.getSortOrder()).isEqualTo(2);
    assertThat(parsedMat.isCraftable()).isTrue();
    assertThat(parsedMat.getColor().color).isEqualTo(0x123456);
    assertThat(parsedMat.isHidden()).isFalse();

    // material 2
    parsedMat = iterator.next();
    assertThat(parsedMat.getIdentifier()).isEqualTo(MATERIAL_ID_2);
    assertThat(parsedMat.getTier()).isEqualTo(3);
    assertThat(parsedMat.getSortOrder()).isEqualTo(4);
    assertThat(parsedMat.isCraftable()).isFalse();
    assertThat(parsedMat.getColor().color).isEqualTo(0xFFFFFF);
    assertThat(parsedMat.isHidden()).isTrue();
  }
}
