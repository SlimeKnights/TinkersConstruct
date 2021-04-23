package slimeknights.tconstruct.library.tools.helper;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.server.SChangeBlockPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.WorldEvents;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.network.TinkerNetwork;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Collections;
import java.util.function.Predicate;

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
   * @param tool        tool stack
   * @param stack       item stack for vanilla methods
   * @param world       the current world
   * @param player      the player using the tool
   * @param origin      the origin block spot to start from
   * @param sideHit     side of the block that was hit
   * @param matchType   Type of match
   * @return A list of BlockPos's that the AOE tool can affect. Note these positions will likely be mutable
   */
  public Iterable<BlockPos> getAOEBlocks(ToolStack tool, ItemStack stack, World world, PlayerEntity player, BlockPos origin, Direction sideHit, AOEMatchType matchType) {
    return Collections.emptyList();
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
   * @param pos         Position to break, may be mutable
   */
  public void breakExtraBlock(ToolStack tool, ItemStack stack, ServerPlayerEntity player, ServerWorld world, BlockPos pos) {
    if (tool.isBroken()) {
      return;
    }
    // prevent calling that stuff for air blocks, could lead to unexpected behaviour since it fires events
    // this should never actually happen, but just in case some AOE is odd
    BlockState state = world.getBlockState(pos);
    if (state.isAir(world, pos)) {
      return;
    }

    // break the actual block
    pos = pos.toImmutable(); // prevent mutable position leak, breakBlock has a few places wanting immutable
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

      // need to calculate the iterator before we break the block, as we need the reference hardness from the center
      Iterable<BlockPos> extraBlocks = getAOEBlocks(tool, stack, world, player, pos, BlockSideHitListener.getSideHit(player), AOEMatchType.BREAKING);

      // actually break the block, run AOE if successful
      if (breakBlock(tool, stack, serverPlayer, world, pos, state)) {
        for (BlockPos extraPos : extraBlocks) {
          breakExtraBlock(tool, stack, serverPlayer, world, extraPos);
        }
      }

      // blocks done being broken, clear extra enchants added
      if (addedEnchants) {
        ModifierUtil.clearEnchantments(stack);
      }
    }

    return true;
  }

  /**
   * Tills blocks within an AOE area
   * @param context   Harvest context
   * @param toolType  Tool type used
   * @param sound     Sound to play on tilling
   * @return  Action result from tilling
   */
  public ActionResultType transformBlocks(ItemUseContext context, ToolType toolType, SoundEvent sound, boolean requireGround) {
    PlayerEntity player = context.getPlayer();
    if (player != null && player.isSneaking()) {
      return ActionResultType.PASS;
    }

    // tool must not be broken
    Hand hand = context.getHand();
    ItemStack stack = context.getItem();
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return ActionResultType.FAIL;
    }

    // for hoes and shovels, must have nothing but plants above
    World world = context.getWorld();
    BlockPos pos = context.getPos();
    if (requireGround) {
      if (context.getFace() == Direction.DOWN) {
        return ActionResultType.PASS;
      }
      Material material = world.getBlockState(pos.up()).getMaterial();
      if (!material.isReplaceable() && material != Material.PLANTS) {
        return ActionResultType.PASS;
      }
    }

    // must actually transform
    BlockState original = world.getBlockState(pos);
    BlockState transformed = original.getToolModifiedState(world, pos, player, stack, toolType);
    boolean isCampfire = false;
    boolean didTransform = transformed != null;
    if (transformed == null) {
      // shovel special case: campfires
      if (toolType == ToolType.SHOVEL && original.getBlock() instanceof CampfireBlock && original.get(CampfireBlock.LIT)) {
        isCampfire = true;
        if (!world.isRemote()) {
          world.playEvent(null, WorldEvents.FIRE_EXTINGUISH_SOUND, pos, 0);
          CampfireBlock.extinguish(world, pos, original);
        }
        transformed = original.with(CampfireBlock.LIT, false);
      } else {
        // try to match the clicked block
        transformed = world.getBlockState(pos);
      }
    }

    // if we made a successful transform, client can stop early
    EquipmentSlotType slot = hand == Hand.MAIN_HAND ? EquipmentSlotType.MAINHAND : EquipmentSlotType.OFFHAND;
    if (didTransform || isCampfire) {
      if (world.isRemote()) {
        return ActionResultType.SUCCESS;
      }

      // change the block state
      world.setBlockState(pos, transformed, Constants.BlockFlags.DEFAULT_AND_RERENDER);
      if (requireGround) {
        world.destroyBlock(pos.up(), true);
      }

      // play sound
      if (!isCampfire) {
        world.playSound(null, pos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
      }

      // if the tool breaks, we are done
      if (player == null || !player.isCreative()) {
        if (ToolDamageUtil.damage(tool, 1, player, stack)) {
          return ActionResultType.SUCCESS;
        }
      }
      // if it was a campfire, we are done
      if (isCampfire) {
        return ActionResultType.SUCCESS;
      }
    }

    // AOE transforming, run even if we did not transform the center
    // note we consider anything effective, as hoes are not effective on all tillable blocks
    boolean didAoe = false;
    if (player != null) {
      for (BlockPos newPos : getAOEBlocks(tool, stack, world, player, pos, context.getFace(), AOEMatchType.TRANSFORM)) {
        if (pos.equals(newPos)) {
          //in case it attempts to run the same position twice
          continue;
        }

        // hoes and shovels: air or plants above
        BlockPos above = newPos.up();
        if (requireGround) {
          Material material = world.getBlockState(above).getMaterial();
          if (!material.isReplaceable() && material != Material.PLANTS) {
            continue;
          }
        }

        // block type must be the same
        BlockState newState = world.getBlockState(newPos).getToolModifiedState(world, newPos, player, stack, toolType);
        if (newState != null && transformed.getBlock() == newState.getBlock()) {
          if (world.isRemote()) {
            return ActionResultType.SUCCESS;
          }
          didAoe = true;
          world.setBlockState(newPos, newState, Constants.BlockFlags.DEFAULT_AND_RERENDER);
          world.playSound(null, newPos, sound, SoundCategory.BLOCKS, 1.0F, 1.0F);

          // if required, break the block above (typically plants)
          if (requireGround) {
            world.destroyBlock(above, true);
          }

          // stop if the tool broke
          if (!player.isCreative() && ToolDamageUtil.damageAnimated(tool, 1, player, slot)) {
            break;
          }
        }
      }
      if (didAoe) {
        player.spawnSweepParticles();
      }
    }

    // if anything happened, return success
    return didTransform || didAoe ? ActionResultType.SUCCESS : ActionResultType.PASS;
  }

  /**
   * Gets the predicate for whether a given position can be broken in AOE
   * @param self       Tool harvest logic
   * @param tool       Tool used
   * @param stack      Item stack, for vanilla hooks
   * @param world      World instance
   * @param origin     Center position
   * @param matchType  Match logic
   * @return  Predicate for AOE block matching
   */
  public static Predicate<BlockPos> getDefaultBlockPredicate(ToolHarvestLogic self, ToolStack tool, ItemStack stack, World world, BlockPos origin, AOEMatchType matchType) {
    // requires effectiveness
    if (matchType == AOEMatchType.BREAKING) {
      // don't let hardness vary too much
      float refHardness = world.getBlockState(origin).getBlockHardness(world, origin);
      return pos -> {
        BlockState state = world.getBlockState(pos);
        if (state.isAir(world, pos)) {
          return false;
        }
        // if the hardness varies by too much, don't allow breaking
        float hardness = state.getBlockHardness(world, pos);
        if (refHardness == 0 ? hardness == 0 : hardness / refHardness <= 3) {
          return self.isEffective(tool, stack, state);
        }
        return false;
      };
    } else {
      return pos -> !world.getBlockState(pos).isAir(world, pos);
    }
  }

  public enum AOEMatchType {
    /** Used when the block is being broken, typically matches only harvestable blocks
     * When using this type, the iteratable should be fetched before breaking the block */
    BREAKING,
    /** Used for right click interactions such as hoeing, typically matches any block (will filter later) */
    TRANSFORM
  }
}
