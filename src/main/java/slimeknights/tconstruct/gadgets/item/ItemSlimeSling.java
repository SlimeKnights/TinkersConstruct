package slimeknights.tconstruct.gadgets.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.shared.TinkerCommons;

public class ItemSlimeSling extends Item {

  public ItemSlimeSling() {
    this.setMaxStackSize(1);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
  }

  /**
   * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
   */
  public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
  {
    playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
    return itemStackIn;
  }

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
  public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
    // has to be on ground to do something
    if(!player.onGround || !(player instanceof EntityPlayerMP)) {
      return;
    }

    EntityPlayerMP playerMP = (EntityPlayerMP) player;

    // copy chargeup code from bow \o/
    int i = this.getMaxItemUseDuration(stack) - timeLeft;
    float f = (float)i / 20.0F;
    f = (f * f + f * 2.0F) / 3.0F;
    f *= 4f;

    if(f > 6f) {
      f = 6f;
    }

    // check if player was targeting a block
    MovingObjectPosition mop = getMovingObjectPositionFromPlayer(world, player, false);

    if(mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
      // we fling the inverted player look vector
      Vec3 vec = player.getLookVec().normalize();

      player.addVelocity(vec.xCoord * -f,
                         vec.yCoord * -f/3f,
                         vec.zCoord * -f);
      playerMP.playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(player));
      TinkerCommons.potionSlimeBounce.apply(player);
    }
  }
}
