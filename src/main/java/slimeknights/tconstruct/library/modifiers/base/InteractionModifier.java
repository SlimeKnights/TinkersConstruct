package slimeknights.tconstruct.library.modifiers.base;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.tools.capability.TinkerDataKeys;
import slimeknights.tconstruct.library.tools.context.EquipmentChangeContext;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

/** Modifier that implements interaction abilities, set up to automatically set relevant properties for chestplates */
public class InteractionModifier extends Modifier {
  public InteractionModifier(int color) {
    super(color);
  }

  @Override
  public void onEquip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlotType.CHEST) {
      ModifierUtil.addTotalArmorModifierLevel(tool, context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, 1);
    }
  }

  @Override
  public void onUnequip(IModifierToolStack tool, int level, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlotType.CHEST) {
      ModifierUtil.addTotalArmorModifierLevel(tool, context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, -1);
    }
  }

  public static class SingleUse extends InteractionModifier {
    public SingleUse(int color) {
      super(color);
    }

    @Override
    public ITextComponent getDisplayName(int level) {
      // display name without the level
      return super.getDisplayName();
    }
  }
}
