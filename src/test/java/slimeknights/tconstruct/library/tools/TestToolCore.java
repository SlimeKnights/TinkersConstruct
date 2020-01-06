package slimeknights.tconstruct.library.tools;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;

import java.util.List;

public class TestToolCore extends ToolCore {

  public TestToolCore(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  public TestToolCore() {
    this(new Item.Properties(), ToolDefinitionFixture.getTestToolDefinition());
  }

  @Override
  public void getTooltip(ItemStack stack, List<String> tooltips) {

  }
}
