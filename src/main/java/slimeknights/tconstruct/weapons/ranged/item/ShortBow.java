package slimeknights.tconstruct.weapons.ranged.item;

import com.google.common.collect.ImmutableList;

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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.BowNBT;
import slimeknights.tconstruct.library.tools.IAmmoUser;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.AmmoHelper;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.weapons.ranged.TinkerRangedWeapons;

public class ShortBow extends ToolCore implements IAmmoUser {

  public ShortBow() {
    super(PartMaterialType.bowstring(TinkerTools.bowString),
          PartMaterialType.bow(TinkerTools.bowLimb),
          PartMaterialType.bow(TinkerTools.bowLimb));

    this.addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter()
    {
      @Override
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
      @Override
      @SideOnly(Side.CLIENT)
      public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn)
      {
        return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;
      }
    });
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    addDefaultSubItems(subItems, TinkerMaterials.string);
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

  @Override
  public ItemStack getAmmoToRender(ItemStack weapon, EntityLivingBase player) {
    if(ToolHelper.isBroken(weapon)) {
      return null;
    }
    return AmmoHelper.findAmmoFromInventory(ImmutableList.of(TinkerRangedWeapons.arrow, Items.ARROW), player);
  }

    /* Data Stuff */

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    BowNBT data = new BowNBT();
    HeadMaterialStats head1 = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    HeadMaterialStats head2 = materials.get(2).getStatsOrUnknown(MaterialTypes.HEAD);
    BowMaterialStats limb1 = materials.get(1).getStatsOrUnknown(MaterialTypes.BOW);
    BowMaterialStats limb2 = materials.get(2).getStatsOrUnknown(MaterialTypes.BOW);
    BowStringMaterialStats bowstring = materials.get(0).getStatsOrUnknown(MaterialTypes.BOWSTRING);


    data.head(head1, head2);
    data.limb(limb1, limb2);
    data.bowstring(bowstring);

    return data.get();
  }
}
