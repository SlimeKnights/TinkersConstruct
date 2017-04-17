package slimeknights.tconstruct.tools.tools;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.events.TinkerToolEvent;
import slimeknights.tconstruct.library.materials.Material;
import slimeknights.tconstruct.library.tinkering.Category;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;
import slimeknights.tconstruct.library.tools.AoeToolCore;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.utils.ToolHelper;
import slimeknights.tconstruct.tools.TinkerTools;

public class Shovel extends AoeToolCore {

  public static final ImmutableSet<net.minecraft.block.material.Material> effective_materials =
      ImmutableSet.of(net.minecraft.block.material.Material.GRASS,
                      net.minecraft.block.material.Material.GROUND,
                      net.minecraft.block.material.Material.SAND,
                      net.minecraft.block.material.Material.CRAFTED_SNOW,
                      net.minecraft.block.material.Material.SNOW,
                      net.minecraft.block.material.Material.CLAY,
                      net.minecraft.block.material.Material.CAKE);

  public Shovel() {
    this(PartMaterialType.handle(TinkerTools.toolRod),
         PartMaterialType.head(TinkerTools.shovelHead),
         PartMaterialType.extra(TinkerTools.binding));
  }

  protected Shovel(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    addCategory(Category.HARVEST);

    setHarvestLevel("shovel", 0);
  }

  @Override
  public boolean isEffective(IBlockState state) {
    return effective_materials.contains(state.getMaterial()) || ItemSpade.EFFECTIVE_ON.contains(state.getBlock());
  }

  // grass paths
  @Nonnull
  @Override
  public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
    ItemStack stack = player.getHeldItem(hand);
    if(ToolHelper.isBroken(stack)) {
      return EnumActionResult.FAIL;
    }

    EnumActionResult result = Items.DIAMOND_SHOVEL.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    if(result == EnumActionResult.SUCCESS) {
      TinkerToolEvent.OnShovelMakePath.fireEvent(stack, player, world, pos);
    }

    // only do the AOE path if the selected block is grass or grass path
    Block block = world.getBlockState(pos).getBlock();
    if(block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
      for(BlockPos aoePos : getAOEBlocks(stack, world, player, pos)) {
        // stop if the tool breaks during the process
        if(ToolHelper.isBroken(stack)) {
          break;
        }

        EnumActionResult aoeResult = Items.DIAMOND_SHOVEL.onItemUse(player, world, aoePos, hand, facing, hitX, hitY, hitZ);
        // if we pass on an earlier block, check if another block succeeds here instead
        if(result != EnumActionResult.SUCCESS) {
          result = aoeResult;
        }

        if(aoeResult == EnumActionResult.SUCCESS) {
          TinkerToolEvent.OnShovelMakePath.fireEvent(stack, player, world, aoePos);
        }
      }
    }

    return result;
  }

  @Override
  public double attackSpeed() {
    return 1f;
  }

  @Override
  public float damagePotential() {
    return 0.9f;
  }

  @Override
  protected ToolNBT buildTagData(List<Material> materials) {
    return buildDefaultTag(materials);
  }
}
