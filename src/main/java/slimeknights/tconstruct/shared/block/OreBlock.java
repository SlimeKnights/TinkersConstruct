package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.common.ToolType;
import slimeknights.tconstruct.library.utils.HarvestLevels;

import javax.annotation.Nullable;

public class OreBlock extends Block {

  public OreBlock() {
    this(Material.ROCK);
  }

  public OreBlock(Material material) {
    super(Block.Properties.create(material).hardnessAndResistance(1.0F));
  }

  @Override
  public BlockRenderLayer getRenderLayer() {
    return BlockRenderLayer.CUTOUT_MIPPED;
  }

  @Nullable
  @Override
  //TODO: Replace when forge Re-Evaluates
  public net.minecraftforge.common.ToolType getHarvestTool(BlockState state) {
    return ToolType.PICKAXE;
  }

  @Override
  //TODO: Replace when forge Re-Evaluates
  public int getHarvestLevel(BlockState state) {
    return 1;
  }
}
