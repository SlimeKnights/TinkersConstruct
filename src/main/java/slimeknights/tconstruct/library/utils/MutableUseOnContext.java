package slimeknights.tconstruct.library.utils;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/** Mutable use on context that can easily be moved to another location */
public class MutableUseOnContext extends UseOnContext {
  private final BlockPos.MutableBlockPos offsetPos;
  @Getter
  private Vec3 clickedLocation;
  public MutableUseOnContext(UseOnContext base) {
    this(base.getLevel(), base.getPlayer(), base.getHand(), base.getItemInHand(), base.getHitResult());
  }

  public MutableUseOnContext(Level pLevel, @Nullable Player pPlayer, InteractionHand pHand, ItemStack pItemStack, BlockHitResult pHitResult) {
    super(pLevel, pPlayer, pHand, pItemStack, pHitResult);
    this.offsetPos = super.getClickedPos().mutable();
    this.clickedLocation = super.getClickLocation();
  }

  @Override
  public BlockPos getClickedPos() {
    return offsetPos;
  }

  /** Sets the offset position */
  public void setOffsetPos(BlockPos offset) {
    clickedLocation = clickedLocation.add(
      offset.getX() - offsetPos.getX(),
      offset.getY() - offsetPos.getY(),
      offset.getZ() - offsetPos.getZ());
    offsetPos.set(offset);
  }
}
