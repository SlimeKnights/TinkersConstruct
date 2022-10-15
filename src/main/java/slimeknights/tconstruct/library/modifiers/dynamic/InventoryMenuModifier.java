package slimeknights.tconstruct.library.modifiers.dynamic;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.tconstruct.library.json.serializer.GenericIntSerializer;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.hooks.IArmorInteractModifier;
import slimeknights.tconstruct.library.modifiers.impl.InventoryModifier;
import slimeknights.tconstruct.library.tools.capability.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class InventoryMenuModifier extends InventoryModifier implements IArmorInteractModifier {
  /** Loader instance */
  public static final GenericIntSerializer<InventoryMenuModifier> LOADER = new GenericIntSerializer<>("size", InventoryMenuModifier::new, t -> t.slotsPerLevel);

  public InventoryMenuModifier(int size) {
    super(size);
  }

  public InventoryMenuModifier(ResourceLocation key, int size) {
    super(key, size);
  }

  @Override
  public int getPriority() {
    return 75; // run latest so the keybind does not prevent shield strap or tool belt
  }

  @Override
  public boolean startArmorInteract(IToolStackView tool, int level, Player player, EquipmentSlot slot, TooltipKey modifier) {
    return ToolInventoryCapability.tryOpenContainer(player.getItemBySlot(slot), tool, player, slot).consumesAction();
  }

  @SuppressWarnings("unchecked")
  @Nullable
  @Override
  public <T> T getModule(Class<T> type) {
    if (type == IArmorInteractModifier.class) {
      return (T) this;
    }
    return super.getModule(type);
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }
}
