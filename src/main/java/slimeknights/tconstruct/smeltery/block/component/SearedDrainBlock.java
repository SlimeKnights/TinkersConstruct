package slimeknights.tconstruct.smeltery.block.component;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraftforge.fluids.SimulationResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import slimeknights.tconstruct.smeltery.tileentity.DrainTileEntity;
import slimeknights.tconstruct.smeltery.tileentity.ITankTileEntity;

/** Extenson to include interaction behavior */
public class SearedDrainBlock extends SmelteryIOBlock {
  public SearedDrainBlock(Settings properties) {
    super(properties, DrainTileEntity::new);
  }

  @SuppressWarnings("deprecation")
  @Deprecated
  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    throw new RuntimeException("Needs Implementing!");
    /*// success if the item is a fluid handler, regardless of if fluid moved
    ItemStack held = player.getStackInHand(hand);
    Direction face = hit.getSide();
    if (FluidUtil.getFluidHandler(held).isPresent()) {
      if (!world.isClient()) {
        // find the player inventory and the tank fluid handler and interact
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null) {
          te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, face)
            .ifPresent(handler -> player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                                        .ifPresent(inv -> {
                                          SimulationResult result = FluidUtil.tryEmptyContainerAndStow(held, handler, inv, Integer.MAX_VALUE, player, true);
                                          if (result.isSuccess()) {
                                            player.setStackInHand(hand, result.getResult());
                                          }
                                        }));
        }
      }
      return ActionResult.SUCCESS;
    } else if (ITankTileEntity.interactWithBucket(world, pos, player, hand, face, state.get(FACING).getOpposite())) {
      return ActionResult.SUCCESS;
    }
    return ActionResult.PASS;*/
  }
}
