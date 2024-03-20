package slimeknights.tconstruct.library.modifiers.hook.combat;

import net.minecraft.world.entity.EquipmentSlot;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.context.LootingContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;

public interface ArmorLootingModifierHook {
  /**
   * Gets the amount to boost the tool's luck by
   * @param tool          Tool instance
   * @param modifier      Modifier entry calling the hook
   * @param context       Looting context
   * @param equipment     Other equipment available to the holder
   * @param slot          Slot being queried for looting
   * @param looting       Looting value set from previous modifiers
   * @return New looting value
   */
  int updateArmorLooting(IToolStackView tool, ModifierEntry modifier, LootingContext context, EquipmentContext equipment, EquipmentSlot slot, int looting);


  /**
   * Gets the looting bonuses from armor
   * @param tool      Tool who triggered looting, null if a projectile caused looting
   * @param context   Looting context
   * @param looting   Looting from before this hook, may be negative
   * @return  Updated looting value, may be negative
   */
  static int getLooting(@Nullable IToolStackView tool, LootingContext context, int looting) {
    // first, build the context
    EquipmentContext equipment;
    EquipmentSlot lootingSlot = context.getLootingSlot();
    if (tool != null && lootingSlot != null) {
      equipment = EquipmentContext.withTool(context.getHolder(), tool, lootingSlot);
    } else {
      equipment = new EquipmentContext(context.getHolder());
    }
    // no extra setup work, so we can skip the pre-validation and go right to boosting
    for (EquipmentSlot slot : EquipmentSlot.values()) {
      // TODO: slot is null for bows, what if a bow has a modifier that implements both hooks? maybe bows should set looting slot and we ignore it?
      if (slot != lootingSlot) {
        IToolStackView armor = equipment.getValidTool(slot);
        if (armor != null) {
          for (ModifierEntry entry : armor.getModifierList()) {
            looting = entry.getHook(TinkerHooks.ARMOR_LOOTING).updateArmorLooting(armor, entry, context, equipment, slot, looting);
          }
        }
      }
    }
    return looting;
  }


  /** Merger that runs each hook after the previous */
  record ComposeMerger(Collection<ArmorLootingModifierHook> modules) implements ArmorLootingModifierHook {
    @Override
    public int updateArmorLooting(IToolStackView tool, ModifierEntry modifier, LootingContext context, EquipmentContext equipment, EquipmentSlot slot, int looting) {
      for (ArmorLootingModifierHook module : modules) {
        looting = module.updateArmorLooting(tool, modifier, context, equipment, slot, looting);
      }
      return looting;
    }
  }
}
