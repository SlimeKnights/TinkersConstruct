package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;

import slimeknights.tconstruct.library.TinkerRegistry;

public class BlockTinkerFluid extends BlockFluidClassic {

  public BlockTinkerFluid(Fluid fluid, Material material) {
    super(fluid, material);

    setCreativeTab(TinkerRegistry.tabSmeltery);
  }

  @Nonnull
  @Override
  public String getUnlocalizedName() {
    Fluid fluid = FluidRegistry.getFluid(fluidName);
    if(fluid != null) {
      return fluid.getUnlocalizedName();
    }
    return super.getUnlocalizedName();
  }

  @Override
  public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
    return BlockFaceShape.UNDEFINED;
  }
}
