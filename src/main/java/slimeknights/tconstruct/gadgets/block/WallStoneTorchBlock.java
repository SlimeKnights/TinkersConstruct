package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.WallTorchBlock;
import net.minecraft.block.material.Material;

public class WallStoneTorchBlock extends WallTorchBlock {

  public WallStoneTorchBlock() {
    super(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0F).lightValue(14).sound(SoundType.STONE));
  }
}
