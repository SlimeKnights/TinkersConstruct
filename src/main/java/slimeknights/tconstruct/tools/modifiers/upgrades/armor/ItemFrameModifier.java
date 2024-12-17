package slimeknights.tconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.capability.inventory.InventoryModule;
import slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** @deprecated use {@link InventoryModule} */
@Deprecated(forRemoval = true)
public class ItemFrameModifier extends Modifier {
  /** Pattern and inventory key */
  private static final Pattern ITEM_FRAME = new Pattern(TConstruct.MOD_ID, "item_frame");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(InventoryModule.builder().pattern(ITEM_FRAME).flatLimit(1).slotsPerLevel(1));
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook#getAllStacks(IToolStackView, ModifierEntry, List)} */
  @Deprecated(forRemoval = true)
  public void getAllStacks(IToolStackView tool, ModifierEntry entry, List<ItemStack> stackList) {
    entry.getHook(ToolInventoryCapability.HOOK).getAllStacks(tool, entry, stackList);
  }
}
