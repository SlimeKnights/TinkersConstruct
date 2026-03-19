package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.EntityInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.interaction.ShearsModule;

import javax.annotation.Nullable;

/** @deprecated use {@link ShearsModule} */
@Deprecated(forRemoval = true)
@RequiredArgsConstructor
public class ShearsAbilityModifier extends NoLevelsModifier implements EntityInteractionModifierHook, ToolActionModifierHook {
  private final ShearsModule shears;
  @Getter
  private final int priority;

  public ShearsAbilityModifier(int range, int priority) {
    this.shears = new ShearsModule(range, 0, 1);
    this.priority = priority;
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addModule(ShowOffhandModule.DISALLOW_BROKEN);
    hookBuilder.addHook(this, ModifierHooks.ENTITY_INTERACT, ModifierHooks.TOOL_ACTION);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, @Nullable RegistryAccess access) {
    return InteractionSource.formatModifierName(tool, this, super.getDisplayName(tool, entry, access));
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }
  
  /**
   * Swings the given's player hand
   *
   * @param player the current player
   * @param hand the given hand the tool is in
   */
  @Deprecated(forRemoval = true)
  protected void swingTool(Player player, InteractionHand hand) {
    player.swing(hand);
    player.sweepAttack();
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    return isShears(tool) && shears.canPerformAction(tool, modifier, toolAction);
  }

  /**
   * Checks whether the tool counts as shears for modifier logic
   *
   * @param tool  Current tool instance
   */
  protected boolean isShears(IToolStackView tool) {
    return true;
  }

  @Override
  public InteractionResult beforeEntityUse(IToolStackView tool, ModifierEntry modifier, Player player, Entity target, InteractionHand hand, InteractionSource source) {
    if (isShears(tool)) {
      return shears.beforeEntityUse(tool, modifier, player, target, hand, source);
    }
    return InteractionResult.PASS;
  }
}
