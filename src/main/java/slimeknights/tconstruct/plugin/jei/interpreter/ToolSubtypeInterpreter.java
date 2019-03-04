package slimeknights.tconstruct.plugin.jei.interpreter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import slimeknights.tconstruct.library.utils.TagUtil;

// Handles all Tinker tool parts subtypes
public class ToolSubtypeInterpreter implements ISubtypeInterpreter {

  @Override
  public String apply(ItemStack itemStack) {
    StringBuilder builder = new StringBuilder();
    builder.append(itemStack.getItemDamage());

    // just pull the list of materials from the NBT, no need to convert to materials then back again
    NBTTagList materials = TagUtil.getBaseMaterialsTagList(itemStack);
    if(materials.getTagType() == TagUtil.TAG_TYPE_STRING) {
      builder.append(':');
      for(int i = 0; i < materials.tagCount(); i++) {
        // looks nicer if there is no comma at the start
        if(i != 0) {
          builder.append(',');
        }
        builder.append(materials.getStringTagAt(i));
      }
    }

    return builder.toString();
  }

}
