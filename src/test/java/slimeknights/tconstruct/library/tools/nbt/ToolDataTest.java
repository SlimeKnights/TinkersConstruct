package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.TestToolCore;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

class ToolDataTest extends BaseMcTest {

  private static final TestToolCore testToolItem = new TestToolCore();

  private final ToolData testToolData = new ToolData(testToolItem, MaterialNBT.EMPTY);

  @BeforeAll
  static void beforeAll() {
    testToolItem.setRegistryName("test:tool_item");
    ForgeRegistries.ITEMS.register(testToolItem);
  }

  @Test
  void serializeNBT_item() {
    CompoundNBT nbt = testToolData.serializeToNBT();

    assertThat(nbt.getString(ToolData.TAG_ITEM)).isEqualTo("test:tool_item");
  }

  @Test
  void serializeNBT_materials() {
    CompoundNBT nbt = testToolData.serializeToNBT();

    assertThat(nbt.contains(ToolData.TAG_MATERIALS, Constants.NBT.TAG_STRING)).isTrue();
  }


  @Test
  void deserializeNBT_item() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(ToolData.TAG_ITEM, new StringNBT("test:tool_item"));

    ToolData toolData = ToolData.readFromNBT(nbt);

    assertThat(toolData.toolItem).isEqualTo(testToolItem);
  }

  @Test
  void deserializeNBT_materials() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(ToolData.TAG_MATERIALS, new ListNBT());

    ToolData toolData = ToolData.readFromNBT(nbt);

    assertThat(toolData.materials).isNotNull();
    assertThat(toolData.materials.getMaterials()).isNotNull();
  }


  @Test
  void serializeMissingTool_emptyOrNoItemTag() {
    //noinspection ConstantConditions
    ToolData toolData = new ToolData(null, MaterialNBT.EMPTY);

    CompoundNBT nbt = toolData.serializeToNBT();

    assertThat(nbt.getString(ToolData.TAG_ITEM)).isEmpty();
  }
}
