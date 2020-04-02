package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.test.BaseMcTest;

@ExtendWith(MaterialRegistryExtension.class)
public abstract class ToolCoreTest extends BaseMcTest {

  protected static TestToolCore tool;
  protected ItemStack testItemStack;

  @BeforeAll
  synchronized static void beforeAllToolCore() {
    if (tool == null) {
      Item.Properties properties = new Item.Properties()
        .addToolType(ToolType.PICKAXE, 1)
        .maxStackSize(1);
      tool = new TestToolCore(properties, ToolDefinitionFixture.getStandardToolDefinition());
      tool.setRegistryName("test:toolcore");
      ForgeRegistries.ITEMS.register(tool);
    }
  }

  @BeforeEach
  void setUpToolCore() {
    testItemStack = buildTestTool(tool);
  }

  protected ItemStack buildTestTool(ToolCore tool) {
    return ToolBuildHandler.buildItemFromMaterials(
      tool,
      ImmutableList.of(
        MaterialFixture.MATERIAL_WITH_HEAD,
        MaterialFixture.MATERIAL_WITH_HANDLE,
        MaterialFixture.MATERIAL_WITH_EXTRA)
    );
  }

  protected boolean isTestitemBroken() {
    return ToolData.from(testItemStack).getStats().broken;
  }

  protected void breakTool(ItemStack stack) {
    stack.setDamage(99999);
  }
}
