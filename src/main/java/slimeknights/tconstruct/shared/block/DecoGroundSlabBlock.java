package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class DecoGroundSlabBlock extends SlabBlock {

  public DecoGroundSlabBlock() {
    super(Block.Properties.create(Material.ROCK).sound(SoundType.GROUND).hardnessAndResistance(2.0F));
  }

  @Nullable
  @Override
  //TODO: Replace when forge Re-Evaluates
  public net.minecraftforge.common.ToolType getHarvestTool(BlockState state) {
    return ToolType.SHOVEL;
  }

  @Override
  //TODO: Replace when forge Re-Evaluates
  public int getHarvestLevel(BlockState state) {
    return -1;
  }
}
