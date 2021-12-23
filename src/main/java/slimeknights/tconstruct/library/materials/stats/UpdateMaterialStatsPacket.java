package slimeknights.tconstruct.library.materials.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.utils.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class UpdateMaterialStatsPacket implements IThreadsafePacket {
  private static final Logger log = Util.getLogger("NetworkSync");

  protected final Map<MaterialId, Collection<IMaterialStats>> materialToStats;

  public UpdateMaterialStatsPacket(PacketBuffer buffer) {
    this(buffer, MaterialRegistry::getClassForStat);
  }

  public UpdateMaterialStatsPacket(PacketBuffer buffer, Function<MaterialStatsId, Class<?>> classResolver) {
    int materialCount = buffer.readInt();
    materialToStats = new HashMap<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      int statCount = buffer.readInt();
      List<IMaterialStats> statList = new ArrayList<>();
      for (int j = 0; j < statCount; j++) {
        decodeStat(buffer, classResolver).ifPresent(statList::add);
      }
      materialToStats.put(id, statList);
    }
  }

  /**
   * Decodes a single stat
   * @param buffer         Buffer instance
   * @param classResolver  Stat to decode
   * @return  Optional of the decoded material stats
   */
  private Optional<IMaterialStats> decodeStat(PacketBuffer buffer, Function<MaterialStatsId, Class<?>> classResolver) {
    MaterialStatsId statsId = new MaterialStatsId(buffer.readResourceLocation());
    try {
      Class<?> clazz = classResolver.apply(statsId);
      if (clazz == null) {
        log.error("Unknown stat type {}. Are client and server in sync?", statsId);
        return Optional.empty();
      }
      IMaterialStats stats = (IMaterialStats) clazz.newInstance();
      stats.decode(buffer);
      return Optional.of(stats);
    } catch (Exception e) {
      log.error("Could not load class for deserialization of stats {}. Are client and server in sync?", statsId, e);
      return Optional.empty();
    }
  }

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(materialToStats.size());
    materialToStats.forEach((materialId, stats) -> {
      buffer.writeResourceLocation(materialId);
      buffer.writeInt(stats.size());
      stats.forEach(stat -> encodeStat(buffer, stat));
    });
  }

  /**
   * Encodes a single material stat
   * @param buffer  Buffer instance
   * @param stat    Stat to encode
   */
  private void encodeStat(PacketBuffer buffer, IMaterialStats stat) {
    buffer.writeResourceLocation(stat.getIdentifier());
    stat.encode(buffer);
  }

  @Override
  public void handleThreadsafe(Context context) {
    MaterialRegistry.updateMaterialStatsFromServer(this);
  }
}
