package slimeknights.tconstruct.library.modifiers.modules.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHook;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.module.ToolModuleHooks;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.MutableUseOnContext;

import java.util.Iterator;
import java.util.List;

/**
 * Shared logic for interaction actions which transform blocks
 */
public interface BlockTransformModule extends ModifierModule, BlockInteractionModifierHook {
  List<ModifierHook<?>> DEFAULT_HOOKS = List.of(TinkerHooks.BLOCK_INTERACT);

  /** If true, disallows targeting the bottom face of the block to transform */
  boolean requireGround();

  @Override
  default List<ModifierHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  default InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    // tool must not be broken
    if (tool.isBroken() || !tool.getDefinitionData().getModule(ToolModuleHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      return InteractionResult.PASS;
    }

    Player player = context.getPlayer();
    if (player != null && player.isShiftKeyDown()) {
      return InteractionResult.PASS;
    }

    // for hoes and shovels, must have nothing but plants above
    if (requireGround() && context.getClickedFace() == Direction.DOWN) {
      return InteractionResult.PASS;
    }

    // must actually transform
    Level world = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState original = world.getBlockState(pos);
    ItemStack stack = context.getItemInHand();
    boolean didTransform = transform(tool, context, original, true);

    // if we made a successful transform, client can stop early
    EquipmentSlot slotType = source.getSlot(context.getHand());
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
          if (transform(tool, offsetContext, newTarget, totalTransformed < 40)) {
            totalTransformed++;
            didTransform = true;

            if (world.isClientSide) {
              break;
            }

            // stop if the tool broke
            if (ToolDamageUtil.damageAnimated(tool, 1, player, slotType)) {
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

  /** Applies this transformation */
  boolean transform(IToolStackView tool, UseOnContext context, BlockState original, boolean playSound);
}
