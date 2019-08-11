package slimeknights.tconstruct.shared.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class MetalBlock extends Block {

  private final MetalType metalType;

  public MetalBlock(MetalType metalType) {
    super(Block.Properties.create(Material.IRON).hardnessAndResistance(5.0F));
    this.metalType = metalType;
  }

  @Override
  public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
    //if(this.metalType == MetalType.ALUBRASS)
    super.fillItemGroup(group, items);
  }

  @Override
  public boolean isBeaconBase(BlockState state, IWorldReader world, BlockPos pos, BlockPos beacon) {
    return true;
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
    return -1;
  }

  public enum MetalType {
    COBALT,
    ARDITE,
    MANYULLYN,
    KNIGHTSLIME,
    PIGIRON,
    ALUBRASS,
    SILKY_JEWEL
  }
}
