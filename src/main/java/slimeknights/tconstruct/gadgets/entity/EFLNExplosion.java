package slimeknights.tconstruct.gadgets.entity;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;
import java.util.Collections;

public class EFLNExplosion extends Explosion {

  protected ImmutableSet<BlockPos> affectedBlockPositionsInternal;

  public EFLNExplosion(World world, @Nullable Entity entity, @Nullable DamageSource damage, @Nullable ExplosionBehavior context, double x, double y, double z, float size, boolean causesFire, DestructionType mode) {
    super(world, entity, damage, context, x, y, z, size, causesFire, mode);
  }

  /**
   * Does the first part of the explosion (destroy blocks)
   */
  @Override
  public void collectBlocksAndDamageEntities() {
    ImmutableSet.Builder<BlockPos> builder = ImmutableSet.builder();

    // we do a sphere of a certain radius, and check if the blockpos is inside the radius
    float r = this.power * this.power;
    int i = (int) r + 1;

    for (int j = -i; j < i; ++j) {
      for (int k = -i; k < i; ++k) {
        for (int l = -i; l < i; ++l) {
          int d = j * j + k * k + l * l;
          // inside the sphere?
          if (d <= r) {
            BlockPos blockpos = new BlockPos(j, k, l).add(this.x, this.y, this.z);
            // no air blocks
            if (this.world.isAir(blockpos)) {
              continue;
            }

            // explosion "strength" at the current position
            float f = this.power * (1f - d / (r));
            BlockState blockstate = this.world.getBlockState(blockpos);

            FluidState ifluidstate = this.world.getFluidState(blockpos);
            float f2 = Math.max(blockstate.getExplosionResistance(this.world, blockpos, this), ifluidstate.getExplosionResistance(this.world, blockpos, this));
            if (this.entity != null) {
              f2 = this.entity.getEffectiveExplosionResistance(this, this.world, blockpos, blockstate, ifluidstate, f2);
            }

            f -= (f2 + 0.3F) * 0.3F;

            if (f > 0.0F && (this.entity == null || this.entity.canExplosionDestroyBlock(this, this.world, blockpos, blockstate, f))) {
              builder.add(blockpos);
            }
          }
        }
      }
    }

    this.affectedBlockPositionsInternal = builder.build();
  }

  @Override
  public void affectWorld(boolean spawnParticles) {
    if (this.world.isClient) {
      this.world.playSound(this.x, this.y, this.z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F, false);
    }

    this.world.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);

    ObjectArrayList<Pair<ItemStack, BlockPos>> arrayList = new ObjectArrayList<>();
    Collections.shuffle(this.affectedBlocks, this.world.random);

    for (BlockPos blockpos : this.affectedBlocks) {
      BlockState blockstate = this.world.getBlockState(blockpos);
      Block block = blockstate.getBlock();

      if (!blockstate.isAir(this.world, blockpos)) {
        BlockPos blockpos1 = blockpos.toImmutable();

        this.world.getProfiler().push("explosion_blocks");

        if (blockstate.canDropFromExplosion(this.world, blockpos, this) && this.world instanceof ServerWorld) {
          BlockEntity tileentity = blockstate.hasTileEntity() ? this.world.getBlockEntity(blockpos) : null;
          LootContext.Builder builder = (new LootContext.Builder((ServerWorld) this.world)).random(this.world.random).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockpos)).parameter(LootContextParameters.TOOL, ItemStack.EMPTY).optionalParameter(LootContextParameters.BLOCK_ENTITY, tileentity).optionalParameter(LootContextParameters.THIS_ENTITY, this.entity);

          if (this.destructionType == DestructionType.DESTROY) {
            builder.parameter(LootContextParameters.EXPLOSION_RADIUS, this.power);
          }

          blockstate.getDroppedStacks(builder).forEach((stack) -> addStack(arrayList, stack, blockpos1));
        }

        blockstate.onBlockExploded(this.world, blockpos, this);
        this.world.getProfiler().pop();
      }
    }
  }

  public void addAffectedBlock(BlockPos blockPos) {
    this.affectedBlocks.add(blockPos);
  }

  private static void addStack(ObjectArrayList<Pair<ItemStack, BlockPos>> arrayList, ItemStack merge, BlockPos blockPos) {
    int i = arrayList.size();

    for (int j = 0; j < i; ++j) {
      Pair<ItemStack, BlockPos> pair = arrayList.get(j);
      ItemStack itemstack = pair.getFirst();

      if (ItemEntity.canMerge(itemstack, merge)) {
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
