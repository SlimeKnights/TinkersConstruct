package slimeknights.tconstruct.library.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.MaterialItemFixture;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MaterialRegistryExtension.class)
class ToolBuildHandlerTest extends BaseMcTest {

  private ItemStack materialItem1 = MaterialItemFixture.MATERIAL_ITEM.getItemstackWithMaterial(MaterialFixture.MATERIAL_1);
  private ItemStack materialItem2 = MaterialItemFixture.MATERIAL_ITEM_2.getItemstackWithMaterial(MaterialFixture.MATERIAL_2);
  private ToolCore testToolCore = new TestToolCore();

  @Test
  void testBuildTool_correctItems() {
    NonNullList<ItemStack> input = NonNullList.create();
    input.add(materialItem1);
    input.add(materialItem2);

    ItemStack itemStack = ToolBuildHandler.buildItemFromStacks(input.stream(), 3, testToolCore);

    assertThat(itemStack.isEmpty()).isFalse();
  }

  @Test
  void tooManyItems_fail() {
    NonNullList<ItemStack> input = NonNullList.create();
    input.add(materialItem1);
    input.add(materialItem2);
    input.add(materialItem2);

    ItemStack itemStack = ToolBuildHandler.buildItemFromStacks(input.stream(), 3, testToolCore);

    assertThat(itemStack.isEmpty()).isTrue();
  }

  @Test
  void notEnoughItems_fail() {
    NonNullList<ItemStack> input = NonNullList.create();
    input.add(materialItem1);

    ItemStack itemStack = ToolBuildHandler.buildItemFromStacks(input.stream(), 3, testToolCore);

    assertThat(itemStack.isEmpty()).isTrue();
  }

  @Test
  void incorrectItems_fail() {
    NonNullList<ItemStack> input = NonNullList.create();
    input.add(materialItem2);
    input.add(materialItem1);

    ItemStack itemStack = ToolBuildHandler.buildItemFromStacks(input.stream(), 3, testToolCore);

    assertThat(itemStack.isEmpty()).isTrue();
  }
}
