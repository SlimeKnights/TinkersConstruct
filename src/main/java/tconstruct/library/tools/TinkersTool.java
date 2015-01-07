package tconstruct.library.tools;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.TinkersItem;

/**
 * Intermediate abstraction layer for all tools/melee weapons.
 * This class has all the callbacks for blocks and enemies so tools and weapons can share behaviour.
 */
public abstract class TinkersTool extends TinkersItem {
  @Override
  public float getDigSpeed(ItemStack itemstack, IBlockState state) {
    return ToolHelper.calcDigSpeed(itemstack, state);
  }

  @Override
  public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
    // deal damage
    return true;
  }
}
