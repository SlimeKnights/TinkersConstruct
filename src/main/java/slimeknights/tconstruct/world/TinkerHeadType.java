package slimeknights.tconstruct.world;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.level.block.SkullBlock.Type;
import net.minecraft.world.entity.EntityType;
import net.minecraft.util.StringRepresentable;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.function.Supplier;

/** Enum representing all heads provided by Tinkers */
@RequiredArgsConstructor
public enum TinkerHeadType implements Type, StringRepresentable {
  BLAZE(EntityType.BLAZE.delegate),
  ENDERMAN(EntityType.ENDERMAN.delegate),
  STRAY(EntityType.STRAY.delegate),
  // zombies
  HUSK(EntityType.HUSK.delegate),
  DROWNED(EntityType.DROWNED.delegate),
  // spider
  SPIDER(EntityType.SPIDER.delegate),
  CAVE_SPIDER(EntityType.CAVE_SPIDER.delegate),
  // piglin
  PIGLIN(EntityType.PIGLIN.delegate),
  PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE.delegate),
  ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN.delegate);

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
