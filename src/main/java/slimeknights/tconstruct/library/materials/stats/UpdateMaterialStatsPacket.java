package slimeknights.tconstruct.library.materials.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent.Context;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.network.packet.IThreadsafePacket;
import slimeknights.mantle.util.typed.TypedMapBuilder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.utils.Util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class UpdateMaterialStatsPacket implements IThreadsafePacket {
  private static final Logger log = Util.getLogger("NetworkSync");

  protected final Map<MaterialId, Collection<IMaterialStats>> materialToStats;

  public UpdateMaterialStatsPacket(FriendlyByteBuf buffer) {
    this(buffer, MaterialRegistry.getInstance().getStatTypeLoader());
  }

  public UpdateMaterialStatsPacket(FriendlyByteBuf buffer, Loadable<MaterialStatType<?>> statTypeLoader) {
    int materialCount = buffer.readInt();
    materialToStats = new HashMap<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      int statCount = buffer.readInt();
      List<IMaterialStats> statList = new ArrayList<>();
      for (int j = 0; j < statCount; j++) {
        ResourceLocation statId = null;
        try {
          MaterialStatType<?> statType = statTypeLoader.decode(buffer);
          statId = statType.getId();
          statList.add(statType.getLoadable().decode(buffer, TypedMapBuilder.builder().put(MaterialStatType.CONTEXT_KEY, statType).build()));
        } catch (RuntimeException e) {
          log.error("Could not deserialize stat {} for material {}. Are client and server in sync?", statId, id, e);
          throw e;
        }
      }
      materialToStats.put(id, statList);
    }
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(materialToStats.size());
    materialToStats.forEach((materialId, stats) -> {
      buffer.writeResourceLocation(materialId);
      buffer.writeInt(stats.size());
      for (IMaterialStats stat : stats) {
        encodeStat(buffer, stat, stat.getType(), materialId);
      }
    });
  }

  /**
   * Encodes a single material stat
   *
   * @param buffer     Buffer instance
   * @param stat       Stat to encode
   * @param material   Material being encoded
   */
  @SuppressWarnings("unchecked")
  private <T extends IMaterialStats> void encodeStat(FriendlyByteBuf buffer, IMaterialStats stat, MaterialStatType<T> type, MaterialId material) {
    try {
      MaterialStatsId.PARSER.encode(buffer, type.getId());
      type.getLoadable().encode(buffer, (T) stat);
    } catch (RuntimeException e) {
      TConstruct.LOG.error("Could not encode stat {} for material {}", stat.getIdentifier(), material, e);
      throw e;
    }
  }

  @Override
  public void handleThreadsafe(Context context) {
    MaterialRegistry.updateMaterialStatsFromServer(this);
  }
}
