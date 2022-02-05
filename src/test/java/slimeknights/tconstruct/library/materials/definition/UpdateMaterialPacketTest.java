package slimeknights.tconstruct.library.materials.definition;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextColor;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateMaterialPacketTest extends BaseMcTest {

  public static final MaterialId MATERIAL_ID_1 = MaterialFixture.MATERIAL_1.getIdentifier();
  public static final MaterialId MATERIAL_ID_2 = MaterialFixture.MATERIAL_2.getIdentifier();
  public static final MaterialId REDIRECT_ID = new MaterialId("test", "redirect");

  @Test
  void testGenericEncodeDecode() {
    IMaterial material1 = new Material(MATERIAL_ID_1, 1, 2, true, TextColor.fromRgb(0x123456), false);
    IMaterial material2 = new Material(MATERIAL_ID_2, 3, 4, false, TextColor.fromRgb(0xFFFFFF), true);
    Collection<IMaterial> materials = ImmutableList.of(material1, material2);
    Map<MaterialId,MaterialId> redirects = ImmutableMap.of(REDIRECT_ID, MATERIAL_ID_1);

    // send a packet over the buffer
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    UpdateMaterialsPacket packetToEncode = new UpdateMaterialsPacket(materials, redirects);
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
    assertThat(parsedMat.getColor().value).isEqualTo(0x123456);
    assertThat(parsedMat.isHidden()).isFalse();

    // material 2
    parsedMat = iterator.next();
    assertThat(parsedMat.getIdentifier()).isEqualTo(MATERIAL_ID_2);
    assertThat(parsedMat.getTier()).isEqualTo(3);
    assertThat(parsedMat.getSortOrder()).isEqualTo(4);
    assertThat(parsedMat.isCraftable()).isFalse();
    assertThat(parsedMat.getColor().value).isEqualTo(0xFFFFFF);
    assertThat(parsedMat.isHidden()).isTrue();

    // redirects not included
    Map<MaterialId,MaterialId> decodedRedirects = decoded.getRedirects();
    assertThat(decodedRedirects).hasSize(1);
    assertThat(decodedRedirects.get(REDIRECT_ID)).isEqualTo(MATERIAL_ID_1);
  }
}
