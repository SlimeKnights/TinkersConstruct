package slimeknights.tconstruct.plugin.jei.interpreter;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.tools.Pattern;

// Handles pattern and cast subtypes
public class PatternSubtypeInterpreter implements ISubtypeInterpreter {

  @Override
  public String getSubtypeInfo(ItemStack stack) {
    String meta = stack.getItemDamage() + ":";

    Item part = Pattern.getPartFromTag(stack);
    if(part == null) {
      return meta;
    }

    return meta + part.getRegistryName();
  }

}
