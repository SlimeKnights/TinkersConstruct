package slimeknights.tconstruct.tools.ranged.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.entity.EntityArrow;

public class Arrow extends ProjectileCore {

  public Arrow() {
    super(PartMaterialType.arrowShaft(TinkerTools.arrowShaft),
          PartMaterialType.arrowHead(TinkerTools.arrowHead),
          PartMaterialType.fletching(TinkerTools.fletching));

    addCategory(Category.NO_MELEE, Category.PROJECTILE);
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    if(this.isInCreativeTab(tab)) {
      addDefaultSubItems(subItems, TinkerMaterials.wood, null, TinkerMaterials.feather);
    }
  }

  @Override
  public float damagePotential() {
    return 1f;
  }

  @Override
  public double attackSpeed() {
    return 1;
  }

  @Override
  public ProjectileNBT buildTagData(List<Material> materials) {
    ProjectileNBT data = new ProjectileNBT();

    ArrowShaftMaterialStats shaft = materials.get(0).getStatsOrUnknown(MaterialTypes.SHAFT);
    HeadMaterialStats head = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
    FletchingMaterialStats fletching = materials.get(2).getStatsOrUnknown(MaterialTypes.FLETCHING);

    data.head(head);
    data.fletchings(fletching);
    data.shafts(this, shaft);

    data.attack += 2;

    return data;
  }

  @Override
  public EntityProjectileBase getProjectile(ItemStack stack, ItemStack bow, World world, EntityPlayer player, float speed, float inaccuracy, float power, boolean usedAmmo) {
    inaccuracy -= (1f - 1f / ProjectileNBT.from(stack).accuracy) * speed / 2f;
    return new EntityArrow(world, player, speed, inaccuracy, power, getProjectileStack(stack, world, player, usedAmmo), bow);
  }
}
