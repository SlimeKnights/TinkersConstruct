package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class SlimeBlock extends net.minecraft.block.SlimeBlock {

  private final SlimeType slimeType;

  public SlimeBlock(SlimeType slimeType) {
    super(Block.Properties.create(Material.CLAY, MaterialColor.GRASS).slipperiness(0.8F).sound(SoundType.SLIME));

    this.slimeType = slimeType;
  }

  @Override
  public boolean isStickyBlock(BlockState state) {
    return true;
  }

  public enum SlimeType {
    GREEN(0x01cd4e, 0x69bc5e),
    BLUE(0x01cbcd, 0x74c5c8),
    PURPLE(0xaf4cf6, 0xcc68ff),
    BLOOD(0xb50101, 0xb80000),
    MAGMA(0xff970d, 0xffab49);

    SlimeType(int color, int ballColor) {
      this.meta = this.ordinal();
      this.color = color;
      this.ballColor = ballColor;
    }

    public final int meta;
    private final int color, ballColor;

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
  }
}
