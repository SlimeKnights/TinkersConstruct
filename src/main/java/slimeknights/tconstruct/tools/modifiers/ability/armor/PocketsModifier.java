package slimeknights.tconstruct.tools.modifiers.ability.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class PocketsModifier extends InventoryModifier implements IArmorInteractModifier {
  private static final ResourceLocation INVENTORY_KEY = TConstruct.getResource("pockets");
  public PocketsModifier() {
    super(INVENTORY_KEY, 9);
  }

  @Override
  public boolean startArmorInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot) {
    if (player.isShiftKeyDown()) {
      return ToolInventoryCapability.tryOpenContainer(player.getItemBySlot(slot), tool, player, slot).consumesAction();
    }
    return false;
  }

  @Override
  protected boolean isSelfHook(ModifierHook<?> hook) {
    return hook == ModifierHooks.ARMOR_INTERACT || super.isSelfHook(hook);
  }
}
