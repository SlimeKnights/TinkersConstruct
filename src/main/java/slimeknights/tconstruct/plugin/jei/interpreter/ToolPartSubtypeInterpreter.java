package slimeknights.tconstruct.plugin.jei.interpreter;

import net.minecraft.item.ItemStack;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.utils.TinkerUtil;

// Handles all Tinker tool parts subtypes
public class ToolPartSubtypeInterpreter implements ISubtypeInterpreter {

  @Override
  public String apply(ItemStack itemStack) {
    String meta = itemStack.getItemDamage() + ":";

    Material material = TinkerUtil.getMaterialFromStack(itemStack);
    if(material == null) {
      return meta;
    }

    return meta + material.getIdentifier();
  }

}
