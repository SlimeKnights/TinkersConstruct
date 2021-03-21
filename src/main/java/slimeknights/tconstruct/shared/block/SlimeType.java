package slimeknights.tconstruct.shared.block;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.IStringSerializable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.TinkerFood;

import java.util.Locale;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SlimeType implements IStringSerializable {
  EARTH(0x01cd4e, "green", TinkerTags.Items.EARTH_SLIMEBALL),
  SKY(0x01cbcd, "blue", TinkerTags.Items.SKY_SLIMEBALL),
  ICHOR(0xff970d, "magma", TinkerTags.Items.ICHOR_SLIMEBALL),
  ENDER(0xaf4cf6, "purple", TinkerTags.Items.ENDER_SLIMEBALL),
  BLOOD(0xb50101, "vanilla", TinkerTags.Items.BLOOD_SLIMEBALL); // blood is used for vanilla dirt to make logic simplier

  /**
   * Slime types added by the mod
   */
  public static final SlimeType[] TINKER = {SKY, ENDER, BLOOD, ICHOR};

  /**
   * Slime types from slimes
   */
  public static final SlimeType[] TRUE_SLIME = {EARTH, SKY, ENDER, ICHOR};

  /* Block color for this slime type */
  @Getter
  private final int color;
  @Getter @Deprecated
  private final String originalName;
  private final ITag<Item> slimeBallTag;

  public ITag<Item> getSlimeBallTag() {
    return slimeBallTag;
  }

  /**
   * Returns the slimeball food item for this slime type
   * @param type SlimeType
   * @return Appropriate TinkerFood
   */
  public Food getSlimeFood(SlimeType type) {
    switch (type) {
      case SKY:
      default:
        return TinkerFood.BLUE_SLIME_BALL;
      case ENDER:
        return TinkerFood.PURPLE_SLIME_BALL;
      case BLOOD:
        return TinkerFood.BLOOD_SLIME_BALL;
      case ICHOR:
        return TinkerFood.MAGMA_SLIME_BALL;
    }
  }

  /**
   * Returns the slime_drop food item for this slime type
   * @param type SlimeType
   * @return Appropriate TinkerFood
   */
  public Food getSlimeDropFood(SlimeType type) {
    switch (type) {
      case EARTH:
      default:
        return TinkerFood.GREEN_SLIME_DROP;
      case SKY:
        return TinkerFood.BLUE_SLIME_DROP;
      case ENDER:
        return TinkerFood.PURPLE_SLIME_DROP;
      case BLOOD:
        return TinkerFood.BLOOD_SLIME_DROP;
      case ICHOR:
        return TinkerFood.MAGMA_SLIME_DROP;
    }
  }

  @Override
  public String getString() {
    return this.name().toLowerCase(Locale.US);
  }
}
