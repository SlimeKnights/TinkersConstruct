package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.utils.ToolHelper;

public abstract class AoeToolCore extends TinkerToolCore implements IAoeTool {

  public AoeToolCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.AOE);
  }

  @Override
  public ImmutableList<BlockPos> getAOEBlocks(ItemStack stack, World world, EntityPlayer player, BlockPos origin) {
    return ToolHelper.calcAOEBlocks(stack, world, player, origin, 1, 1, 1);
  }

  @Override
  public boolean isAoeHarvestTool() {
    return true;
  }
}
