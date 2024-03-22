package slimeknights.tconstruct.library.modifiers.modules.unserializable;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierHookProvider;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Hook to cure effects using the worn item when its unequipped. Not enabled for composable simply because there is no benefit in JSON even if serialization is trivial. */
@RequiredArgsConstructor
public enum CureOnRemovalModule implements ModifierHookProvider, EquipmentChangeModifierHook {
  HELMET(EquipmentSlot.HEAD),
  CHESTPLATE(EquipmentSlot.CHEST),
  LEGGINGS(EquipmentSlot.LEGS),
  BOOT(EquipmentSlot.FEET);

  private static final List<ModifierHook<?>> DEFAULT_HOOKS = ModifierModule.<CureOnRemovalModule>defaultHooks(TinkerHooks.EQUIPMENT_CHANGE);

  private final EquipmentSlot slot;

  @Override
  public List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == slot) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(modifier.getModifier()) == 0 || replacement.getItem() != tool.getItem()) {
        // cure effects using the helmet
        context.getEntity().curePotionEffects(new ItemStack(tool.getItem()));
      }
    }
  }
}
