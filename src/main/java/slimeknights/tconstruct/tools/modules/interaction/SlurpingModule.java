package slimeknights.tconstruct.tools.modules.interaction;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.LevelingInt;
import slimeknights.tconstruct.library.json.LevelingValue;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;

import javax.annotation.Nullable;
import java.util.List;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;
import static slimeknights.tconstruct.library.tools.helper.ModifierUtil.asPlayer;

/** Modifier that after a short time applies a fluid effect to the holder. */
public record SlurpingModule(LevelingValue strength, LevelingInt duration) implements ModifierModule, GeneralInteractionModifierHook, UsingToolModifierHook, KeybindInteractModifierHook, InventoryTickModifierHook, EquipmentChangeModifierHook {
  private static final float DEGREE_TO_RADIANS = (float)Math.PI / 180F;
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SlurpingModule>defaultHooks(ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING, ModifierHooks.ARMOR_INTERACT, ModifierHooks.INVENTORY_TICK, ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<SlurpingModule> LOADER = RecordLoadable.create(
    LevelingValue.LOADABLE.requiredField("strength", SlurpingModule::strength),
    LevelingInt.LOADABLE.requiredField("duration", SlurpingModule::duration),
    SlurpingModule::new);

  @Override
  public RecordLoadable<SlurpingModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  /* Helpers */

  /** Checks if we can slurp the given fluid */
  private int slurp(FluidStack fluid, ModifierEntry entry, LivingEntity entity, @Nullable Player player, FluidAction action) {
    if (!fluid.isEmpty()) {
      FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
      return recipe.hasEntityEffects() ? recipe.applyToEntity(fluid, strength.compute(entry), FluidEffectContext.builder(entity.level()).user(entity, player).target(entity), action) : 0;
    }
    return 0;
  }

  /** Drinks some of the fluid in the tank, reducing its value and spawning particles and playing sounds */
  private void finishDrinking(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, boolean playSound) {
    FluidStack fluid = TANK_HELPER.getFluid(tool);
    if (!fluid.isEmpty()) {
      // sound and particles
      if (playSound) {
        entity.playSound(SoundEvents.GENERIC_DRINK, 0.5F, entity.getRandom().nextFloat() * 0.1f + 0.9f);
      }
      addFluidParticles(entity, fluid, 16);
      // apply effect
      if (!entity.level().isClientSide) {
        Player player = asPlayer(entity);
        int consumed = slurp(fluid, modifier, entity, player, FluidAction.EXECUTE);
        if (consumed > 0 && (player == null || !player.isCreative())) {
          fluid.shrink(consumed);
          TANK_HELPER.setFluid(tool, fluid);
        }
      }
    }
  }

  /** Adds the given number of fluid particles */
  private static void addFluidParticles(LivingEntity entity, FluidStack fluid, int count) {
    for(int i = 0; i < count; ++i) {
      RandomSource random = entity.getRandom();
      Vec3 motion = new Vec3((random.nextFloat() - 0.5D) * 0.1D, random.nextFloat() * 0.1D + 0.1D, 0.0D);
      motion = motion.xRot(-entity.getXRot() * DEGREE_TO_RADIANS);
      motion = motion.yRot(-entity.getYRot() * DEGREE_TO_RADIANS);
      Vec3 position = new Vec3((random.nextFloat() - 0.5D) * 0.3D, (-random.nextFloat()) * 0.6D - 0.3D, 0.6D);
      position = position.xRot(-entity.getXRot() * DEGREE_TO_RADIANS);
      position = position.yRot(-entity.getYRot() * DEGREE_TO_RADIANS);
      position = position.add(entity.getX(), entity.getEyeY(), entity.getZ());
      FluidParticleData data = new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid);
      Level level = entity.level();
      if (level instanceof ServerLevel serverLevel) {
        serverLevel.sendParticles(data, position.x, position.y, position.z, 1, motion.x, motion.y + 0.05D, motion.z, 0.0D);
      } else {
        level.addParticle(data, position.x, position.y, position.z, motion.x, motion.y + 0.05D, motion.z);
      }
    }
  }


  /* Held */

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.RIGHT_CLICK) {
      if (slurp(TANK_HELPER.getFluid(tool), modifier, player, player, FluidAction.SIMULATE) > 0) {
        GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
        return InteractionResult.CONSUME;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return duration.compute(modifier.getEffectiveLevel());
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return UseAnim.DRINK;
  }

  @Override
  public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    // mark the tool with whether we can drink; don't play particles if simulate does nothing
    int useTime = useDuration - timeLeft;
    boolean notActive = modifier != activeModifier;
    if (notActive && useTime == 0) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty() && slurp(fluid, modifier, entity, asPlayer(entity), FluidAction.SIMULATE) > 0) {
        tool.getPersistentData().putBoolean(modifier.getId(), true);
      }
    }

    // if we reached the end, finish drinking; don't have to release the current use
    int duration = getUseDuration(tool, modifier);
    if (notActive && useTime == duration) {
      finishDrinking(tool, modifier, entity, true);
      tool.getPersistentData().remove(modifier.getId());
    }
    // if we have not finished drinking, and we can drink, play effects
    else if (useTime < duration && useTime % 4 == 0 && (!notActive || tool.getPersistentData().getBoolean(modifier.getId()))) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty()) {
        addFluidParticles(entity, fluid, 5);
        // add drinking sounds if blocking or using another modifier
        if (notActive) {
          entity.playSound(SoundEvents.GENERIC_DRINK, 0.5F, entity.getRandom().nextFloat() * 0.1f + 0.9f);
        }
      }
    }
  }

  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    if (useDuration - timeLeft == getUseDuration(tool, modifier)) {
      finishDrinking(tool, modifier, entity, modifier != activeModifier);
    }
    tool.getPersistentData().remove(modifier.getId());
  }


  /* Armor */

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    if (keyModifier == TooltipKey.NORMAL) {
      if (slurp(TANK_HELPER.getFluid(tool), modifier, player, player, FluidAction.SIMULATE) > 0) {
        tool.getPersistentData().putInt(modifier.getId(), player.tickCount + duration.compute(modifier.getEffectiveLevel()));
        return true;
      }
    }
    return false;
  }

  @Override
  public void stopInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
    tool.getPersistentData().remove(modifier.getId());
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    IToolStackView replacement = context.getReplacementTool();
    // modifier list changing is a good heuristic for tool changing, avoids deleting during the slurp
    Level level = context.getLevel();
    if (!level.isClientSide && (replacement == null || replacement.getItem() != tool.getItem() || !replacement.getModifiers().equals(tool.getModifiers()))) {
      tool.getPersistentData().remove(modifier.getId());
    }
  }

  @Override
  public void onInventoryTick(IToolStackView tool, ModifierEntry modifier, Level world, LivingEntity holder, int itemSlot, boolean isSelected, boolean isCorrectSlot, ItemStack stack) {
    if (isCorrectSlot && tool.hasTag(TinkerTags.Items.WORN_ARMOR)) {
      ModDataNBT persistentData = tool.getPersistentData();
      int finishTime = persistentData.getInt(modifier.getId());
      if (finishTime > 0) {
        // how long we have left?
        int timeLeft = finishTime - holder.tickCount;
        if (timeLeft < 0) {
          // particles a bit stronger
          finishDrinking(tool, modifier, holder, true);

          // stop drinking
          persistentData.remove(modifier.getId());
        }
        // sound is only every 4 ticks
        else if (timeLeft % 4 == 0) {
          holder.playSound(SoundEvents.GENERIC_DRINK, 0.5F, holder.getRandom().nextFloat() * 0.1f + 0.9f);
          addFluidParticles(holder, TANK_HELPER.getFluid(tool), 5);
        }
      }
    }
  }
}
