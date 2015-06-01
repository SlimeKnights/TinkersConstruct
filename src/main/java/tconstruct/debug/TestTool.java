package tconstruct.debug;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.PartMaterialWrapper;
import tconstruct.library.tools.TinkersTool;
import tconstruct.library.utils.ToolBuilder;
import tconstruct.tools.TinkerMaterials;

public class TestTool extends TinkersTool {

  public TestTool(PartMaterialWrapper... requiredComponents) {
    super(requiredComponents);

    this.setCreativeTab(CreativeTabs.tabTools);
  }

  @Override
  protected NBTTagCompound buildTag(Material[] materials) {
    TinkerRegistry.log.info("Parts are valid");
    return ToolBuilder.buildSimpleTool(materials[0], materials[1]);
  }

  @Override
  public String getItemType() {
    return "harvest";
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    subItems.add(this.buildItem(new Material[]{TinkerMaterials.stone, TinkerMaterials.wood}));
  }
}
