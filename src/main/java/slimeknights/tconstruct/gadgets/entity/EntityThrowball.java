package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;

import io.netty.buffer.ByteBuf;
import slimeknights.tconstruct.gadgets.Exploder;
import slimeknights.tconstruct.gadgets.item.ItemThrowball;
import slimeknights.tconstruct.shared.TinkerCommons;

public class EntityThrowball extends EntityThrowable implements IEntityAdditionalSpawnData {

  public ItemThrowball.ThrowballType type;

  public EntityThrowball(World worldIn) {
    super(worldIn);
  }

  public EntityThrowball(World worldIn, double x, double y, double z) {
    super(worldIn, x, y, z);
  }

  public EntityThrowball(World worldIn, EntityLivingBase throwerIn, ItemThrowball.ThrowballType type) {
    super(worldIn, throwerIn);
    this.type = type;
  }

  @Override
  protected void onImpact(@Nonnull RayTraceResult result) {
    if(type != null) {
      switch(type) {
        case GLOW:
          placeGlow(result);
          break;
        case EFLN:
          explode(6f);
          break;
      }
    }

    if(!this.getEntityWorld().isRemote) {
      this.setDead();
    }
  }

  private void placeGlow(RayTraceResult result) {
    if(!getEntityWorld().isRemote) {
      BlockPos pos = result.getBlockPos();
      if(pos == null && result.entityHit != null) {
        pos = result.entityHit.getPosition();
      }

      EnumFacing facing = EnumFacing.DOWN;
      if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
        pos = pos.offset(result.sideHit);
        facing = result.sideHit.getOpposite();
      }

      // add the glow using the special function in BlockGlow so it faces the right way after placing
      TinkerCommons.blockGlow.addGlow(getEntityWorld(), pos, facing);
    }
  }

  protected void explode(float strength) {
    if(!getEntityWorld().isRemote) {
      ExplosionEFLN explosion = new ExplosionEFLN(getEntityWorld(), this, posX, posY, posZ, strength, false, false);
      if(!ForgeEventFactory.onExplosionStart(world, explosion)) {
        Exploder.startExplosion(getEntityWorld(), explosion, this, new BlockPos(posX, posY, posZ), strength, strength);
      }
    }
  }

  @Override
  public void writeEntityToNBT(NBTTagCompound compound) {
    super.writeEntityToNBT(compound);
    ensureType();
    compound.setInteger("type", type.ordinal());
  }

  @Override
  public void readEntityFromNBT(NBTTagCompound compound) {
    super.readEntityFromNBT(compound);
    type = ItemThrowball.ThrowballType.values()[compound.getInteger("type")];
    ensureType();
  }

  @Override
  public void writeSpawnData(ByteBuf buffer) {
    ensureType();
    buffer.writeInt(type.ordinal());
  }

  @Override
  public void readSpawnData(ByteBuf additionalData) {
    type = ItemThrowball.ThrowballType.values()[additionalData.readInt()];
    ensureType();
  }

  private void ensureType() {
    if(type == null) {
      type = ItemThrowball.ThrowballType.GLOW;
    }
  }

}
