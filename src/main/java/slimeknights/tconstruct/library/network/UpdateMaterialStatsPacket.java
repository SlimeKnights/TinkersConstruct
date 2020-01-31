package slimeknights.tconstruct.library.network;

import com.google.common.annotations.VisibleForTesting;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMaterialStatsPacket {

  public static final Logger log = Util.getLogger("NetworkSync");

  @VisibleForTesting
  protected Map<MaterialId, Collection<BaseMaterialStats>> materialToStats;

  public UpdateMaterialStatsPacket(PacketBuffer buffer, Function<MaterialStatsId, Class<?>> classResolver) {
    decode(buffer, classResolver);
  }

  public void decode(PacketBuffer buffer, Function<MaterialStatsId, Class<?>> classResolver) {
    int materialCount = buffer.readInt();
    materialToStats = new HashMap<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      int statCount = buffer.readInt();
      List<BaseMaterialStats> statList = new ArrayList<>();
      for (int j = 0; j < statCount; j++) {
        decodeStat(buffer, classResolver).ifPresent(statList::add);
      }
      materialToStats.put(id, statList);
    }
  }

  private Optional<BaseMaterialStats> decodeStat(PacketBuffer buffer, Function<MaterialStatsId, Class<?>> classResolver) {
    MaterialStatsId statsId = new MaterialStatsId(buffer.readResourceLocation());
    try {
      Class<?> clazz = classResolver.apply(statsId);
      BaseMaterialStats stats = (BaseMaterialStats) clazz.newInstance();
      stats.decode(buffer);
      return Optional.of(stats);
    } catch (Exception e) {
      log.error("Could not load class for deserialization of stats {}. Are client and server in sync?", statsId, e);
      return Optional.empty();
    }
  }

  public void encode(PacketBuffer buffer) {
    buffer.writeInt(materialToStats.size());
    materialToStats.forEach((materialId, stats) -> {
      buffer.writeResourceLocation(materialId);
      buffer.writeInt(stats.size());
      stats.forEach(stat -> encodeStat(buffer, stat));
    });
  }

  private void encodeStat(PacketBuffer buffer, BaseMaterialStats stat) {
    buffer.writeResourceLocation(stat.getIdentifier());
    stat.encode(buffer);
  }
}
