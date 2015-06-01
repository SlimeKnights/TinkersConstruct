package tconstruct.library.tools;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.MaterialItem;
import tconstruct.library.tools.IToolPart;

public class ToolPart extends MaterialItem implements IToolPart {

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    // todo: check if the part supports the material
    for (Material mat : TinkerRegistry.getAllMaterials()) {
      subItems.add(new ItemStack(this, 1, mat.metadata));
    }
  }
}
