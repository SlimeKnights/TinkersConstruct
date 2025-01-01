package slimeknights.tconstruct.tools.modifiers.traits.general;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.Sounds;
import slimeknights.tconstruct.library.events.teleport.EnderportingTeleportEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;

import javax.annotation.Nullable;
import java.util.Set;

public class EnderportingModifier extends NoLevelsModifier implements PlantHarvestModifierHook, ProjectileHitModifierHook, ProjectileLaunchModifierHook, BlockHarvestModifierHook, MeleeHitModifierHook {
  private static final ResourceLocation PRIMARY_ARROW = TConstruct.getResource("enderporting_primary");
  private static final Set<RelativeMovement> PACKET_FLAGS = ImmutableSet.of(RelativeMovement.X, RelativeMovement.Y, RelativeMovement.Z);

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.PLANT_HARVEST, ModifierHooks.PROJECTILE_HIT, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.BLOCK_HARVEST, ModifierHooks.MELEE_HIT);
  }

  @Override
  public int getPriority() {
    return 45;
  }

  /** Attempts to teleport to the given location */
  private static boolean tryTeleport(LivingEntity living, double x, double y, double z) {
    Level world = living.getCommandSenderWorld();
    // should never happen with the hooks, but just in case
    if (world.isClientSide) {
      return false;
    }
    // this logic is cloned from suffocation damage logic
    float scaledWidth = living.getBbWidth() * 0.8F;
    float eyeHeight = living.getEyeHeight();
    AABB aabb = AABB.ofSize(new Vec3(x, y + (eyeHeight / 2), z), scaledWidth, eyeHeight, scaledWidth);

    boolean didCollide = world.getBlockCollisions(living, aabb).iterator().hasNext();

    // if we collided, try again 1 block down, means mining the top of 2 blocks is valid
    if (didCollide && living.getBbHeight() > 1) {
      // try again 1 block down
      aabb = aabb.move(0, -1, 0);
      didCollide = world.getBlockCollisions(living, aabb).iterator().hasNext();
      y -= 1;
    }

    // as long as no collision now, we can teleport
    if (!didCollide) {
      // actual teleport
      EnderportingTeleportEvent event = new EnderportingTeleportEvent(living, x, y, z);
      MinecraftForge.EVENT_BUS.post(event);
      if (!event.isCanceled()) {
        // this logic only runs serverside, so need to use the server controller logic to move the player
        if (living instanceof ServerPlayer playerMP) {
          playerMP.connection.teleport(x, y, z, playerMP.getYRot(), playerMP.getXRot(), PACKET_FLAGS);
        } else {
          living.setPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
        }
        // particles must be sent on a server
        if (world instanceof ServerLevel serverWorld) {
          for (int i = 0; i < 32; ++i) {
            serverWorld.sendParticles(ParticleTypes.PORTAL, living.getX(), living.getY() + world.random.nextDouble() * 2.0D, living.getZ(), 1, world.random.nextGaussian(), 0.0D, world.random.nextGaussian(), 0);
          }
        }
        world.playSound(null, living.getX(), living.getY(), living.getZ(), Sounds.ENDERPORTING.getSound(),  living.getSoundSource(), 1f, 1f);
        return true;
      }
    }
    return false;
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (!context.isExtraAttack()) {
      LivingEntity target = context.getLivingTarget();
      // if the entity is dead now
      if (target != null) {
        LivingEntity attacker = context.getAttacker();
        Vec3 oldPosition = attacker.position();
        if (tryTeleport(attacker, target.getX(), target.getY(), target.getZ())) {
          tryTeleport(target, oldPosition.x, oldPosition.y, oldPosition.z);
          ToolDamageUtil.damageAnimated(tool, 2, attacker, context.getSlotType());
        }
      }
    }
  }

  @Override
  public void finishHarvest(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, int harvested) {
    if (harvested > 0 && context.canHarvest()) {
      BlockPos pos = context.getPos();
      LivingEntity living = context.getLiving();
      if (tryTeleport(living, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f)) {
        ToolDamageUtil.damageAnimated(tool, 2, living);
      }
    }
  }

  @Override
  public void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos) {
    // only teleport to the center block
    if (context.getClickedPos().equals(pos)) {
      LivingEntity living = context.getPlayer();
      if (living != null && tryTeleport(living, pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f)) {
        ToolDamageUtil.damageAnimated(tool, 2, living, context.getHand());
      }
    }
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (attacker != null && attacker != target && persistentData.getBoolean(PRIMARY_ARROW)) {
      Entity hitEntity = hit.getEntity();
      Vec3 oldPosition = attacker.position();
      if (attacker.level() == projectile.level() && tryTeleport(attacker, hitEntity.getX(), hitEntity.getY(), hitEntity.getZ()) && target != null) {
        tryTeleport(target, oldPosition.x, oldPosition.y, oldPosition.z);
      }
    }
    return false;
  }

  @Override
  public boolean onProjectileHitBlock(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, BlockHitResult hit, @Nullable LivingEntity attacker) {
    if (attacker != null && persistentData.getBoolean(PRIMARY_ARROW)) {
      BlockPos target = hit.getBlockPos().relative(hit.getDirection());
      if (attacker.level() == projectile.level() && tryTeleport(attacker, target.getX() + 0.5f, target.getY(), target.getZ() + 0.5f)) {
        projectile.discard();
      }
    }
    return false;
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    if (primary) {
      // damage on shoot as we won't have tool context once the arrow lands
      ToolDamageUtil.damageAnimated(tool, 10, shooter, shooter.getUsedItemHand());
      persistentData.putBoolean(PRIMARY_ARROW, true);
    }
  }
}
