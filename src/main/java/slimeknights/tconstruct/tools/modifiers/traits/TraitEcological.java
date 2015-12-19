package slimeknights.tconstruct.tools.modifiers.traits;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.ToolHelper;


// Your tool is green!
// This is a very weak version of repair/moss.. be careful not to catch any splinters!
public class TraitEcological extends AbstractTrait {

  private static int chance = 100; // 1/X chance of getting the effect

  public TraitEcological() {
    super("ecological", EnumChatFormatting.GREEN);
  }

  @Override
  public void onUpdate(ItemStack tool, World world, Entity entity, int itemSlot, boolean isSelected) {
    // *20 because 20 ticks in a second
    if(random.nextInt(20 * chance) == 0) {
      ToolHelper.repairTool(tool, 1);
    }
  }

  @Override
  public void afterBlockBreak(ItemStack tool, World world, Block block, BlockPos pos, EntityLivingBase player, boolean wasEffective) {
    splinter(player);
  }

  @Override
  public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
    splinter(player);
  }

  private void splinter(EntityLivingBase player) {
    // SPLINTERS!
    if(!player.worldObj.isRemote && random.nextInt(chance) == 0) {
      player.attackEntityFrom(DamageSource.cactus, 0.1f);
    }
  }
}
