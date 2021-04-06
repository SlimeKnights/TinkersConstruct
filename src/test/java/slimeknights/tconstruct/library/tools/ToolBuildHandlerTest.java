package slimeknights.tconstruct.library.tools;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Lazy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.fixture.MaterialFixture;
import slimeknights.tconstruct.fixture.MaterialItemFixture;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MaterialRegistryExtension.class)
class ToolBuildHandlerTest extends BaseMcTest {

  private final Lazy<ItemStack> materialItem1 = Lazy.of(() -> MaterialItemFixture.MATERIAL_ITEM.getItemstackWithMaterial(MaterialFixture.MATERIAL_1));
  private final Lazy<ItemStack> materialItem2 = Lazy.of(() -> MaterialItemFixture.MATERIAL_ITEM_2.getItemstackWithMaterial(MaterialFixture.MATERIAL_2));
  private final Lazy<ToolCore> testToolCore = Lazy.of(TestToolCore::new);

  @Test
  void testBuildTool_correctItems() {
    NonNullList<ItemStack> input = NonNullList.create();
    input.add(materialItem1.get());
    input.add(materialItem2.get());

    ItemStack itemStack = ToolBuildHandler.buildItemFromStacks(input, testToolCore.get());

    assertThat(itemStack.isEmpty()).isFalse();
  }

  @Test
  void tooManyItems_fail() {
    NonNullList<ItemStack> input = NonNullList.create();
    input.add(materialItem1.get());
    input.add(materialItem2.get());
    input.add(materialItem2.get());

    ItemStack itemStack = ToolBuildHandler.buildItemFromStacks(input, testToolCore.get());

    assertThat(itemStack.isEmpty()).isTrue();
  }

  @Test
  void notEnoughItems_fail() {
    NonNullList<ItemStack> input = NonNullList.create();
    input.add(materialItem1.get());

    ItemStack itemStack = ToolBuildHandler.buildItemFromStacks(input, testToolCore.get());

    assertThat(itemStack.isEmpty()).isTrue();
  }

  @Test
  void incorrectItems_fail() {
    NonNullList<ItemStack> input = NonNullList.create();
    input.add(materialItem2.get());
    input.add(materialItem1.get());

    ItemStack itemStack = ToolBuildHandler.buildItemFromStacks(input, testToolCore.get());

    assertThat(itemStack.isEmpty()).isTrue();
  }
}
