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

/** Variants of slimy foliage, for grass and leaves notably. */
@Getter
public enum FoliageType implements StringRepresentable {
  EARTH(0x8CD782, Tiers.STONE,   MapColor.GRASS, false),
  SKY  (0x00F4DA, Tiers.GOLD,    MapColor.DIAMOND, false),
  ICHOR(0xd09800, Tiers.IRON,    MapColor.COLOR_ORANGE, true),
  ENDER(0xa92dff, Tiers.DIAMOND, MapColor.COLOR_PURPLE, false),
  BLOOD(0xb80000, Tiers.WOOD,    MapColor.COLOR_RED, true);

  /** Foliage types fully implemented in game */
  public static final FoliageType[] VISIBLE = {EARTH, SKY, BLOOD, ENDER};
  /** Foliage types using overworld style (grass, wood) */
  public static final FoliageType[] OVERWORLD = {EARTH, SKY};
  /** Folage types using nether style (nylium, fungus) */
  public static final FoliageType[] NETHER = {ICHOR, BLOOD};

  /* Block color for this slime type */
  private final int color;
  /** Tier needed to harvest dirt blocks of this type */
  private final Tiers harvestTier;
  /** Color for this block on maps */
  private final MapColor mapColor;
  /** If true, this block type has fungus foliage instead of grass */
  private final boolean nether;
  @Getter
  private final String serializedName = this.name().toLowerCase(Locale.ROOT);

  /* Tags */
  /** Tag for grass blocks with this foliage type */
  private final TagKey<Block> grassBlockTag;

  FoliageType(int color, Tiers harvestTier, MapColor mapColor, boolean nether) {
    this.color = color;
    this.harvestTier = harvestTier;
    this.mapColor = mapColor;
    this.nether = nether;
    // tags
    grassBlockTag = TinkerTags.Blocks.tag((nether ? "slimy_nylium/" : "slimy_grass/") + this.getSerializedName());
  }

  private SlimeType slimeType;

  /** Gets the slime type for this dirt type */
  @Nullable
  public SlimeType asSlime() {
    if (slimeType == null && this != BLOOD) {
      slimeType = SlimeType.values()[this.ordinal()];
    }
    return slimeType;
  }
}
