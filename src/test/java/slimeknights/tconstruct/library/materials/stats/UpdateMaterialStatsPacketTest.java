package slimeknights.tconstruct.library.materials.stats;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.types.FlexMaterialStatType;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateMaterialStatsPacketTest extends BaseMcTest {

  public static final MaterialId MATERIAL_ID = MaterialFixture.MATERIAL_1.getIdentifier();
  private static final IdAwareComponentRegistry<MaterialStatType<?>> LOADER = new IdAwareComponentRegistry<>("Unknown stat type");
  static {
    LOADER.register(MaterialStatsFixture.COMPLEX_TYPE);
    LOADER.register(HeadMaterialStats.TYPE);
    LOADER.register(HandleMaterialStats.TYPE);
    LOADER.register(StatlessMaterialStats.BINDING.getType());
  }

  @Test
  void testGenericEncodeDecode() {
    Map<MaterialId, Collection<IMaterialStats>> materialToStats = Map.of(
      MATERIAL_ID, List.of(MaterialStatsFixture.MATERIAL_STATS)
    );

    Collection<FlexMaterialStatType> dynamic = new HashSet<>();// I dont know how to test so just an empty set
    UpdateMaterialStatsPacket packetToDecode = sendAndReceivePacket(materialToStats, dynamic);
    assertThat(packetToDecode.materialToStats).hasSize(1);
    assertThat(packetToDecode.materialToStats).containsKey(MATERIAL_ID);
    assertThat(packetToDecode.materialToStats.get(MATERIAL_ID)).hasSize(1);

    IMaterialStats materialStats = packetToDecode.materialToStats.get(MATERIAL_ID).iterator().next();
    assertThat(materialStats).isExactlyInstanceOf(ComplexTestStats.class);
    // ensure the loadable is passed the context field for the proper type
    assertThat(materialStats.getType()).isEqualTo(MaterialStatsFixture.COMPLEX_TYPE);
    ComplexTestStats realStats = (ComplexTestStats) materialStats;
    assertThat(realStats.num()).isEqualTo(1);
    assertThat(realStats.floating()).isEqualTo(2f);
    assertThat(realStats.text()).isEqualTo("3");
  }

  @Test
  void testAllTicDefaults() {
    List<IMaterialStats> stats = List.of(
        HeadMaterialStats.TYPE.getDefaultStats(),
        HandleMaterialStats.TYPE.getDefaultStats(),
        StatlessMaterialStats.BINDING);
    Map<MaterialId, Collection<IMaterialStats>> materialToStats = Map.of(MATERIAL_ID, stats);
    Collection<FlexMaterialStatType> dynamic = new HashSet<>();
    UpdateMaterialStatsPacket packet = sendAndReceivePacket(materialToStats, dynamic);

    assertThat(packet.materialToStats.get(MATERIAL_ID)).isEqualTo(stats);
  }

  private UpdateMaterialStatsPacket sendAndReceivePacket(Map<MaterialId, Collection<IMaterialStats>> materialToStats, Collection<FlexMaterialStatType> dynamic) {
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

    UpdateMaterialStatsPacket packetToEncode = new UpdateMaterialStatsPacket(materialToStats, dynamic);
    packetToEncode.encode(buffer);

    return new UpdateMaterialStatsPacket(buffer, LOADER);
  }
}
