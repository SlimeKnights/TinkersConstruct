package slimeknights.tconstruct.shared.block;

import net.minecraft.block.BlockState;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;
import net.minecraft.util.IStringSerializable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.shared.TinkerFood;

import java.util.Locale;

public class SlimeBlock extends net.minecraft.block.SlimeBlock {

  public SlimeBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean isStickyBlock(BlockState state) {
    return true;
  }

  public enum SlimeType implements IStringSerializable {
    GREEN(0x01cd4e, 0x69bc5e, TinkerTags.Items.GREEN_SLIMEBALL),
    BLUE(0x01cbcd, 0x74c5c8, TinkerTags.Items.BLUE_SLIMEBALL),
    PURPLE(0xaf4cf6, 0xcc68ff, TinkerTags.Items.PURPLE_SLIMEBALL),
    BLOOD(0xb50101, 0xb80000, TinkerTags.Items.BLOOD_SLIMEBALL),
    MAGMA(0xff970d, 0xffab49, TinkerTags.Items.MAGMA_SLIMEBALL);

    SlimeType(int color, int ballColor, Tag<Item> slimeBall) {
      this.meta = this.ordinal();
      this.color = color;
      this.ballColor = ballColor;
      this.slimeBallTag = slimeBall;
    }

    public final int meta;
    private final int color, ballColor;
    private final Tag<Item> slimeBallTag;
    /**
     * Slime types added by the mod
     */
    public static final SlimeType[] TINKER = {BLUE, PURPLE, BLOOD, MAGMA};

    /**
     * Returns the block color for this slime type
     */
    public int getColor() {
      return this.color;
    }

    /**
     * Returns the slimeball color for this slime type, usually it is less saturated
     */
    public int getBallColor() {
      return this.ballColor;
    }

    public Tag<Item> getSlimeBallTag() {
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
    public String getName() {
      return this.name().toLowerCase(Locale.US);
    }
  }
}
