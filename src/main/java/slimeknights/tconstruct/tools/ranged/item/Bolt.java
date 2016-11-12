package slimeknights.tconstruct.tools.ranged.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.entity.EntityProjectileBase;
import slimeknights.tconstruct.library.materials.ArrowShaftMaterialStats;
import slimeknights.tconstruct.library.materials.FletchingMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.MaterialTypes;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ProjectileNBT;
import slimeknights.tconstruct.library.tools.ranged.ProjectileCore;
import slimeknights.tconstruct.library.utils.TinkerUtil;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.entity.EntityArrow;
import slimeknights.tconstruct.tools.common.entity.EntityBolt;

public class Bolt extends ProjectileCore {

  public Bolt() {
    super(PartMaterialType.arrowShaft(TinkerTools.boltCore),
          PartMaterialType.head(TinkerTools.boltCore),
          PartMaterialType.fletching(TinkerTools.fletching));

    addCategory(Category.NO_MELEE, Category.PROJECTILE);
  }

  @Override
  public void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
    for(Material head : TinkerRegistry.getAllMaterials()) {
      List<Material> mats = new ArrayList<Material>(3);

      if(head.hasStats(MaterialTypes.HEAD)) {
        mats.add(TinkerMaterials.wood);
        mats.add(head);
        mats.add(TinkerMaterials.feather);

        ItemStack tool = buildItem(mats);
        // only valid ones
        if(hasValidMaterials(tool)) {
          subItems.add(tool);
          if(!Config.listAllMaterials) {
            break;
          }
        }
      }
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
  public ItemStack buildItemFromStacks(ItemStack[] stacks) {
    if(stacks.length != 2) {
      return null;
    }

    ItemStack boltCore = stacks[0];
    ItemStack fletching = stacks[1];

    ItemStack boltCoreHead = TinkerTools.boltCore.buildInternalItemstackForCrafting(BoltCore.getHeadMaterial(boltCore));

    return super.buildItemFromStacks(new ItemStack[]{boltCore, boltCoreHead, fletching});
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
    inaccuracy -= (1f - 1f/ProjectileNBT.from(stack).accuracy) * speed/2f;
    return new EntityBolt(world, player, speed, inaccuracy, power, getProjectileStack(stack, world, player, usedAmmo), bow);
  }
}
