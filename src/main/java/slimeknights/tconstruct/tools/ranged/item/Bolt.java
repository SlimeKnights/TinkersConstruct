package slimeknights.tconstruct.tools.ranged.item;

import com.google.common.collect.ImmutableList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
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
import slimeknights.tconstruct.library.utils.ListUtil;
import slimeknights.tconstruct.tools.TinkerMaterials;
import slimeknights.tconstruct.tools.TinkerTools;
import slimeknights.tconstruct.tools.common.entity.EntityBolt;
import slimeknights.tconstruct.tools.melee.item.Rapier;
import slimeknights.tconstruct.tools.traits.TraitEnderference;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Bolt extends ProjectileCore {

  protected final List<PartMaterialType> toolBuildComponents;

  public Bolt() {
    super(PartMaterialType.arrowShaft(TinkerTools.boltCore),
          new BoltHeadPartMaterialType(TinkerTools.boltCore),
          PartMaterialType.fletching(TinkerTools.fletching));

    addCategory(Category.NO_MELEE, Category.PROJECTILE);
    toolBuildComponents = ImmutableList.of(requiredComponents[0], requiredComponents[2]);
  }

  @Override
  public List<PartMaterialType> getToolBuildComponents() {
    return toolBuildComponents;
  }

  @Override
  public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
    if(this.isInCreativeTab(tab)) {
      for(Material head : TinkerRegistry.getAllMaterials()) {
        List<Material> mats = new ArrayList<>(3);

        if(head.hasStats(MaterialTypes.HEAD)) {
          mats.add(TinkerMaterials.wood);
          mats.add(head);
          mats.add(TinkerMaterials.feather);

          ItemStack tool = buildItem(mats);
          // only valid ones
          if(hasValidMaterials(tool)) {
            subItems.add(tool);
            if(!Config.listAllToolMaterials) {
              break;
            }
          }
        }
      }
    }
  }

  @Override
  public Material getMaterialForPartForGuiRendering(int index) {
    return super.getMaterialForPartForGuiRendering(index + 1);
  }

  @Override
  public ItemStack buildItemForRenderingInGui() {
    List<Material> materials = IntStream.range(0, getRequiredComponents().size())
                                        .mapToObj(super::getMaterialForPartForGuiRendering)
                                        .collect(Collectors.toList());

    return buildItemForRendering(materials);
  }

  @Override
  public float damagePotential() {
    return 1f;
  }

  @Override
  public double attackSpeed() {
    return 1;
  }

  @Nonnull
  @Override
  public ItemStack buildItemFromStacks(NonNullList<ItemStack> inputStacks) {
    List<ItemStack> stacks = inputStacks.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
    if(stacks.size() != 2) {
      return ItemStack.EMPTY;
    }

    ItemStack boltCore = stacks.get(0);
    ItemStack fletching = stacks.get(1);

    // we only care about the material returned by getMaterial call
    ItemStack boltCoreHead = BoltCore.getHeadStack(boltCore);

    return super.buildItemFromStacks(ListUtil.getListFrom(boltCore, boltCoreHead, fletching));
  }

  @Override
  public boolean dealDamageRanged(ItemStack stack, Entity projectile, EntityLivingBase player, Entity target, float damage) {
    // friggin vanilla hardcode 2
    if(target instanceof EntityEnderman && ((EntityEnderman) target).getActivePotionEffect(TraitEnderference.Enderference) != null) {
      return target.attackEntityFrom(new DamageSourceProjectileForEndermen(DAMAGE_TYPE_PROJECTILE, projectile, player), damage);
    }

    DamageSource damageSource = new EntityDamageSourceIndirect(DAMAGE_TYPE_PROJECTILE, projectile, player).setProjectile();
    return Rapier.dealHybridDamage(damageSource, target, damage);
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

    data.durability *= 0.8f;

    return data;
  }

  @Override
  public EntityProjectileBase getProjectile(ItemStack stack, ItemStack bow, World world, EntityPlayer player, float speed, float inaccuracy, float power, boolean usedAmmo) {
    inaccuracy -= (1f - 1f / ProjectileNBT.from(stack).accuracy) * speed / 2f;
    return new EntityBolt(world, player, speed, inaccuracy, power, getProjectileStack(stack, world, player, usedAmmo), bow);
  }

  private static class BoltHeadPartMaterialType extends PartMaterialType {

    public BoltHeadPartMaterialType(IToolPart part) {
      super(part, MaterialTypes.HEAD);
    }

    @Override
    public boolean isValidMaterial(Material material) {
      return material.isCastable() && super.isValidMaterial(material);
    }
  }
}
