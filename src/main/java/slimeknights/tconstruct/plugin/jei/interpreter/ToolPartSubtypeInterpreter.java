package slimeknights.tconstruct.plugin.jei.interpreter;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.utils.TinkerUtil;

// Handles all Tinker tool parts subtypes
public class ToolPartSubtypeInterpreter implements ISubtypeInterpreter {

  @Override
  public String getSubtypeInfo(ItemStack stack) {
    String meta = stack.getItemDamage() + ":";

    Material material = TinkerUtil.getMaterialFromStack(stack);
    if(material == null) {
      return meta;
    }

    return meta + material.getIdentifier();
  }

}
