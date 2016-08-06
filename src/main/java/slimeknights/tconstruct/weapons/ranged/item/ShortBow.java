package slimeknights.tconstruct.weapons.ranged.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.weapons.ranged.TinkerRangedWeapons;

public class ShortBow extends ToolCore {

  public ShortBow() {
    super(PartMaterialType.bowstring(TinkerTools.bowString),
          PartMaterialType.bow(TinkerTools.bowLimb),
          PartMaterialType.bow(TinkerTools.bowLimb));

    this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
    {
      @SideOnly(Side.CLIENT)
      public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
      {
        if (entityIn == null)
        {
          return 0.0F;
        }
        else
        {
          ItemStack itemstack = entityIn.getActiveItemStack();
          return itemstack != null && itemstack.getItem() == TinkerRangedWeapons.shortBow ? (float)(stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / 20.0F : 0.0F;
        }
      }
    });
    this.addPropertyOverride(new ResourceLocation("pulling"), new IItemPropertyGetter()
    {
      @SideOnly(Side.CLIENT)
      public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
      {
        return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
      }
    });
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    addDefaultBowItems(subItems);
  }

  /* Tic Tool Stuff */

  @Override
  public float damagePotential() {
    return 0.7f;
  }

  @Override
  public double attackSpeed() {
    return 3;
  }

  /* Bow usage stuff */

  @Nonnull
  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return EnumAction.BOW;
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return 72000;
  }

  @Nonnull
  @Override
  public ActionResult<ItemStack> onItemRightClick(@Nonnull ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
    if(!ToolHelper.isBroken(itemStackIn)) {
      playerIn.setActiveHand(hand);
      return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }
    return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
  }

  /* Data Stuff */

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolNBT toolNBT = new ToolNBT();
    return toolNBT.get();
  }
}
