package slimeknights.tconstruct.library.network;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.NetworkTestStats;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.Collection;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateMaterialStatsPacketTest extends BaseMcTest {

  @Test
  void testGenericEncodeDecode() {

    MaterialId materialId = MaterialFixture.MATERIAL_1.getIdentifier();
    Map<MaterialId, Collection<BaseMaterialStats>> materialToStats = ImmutableMap.of(
      materialId, ImmutableList.of(new NetworkTestStats(1, 2, 3f, 4f, 5d, 6d, "7"))
    );
    PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());

    UpdateMaterialStatsPacket packetToEncode = new UpdateMaterialStatsPacket(materialToStats);
    packetToEncode.encode(buffer);

    UpdateMaterialStatsPacket packetToDecode = new UpdateMaterialStatsPacket();
    packetToDecode.decode(buffer);

    assertThat(packetToDecode.materialToStats).hasSize(1);
    assertThat(packetToDecode.materialToStats).containsKey(materialId);
    assertThat(packetToDecode.materialToStats.get(materialId)).hasSize(1);

    BaseMaterialStats materialStats = packetToDecode.materialToStats.get(materialId).iterator().next();
    assertThat(materialStats).isExactlyInstanceOf(NetworkTestStats.class);
    NetworkTestStats realStats = (NetworkTestStats) materialStats;
    assertThat(realStats.getNum1()).isEqualTo(1);
    assertThat(realStats.getNum2()).isEqualTo(2);
    assertThat(realStats.getFloating1()).isEqualTo(3f);
    assertThat(realStats.getFloating2()).isEqualTo(4f);
    assertThat(realStats.getDouble1()).isEqualTo(5d);
    assertThat(realStats.getDouble2()).isEqualTo(6d);
    assertThat(realStats.getText()).isNullOrEmpty();
  }
}
