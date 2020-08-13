package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.ToolCore;
import slimeknights.tconstruct.library.tools.events.TinkerToolEvent;

public class AoeToolInteractionUtil {

  /**
   * Preconditions for {@link #breakExtraBlock(ItemStack, World, PlayerEntity, BlockPos, BlockPos)} and {@link #shearExtraBlock(ItemStack, World, PlayerEntity, BlockPos, BlockPos)}
   *
   * @param stack
   * @param world
   * @param player
   * @param pos
   * @param refPos
   * @return true if the extra block can be broken
   */
  private static boolean canBreakExtraBlock(ItemStack stack, World world, PlayerEntity player, BlockPos pos, BlockPos refPos) {
    // prevent calling that stuff for air blocks, could lead to unexpected behaviour since it fires events
    if (world.isAirBlock(pos)) {
      return false;
    }

    if (!stack.hasTag() || !(stack.getItem() instanceof ToolCore)) {
      return false;
    }

    // check if the block can be broken, since extra block breaks shouldn't instantly break stuff like obsidian
    // or precious ores you can't harvest while mining stone
    BlockState blockState = world.getBlockState(pos);
    Block block = blockState.getBlock();

    ToolCore toolCore = (ToolCore) stack.getItem();

    if (!toolCore.isEffective(blockState) && !ToolInteractionUtil.isToolEffectiveAgainstBlock(stack, blockState)) {
      return false;
    }

    BlockState refBlockState = world.getBlockState(refPos);
    float refStrength = refBlockState.getPlayerRelativeBlockHardness(player, world, refPos);
    float strength = blockState.getPlayerRelativeBlockHardness(player, world, pos);

    // only harvestable blocks that aren't impossibly slow to harvest
    if (!ForgeHooks.canHarvestBlock(blockState, player, world, pos) || refStrength / strength > 10f) {
      return false;
    }

    // From this point on it's clear that the player CAN break the block

    if (player.abilities.isCreativeMode) {
      block.onBlockHarvested(world, pos, blockState, player);
      FluidState fluidState = world.getFluidState(pos);

      if (block.removedByPlayer(blockState, world, pos, player, false, fluidState)) {
        block.onPlayerDestroy(world, pos, blockState);
      }

      // send update to client
      if (!world.isRemote) {
        TinkerNetwork.getInstance().sendVanillaPacket(player, new SChangeBlockPacket(world, pos));
      }
      return false;
    }
    return true;
  }

  public static void breakExtraBlock(ItemStack stack, World world, PlayerEntity player, BlockPos pos, BlockPos refPos) {
    if (!canBreakExtraBlock(stack, world, player, pos, refPos)) {
      return;
    }

    BlockState state = world.getBlockState(pos);
    Block block = state.getBlock();

    // callback to the tool the player uses. Called on both sides. This damages the tool n stuff.
    stack.onBlockDestroyed(world, state, pos, player);

    // server sided handling
    if (!world.isRemote) {
      int xp = ForgeHooks.onBlockBreakEvent(world, ((ServerPlayerEntity) player).interactionManager.getGameType(), (ServerPlayerEntity) player, pos);
      if (xp == -1) {
        return;
      }

      // serverside we reproduce ItemInWorldManager.tryHarvestBlock

      TileEntity tileEntity = world.getTileEntity(pos);
      FluidState fluidState = world.getFluidState(pos);

      // ItemInWorldManager.removeBlock
      if (block.removedByPlayer(state, world, pos, player, true, fluidState)) { // boolean is if block can be harvested, checked above
        block.onPlayerDestroy(world, pos, state);
        block.harvestBlock(world, player, pos, state, tileEntity, stack);
        block.dropXpOnBlockBreak(world, pos, xp);
      }

      TinkerNetwork.getInstance().sendVanillaPacket(player, new SChangeBlockPacket(world, pos));
    }
    // client sided handling
    else {
      // clientside we do a "this clock has been clicked on long enough to be broken" call. This should not send any new packets
      // the code above, executed on the server, sends a block-updates that give us the correct state of the block we destroy.

      // following code can be found in PlayerControllerMP.onPlayerDestroyBlock
      world.playBroadcastSound(2001, pos, Block.getStateId(state));

      FluidState fluidState = world.getFluidState(pos);

      if (block.removedByPlayer(state, world, pos, player, true, fluidState)) {
        block.onPlayerDestroy(world, pos, state);
      }
      // callback to the tool
      stack.onBlockDestroyed(world, state, pos, player);

      if (stack.getCount() == 0 && stack == player.getHeldItemMainhand()) {
        ForgeEventFactory.onPlayerDestroyItem(player, stack, Hand.MAIN_HAND);
        player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
      }

      // send an update to the server, so we get an update back
      //if(PHConstruct.extraBlockUpdates)
      ClientPlayNetHandler connection = Minecraft.getInstance().getConnection();
      assert connection != null;
      connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.STOP_DESTROY_BLOCK, pos, ((BlockRayTraceResult) Minecraft.getInstance().objectMouseOver).getFace()));
    }
  }

  public static ImmutableList<BlockPos> calcAOEBlocks(ItemStack stack, World world, PlayerEntity player, BlockPos origin, int width, int height, int depth) {
    return calcAOEBlocks(stack, world, player, origin, width, height, depth, -1);
  }

  public static ImmutableList<BlockPos> calcAOEBlocks(ItemStack stack, World world, PlayerEntity player, BlockPos origin, int width, int height, int depth, int distance) {
    // only works with toolcore because we need the raytrace call
    if (stack.isEmpty() || !(stack.getItem() instanceof ToolCore)) {
      return ImmutableList.of();
    }

    // find out where the player is hitting the block
    BlockState state = world.getBlockState(origin);

    if (!((ToolCore) stack.getItem()).isEffective(state) && !ToolInteractionUtil.isToolEffectiveAgainstBlock(stack, state)) {
      return ImmutableList.of();
    }

    if (state.getMaterial() == Material.AIR) {
      // what are you DOING?
      return ImmutableList.of();
    }

    // raytrace to get the side, but has to result in the same block
    BlockRayTraceResult mop = ((ToolCore) stack.getItem()).blockRayTrace(world, player, RayTraceContext.FluidMode.ANY);
    if (mop == null || !origin.equals(mop.getPos())) {
      mop = ((ToolCore) stack.getItem()).blockRayTrace(world, player, RayTraceContext.FluidMode.NONE);
      if (mop == null || !origin.equals(mop.getPos())) {
        return ImmutableList.of();
      }
    }

    // fire event
    TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(stack, player, state, width, height, depth, distance);

    if(event.isCanceled()) {
      return ImmutableList.of();
    }

    width = event.width;
    height = event.height;
    depth = event.depth;
    distance = event.distance;

    // we know the block and we know which side of the block we're hitting. time to calculate the depth along the different axes
    int x, y, z;
    BlockPos start = origin;
    switch (mop.getFace()) {
      case DOWN:
      case UP:
        // x y depends on the angle we look?
        Vector3i vec = player.getHorizontalFacing().getDirectionVec();
        x = vec.getX() * height + vec.getZ() * width;
        y = mop.getFace().getAxisDirection().getOffset() * -depth;
        z = vec.getX() * width + vec.getZ() * height;
        start = start.add(-x / 2, 0, -z / 2);

        if (x % 2 == 0) {
          if (x > 0 && mop.getHitVec().getX() - mop.getPos().getX() > 0.5d) {
            start = start.add(1, 0, 0);
          }
          else if (x < 0 && mop.getHitVec().getX() - mop.getPos().getX() < 0.5d) {
            start = start.add(-1, 0, 0);
          }
        }

        if (z % 2 == 0) {
          if (z > 0 && mop.getHitVec().getZ() - mop.getPos().getZ() > 0.5d) {
            start = start.add(0, 0, 1);
          }
          else if (z < 0 && mop.getHitVec().getZ() - mop.getPos().getZ() < 0.5d) {
            start = start.add(0, 0, -1);
          }
        }

        break;
      case NORTH:
      case SOUTH:
        x = width;
        y = height;
        z = mop.getFace().getAxisDirection().getOffset() * -depth;
        start = start.add(-x / 2, -y / 2, 0);

        if (x % 2 == 0 && mop.getHitVec().getX() - mop.getPos().getX() > 0.5d) {
          start = start.add(1, 0, 0);
        }

        if (y % 2 == 0 && mop.getHitVec().getY() - mop.getPos().getY() > 0.5d) {
          start = start.add(0, 1, 0);
        }

        break;
      case WEST:
      case EAST:
        x = mop.getFace().getAxisDirection().getOffset() * -depth;
        y = height;
        z = width;
        start = start.add(-0, -y / 2, -z / 2);

        if (y % 2 == 0 && mop.getHitVec().getY() - mop.getPos().getY() > 0.5d) {
          start = start.add(0, 1, 0);
        }

        if (z % 2 == 0 && mop.getHitVec().getZ() - mop.getPos().getZ() > 0.5d) {
          start = start.add(0, 0, 1);
        }

        break;
      default:
        x = y = z = 0;
    }

    ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();

    for (int xp = start.getX(); xp != start.getX() + x; xp += x / MathHelper.abs(x)) {
      for (int yp = start.getY(); yp != start.getY() + y; yp += y / MathHelper.abs(y)) {
        for (int zp = start.getZ(); zp != start.getZ() + z; zp += z / MathHelper.abs(z)) {
          // don't add the origin block
          if (xp == origin.getX() && yp == origin.getY() && zp == origin.getZ()) {
            continue;
          }

          if (distance > 0 && MathHelper.abs(xp - origin.getX()) + MathHelper.abs(yp - origin.getY()) + MathHelper.abs(zp - origin.getZ()) > distance) {
            continue;
          }

          BlockPos pos = new BlockPos(xp, yp, zp);

          boolean isEffective = ((ToolCore) stack.getItem()).isEffective(world.getBlockState(pos));
          boolean isToolEffectiveAgainstBlock = ToolInteractionUtil.isToolEffectiveAgainstBlock(stack, world.getBlockState(pos));

          if (isToolEffectiveAgainstBlock) {
            builder.add(pos);
          }
          else if (isEffective) {
            builder.add(pos);
          }
        }
      }
    }

    return builder.build();
  }

  /**
   * Same as {@link #breakExtraBlock(ItemStack, World, PlayerEntity, BlockPos, BlockPos)}, but attempts to shear the block first
   * @param stack
   * @param world
   * @param player
   * @param pos
   * @param refPos
   */
  public static void shearExtraBlock(ItemStack stack, World world, PlayerEntity player, BlockPos pos, BlockPos refPos) {
    if (!canBreakExtraBlock(stack, world, player, pos, refPos)) {
      return;
    }

    // if we cannot shear the block, just run normal block break code
    if (!ToolInteractionUtil.shearBlock(stack, world, player, pos)) {
      breakExtraBlock(stack, world, player, pos, refPos);
    }
  }
}
