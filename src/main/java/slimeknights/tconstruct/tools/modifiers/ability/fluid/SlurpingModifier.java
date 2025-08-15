package slimeknights.tconstruct.tools.modifiers.ability.fluid;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectManager;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffects;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.StatBoostModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.particle.FluidParticleData;

import static slimeknights.tconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

/** Modifier to handle spilling recipes on helmets */
public class SlurpingModifier extends Modifier implements KeybindInteractModifierHook, GeneralInteractionModifierHook, UsingToolModifierHook {
  private static final float DEGREE_TO_RADIANS = (float)Math.PI / 180F;
  private static final TinkerDataKey<SlurpingInfo> SLURP_FINISH_TIME = TConstruct.createKey("slurping_finish");

  public SlurpingModifier() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, PlayerTickEvent.class, this::playerTick);
  }

  @Override
  public int getPriority() {
    return 40;
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ToolTankHelper.TANK_HANDLER);
    hookBuilder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
    hookBuilder.addHook(this, ModifierHooks.ARMOR_INTERACT, ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING);
  }

  /** Checks if we can slurp the given fluid */
  private int slurp(FluidStack fluid, float level, Player player, FluidAction action) {
    if (!fluid.isEmpty()) {
      FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
      return recipe.hasEntityEffects() ? recipe.applyToEntity(fluid, level, FluidEffectContext.builder(player.level()).user(player).target(player), action) : 0;
    }
    return 0;
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    if (keyModifier == TooltipKey.NORMAL) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (slurp(fluid, modifier.getEffectiveLevel(), player, FluidAction.SIMULATE) > 0) {
        player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.put(SLURP_FINISH_TIME, new SlurpingInfo(fluid, player.tickCount + 20)));
        return true;
      }
    }
    return false;
  }

  /** Adds the given number of fluid particles */
  private static void addFluidParticles(Player player, FluidStack fluid, int count) {
    for(int i = 0; i < count; ++i) {
      Vec3 motion = new Vec3((RANDOM.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
      motion = motion.xRot(-player.getXRot() * DEGREE_TO_RADIANS);
      motion = motion.yRot(-player.getYRot() * DEGREE_TO_RADIANS);
      Vec3 position = new Vec3((RANDOM.nextFloat() - 0.5D) * 0.3D, (-RANDOM.nextFloat()) * 0.6D - 0.3D, 0.6D);
      position = position.xRot(-player.getXRot() * DEGREE_TO_RADIANS);
      position = position.yRot(-player.getYRot() * DEGREE_TO_RADIANS);
      position = position.add(player.getX(), player.getEyeY(), player.getZ());
      FluidParticleData data = new FluidParticleData(TinkerCommons.fluidParticle.get(), fluid);
      Level level = player.level();
      if (level instanceof ServerLevel serverLevel) {
        serverLevel.sendParticles(data, position.x, position.y, position.z, 1, motion.x, motion.y + 0.05D, motion.z, 0.0D);
      } else {
        level.addParticle(data, position.x, position.y, position.z, motion.x, motion.y + 0.05D, motion.z);
      }
    }
  }

  /** Drinks some of the fluid in the tank, reducing its value */
  private void finishDrinking(IToolStackView tool, Player player) {
    // only server needs to drink
    if (!player.level().isClientSide) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      int consumed = slurp(fluid, tool.getModifier(this).getEffectiveLevel(), player, FluidAction.EXECUTE);
      if (!player.isCreative() && consumed > 0) {
        fluid.shrink(consumed);
        TANK_HELPER.setFluid(tool, fluid);
      }
    }
  }

  /** Called on player tick to update drinking */
  private void playerTick(PlayerTickEvent event) {
    Player player = event.player;
    if (player.isSpectator()) {
      return;
    }
    player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
      // if drinking
      SlurpingInfo info = data.get(SLURP_FINISH_TIME);
      if (info != null) {
        // how long we have left?
        int timeLeft = info.finishTime - player.tickCount;
        if (timeLeft < 0) {
          // particles a bit stronger
          player.playSound(SoundEvents.GENERIC_DRINK, 0.5F, RANDOM.nextFloat() * 0.1f + 0.9f);
          addFluidParticles(player, info.fluid, 16);
          finishDrinking(ToolStack.from(player.getItemBySlot(EquipmentSlot.HEAD)), player);

          // stop drinking
          data.remove(SLURP_FINISH_TIME);
        }
        // sound is only every 4 ticks
        else if (timeLeft % 4 == 0) {
          player.playSound(SoundEvents.GENERIC_DRINK, 0.5F, RANDOM.nextFloat() * 0.1f + 0.9f);
          addFluidParticles(player, info.fluid, 5);
        }
      }
    });
  }

  @Override
  public void stopInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
    player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.remove(SLURP_FINISH_TIME));
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.RIGHT_CLICK) {
      if (slurp(TANK_HELPER.getFluid(tool), modifier.getEffectiveLevel(), player, FluidAction.SIMULATE) > 0) {
        GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
        return InteractionResult.CONSUME;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 21;
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
    if (notActive && useTime == 0 && entity instanceof Player player) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty() && slurp(fluid, modifier.getEffectiveLevel(), player, FluidAction.SIMULATE) > 0) {
        tool.getPersistentData().putBoolean(getId(), true);
      }
    }

    // if we reached the end, finish drinking; don't have to release the current use
    int duration = getUseDuration(tool, modifier);
    if (notActive && useTime == duration) {
      if (entity instanceof Player player) {
        finishDrinking(tool, player);
      }
      tool.getPersistentData().remove(getId());
    }
    // if we have not finished drinking, and we can drink, play effects
    else if (useTime < duration && useTime % 4 == 0 && (!notActive || tool.getPersistentData().getBoolean(getId())) && entity instanceof Player player) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty()) {
        addFluidParticles(player, fluid, 5);
        // add drinking sounds if blocking or using another modifier
        if (notActive) {
          player.playSound(SoundEvents.GENERIC_DRINK, 0.5F, RANDOM.nextFloat() * 0.1f + 0.9f);
        }
      }
    }
  }

  @Override
  public void beforeReleaseUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    if (useDuration - timeLeft == getUseDuration(tool, modifier) && entity instanceof Player player) {
      finishDrinking(tool, player);
    }
    tool.getPersistentData().remove(getId());
  }

  private record SlurpingInfo(FluidStack fluid, int finishTime) {}
}
