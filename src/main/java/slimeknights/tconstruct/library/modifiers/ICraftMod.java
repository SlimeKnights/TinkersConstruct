package slimeknights.tconstruct.library.modifiers;

import net.minecraft.item.ItemStack;

public interface ICraftMod {
  String getIdentifier();
  boolean canApply(ItemStack original, ItemStack[] inputs, int[] openSlots);
  void apply(ItemStack original, ItemStack output);
}
