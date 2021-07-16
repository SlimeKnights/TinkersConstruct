package slimeknights.tconstruct.shared.block;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.IStringSerializable;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.Locale;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SlimeType implements IStringSerializable {
  EARTH(0x01cd4e, 0x8CD782, TinkerTags.Items.EARTH_SLIMEBALL, false),
  SKY(0x01cbcd, 0x00F4DA, TinkerTags.Items.SKY_SLIMEBALL, false),
  ICHOR(0xff970d, 0xd09800, TinkerTags.Items.ICHOR_SLIMEBALL, true, 10),
  ENDER(0xaf4cf6, 0xa92dff, TinkerTags.Items.ENDER_SLIMEBALL, false),
  BLOOD(0xb50101, 0xb80000, TinkerTags.Items.BLOOD_SLIMEBALL, true);

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
  /** Tag for slime balls of this type */
  private final ITag<Item> slimeBallTag;
  /** If true, this block type has fungus foliage instead of grass */
  private final boolean nether;
  /** Light level of slime blocks of this type */
  private final int lightLevel;

  SlimeType(int color, int defaultFoliageColor, ITag<Item> slimeBallTag, boolean nether) {
    this(color, defaultFoliageColor, slimeBallTag, nether, 0);
  }

  @Override
  public String getString() {
    return this.name().toLowerCase(Locale.US);
  }
}
