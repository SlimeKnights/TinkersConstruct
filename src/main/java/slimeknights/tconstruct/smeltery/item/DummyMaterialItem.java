package slimeknights.tconstruct.smeltery.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import slimeknights.tconstruct.TConstruct;

import java.util.List;

/** Item for creating casts that looks like a tool part */
public class DummyMaterialItem extends Item {
  private static final Component DUMMY_TOOL_PART = TConstruct.makeTranslation("item", "dummy_tool_part.tooltip").withStyle(ChatFormatting.GRAY);
  public DummyMaterialItem(Properties pProperties) {
    super(pProperties);
  }

  @Override
  public void appendHoverText(ItemStack pStack, TooltipContext context, List<Component> tooltip, TooltipFlag pIsAdvanced) {
    tooltip.add(DUMMY_TOOL_PART);
  }
}
