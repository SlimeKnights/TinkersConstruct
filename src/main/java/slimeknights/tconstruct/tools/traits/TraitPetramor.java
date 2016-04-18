package slimeknights.tconstruct.tools.traits;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitPetramor extends AbstractTrait {

  private static final float chance = 0.1f;

  public TraitPetramor() {
    super("petramor", TextFormatting.RED);
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, IBlockState state, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    if(!world.isRemote && state.getMaterial() == Material.ROCK && random.nextFloat() < chance) {
      ToolHelper.healTool(tool, 5, player);
    }
  }
}
