package slimeknights.tconstruct.library.materials.stats;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Tiers;

import org.junit.jupiter.api.Test;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.MaterialStatTypesFixture;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicMaterialStatType;
import slimeknights.tconstruct.library.materials.stats.dynamic.FloatDynamicStatField;
import slimeknights.tconstruct.library.materials.stats.dynamic.RepairableDynamicMaterialStats;
import slimeknights.tconstruct.library.materials.stats.dynamic.TierDynamicStatField;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import java.util.Collection;
import java.util.Iterator;
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
        MATERIAL_ID, List.of(MaterialStatsFixture.MATERIAL_STATS, MaterialStatTypesFixture.MATERIAL_STATS));

    Map<MaterialStatsId, DynamicMaterialStatType> dynamicStatTypes = Map.of(
        MaterialStatTypesFixture.DYNAMIC_TYPE.getId(), MaterialStatTypesFixture.DYNAMIC_TYPE
    );
    UpdateMaterialStatsPacket packetToDecode = sendAndReceivePacket(materialToStats, dynamicStatTypes);
    assertThat(packetToDecode.dynamicStatTypes).hasSize(1);
    assertThat(packetToDecode.dynamicStatTypes).containsKey(MaterialStatTypesFixture.STATS_TYPE);
    assertThat(packetToDecode.dynamicStatTypes.get(MaterialStatTypesFixture.STATS_TYPE)).isEqualTo(MaterialStatTypesFixture.DYNAMIC_TYPE);

    assertThat(packetToDecode.materialToStats).hasSize(1);
    assertThat(packetToDecode.materialToStats).containsKey(MATERIAL_ID);
    assertThat(packetToDecode.materialToStats.get(MATERIAL_ID)).hasSize(2);

    Iterator<IMaterialStats> iterator = packetToDecode.materialToStats.get(MATERIAL_ID).iterator();
    IMaterialStats materialStats = iterator.next();
    assertThat(materialStats).isExactlyInstanceOf(ComplexTestStats.class);
    // ensure the loadable is passed the context field for the proper type
    assertThat(materialStats.getType()).isEqualTo(MaterialStatsFixture.COMPLEX_TYPE);
    ComplexTestStats realStats = (ComplexTestStats) materialStats;
    assertThat(realStats.num()).isEqualTo(1);
    assertThat(realStats.floating()).isEqualTo(2f);
    assertThat(realStats.text()).isEqualTo("3");

    materialStats = iterator.next();
    assertThat(materialStats).isExactlyInstanceOf(RepairableDynamicMaterialStats.class);
    // ensure the loadable is passed the context field for the proper type
    assertThat(materialStats.getType().getId()).isEqualTo(MaterialStatTypesFixture.STATS_TYPE);
    RepairableDynamicMaterialStats realDynamicStats = (RepairableDynamicMaterialStats) materialStats;
    assertThat(realDynamicStats.getStat("test1")).isExactlyInstanceOf(FloatDynamicStatField.FloatDynamicStat.class);
    assertThat(((FloatDynamicStatField.FloatDynamicStat)realDynamicStats.getStat("test1")).getValue()).isEqualTo(1f);
    assertThat(realDynamicStats.getStat("test2")).isExactlyInstanceOf(FloatDynamicStatField.FloatDynamicStat.class);
    assertThat(((FloatDynamicStatField.FloatDynamicStat)realDynamicStats.getStat("test2")).getValue()).isEqualTo(2f);
    assertThat(realDynamicStats.getStat("test3")).isExactlyInstanceOf(TierDynamicStatField.TierDynamicStat.class);
    assertThat(((TierDynamicStatField.TierDynamicStat)realDynamicStats.getStat("test3")).getValue()).isEqualTo(Tiers.STONE);
    assertThat(realDynamicStats.durability()).isEqualTo(2);

  }

  @Test
  void testAllTicDefaults() {
    List<IMaterialStats> stats = List.of(
        HeadMaterialStats.TYPE.getDefaultStats(),
        HandleMaterialStats.TYPE.getDefaultStats(),
        StatlessMaterialStats.BINDING);
    Map<MaterialId, Collection<IMaterialStats>> materialToStats = Map.of(MATERIAL_ID, stats);
    
    UpdateMaterialStatsPacket packet = sendAndReceivePacket(materialToStats, Map.of());

    assertThat(packet.materialToStats.get(MATERIAL_ID)).isEqualTo(stats);
  }

  private UpdateMaterialStatsPacket sendAndReceivePacket(Map<MaterialId, Collection<IMaterialStats>> materialToStats, Map<MaterialStatsId, DynamicMaterialStatType> dynamicStatTypes) {
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());

    UpdateMaterialStatsPacket packetToEncode = new UpdateMaterialStatsPacket(materialToStats, dynamicStatTypes);
    packetToEncode.encode(buffer);

    return new UpdateMaterialStatsPacket(buffer, LOADER);
  }
}
