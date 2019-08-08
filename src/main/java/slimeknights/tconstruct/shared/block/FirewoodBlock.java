package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class FirewoodBlock extends Block {

  public FirewoodBlock() {
    super(Block.Properties.create(Material.WOOD).hardnessAndResistance(2.0F, 7.0F).sound(SoundType.WOOD).lightValue(7));
  }

  @Nullable
  @Override
  //TODO: Replace when forge Re-Evaluates
  public net.minecraftforge.common.ToolType getHarvestTool(BlockState state) {
    return ToolType.AXE;
  }

  @Override
  //TODO: Replace when forge Re-Evaluates
  public int getHarvestLevel(BlockState state) {
    return -1;
  }
}
