package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluid;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluidManager;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.module.ToolModuleHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modifiers.ability.armor.WettingModifier;

import javax.annotation.Nullable;

/** Modifier to handle spilling recipes */
public class SpillingModifier extends WettingModifier implements EntityInteractionModifierHook {
  /** Overridable method to create the attack context */
  @Override
  public ToolAttackContext createContext(LivingEntity self, @Nullable Player player, @Nullable Entity attacker, FluidStack fluid) {
    assert attacker != null;
    spawnParticles(attacker, fluid);
    return new ToolAttackContext(self, player, InteractionHand.MAIN_HAND, attacker, attacker instanceof LivingEntity living ? living : null, false, 1.0f, false);
  }

  /** Checks if the modifier triggers */
  @Override
  protected boolean doesTrigger(DamageSource source, boolean isDirectDamage) {
    return source.getEntity() != null && isDirectDamage;
  }

  private void spillFluid(IToolStackView tool, int level, ToolAttackContext context, FluidStack fluid) {
    if (!fluid.isEmpty()) {
      SpillingFluid recipe = SpillingFluidManager.INSTANCE.find(fluid.getFluid());
      if (recipe.hasEffects()) {
        FluidStack remaining = recipe.applyEffects(fluid, level, context);
        spawnParticles(context.getTarget(), fluid);
        Player player = context.getPlayerAttacker();
        if (player == null || !player.isCreative()) {
          setFluid(tool, remaining);
        }
      }
    }
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    if (damageDealt > 0 && context.isFullyCharged()) {
      spillFluid(tool, level, context, getFluid(tool));
    }
    return 0;
  }

  @Override
  public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {    // melee items get spilling via attack, non melee interact to use it
    if (source != InteractionSource.ARMOR && !tool.hasTag(TinkerTags.Items.MELEE) && tool.getDefinitionData().getModule(ToolModuleHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      FluidStack fluid = getFluid(tool);
      if (!fluid.isEmpty() && SpillingFluidManager.INSTANCE.contains(fluid.getFluid())) {
        if (!player.level.isClientSide) {
          ToolAttackContext context = new ToolAttackContext(player, player, hand, target, target instanceof LivingEntity l ? l : null, false, 1.0f, false);
          spillFluid(tool, modifier.getLevel(), context, fluid);
        }
        player.getCooldowns().addCooldown(tool.getItem(), 20);
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.ENTITY_INTERACT);
  }
}
