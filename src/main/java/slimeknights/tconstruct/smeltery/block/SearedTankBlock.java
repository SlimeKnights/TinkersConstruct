package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.smeltery.tileentity.TankTileEntity;

import javax.annotation.Nullable;
import java.util.Locale;

public class SearedTankBlock extends SearedBlock {

  public SearedTankBlock(Properties properties) {
    super(properties);
  }

  @Deprecated
  @Override
  @OnlyIn(Dist.CLIENT)
  public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
    return 1.0F;
  }

  @Deprecated
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

  @Deprecated
  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    if (!world.isRemote()) {
      TileEntity te = world.getTileEntity(pos);
      if (te != null) {
        te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit.getFace()).ifPresent((handler) -> {
          if (FluidUtil.interactWithFluidHandler(player, hand, handler)) {
            world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.BLOCKS, 1, 1);
          }
        });
      }
    }
    if (FluidUtil.getFluidHandler(player.getHeldItem(hand)).isPresent()) {
      return ActionResultType.SUCCESS;
    }
    return super.onBlockActivated(state, world, pos, player, hand, hit);
  }

  @Override
  public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
    TileEntity te = world.getTileEntity(pos);
    if (te instanceof TankTileEntity) {
      return ((TankTileEntity) te).getInternalTank().getFluid().getFluid().getAttributes().getLuminosity();
    }
    return super.getLightValue(state, world, pos);
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    TileEntity te = worldIn.getTileEntity(pos);
    if (te instanceof TankTileEntity && stack != null && stack.hasTag()) {
      ((TankTileEntity) te).readTank(stack.getTag().getCompound(Tags.TANK));
    }
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
  }

  @Deprecated
  @Override
  public boolean hasComparatorInputOverride(BlockState state) {
    return true;
  }

  @Deprecated
  @Override
  public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
    TileEntity te = worldIn.getTileEntity(pos);
    if (!(te instanceof TankTileEntity)) {
      return 0;
    }
    return ((TankTileEntity) te).comparatorStrength();
  }

  public enum TankType implements IStringSerializable {
    TANK,
    GAUGE,
    WINDOW;

    @Override
    public String getName() {
      return this.toString().toLowerCase(Locale.US);
    }
  }
}
