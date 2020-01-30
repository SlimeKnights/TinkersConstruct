package slimeknights.tconstruct.library.network;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UpdateMaterialStatsPacket implements ITinkerPacket {

  public static final Logger log = Util.getLogger("NetworkSync");

  @VisibleForTesting
  protected Map<MaterialId, Collection<? extends BaseMaterialStats>> materialToStats;

  public UpdateMaterialStatsPacket() {
  }

  public UpdateMaterialStatsPacket(Map<MaterialId, Collection<? extends BaseMaterialStats>> materialToStatsPerType) {
    this.materialToStats = materialToStatsPerType;
  }

  public UpdateMaterialStatsPacket(PacketBuffer buffer) {
    decode(buffer);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    int materialCount = buffer.readInt();
    materialToStats = new HashMap<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      int statCount = buffer.readInt();
      List<BaseMaterialStats> statList = new ArrayList<>();
      for (int j = 0; j < statCount; j++) {
        decodeStat(buffer).ifPresent(statList::add);
      }
      materialToStats.put(id, statList);
    }
  }

  private Optional<BaseMaterialStats> decodeStat(PacketBuffer buffer) {
    //MaterialStatsId statsId = new MaterialStatsId(buffer.readResourceLocation());
    String className = buffer.readString();
    try {
      Class<?> clazz = buffer.getClass().getClassLoader().loadClass(className);
      BaseMaterialStats stats = (BaseMaterialStats) clazz.newInstance();
      stats.decode(buffer);
      return Optional.of(stats);
    } catch (Exception e) {
      log.error("Could not load class {} for deserialization of stats", className, e);
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

  private void encodeStat(PacketBuffer buffer, BaseMaterialStats stat) {
    Class<? extends BaseMaterialStats> clazz = stat.getClass();
    buffer.writeString(clazz.getCanonicalName());
    stat.encode(buffer);
  }

  @Override
  public void handle(NetworkEvent.Context context) {
    // todo: handle new stats
    log.info("GOT NEW STATS");
  }
}
