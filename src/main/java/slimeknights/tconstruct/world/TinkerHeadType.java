package slimeknights.tconstruct.world;

import lombok.RequiredArgsConstructor;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.SkullBlock.Type;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Supplier;

/** Enum representing all heads provided by Tinkers */
@RequiredArgsConstructor
public enum TinkerHeadType implements Type, StringRepresentable {
  BLAZE(() -> EntityType.BLAZE),
  ENDERMAN(() -> EntityType.ENDERMAN),
  STRAY(() -> EntityType.STRAY),
  // zombies
  HUSK(() -> EntityType.HUSK),
  DROWNED(() -> EntityType.DROWNED),
  // spider
  SPIDER(() -> EntityType.SPIDER),
  CAVE_SPIDER(() -> EntityType.CAVE_SPIDER),
  // piglin
  PIGLIN(() -> EntityType.PIGLIN),
  PIGLIN_BRUTE(() -> EntityType.PIGLIN_BRUTE),
  ZOMBIFIED_PIGLIN(() -> EntityType.ZOMBIFIED_PIGLIN);

  private final Supplier<EntityType<?>> type;

  /** Gets the associated entity type */
  public EntityType<?> getType() {
    return type.get();
  }

  @Override
  public String getSerializedName() {
    return this.name().toLowerCase(Locale.ROOT);
  }

  /**
   * Gets the head type for the given entity type
   * @param type  Entity type
   * @return  Head type
   */
  @Nullable
  public static TinkerHeadType fromEntityType(EntityType<?> type) {
    for (TinkerHeadType headType : values()) {
      if (headType.getType() == type) {
        return headType;
      }
    }
    return null;
  }
}
