package slimeknights.tconstruct.tables.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import slimeknights.tconstruct.tables.block.entity.chest.AbstractChestBlockEntity;

import javax.annotation.Nullable;

/**
 * Shared block logic for all chest types
 */
public class ChestBlock extends TabbedTableBlock {
  private static final VoxelShape SHAPE = Shapes.or(
    Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D), //top
    Block.box(1.0D, 3.0D, 1.0D, 15.0D, 16.0D, 15.0D), //middle
    Block.box(0.5D, 0.0D, 0.5D, 2.5D, 15.0D, 2.5D), //leg
    Block.box(13.5D, 0.0D, 0.5D, 15.5D, 15.0D, 2.5D), //leg
    Block.box(13.5D, 0.0D, 13.5D, 15.5D, 15.0D, 15.5D), //leg
    Block.box(0.5D, 0.0D, 13.5D, 2.5D, 15.0D, 15.5D) //leg
                                                        );

  private final BlockEntitySupplier<? extends BlockEntity> blockEntity;
  private final boolean dropsItems;
  public ChestBlock(Properties builder, BlockEntitySupplier<? extends BlockEntity> blockEntity, boolean dropsItems) {
    super(builder);
    this.blockEntity = blockEntity;
    this.dropsItems = dropsItems;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return blockEntity.create(pPos, pState);
  }

  @Override
  public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(worldIn, pos, state, placer, stack);
    // check if we also have an inventory

    CompoundTag tag = stack.getTag();
    if (tag != null && tag.contains("TinkerData", Tag.TAG_COMPOUND)) {
      CompoundTag tinkerData = tag.getCompound("TinkerData");
      BlockEntity te = worldIn.getBlockEntity(pos);
      if (te instanceof AbstractChestBlockEntity chest) {
        chest.readInventory(tinkerData);
      }
    }
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @SuppressWarnings("deprecation")
  @Override
  @Deprecated
  public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
    BlockEntity te = worldIn.getBlockEntity(pos);
    Inventory playerInventory = player.getInventory();
    ItemStack heldItem = playerInventory.getSelected();

    if (!heldItem.isEmpty() && te instanceof AbstractChestBlockEntity chest && chest.canInsert(player, heldItem)) {
      IItemHandlerModifiable itemHandler = chest.getItemHandler();
      ItemStack rest = ItemHandlerHelper.insertItem(itemHandler, heldItem, false);
      if (rest.isEmpty() || rest.getCount() < heldItem.getCount()) {
        playerInventory.items.set(playerInventory.selected, rest);
        return InteractionResult.SUCCESS;
      }
    }

    return super.use(state, worldIn, pos, player, handIn, hit);
  }

  @Override
  protected void dropInventoryItems(BlockState state, Level worldIn, BlockPos pos, IItemHandler inventory) {
    if (dropsItems) {
      dropInventoryItems(worldIn, pos, inventory);
    }
  }
}
