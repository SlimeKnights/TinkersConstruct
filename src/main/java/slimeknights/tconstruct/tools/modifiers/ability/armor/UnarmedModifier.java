package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.world.entity.EquipmentSlot.Type;
import slimeknights.mantle.util.OffhandCooldownTracker;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.context.ToolRebuildContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modifiers.ability.tool.OffhandAttackModifier;

public class UnarmedModifier extends OffhandAttackModifier {
  @Override
  public void addToolStats(ToolRebuildContext context, int level, ModifierStatsBuilder builder) {
    ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 0.4f);
    // main hand has 4.0 attack speed, so make the offhand have that too
    ToolStats.ATTACK_SPEED.add(builder, 4.0 - context.getBaseStats().get(ToolStats.ATTACK_SPEED));
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }

  @Override
  public void onEquip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot().getType() == Type.ARMOR) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setEnabled(true));
      ModifierUtil.addTotalArmorModifierLevel(tool, context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, 1);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, int level, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot().getType() == Type.ARMOR) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setEnabled(false));
      ModifierUtil.addTotalArmorModifierLevel(tool, context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, -1);
    }
  }
}
