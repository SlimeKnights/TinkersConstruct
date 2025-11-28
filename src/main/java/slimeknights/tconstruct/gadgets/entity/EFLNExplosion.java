package slimeknights.tconstruct.gadgets.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import slimeknights.tconstruct.library.utils.CustomExplosion;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom explosion logic for EFLNs, more spherical and less random, plus works underwater and more control over damage amount.
 * Loosely, the distinction between this and a regular explosion is this simply breaks all blocks within range if the power is high enough.
 * A normal explosion does a bunch of ray casts from the center, and those ray casts can be interrupted by blocks with high enough resistance (such as water).
 */
public class EFLNExplosion extends CustomExplosion {
  public EFLNExplosion(Level world, Vec3 location, float size, @Nullable Entity entity, float damage, @Nullable DamageSource source, float knockback, boolean causesFire, BlockInteraction mode) {
    super(world, location, size, entity, null, damage, source, knockback, null, causesFire, mode);
  }

  /** @deprecated use {@link #EFLNExplosion(Level, Vec3, float, Entity, float, DamageSource, float, boolean, BlockInteraction)} */
  @Deprecated(forRemoval = true)
  public EFLNExplosion(Level world, Vec3 location, float size, @Nullable Entity entity, float damage, @Nullable DamageSource source, boolean causesFire, BlockInteraction mode) {
    this(world, location, size, entity, damage, source, 1, causesFire, mode);
  }

  @Override
  protected void calculateHitBlocks() {
    // optimization: if we are not interacting with blocks, no need to calculate blocks
    if (!interactsWithBlocks() && !fire) {
      return;
    }

    // we do a sphere of a certain radius, and check if the blockpos is inside the radius
    float radius = this.radius * this.radius;
    int range = (int)radius + 1;

    Set<BlockPos> set = new HashSet<>();
    for (int x = -range; x < range; ++x) {
      for (int y = -range; y < range; ++y) {
        for (int z = -range; z < range; ++z) {
          int distance = x * x + y * y + z * z;
          // inside the sphere?
          if (distance <= radius) {
            BlockPos blockpos = new BlockPos(x, y, z).offset(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z));
            // no air blocks
            if (this.level.isEmptyBlock(blockpos)) {
              continue;
            }

            // explosion "strength" at the current position
            float strength = this.radius * (1f - distance / (radius));
            BlockState blockstate = this.level.getBlockState(blockpos);

            FluidState fluid = this.level.getFluidState(blockpos);
            float power = Math.max(blockstate.getExplosionResistance(this.level, blockpos, this), fluid.getExplosionResistance(this.level, blockpos, this));
            if (this.source != null) {
              power = this.source.getBlockExplosionResistance(this, this.level, blockpos, blockstate, fluid, power);
            }

            strength -= (power + 0.3F) * 0.3F;

            if (strength > 0.0F && (this.source == null || this.source.shouldBlockExplode(this, this.level, blockpos, blockstate, strength))) {
              set.add(blockpos);
            }
          }
        }
      }
    }
    this.toBlow.addAll(set);
  }
}
