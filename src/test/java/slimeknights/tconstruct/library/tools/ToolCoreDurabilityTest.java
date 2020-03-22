package slimeknights.tconstruct.library.tools;

import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.library.tools.nbt.ToolData;
import slimeknights.tconstruct.test.TestLivingEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class ToolCoreDurabilityTest extends ToolCoreTest {

  @Test
  void testNewToolDurability() {
    int statDurability = ToolData.from(testItemStack).getStats().durability;

    assertThat(testItemStack.getDamage()).isEqualTo(0);
    assertThat(testItemStack.getMaxDamage()).isEqualTo(statDurability);
    assertThat(isTestitemBroken()).isFalse();
  }

  @Test
  void testSettingDamage() {
    int statDurability = ToolData.from(testItemStack).getStats().durability;

    testItemStack.setDamage(1);

    assertThat(testItemStack.getDamage()).isEqualTo(1);
    assertThat(testItemStack.getMaxDamage()).isEqualTo(statDurability);
    assertThat(isTestitemBroken()).isFalse();
  }

  @Test
  void testDealingDamage() {
    testItemStack.damageItem(10, TestLivingEntity.getTestLivingEntity(), testLivingEntity -> {});

    assertThat(testItemStack.getDamage()).isEqualTo(10);
    assertThat(isTestitemBroken()).isFalse();
  }

  @Test
  void testMaxDamageBreaksTool() {
    int statDurability = ToolData.from(testItemStack).getStats().durability;

    testItemStack.setDamage(statDurability);

    assertThat(testItemStack.getDamage()).isEqualTo(statDurability);
    assertThat(isTestitemBroken()).isTrue();
  }

  @Test
  void testMoreThanMaxDamageBreaksTool() {
    int statDurability = ToolData.from(testItemStack).getStats().durability;

    testItemStack.setDamage(99999999);

    assertThat(testItemStack.getDamage()).isEqualTo(statDurability);
    assertThat(isTestitemBroken()).isTrue();
  }

  @Test
  void testVanillaBreakCallback() {
    testItemStack.damageItem(99999, TestLivingEntity.getTestLivingEntity(),
      testLivingEntity -> fail("Vanilla on-break-callback got called. " +
        "This means the itemstack got reduced, removing the item. " +
        "This should never happen with TiC tools"));

    assertThat(isTestitemBroken()).isTrue();
  }

  @Test
  void testVanillaBreakDoesNotReduceStacksize() {
    testItemStack.damageItem(99999, TestLivingEntity.getTestLivingEntity(),
      testLivingEntity -> {});

    assertThat(isTestitemBroken()).isTrue();
    assertThat(testItemStack.isEmpty()).isFalse();
  }
}
