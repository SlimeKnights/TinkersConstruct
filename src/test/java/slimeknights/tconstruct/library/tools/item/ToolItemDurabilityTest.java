package slimeknights.tconstruct.library.tools.item;

import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static org.assertj.core.api.Assertions.assertThat;

public class ToolItemDurabilityTest extends ToolItemTest {

  @Test
  void testNewToolDurability() {
    int statDurability = ToolStack.from(testItemStack).getStats().getInt(ToolStats.DURABILITY);

    assertThat(testItemStack.getDamageValue()).isEqualTo(0);
    assertThat(testItemStack.getMaxDamage()).isEqualTo(statDurability);
    assertThat(IsTestItemBroken()).isFalse();
  }

  @Test
  void testSettingDamage() {
    int statDurability = ToolStack.from(testItemStack).getStats().getInt(ToolStats.DURABILITY);

    testItemStack.setDamageValue(1);

    assertThat(testItemStack.getDamageValue()).isEqualTo(1);
    assertThat(testItemStack.getMaxDamage()).isEqualTo(statDurability);
    assertThat(IsTestItemBroken()).isFalse();
  }

  /*
  @Test
  void testDealingDamage() {
    testItemStack.damageItem(10, TestLivingEntity.getTestLivingEntity(), testLivingEntity -> {});

    assertThat(testItemStack.getDamage()).isEqualTo(10);
    assertThat(isTestitemBroken()).isFalse();
  }
  */

  @Test
  void testMaxDamageBreaksTool() {
    ToolStack tool = ToolStack.from(testItemStack);
    int statDurability = tool.getStats().getInt(ToolStats.DURABILITY);

    tool.setDamage(statDurability);

    assertThat(tool.getDamage()).isEqualTo(statDurability);
    assertThat(tool.isBroken()).isTrue();
  }

  @Test
  void testMoreThanMaxDamageBreaksTool() {
    ToolStack tool = ToolStack.from(testItemStack);
    int statDurability = tool.getStats().getInt(ToolStats.DURABILITY);

    testItemStack.setDamageValue(99999999);

    assertThat(tool.getDamage()).isEqualTo(statDurability);
    assertThat(tool.isBroken()).isTrue();
  }

  /*
  @Test
  void testVanillaBreakCallback() {
    AtomicBoolean callbackCalled = new AtomicBoolean(false);
    testItemStack.damageItem(99999, TestLivingEntity.getTestLivingEntity(),
      testLivingEntity -> callbackCalled.set(true));

    // this works because the callback is called synchronously
    assertThat(callbackCalled.get()).isTrue();
    assertThat(isTestitemBroken()).isTrue();
  }
  */

  /*
  @Test
  void testVanillaBreakDoesNotReduceStacksize() {
    testItemStack.damageItem(99999, TestLivingEntity.getTestLivingEntity(),
      testLivingEntity -> {});

    assertThat(isTestitemBroken()).isTrue();
    assertThat(testItemStack.isEmpty()).isFalse();
  }
  */
}
