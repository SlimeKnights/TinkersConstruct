package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.TestToolCore;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

class ToolItemNBTTest extends BaseMcTest {

  private static final TestToolCore testToolItem = new TestToolCore();
  private static final ResourceLocation TEST_REGISTRY_NAME = new ResourceLocation("test", "tool_item");
  private final ToolItemNBT testToolItemNBT = new ToolItemNBT(testToolItem);

  @BeforeAll
  static void beforeAll() {
    testToolItem.setRegistryName(TEST_REGISTRY_NAME);
    ForgeRegistries.ITEMS.register(testToolItem);
  }

  @Test
  void serializeNBT_item() {
    StringNBT nbt = testToolItemNBT.serializeToNBT();

    assertThat(nbt.getString()).isEqualTo(TEST_REGISTRY_NAME.toString());
  }

  @Test
  void deserializeNBT_item() {
    StringNBT nbt = StringNBT.valueOf(TEST_REGISTRY_NAME.toString());

    ToolItemNBT toolItemNBT = ToolItemNBT.readFromNBT(nbt);

    assertThat(toolItemNBT.getToolItem()).isEqualTo(testToolItem);
  }

  @Test
  void serializeMissingTool_emptyOrNoItemTag() {
    //noinspection ConstantConditions
    ToolItemNBT toolItemNBT = new ToolItemNBT(null);

    StringNBT nbt = toolItemNBT.serializeToNBT();

    assertThat(nbt).isNotNull();
    assertThat(nbt.getString()).isEmpty();
  }

  @Test
  void deserializeMissingTool_invalidItem_dummy() {
    StringNBT nbt = StringNBT.valueOf("test:nonexistant");

    ToolItemNBT toolItemNBT = ToolItemNBT.readFromNBT(nbt);

    assertThat(toolItemNBT.getToolItem()).isNotNull();
  }

  @Test
  void wrongNbtType_dummy() {
    INBT wrongNbt = new CompoundNBT();

    ToolItemNBT toolItemNBT = ToolItemNBT.readFromNBT(wrongNbt);

    assertThat(toolItemNBT.getToolItem()).isNotNull();
  }
}
