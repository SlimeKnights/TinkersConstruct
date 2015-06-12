package tconstruct.library.tools;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.tinkering.Material;
import tconstruct.library.tinkering.MaterialItem;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;

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

    if (material == Material.UNKNOWN) {
      NBTTagCompound tag = TagUtil.getTagSafe(stack);
      String materialID = tag.getString(Tags.PART_MATERIAL);

      String error;
      if (materialID != null && !materialID.isEmpty()) {
        error = StatCollector.translateToLocalFormatted("tooltip.part.missingMaterial", materialID);
      } else {
        error = StatCollector.translateToLocal("tooltip.part.missingInfo");
      }
      tooltip.add(error);
    } else {
      tooltip.add(material.textColor + material.getLocalizedName());
    }

    if (advanced) {
      String materialInfo = StatCollector.translateToLocalFormatted("tooltip.part.materialAddedBy",
                                                                    TinkerRegistry.getTrace(material));
      tooltip.add(materialInfo);
    }
  }
}
