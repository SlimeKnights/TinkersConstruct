package slimeknights.tconstruct.tools.modifiers.ability.interaction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ToolAction;
import slimeknights.tconstruct.library.modifiers.impl.InteractionModifier;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.MutableUseOnContext;

import java.util.Iterator;

@RequiredArgsConstructor
public class BlockTransformModifier extends InteractionModifier.SingleUse {
  @Getter
  private final int priority;
  private final ToolAction action;
  private final SoundEvent sound;
  private final boolean requireGround;
  private final int eventId;

  public BlockTransformModifier(int priority, ToolAction action, SoundEvent sound, boolean requireGround) {
    this(priority, action, sound, requireGround, -1);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return priority > Short.MIN_VALUE;
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, int level, ToolAction toolAction) {
    return action == toolAction;
  }

  @Override
  public InteractionResult afterBlockUse(IToolStackView tool, int level, UseOnContext context, EquipmentSlot slotType) {
    // tool must not be broken
    if (tool.isBroken()) {
      return InteractionResult.PASS;
    }

    Player player = context.getPlayer();
    if (player != null && player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }

    // for hoes and shovels, must have nothing but plants above
    if (requireGround && context.getClickedFace() == Direction.DOWN) {
      return InteractionResult.PASS;
    }

    // must actually transform
    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState original = world.getBlockState(pos);
    ItemStack stack = context.getItemInHand();
    boolean didTransform = transform(context, original, true);

    // if we made a successful transform, client can stop early
    if (didTransform) {
      if (world.isClientSide) {
        return InteractionResult.SUCCESS;
      }

      // if the tool breaks or it was a campfire, we are done
      if (ToolDamageUtil.damage(tool, 1, player, stack)) {
        if (player != null) {
          player.broadcastBreakEvent(slotType);
        }
        return InteractionResult.CONSUME;
      }
    }

    // AOE transforming, run even if we did not transform the center
    // note we consider anything effective, as hoes are not effective on all tillable blocks
    if (player != null && !tool.isBroken()) {
      int totalTransformed = 0;
      Iterator<BlockPos> aoePos = tool.getDefinition().getData().getAOE().getBlocks(tool, stack, player, original, world, pos, context.getClickedFace(), IAreaOfEffectIterator.AOEMatchType.TRANSFORM).iterator();
      if (aoePos.hasNext()) {
        MutableUseOnContext offsetContext = new MutableUseOnContext(context);
        do {
          BlockPos newPos = aoePos.next();
          if (pos.equals(newPos)) {
            continue;
          }

          // try interacting with the new position
          offsetContext.setOffsetPos(newPos);

          BlockState newTarget = world.getBlockState(newPos);

          // limit to playing 40 sounds, that's more than enough for most transforms
          if (transform(offsetContext, newTarget, totalTransformed < 40)) {
            totalTransformed++;
            didTransform = true;

            // stop if the tool broke
            if (world.isClientSide || ToolDamageUtil.damageAnimated(tool, 1, player, slotType)) {
              break;
            }
          }
        } while (aoePos.hasNext());

        // sweep attack if we transformed any
        if (totalTransformed > 0) {
          player.sweepAttack();
        }
      }
    }

    // if anything happened, return success
    return didTransform ? InteractionResult.sidedSuccess(world.isClientSide) : InteractionResult.PASS;
  }

  /** Transforms the given block */
  protected boolean transform(UseOnContext context, BlockState original, boolean playSound) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockPos above = pos.above();

    // hoes and shovels: air or plants above
    if (requireGround) {
      Material material = level.getBlockState(above).getMaterial();
      if (!material.isReplaceable() && material != Material.PLANT) {
        return false;
      }
    }

    // normal action transform
    Player player = context.getPlayer();
    BlockState transformed = original.getToolModifiedState(level, pos, player, context.getItemInHand(), action);
    if (transformed != null) {
      if (playSound) {
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (eventId != -1) {
          level.levelEvent(player, eventId, pos, 0);
        }
      }
      if (!level.isClientSide) {
        level.setBlock(pos, transformed, Block.UPDATE_ALL_IMMEDIATE);
        if (requireGround) {
          level.destroyBlock(above, true);
        }
      }
      return true;
    }
    return false;
  }
}
