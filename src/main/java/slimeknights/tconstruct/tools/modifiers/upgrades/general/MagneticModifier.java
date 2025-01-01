package slimeknights.tconstruct.tools.modifiers.upgrades.general;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.PlantHarvestModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.special.ShearsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.NamespacedNBT;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class MagneticModifier extends Modifier implements PlantHarvestModifierHook, ShearsModifierHook, BlockBreakModifierHook, MeleeHitModifierHook, ProjectileLaunchModifierHook {
  /** Player modifier data key for haste */
  private static final TinkerDataKey<Integer> MAGNET = TConstruct.createKey("magnet");

  public MagneticModifier() {
    // TODO: move this out of constructor to generalized logic
    MinecraftForge.EVENT_BUS.addListener(MagneticModifier::onLivingTick);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.PLANT_HARVEST, ModifierHooks.SHEAR_ENTITY, ModifierHooks.BLOCK_BREAK, ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_LAUNCH);
    hookBuilder.addModule(new ArmorLevelModule(MAGNET, false, null));
  }

  @Override
  public void afterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
    if (!context.isAOE()) {
      TinkerModifiers.magneticEffect.get().apply(context.getLiving(), 30, modifier.getLevel() - 1);
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (!context.isExtraAttack()) {
      TinkerModifiers.magneticEffect.get().apply(context.getAttacker(), 30, modifier.getLevel() - 1);
    }
  }

  @Override
  public void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos) {
    Player player = context.getPlayer();
    if (player != null) {
      TinkerModifiers.magneticEffect.get().apply(player, 30, modifier.getLevel() - 1);
    }
  }

  @Override
  public void afterShearEntity(IToolStackView tool, ModifierEntry modifier, Player player, Entity entity, boolean isTarget) {
    if (isTarget) {
      TinkerModifiers.magneticEffect.get().apply(player, 30, modifier.getLevel() - 1);
    }
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    if (primary) {
      TinkerModifiers.magneticEffect.get().apply(shooter, 30, modifier.getLevel() - 1);
    }
  }


  // armor

  /** Called to perform the magnet for armor */
  private static void onLivingTick(LivingTickEvent event) {
    // TOOD: this will run on any held armor that is also melee/harvest, is that a problem?
    LivingEntity entity = event.getEntity();
    if (!entity.isSpectator() && (entity.tickCount & 1) == 0) {
      int level = ArmorLevelModule.getLevel(entity, MAGNET);
      if (level > 0) {
        applyMagnet(entity, level);
      }
    }
  }

  /** Performs the magnetic effect */
  public static <T extends Entity> void applyVelocity(LivingEntity entity, int amplifier, Class<T> targetClass, int minRange, float speed, int maxPush) {
    // super magnetic - inspired by botanias code
    double x = entity.getX();
    double y = entity.getY();
    double z = entity.getZ();
    float range = minRange + amplifier;
    List<T> targets = entity.level().getEntitiesOfClass(targetClass, new AABB(x - range, y - range, z - range, x + range, y + range, z + range));

    // only pull up to a max targets
    int pulled = 0;
    for (T target : targets) {
      if (target.isRemoved()) {
        continue;
      }
      // calculate direction: item -> player
      Vec3 vec = entity.position()
                       .subtract(target.getX(), target.getY(), target.getZ())
                       .normalize()
                       .scale(speed * (amplifier + 1));
      if (!target.isNoGravity()) {
        vec = vec.add(0, 0.04f, 0);
      }

      // we calculated the movement vector and set it to the correct strength.. now we apply it \o/
      target.setDeltaMovement(target.getDeltaMovement().add(vec));

      pulled++;
      if (pulled > maxPush) {
        break;
      }
    }
  }

  /** Performs the magnetic effect */
  public static void applyMagnet(LivingEntity entity, int amplifier) {
    applyVelocity(entity, amplifier, ItemEntity.class, 3, 0.05f, 100);
  }
}
