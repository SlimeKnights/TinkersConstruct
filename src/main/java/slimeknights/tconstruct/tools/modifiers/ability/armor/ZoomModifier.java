package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.KeybindInteractModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.TinkerDataCapability;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ZoomModifier extends NoLevelsModifier implements KeybindInteractModifierHook, GeneralInteractionModifierHook {
  private static final ResourceLocation ZOOM = TConstruct.getResource("zoom");

  @Override
  public int getPriority() {
    return 50;
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (context.getEntity().level.isClientSide) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(this) == 0) {
        context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(ZOOM));
      }
    }
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    if (player.isShiftKeyDown()) {
      player.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
      if (player.level.isClientSide()) {
        player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(ZOOM, 0.1f));
      }
      return true;
    }
    return false;
  }

  @Override
  public void stopInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
    player.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
    if (player.level.isClientSide()) {
      player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(ZOOM));
    }
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (source == InteractionSource.RIGHT_CLICK) {
      player.playSound(SoundEvents.SPYGLASS_USE, 1.0F, 1.0F);
      player.startUsingItem(hand);
      if (player.level.isClientSide) {
        player.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(ZOOM, 0.1f));
      }
      tool.getPersistentData().putBoolean(ZOOM, true);
      return InteractionResult.CONSUME;
    }
    return InteractionResult.PASS;
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return tool.getPersistentData().getBoolean(ZOOM) ? UseAnim.SPYGLASS : UseAnim.NONE;
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return tool.getPersistentData().getBoolean(ZOOM) ? 1200 : 0;
  }

  @Override
  public boolean onFinishUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity) {
    if (tool.getPersistentData().getBoolean(ZOOM)) {
      entity.playSound(SoundEvents.SPYGLASS_STOP_USING, 1.0F, 1.0F);
      if (entity.level.isClientSide) {
        entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(ZOOM));
      }
      tool.getPersistentData().remove(ZOOM);
      return true;
    }
    return false;
  }

  @Override
  public boolean onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    return onFinishUsing(tool, modifier, entity);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.ARMOR_INTERACT, TinkerHooks.GENERAL_INTERACT);
  }
}
