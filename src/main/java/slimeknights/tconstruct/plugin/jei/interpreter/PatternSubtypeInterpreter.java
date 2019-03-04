package slimeknights.tconstruct.plugin.jei.interpreter;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import slimeknights.tconstruct.library.tools.Pattern;

// Handles pattern and cast subtypes
public class PatternSubtypeInterpreter implements ISubtypeInterpreter {

  @Override
  public String apply(ItemStack itemStack) {
    String meta = itemStack.getItemDamage() + ":";

    Item part = Pattern.getPartFromTag(itemStack);
    if(part == null) {
      return meta;
    }

    return meta + part.getRegistryName();
  }

}
