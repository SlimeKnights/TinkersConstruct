package slimeknights.tconstruct.library.tools;

import net.minecraftforge.common.ToolType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.verify;

public class ToolCoreHarvestTest extends ToolCoreTest {

  //private PlayerEntity player = spy(new TestPlayerEntity(TestServerWorld.getTestServerWorld()));

  @Test
  void getToolTypes_notBroken_ok() {
    assertThat(testItemStack.getToolTypes()).contains(ToolType.PICKAXE);
    assertThat(IsTestItemBroken()).isFalse();
  }

  @Test
  void getToolTypes_broken_none() {
    breakTool(testItemStack);

    assertThat(testItemStack.getToolTypes()).isEmpty();
    assertThat(IsTestItemBroken()).isTrue();
  }

  @Test
  void getHarvestLevel_notBroken_ok() {
    assertThat(testItemStack.getHarvestLevel(ToolType.PICKAXE, null, null)).isEqualTo(1);
    assertThat(testItemStack.getHarvestLevel(ToolType.SHOVEL, null, null)).isEqualTo(-1);
  }

  @Test
  void getHarvestLevel_broken_none() {
    breakTool(testItemStack);

    assertThat(testItemStack.getHarvestLevel(ToolType.PICKAXE, null, null)).isEqualTo(-1);
    assertThat(testItemStack.getHarvestLevel(ToolType.SHOVEL, null, null)).isEqualTo(-1);
  }

  /*
  @Test
  void onBlockBreak_effective_oneDamage() {
    int damageBefore = testItemStack.getDamage();

    breakBlock(Blocks.DIRT);

    assertThat(testItemStack.getDamage() - damageBefore).isEqualTo(1);
    verify(player).addStat(any(Stat.class));
  }
  */

  /*
  @Test
  void onBlockBreak_notEffective_twoDamage() {
    int damageBefore = testItemStack.getDamage();

    breakBlock(Blocks.OBSIDIAN);

    assertThat(testItemStack.getDamage() - damageBefore).isEqualTo(2);
    verify(player, never()).addStat(any(Stat.class));
  }
  */

  // trait interaction is tested in trait tests
  /*
  @Test
  void onBlockBreak_broken_noInteraction() {
    breakTool(testItemStack);
    int damageBefore = testItemStack.getDamage();

    breakBlock(Blocks.DIRT);

    assertThat(testItemStack.getDamage() - damageBefore).isEqualTo(0);
    verify(player, never()).addStat(any(Stat.class));
  }
  */

  /*
  private void breakBlock(Block block) {
    BlockState blockState = new BlockState(block, ImmutableMap.of());

    testItemStack.onBlockDestroyed(player.world, blockState, BlockPos.ZERO, player);
  }
  */

}
