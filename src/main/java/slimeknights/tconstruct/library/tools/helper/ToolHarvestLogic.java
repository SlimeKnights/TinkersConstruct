package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants.WorldEvents;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collections;
import java.util.List;

/**
 * External logic for the ToolCore that handles mining calculations and breaking blocks.
 */
public class ToolHarvestLogic {

  /** Default harvest logic object */
  public static final ToolHarvestLogic DEFAULT = new ToolHarvestLogic();


  /**
   * Gets the amoubt of damage this tool should take for the given block state
   * @param tool   Tool to check
   * @param stack  Stack for getting tool types
   * @param state  State to check
   * @return  Damage to deal
   */
  public int getDamage(ToolStack tool, ItemStack stack, World world, BlockPos pos, BlockState state) {
    if (state.getBlockHardness(world, pos) == 0) {
      return 0;
    }
    // if it lacks the harvest tag, it takes double damage (swords for instance)
    return tool.getItem().isIn(TinkerTags.Items.HARVEST) ? 1 : 2;
  }

  /**
   * Checks if this tool is effective against the given block
   * @param tool    Tool to check
   * @param stack   Tool stack
   * @param state   Block state
   * @return  True if effective
   */
  public boolean isEffectiveAgainst(ToolStack tool, ItemStack stack, BlockState state) {
    return stack.getToolTypes().stream().anyMatch(state::isToolEffective);
  }

  /**
   * Checks if the given tool is effective on the given state
   * @param tool   Tool to check
   * @param stack  Stack for getting tool types
   * @param state  State to check
   * @return  True if this tool is effective
   */
  public final boolean isEffective(ToolStack tool, ItemStack stack, BlockState state) {
    if (tool.isBroken()) {
      return false;
    }

    // harvest level too low -> not effective
    if (state.getRequiresTool() && tool.getStats().getHarvestLevel() < state.getHarvestLevel()) {
      return false;
    }

    // find a matching tool type
    return isEffectiveAgainst(tool, stack, state);
  }

  /**
   * Calculates the dig speed for the given blockstate
   *
   * @param stack the tool stack
   * @param blockState the block state to check
   * @return the dig speed
   */
  public float getDestroySpeed(ItemStack stack, BlockState blockState) {
    if(!stack.hasTag()) {
      return 1f;
    }

    // TODO: general modifiable
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return 0.3f;
    }

    if (!isEffective(tool, stack, blockState)) {
      return 1f;
    }

    // calculate speed depending on stats
    return tool.getStats().getMiningSpeed();
  }


  /**
   * Gets a list of blocks that the tool can affect.
   *
   * @param tool  tool stack
   * @param stack item stack for vanilla methods
   * @param world the current world
   * @param player the player using the tool
   * @param origin the origin block spot to start from
   * @return A list of BlockPos's that the AOE tool can affect.
   */
  public List<BlockPos> getAOEBlocks(ToolStack tool, ItemStack stack, World world, PlayerEntity player, BlockPos origin) {
    return Collections.emptyList();
  }

  /**
   * Gets a list of blocks that the tool can affect. Variant if you lack a tool stack
   *
   * @param stack item stack for vanilla methods
   * @param world the current world
   * @param player the player using the tool
   * @param origin the origin block spot to start from
   * @return A list of BlockPoses that the AOE tool can affect.
   */
  public final List<BlockPos> getAOEBlocks(ItemStack stack, World world, PlayerEntity player, BlockPos origin) {
    return getAOEBlocks(ToolStack.from(stack), stack, world, player, origin);
  }

  /**
   * Actually removes a block from the world. Cloned from {@link net.minecraft.server.management.PlayerInteractionManager}
   * @param player      Player breaking
   * @param world       World
   * @param pos         Position to break
   * @param canHarvest  If true, the player can harvest
   * @return  True if the block was removed
   */
  private static boolean removeBlock(PlayerEntity player, World world, BlockPos pos, boolean canHarvest) {
    BlockState state = world.getBlockState(pos);
    boolean removed = state.removedByPlayer(world, pos, player, canHarvest, world.getFluidState(pos));
    if (removed) {
      state.getBlock().onPlayerDestroy(world, pos, state);
    }
    return removed;
  }

  /**
   * Called to break a block using this tool
   * @param tool     Tool instance
   * @param stack    Stack instance for vanilla functions
   * @param player   Player instance
   * @param world    World instance
   * @param pos      Position to break
   * @param state    State being broken
   * @return  True if broken
   */
  protected boolean breakBlock(ToolStack tool, ItemStack stack, ServerPlayerEntity player, ServerWorld world, BlockPos pos, BlockState state) {
    // have to rerun the event to get the EXP, also ensures extra blocks broken get EXP properly
    GameType type = player.interactionManager.getGameType();
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
      removeBlock(player, world, pos, false);
      return true;
    }

    boolean canHarvest = state.canHarvestBlock(world, pos, player);

    // determine damage to do
    int damage = getDamage(tool, stack, world, pos, state);

    // block harvest callbacks
    TileEntity te = canHarvest ? world.getTileEntity(pos) : null;
    boolean removed = removeBlock(player, world, pos, canHarvest);
    if (removed && canHarvest) {
      state.getBlock().harvestBlock(world, player, pos, state, te, stack);
    }

    // drop XP
    if (removed && exp > 0) {
      state.getBlock().dropXpOnBlockBreak(world, pos, exp);
    }

    // handle modifiers if not broken
    // broken means we are using "empty hand"
    if (!tool.isBroken()) {
      for (ModifierEntry entry : tool.getModifierList()) {
        entry.getModifier().afterBlockBreak(tool, entry.getLevel(), world, state, pos, player, canHarvest);
      }
      ToolDamageUtil.damageAnimated(tool, damage, player);
    }

    return true;
  }

  /**
   * Breaks a secondary block
   * @param tool        Tool instance
   * @param stack       Stack instance for vanilla functions
   * @param player      Player instance
   * @param world       World instance
   * @param pos         Position to break
   * @param refStrength Strength required to break the center block, should be comparable
   */
  public void breakExtraBlock(ToolStack tool, ItemStack stack, ServerPlayerEntity player, ServerWorld world, BlockPos pos, float refStrength) {
    if (tool.isBroken()) {
      return;
    }
    // prevent calling that stuff for air blocks, could lead to unexpected behaviour since it fires events
    if (world.isAirBlock(pos)) {
      return;
    }

    // if the block is a lot slower, skip harvesting
    BlockState state = world.getBlockState(pos);
    if (refStrength / state.getPlayerRelativeBlockHardness(player, world, pos) > 3) {
      return;
    }

    // break the actual block
    if (breakBlock(tool, stack, player, world, pos, state)) {
      world.playEvent(WorldEvents.BREAK_BLOCK_EFFECTS, pos, Block.getStateId(state));
      TinkerNetwork.getInstance().sendVanillaPacket(player, new SChangeBlockPacket(world, pos));
    }
  }

  /**
   * Call on block break to break a block.
   * Used in {@link net.minecraftforge.common.extensions.IForgeItem#onBlockStartBreak(ItemStack, BlockPos, PlayerEntity)}.
   * See also {@link net.minecraft.client.multiplayer.PlayerController#onPlayerDestroyBlock(BlockPos)} (client)
   * and {@link net.minecraft.server.management.PlayerInteractionManager#tryHarvestBlock(BlockPos)} (server)
   * @param stack   Stack instance
   * @param pos     Position to break
   * @param player  Player instance
   * @return  True if the block break is overridden.
   */
  public boolean handleBlockBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
    // client can run normal block breaking
    if (player.getEntityWorld().isRemote || !(player instanceof ServerPlayerEntity)) {
      return false;
    }

    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
    ServerWorld world = serverPlayer.getServerWorld();
    ToolStack tool = ToolStack.from(stack);
    BlockState state = world.getBlockState(pos);
    // if broken, clear the item stack temporarily then break
    if (tool.isBroken()) {
      player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
      breakBlock(tool, ItemStack.EMPTY, serverPlayer, world, pos, state);
      player.setHeldItem(Hand.MAIN_HAND, stack);
    } else {
      // add enchants
      boolean addedEnchants = ModifierUtil.applyEnchantments(tool, stack, player);

      // need to calculate these before we break the block
      float refStrength = state.getPlayerRelativeBlockHardness(player, world, pos);
      List<BlockPos> extraBlocks = getAOEBlocks(tool, stack, world, player, pos);
      // actually break the block, run AOE if successful
      if (breakBlock(tool, stack, serverPlayer, world, pos, state)) {
        for (BlockPos extraPos : extraBlocks) {
          breakExtraBlock(tool, stack, serverPlayer, world, extraPos, refStrength);
        }
      }

      // blocks done being broken, clear extra enchants added
      if (addedEnchants) {
        ModifierUtil.clearEnchantments(stack);
      }
    }

    return true;
  }
}
