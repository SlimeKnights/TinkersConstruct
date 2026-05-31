package slimeknights.tconstruct.library.tools.part;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;

import java.util.List;
import java.util.function.Supplier;

/** Item which shows the cast cost in the tooltip */
public class PartCastItem extends Item {
  public static final String COST_KEY = TConstruct.makeTranslationKey("item", "cast.cost");

  private final Supplier<? extends IMaterialItem> part;
  public PartCastItem(Properties props, Supplier<? extends IMaterialItem> part) {
    super(props);
    this.part = part;
  }

  @Override
  public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    int cost = MaterialCastingLookup.getItemCost(part.get());
    if (cost > 0) {
      tooltip.add(Component.translatable(COST_KEY, cost).withStyle(ChatFormatting.GRAY));
    }
  }
}
