package slimeknights.tconstruct.library.tools.item;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
//import static org.mockito.Mockito.verify;

public class ToolItemHarvestTest extends ToolItemTest {

  //private PlayerEntity player = spy(new TestPlayerEntity(TestServerWorld.getTestServerWorld()));

  @Test
  void getToolTypes_notBroken_ok() {
    assertThat(IsTestItemBroken()).isFalse();
//    assertThat(testItemStack.getToolTypes()).contains(ToolType.PICKAXE);
    fail("TODO: implement tool actions hook");
  }

  @Test
  void getToolTypes_broken_none() {
    breakTool(testItemStack);

    assertThat(IsTestItemBroken()).isTrue();
    fail("TODO: implement tool actions hook");
    //assertThat(testItemStack.getToolTypes()).isEmpty();
  }

  @Test
  void getHarvestLevel_notBroken_ok() {
    fail("TODO: harvest level hooks");
//    assertThat(testItemStack.getHarvestLevel(ToolType.PICKAXE, null, null)).isEqualTo(1);
//    assertThat(testItemStack.getHarvestLevel(ToolType.SHOVEL, null, null)).isEqualTo(-1);
  }

  @Test
  void getHarvestLevel_broken_none() {
    breakTool(testItemStack);
    fail("TODO: harvest level hooks");
//    assertThat(testItemStack.getHarvestLevel(ToolType.PICKAXE, null, null)).isEqualTo(-1);
//    assertThat(testItemStack.getHarvestLevel(ToolType.SHOVEL, null, null)).isEqualTo(-1);
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
