package tconstruct.library.tinkering;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;

/**
 * Represents an item that has a Material associated with it.
 * The metadata of an itemstack identifies which material the itemstack of this item has.
 */
public class MaterialItem extends Item implements IMaterialItem {
  public MaterialItem() {
    this.setHasSubtypes(true);
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    // this adds a variant of each material to the creative menu
    for (Material mat : TinkerRegistry.getAllMaterials()) {
      subItems.add(getItemstackWithMaterial(mat));
    }
  }

  @Override
  public String getMaterialID(ItemStack stack) {
    return getMaterial(stack).identifier;
  }

  @Override
  public Material getMaterial(ItemStack stack) {
    NBTTagCompound tag = TagUtil.getTagCompoundSafe(stack);

    return TinkerRegistry.getMaterial(tag.getString(Tags.PART_MATERIAL));
  }

  public ItemStack getItemstackWithMaterial(Material material) {
    ItemStack stack = new ItemStack(this);
    NBTTagCompound tag = new NBTTagCompound();
    tag.setString(Tags.PART_MATERIAL, material.identifier);
    stack.setTagCompound(tag);

    return stack;
  }
}
