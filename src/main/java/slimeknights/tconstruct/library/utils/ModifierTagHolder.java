package slimeknights.tconstruct.library.utils;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import slimeknights.tconstruct.library.modifiers.ModifierNBT;

public class ModifierTagHolder {

  private final ItemStack itemStack;
  private final NBTTagList tagList;
  private final int index;
  public final NBTTagCompound tag;

  private ModifierNBT modifierNBT;

  private ModifierTagHolder(ItemStack itemStack, String modifier) {
    this.itemStack = itemStack;
    this.tagList = TagUtil.getModifiersTagList(itemStack);
    this.index = TinkerUtil.getIndexInCompoundList(tagList, modifier);
    this.tag = tagList.getCompoundTagAt(index);
  }

  public <T extends ModifierNBT> T getTagData(Class<T> clazz) {
    T data = ModifierNBT.readTag(tag, clazz);
    modifierNBT = data;
    return data;
  }

  public void save() {
    if(modifierNBT != null) {
      modifierNBT.write(tag);
    }
    tagList.set(index, tag);
    TagUtil.setModifiersTagList(itemStack, tagList);
  }

  public static ModifierTagHolder getModifier(ItemStack itemStack, String modifier) {
    return new ModifierTagHolder(itemStack, modifier);
  }
}
