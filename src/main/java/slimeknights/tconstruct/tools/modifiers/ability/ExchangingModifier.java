package slimeknights.tconstruct.tools.modifiers.ability;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.BlockFlags;
import slimeknights.tconstruct.common.network.UpdateNeighborsPacket;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.helper.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

public class ExchangingModifier extends SingleUseModifier {
  public ExchangingModifier() {
    super(0x258474);
  }

  @Override
  public int getPriority() {
    // super low because we need to run after the shears ability modifier, and any other similar hooks
    return Short.MIN_VALUE - 20;
  }

  @Override
  public Boolean removeBlock(IModifierToolStack tool, int level, ToolHarvestContext context) {
    // must have blocks in the offhand
    ItemStack offhand = context.getLiving().getHeldItemOffhand();
    BlockState state = context.getState();
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    if ((!context.isEffective() && state.getBlockHardness(world, pos) > 0) || offhand.isEmpty() || !(offhand.getItem() instanceof BlockItem)) {
      return null;
    }

    // from this point on, we are in charge of breaking the block, start by harvesting it so piglins get mad and stuff
    PlayerEntity player = context.getPlayer();
    if (player != null) {
      state.getBlock().onBlockHarvested(world, pos, state, player);
    }

    // block is unchanged, stuck setting it to a temporary block before replacing, as otherwise we risk duplication with the TE and tryPlace will likely fail
    BlockItem blockItem = (BlockItem) offhand.getItem();
    BlockState fluidState = world.getFluidState(pos).getBlockState();
    boolean placedBlock = false;
    if (state.getBlock() == blockItem.getBlock()) {
      // the 0 in the last parameter prevents neighbor updates, meaning torches won't drop
      // this is fine as the block will be replaced in the next step by the proper block,
      // however doing it in one step is probably more ideal for block updates, hence only doing it when needed
      placedBlock = world.setBlockState(pos, fluidState, 0, 0);
      if (!placedBlock) {
        return false;
      }
    }

    // generate placing context
    Direction sideHit = context.getSideHit();
    // subtract the offsets instead of adding as the position is empty, want to "hit" a realistic location
    Vector3d hit = new Vector3d((double)pos.getX() + 0.5D - sideHit.getXOffset() * 0.5D, pos.getY() + 0.5D - sideHit.getYOffset() * 0.5D, pos.getZ() + 0.5D - sideHit.getZOffset() * 0.5D);
    BlockItemUseContext blockUseContext = new BlockItemUseContext(world, player, Hand.OFF_HAND, offhand, new BlockRayTraceResult(hit, sideHit, pos, false));
    blockUseContext.replaceClicked = true; // force replacement, even if the position is not replacable (as it most always will be)

    // swap the block, it never goes to air so things like torches will remain
    ActionResultType success = blockItem.tryPlace(blockUseContext);
    if (success.isSuccessOrConsume()) {
      if (!context.isAOE() && player != null) {
        TinkerNetwork.getInstance().sendTo(new UpdateNeighborsPacket(state, pos), player);
      }
      context.getLiving().swing(Hand.OFF_HAND, false);
      return true;
    } else if (placedBlock) {
      // notify that the fluid was placed properly, as it was suppressed earlier, and placing again will fail to hit it
      state.updateDiagonalNeighbors(world, pos, BlockFlags.BLOCK_UPDATE, 511);
      fluidState.updateNeighbours(world, pos, BlockFlags.BLOCK_UPDATE, 511);
      fluidState.updateDiagonalNeighbors(world, pos, BlockFlags.BLOCK_UPDATE, 511);
      return true;
    } else {
      // so we failed to place the new block for some reason, remove the old block to prevent dupes
      return world.setBlockState(pos, fluidState, 3);
    }
  }
}
