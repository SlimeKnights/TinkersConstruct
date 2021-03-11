package slimeknights.tconstruct.library.tools;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.library.utils.TooltipType;

import java.util.List;

public class TestToolCore extends ToolCore {

  public TestToolCore(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean canHarvestBlock(BlockState state) {
    return state.getBlock() == Blocks.DIRT;
  }

  public TestToolCore() {
    this(new Item.Properties(), ToolDefinitionFixture.getTestToolDefinition());
  }

  @Override
  public void getTooltip(ItemStack stack, List<ITextComponent> tooltips, TooltipType tooltipType) {

  }
}
