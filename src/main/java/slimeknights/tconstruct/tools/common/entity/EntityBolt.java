package slimeknights.tconstruct.tools.common.entity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import io.netty.buffer.ByteBuf;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;

public class EntityBolt extends EntityProjectileBase {

  public EntityBolt(World world) {
    super(world);
  }

  public EntityBolt(World world, double d, double d1, double d2) {
    super(world, d, d1, d2);
  }

  public EntityBolt(World world, EntityPlayer player, float speed, float inaccuracy, float power, ItemStack stack, ItemStack launchingStack) {
    super(world, player, speed, inaccuracy, power, stack, launchingStack);
  }

  @Override
  protected void onEntityHit(Entity entityHit) {
    super.onEntityHit(entityHit);
    if(!this.getEntityWorld().isRemote && entityHit instanceof EntityLivingBase) {
      EntityLivingBase entityLivingBaseHit = (EntityLivingBase) entityHit;
      entityLivingBaseHit.setArrowCountInEntity(entityLivingBaseHit.getArrowCountInEntity() + 1);
    }
  }

  @Override
  protected void playHitBlockSound(float speed, IBlockState state) {
    this.playSound(SoundEvents.ENTITY_ARROW_HIT, 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
  }

  @Override
  public double getGravity() {
    return 0.065;
  }

  @Override
  public double getSlowdown() {
    return 0.015;
  }
}
