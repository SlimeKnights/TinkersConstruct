package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.ability.tool.OffhandAttackModifier;

// TODO: rename in 1.19
public class AmbidextrousModifier extends OffhandAttackModifier implements EquipmentChangeModifierHook, ToolStatsModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.EQUIPMENT_CHANGE, TinkerHooks.TOOL_STATS);
    hookBuilder.addModule(ShowOffhandModule.DISALLOW_BROKEN);
  }

  @Override
  public void addToolStats(ToolRebuildContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    // main hand has 4.0 attack speed, so make the offhand have that too
    ToolStats.ATTACK_SPEED.add(builder, 4.0 - context.getBaseStats().get(ToolStats.ATTACK_SPEED));
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot() == EquipmentSlot.CHEST) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setEnabled(true));
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot() == EquipmentSlot.CHEST) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setEnabled(false));
    }
  }
}
