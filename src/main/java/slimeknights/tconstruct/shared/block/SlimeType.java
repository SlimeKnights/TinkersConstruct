package slimeknights.tconstruct.shared.block;

import lombok.Getter;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.MapColor;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.world.block.DirtType;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.Locale;

/** Types of slime available in tinkers, all types notably have balls, congealed, and blocks */
@Getter
public enum SlimeType implements StringRepresentable {
  EARTH(0x01cd4e, MapColor.GRASS, false),
  SKY  (0x01cbcd, MapColor.DIAMOND, false),
  ICHOR(0xff970d, MapColor.COLOR_ORANGE, true, 10),
  ENDER(0xaf4cf6, MapColor.COLOR_PURPLE, false);

  /** Slime types added by the mod */
  public static final SlimeType[] TINKER = {SKY, ICHOR, ENDER};
  /** Slime types that flow downwards, ichor flows up */
  public static final SlimeType[] LIQUID = {EARTH, SKY, ENDER};
  /** Slime types that use overworld foliage */
  public static final SlimeType[] OVERWORLD = {EARTH, SKY, ENDER};
  /** Slime types that use nether foliage */
  public static final SlimeType[] NETHER = {ICHOR};

  /* Block color for this slime type */
  private final int color;
  /** Color for this block on maps */
  private final MapColor mapColor;
  /** If true, this block type has fungus foliage instead of grass */
  private final boolean nether;
  /** Light level of slime blocks of this type */
  private final int lightLevel;
  @Getter
  private final String serializedName = this.name().toLowerCase(Locale.ROOT);

  /* Tags */
  /** Tag for slime balls of this type */
  private final TagKey<Item> slimeballTag;

  SlimeType(int color,  MapColor mapColor, boolean nether, int lightLevel) {
    this.color = color;
    this.mapColor = mapColor;
    this.nether = nether;
    this.lightLevel = lightLevel;
    // tags
    slimeballTag = TinkerTags.Items.forgeTag("slimeball/" + this.getSerializedName());
  }

  SlimeType(int color, MapColor mapColor, boolean nether) {
    this(color, mapColor, nether, 0);
  }

  private FoliageType foliageType;
  private DirtType dirtType;

  /** Gets the foliage type for this slime type */
  public FoliageType asFoliage() {
    if (foliageType == null) {
      foliageType = FoliageType.values()[this.ordinal()];
    }
    return foliageType;
  }

  /** Gets the dirt type for this slime type */
  public DirtType asDirt() {
    if (dirtType == null) {
      dirtType = DirtType.values()[this.ordinal()];
    }
    return dirtType;
  }
}
