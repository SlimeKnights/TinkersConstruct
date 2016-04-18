package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import io.netty.buffer.ByteBuf;
import slimeknights.tconstruct.gadgets.Exploder;
import slimeknights.tconstruct.gadgets.item.ItemThrowball;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.block.BlockGlow;

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
  protected void onImpact(RayTraceResult result) {
    switch(type) {
      case GLOW:
        placeGlow(result);
        break;
      case EFLN:
        explode(6f);
        break;
    }

    if (!this.worldObj.isRemote)
    {
      this.setDead();
    }
  }

  private void placeGlow(RayTraceResult result) {
    if(!worldObj.isRemote) {
      BlockPos pos = result.getBlockPos();
      if(pos == null && result.entityHit != null) {
        pos = result.entityHit.getPosition();
      }
      EnumFacing facing = EnumFacing.DOWN;
      if(result.typeOfHit == RayTraceResult.Type.BLOCK) {
        pos = pos.offset(result.sideHit);
        facing = result.sideHit.getOpposite();
      }
      worldObj.setBlockState(pos, TinkerCommons.blockGlow.getDefaultState().withProperty(BlockGlow.FACING, facing));
    }
  }

  protected void explode(float strength) {
    if(!worldObj.isRemote) {
      //TinkerGadgets.proxy.customExplosion(worldObj, new ExplosionEFLN(worldObj, this, posX, posY, posZ, strength, false, false));
      Exploder.startExplosion(worldObj, new ExplosionEFLN(worldObj, this, posX, posY, posZ, strength, false, false), this, new BlockPos(posX, posY, posZ), strength, strength);
    }
  }

  @Override
  public void writeSpawnData(ByteBuf buffer) {
    if(type == null) {
      type = ItemThrowball.ThrowballType.GLOW;
    }
    buffer.writeInt(type.ordinal());
  }

  @Override
  public void readSpawnData(ByteBuf additionalData) {
    type = ItemThrowball.ThrowballType.values()[additionalData.readInt()];
  }
}
