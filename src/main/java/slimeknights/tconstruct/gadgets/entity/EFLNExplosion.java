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
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.ExplosionContext;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collections;

public class EFLNExplosion extends Explosion {

  protected ImmutableSet<BlockPos> affectedBlockPositionsInternal;

  public EFLNExplosion(World world, @Nullable Entity entity, @Nullable DamageSource damage, @Nullable ExplosionContext context, double x, double y, double z, float size, boolean causesFire, Explosion.Mode mode) {
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
      this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
    }

    this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);

    ObjectArrayList<Pair<ItemStack, BlockPos>> arrayList = new ObjectArrayList<>();
    Collections.shuffle(this.toBlow, this.level.random);

    for (BlockPos blockpos : this.toBlow) {
      BlockState blockstate = this.level.getBlockState(blockpos);
      Block block = blockstate.getBlock();

      if (!blockstate.isAir(this.level, blockpos)) {
        BlockPos blockpos1 = blockpos.immutable();

        this.level.getProfiler().push("explosion_blocks");

        if (blockstate.canDropFromExplosion(this.level, blockpos, this) && this.level instanceof ServerWorld) {
          TileEntity tileentity = blockstate.hasTileEntity() ? this.level.getBlockEntity(blockpos) : null;
          LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.level)).withRandom(this.level.random).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(blockpos)).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withOptionalParameter(LootParameters.BLOCK_ENTITY, tileentity).withOptionalParameter(LootParameters.THIS_ENTITY, this.source);

          if (this.blockInteraction == Explosion.Mode.DESTROY) {
            builder.withParameter(LootParameters.EXPLOSION_RADIUS, this.radius);
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
