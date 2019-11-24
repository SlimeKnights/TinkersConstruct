package slimeknights.tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;

import java.util.List;

/** Stuff needed for a modifier to be displayed in books */
public interface IModifierDisplay {

  /** Color of the modifier, used for the title header */
  int getColor();


  /**
   * List of ItemStacks possible to apply the modifier.
   * Each list entry corresponds to one item combination that can be applied
   * Example: first entry with size 1 and redstone, second entry with size 1 and redstone block,...
   */
  List<List<ItemStack>> getItems();
}
