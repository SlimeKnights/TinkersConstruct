package tconstruct.library.tinkering;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tconstruct.library.TinkerRegistry;
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

  /**
   * Builds an Itemstack of this tool with the given materials, if applicable.
   * @param stacks Items to build with. Have to be in the correct order. No nulls!
   * @return The built item or null if invalid input.
   */
  public ItemStack buildItemFromStacks(ItemStack[] stacks) {
    Material[] materials = new Material[stacks.length];
    // not a valid part arrangement for tis tool
    for (int i = 0; i < stacks.length; i++) {
      if (!validComponent(i, stacks[i])) {
        return null;
      }

      materials[i] = TinkerUtil.getMaterialFromStack(stacks[i]);
    }

    return buildItem(materials);
  }

  /**
   * Builds an Itemstack of this tool with the given materials.
   * @param materials Materials to build with. Have to be in the correct order. No nulls!
   * @return The built item or null if invalid input.
   */
  public ItemStack buildItem(Material[] materials) {
    ItemStack tool = new ItemStack(this);
    NBTTagCompound basetag = new NBTTagCompound();
    NBTTagCompound toolTag = buildTag(materials);
    NBTTagCompound dataTag = buildData(materials);

    basetag.setTag(getTagName(), toolTag);
    basetag.setTag(Tags.TINKER_DATA, dataTag);
    tool.setTagCompound(basetag);

    return tool;
  }

  /**
   * Creates an NBT Tag with the materials that were used to build the item.
   */
  private NBTTagCompound buildData(Material[] materials) {
    NBTTagCompound tag = new NBTTagCompound();
    for(int i = 0; i < materials.length; i++)
      tag.setString(String.valueOf(i), materials[i].identifier);

    return tag;
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
