package slimeknights.tconstruct.library.tools;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.utils.TagUtil;

public class Pattern extends Item implements IPattern {

  public static final String TAG_PARTTYPE = "PartType";

  public Pattern() {
    this.setCreativeTab(TinkerRegistry.tabParts);
    this.setHasSubtypes(true);
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    if(this.isInCreativeTab(tab)) {
      subItems.add(new ItemStack(this));

      for(Item toolpart : getSubItemToolparts()) {
        ItemStack stack = new ItemStack(this);
        setTagForPart(stack, toolpart);

        if(isValidSubitem(toolpart)) {
          subItems.add(stack);
        }
      }
    }
  }

  protected Collection<Item> getSubItemToolparts() {
    return TinkerRegistry.getPatternItems();
  }

  protected boolean isValidSubitem(Item toolpart) {
    if(toolpart instanceof IToolPart) {
      for(Material material : TinkerRegistry.getAllMaterials()) {
        if(isValidSubitemMaterial(material) && ((IToolPart) toolpart).canUseMaterial(material)) {
          return true;
        }
      }
      return false;
    }
    return true;
  }

  protected boolean isValidSubitemMaterial(Material material) {
    return material.isCraftable();
  }

  @Nonnull
  @Override
  public String getItemStackDisplayName(@Nonnull ItemStack stack) {
    Item part = getPartFromTag(stack);
    String unloc = this.getUnlocalizedNameInefficiently(stack);
    if(part == null) {
      return Util.translate(unloc + ".blank");
    }

    return Util.translateFormatted(unloc + ".name", part.getItemStackDisplayName(ItemStack.EMPTY));
  }

  public static ItemStack setTagForPart(ItemStack stack, Item toolPart) {
    NBTTagCompound tag = TagUtil.getTagSafe(stack);

    tag.setString(TAG_PARTTYPE, toolPart.getRegistryName().toString());

    stack.setTagCompound(tag);
    return stack;
  }

  @Nullable
  public static Item getPartFromTag(ItemStack stack) {
    NBTTagCompound tag = TagUtil.getTagSafe(stack);
    String part = tag.getString(TAG_PARTTYPE);

    return Item.getByNameOrId(part);
  }

  public boolean isBlankPattern(ItemStack stack) {
    if(stack.isEmpty() || !(stack.getItem() instanceof IPattern)) {
      return false;
    }

    if(!stack.hasTagCompound()) {
      return true;
    }

    return Config.reuseStencil || !stack.getTagCompound().hasKey(TAG_PARTTYPE);
  }

  @Override
  public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
    Item part = getPartFromTag(stack);
    if(part != null && part instanceof IToolPart) {
      float cost = ((IToolPart) part).getCost() / (float) Material.VALUE_Ingot;
      tooltip.add(Util.translateFormatted("tooltip.pattern.cost", Util.df.format(cost)));
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
