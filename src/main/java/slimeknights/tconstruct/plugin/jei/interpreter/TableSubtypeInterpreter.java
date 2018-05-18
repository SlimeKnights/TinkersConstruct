package slimeknights.tconstruct.plugin.jei.interpreter;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import mezz.jei.api.ISubtypeRegistry.ISubtypeInterpreter;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.shared.tileentity.TileTable;

// Hanldes table and rack subtypes
public class TableSubtypeInterpreter implements ISubtypeInterpreter {

  @Override
  public String apply(ItemStack itemStack) {
    // we have to handle the metadata here
    String meta = itemStack.getMetadata() + ":";

    // if the legs exist, return that for the identification key
    NBTTagCompound tag = TagUtil.getTagSafe(itemStack).getCompoundTag(TileTable.FEET_TAG);
    ItemStack legs = new ItemStack(tag);
    if(!legs.isEmpty()) {
      return meta + legs.getItem().getRegistryName() + ":" + legs.getMetadata();
    }

    // otherwise, simply go back to the meta
    return meta;
  }

}
