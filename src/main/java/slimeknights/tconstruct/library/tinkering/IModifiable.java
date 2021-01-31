package slimeknights.tconstruct.library.tinkering;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import java.util.List;

public interface IModifiable extends ITinkerable {
  List<ITextComponent> getTraits(ItemStack stack);
}
