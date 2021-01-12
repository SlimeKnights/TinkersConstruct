package slimeknights.tconstruct.smeltery.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import slimeknights.tconstruct.smeltery.tileentity.DrainTileEntity;

import javax.annotation.Nullable;

public class SearedDrainBlock extends SearedBlock {
  public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

  public SearedDrainBlock(Properties properties) {
    super(properties);
  }

  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new DrainTileEntity();
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  @Deprecated
  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    // success if the item is a fluid handler, regardless of if fluid moved
    ItemStack held = player.getHeldItem(hand);
    if (FluidUtil.getFluidHandler(held).isPresent()) {
      if (!world.isRemote()) {
        // find the player inventory and the tank fluid handler and interact
        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
          te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, hit.getFace())
            .ifPresent(handler -> player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                                        .ifPresent(inv -> {
                                          FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(held, handler, inv, Integer.MAX_VALUE, player, true);
                                          if (result.isSuccess()) {
                                            player.setHeldItem(hand, result.getResult());
                                          }
                                        }));
        }
      }
      return ActionResultType.SUCCESS;
    }
    return ActionResultType.PASS;
  }
}
