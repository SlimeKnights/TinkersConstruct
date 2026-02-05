package slimeknights.tconstruct.tools.modifiers.ability.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.common.network.UpdateNeighborsPacket;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.mining.RemoveBlockModifierHook;
import slimeknights.tconstruct.library.modifiers.impl.NoLevelsModifier;
import slimeknights.tconstruct.library.module.ModuleHookMap.Builder;
import slimeknights.tconstruct.library.tools.capability.BlockItemProviderCapability;
import slimeknights.tconstruct.library.tools.capability.ToolBlockItemProviderHook;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;

public class ExchangingModifier extends NoLevelsModifier implements RemoveBlockModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.REMOVE_BLOCK);
  }

  @Override
  public int getPriority() {
    // super low because we need to run after the shears ability modifier, and any other similar hooks
    return Short.MIN_VALUE - 20;
  }

  @Nullable
  @Override
  public Boolean removeBlock(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
    // We check the offhand first
    ItemStack item = context.getLiving().getOffhandItem();
    BlockState state = context.getState();
    Level world = context.getWorld();
    BlockPos pos = context.getPos();
    LivingEntity entity = context.getLiving();
    if (item.isEmpty()) return null;

    BlockItemProviderCapability blockProvider = BlockItemProviderCapability.getBlockProvider(item);
    ItemStack backingStack = blockProvider == null ? ItemStack.EMPTY : blockProvider.getBlockItemStack(item, entity);
    BlockItem blockItem = null;

    // blockProvider != null is technically always true, but this makes the static analysis happy as it doesn't know that ItemStack.EMPTY.getItem() instanceof BlockItem is always false.
    if (blockProvider != null) {
      blockItem = BlockItemProviderCapability.verifyBlockItem(backingStack, blockProvider);
    }

    // if the thing in our offhand cannot provide at all or cannot currently provide then check
    // the mainhand next (this tool), in case we have glowing or a similar modifier to provide blocks.
    if (blockItem == null) {
      item = context.getLiving().getMainHandItem();
      // skip forges cap system and go to the tinkers hook because we know this is a tinkers tool
      blockProvider = new ToolBlockItemProviderHook.CapabilityImpl(tool);
      backingStack = blockProvider.getBlockItemStack(item, entity);
      blockItem = BlockItemProviderCapability.verifyBlockItem(backingStack, blockProvider);

      // nothing could provide
      if (blockItem == null) return null;
    }

    // immediately do a defensive copy of the stack.
    ItemStack fakeStack = backingStack.copyWithCount(1);

    // if we are an adventure mode player, check if we are allowed to place it.
    // Note that we check the mined position as the block we are placing 'against', which could be considered variance against vanilla but it is the block that make the most sense here.
    if (entity instanceof Player player && !player.mayBuild() && !fakeStack.hasAdventureModePlaceTagForBlock(BuiltInRegistries.BLOCK, new BlockInWorld(world, pos, false))) {
      return null;
    }

    // from this point on, we are in charge of breaking the block, start by harvesting it so piglins get mad and stuff
    Player player = context.getPlayer();
    if (player != null) {
      state.getBlock().playerWillDestroy(world, pos, state, player);
    }

    // block is unchanged? stuck setting it to a temporary block before replacing, as otherwise we risk duplication with the TE and tryPlace will likely fail
    BlockState fluidState = world.getFluidState(pos).createLegacyBlock();
    boolean placedBlock = false;
    if (state.getBlock() == blockItem.getBlock()) {
      // the 0 in the last parameter prevents neighbor updates, meaning torches won't drop
      // this is fine as the block will be replaced in the next step by the proper block,
      // however doing it in one step is probably more ideal for block updates, hence only doing it when needed
      placedBlock = world.setBlock(pos, fluidState, 0, 0);
      if (!placedBlock) {
        return false;
      }
    }

    // generate placing context
    // use opposite side for hit as that produces better slab placement
    BlockPlaceContext blockUseContext = new BlockPlaceContext(world, player, InteractionHand.OFF_HAND, fakeStack, Util.createTraceResult(pos, context.getSideHit().getOpposite(), true));
    blockUseContext.replaceClicked = true; // force replacement, even if the position is not replacable (as it most always will be)

    // swap the block, it never goes to air so things like torches will remain
    InteractionResult success = blockItem.place(blockUseContext);

    // If our fake stack is now empty then it got placed (or otherwise consumed), so consume an item from the provider.
    if (fakeStack.isEmpty()) {
      blockProvider.consume(item, backingStack, entity);
    }

    if (success.consumesAction()) {
      if (!context.isAOE() && player != null) {
        TinkerNetwork.getInstance().sendTo(new UpdateNeighborsPacket(state, pos), player);
      }
      context.getLiving().swing(InteractionHand.OFF_HAND, false);
      return true;
    } else if (placedBlock) {
      // notify that the fluid was placed properly, as it was suppressed earlier, and placing again will fail to hit it
      state.updateIndirectNeighbourShapes(world, pos, Block.UPDATE_CLIENTS, 511);
      fluidState.updateNeighbourShapes(world, pos, Block.UPDATE_CLIENTS, 511);
      fluidState.updateIndirectNeighbourShapes(world, pos, Block.UPDATE_CLIENTS, 511);
      return true;
    } else {
      // so we failed to place the new block for some reason, remove the old block to prevent dupes
      return world.setBlock(pos, fluidState, 3);
    }
  }

}
