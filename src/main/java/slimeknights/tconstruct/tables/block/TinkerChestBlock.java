package slimeknights.tconstruct.tables.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.tables.tileentity.chest.TinkerChestTileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class TinkerChestBlock extends TinkerTableBlock {
  private static final VoxelShape SHAPE = VoxelShapes.or(
    Block.makeCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D), //top
    Block.makeCuboidShape(1.0D, 3.0D, 1.0D, 15.0D, 16.0D, 15.0D), //middle
    Block.makeCuboidShape(0.5D, 0.0D, 0.5D, 2.5D, 15.0D, 2.5D), //leg
    Block.makeCuboidShape(13.5D, 0.0D, 0.5D, 15.5D, 15.0D, 2.5D), //leg
    Block.makeCuboidShape(13.5D, 0.0D, 13.5D, 15.5D, 15.0D, 15.5D), //leg
    Block.makeCuboidShape(0.5D, 0.0D, 13.5D, 2.5D, 15.0D, 15.5D) //leg
                                                        );

  private final Supplier<? extends TileEntity> te;
  public TinkerChestBlock(Properties builder, Supplier<? extends TileEntity> te) {
    super(builder);
    this.te = te;
  }

  @Nonnull
  @Override
  public TileEntity createTileEntity(BlockState blockState, IBlockReader iBlockReader) {
    return te.get();
  }

  @Override
  public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    // check if we also have an inventory

    CompoundNBT tag = stack.getTag();
    if (tag != null && tag.contains("TinkerData", NBT.TAG_COMPOUND)) {
      CompoundNBT tinkerData = tag.getCompound("TinkerData");
      TileEntity te = worldIn.getTileEntity(pos);
      if (te instanceof TinkerChestTileEntity) {
        ((TinkerChestTileEntity)te).readInventoryFromNBT(tinkerData);
      }
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
    return SHAPE;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    TileEntity te = worldIn.getTileEntity(pos);
    ItemStack heldItem = player.inventory.getCurrentItem();

    if (!heldItem.isEmpty() && te instanceof TinkerChestTileEntity) {
      IItemHandlerModifiable itemHandler = ((TinkerChestTileEntity) te).getItemHandler();
      ItemStack rest = ItemHandlerHelper.insertItem(itemHandler, heldItem, false);

      if (rest.isEmpty() || rest.getCount() < heldItem.getCount()) {
        player.inventory.mainInventory.set(player.inventory.currentItem, rest);
        return ActionResultType.SUCCESS;
      }
    }

    return super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
  }
}
