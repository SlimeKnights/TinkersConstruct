package slimeknights.tconstruct.tools.modifiers.ability.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.AreaOfEffectHighlightModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator.AOEMatchType;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.Util;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

/** Modifier to handle spilling recipes on interaction */
public class SplashingModifier extends Modifier implements EntityInteractionModifierHook, BlockInteractionModifierHook, AreaOfEffectHighlightModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ToolTankHelper.TANK_HANDLER);
    hookBuilder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
    hookBuilder.addHook(this, ModifierHooks.ENTITY_INTERACT, ModifierHooks.BLOCK_INTERACT, ModifierHooks.AOE_HIGHLIGHT);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return InteractionSource.formatModifierName(tool, entry.getModifier(), entry.getDisplayName());
  }

  @Override
  public boolean shouldHighlight(IToolStackView tool, ModifierEntry modifier, UseOnContext context, BlockPos offset, BlockState state) {
    FluidStack fluid = TANK_HELPER.getFluid(tool);
    if (!fluid.isEmpty()) {
      return FluidEffectManager.INSTANCE.find(fluid.getFluid()).hasBlockEffects();
    }
    return false;
  }

  @Override
  public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
    // melee items get spilling via attack, non melee interact to use it
    if (!tool.isBroken() && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty()) {
        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
        if (recipe.hasEntityEffects()) {
          Level world = player.level();

          // cooldown based on attack speed/draw speed. both are on the same scale and default to 1, we don't care which one the tool uses
          // applied before we do the effect to block recursive calls, notably ender might cause that
          player.getCooldowns().addCooldown(tool.getItem(), (int)(20 / ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.DRAW_SPEED)));

          if (!world.isClientSide) {
            // for the main target, consume fluids
            float level = modifier.getEffectiveLevel();
            int numTargets = 0;
            int consumed = recipe.applyToEntity(fluid, level, FluidEffectContext.builder(world).user(player).target(target), FluidAction.EXECUTE);
            if (consumed > 0) {
              numTargets++;
              UseFluidOnHitModifier.spawnParticles(target, fluid);
              fluid.shrink(consumed);
            }

            // expanded logic, consumes extra fluid per target
            if (!fluid.isEmpty()) {
              float range = 1 + tool.getModifierLevel(TinkerModifiers.expanded.get());
              float rangeSq = range * range;
              for (Entity aoeTarget : world.getEntitiesOfClass(Entity.class, target.getBoundingBox().inflate(range, 0.25, range))) {
                if (aoeTarget != player && aoeTarget != target && !(aoeTarget instanceof ArmorStand stand && stand.isMarker()) && target.distanceToSqr(aoeTarget) < rangeSq) {
                  consumed = recipe.applyToEntity(fluid, level, FluidEffectContext.builder(world).user(player).target(aoeTarget), FluidAction.EXECUTE);
                  if (consumed > 0) {
                    numTargets++;
                    UseFluidOnHitModifier.spawnParticles(aoeTarget, fluid);
                    // consume fluid for each target entity
                    fluid.shrink(consumed);
                    if (fluid.isEmpty()) {
                      break;
                    }
                  }
                }
              }
            }

            // consume the fluid last, if any target used fluid
            if (!player.isCreative() ) {
              if (numTargets > 0) {
                TANK_HELPER.setFluid(tool, fluid);
              }

              // damage the tool, we charge for the multiplier and for the number of targets hit
              ToolDamageUtil.damageAnimated(tool, Mth.ceil(numTargets * level), player, hand);
            }
          }

          return InteractionResult.SUCCESS;
        }
      }
    }
    return InteractionResult.PASS;
  }

  /** Spawns particles at the given entity */
  private static void spawnParticles(Level level, BlockHitResult hit, FluidStack fluid) {
    if (level instanceof ServerLevel) {
      Vec3 location = hit.getLocation();
      ((ServerLevel)level).sendParticles(new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid), location.x(), location.y(), location.z(), 10, 0.1, 0.2, 0.1, 0.2);
    }
  }

  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    if (!tool.isBroken() && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty()) {
        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
        if (recipe.hasBlockEffects()) {
          Player player = context.getPlayer();
          Level world = context.getLevel();

          // cooldown based on draw speed, works similarly enough to attack speed
          // applied before we do the effect to block recursive calls, notably ender might cause that
          if (player != null) {
            player.getCooldowns().addCooldown(tool.getItem(), (int)(20 / ConditionalStatModifierHook.getModifiedStat(tool, player, ToolStats.DRAW_SPEED)));
          }

          if (!context.getLevel().isClientSide) {
            float level = modifier.getEffectiveLevel();
            int numTargets = 0;
            BlockHitResult hit = context.getHitResult();
            BlockState state = world.getBlockState(hit.getBlockPos());
            int consumed = recipe.applyToBlock(fluid, level, FluidEffectContext.builder(world).user(player).block(hit), FluidAction.EXECUTE);
            if (consumed > 0) {
              numTargets++;
              spawnParticles(world, hit, fluid);
              fluid.shrink(consumed);
            }

            // AOE selection logic, get boosted from expanded
            if (!fluid.isEmpty()) {
              for (BlockPos offset : tool.getHook(ToolHooks.AOE_ITERATOR).getBlocks(tool, context, state, AOEMatchType.TRANSFORM)) {
                BlockHitResult offsetHit = Util.offset(hit, offset);
                consumed = recipe.applyToBlock(fluid, level, FluidEffectContext.builder(world).user(player).block(offsetHit), FluidAction.EXECUTE);
                if (consumed > 0) {
                  numTargets++;
                  spawnParticles(world, offsetHit, fluid);
                  fluid.shrink(consumed);
                  // stop if we run out of fluid
                  if (fluid.isEmpty()) {
                    break;
                  }
                }
              }
            }

            // update fluid in tool and damage tool
            if (player == null || !player.isCreative() ) {
              if (numTargets > 0) {
                TANK_HELPER.setFluid(tool, fluid);
              }

              // damage the tool, we charge for the multiplier and for the number of targets hit
              ItemStack stack = context.getItemInHand();
              if (ToolDamageUtil.damage(tool, Mth.ceil(numTargets * level), player, stack) && player != null) {
                player.broadcastBreakEvent(source.getSlot(context.getHand()));
              }
            }
          }
          return InteractionResult.SUCCESS;
        }
      }
    }
    return InteractionResult.PASS;
  }
}
