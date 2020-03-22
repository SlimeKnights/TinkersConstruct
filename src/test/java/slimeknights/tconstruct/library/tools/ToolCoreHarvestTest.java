package slimeknights.tconstruct.library.tools;

import net.minecraftforge.common.ToolType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolCoreHarvestTest extends ToolCoreTest {

  @Test
  void getToolTypes_okWhenNotBroken() {
    assertThat(testItemStack.getToolTypes()).contains(ToolType.PICKAXE);
    assertThat(isTestitemBroken()).isFalse();
  }

  @Test
  void getToolTypes_noneWhenBroken() {
    breakTool(testItemStack);

    assertThat(testItemStack.getToolTypes()).isEmpty();
    assertThat(isTestitemBroken()).isTrue();
  }

  @Test
  void getHarvestLevel_okWhenNotBroken() {
    assertThat(testItemStack.getHarvestLevel(ToolType.PICKAXE, null, null)).isEqualTo(1);
    assertThat(testItemStack.getHarvestLevel(ToolType.SHOVEL, null, null)).isEqualTo(-1);
  }

  @Test
  void getHarvestLevel_noneWhenBroken() {
    breakTool(testItemStack);

    assertThat(testItemStack.getHarvestLevel(ToolType.PICKAXE, null, null)).isEqualTo(-1);
    assertThat(testItemStack.getHarvestLevel(ToolType.SHOVEL, null, null)).isEqualTo(-1);
  }
}
