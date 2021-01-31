package slimeknights.tconstruct.library.network;

import com.google.common.collect.ImmutableList;
import io.netty.buffer.Unpooled;
import net.minecraft.fluid.Fluids;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.ModifierFixture;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateMaterialPacketTest extends BaseMcTest {

  public static final MaterialId MATERIAL_ID = MaterialFixture.MATERIAL_1.getIdentifier();

  @BeforeAll
  static void beforeAll() {
    ModifierFixture.init();
  }

  @Test
  void testGenericEncodeDecode() {
    IMaterial material = new Material(MATERIAL_ID, Fluids.WATER, 123, true, Color.fromInt(0x123456), 100,
                                      Collections.singletonList(new ModifierEntry(ModifierFixture.TEST_MODIFIER_1, 2)));
    Collection<IMaterial> materials = ImmutableList.of(material);

    // send a packet over the buffer
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
    UpdateMaterialsPacket packetToEncode = new UpdateMaterialsPacket(materials);
    packetToEncode.encode(buffer);
    UpdateMaterialsPacket decoded = new UpdateMaterialsPacket(buffer);

    // parse results
    Collection<IMaterial> parsed = decoded.getMaterials();
    assertThat(parsed).hasSize(1);
    IMaterial parsedMat = parsed.iterator().next();
    assertThat(parsedMat.getIdentifier()).isEqualTo(MATERIAL_ID);
    assertThat(parsedMat.getFluid()).isEqualTo(Fluids.WATER);
    assertThat(parsedMat.getFluidPerUnit()).isEqualTo(123);
    assertThat(parsedMat.isCraftable()).isTrue();
    assertThat(parsedMat.getColor().color).isEqualTo(0x123456);
    assertThat(parsedMat.getTemperature()).isEqualTo(100);
    // traits
    List<ModifierEntry> traits = parsedMat.getTraits();
    assertThat(traits).hasSize(1);
    ModifierEntry entry = traits.get(0);
    assertThat(entry.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(entry.getLevel()).isEqualTo(2);
  }
}
