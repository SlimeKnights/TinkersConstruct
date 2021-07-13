package slimeknights.tconstruct.library.tools.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.utils.TooltipType;

import java.util.List;

public class TestToolItem extends ToolItem {

  public TestToolItem(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean canHarvestBlock(BlockState state) {
    return state.getBlock() == Blocks.DIRT;
  }

  public TestToolItem() {
    this(new Item.Properties(), ToolDefinitionFixture.getTestToolDefinition());
  }

  @Override
  public void getTooltip(ItemStack stack, List<ITextComponent> tooltips, TooltipType tooltipType, boolean isAdvanced) {}
}
