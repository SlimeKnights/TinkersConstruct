package slimeknights.tconstruct.library.modifiers.modules.behavior;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.AreaOfEffectHighlightModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator.AOEMatchType;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.MutableUseOnContext;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * Shared logic for interaction actions which transform blocks
 */
public interface BlockTransformModule extends ModifierModule, BlockInteractionModifierHook, AreaOfEffectHighlightModifierHook, DisplayNameModifierHook {
  List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<BlockTransformModule>defaultHooks(ModifierHooks.BLOCK_INTERACT, ModifierHooks.AOE_HIGHLIGHT, ModifierHooks.DISPLAY_NAME);

  /** If true, disallows targeting the bottom face of the block to transform */
  boolean requireGround();

  @Override
  default List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  default Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    return InteractionSource.formatModifierName(tool, entry.getModifier(), name);
  }

  @Override
  default InteractionResult afterBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    // tool must not be broken
    if (tool.isBroken() || !tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
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
    if (!tool.isBroken()) {
      int totalTransformed = 0;
      Iterator<BlockPos> aoePos = tool.getHook(ToolHooks.AOE_ITERATOR).getBlocks(tool, context, original, AOEMatchType.TRANSFORM).iterator();
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
            if (ToolDamageUtil.damage(tool, 1, player, context.getItemInHand())) {
              if (player != null) {
                player.broadcastBreakEvent(context.getHand());
              }
              break;
            }
          }
        } while (aoePos.hasNext());

        // sweep attack if we transformed any
        if (totalTransformed > 0 && player != null) {
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
