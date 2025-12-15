package slimeknights.tconstruct.tools.modules.combat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.MonsterMeleeHitModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.ranged.LauncherHitModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition;
import slimeknights.tconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;
import static slimeknights.tconstruct.tools.modifiers.ability.fluid.UseFluidOnHitModifier.spawnParticles;

/** Module to apply spilling effects post melee or projectile hit */
public record SpillingModule(LevelingValue level, ModifierCondition<IToolStackView> condition) implements ModifierModule, MeleeHitModifierHook, MonsterMeleeHitModifierHook.RedirectAfter, LauncherHitModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SpillingModule>defaultHooks(ModifierHooks.MELEE_HIT, ModifierHooks.MONSTER_MELEE_HIT, ModifierHooks.LAUNCHER_HIT);
  public static final RecordLoadable<SpillingModule> LOADER = RecordLoadable.create(LevelingValue.LOADABLE.directField(SpillingModule::level), ModifierCondition.TOOL_FIELD, SpillingModule::new);

  @Override
  public RecordLoadable<SpillingModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /** Applies the fluid to the target */
  private void applyEffect(IToolStackView tool, ModifierEntry modifier, LivingEntity attacker, @Nullable Player playerAttacker, Entity target, @Nullable LivingEntity livingTarget) {
    if (condition.matches(tool, modifier)) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty()) {
        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
        if (recipe.hasEntityEffects()) {
          int consumed = recipe.applyToEntity(fluid, this.level.compute(modifier.getEffectiveLevel()), FluidEffectContext.builder(attacker.level()).user(attacker, playerAttacker).target(target, livingTarget), FluidAction.EXECUTE);
          if (consumed > 0 && (playerAttacker == null || !playerAttacker.isCreative())) {
            spawnParticles(target, fluid);
            fluid.shrink(consumed);
            TANK_HELPER.setFluid(tool, fluid);
          }
        }
      }
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (damageDealt > 0 && context.isFullyCharged()) {
      applyEffect(tool, modifier, context.getAttacker(), context.getPlayerAttacker(), context.getTarget(), context.getLivingTarget());
    }
  }

  @Override
  public void onLauncherHitEntity(IToolStackView tool, ModifierEntry modifier, Projectile projectile, LivingEntity attacker, Entity target, @Nullable LivingEntity livingTarget, float damageDealt) {
    if (damageDealt > 0) {
      applyEffect(tool, modifier, attacker, ModifierUtil.asPlayer(attacker), target, livingTarget);
    }
  }
}
