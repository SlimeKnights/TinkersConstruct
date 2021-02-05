package slimeknights.tconstruct.shared.block;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.IStringSerializable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.TinkerFood;

import java.util.Locale;

public class StickySlimeBlock extends net.minecraft.block.SlimeBlock {

  public StickySlimeBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean isStickyBlock(BlockState state) {
    return true;
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public enum SlimeType implements IStringSerializable {
    GREEN(0x01cd4e, TinkerTags.Items.GREEN_SLIMEBALL),
    BLUE(0x01cbcd, TinkerTags.Items.BLUE_SLIMEBALL),
    PURPLE(0xaf4cf6, TinkerTags.Items.PURPLE_SLIMEBALL),
    BLOOD(0xb50101, TinkerTags.Items.BLOOD_SLIMEBALL),
    MAGMA(0xff970d, TinkerTags.Items.MAGMA_SLIMEBALL);

    /**
     * Slime types added by the mod
     */
    public static final SlimeType[] TINKER = {BLUE, PURPLE, BLOOD, MAGMA};

    /**
     * Slime types from slimes
     */
    public static final SlimeType[] TRUE_SLIME = {GREEN, BLUE, PURPLE, MAGMA};

    /* Block color for this slime type */
    @Getter
    private final int color;
    private final ITag<Item> slimeBallTag;

    public ITag<Item> getSlimeBallTag() {
      return slimeBallTag;
    }

    /**
     * Returns the slimeball food item for this slime type
     * @param type  SlimeType
     * @return  Appropriate TinkerFood
     */
    public Food getSlimeFood(SlimeType type) {
      switch (type) {
        case BLUE: default:
          return TinkerFood.BLUE_SLIME_BALL;
        case PURPLE:
          return TinkerFood.PURPLE_SLIME_BALL;
        case BLOOD:
          return TinkerFood.BLOOD_SLIME_BALL;
        case MAGMA:
          return TinkerFood.MAGMA_SLIME_BALL;
      }
    }

    /**
     * Returns the slime_drop food item for this slime type
     * @param type  SlimeType
     * @return  Appropriate TinkerFood
     */
    public Food getSlimeDropFood(SlimeType type) {
      switch (type) {
        case GREEN: default:
          return TinkerFood.GREEN_SLIME_DROP;
        case BLUE:
          return TinkerFood.BLUE_SLIME_DROP;
        case PURPLE:
          return TinkerFood.PURPLE_SLIME_DROP;
        case BLOOD:
          return TinkerFood.BLOOD_SLIME_DROP;
        case MAGMA:
          return TinkerFood.MAGMA_SLIME_DROP;
      }
    }

    @Override
    public String getString() {
      return this.name().toLowerCase(Locale.US);
    }
  }
}
