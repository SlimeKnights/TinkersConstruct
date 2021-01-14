package slimeknights.tconstruct.gadgets.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import slimeknights.tconstruct.gadgets.entity.ThrowableItem;
import slimeknights.tconstruct.gadgets.entity.shuriken.FlintShurikenEntity;

public class FlintShurikenItem extends ThrowableItem {

  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
    ItemStack itemStack = playerIn.getHeldItem(handIn);
    if (!playerIn.abilities.isCreativeMode) {
      itemStack.shrink(1);
    }

    playerIn.getCooldownTracker().setCooldown(itemStack.getItem(), 4);

    if(!worldIn.isRemote) {
      FlintShurikenEntity entity = new FlintShurikenEntity(worldIn, playerIn);
      entity.setItem(itemStack);
      entity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
      worldIn.addEntity(entity);
    }

    playerIn.addStat(Stats.ITEM_USED.get(this));
    return new ActionResult<>(ActionResultType.SUCCESS, itemStack);
  }
}
