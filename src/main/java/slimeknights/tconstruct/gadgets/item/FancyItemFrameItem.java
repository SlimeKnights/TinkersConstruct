package slimeknights.tconstruct.gadgets.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.HangingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class FancyItemFrameItem extends Item {

  private final TriFunction<? extends HangingEntity, World, BlockPos, Direction> entityProvider;

  public FancyItemFrameItem(Properties props, TriFunction<? extends HangingEntity, World, BlockPos, Direction> entityProvider) {
    super(props);
    this.entityProvider = entityProvider;
  }

  /**
   * Called when this item is used when targetting a Block
   */
  @Override
  @Nonnull
  public ActionResultType onItemUse(ItemUseContext context) {
    BlockPos pos = context.getPos();
    Direction facing = context.getFace();
    BlockPos placeLocation = pos.offset(facing);
    PlayerEntity player = context.getPlayer();
    ItemStack stack = context.getItem();
    if (player != null && !this.canPlace(player, facing, stack, placeLocation)) {
      return ActionResultType.FAIL;
    }

    World world = context.getWorld();
    HangingEntity frame = this.entityProvider.apply(world, placeLocation, facing);
    CompoundNBT tag = stack.getTag();
    if (tag != null) {
      EntityType.applyItemNBT(world, player, frame, tag);
    }

    if (frame.onValidSurface()) {
      if (!world.isRemote) {
        frame.playPlaceSound();
        world.addEntity(frame);
      }
      stack.shrink(1);
      return ActionResultType.func_233537_a_(world.isRemote);
    }
    return ActionResultType.CONSUME;
  }

  private boolean canPlace(PlayerEntity player, Direction facing, ItemStack stack, BlockPos pos) {
    return !World.isOutsideBuildHeight(pos) && player.canPlayerEdit(pos, facing, stack);
  }

  @FunctionalInterface
  public interface TriFunction<R, T, U, V> {
    R apply(T t, U u, V v);
  }
}
