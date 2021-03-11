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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateMaterialPacketTest extends BaseMcTest {

  public static final MaterialId MATERIAL_ID_1 = MaterialFixture.MATERIAL_1.getIdentifier();
  public static final MaterialId MATERIAL_ID_2 = MaterialFixture.MATERIAL_2.getIdentifier();

  @BeforeAll
  static void beforeAll() {
    ModifierFixture.init();
  }

  @Test
  void testGenericEncodeDecode() {
    IMaterial material1 = new Material(MATERIAL_ID_1, 1, 2, Fluids.WATER, 123, true, Color.fromInt(0x123456), 100,
                                       Arrays.asList(new ModifierEntry(ModifierFixture.TEST_MODIFIER_1, 2), new ModifierEntry(ModifierFixture.TEST_MODIFIER_2, 3)));
    IMaterial material2 = new Material(MATERIAL_ID_2, 3, 4, Fluids.EMPTY, 0, false, Color.fromInt(0xFFFFFF), 0, Collections.emptyList());
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
    assertThat(parsedMat.getFluid()).isEqualTo(Fluids.WATER);
    assertThat(parsedMat.getFluidPerUnit()).isEqualTo(123);
    assertThat(parsedMat.isCraftable()).isTrue();
    assertThat(parsedMat.getColor().color).isEqualTo(0x123456);
    assertThat(parsedMat.getTemperature()).isEqualTo(100);
    // traits
    List<ModifierEntry> traits = parsedMat.getTraits();
    assertThat(traits).hasSize(2);
    ModifierEntry trait = traits.get(0);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(trait.getLevel()).isEqualTo(2);
    trait = traits.get(1);
    assertThat(trait.getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_2);
    assertThat(trait.getLevel()).isEqualTo(3);

    // material 2
    parsedMat = iterator.next();
    assertThat(parsedMat.getIdentifier()).isEqualTo(MATERIAL_ID_2);
    assertThat(parsedMat.getTier()).isEqualTo(3);
    assertThat(parsedMat.getSortOrder()).isEqualTo(4);
    assertThat(parsedMat.getFluid()).isEqualTo(Fluids.EMPTY);
    assertThat(parsedMat.getFluidPerUnit()).isEqualTo(0);
    assertThat(parsedMat.isCraftable()).isFalse();
    assertThat(parsedMat.getColor().color).isEqualTo(0xFFFFFF);
    assertThat(parsedMat.getTemperature()).isEqualTo(0);
    assertThat(parsedMat.getTraits()).isEmpty();
  }
}
