package slimeknights.tconstruct.library.modifiers.fluid.block;

import lombok.Getter;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import slimeknights.tconstruct.compat.neoforged.neoforge.common.ForgeHooks;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.fluid.EffectLevel;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffect;
import slimeknights.tconstruct.library.modifiers.fluid.FluidEffectContext;

/** Fluid effect causing a ranged block interaction */
@Getter
public enum BlockInteractFluidEffect implements FluidEffect<FluidEffectContext.Block> {
  INSTANCE;

  private final SingletonLoader<BlockInteractFluidEffect> loader = new SingletonLoader<>(this);

  /** Damages the stack in the context if needed */
  private static void damageIfNeeded(UseOnContext context) {
    ItemStack stack = context.getItemInHand();
    Level level = context.getLevel();
    // vanilla tools tend not to call the proper damage methods if player is null, so just manually damage the stack
    // we expect modded items will have the same bug, so just go ahead and damage them. On the chance it works, they get 2 damage, no big deal
    // our tools we know work so ignore them
    if (!level.isClientSide && context.getPlayer() == null && stack.isDamageableItem() && !stack.is(TinkerTags.Items.MODIFIABLE)) {
      // unable to call Forge damageItem as that needs entity access, but its just vanilla broken anyways, right?
      stack.setDamageValue(stack.getDamageValue() + 1);
      // calling methods again instead of using return as return may be incorrect for custom broken stacks
      if (stack.getDamageValue() >= stack.getMaxDamage()) {
        // but that won't happen, right? will need to consider another workaround in that case.
        stack.shrink(1);
        stack.setDamageValue(0);
        level.playSound(null, context.getClickedPos(), SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
    }
  }

  /** Based on {@link net.minecraft.server.level.ServerPlayerGameMode#useItemOn(ServerPlayer, Level, ItemStack, InteractionHand, BlockHitResult)} */
  @Override
  public float apply(FluidStack fluid, EffectLevel level, FluidEffectContext.Block context, FluidAction action) {
    // inside world border?
    Level world = context.getLevel();
    BlockPos pos = context.getBlockPos();
    if (!world.getWorldBorder().isWithinBounds(pos)) {
      return 0;
    }
    // block usable?
    BlockState state = context.getBlockState();
    FeatureFlagSet enabled = world.enabledFeatures();
    if (!state.getBlock().isEnabled(enabled)) {
      return 0;
    }
    // we have no way of checking if clicking the block does anything without actually clicking, so just always charge the full amount
    if (action.simulate()) {
      return 1;
    }

    // determine if sneak bypass is enabled
    LivingEntity entity = context.getEntity();
    Player player = context.getPlayer();
    boolean skipBlock = false;
    if (player != null) {
      skipBlock = player.isSecondaryUseActive() && (!player.getMainHandItem().doesSneakBypassUse(world, pos, player) || !player.getOffhandItem().doesSneakBypassUse(player.level(), pos, player));
    } else if (entity != null) {
      skipBlock = entity.isShiftKeyDown() && (!entity.getMainHandItem().isEmpty() || !entity.getOffhandItem().isEmpty());
    }

    // interact with both hands if we have an entity, just main hand otherwise
    BlockHitResult hitResult = context.getHitResult();
    for (InteractionHand hand : entity == null ? new InteractionHand[] {InteractionHand.MAIN_HAND} : InteractionHand.values()) {
      // find what item to use
      ItemStack heldItem = context.getStack();
      if (entity != null) {
        heldItem = entity.getItemInHand(hand);
      }
      // if the item is not enabled, give up entirely. Not sure why, its just what vanilla does
      if (!heldItem.isItemEnabled(enabled)) {
        return 0;
      }

      // try the event
      TriState useItem = TriState.DEFAULT;
      TriState useBlock = TriState.DEFAULT;
      if (player != null) {
        PlayerInteractEvent.RightClickBlock event = ForgeHooks.onRightClickBlock(player, hand, pos, hitResult);
        if (event.isCanceled()) {
          // if successful, swing hand
          if (event.getCancellationResult().consumesAction()) {
            if (entity != null) {
              entity.swing(hand, true);
            }
            return 1;
          }
          return 0;
        }
        useItem = event.getUseItem();
        useBlock = event.getUseBlock();
      }
      // skipped: never spectator mode if we made it this far

      // use the item
      UseOnContext useContext = new UseOnContext(world, player, hand, heldItem, hitResult);
      if (useItem != TriState.FALSE && !heldItem.isEmpty()) {
        InteractionResult result = heldItem.onItemUseFirst(useContext);
        if (result != InteractionResult.PASS) {
          if (result.consumesAction()) {
            if (entity != null) {
              entity.swing(hand, true);
            }
            damageIfNeeded(useContext);
            return 1;
          }
          return 0; // failure exits the loop
        }
      }

      // click the block
      ItemStack original = heldItem.copy();
      if (player != null && (useBlock == TriState.TRUE || (useItem == TriState.DEFAULT && !skipBlock))) {
        ItemInteractionResult blockResult = state.useItemOn(heldItem, world, player, hand, hitResult);
        if (blockResult.consumesAction()) {
          if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, original);
          }
          player.swing(hand, true);
          return 1;
        }
        if (blockResult == ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION && hand == InteractionHand.MAIN_HAND) {
          InteractionResult result = state.useWithoutItem(world, player, hitResult);
          if (result.consumesAction()) {
            if (player instanceof ServerPlayer serverPlayer) {
              CriteriaTriggers.DEFAULT_BLOCK_USE.trigger(serverPlayer, pos);
            }
            player.swing(hand, true);
            return 1;
          }
        }
      }

      // post block item usage
      if (useItem == TriState.TRUE || (useItem == TriState.DEFAULT && !heldItem.isEmpty() && (player == null || !player.getCooldowns().isOnCooldown(heldItem.getItem())))) {
        InteractionResult result;
        if (player != null && player.isCreative()) {
          int oldCount = heldItem.getCount();
          result = heldItem.useOn(useContext);
          heldItem.setCount(oldCount);
        } else {
          result = heldItem.useOn(useContext);
          damageIfNeeded(useContext);
        }
        if (result != InteractionResult.PASS) {
          if (result.consumesAction()) {
            if (player instanceof ServerPlayer serverPlayer) {
              CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, heldItem);
            }
            if (entity != null) {
              entity.swing(hand, true);
            }
            return 1;
          }
          return 0; // failure exits the loop
        }
      }
    }
    return 0;
  }

  @Override
  public Component getDescription(RegistryAccess registryAccess) {
    return Component.translatable(FluidEffect.getTranslationKey(getLoader()) + ".block");
  }
}
