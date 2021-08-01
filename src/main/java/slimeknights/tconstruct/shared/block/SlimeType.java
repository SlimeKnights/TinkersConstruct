package slimeknights.tconstruct.shared.block;

import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.Tags.IOptionalNamedTag;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.Locale;

@Getter
public enum SlimeType implements IStringSerializable {
  EARTH(0x01cd4e, 0x8CD782, false),
  SKY(0x01cbcd, 0x00F4DA, false),
  ICHOR(0xff970d, 0xd09800, true, 10),
  ENDER(0xaf4cf6, 0xa92dff, false),
  BLOOD(0xb50101, 0xb80000, true);

  /** Slime types added by the mod */
  public static final SlimeType[] TINKER = {SKY, ENDER, BLOOD, ICHOR};
  /** Slime types from slimes */
  public static final SlimeType[] TRUE_SLIME = {EARTH, SKY, ENDER, ICHOR};
  /** Slime types that flow downwards, ichor flows up */
  public static final SlimeType[] LIQUID = {EARTH, SKY, BLOOD, ENDER};
  /** Slime types that use overworld foliage */
  public static final SlimeType[] OVERWORLD = {EARTH, SKY, ENDER};
  /** Slime types that use overworld foliage */
  public static final SlimeType[] NETHER = {ICHOR, BLOOD};

  /* Block color for this slime type */
  private final int color;
  /** Default color for this foliage, used in inventory */
  private final int defaultFoliageColor;
  /** If true, this block type has fungus foliage instead of grass */
  private final boolean nether;
  /** Light level of slime blocks of this type */
  private final int lightLevel;

  /* Tags */
  /** Tag for dirt blocks of this type, including blocks with grass on top */
  private final IOptionalNamedTag<Block> dirtBlockTag;
  /** Tag for grass blocks with this foliage type */
  private final IOptionalNamedTag<Block> grassBlockTag;
  /** Tag for slime balls of this type */
  private final IOptionalNamedTag<Item> slimeballTag;

  SlimeType(int color, int defaultFoliageColor, boolean nether, int lightLevel) {
    this.color = color;
    this.defaultFoliageColor = defaultFoliageColor;
    this.nether = nether;
    this.lightLevel = lightLevel;
    // tags
    String name = this.getString();
    grassBlockTag = TinkerTags.Blocks.tag((nether ? "slimy_nylium/" : "slimy_grass/") + name);
    dirtBlockTag = TinkerTags.Blocks.tag("slimy_soil/" + ("blood".equals(name) ? "vanilla" : name));
    slimeballTag = TinkerTags.Items.forgeTag("slimeball/" + name);
  }

  SlimeType(int color, int defaultFoliageColor, boolean nether) {
    this(color, defaultFoliageColor, nether, 0);
  }

  @Override
  public String getString() {
    return this.name().toLowerCase(Locale.US);
  }
}
