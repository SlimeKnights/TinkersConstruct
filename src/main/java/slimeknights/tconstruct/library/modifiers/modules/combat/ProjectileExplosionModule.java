package slimeknights.tconstruct.library.modifiers.modules.combat;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CheckReturnValue;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Explosion.BlockInteraction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.CombatHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerDamageTypes;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.gadgets.entity.EFLNExplosion;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.entity.ProjectileWithPower;
import slimeknights.tconstruct.library.modifiers.fluid.entity.DamageFluidEffect.DamageTypePair;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileFuseModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.CustomExplosion;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module causing the projectile to explode on hit. Explosion damage will be based on {@link ToolStats#PROJECTILE_DAMAGE}
 * @param radius             Explosion radius
 * @param knockback          Amount of knockback to deal
 * @param placeFire          If true, the explosion places fire
 * @param blockInteraction   Block interaction behavior.
 */
public record ProjectileExplosionModule(LevelingValue radius, float eflnBonus, LevelingValue knockback, boolean placeFire, Explosion.BlockInteraction blockInteraction) implements ModifierModule, ProjectileHitModifierHook, ProjectileFuseModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ProjectileExplosionModule>defaultHooks(ModifierHooks.PROJECTILE_HIT, ModifierHooks.PROJECTILE_HIT_CLIENT, ModifierHooks.PROJECTILE_FUSE);
  public static final RecordLoadable<ProjectileExplosionModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.requiredField("radius", ProjectileExplosionModule::radius),
    FloatLoadable.FROM_ZERO.defaultField("efln_bonus", 0f, false, ProjectileExplosionModule::eflnBonus),
    LevelingValue.LOADABLE.defaultField("knockback", LevelingValue.flat(1), true, ProjectileExplosionModule::knockback),
    BooleanLoadable.INSTANCE.defaultField("place_fire", false, ProjectileExplosionModule::placeFire),
    new EnumLoadable<>(Explosion.BlockInteraction.class).requiredField("block_interaction", ProjectileExplosionModule::blockInteraction),
    ProjectileExplosionModule::new);
  /** Datakey for EFLN style explosions, works underwater */
  public static final ResourceLocation EFLN = TConstruct.getResource("efln");

  /** Use the builder via {@link #radius(float, float)}, directly calling the constructor is subject to break when we add new features. */
  @Internal
  public ProjectileExplosionModule {}

  /** Creates a new builder instance */
  public static Builder radius(float flat, float perLevel) {
    return new Builder(new LevelingValue(flat, perLevel));
  }

  @Override
  public RecordLoadable<ProjectileExplosionModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Triggers the explosion at the given location */
  private boolean explode(ModifierEntry modifier, Projectile projectile, ModDataNBT persistentData, Vec3 location) {
    float level = modifier.getEffectiveLevel();
    float radius = this.radius.computeForLevel(level);
    // limit to non-reusable ammo, mostly ensures ballisa doesn't explode as the damage will be wrong
    // TODO: consider dedicated tag blacklist
    if (radius > 0.5f && !projectile.getType().is(TinkerTags.EntityTypes.REUSABLE_AMMO)) {
      Level world = projectile.level();
      if (!world.isClientSide) {
        float power = ProjectileWithPower.getDamage(projectile);
        // figure out who to blame for the damage
        Entity cause = projectile.getOwner();
        DamageTypePair damageType = (cause != null ? TinkerDamageTypes.MOB_EXPLOSION : TinkerDamageTypes.EXPLOSION);
        DamageSource damageSource = CombatHelper.damageSource(damageType.ranged(), projectile, cause);

        // damage fishing rods, since they are supposed to damage on retrieve
        // if you need this for your custom projectile, let us know and we can dehardcode it
        ModifierUtil.updateFishingRod(projectile, 2 + 3 * modifier.getLevel(), true);

        // discard projectile so it doesn't explode again
        projectile.discard();

        // if marked, use EFLN style explosion
        // controlled by persistent data so another modifier can set this, we use fins
        CustomExplosion explosion;
        if (persistentData.getBoolean(EFLN)) {
          explosion = new EFLNExplosion(
            world, location, radius + eflnBonus, projectile,
            power, damageSource, knockback.computeForScale(level),
            placeFire, blockInteraction
          );
        } else {
          explosion = new CustomExplosion(
            world, location, radius, projectile, null,
            power, damageSource, knockback.computeForScale(level), null,
            placeFire, blockInteraction
          );
        }
        // cause the explosion
       explosion.handleServer();
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean onProjectileHitsBlock(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity owner) {
    return explode(modifier, projectile, persistentData, hit.getLocation());
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target, boolean notBlocked) {
    return explode(modifier, projectile, persistentData, hit.getLocation());
  }

  @Override
  public void onProjectileFuseFinish(ModifierNBT modifiers, ModDataNBT persistentData, ModifierEntry modifier, ItemStack ammo, Projectile projectile, @Nullable AbstractArrow arrow) {
    explode(modifier, projectile, persistentData, projectile.position());
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  @Setter
  @CanIgnoreReturnValue
  @SuppressWarnings("unused") // API
  public static class Builder {
    /** Explosion distance from target */
    private final LevelingValue radius;
    /** Explosion distance from target */
    private float eflnBonus = 0;
    /** Explosion knockback multiplier for entities */
    private LevelingValue knockback = LevelingValue.flat(1f);
    /** If true, explosion places fires */
    private boolean placeFire = false;
    /** Behavior of explosion against blocks */
    private BlockInteraction blockInteraction = BlockInteraction.DESTROY_WITH_DECAY;

    /** Sets the explosion not damage blocks */
    public Builder ignoreBlocks() {
      return blockInteraction(BlockInteraction.KEEP);
    }

    /** Sets the explosion to drop all blocks */
    public Builder noBlockDecay() {
      return blockInteraction(BlockInteraction.DESTROY);
    }

    /** Sets this to place fire */
    public Builder placeFire() {
      return placeFire(true);
    }

    /** Builds the final effect */
    @CheckReturnValue
    public ProjectileExplosionModule build() {
      return new ProjectileExplosionModule(radius, eflnBonus, knockback, placeFire, blockInteraction);
    }
  }
}
