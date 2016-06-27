package slimeknights.tconstruct.gadgets.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

import slimeknights.mantle.item.ItemTooltip;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.common.TinkerNetwork;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.tools.network.EntityMovementChangePacket;

public class ItemSlimeSling extends ItemTooltip {

  public ItemSlimeSling() {
    this.setMaxStackSize(1);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
    playerIn.setActiveHand(hand);
    return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
  }

  @Nonnull
  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.BOW;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 72000;
  }

  // sling logic

  @Override
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase entity, int timeLeft) {
    if(!(entity instanceof EntityPlayer)) {
      return;
    }
    EntityPlayer player = (EntityPlayer) entity;
    // has to be on ground to do something
    if(!player.onGround) {
      return;
    }

    // copy chargeup code from bow \o/
    int i = this.getMaxItemUseDuration(stack) - timeLeft;
    float f = (float) i / 20.0F;
    f = (f * f + f * 2.0F) / 3.0F;
    f *= 4f;

    if(f > 6f) {
      f = 6f;
    }

    // check if player was targeting a block
    RayTraceResult mop = rayTrace(world, player, false);

    if(mop != null && mop.typeOfHit == RayTraceResult.Type.BLOCK) {
      // we fling the inverted player look vector
      Vec3d vec = player.getLookVec().normalize();

      player.addVelocity(vec.xCoord * -f,
                         vec.yCoord * -f / 3f,
                         vec.zCoord * -f);

      if(player instanceof EntityPlayerMP) {
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        TinkerNetwork.sendTo(new EntityMovementChangePacket(player), playerMP);
        //playerMP.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
      }
      player.playSound(Sounds.slimesling, 1f, 1f);
      SlimeBounceHandler.addBounceHandler(player);
    }
  }
}
