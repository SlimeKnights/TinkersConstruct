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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ExplosionEFLN extends Explosion {

  protected ImmutableSet<BlockPos> affectedBlockPositionsInternal;

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
    float r = size * size;
    int i = (int) r + 1;

    for(int j = -i; j < i; ++j) {
      for(int k = -i; k < i; ++k) {
        for(int l = -i; l < i; ++l) {
          int d = j * j + k * k + l * l;
          // inside the sphere?
          if(d <= r) {
            BlockPos blockpos = new BlockPos(j, k, l).add(x, y, z);
            // no air blocks
            if(world.isAirBlock(blockpos)) {
              continue;
            }

            // explosion "strength" at the current position
            float f = this.size * (1f - d / (r));
            IBlockState iblockstate = this.world.getBlockState(blockpos);

            float f2 = this.exploder != null ? this.exploder.getExplosionResistance(this, this.world, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(world, blockpos, null, this);
            f -= (f2 + 0.3F) * 0.3F;

            if(f > 0.0F && (this.exploder == null || this.exploder.canExplosionDestroyBlock(this, this.world, blockpos, iblockstate, f))) {
              builder.add(blockpos);
            }
          }
        }
      }
    }

    this.affectedBlockPositionsInternal = builder.build();
  }

  @Override
  public void doExplosionB(boolean spawnParticles) {
    this.world.playSound(null, this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F);

    this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);

    for(BlockPos blockpos : this.affectedBlockPositionsInternal) {
      IBlockState iblockstate = this.world.getBlockState(blockpos);
      Block block = iblockstate.getBlock();

      /*
      if (spawnParticles)
      {
        double d0 = (double)((float)blockpos.getX() + this.world.rand.nextFloat());
        double d1 = (double)((float)blockpos.getY() + this.world.rand.nextFloat());
        double d2 = (double)((float)blockpos.getZ() + this.world.rand.nextFloat());
        double d3 = d0 - this.explosionX;
        double d4 = d1 - this.explosionY;
        double d5 = d2 - this.explosionZ;
        double d6 = (double) MathHelper.sqrt_double(d3 * d3 + d4 * d4 + d5 * d5);
        d3 = d3 / d6;
        d4 = d4 / d6;
        d5 = d5 / d6;
        double d7 = 0.5D / (d6 / (double)this.explosionSize + 0.1D);
        d7 = d7 * (double)(this.world.rand.nextFloat() * this.world.rand.nextFloat() + 0.3F);
        d3 = d3 * d7;
        d4 = d4 * d7;
        d5 = d5 * d7;
        this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (d0 + this.explosionX) / 2.0D, (d1 + this.explosionY) / 2.0D, (d2 + this.explosionZ) / 2.0D, d3, d4, d5, new int[0]);
        this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5, new int[0]);
      }*/

      if(iblockstate.getMaterial() != Material.AIR) {
        if(block.canDropFromExplosion(this)) {
          block.dropBlockAsItemWithChance(this.world, blockpos, this.world.getBlockState(blockpos), 1.0F, 0);
        }

        block.onBlockExploded(this.world, blockpos, this);
      }
    }
  }

  public void addAffectedBlock(BlockPos blockPos) {
    this.affectedBlockPositions.add(blockPos);
  }

}
