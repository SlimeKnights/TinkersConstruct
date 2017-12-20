package slimeknights.tconstruct.gadgets.item;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityVelocity;
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
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.SlimeBounceHandler;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.utils.EntityUtil;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.network.EntityMovementChangePacket;

public class ItemSlimeSling extends ItemTooltip {

  public ItemSlimeSling() {
    this.setMaxStackSize(1);
    this.setCreativeTab(TinkerRegistry.tabGadgets);
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
    ItemStack itemStackIn = playerIn.getHeldItem(hand);
    playerIn.setActiveHand(hand);
    return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
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
    float f = i / 20.0F;
    f = (f * f + f * 2.0F) / 3.0F;
    f *= 4f;

    if(f > 6f) {
      f = 6f;
    }

    // check if player was targeting a block
    RayTraceResult mop = rayTrace(world, player, false);
    if (Config.slingOther) {
      Vec3d eye = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ); // Entity.getPositionEyes
      Vec3d look = player.getLook(1.0f);
      RayTraceResult mopEnt = EntityUtil.raytraceEntity(player, eye, look, 3.2f, true);

      if (mopEnt != null && mopEnt.typeOfHit == RayTraceResult.Type.ENTITY && mopEnt.entityHit instanceof EntityLivingBase)
        mop = mopEnt;
    }

    if(mop != null &&
            (mop.typeOfHit == RayTraceResult.Type.BLOCK ||
            (Config.slingOther && mop.typeOfHit == RayTraceResult.Type.ENTITY))) {
      // we fling the inverted player look vector
      Vec3d vec = player.getLookVec().normalize();

      if (mop.typeOfHit == RayTraceResult.Type.ENTITY) {
        float sizeFactor = (mop.entityHit.width * mop.entityHit.width * mop.entityHit.height)
                            / (entity.width * entity.width * entity.height);

        if (!world.isRemote) {
          mop.entityHit.addVelocity(vec.x * f / sizeFactor,
                  vec.y * f / sizeFactor,
                  vec.z * f / sizeFactor);
          TinkerTools.proxy.spawnAttackParticle(Particles.FRYPAN_ATTACK, player, 0.6d);
          if(mop.entityHit instanceof EntityPlayerMP) {
            TinkerNetwork.sendPacket(player, new SPacketEntityVelocity(mop.entityHit));
          }
        }

        f *= sizeFactor / (sizeFactor + 1);
      }

      player.addVelocity(vec.x * -f,
                         vec.y * -f / 3f,
                         vec.z * -f);

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
