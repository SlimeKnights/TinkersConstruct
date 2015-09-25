package slimeknights.tconstruct.tools.modifiers;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;

public class TraitPetramor extends AbstractTrait {

  private static float chance = 0.1f;

  public TraitPetramor() {
    super("petramor", EnumChatFormatting.RED);
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    if(!world.isRemote && block.getMaterial() == Material.rock && random.nextFloat() < chance) {
      ToolHelper.healTool(tool, 5, player);
    }
  }
}
