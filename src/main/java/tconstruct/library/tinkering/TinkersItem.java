package tconstruct.library.tinkering;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collections;
import java.util.List;

import tconstruct.library.utils.Tags;
import tconstruct.library.utils.TinkerUtil;

/**
 * The base for each Tinker tool.
 */
public abstract class TinkersItem extends Item implements ITinkerable, IModifyable {

  public final PartMaterialWrapper[] requiredComponents;

  public TinkersItem(PartMaterialWrapper... requiredComponents) {
    this.requiredComponents = requiredComponents;

    this.setMaxStackSize(1);
  }

  /* Building the Item */
  public boolean validComponent(int slot, ItemStack stack) {
    if (slot > requiredComponents.length || slot < 0) {
      return false;
    }

    return requiredComponents[slot].isValid(stack);
  }

  public ItemStack buildItem(ItemStack[] stacks) {
    Material[] materials = new Material[stacks.length];
    // not a valid part arrangement for tis tool
    for (int i = 0; i < stacks.length; i++) {
      if (!validComponent(i, stacks[i])) {
        return null;
      }

      materials[i] = TinkerUtil.getMaterialFromStack(stacks[i]);
    }

    ItemStack tool = new ItemStack(this);
    NBTTagCompound basetag = new NBTTagCompound();
    NBTTagCompound toolTag = buildTag(materials);

    basetag.setTag(getTagName(), toolTag);
    tool.setTagCompound(basetag);

    return tool;
  }

  protected abstract NBTTagCompound buildTag(Material[] materials);

  @Override
  public String getTagName() {
    return Tags.TOOL_BASE;
  }

  /* Information */

  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip,
                             boolean advanced) {
    Collections.addAll(tooltip, this.getInformation(stack));
  }
}
