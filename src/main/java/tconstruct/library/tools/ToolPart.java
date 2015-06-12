package tconstruct.library.tools;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    super.getSubItems(itemIn, tab, subItems);
    // todo: check if the part supports the material

  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
    Material material = getMaterial(stack);

    tooltip.add(material.textColor + material.identifier);

    if(advanced) {
      String materialInfo = StatCollector.translateToLocal("tooltip.part.materialAddedBy");
      tooltip.add(String.format(materialInfo, TinkerRegistry.getTrace(material)));
    }
  }
}
