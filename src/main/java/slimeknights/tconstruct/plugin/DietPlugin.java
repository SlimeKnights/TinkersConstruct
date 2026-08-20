package slimeknights.tconstruct.plugin;

import com.illusivesoulworks.diet.common.capability.DietCapability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil.FoodConsumer;

import java.util.List;

/** Plugin to enable compat with the Diet mod */
public class DietPlugin {
  /** Call on mod construct to enable the compat */
  public static void onConstruct() {
    ModifierUtil.foodConsumer = new FoodConsumer() {
      @Override
      public void onConsume(Player player, ItemStack stack, int hunger, float saturation) {
        DietCapability.get(player).ifPresent(cap -> cap.consume(stack, hunger, saturation));
      }

      @Override
      public void onConsume(Player player, List<ItemStack> stacks, int hunger, float saturation) {
        DietCapability.get(player).ifPresent(cap -> cap.consume(stacks, hunger, saturation));
      }
    };
  }
}
