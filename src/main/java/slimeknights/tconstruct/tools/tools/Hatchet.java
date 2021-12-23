package slimeknights.tconstruct.tools.tools;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.client.particle.Particles;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.List;

public class Hatchet extends AoeToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.WOOD,
                      net.minecraft.block.material.Material.VINE,
                      net.minecraft.block.material.Material.PLANTS,
                      net.minecraft.block.material.Material.GOURD,
                      net.minecraft.block.material.Material.CACTUS);

  public Hatchet() {
    this(PartMaterialType.handle(TinkerTools.toolRod),
         PartMaterialType.head(TinkerTools.axeHead),
         PartMaterialType.extra(TinkerTools.binding));
  }

  protected Hatchet(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.HARVEST);
    addCategory(Category.WEAPON);

    this.setHarvestLevel("axe", 0);
  }

  @Override
  public boolean isEffective(IBlockState state) {
    return effective_materials.contains(state.getMaterial()) || ItemAxe.EFFECTIVE_ON.contains(state.getBlock());
  }

  @Override
  public float damagePotential() {
    return 1.1f;
  }

  @Override
  public double attackSpeed() {
    return 1.1f; // a bit faster than vanilla axes
  }

  @Override
  public float knockback() {
    return 1.3f;
  }

  // hatches 1 : leaves 0
  @Override
  public float getStrVsBlock(ItemStack stack, IBlockState state) {
    if(state.getBlock().getMaterial(state) == net.minecraft.block.material.Material.LEAVES) {
      return ToolHelper.calcDigSpeed(stack, state);
    }
    return super.getStrVsBlock(stack, state);
  }

  @Override
  public void afterBlockBreak(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase player, int damage, boolean wasEffective) {
    // breaking leaves does not reduce durability
    if(state.getBlock().isLeaves(state, world, pos)) {
      damage = 0;
    }
    super.afterBlockBreak(stack, world, state, pos, player, damage, wasEffective);
  }

  @Override
  public boolean dealDamage(ItemStack stack, EntityLivingBase player, Entity entity, float damage) {
    boolean hit = super.dealDamage(stack, player, entity, damage);

    if(hit && readyForSpecialAttack(player)) {
      TinkerTools.proxy.spawnAttackParticle(Particles.HATCHET_ATTACK, player, 0.8d);
    }

    return hit;
  }

  @Override
  public boolean canDisableShield(ItemStack stack, ItemStack shield, EntityLivingBase entity, EntityLivingBase attacker) {
    return true;
  }

  @Override
  protected ToolNBT buildTagData(List<Material> materials) {
    ToolNBT data = buildDefaultTag(materials);
    data.attack += 0.5f;
    return data;
  }
}
