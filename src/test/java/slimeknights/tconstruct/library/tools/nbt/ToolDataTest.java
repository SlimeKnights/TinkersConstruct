package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

class ToolDataTest extends BaseMcTest {

  private final ToolData testToolData = new ToolData(ToolItemNBT.EMPTY, MaterialNBT.EMPTY, StatsNBT.EMPTY);

  @Test
  void serializeNBT() {
    CompoundNBT nbt = testToolData.serializeToNBT();

    assertThat(nbt.contains("Unknown")).isTrue();
    assertThat(nbt.getTagId("Unknown")).isEqualTo((byte) Constants.NBT.TAG_STRING);
    assertThat(nbt.contains(Tags.MATERIALS)).isTrue();
    assertThat(nbt.getTagId(Tags.MATERIALS)).isEqualTo((byte) Constants.NBT.TAG_LIST);
    assertThat(nbt.contains(Tags.BASE)).isTrue();
    assertThat(nbt.getTagId(Tags.BASE)).isEqualTo((byte) Constants.NBT.TAG_COMPOUND);
  }

  @Test
  void deserializeNBT_item() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(Tags.BASE, StringNBT.valueOf("test:tool_item"));

    ToolData toolData = ToolData.readFromNBT(nbt);

    assertThat(toolData.getToolItem()).isNotNull();
  }

  @Test
  void deserializeNBT_materials() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(Tags.MATERIALS, new ListNBT());

    ToolData toolData = ToolData.readFromNBT(nbt);

    assertThat(toolData.getMaterials()).isNotNull();
  }

  @Test
  void deserializeNBT_stats() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.put(Tags.BASE, new CompoundNBT());

    ToolData toolData = ToolData.readFromNBT(nbt);

    assertThat(toolData.getMaterials()).isNotNull();
  }

  @Test
  void deserialize_empty() {
    CompoundNBT nbt = new CompoundNBT();

    ToolData toolData = ToolData.readFromNBT(nbt);

    assertThat(toolData.getToolItem()).isNotNull();
    assertThat(toolData.getMaterials()).isNotNull();
    assertThat(toolData.getStats()).isNotNull();
  }
}
