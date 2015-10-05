package slimeknights.tconstruct.tools.item;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.materials.ToolMaterialStats;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Hatchet extends AoeToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.wood,
                      net.minecraft.block.material.Material.leaves,
                      net.minecraft.block.material.Material.vine,
                      net.minecraft.block.material.Material.plants,
                      net.minecraft.block.material.Material.gourd,
                      net.minecraft.block.material.Material.cactus,
                      net.minecraft.block.material.Material.circuits);

  public Hatchet() {
    super(new PartMaterialType.ToolPartType(TinkerTools.toolRod),
          new PartMaterialType.ToolPartType(TinkerTools.axeHead),
          new PartMaterialType.ToolPartType(TinkerTools.binding));

    addCategory(Category.HARVEST);
    addCategory(Category.WEAPON);

    this.setHarvestLevel("axe", 0);
  }

  @Override
  public boolean isEffective(Block block) {
    return effective_materials.contains(block.getMaterial()) || ItemAxe.EFFECTIVE_ON.contains(block);
  }

  @Override
  public float damagePotential() {
    return 0.73f;
  }

  @Override
  public float knockback() {
    return 1.3f;
  }

  // hatches 1 : leaves 0
  @Override
  public float getDigSpeed(ItemStack itemstack, IBlockState state) {
    if(state.getBlock().getMaterial() == net.minecraft.block.material.Material.leaves) {
      return ToolHelper.calcDigSpeed(itemstack, state);
    }
    return super.getDigSpeed(itemstack, state);
  }

  @Override
  public void afterBlockBreak(ItemStack stack, World world, Block block, BlockPos pos, EntityLivingBase player, int damage, boolean wasEffective) {
    // breaking leaves does not reduce durability
    if(block.isLeaves(world, pos)) {
      damage = 0;
    }
    super.afterBlockBreak(stack, world, block, pos, player, damage, wasEffective);
  }

  @Override
  public NBTTagCompound buildTag(List<Material> materials) {
    ToolMaterialStats handle = materials.get(0).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats head = materials.get(1).getStats(ToolMaterialStats.TYPE);
    ToolMaterialStats binding = materials.get(2).getStats(ToolMaterialStats.TYPE);

    ToolNBT data = new ToolNBT(head);

    // bonus base damage
    data.attack += 1f;

    // the binding has the most impact here, since it has to hold the parts together
    data.durability = data.durability / 2 + (int) (binding.extraQuality * (float) data.durability / 2f);

    // handle.. stuff..
    data.durability *= 0.8f + 0.2f * handle.handleQuality;
    // flat durability from other parts
    data.durability += 0.05f * handle.durability + 0.15f * binding.durability;

    // how well handle and binding interact
    float coeff = (0.5f + handle.handleQuality / 2) * (0.5f + binding.extraQuality / 2);

    data.attack *= 0.6f + 0.4f * coeff;
    data.speed *= 0.6f + 0.4f * coeff;

    // 3 free modifiers
    data.modifiers = DEFAULT_MODIFIERS;

    return data.get();
  }
}
