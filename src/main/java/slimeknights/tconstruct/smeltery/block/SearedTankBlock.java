package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import javax.annotation.Nullable;

public class SearedTankBlock extends SearedBlock {

  public SearedTankBlock(Properties properties) {
    super(properties);
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 1.0F;
  }
  @Override
  public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return false;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader worldIn) {
    return new TankTileEntity();
  }

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    if (!world.isRemote()) {
      TileEntity te = world.getTileEntity(pos);
      if (te != null) {
        IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit.getFace()).orElse(null);
        if (handler != null) {
          if (FluidUtil.interactWithFluidHandler(player, hand, handler)) {
            player.sendStatusMessage(new TranslationTextComponent("test"), true);
          }
        }
      }
    }
    if (FluidUtil.getFluidHandler(player.getHeldItem(hand)).isPresent()) {
      return ActionResultType.SUCCESS;
    }
    //return FluidUtil.getFluidHandler(player.getHeldItem(hand)) != null;
    return super.onBlockActivated(state, world, pos, player, hand, hit);
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    //TileEntity te = worldIn.getTileEntity(pos);
    //if (te instanceof TankTileEntity && stack != null && stack.hasTag()) {
    //  ((TankTileEntity) te).readTank(stack.getTag());
    //}
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
  }
}
