package slimeknights.tconstruct.gadgets.entity;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ExplosionEFLN extends Explosion {

  protected ImmutableSet<BlockPos> affectedBlockPositions;

  @SideOnly(Side.CLIENT)
  public ExplosionEFLN(World worldIn, Entity entityIn, double x, double y, double z, float size, List<BlockPos> affectedPositions) {
    super(worldIn, entityIn, x, y, z, size, affectedPositions);
  }

  @SideOnly(Side.CLIENT)
  public ExplosionEFLN(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean smoking, List<BlockPos> affectedPositions) {
    super(worldIn, entityIn, x, y, z, size, flaming, smoking, affectedPositions);
  }

  public ExplosionEFLN(World worldIn, Entity entityIn, double x, double y, double z, float size, boolean flaming, boolean smoking) {
    super(worldIn, entityIn, x, y, z, size, flaming, smoking);
  }

  @Override
  public void doExplosionA() {
    ImmutableSet.Builder<BlockPos> builder = ImmutableSet.builder();

    // we do a sphere of a certain radius, and check if the blockpos is inside the radius
    float r = explosionSize * explosionSize;
    int i = (int) r + 1;

    for(int j = -i; j < i; ++j) {
      for(int k = -i; k < i; ++k) {
        for(int l = -i; l < i; ++l) {
          int d = j * j + k * k + l * l;
          // inside the sphere?
          if(d <= r) {
            BlockPos blockpos = new BlockPos(j, k, l).add(explosionX, explosionY, explosionZ);
            // no air blocks
            if(worldObj.isAirBlock(blockpos)) {
              continue;
            }

            // explosion "strength" at the current position
            float f = this.explosionSize * (1f - d / (r));
            IBlockState iblockstate = this.worldObj.getBlockState(blockpos);

            float f2 = this.exploder != null ? this.exploder.getExplosionResistance(this, this.worldObj, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(worldObj, blockpos, (Entity) null, this);
            f -= (f2 + 0.3F) * 0.3F;


            if(f > 0.0F && (this.exploder == null || this.exploder.verifyExplosion(this, this.worldObj, blockpos, iblockstate, f))) {
              builder.add(blockpos);
            }
          }
        }
      }
    }

    this.affectedBlockPositions = builder.build();
  }

  @Override
  public void doExplosionB(boolean spawnParticles) {
    this.worldObj.playSound(null, this.explosionX, this.explosionY, this.explosionZ, SoundEvents.entity_generic_explode, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2F) * 0.7F);

    this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.explosionX, this.explosionY, this.explosionZ, 1.0D, 0.0D, 0.0D);

    for(BlockPos blockpos : this.affectedBlockPositions) {
      IBlockState iblockstate = this.worldObj.getBlockState(blockpos);
      Block block = iblockstate.getBlock();

      /*
      if (spawnParticles)
      {
        double d0 = (double)((float)blockpos.getX() + this.worldObj.rand.nextFloat());
        double d1 = (double)((float)blockpos.getY() + this.worldObj.rand.nextFloat());
        double d2 = (double)((float)blockpos.getZ() + this.worldObj.rand.nextFloat());
        double d3 = d0 - this.explosionX;
        double d4 = d1 - this.explosionY;
        double d5 = d2 - this.explosionZ;
        double d6 = (double) MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
        d3 = d3 / d6;
        d4 = d4 / d6;
        d5 = d5 / d6;
        double d7 = 0.5D / (d6 / (double)this.explosionSize + 0.1D);
        d7 = d7 * (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
        d3 = d3 * d7;
        d4 = d4 * d7;
        d5 = d5 * d7;
        this.worldObj.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.explosionX) / 2.0D, (d1 + this.explosionY) / 2.0D, (d2 + this.explosionZ) / 2.0D, d3, d4, d5, new int[0]);
        this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
      }*/

      if(iblockstate.getMaterial() != Material.air) {
        if(block.canDropFromExplosion(this)) {
          block.dropBlockAsItemWithChance(this.worldObj, blockpos, this.worldObj.getBlockState(blockpos), 1.0F, 0);
        }

        block.onBlockExploded(this.worldObj, blockpos, this);
      }
    }
  }
}
