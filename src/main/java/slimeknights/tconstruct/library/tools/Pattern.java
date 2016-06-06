package slimeknights.tconstruct.library.tools;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

import java.text.DecimalFormat;
import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.utils.TagUtil;

public class Pattern extends Item implements IPattern {

  public static final DecimalFormat df = new DecimalFormat("#.##");

  public static final String TAG_PARTTYPE = "PartType";

  public Pattern() {
    this.setCreativeTab(TinkerRegistry.tabParts);
    this.setHasSubtypes(true);
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    subItems.add(new ItemStack(this));

    for(Item toolpart : TinkerRegistry.getPatternItems()) {
      ItemStack stack = new ItemStack(this);
      setTagForPart(stack, toolpart);

      subItems.add(stack);
    }
  }

  @Nonnull
  @Override
  public String getItemStackDisplayName(@Nonnull ItemStack stack) {
    Item part = getPartFromTag(stack);
    String unloc = this.getUnlocalizedNameInefficiently(stack);
    if(part == null) {
      return Util.translate(unloc + ".blank");
    }

    return Util.translateFormatted(unloc + ".name", part.getItemStackDisplayName(null));
  }

  public static void setTagForPart(ItemStack stack, Item toolPart) {
    NBTTagCompound tag = TagUtil.getTagSafe(stack);

    tag.setString(TAG_PARTTYPE, toolPart.getRegistryName().toString());

    stack.setTagCompound(tag);
  }

  public static Item getPartFromTag(ItemStack stack) {
    NBTTagCompound tag = TagUtil.getTagSafe(stack);
    String part = tag.getString(TAG_PARTTYPE);

    return GameData.getItemRegistry().getObject(new ResourceLocation(part));
  }

  public boolean isBlankPattern(ItemStack stack) {
    if(stack == null || !(stack.getItem() instanceof IPattern)) {
      return false;
    }

    if(!stack.hasTagCompound()) {
      return true;
    }

    return Config.reuseStencil || !stack.getTagCompound().hasKey(TAG_PARTTYPE);
  }

  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    Item part = getPartFromTag(stack);
    if(part != null && part instanceof IToolPart) {
      float cost = ((IToolPart)part).getCost() / (float) Material.VALUE_Ingot;
      tooltip.add(Util.translateFormatted("tooltip.pattern.cost", df.format(cost)));
    }
  }

  public static String getTextureIdentifier(Item item) {
    String identifier = item.getRegistryName().toString();
    // sanitize it (remove modid)
    if(identifier.contains(":")) {
      identifier = identifier.substring(identifier.lastIndexOf(':') + 1);
    }

    return "_" + identifier;
  }
}
