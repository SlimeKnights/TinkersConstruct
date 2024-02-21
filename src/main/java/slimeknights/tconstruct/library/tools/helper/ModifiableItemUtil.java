package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.Multimap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import slimeknights.tconstruct.library.modifiers.hook.behavior.AttributesModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InventoryTickModifierHook;
import slimeknights.tconstruct.library.tools.item.ModifiableItem;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** @deprecated all utilities have been relocated */
@Deprecated
public class ModifiableItemUtil {
  private ModifiableItemUtil() {}

  /** @deprecated use {@link AttributesModifierHook#getHeldAttributeModifiers(IToolStackView, EquipmentSlot)} */
  @Deprecated
  public static Multimap<Attribute,AttributeModifier> getMeleeAttributeModifiers(IToolStackView tool, EquipmentSlot slot) {
    return AttributesModifierHook.getHeldAttributeModifiers(tool, slot);
  }

  /** @deprecated use {@link ModifiableItem#shouldCauseReequip(ItemStack, ItemStack, boolean)} */
  @Deprecated
  public static boolean shouldCauseReequip(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return ModifiableItem.shouldCauseReequip(oldStack, newStack, slotChanged);
  }

  /** @deprecated use {@link InventoryTickModifierHook#heldInventoryTick(ItemStack, Level, Entity, int, boolean)} */
  @Deprecated
  public static void heldInventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    InventoryTickModifierHook.heldInventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
  }
}
