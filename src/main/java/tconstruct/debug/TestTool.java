package tconstruct.debug;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.PartMaterialWrapper;
import tconstruct.library.tinkering.TinkersItem;
import tconstruct.library.tools.TinkersTool;
import tconstruct.library.utils.Log;
import tconstruct.library.utils.ToolBuilder;
import tconstruct.tools.TinkerMaterials;

public class TestTool extends TinkersTool {

  public TestTool(PartMaterialWrapper... requiredComponents) {
    super(requiredComponents);

    this.setCreativeTab(CreativeTabs.tabTools);
  }

  @Override
  protected NBTTagCompound buildTag(Material[] materials) {
    Log.info("Parts are valid");
    return ToolBuilder.buildSimpleTool(materials[0], materials[1]);
  }

  @Override
  public String getItemType() {
    return "harvest";
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    ItemStack stack = new ItemStack(itemIn);
    NBTTagCompound baseTag = new NBTTagCompound();
    baseTag.setTag(getTagName(), this.buildTag(new Material[]{TinkerMaterials.stone, TinkerMaterials.wood}));
    stack.setTagCompound(baseTag);

    subItems.add(stack);
  }
}
