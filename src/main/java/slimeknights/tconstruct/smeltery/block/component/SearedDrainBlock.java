package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import slimeknights.tconstruct.smeltery.tileentity.DrainTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;

/** Extenson to include interaction behavior */
public class SearedDrainBlock extends SmelteryIOBlock {
  public SearedDrainBlock(Properties properties) {
    super(properties, DrainTileEntity::new);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
    // success if the item is a fluid handler, regardless of if fluid moved
    ItemStack held = player.getHeldItem(hand);
    Direction face = hit.getFace();
    if (FluidUtil.getFluidHandler(held).isPresent()) {
      if (!world.isRemote()) {
        // find the player inventory and the tank fluid handler and interact
        TileEntity te = world.getTileEntity(pos);
        if (te != null) {
          te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face)
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
    } else if (ITankTileEntity.interactWithBucket(world, pos, player, hand, face, state.get(FACING).getOpposite())) {
      return ActionResultType.SUCCESS;
    }
    return ActionResultType.PASS;
  }
}
