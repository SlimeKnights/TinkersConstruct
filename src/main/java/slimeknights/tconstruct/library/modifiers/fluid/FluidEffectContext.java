package slimeknights.tconstruct.library.modifiers.fluid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.library.tools.helper.ModifierUtil.asLiving;
import static slimeknights.tconstruct.library.tools.helper.ModifierUtil.asPlayer;

/** Context for calling fluid effects */
@Getter
@RequiredArgsConstructor
public abstract class FluidEffectContext {
  protected final Level level;
  /** Entity using the fluid */
  @Nullable
  protected final LivingEntity entity;
  /** Player using the fluid, may be null if a non-player is the source of the fluid */
  @Nullable
  protected final Player player;
  /** Projectile that caused the fluid, null if no projectile is used (e.g. melee or interact effects) */
  @Nullable
  protected final Projectile projectile;

  /** Gets a damage source based on this context */
  public DamageSource createDamageSource() {
    if (projectile != null) {
      return projectile.damageSources().mobProjectile(projectile, entity);
    }
    if (player != null) {
      return player.damageSources().playerAttack(player);
    }
    if (entity != null) {
      return entity.damageSources().mobAttack(entity);
    }
    // we should never reach here, but just in case
    return level.damageSources().generic();
  }

  /** Context for fluid effects targeting an entity */
  @Getter
  public static class Entity extends FluidEffectContext {
    private final net.minecraft.world.entity.Entity target;
    @Nullable
    private final LivingEntity livingTarget;
    public Entity(Level level, @Nullable LivingEntity holder, @Nullable Player player, @Nullable Projectile projectile, net.minecraft.world.entity.Entity target, @Nullable LivingEntity livingTarget) {
      super(level, holder, player, projectile);
      this.target = target;
      this.livingTarget = livingTarget;
    }

    public Entity(Level level, @Nullable LivingEntity holder, @Nullable Projectile projectile, net.minecraft.world.entity.Entity target) {
      this(level, holder, asPlayer(holder), projectile, target, asLiving(target));
    }

    public Entity(Level level, Player player, @Nullable Projectile projectile, LivingEntity target) {
      this(level, player, player, projectile, target, target);
    }
  }

  /** Context for fluid effects targeting an entity */
  public static class Block extends FluidEffectContext {
    @Getter
    private final BlockHitResult hitResult;
    private BlockState state;
    public Block(Level level, @Nullable LivingEntity holder, @Nullable Player player, @Nullable Projectile projectile, BlockHitResult hitResult) {
      super(level, holder, player, projectile);
      this.hitResult = hitResult;
    }

    public Block(Level level, @Nullable LivingEntity holder, @Nullable Projectile projectile, BlockHitResult hitResult) {
      this(level, holder, asPlayer(holder), projectile, hitResult);
    }

    public Block(Level level, @Nullable Player player, @Nullable Projectile projectile, BlockHitResult hitResult) {
      this(level, player, player, projectile, hitResult);
    }

    /** Gets the block state targeted by this context */
    public BlockState getBlockState() {
      if (state == null) {
        state = level.getBlockState(hitResult.getBlockPos());
      }
      return state;
    }

    /** Checks if the block in front of the hit block is replaceable */
    public boolean isOffsetReplaceable() {
      return level.getBlockState(hitResult.getBlockPos().relative(hitResult.getDirection())).canBeReplaced();
    }
  }
}
