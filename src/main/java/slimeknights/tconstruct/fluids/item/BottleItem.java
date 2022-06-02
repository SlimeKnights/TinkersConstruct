package slimeknights.tconstruct.fluids.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Item that can fill a bottle on right click
 */
public class BottleItem extends Item {
  private final ItemLike potion;
  public BottleItem(ItemLike potion, Properties props) {
    super(props);
    this.potion = potion;
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pHand) {
    ItemStack current = player.getItemInHand(pHand);
    BlockHitResult hit = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
    if (hit.getType() == HitResult.Type.BLOCK) {
      BlockPos pos = hit.getBlockPos();
      if (!level.mayInteract(player, pos)) {
        return InteractionResultHolder.pass(current);
      }

      if (level.getFluidState(pos).is(FluidTags.WATER)) {
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(ItemUtils.createFilledResult(current, player, PotionUtils.setPotion(new ItemStack(potion), Potions.WATER)), level.isClientSide());
      }
    }
    return InteractionResultHolder.pass(current);
  }
}
