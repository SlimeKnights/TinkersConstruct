package tconstruct.tools.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import java.util.List;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tools.IToolPart;
import tconstruct.library.utils.TagUtil;
import tconstruct.tools.ToolClientProxy;

public class Pattern extends Item {
  public static final String TAG_PARTTYPE = "PartType";

  public Pattern() {
    this.setCreativeTab(TinkerRegistry.tabTools);
    this.setHasSubtypes(true);
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    subItems.add(new ItemStack(this));

    for(IToolPart toolpart : TinkerRegistry.getToolParts()) {
      if(!(toolpart instanceof Item)) {
        continue;
      }

      ItemStack stack = new ItemStack(this);
      this.setTagForPart(stack, (Item)toolpart);

      subItems.add(stack);
    }
  }

  public void setTagForPart(ItemStack stack, Item toolPart) {
    NBTTagCompound tag = TagUtil.getTagSafe(stack);
    ResourceLocation partLocation = ToolClientProxy.getItemLocation(toolPart);

    tag.setString(TAG_PARTTYPE, partLocation.getResourcePath());

    stack.setTagCompound(tag);
  }
}
