package slimeknights.tconstruct.world.block;

import lombok.Getter;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MaterialColor;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.block.SlimeType;

import javax.annotation.Nullable;
import java.util.Locale;

/** Variants of slimy dirt */
@Getter
public enum DirtType implements StringRepresentable {
  EARTH  (Tiers.STONE,   MaterialColor.GRASS),
  SKY    (Tiers.GOLD,    MaterialColor.WARPED_STEM),
  ICHOR  (Tiers.IRON,    MaterialColor.TERRACOTTA_LIGHT_BLUE),
  ENDER  (Tiers.DIAMOND, MaterialColor.TERRACOTTA_ORANGE),
  VANILLA(Tiers.WOOD,    MaterialColor.DIRT);

  /** Dirt types added by the mod */
  public static final DirtType[] TINKER = {EARTH, SKY, ICHOR, ENDER};

  /** Tier needed to harvest dirt blocks of this type */
  private final Tiers harvestTier;
  /** Color for this block on maps */
  private final MaterialColor mapColor;
  @Getter
  private final String serializedName = this.name().toLowerCase(Locale.ROOT);

  /* Tags */
  /** Tag for dirt blocks of this type, including blocks with grass on top */
  private final TagKey<Block> blockTag;

  DirtType(Tiers harvestTier, MaterialColor mapColor) {
    this.harvestTier = harvestTier;
    this.mapColor = mapColor;
    this.blockTag = TinkerTags.Blocks.tag("slimy_soil/" + this.getSerializedName());
  }

  private SlimeType slimeType;

  /** Gets the slime type for this dirt type */
  @Nullable
  public SlimeType asSlime() {
    if (slimeType == null && this != VANILLA) {
      slimeType = SlimeType.values()[this.ordinal()];
    }
    return slimeType;
  }
}
