package slimeknights.tconstruct.shared.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;
import java.util.List;

public class CheeseBlockItem extends BlockItem {
  public CheeseBlockItem(Block pBlock, Properties pProperties) {
    super(pBlock, pProperties);
  }

  @Override
  public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
    CheeseItem.removeRandomEffect(living);
    ItemStack result = super.finishUsingItem(stack, level, living);
    if (!(living instanceof Player player) || !player.getAbilities().instabuild) {
      ItemStack cheese = new ItemStack(TinkerCommons.cheeseIngot, 3);
      if (result.isEmpty()) {
        return cheese;
      } else if (living instanceof Player player) {
        if (!player.addItem(cheese)) {
          player.drop(cheese, false);
        }
      } else {
        living.spawnAtLocation(cheese);
      }

    }
    return result;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level pLevel, List<Component> tooltip, TooltipFlag pIsAdvanced) {
    tooltip.add(CheeseItem.TOOLTIP);
  }
}
