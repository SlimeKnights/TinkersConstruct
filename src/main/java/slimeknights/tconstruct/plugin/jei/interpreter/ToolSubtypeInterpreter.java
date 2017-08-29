package slimeknights.tconstruct.plugin.jei.interpreter;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import slimeknights.tconstruct.library.utils.TagUtil;

// Handles all Tinker tool parts subtypes
public class ToolSubtypeInterpreter implements ISubtypeInterpreter {

  @Override
  public String getSubtypeInfo(ItemStack stack) {
    StringBuilder builder = new StringBuilder();
    builder.append(stack.getItemDamage());

    // just pull the list of materials from the NBT, no need to convert to materials then back again
    NBTTagList materials = TagUtil.getBaseMaterialsTagList(stack);
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
