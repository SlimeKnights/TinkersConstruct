package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolActions;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.common.network.TinkerNetwork;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.definition.aoe.IAreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;

import java.util.Collections;
import java.util.Objects;

/**
 * External logic for the ToolCore that handles mining calculations and breaking blocks.
 * TODO: needs big updates
 */
public class ToolHarvestLogic {
  private ToolHarvestLogic() {}

  /**
   * Gets the amount of damage this tool should take for the given block state
   * @param tool   Tool to check
   * @param state  State to check
   * @return  Damage to deal
   */
  public static int getDamage(ToolStack tool, Level world, BlockPos pos, BlockState state) {
    if (state.getDestroySpeed(world, pos) == 0) {
      // tools that can shear take damage from instant break for non-fire
      return (!state.is(BlockTags.FIRE) && ModifierUtil.canPerformAction(tool, ToolActions.SHEARS_DIG)) ? 1 : 0;
    }
    // if it lacks the harvest tag, it takes double damage (swords for instance)
    return tool.hasTag(TinkerTags.Items.HARVEST_PRIMARY) ? 1 : 2;
  }

  /**
   * Checks if the given tool is effective on the given state
   * @param tool   Tool to check
   * @param state  State to check
   * @return  True if this tool is effective
   */
  public static boolean isEffective(IToolStackView tool, BlockState state) {
    // must not be broken, and the tool definition must be effective
    return !tool.isBroken() && tool.getDefinition().getData().getHarvestLogic().isEffective(tool, state);
  }

  /**
   * Calculates the dig speed for the given blockstate
   *
   * @param stack the tool stack
   * @param state the block state to check
   * @return the dig speed
   */
  public static float getDestroySpeed(ItemStack stack, BlockState state) {
    if(!stack.hasTag()) {
      return 1f;
    }
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return 0.3f;
    }
    return tool.getDefinition().getData().getHarvestLogic().getDestroySpeed(tool, state);
  }

  /**
   * Actually removes a block from the world. Cloned from {@link net.minecraft.server.level.ServerPlayerGameMode}
   * @param tool     Tool used in breaking
   * @param context  Harvest context
   * @return  True if the block was removed
   */
  private static boolean removeBlock(IToolStackView tool, ToolHarvestContext context) {
    Boolean removed = null;
    if (!tool.isBroken()) {
      for (ModifierEntry entry : tool.getModifierList()) {
        removed = entry.getModifier().removeBlock(tool, entry.getLevel(), context);
        if (removed != null) {
          break;
        }
      }
    }
    // if not removed by any modifier, remove with normal forge hook
    BlockState state = context.getState();
    ServerLevel world = context.getWorld();
    BlockPos pos = context.getPos();
    if (removed == null) {
      removed = state.onDestroyedByPlayer(world, pos, context.getPlayer(), context.canHarvest(), world.getFluidState(pos));
    }
    // if removed by anything, finally destroy it
    if (removed) {
      state.getBlock().destroy(world, pos, state);
    }
    return removed;
  }

  /**
   * Called to break a block using this tool
   * @param tool      Tool instance
   * @param stack     Stack instance for vanilla functions
   * @param context   Harvest context
   * @return  True if broken
   */
  protected static boolean breakBlock(ToolStack tool, ItemStack stack, ToolHarvestContext context) {
    // have to rerun the event to get the EXP, also ensures extra blocks broken get EXP properly
    ServerPlayer player = Objects.requireNonNull(context.getPlayer());
    ServerLevel world = context.getWorld();
    BlockPos pos = context.getPos();
    GameType type = player.gameMode.getGameModeForPlayer();
    int exp = ForgeHooks.onBlockBreakEvent(world, type, player, pos);
    if (exp == -1) {
      return false;
    }
    // checked after the Forge hook, so we have to recheck
    if (player.blockActionRestricted(world, pos, type)) {
      return false;
    }

    // creative just removes the block
    if (player.isCreative()) {
      removeBlock(tool, context);
      return true;
    }

    // determine damage to do
    BlockState state = context.getState();
    int damage = getDamage(tool, world, pos, state);

    // remove the block
    boolean canHarvest = context.canHarvest();
    BlockEntity te = canHarvest ? world.getBlockEntity(pos) : null; // ensures tile entity is fetched so its around for afterBlockBreak
    boolean removed = removeBlock(tool, context);

    // harvest drops
    Block block = state.getBlock();
    if (removed && canHarvest) {
      block.playerDestroy(world, player, pos, state, te, stack);
    }

    // drop XP
    if (removed && exp > 0) {
      block.popExperience(world, pos, exp);
    }

    // handle modifiers if not broken
    // broken means we are using "empty hand"
    if (!tool.isBroken()) {
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().afterBlockBreak(tool, entry.getLevel(), context);
      }
      ToolDamageUtil.damageAnimated(tool, damage, player);
    }

    return true;
  }

  /**
   * Breaks a secondary block
   * @param tool      Tool instance
   * @param stack     Stack instance for vanilla functions
   * @param context   Tool harvest context
   */
  public static void breakExtraBlock(ToolStack tool, ItemStack stack, ToolHarvestContext context) {
    // break the actual block
    if (breakBlock(tool, stack, context)) {
      Level world = context.getWorld();
      BlockPos pos = context.getPos();
      // need to send the event to tell the client a block was broken
      // normally this is sent within one of the block breaking hooks that is called on both sides, suppressing the packet being sent to the breaking player
      // we only break the center block client side, so need to send the event directly
      // TODO: in theory, we can use this to reduce the number of sounds playing on breaking a lot of blocks, would require sending a custom packet if we want the particles still
      world.levelEvent(2001, pos, Block.getId(context.getState()));
      TinkerNetwork.getInstance().sendVanillaPacket(Objects.requireNonNull(context.getPlayer()), new ClientboundBlockUpdatePacket(world, pos));
    }
  }

  /**
   * Call on block break to break a block.
   * Used in {@link net.minecraftforge.common.extensions.IForgeItem#onBlockStartBreak(ItemStack, BlockPos, Player)}.
   * See also {@link net.minecraft.client.multiplayer.MultiPlayerGameMode#destroyBlock(BlockPos)} (client)
   * and {@link net.minecraft.server.level.ServerPlayerGameMode#destroyBlock(BlockPos)} (server)
   * @param stack   Stack instance
   * @param pos     Position to break
   * @param player  Player instance
   * @return  True if the block break is overridden.
   */
  public static boolean handleBlockBreak(ItemStack stack, BlockPos pos, Player player) {
    // client can run normal block breaking
    if (player.level.isClientSide || !(player instanceof ServerPlayer serverPlayer)) {
      return false;
    }

    // create contexts
    ServerLevel world = serverPlayer.getLevel();
    ToolStack tool = ToolStack.from(stack);
    BlockState state = world.getBlockState(pos);
    Direction sideHit = BlockSideHitListener.getSideHit(player);

    // if broken, clear the item stack temporarily then break
    if (tool.isBroken()) {
      // no harvest context
      player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
      ToolHarvestContext context = new ToolHarvestContext(world, serverPlayer, state, pos, sideHit,
                                                          !player.isCreative() && state.canHarvestBlock(world, pos, player), false);
      breakBlock(tool, ItemStack.EMPTY, context);
      player.setItemInHand(InteractionHand.MAIN_HAND, stack);
    } else {
      // add in harvest info
      ToolHarvestContext context = new ToolHarvestContext(world, serverPlayer, state, pos, sideHit,
                                                          !player.isCreative() && state.canHarvestBlock(world, pos, player),
                                                          isEffective(tool, state));

      // add enchants
      ListTag originalEnchants = ModifierUtil.applyHarvestEnchantments(tool, stack, context);
      // need to calculate the iterator before we break the block, as we need the reference hardness from the center
      Iterable<BlockPos> extraBlocks = context.isEffective() ? tool.getDefinition().getData().getAOE().getBlocks(tool, stack, player, state, world, pos, sideHit, IAreaOfEffectIterator.AOEMatchType.BREAKING) : Collections.emptyList();

      // actually break the block, run AOE if successful
      if (breakBlock(tool, stack, context)) {
        for (BlockPos extraPos : extraBlocks) {
          BlockState extraState = world.getBlockState(extraPos);
          // prevent calling that stuff for air blocks, could lead to unexpected behaviour since it fires events
          // this should never actually happen, but just in case some AOE is odd
          if (!extraState.isAir()) {
            // prevent mutable position leak, breakBlock has a few places wanting immutable
            breakExtraBlock(tool, stack, context.forPosition(extraPos.immutable(), extraState));
          }
        }
        for (ModifierEntry entry : tool.getModifierList()) {
          entry.getModifier().finishBreakingBlocks(tool, entry.getLevel(), context);
        }
      }

      // blocks done being broken, clear extra enchants added
      if (originalEnchants != null) {
        ModifierUtil.restoreEnchantments(stack, originalEnchants);
      }
    }

    return true;
  }
}
