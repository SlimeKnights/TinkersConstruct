package slimeknights.tconstruct.tools.modifiers.ability;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.World;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.modifiers.SingleUseModifier;
import slimeknights.tconstruct.library.tools.nbt.IModifierToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

/** Modifier version of a {@link net.minecraft.item.BlockItem}
 * */
public abstract class PlaceBlockModifier extends SingleUseModifier /*, BlockItem*/ {
  private static final ResourceLocation STATE_LOCATION = Util.getResource("blockstatetag");
  private static final ResourceLocation TILE_ENTITY_LOCATION = Util.getResource("blockentitytag");
  
  public PlaceBlockModifier(int color) {
    super(color);
  }

  @Override
  public ActionResultType onItemUse(IModifierToolStack tool, int level, ItemUseContext context) {
    return tryPlace(new BlockItemUseContext(context));
  }
  
  /** Handles block placement
   * <br>
   * It's generally not recommended to override it directly, there are better methods to override below
   * @param blockitemusecontext Placement context 
   * @return result of placement
   * */
  protected ActionResultType tryPlace(BlockItemUseContext blockitemusecontext) {
     if (!blockitemusecontext.canPlace()) {
        return ActionResultType.FAIL;
     } else {
        BlockItemUseContext context = getBlockItemUseContext(blockitemusecontext);
        if (context == null) {
           return ActionResultType.FAIL;
        } else {
           BlockState blockstate = getStateForPlacement(context);
           if (blockstate == null) {
              return ActionResultType.FAIL;
           } else if (!placeBlock(context, blockstate)) {
              return ActionResultType.FAIL;
           } else {
              BlockPos blockpos = context.getPos();
              World world = context.getWorld();
              PlayerEntity player = context.getPlayer();
              ItemStack stack = context.getItem();
              ToolStack tool = ToolStack.from(stack);
              BlockState stateInWorld = world.getBlockState(blockpos);
              Block block = stateInWorld.getBlock();
              if (block == blockstate.getBlock()) {
                 stateInWorld = setStateFromTag(blockpos, world, tool, stateInWorld);
                 onBlockPlaced(blockpos, world, player, tool, stack, stateInWorld);
                 // We're giving it the whole stack because the tool placement modifier will want to see the whole stack.
                 block.onBlockPlacedBy(world, blockpos, stateInWorld, player, stack);
                 // We aren't really a BlockItem, so no advancement advancements.
              }

              SoundType soundtype = stateInWorld.getSoundType(world, blockpos, context.getPlayer());
              world.playSound(player, blockpos, getPlaceSound(stateInWorld, world, blockpos, context.getPlayer()), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
              if (player == null || !player.abilities.isCreativeMode) {
                onBlockPlacement(tool, stack, player);
              }

              return ActionResultType.func_233537_a_(world.isRemote);
           }
        }
     }
  }
  
  /** @param context Placement context 
   * @return The block to be placed
   * */
  protected abstract Block getBlock(BlockItemUseContext context);
  
  /** Allows the modifier to modify the tool on successful placement
   * @param tool The current tool
   * @param stack The current stack
   * @param player The current player
   * */
  protected void onBlockPlacement(IModifierToolStack tool, ItemStack stack, PlayerEntity entity) {}

  /** Allows the modifier to override the placement context
   * @param context Placement context
   * @return Placement context to use
   * */
  @Nullable
  protected BlockItemUseContext getBlockItemUseContext(BlockItemUseContext context) {
     return context;
  }

  /** Gets a state for placement, as well as checks if the block can be placed in the first place 
   * @param context Placement context
   * @return Block state to place
   * */
  @Nullable
  protected BlockState getStateForPlacement(BlockItemUseContext context) {
     BlockState blockstate = getBlock(context).getStateForPlacement(context);
     return blockstate != null && canPlace(context, blockstate) ? blockstate : null;
  }

  /** Determines if the state can be placed
   * @param context Placement context
   * @param state Block state to place
   * @return True if the state can be placed
   * */
  protected boolean canPlace(BlockItemUseContext context, BlockState state) {
    PlayerEntity playerentity = context.getPlayer();
    ISelectionContext iselectioncontext = playerentity == null ? ISelectionContext.dummy() : ISelectionContext.forEntity(playerentity);
    return (!checkPosition() || state.isValidPosition(context.getWorld(), context.getPos())) && context.getWorld().placedBlockCollides(state, context.getPos(), iselectioncontext);
  }
  
  /** Allows the modifier to override any block placement checks
   * @return False if the block can be placed
   * */
  protected boolean checkPosition() {
    return true;
  }
  
  /** Actually places the block state
   * @param context Placement context
   * @param state Block state to place
   * @return True if the state was successfully placed
   * */
  protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
    return context.getWorld().setBlockState(context.getPos(), state, 11);
  }
  
  /** Called after the block is fully set
   * <br>
   * To call setTileEntityNBT, use super.onBlockPlaced
   * @param pos Block position
   * @param world The current world
   * @param player The current player
   * @param tool The current tool
   * @param stack The current stack
   * @param state Block state of the placed block
   * @return True if the action was performed successfully
   * */
  protected boolean onBlockPlaced(BlockPos pos, World world, @Nullable PlayerEntity player, IModifierToolStack tool, ItemStack stack, BlockState state) {
    return setTileEntityNBT(world, player, pos, tool);
  }
  
  /** Gets the placement sound
   * @param state Block state of the placed block
   * @param world The current world
   * @param pos Block position
   * @param player The current player
   * @return Placement sound event
   * */
  protected SoundEvent getPlaceSound(BlockState state, World world, BlockPos pos, PlayerEntity player) {
    return state.getSoundType(world, pos, player).getPlaceSound();
  }
  
  /** @return The resource location of a BlockStateTag to place
   * */
  protected static ResourceLocation getStateLocation() {
    return STATE_LOCATION;
  };
  
  /** @return The resource location of a BlockEntityTag to place
   * */
  protected static ResourceLocation getTileEntityLocation() {
    return TILE_ENTITY_LOCATION;
  };
  
  /** Internal: sets state properties from tool tags
   * */
  private static BlockState setStateFromTag(BlockPos pos, World world, IModifierToolStack tool, BlockState state) {
    BlockState blockState = state;
    CompoundNBT blockStateCompound = tool.getPersistentData().getCompound(getStateLocation());
    if (blockStateCompound != null) {
       StateContainer<Block, BlockState> stateContainer = state.getBlock().getStateContainer();

       for(String propertyName : blockStateCompound.keySet()) {
          Property<?> property = stateContainer.getProperty(propertyName);
          if (property != null) {
             String propertyValue = blockStateCompound.get(propertyName).getString();
             blockState = setProperty(blockState, property, propertyValue);
          }
       }
    }

    if (blockState != state) {
       world.setBlockState(pos, blockState, 2);
    }

    return blockState;
  }

  /** Internal: sets tile entity properties from tool tags
   * */
  private static boolean setTileEntityNBT(World world, @Nullable PlayerEntity player, BlockPos pos, IModifierToolStack tool) {
    if (world.getServer() == null) {
       return false;
    } else {
       CompoundNBT compound = tool.getPersistentData().getCompound(getTileEntityLocation());
       if (compound != null) {
          TileEntity tile = world.getTileEntity(pos);
          if (tile != null) {
             if (!world.isRemote && tile.onlyOpsCanSetNbt() && (player == null || !player.canUseCommandBlock())) {
                return false;
             }

             CompoundNBT tileCompound = tile.write(new CompoundNBT());
             CompoundNBT tileCompound2 = tileCompound.copy();
             tileCompound.merge(compound);
             tileCompound.putInt("x", pos.getX());
             tileCompound.putInt("y", pos.getY());
             tileCompound.putInt("z", pos.getZ());
             if (!tileCompound.equals(tileCompound2)) {
                tile.read(world.getBlockState(pos), tileCompound);
                tile.markDirty();
                return true;
             }
          }
       }

       return false;
    }
  }

  /** Helper: sets state property
   * */
  private static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, String propertyValue) {
     return property.parseValue(propertyValue).map((value) -> {
        return state.with(property, value);
     }).orElse(state);
  }
}
