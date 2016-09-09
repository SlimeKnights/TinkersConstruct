package slimeknights.tconstruct.tools.ranged.item;

import com.google.common.collect.ImmutableList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import javax.annotation.Nonnull;

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
    super(PartMaterialType.bowstring(TinkerTools.bowString),
          PartMaterialType.bow(TinkerTools.bowLimb),
          PartMaterialType.bow(TinkerTools.bowLimb));
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

  @Override
  protected float baseInaccuracy() {
    return 1f;
  }

  private ImmutableList<Item> arrowMatches = null;

  @Override
  protected List<Item> getAmmoItems() {
    if(arrowMatches == null) {
      ImmutableList.Builder<Item> builder = ImmutableList.builder();
      if(TinkerRangedWeapons.arrow != null) {
        builder.add(TinkerRangedWeapons.arrow);
      }
      builder.add(Items.ARROW);
      builder.add(Items.TIPPED_ARROW);
      builder.add(Items.SPECTRAL_ARROW);
      arrowMatches = builder.build();
    }
    return arrowMatches;
  }

  /* Data Stuff */

  @Override
  public ProjectileLauncherNBT buildTagData(List<Material> materials) {
    ProjectileLauncherNBT data = new ProjectileLauncherNBT();
    HeadMaterialStats head1 = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    HeadMaterialStats head2 = materials.get(2).getStatsOrUnknown(MaterialTypes.HEAD);
    BowMaterialStats limb1 = materials.get(1).getStatsOrUnknown(MaterialTypes.BOW);
    BowMaterialStats limb2 = materials.get(2).getStatsOrUnknown(MaterialTypes.BOW);
    BowStringMaterialStats bowstring = materials.get(0).getStatsOrUnknown(MaterialTypes.BOWSTRING);


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
