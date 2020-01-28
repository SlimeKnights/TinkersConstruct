package slimeknights.tconstruct.library.network;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UpdateMaterialStatsPacket implements ITinkerPacket {

  public static final Logger log = Util.getLogger("NetworkSync");

  @VisibleForTesting
  protected Map<MaterialId, Collection<BaseMaterialStats>> materialToStats;

  public UpdateMaterialStatsPacket() {
  }

  public UpdateMaterialStatsPacket(Map<MaterialId, Collection<BaseMaterialStats>> materialToStatsPerType) {
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
      int fieldCount = buffer.readInt();
      for (int i = 0; i < fieldCount; i++) {
        readStat(stats, clazz, buffer);
      }
      return Optional.of(stats);
    } catch (Exception e) {
      log.error("Could not load class {} for deserialization of stats", className, e);
      return Optional.empty();
    }
  }

  private void readStat(BaseMaterialStats stats, Class<?> clazz, PacketBuffer buffer) {
    String fieldName = buffer.readString();
    if(fieldName.isEmpty()) {
      // stat couldn't be serialized
      return;
    }
    try {
      Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      Class<?> type = field.getType();

      if (isInteger(type)) {
        field.set(stats, buffer.readInt());
      } else if (isFloat(type)) {
        field.set(stats, buffer.readFloat());
      } else if (isDouble(type)) {
        field.set(stats, buffer.readDouble());
      } else {
        log.warn("Field '{}' of stats {} is not supported for automatic sync. " +
            "Only int and float are supported. If you need something else contact us",
          fieldName,
          clazz);
        buffer.readString();
      }
    } catch (IllegalAccessException | NoSuchFieldException e) {
      log.error("Error while trying to deserialize '{}' of stats {}", fieldName, clazz, e);
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

    // we currently do not support sending custom fields, since int/float should suffice for all stats.
    // if you need something else, contact us.
    if (clazz.getSuperclass() != BaseMaterialStats.class) {
      log.warn("Class {} is not directly derived from BaseMaterialStats. " +
          "Automatic sync from server to client might not work. " +
          "This means the stats might lead to unexpected results on the client.",
        clazz.getCanonicalName());
    }

    List<Field> writableStats = Arrays.stream(clazz.getDeclaredFields())
//      .filter(AccessibleObject::isAccessible)
      .collect(Collectors.toList());
    buffer.writeInt(writableStats.size());
    writableStats.forEach(field -> writeField(stat, field, buffer));
  }

  private void writeField(BaseMaterialStats stats, Field field, PacketBuffer buffer) {
    try {
      buffer.writeString(field.getName());
      Class<?> type = field.getType();
      field.setAccessible(true);
      if (isInteger(type)) {
        buffer.writeInt((int)field.get(stats));
      } else if (isFloat(type)) {
        buffer.writeFloat((float)field.get(stats));
      } else if (isDouble(type)) {
        buffer.writeDouble((double) field.get(stats));
      } else {
        log.warn("Field '{}' of stats {} is not supported for automatic sync. " +
            "Only int and float are supported. If you need something else contact us",
          field.getName(),
          stats.getClass());
        buffer.writeString("");
      }
    } catch (IllegalAccessException e) {
      log.error("Error while trying to serialize field '{}' of stats {}", field.getName(), stats.getClass(), e);
    }
  }

  private boolean isInteger(Class<?> type) {
    return Integer.class.isAssignableFrom(type) || int.class.isAssignableFrom(type);
  }

  private boolean isFloat(Class<?> type) {
    return Float.class.isAssignableFrom(type) || float.class.isAssignableFrom(type);
  }

  private boolean isDouble(Class<?> type) {
    return Double.class.isAssignableFrom(type) || double.class.isAssignableFrom(type);
  }

  @Override
  public void handle(NetworkEvent.Context context) {
    // todo: handle new stats
    log.info("GOT NEW STATS");
  }
}
