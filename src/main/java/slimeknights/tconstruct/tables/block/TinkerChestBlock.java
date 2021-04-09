package slimeknights.tconstruct.tables.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.tables.tileentity.chest.TinkerChestTileEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

public class TinkerChestBlock extends TinkerTableBlock {
  private static final VoxelShape SHAPE = VoxelShapes.union(
    Block.createCuboidShape(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D), //top
    Block.createCuboidShape(1.0D, 3.0D, 1.0D, 15.0D, 16.0D, 15.0D), //middle
    Block.createCuboidShape(0.5D, 0.0D, 0.5D, 2.5D, 15.0D, 2.5D), //leg
    Block.createCuboidShape(13.5D, 0.0D, 0.5D, 15.5D, 15.0D, 2.5D), //leg
    Block.createCuboidShape(13.5D, 0.0D, 13.5D, 15.5D, 15.0D, 15.5D), //leg
    Block.createCuboidShape(0.5D, 0.0D, 13.5D, 2.5D, 15.0D, 15.5D) //leg
                                                        );

  private final Supplier<? extends BlockEntity> te;
  public TinkerChestBlock(Settings builder, Supplier<? extends BlockEntity> te) {
    super(builder);
    this.te = te;
  }

  @NotNull
  @Override
  public BlockEntity createTileEntity(BlockState blockState, BlockView iBlockReader) {
    return te.get();
  }

  @Override
  public void onPlaced(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.onPlaced(worldIn, pos, state, placer, stack);
    // check if we also have an inventory

    CompoundTag tag = stack.getTag();
    if (tag != null && tag.contains("TinkerData", NBT.TAG_COMPOUND)) {
      CompoundTag tinkerData = tag.getCompound("TinkerData");
      BlockEntity te = worldIn.getBlockEntity(pos);
      if (te instanceof TinkerChestTileEntity) {
        ((TinkerChestTileEntity)te).readInventoryFromNBT(tinkerData);
      }
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getOutlineShape(BlockState state, BlockView worldIn, BlockPos pos, ShapeContext context) {
    return SHAPE;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockHitResult hit) {
    BlockEntity te = worldIn.getBlockEntity(pos);
    ItemStack heldItem = player.inventory.getMainHandStack();

    if (!heldItem.isEmpty() && te instanceof TinkerChestTileEntity) {
      IItemHandlerModifiable itemHandler = ((TinkerChestTileEntity) te).getItemHandler();
      ItemStack rest = ItemHandlerHelper.insertItem(itemHandler, heldItem, false);

      if (rest.isEmpty() || rest.getCount() < heldItem.getCount()) {
        player.inventory.main.set(player.inventory.selectedSlot, rest);
        return ActionResult.SUCCESS;
      }
    }

    return super.onUse(state, worldIn, pos, player, handIn, hit);
  }
}
