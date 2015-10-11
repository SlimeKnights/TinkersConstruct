package slimeknights.tconstruct.library.tools;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import slimeknights.tconstruct.common.ClientProxy;
import slimeknights.tconstruct.common.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.IMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.MaterialItem;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.traits.ITrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.Tags;

public class ToolPart extends MaterialItem implements IToolPart {

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

    // Material traits/info

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
      for(ITrait trait : material.getAllTraits()) {
        tooltip.add(material.getTextColor() + trait.getLocalizedName());
      }
    }

    // Stats
    if(Config.extraTooltips) {
      for(IMaterialStats stat : material.getAllStats()) {
        tooltip.addAll(stat.getLocalizedInfo());
      }
    }

    String materialInfo = StatCollector.translateToLocalFormatted("tooltip.part.materialAddedBy",
                                                                  TinkerRegistry.getTrace(material));
    tooltip.add("");
    tooltip.add(materialInfo);
  }

  @Override
  public String getItemStackDisplayName(ItemStack stack) {
    Material material = getMaterial(stack);

    String locString = getUnlocalizedName() + "." + material.getIdentifier();

    // custom name?
    if(StatCollector.canTranslate(locString)) {
      return Util.translate(locString);
    }

    // no, create the default name combo
    return material.getLocalizedItemName(super.getItemStackDisplayName(stack));
  }

  @Override
  public FontRenderer getFontRenderer(ItemStack stack) {
    return ClientProxy.fontRenderer;
  }

  @Override
  public String getLocalizedName() {
    return super.getItemStackDisplayName(null);
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
