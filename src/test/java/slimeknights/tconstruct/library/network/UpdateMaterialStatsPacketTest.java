package slimeknights.tconstruct.library.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.ComplexTestStats;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.tools.stats.CommonMaterialStats;

import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateMaterialStatsPacketTest extends BaseMcTest {

  public static final MaterialId MATERIAL_ID = MaterialFixture.MATERIAL_1.getIdentifier();

  @Test
  void testGenericEncodeDecode() {
    Map<MaterialId, Collection<? extends BaseMaterialStats>> materialToStats = ImmutableMap.of(
      MATERIAL_ID, ImmutableList.of(new ComplexTestStats(1, 2f, "3"))
    );

    UpdateMaterialStatsPacket packetToDecode = sendAndReceivePacket(materialToStats);
    assertThat(packetToDecode.materialToStats).hasSize(1);
    assertThat(packetToDecode.materialToStats).containsKey(MATERIAL_ID);
    assertThat(packetToDecode.materialToStats.get(MATERIAL_ID)).hasSize(1);

    BaseMaterialStats materialStats = packetToDecode.materialToStats.get(MATERIAL_ID).iterator().next();
    assertThat(materialStats).isExactlyInstanceOf(ComplexTestStats.class);
    ComplexTestStats realStats = (ComplexTestStats) materialStats;
    assertThat(realStats.getNum()).isEqualTo(1);
    assertThat(realStats.getFloating()).isEqualTo(2f);
    assertThat(realStats.getText()).isEqualTo("3");
  }

  @Test
  void testAllTicDefaults() {
    ImmutableList<? extends BaseMaterialStats> stats = ImmutableList.of(CommonMaterialStats.DEFAULT);
    Map<MaterialId, Collection<? extends BaseMaterialStats>> materialToStats = ImmutableMap.of(
      MATERIAL_ID, stats
    );

    UpdateMaterialStatsPacket packet = sendAndReceivePacket(materialToStats);

    assertThat(packet.materialToStats.get(MATERIAL_ID)).isEqualTo(stats);
  }

  private UpdateMaterialStatsPacket sendAndReceivePacket(Map<MaterialId, Collection<? extends BaseMaterialStats>> materialToStats) {
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

    UpdateMaterialStatsPacket packetToEncode = new UpdateMaterialStatsPacket(materialToStats);
    packetToEncode.encode(buffer);

    UpdateMaterialStatsPacket packetToDecode = new UpdateMaterialStatsPacket();
    packetToDecode.decode(buffer);
    return packetToDecode;
  }
}
