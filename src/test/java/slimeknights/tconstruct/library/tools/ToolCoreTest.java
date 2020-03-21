package slimeknights.tconstruct.library.tools;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.ToolDefinitionFixture;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MaterialRegistryExtension.class)
class ToolCoreTest extends BaseMcTest {

  private static TestToolCore tool;
  private ItemStack testItemStack;

  @BeforeAll
  static void beforeAll() {
    tool = new TestToolCore(new Item.Properties(), ToolDefinitionFixture.getStandardToolDefinition());
    tool.setRegistryName("test:toolcore");
    ForgeRegistries.ITEMS.register(tool);
  }

  @BeforeEach
  void setUp() {
    testItemStack = ToolBuildHandler.buildItemFromMaterials(
      tool,
      ImmutableList.of(
        MaterialFixture.MATERIAL_WITH_HEAD,
        MaterialFixture.MATERIAL_WITH_HANDLE,
        MaterialFixture.MATERIAL_WITH_EXTRA)
    );
  }

  /* DURABILITY */

  @Test
  void testNewToolDurability() {
    int statDurability = ToolData.from(testItemStack).getStats().durability;

    assertThat(testItemStack.getDamage()).isEqualTo(0);
    assertThat(testItemStack.getMaxDamage()).isEqualTo(statDurability);
    assertThat(ToolData.from(testItemStack).getStats().broken).isFalse();
  }

  @Test
  void testSettingDamage() {
    int statDurability = ToolData.from(testItemStack).getStats().durability;

    testItemStack.setDamage(1);

    assertThat(testItemStack.getDamage()).isEqualTo(1);
    assertThat(testItemStack.getMaxDamage()).isEqualTo(statDurability);
    assertThat(ToolData.from(testItemStack).getStats().broken).isFalse();
  }

  @Test
  void testMaxDamageBreaksTool() {
    int statDurability = ToolData.from(testItemStack).getStats().durability;

    testItemStack.setDamage(statDurability);

    assertThat(testItemStack.getDamage()).isEqualTo(statDurability);
    assertThat(ToolData.from(testItemStack).getStats().broken).isTrue();
  }
}
