package slimeknights.tconstruct.gadgets.block;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;

public class StoneTorchBlock extends TorchBlock {

  public StoneTorchBlock() {
    super(Block.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().hardnessAndResistance(0.0F).lightValue(14).sound(SoundType.STONE));
  }
}
