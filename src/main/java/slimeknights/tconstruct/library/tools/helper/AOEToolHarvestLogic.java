package slimeknights.tconstruct.library.tools.helper;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.tools.events.TinkerToolEvent;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class AOEToolHarvestLogic extends ToolHarvestLogic {
  /** Instance for an AOE tool that only works on a single block, extended by modifiers */
  public static final AOEToolHarvestLogic SMALL_TOOL = new AOEToolHarvestLogic(1, 1, 1);

  /** Instance for an AOE tool like a hammer or a excavator */
  public static final AOEToolHarvestLogic LARGE_TOOL = new AOEToolHarvestLogic(3, 3, 1);

  private final int width;
  private final int height;
  private final int depth;

  @Override
  public List<BlockPos> getAOEBlocks(ToolStack tool, ItemStack stack, World world, PlayerEntity player, BlockPos origin) {
    return calculateAOEBlocks(tool, stack, world, player, origin, width, height, depth);
  }

  /**
   * Tills blocks within an AOE area
   * @param context   Harvest context
   * @param toolType  Tool type used
   * @param sound     Sound to play on tilling
   * @return  Action result from tilling
   */
  public ActionResultType tillBlocks(ItemUseContext context, ToolType toolType, SoundEvent sound) {
    PlayerEntity player = context.getPlayer();

    if (player == null || player.isSneaking()) {
      return ActionResultType.PASS;
    }

    Direction sideHit = context.getFace();

    if (sideHit == Direction.DOWN) {
      return ActionResultType.PASS;
    }

    Hand hand = context.getHand();
    ItemStack stack = player.getHeldItem(hand);
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return ActionResultType.FAIL;
    }

    World world = context.getWorld();
    BlockPos pos = context.getPos();
    BlockState tilledState = world.getBlockState(pos).getToolModifiedState(world, pos, player, stack, toolType);

    if (tilledState == null) {
      return ActionResultType.PASS;
    }

    BlockPos abovePos = pos.up();
    BlockState aboveState = world.getBlockState(abovePos);
    if (aboveState.isOpaqueCube(world, abovePos)) {
      return ActionResultType.PASS;
    }

    if (world.isRemote) {
      return ActionResultType.SUCCESS;
    }

    world.setBlockState(pos, tilledState, Constants.BlockFlags.DEFAULT_AND_RERENDER);

    Material aboveMaterial = aboveState.getMaterial();

    if (aboveMaterial == Material.PLANTS || aboveMaterial == Material.TALL_PLANTS) {
      world.destroyBlock(abovePos, true);
    }

    world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
    int damage = 1;
    int durability = tool.getCurrentDurability();
    for (BlockPos newPos : getAOEBlocks(tool, stack, world, player, pos)) {
      if (pos.equals(newPos)) {
        //in case it attempts to run the same position twice
        continue;
      }
      // if we finished using the tool, stop
      if (damage >= durability) {
        break;
      }

      BlockState stateAbove = world.getBlockState(newPos.up());
      if (!stateAbove.isOpaqueCube(world, newPos.up()) && tilledState == world.getBlockState(newPos).getToolModifiedState(world, newPos, player, stack, toolType)) {
        damage += 1;

        world.setBlockState(newPos, tilledState, Constants.BlockFlags.DEFAULT_AND_RERENDER);

        aboveMaterial = stateAbove.getMaterial();

        if (aboveMaterial == Material.PLANTS || aboveMaterial == Material.TALL_PLANTS) {
          world.destroyBlock(newPos.up(), true);
        }

        world.playSound(null, newPos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }
    }

    ToolDamageUtil.damageAnimated(tool, damage, player);
    return ActionResultType.SUCCESS;
  }


  /* Utils */


  /**
   *  Calculates the blocks that the AOE tool can affect
   *
   * @param stack the tool stack
   * @param world World Instance
   * @param player Player instance
   * @param origin Origin Position
   * @param width The Width
   * @param height The Height
   * @param depth The Depth
   * @return a list of BlockPoses
   */
  public static List<BlockPos> calculateAOEBlocks(ToolStack tool, ItemStack stack, World world, PlayerEntity player, BlockPos origin, int width, int height, int depth) {
    return calculateAOEBlocks(tool, stack, world, player, origin, width, height, depth, -1);
  }

  /**
   *  Calculates the blocks that the AOE tool can affect
   *
   * @param stack the tool stack
   * @param world World Instance
   * @param player Player instance
   * @param origin Origin Position
   * @param width The Width
   * @param height The Height
   * @param depth The Depth
   * @param distance The Distance
   * @return a list of BlockPoses
   */
  public static List<BlockPos> calculateAOEBlocks(ToolStack tool, ItemStack stack, World world, PlayerEntity player, BlockPos origin, int width, int height, int depth, int distance) {
    // only works with modifiable harvest
    if (stack.isEmpty() || tool.isBroken()) {
      return Collections.emptyList();
    }

    // find out where the player is hitting the block
    // air will break the raytrace
    BlockState state = world.getBlockState(origin);
    if (state.getMaterial() == Material.AIR) {
      return Collections.emptyList();
    }
    // if unharvestable, skip
    if (!isEffective(tool, stack, state)) {
      return Collections.emptyList();
    }

    // raytrace to get the side, but has to result in the same block
    BlockRayTraceResult mop = ToolCore.blockRayTrace(world, player, RayTraceContext.FluidMode.ANY);
    if (!origin.equals(mop.getPos())) {
      mop = ToolCore.blockRayTrace(world, player, RayTraceContext.FluidMode.NONE);
      if (!origin.equals(mop.getPos())) {
        return Collections.emptyList();
      }
    }

    // fire event
    // TODO: switch to modifier hook
    TinkerToolEvent.ExtraBlockBreak event = TinkerToolEvent.ExtraBlockBreak.fireEvent(stack, player, state, width, height, depth, distance);
    if (event.isCanceled()) {
      return ImmutableList.of();
    }

    width = event.getWidth();
    height = event.getHeight();
    depth = event.getDepth();
    distance = event.getDistance();

    // we know the block and we know which side of the block we're hitting. time to calculate the depth along the different axes
    int x, y, z;
    BlockPos start = origin;
    Direction face = mop.getFace();
    int offset = face.getAxisDirection().getOffset();
    Vector3d hitVec = mop.getHitVec();
    switch (face.getAxis()) {
      case Y:
        // x y depends on the angle we look
        Vector3i vec = player.getHorizontalFacing().getDirectionVec();
        x = vec.getX() * height + vec.getZ() * width;
        y = offset * -depth;
        z = vec.getX() * width + vec.getZ() * height;
        start = start.add(-x / 2, 0, -z / 2);
        // for even numbers, offset based on where we hit
        if (x % 2 == 0) {
          if (x > 0 && hitVec.getX() - origin.getX() > 0.5d) {
            start = start.add(1, 0, 0);
          }
          else if (x < 0 && hitVec.getX() - origin.getX() < 0.5d) {
            start = start.add(-1, 0, 0);
          }
        }
        if (z % 2 == 0) {
          if (z > 0 && hitVec.getZ() - origin.getZ() > 0.5d) {
            start = start.add(0, 0, 1);
          }
          else if (z < 0 && hitVec.getZ() - origin.getZ() < 0.5d) {
            start = start.add(0, 0, -1);
          }
        }
        break;

      case Z:
        x = width;
        y = height;
        z = mop.getFace().getAxisDirection().getOffset() * -depth;
        start = start.add(-x / 2, -y / 2, 0);
        // for even numbers, offset based on where we hit
        if (x % 2 == 0 && hitVec.getX() - origin.getX() > 0.5d) {
          start = start.add(1, 0, 0);
        }
        if (y % 2 == 0 && hitVec.getY() - origin.getY() > 0.5d) {
          start = start.add(0, 1, 0);
        }
        break;
      case X:
        x = offset * -depth;
        y = height;
        z = width;
        start = start.add(-0, -y / 2, -z / 2);
        // for even numbers, offset based on where we hit
        if (y % 2 == 0 && hitVec.getY() - origin.getY() > 0.5d) {
          start = start.add(0, 1, 0);
        }
        if (z % 2 == 0 && hitVec.getZ() - origin.getZ() > 0.5d) {
          start = start.add(0, 0, 1);
        }
        break;
      default:
        x = y = z = 0;
    }

    // start building the position list
    ImmutableList.Builder<BlockPos> builder = ImmutableList.builder();
    int endX = start.getX() + x, endY = start.getY() + y, endZ = start.getZ() + z;
    int offsetX = Util.sign(x), offsetY = Util.sign(y), offsetZ = Util.sign(z);
    for (int xp = start.getX(); xp != endX; xp += offsetX) {
      for (int yp = start.getY(); yp != endY; yp += offsetY) {
        for (int zp = start.getZ(); zp != endZ; zp += offsetZ) {
          // don't add the origin block
          if (xp == origin.getX() && yp == origin.getY() && zp == origin.getZ()) {
            continue;
          }

          // if checking distance, make sure the distance is not too far
          if (distance > 0 && MathHelper.abs(xp - origin.getX()) + MathHelper.abs(yp - origin.getY()) + MathHelper.abs(zp - origin.getZ()) > distance) {
            continue;
          }

          // if valid, add it
          BlockPos pos = new BlockPos(xp, yp, zp);
          if (isEffective(tool, stack, world.getBlockState(pos))) {
            builder.add(pos);
          }
        }
      }
    }
    return builder.build();
  }
}
