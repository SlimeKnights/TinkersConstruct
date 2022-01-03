package slimeknights.tconstruct.library.tools.item;

import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.verify;

public class ToolItemHarvestTest extends ToolItemTest {

  //private PlayerEntity player = spy(new TestPlayerEntity(TestServerWorld.getTestServerWorld()));

  @Test
  void getToolTypes_notBroken_ok() {
    assertThat(IsTestItemBroken()).isFalse();
    assertThat(testItemStack.canPerformAction(ToolActions.PICKAXE_DIG)).isTrue();
  }

  @Test
  void getToolTypes_broken_none() {

    breakTool(testItemStack);

    assertThat(IsTestItemBroken()).isTrue();
    assertThat(testItemStack.canPerformAction(ToolActions.PICKAXE_DIG)).isFalse();
  }

  @Test
  void getHarvestLevel_notBroken_ok() {
    assertThat(IsTestItemBroken()).isFalse();
    assertThat(testItemStack.isCorrectToolForDrops(Blocks.STONE.defaultBlockState())).isTrue();
  }

  @Test
  void getHarvestLevel_broken_none() {
    breakTool(testItemStack);
    assertThat(IsTestItemBroken()).isTrue();
    assertThat(testItemStack.isCorrectToolForDrops(Blocks.STONE.defaultBlockState())).isFalse();
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
