package slimeknights.tconstruct.tools.modifiers.internal;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import slimeknights.tconstruct.library.modifiers.base.InteractionModifier;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;

import java.util.Set;

public class BlockTransformModifier extends InteractionModifier.SingleUse {
  private final Set<ToolAction> actions;
  private final SoundEvent sound;
  private final boolean requireGround;
  private final int priority;

  public BlockTransformModifier(int color, int priority, SoundEvent sound, boolean requireGround, ToolAction... actions) {
    super(color);
    this.priority = priority;
    this.actions = ImmutableSet.copyOf(actions);
    this.sound = sound;
    this.requireGround = requireGround;
  }

  @Override
  public int getPriority() {
    return priority;
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }

  @Override
  public boolean canPerformAction(IModifierToolStack tool, int level, ToolAction toolAction) {
    return actions.contains(toolAction);
  }

  @Override
  public InteractionResult afterBlockUse(IModifierToolStack tool, int level, UseOnContext context, EquipmentSlot slotType) {
    // tool must not be broken
    if (tool.isBroken()) {
      return InteractionResult.PASS;
    }

    for (ToolAction action : actions) {
      InteractionResult result = transformBlocks(tool, context, action, sound, requireGround);
      if (result.consumesAction()) {
        return result;
      }
    }
    return InteractionResult.PASS;
  }

  /**
   * Tills blocks within an AOE area
   * @param context   Harvest context
   * @param action  Tool type used
   * @param sound     Sound to play on tilling
   * @return  Action result from tilling
   */
  public static InteractionResult transformBlocks(IModifierToolStack tool, UseOnContext context, ToolAction action, SoundEvent sound, boolean requireGround) {
    Player player = context.getPlayer();
    if (player != null && player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }

    // for hoes and shovels, must have nothing but plants above
    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();
    if (requireGround) {
      if (context.getClickedFace() == Direction.DOWN) {
        return InteractionResult.PASS;
      }
      Material material = world.getBlockState(pos.above()).getMaterial();
      if (!material.isReplaceable() && material != Material.PLANT) {
        return InteractionResult.PASS;
      }
    }

    // must actually transform
    BlockState original = world.getBlockState(pos);
    ItemStack stack = context.getItemInHand();
    BlockState transformed = original.getToolModifiedState(world, pos, player, stack, action);
    boolean isCampfire = false;
    boolean didTransform = transformed != null;
    Level level = context.getLevel();
    if (transformed == null) {
      // shovel special case: campfires
      if (action == ToolActions.SHOVEL_FLATTEN && original.getBlock() instanceof CampfireBlock && original.getValue(CampfireBlock.LIT)) {
        isCampfire = true;
        if (!world.isClientSide) {
          if (!level.isClientSide()) {
            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
          }
          CampfireBlock.dowse(player, world, pos, original);
        }
        transformed = original.setValue(CampfireBlock.LIT, false);
      } else {
        // try to match the clicked block
        transformed = world.getBlockState(pos);
      }
    }

    // if we made a successful transform, client can stop early
    InteractionHand hand = context.getHand();
    if (didTransform || isCampfire) {
      if (world.isClientSide) {
        return InteractionResult.SUCCESS;
      }

      // change the block state
      world.setBlock(pos, transformed, Block.UPDATE_ALL_IMMEDIATE);
      if (requireGround) {
        world.destroyBlock(pos.above(), true);
      }

      // play sound
      if (!isCampfire) {
        world.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
      }

      // if the tool breaks or it was a campfire, we are done
      if (ToolDamageUtil.damage(tool, 1, player, stack) || isCampfire) {
        if (player != null) {
          player.broadcastBreakEvent(hand);
        }
        return InteractionResult.SUCCESS;
      }
    }

    // AOE transforming, run even if we did not transform the center
    // note we consider anything effective, as hoes are not effective on all tillable blocks
    int totalTransformed = 0;
    if (player != null && !tool.isBroken()) {
      for (BlockPos newPos : tool.getDefinition().getData().getAOE().getAOEBlocks(tool, stack, player, original, world, pos, context.getClickedFace(), IAreaOfEffectIterator.AOEMatchType.TRANSFORM)) {
        if (pos.equals(newPos)) {
          //in case it attempts to run the same position twice
          continue;
        }

        // hoes and shovels: air or plants above
        BlockPos above = newPos.above();
        if (requireGround) {
          Material material = world.getBlockState(above).getMaterial();
          if (!material.isReplaceable() && material != Material.PLANT) {
            continue;
          }
        }

        // block type must be the same
        BlockState newState = world.getBlockState(newPos).getToolModifiedState(world, newPos, player, stack, action);
        if (newState != null && transformed.getBlock() == newState.getBlock()) {
          if (world.isClientSide) {
            return InteractionResult.SUCCESS;
          }
          totalTransformed++;
          world.setBlock(newPos, newState, Block.UPDATE_ALL_IMMEDIATE);
          // limit to playing 40 sounds, thats more than enough for most transforms
          if (totalTransformed < 40) {
            world.playSound(null, newPos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
          }

          // if required, break the block above (typically plants)
          if (requireGround) {
            world.destroyBlock(above, true);
          }

          // stop if the tool broke
          if (ToolDamageUtil.damageAnimated(tool, 1, player, hand)) {
            break;
          }
        }
      }
      if (totalTransformed > 0) {
        player.sweepAttack();
      }
    }

    // if anything happened, return success
    return didTransform || totalTransformed > 0 ? InteractionResult.SUCCESS : InteractionResult.PASS;
  }
}
