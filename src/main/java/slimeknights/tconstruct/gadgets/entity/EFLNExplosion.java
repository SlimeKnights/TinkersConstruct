package slimeknights.tconstruct.gadgets.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Collections;

public class EFLNExplosion extends Explosion {

  protected ImmutableSet<BlockPos> affectedBlockPositionsInternal;

  public EFLNExplosion(Level world, @Nullable Entity entity, @Nullable DamageSource damage, @Nullable ExplosionDamageCalculator context, double x, double y, double z, float size, boolean causesFire, Explosion.BlockInteraction mode) {
    super(world, entity, damage, context, x, y, z, size, causesFire, mode);
  }

  /**
   * Does the first part of the explosion (destroy blocks)
   */
  @Override
  public void explode() {
    ImmutableSet.Builder<BlockPos> builder = ImmutableSet.builder();

    // we do a sphere of a certain radius, and check if the blockpos is inside the radius
    float r = this.radius * this.radius;
    int i = (int) r + 1;

    for (int j = -i; j < i; ++j) {
      for (int k = -i; k < i; ++k) {
        for (int l = -i; l < i; ++l) {
          int d = j * j + k * k + l * l;
          // inside the sphere?
          if (d <= r) {
            BlockPos blockpos = new BlockPos(j, k, l).offset(this.x, this.y, this.z);
            // no air blocks
            if (this.level.isEmptyBlock(blockpos)) {
              continue;
            }

            // explosion "strength" at the current position
            float f = this.radius * (1f - d / (r));
            BlockState blockstate = this.level.getBlockState(blockpos);

            FluidState ifluidstate = this.level.getFluidState(blockpos);
            float f2 = Math.max(blockstate.getExplosionResistance(this.level, blockpos, this), ifluidstate.getExplosionResistance(this.level, blockpos, this));
            if (this.source != null) {
              f2 = this.source.getBlockExplosionResistance(this, this.level, blockpos, blockstate, ifluidstate, f2);
            }

            f -= (f2 + 0.3F) * 0.3F;

            if (f > 0.0F && (this.source == null || this.source.shouldBlockExplode(this, this.level, blockpos, blockstate, f))) {
              builder.add(blockpos);
            }
          }
        }
      }
    }

    this.affectedBlockPositionsInternal = builder.build();
  }

  @Override
  public void finalizeExplosion(boolean spawnParticles) {
    if (this.level.isClientSide) {
      this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
    }

    this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);

    ObjectArrayList<Pair<ItemStack, BlockPos>> arrayList = new ObjectArrayList<>();
    Collections.shuffle(this.toBlow, this.level.random);

    for (BlockPos blockpos : this.toBlow) {
      BlockState blockstate = this.level.getBlockState(blockpos);
      Block block = blockstate.getBlock();

      if (!blockstate.isAir()) {
        BlockPos blockpos1 = blockpos.immutable();

        this.level.getProfiler().push("explosion_blocks");

        if (blockstate.canDropFromExplosion(this.level, blockpos, this) && this.level instanceof ServerLevel) {
          BlockEntity tileentity = blockstate.hasBlockEntity() ? this.level.getBlockEntity(blockpos) : null;
          LootContext.Builder builder = (new LootContext.Builder((ServerLevel) this.level)).withRandom(this.level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, tileentity).withOptionalParameter(LootContextParams.THIS_ENTITY, this.source);

          if (this.blockInteraction == Explosion.BlockInteraction.DESTROY) {
            builder.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius);
          }

          blockstate.getDrops(builder).forEach((stack) -> addStack(arrayList, stack, blockpos1));
        }

        blockstate.onBlockExploded(this.level, blockpos, this);
        this.level.getProfiler().pop();
      }
    }
  }

  public void addAffectedBlock(BlockPos blockPos) {
    this.toBlow.add(blockPos);
  }

  private static void addStack(ObjectArrayList<Pair<ItemStack, BlockPos>> arrayList, ItemStack merge, BlockPos blockPos) {
    int i = arrayList.size();

    for (int j = 0; j < i; ++j) {
      Pair<ItemStack, BlockPos> pair = arrayList.get(j);
      ItemStack itemstack = pair.getFirst();

      if (ItemEntity.areMergable(itemstack, merge)) {
        ItemStack itemstack1 = ItemEntity.merge(itemstack, merge, 16);
        arrayList.set(j, Pair.of(itemstack1, pair.getSecond()));

        if (merge.isEmpty()) {
          return;
        }
      }
    }

    arrayList.add(Pair.of(merge, blockPos));
  }
}
