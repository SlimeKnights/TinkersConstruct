package slimeknights.tconstruct.tables.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import slimeknights.mantle.block.InventoryBlock;
import slimeknights.mantle.tileentity.InventoryTileEntity;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.tables.tileentity.TableTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TableBlock extends InventoryBlock {

  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

  public static final VoxelShape shape = VoxelShapes.or(
    Block.makeCuboidShape(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D), //top
    Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 4.0D, 15.0D, 4.0D), //leg
    Block.makeCuboidShape(12.0D, 0.0D, 0.0D, 16.0D, 15.0D, 4.0D), //leg
    Block.makeCuboidShape(12.0D, 0.0D, 12.0D, 16.0D, 15.0D, 16.0D), //leg
    Block.makeCuboidShape(0.5D, 0.0D, 12.0D, 4.0D, 15.0D, 16.0D) //leg
  );

  protected TableBlock(Properties builder) {
    super(builder);

    this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH));
  }

  @Nonnull
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }

  @Override
  public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
    return false;
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

    CompoundNBT tag = TagUtil.getTagSafe(stack);
    TileEntity tileEntity = worldIn.getTileEntity(pos);

    if (tileEntity instanceof TableTileEntity) {
      TableTileEntity tableTileEntity = (TableTileEntity) tileEntity;
      CompoundNBT feetTag = tag.getCompound(TableTileEntity.FEET_TAG);

      if (feetTag == null) {
        feetTag = new CompoundNBT();
      }

      tableTileEntity.updateTextureBlock(feetTag);

      if (tag.hasUniqueId("inventory")) {
        tableTileEntity.readInventoryFromNBT(tag.getCompound("inventory"));
      }

      if (stack.hasDisplayName()) {
        tableTileEntity.setCustomName(stack.getDisplayName());
      }
    }
  }

  @Override
  public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
    this.onPlayerDestroy(world, pos, state);

    if (willHarvest) {
      this.harvestBlock(world, player, pos, state, world.getTileEntity(pos), player.getHeldItemMainhand());
    }

    if (keepInventory(state)) {
      TileEntity te = world.getTileEntity(pos);

      if (te instanceof InventoryTileEntity) {
        ((InventoryTileEntity) te).clear();
      }
    }

    world.removeBlock(pos, false);

    return false;
  }

  protected boolean keepInventory(BlockState state) {
    return false;
  }

  private void writeDataOntoItemStack(@Nonnull ItemStack item, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean inventorySave) {
    TileEntity tileEntity = world.getTileEntity(pos);

    if (tileEntity != null && tileEntity instanceof TableTileEntity) {
      TableTileEntity table = (TableTileEntity) tileEntity;
      CompoundNBT tag = TagUtil.getTagSafe(item);

      // texture
      CompoundNBT data = table.getTextureBlock();

      if (!data.isEmpty()) {
        tag.put(TableTileEntity.FEET_TAG, data);
      }

      // save inventory, if not empty
      if (inventorySave && keepInventory(state)) {
        if (!table.isInventoryEmpty()) {
          CompoundNBT inventoryTag = new CompoundNBT();
          table.writeInventoryToNBT(inventoryTag);
          tag.put("inventory", inventoryTag);
          table.clear();
        }
      }

      if (!tag.isEmpty()) {
        item.setTag(tag);
      }
    }
  }

  @Override
  public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
    return super.getPickBlock(state, target, world, pos, player);
  }

  public static ItemStack createItemStack(TableBlock table, Block block) {
    ItemStack stack = new ItemStack(table, 1);

    if (block != null) {
      ItemStack blockStack = new ItemStack(block, 1);
      CompoundNBT tag = new CompoundNBT();
      CompoundNBT subTag = new CompoundNBT();

      blockStack.write(subTag);
      tag.put(TableTileEntity.FEET_TAG, subTag);
      stack.setTag(tag);
    }

    return stack;
  }

  @Override
  public BlockState getStateForPlacement(BlockItemUseContext context) {
    return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
  }

  /**
   * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   */
  @Override
  public BlockState rotate(BlockState state, Rotation rot) {
    return state.with(FACING, rot.rotate(state.get(FACING)));
  }

  /**
   * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
   * blockstate.
   */
  @Override
  public BlockState mirror(BlockState state, Mirror mirrorIn) {
    return state.rotate(mirrorIn.toRotation(state.get(FACING)));
  }

  @Override
  protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return shape;
  }
}
