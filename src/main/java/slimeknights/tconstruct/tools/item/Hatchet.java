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
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Hatchet extends AoeToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.wood,
                      net.minecraft.block.material.Material.vine,
                      net.minecraft.block.material.Material.plants,
                      net.minecraft.block.material.Material.gourd,
                      net.minecraft.block.material.Material.cactus);

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
  public boolean isEffective(Block block) {
    return effective_materials.contains(block.getMaterial()) || ItemAxe.EFFECTIVE_ON.contains(block);
  }

  @Override
  public float damagePotential() {
    return 1.1f;
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
    ToolNBT data = buildDefaultTag(materials);
    data.attack += 0.5f;
    return data.get();
  }
}
