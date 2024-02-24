package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.combat.DamageTakenModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluid;
import slimeknights.tconstruct.library.modifiers.spilling.SpillingFluidManager;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.ToolAttackContext;
import slimeknights.tconstruct.library.tools.definition.module.ToolModuleHooks;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.ability.armor.UseFluidOnHitModifier;

import javax.annotation.Nullable;

/** Modifier to handle spilling recipes */
public class SpillingModifier extends UseFluidOnHitModifier implements EntityInteractionModifierHook, DamageTakenModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.ENTITY_INTERACT, TinkerHooks.DAMAGE_TAKEN);
  }

  @Override
  public int afterEntityHit(IToolStackView tool, int level, ToolAttackContext context, float damageDealt) {
    if (damageDealt > 0 && context.isFullyCharged()) {
      FluidStack fluid = getFluid(tool);
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
    return 0;
  }

  @Override
  public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {    // melee items get spilling via attack, non melee interact to use it
    if (source != InteractionSource.ARMOR && !tool.hasTag(TinkerTags.Items.MELEE) && tool.getDefinitionData().getModule(ToolModuleHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      FluidStack fluid = getFluid(tool);
      if (!fluid.isEmpty()) {
        SpillingFluid recipe = SpillingFluidManager.INSTANCE.find(fluid.getFluid());
        if (recipe.hasEffects()) {
          if (!player.level.isClientSide) {
            // for the main target, consume fluids
            int level = modifier.getLevel();
            ToolAttackContext context = new ToolAttackContext(player, player, hand, target, target instanceof LivingEntity l ? l : null, false, 1.0f, false);
            FluidStack remaining = recipe.applyEffects(fluid.copy(), level, context);
            spawnParticles(target, fluid);
            if (!player.isCreative()) {
              setFluid(tool, remaining);
            }

            // expanded logic, they do not consume fluid, you get some splash for free
            int numTargets = 1;
            float range = 1 + tool.getModifierLevel(TinkerModifiers.expanded.get());
            float rangeSq = range * range;
            for (Entity aoeTarget : player.level.getEntitiesOfClass(Entity.class, target.getBoundingBox().inflate(range, 0.25, range))) {
              if (aoeTarget != player && aoeTarget != target && !(aoeTarget instanceof ArmorStand stand && stand.isMarker()) && target.distanceToSqr(aoeTarget) < rangeSq) {
                numTargets++;
                context = new ToolAttackContext(player, player, hand, aoeTarget, aoeTarget instanceof LivingEntity l ? l : null, false, 1.0f, true);

                recipe.applyEffects(fluid.copy(), level, context);
                spawnParticles(aoeTarget, fluid);
              }
            }

            // damage the tool, we charge for the multiplier and for the number of targets hit
            ToolDamageUtil.damageAnimated(tool, numTargets * level, player, hand);
          }

          // cooldown based on attack speed/draw speed. both are on the same scale and default to 1, we don't care which one the tool uses
          player.getCooldowns().addCooldown(tool.getItem(), (int)(20 / (tool.getStats().get(ToolStats.ATTACK_SPEED) * ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.DRAW_SPEED))));
          return InteractionResult.SUCCESS;
        }
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public ToolAttackContext createContext(LivingEntity self, @Nullable Player player, @Nullable Entity attacker, FluidStack fluid) {
    assert attacker != null;
    spawnParticles(attacker, fluid);
    return new ToolAttackContext(self, player, InteractionHand.MAIN_HAND, attacker, attacker instanceof LivingEntity living ? living : null, false, 1.0f, false);
  }

  @Override
  protected boolean doesTrigger(DamageSource source, boolean isDirectDamage) {
    return source.getEntity() != null && isDirectDamage;
  }

  @Override
  public void onDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    useFluid(tool, modifier, context, slotType, source, isDirectDamage);
  }
}
