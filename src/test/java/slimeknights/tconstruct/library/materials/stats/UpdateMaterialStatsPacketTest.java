package slimeknights.tconstruct.library.materials.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.MaterialStatsFixture;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.tools.stats.ExtraMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdateMaterialStatsPacketTest extends BaseMcTest {

  public static final MaterialId MATERIAL_ID = MaterialFixture.MATERIAL_1.getIdentifier();

  @Test
  void testGenericEncodeDecode() {
    Map<MaterialId, Collection<IMaterialStats>> materialToStats = ImmutableMap.of(
      MATERIAL_ID, ImmutableList.of(MaterialStatsFixture.MATERIAL_STATS)
    );

    UpdateMaterialStatsPacket packetToDecode = sendAndReceivePacket(materialToStats);
    assertThat(packetToDecode.materialToStats).hasSize(1);
    assertThat(packetToDecode.materialToStats).containsKey(MATERIAL_ID);
    assertThat(packetToDecode.materialToStats.get(MATERIAL_ID)).hasSize(1);

    IMaterialStats materialStats = packetToDecode.materialToStats.get(MATERIAL_ID).iterator().next();
    assertThat(materialStats).isExactlyInstanceOf(ComplexTestStats.class);
    ComplexTestStats realStats = (ComplexTestStats) materialStats;
    assertThat(realStats.getNum()).isEqualTo(1);
    assertThat(realStats.getFloating()).isEqualTo(2f);
    assertThat(realStats.getText()).isEqualTo("3");
  }

  @Test
  void testAllTicDefaults() {
    ImmutableList<IMaterialStats> stats = ImmutableList.of(
      HeadMaterialStats.DEFAULT,
      HandleMaterialStats.DEFAULT,
      ExtraMaterialStats.DEFAULT);
    Map<MaterialId, Collection<IMaterialStats>> materialToStats = ImmutableMap.of(
      MATERIAL_ID, stats
    );

    UpdateMaterialStatsPacket packet = sendAndReceivePacket(materialToStats);

    assertThat(packet.materialToStats.get(MATERIAL_ID)).isEqualTo(stats);
  }

  private UpdateMaterialStatsPacket sendAndReceivePacket(Map<MaterialId, Collection<IMaterialStats>> materialToStats) {
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    Function<MaterialStatsId, Class<?>> classResolverMock = createClassResolverMock(materialToStats);

    UpdateMaterialStatsPacket packetToEncode = new UpdateMaterialStatsPacket(materialToStats);
    packetToEncode.encode(buffer);

    return new UpdateMaterialStatsPacket(buffer, classResolverMock);
  }

  @SuppressWarnings("unchecked")
  private Function<MaterialStatsId, Class<?>> createClassResolverMock(Map<MaterialId, Collection<IMaterialStats>> materialToStats) {
    Function<MaterialStatsId, Class<?>> classResolverMock = mock(Function.class);
    materialToStats.values().stream()
      .flatMap(Collection::stream)
      .forEach(stat -> when(classResolverMock.apply(stat.getIdentifier())).thenReturn((Class) stat.getClass()));
    return classResolverMock;
  }
}
