package slimeknights.tconstruct.tools.modules;

import lombok.Getter;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.common.util.LazyOptional;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.mapping.SimpleRecordLoadable;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.UsingToolModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modifiers.ability.interaction.BlockingModifier;

import java.util.List;

/**
 * Shared logic for {@link slimeknights.tconstruct.tools.data.ModifierIds#scope} and {@link slimeknights.tconstruct.tools.data.ModifierIds#zoom}.
 * TODO 1.21: move to {@link slimeknights.tconstruct.tools.modules.interaction}
 */
public enum ZoomModule implements ModifierModule, GeneralInteractionModifierHook, KeybindInteractModifierHook, UsingToolModifierHook, EquipmentChangeModifierHook {
  SPYGLASS(ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING, ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.ARMOR_INTERACT) {
    @Override
    public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity living, int useDuration, int timeLeft, ModifierEntry activeModifier) {
      // running on first usage tick means zoom will work if another modifier clicks as well
      if (timeLeft == useDuration) {
        living.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
        if (living.level().isClientSide) {
          setZoom(modifier, living, 0.1f);
        }
      }
    }
  },
  SCOPE(ModifierHooks.GENERAL_INTERACT, ModifierHooks.TOOL_USING, ModifierHooks.EQUIPMENT_CHANGE) {
    @Override
    public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
      if (entity.level().isClientSide) {
        int useTime = useDuration - timeLeft;
        if (useTime > 0) {
          float drawTime = tool.getPersistentData().getInt(GeneralInteractionModifierHook.KEY_DRAWTIME);
          if (drawTime <= 0) {
            drawTime = 20;
          }
          float fov = 1 - (0.6f * Math.min(useTime / drawTime, 1));
          setZoom(modifier, entity, fov);
        }
      }
    }
  };

  /** Loader instance */
  public static final RecordLoadable<ZoomModule> LOADER = new SimpleRecordLoadable<>(new EnumLoadable<>(ZoomModule.class), "style", null, false);

  @Getter
  private final List<ModuleHook<?>> defaultHooks;

  @SafeVarargs
  ZoomModule(ModuleHook<? super ZoomModule>... hooks) {
    defaultHooks = List.of(hooks);
  }

  @Override
  public Integer getPriority() {
    return 10; // just let everyone else go first
  }

  @Override
  public RecordLoadable<ZoomModule> getLoader() {
    return LOADER;
  }


  /* Helpers */

  /** Starts spyglass style zooming */
  private static void setZoom(ModifierEntry modifier, LivingEntity living, float amount) {
    living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(modifier.getId(), amount));
  }

  /** Stops zooming */
  private static void stopZoom(ModifierEntry modifier, LazyOptional<TinkerDataCapability.Holder> tinkerData) {
    tinkerData.ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(modifier.getId()));
  }

  /** Stops zooming */
  private static void stopZoom(ModifierEntry modifier, LivingEntity entity) {
    stopZoom(modifier, entity.getCapability(TinkerDataCapability.CAPABILITY));
  }


  /* Start zooming */

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.RIGHT_CLICK) {
      GeneralInteractionModifierHook.startUsing(tool, modifier.getId(), player, hand);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    player.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
    if (player.level().isClientSide) {
      // TODO: consider allowing scope on helmets, is that even useful?
      setZoom(modifier, player, 0.1f);
    }
    return true;
  }


  /* Zoom properties, only used if zooming starting the interaction */

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return BlockingModifier.blockWhileCharging(tool, UseAnim.SPYGLASS);
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 1200;
  }


  /* Stop zooming */

  @Override
  public void stopInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
    player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
    if (player.level().isClientSide) {
      stopZoom(modifier, player);
    }
  }

  @Override
  public void afterStopUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int useDuration, int timeLeft, ModifierEntry activeModifier) {
    entity.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
    if (entity.level().isClientSide) {
      stopZoom(modifier, entity);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getEntity().level().isClientSide) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(modifier.getModifier()) == 0) {
        stopZoom(modifier, context.getTinkerData());
      }
    }
  }
}
