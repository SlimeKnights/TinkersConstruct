package slimeknights.tconstruct.world.block;

import lombok.Getter;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.block.SlimeType;

import javax.annotation.Nullable;
import java.util.Locale;

/** Variants of slimy dirt */
@Getter
public enum DirtType implements StringRepresentable {
  EARTH  (Tiers.STONE,   MapColor.GRASS),
  SKY    (Tiers.GOLD,    MapColor.WARPED_STEM),
  ICHOR  (Tiers.IRON,    MapColor.TERRACOTTA_LIGHT_BLUE),
  ENDER  (Tiers.DIAMOND, MapColor.TERRACOTTA_ORANGE),
  VANILLA(Tiers.WOOD,    MapColor.DIRT);

  /** Dirt types added by the mod */
  public static final DirtType[] TINKER = {EARTH, SKY, ICHOR, ENDER};

  /** Tier needed to harvest dirt blocks of this type */
  private final Tiers harvestTier;
  /** Color for this block on maps */
  private final MapColor mapColor;
  @Getter
  private final String serializedName = this.name().toLowerCase(Locale.ROOT);

  /* Tags */
  /** Tag for dirt blocks of this type, including blocks with grass on top */
  private final TagKey<Block> blockTag;

  DirtType(Tiers harvestTier, MapColor mapColor) {
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
