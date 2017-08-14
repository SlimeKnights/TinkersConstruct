package slimeknights.tconstruct.tools.ranged.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import slimeknights.tconstruct.library.client.crosshair.Crosshairs;
import slimeknights.tconstruct.library.client.crosshair.ICrosshair;
import slimeknights.tconstruct.library.client.crosshair.ICustomCrosshairUser;
import slimeknights.tconstruct.library.materials.BowMaterialStats;
import slimeknights.tconstruct.library.materials.BowStringMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileLauncherNBT;
import slimeknights.tconstruct.library.tools.ranged.BowCore;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.ranged.TinkerRangedWeapons;

public class ShortBow extends BowCore implements ICustomCrosshairUser {

  public ShortBow() {
    this(PartMaterialType.bow(TinkerTools.bowLimb),
         PartMaterialType.bow(TinkerTools.bowLimb),
         PartMaterialType.bowstring(TinkerTools.bowString));
  }

  public ShortBow(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    this.addPropertyOverride(PROPERTY_PULL_PROGRESS, pullProgressPropertyGetter);
    this.addPropertyOverride(PROPERTY_IS_PULLING, isPullingPropertyGetter);
  }

  @Override
  public int[] getRepairParts() {
    return new int[] { 0, 1 };
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    if(this.isInCreativeTab(tab)) {
      addDefaultSubItems(subItems, null, null, TinkerMaterials.string);
    }
  }

  /* Tic Tool Stuff */

  @Override
  public float baseProjectileDamage() {
    return 0f;
  }

  @Override
  public float damagePotential() {
    return 0.7f;
  }

  @Override
  public double attackSpeed() {
    return 1.5;
  }

  @Override
  protected float baseInaccuracy() {
    return 1f;
  }

  @Override
  public float projectileDamageModifier() {
    return 0.8f;
  }

  @Override
  public int getDrawTime() {
    return 12;
  }

  @Override
  protected List<Item> getAmmoItems() {
    return TinkerRangedWeapons.getDiscoveredArrows();
  }

  @Override
  public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
    // has to be done in onUpdate because onTickUsing is too early and gets overwritten. bleh.
    // shortbows are more mobile than other bows
    preventSlowDown(entityIn, 0.5f);

    super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
  }

  /* Data Stuff */

  @Override
  public ProjectileLauncherNBT buildTagData(List<Material> materials) {
    ProjectileLauncherNBT data = new ProjectileLauncherNBT();
    HeadMaterialStats head1 = materials.get(0).getStatsOrUnknown(MaterialTypes.HEAD);
    HeadMaterialStats head2 = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    BowMaterialStats limb1 = materials.get(0).getStatsOrUnknown(MaterialTypes.BOW);
    BowMaterialStats limb2 = materials.get(1).getStatsOrUnknown(MaterialTypes.BOW);
    BowStringMaterialStats bowstring = materials.get(2).getStatsOrUnknown(MaterialTypes.BOWSTRING);

    data.head(head1, head2);
    data.limb(limb1, limb2);
    data.bowstring(bowstring);

    return data;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public ICrosshair getCrosshair(ItemStack itemStack, EntityPlayer player) {
    return Crosshairs.SQUARE;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public float getCrosshairState(ItemStack itemStack, EntityPlayer player) {
    return getDrawbackProgress(itemStack, player);
  }
}
