package slimeknights.tconstruct.shared.block;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.IStringSerializable;
import slimeknights.tconstruct.common.TinkerTags;

import java.util.Locale;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SlimeType implements IStringSerializable {
  EARTH(0x01cd4e, 0x8CD782, TinkerTags.Items.EARTH_SLIMEBALL),
  SKY(0x01cbcd, 0x00F4DA, TinkerTags.Items.SKY_SLIMEBALL),
  ICHOR(0xff970d, 0xd09800, TinkerTags.Items.ICHOR_SLIMEBALL),
  ENDER(0xaf4cf6, 0xa92dff, TinkerTags.Items.ENDER_SLIMEBALL),
  BLOOD(0xb50101, 0xb80000, TinkerTags.Items.BLOOD_SLIMEBALL);

  /**
   * Slime types added by the mod
   */
  public static final SlimeType[] TINKER = {SKY, ENDER, BLOOD, ICHOR};

  /**
   * Slime types from slimes
   */
  public static final SlimeType[] TRUE_SLIME = {EARTH, SKY, ENDER, ICHOR};

  /** Slime types that flow downwards, ichor flows up */
  public static final SlimeType[] LIQUID = {EARTH, SKY, BLOOD, ENDER};

  /* Block color for this slime type */
  @Getter
  private final int color;
  @Getter
  private final int defaultFoliageColor;
  @Getter
  private final ITag<Item> slimeBallTag;

  @Override
  public String getString() {
    return this.name().toLowerCase(Locale.US);
  }
}
