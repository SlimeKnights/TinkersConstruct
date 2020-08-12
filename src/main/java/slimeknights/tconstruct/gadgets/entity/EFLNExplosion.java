package slimeknights.tconstruct.gadgets.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IExplosionContext;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collections;

public class EFLNExplosion extends Explosion {

  protected ImmutableSet<BlockPos> affectedBlockPositionsInternal;

  public EFLNExplosion(World world, @Nullable Entity entity, @Nullable DamageSource damage, @Nullable IExplosionContext context, double x, double y, double z, float size, boolean causesFire, Explosion.Mode mode) {
    super(world, entity, damage, context, x, y, z, size, causesFire, mode);
  }

  /**
   * Does the first part of the explosion (destroy blocks)
   */
  @Override
  public void doExplosionA() {
    ImmutableSet.Builder<BlockPos> builder = ImmutableSet.builder();

    // we do a sphere of a certain radius, and check if the blockpos is inside the radius
    float r = this.size * this.size;
    int i = (int) r + 1;

    for (int j = -i; j < i; ++j) {
      for (int k = -i; k < i; ++k) {
        for (int l = -i; l < i; ++l) {
          int d = j * j + k * k + l * l;
          // inside the sphere?
          if (d <= r) {
            BlockPos blockpos = new BlockPos(j, k, l).add(this.x, this.y, this.z);
            // no air blocks
            if (this.world.isAirBlock(blockpos)) {
              continue;
            }

            // explosion "strength" at the current position
            float f = this.size * (1f - d / (r));
            BlockState blockstate = this.world.getBlockState(blockpos);

            FluidState ifluidstate = this.world.getFluidState(blockpos);
            float f2 = Math.max(blockstate.getExplosionResistance(this.world, blockpos, this), ifluidstate.getExplosionResistance(this.world, blockpos, this));
            if (this.exploder != null) {
              f2 = this.exploder.getExplosionResistance(this, this.world, blockpos, blockstate, ifluidstate, f2);
            }

            f -= (f2 + 0.3F) * 0.3F;

            if (f > 0.0F && (this.exploder == null || this.exploder.canExplosionDestroyBlock(this, this.world, blockpos, blockstate, f))) {
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
    if (this.world.isRemote) {
      this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, false);
    }

    this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);

    ObjectArrayList<Pair<ItemStack, BlockPos>> arrayList = new ObjectArrayList<>();
    Collections.shuffle(this.affectedBlockPositions, this.world.rand);

    for (BlockPos blockpos : this.affectedBlockPositions) {
      BlockState blockstate = this.world.getBlockState(blockpos);
      Block block = blockstate.getBlock();

      if (!blockstate.isAir(this.world, blockpos)) {
        BlockPos blockpos1 = blockpos.toImmutable();

        this.world.getProfiler().startSection("explosion_blocks");

        if (blockstate.canDropFromExplosion(this.world, blockpos, this) && this.world instanceof ServerWorld) {
          TileEntity tileentity = blockstate.hasTileEntity() ? this.world.getTileEntity(blockpos) : null;
          LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).withRandom(this.world.rand).withParameter(LootParameters.POSITION, blockpos).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withNullableParameter(LootParameters.BLOCK_ENTITY, tileentity).withNullableParameter(LootParameters.THIS_ENTITY, this.exploder);

          if (this.mode == Explosion.Mode.DESTROY) {
            builder.withParameter(LootParameters.EXPLOSION_RADIUS, this.size);
          }

          blockstate.getDrops(builder).forEach((stack) -> addStack(arrayList, stack, blockpos1));
        }

        blockstate.onBlockExploded(this.world, blockpos, this);
        this.world.getProfiler().endSection();
      }
    }
  }

  public void addAffectedBlock(BlockPos blockPos) {
    this.affectedBlockPositions.add(blockPos);
  }

  private static void addStack(ObjectArrayList<Pair<ItemStack, BlockPos>> arrayList, ItemStack merge, BlockPos blockPos) {
    int i = arrayList.size();

    for (int j = 0; j < i; ++j) {
      Pair<ItemStack, BlockPos> pair = arrayList.get(j);
      ItemStack itemstack = pair.getFirst();

      if (ItemEntity.canMergeStacks(itemstack, merge)) {
        ItemStack itemstack1 = ItemEntity.mergeStacks(itemstack, merge, 16);
        arrayList.set(j, Pair.of(itemstack1, pair.getSecond()));

        if (merge.isEmpty()) {
          return;
        }
      }
    }

    arrayList.add(Pair.of(merge, blockPos));
  }
}
