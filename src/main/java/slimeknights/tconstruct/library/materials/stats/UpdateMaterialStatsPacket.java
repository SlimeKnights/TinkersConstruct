package slimeknights.tconstruct.library.materials.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
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

  public UpdateMaterialStatsPacket(FriendlyByteBuf buffer) {
    this(buffer, MaterialRegistry::getStatDecoder);
  }

  public UpdateMaterialStatsPacket(FriendlyByteBuf buffer, Function<MaterialStatsId, Function<FriendlyByteBuf,? extends IMaterialStats>> decoderResolver) {
    int materialCount = buffer.readInt();
    materialToStats = new HashMap<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      int statCount = buffer.readInt();
      List<IMaterialStats> statList = new ArrayList<>();
      for (int j = 0; j < statCount; j++) {
        decodeStat(buffer, decoderResolver).ifPresent(statList::add);
      }
      materialToStats.put(id, statList);
    }
  }

  /**
   * Decodes a single stat
   * @param buffer           Buffer instance
   * @param decoderResolver  Logic to decode stats
   * @return  Optional of the decoded material stats
   */
  private Optional<IMaterialStats> decodeStat(FriendlyByteBuf buffer, Function<MaterialStatsId,Function<FriendlyByteBuf,? extends IMaterialStats>> decoderResolver) {
    MaterialStatsId statsId = new MaterialStatsId(buffer.readResourceLocation());
    try {
      Function<FriendlyByteBuf,? extends IMaterialStats> decoder = decoderResolver.apply(statsId);
      if (decoder == null) {
        log.error("Unknown stat type {}. Are client and server in sync?", statsId);
        return Optional.empty();
      }
      return Optional.of(decoder.apply(buffer));
    } catch (Exception e) {
      log.error("Could not load class for deserialization of stats {}. Are client and server in sync?", statsId, e);
      return Optional.empty();
    }
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
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
  private void encodeStat(FriendlyByteBuf buffer, IMaterialStats stat) {
    buffer.writeResourceLocation(stat.getIdentifier());
    stat.encode(buffer);
  }

  @Override
  public void handleThreadsafe(Context context) {
    MaterialRegistry.updateMaterialStatsFromServer(this);
  }
}
