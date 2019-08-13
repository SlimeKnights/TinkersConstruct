package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.tinkering.PartMaterialType;

public abstract class SwordCore extends TinkerToolCore {

  public static final ImmutableSet<Material> effective_materials =
      ImmutableSet.of(Material.WEB,
                      Material.VINE,
                      Material.CORAL,
                      Material.GOURD,
                      Material.LEAVES);

  public SwordCore(PartMaterialType... requiredComponents) {
    super(requiredComponents);

    // extended compatibility
    setHarvestLevel("sword", 0);
  }


  @Override
  public boolean isEffective(IBlockState state) {
    return effective_materials.contains(state.getMaterial());
  }

  @Override
  public float getStrVsBlock(ItemStack stack, IBlockState state) {
    if(state.getBlock() == Blocks.WEB) {
      return super.getStrVsBlock(stack, state) * 7.5f;
    }
    return super.getStrVsBlock(stack, state);
  }

  @Override
  public float miningSpeedModifier() {
    return 0.5f; // slooow, because it's a swooooord
  }

  @Override
  public boolean canDestroyBlockInCreative(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
    return false;
  }
}
