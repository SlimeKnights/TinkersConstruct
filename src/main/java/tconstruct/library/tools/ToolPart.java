package tconstruct.library.tools;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.Util;
import tconstruct.library.materials.Material;
import tconstruct.library.tinkering.MaterialItem;
import tconstruct.library.tinkering.PartMaterialType;
import tconstruct.library.traits.ITrait;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;

public class ToolPart extends MaterialItem implements IToolPart {

  public static int COST_Ingot = 2;
  public static int COST_Shard = 1;

  protected int cost;

  public ToolPart(int cost) {
    this.setCreativeTab(TinkerRegistry.tabTools);
    this.cost = cost;
  }

  @Override
  public int getCost() {
    return cost;
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    for(Material mat : TinkerRegistry.getAllMaterials()) {
      // check if the material makes sense for this item (is it usable to build stuff?)
      if(canUseMaterial(mat)) {
        subItems.add(getItemstackWithMaterial(mat));
      }
    }
  }

  public boolean canUseMaterial(Material mat) {
    for(ToolCore tool : TinkerRegistry.getTools()) {
      for(PartMaterialType pmt : tool.requiredComponents) {
        if(pmt.isValid(this, mat)) {
          return true;
        }
      }
    }

    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
    Material material = getMaterial(stack);

    if(material == Material.UNKNOWN) {
      NBTTagCompound tag = TagUtil.getTagSafe(stack);
      String materialID = tag.getString(Tags.PART_MATERIAL);

      String error;
      if(materialID != null && !materialID.isEmpty()) {
        error = StatCollector.translateToLocalFormatted("tooltip.part.missingMaterial", materialID);
      }
      else {
        error = StatCollector.translateToLocal("tooltip.part.missingInfo");
      }
      tooltip.add(error);
    }
    else {
      tooltip.add(material.textColor.toString() + EnumChatFormatting.ITALIC.toString() + material.getLocalizedName());

      for(ITrait trait : material.getAllTraits()) {
        tooltip.add(material.textColor + trait.getLocalizedName());
      }
    }

    if(advanced) {
      String materialInfo = StatCollector.translateToLocalFormatted("tooltip.part.materialAddedBy",
                                                                    TinkerRegistry.getTrace(material));
      tooltip.add(materialInfo);
    }
  }

  @Override
  public String getIdentifier() {
    return Util.getItemLocation(this).getResourcePath();
  }

  static String getIdentifier(ItemStack stack) {
    if(stack != null && stack.getItem() instanceof IToolPart) {
      return ((IToolPart) stack.getItem()).getIdentifier();
    }
    return null;
  }
}
