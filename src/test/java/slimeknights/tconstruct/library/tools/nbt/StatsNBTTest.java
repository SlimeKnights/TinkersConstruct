package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.library.utils.Tags;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MaterialRegistryExtension.class)
class StatsNBTTest extends BaseMcTest {

  private final StatsNBT testStatsNBT = new StatsNBT(1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13, true);

  @Test
  void serialize() {
    CompoundNBT nbt = testStatsNBT.serializeToNBT();
    
    assertThat(nbt.getInt(Tags.DURABILITY)).isEqualTo(1);
    assertThat(nbt.getInt(Tags.HARVEST_LEVEL)).isEqualTo(2);
    assertThat(nbt.getFloat(Tags.ATTACK)).isEqualTo(3);
    assertThat(nbt.getFloat(Tags.MINING_SPEED)).isEqualTo(4);
    assertThat(nbt.getFloat(Tags.REPAIR_COUNT)).isEqualTo(5);
    assertThat(nbt.getFloat(Tags.MINING_SPEED_MULTIPLIER)).isEqualTo(6);
    assertThat(nbt.getFloat(Tags.ATTACK_SPEED_MULTIPLIER)).isEqualTo(7);
    assertThat(nbt.getInt(Tags.BONUS_DURABILITY)).isEqualTo(8);
    assertThat(nbt.getInt(Tags.BONUS_DURABILITY_MULTIPLIER)).isEqualTo(9);
    assertThat(nbt.getInt(Tags.FREE_UPGRADE_SLOTS)).isEqualTo(10);
    assertThat(nbt.getInt(Tags.FREE_ABILITY_SLOTS)).isEqualTo(11);
    assertThat(nbt.getInt(Tags.FREE_ARMOR_SLOTS)).isEqualTo(12);
    assertThat(nbt.getInt(Tags.FREE_TRAIT_SLOTS)).isEqualTo(13);
    assertThat(nbt.getBoolean(Tags.BROKEN)).isTrue();
  }

  @Test
  void serializeEmpty_emptyList() {
    CompoundNBT nbt = StatsNBT.EMPTY.serializeToNBT();

    assertThat(nbt.size()).isEqualTo(14);
  }

  @Test
  void deserialize() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(Tags.DURABILITY, 13);
    nbt.putInt(Tags.HARVEST_LEVEL, 12);
    nbt.putFloat(Tags.ATTACK, 11);
    nbt.putFloat(Tags.MINING_SPEED, 10);
    nbt.putInt(Tags.REPAIR_COUNT, 9);
    nbt.putFloat(Tags.MINING_SPEED_MULTIPLIER, 8);
    nbt.putFloat(Tags.ATTACK_SPEED_MULTIPLIER, 7);
    nbt.putInt(Tags.BONUS_DURABILITY, 6);
    nbt.putInt(Tags.BONUS_DURABILITY_MULTIPLIER, 5);
    nbt.putInt(Tags.FREE_UPGRADE_SLOTS, 4);
    nbt.putInt(Tags.FREE_ABILITY_SLOTS, 3);
    nbt.putInt(Tags.FREE_ARMOR_SLOTS, 2);
    nbt.putInt(Tags.FREE_TRAIT_SLOTS, 1);
    nbt.putBoolean(Tags.BROKEN, false);

    StatsNBT statsNBT = StatsNBT.readFromNBT(nbt);

    assertThat(statsNBT.durability).isEqualTo(13);
    assertThat(statsNBT.harvestLevel).isEqualTo(12);
    assertThat(statsNBT.attack).isEqualTo(11);
    assertThat(statsNBT.miningSpeed).isEqualTo(10);
    assertThat(statsNBT.repairCount).isEqualTo(9);
    assertThat(statsNBT.miningSpeedMultiplier).isEqualTo(8);
    assertThat(statsNBT.attackSpeedMultiplier).isEqualTo(7);
    assertThat(statsNBT.bonusDurability).isEqualTo(6);
    assertThat(statsNBT.bonusDurabilityMultiplier).isEqualTo(5);
    assertThat(statsNBT.freeUpgradeSlots).isEqualTo(4);
    assertThat(statsNBT.freeAbilitySlots).isEqualTo(3);
    assertThat(statsNBT.freeArmorSlots).isEqualTo(2);
    assertThat(statsNBT.freeTraitSlots).isEqualTo(1);
    assertThat(statsNBT.broken).isFalse();
  }

  @Test
  void deserializeNoData_empty() {
    CompoundNBT nbt = new CompoundNBT();

    StatsNBT statsNBT = StatsNBT.readFromNBT(nbt);

    assertThat(statsNBT).isEqualTo(StatsNBT.EMPTY);
  }

  @Test
  void wrongNbtType_empty() {
    INBT wrongNbt = new CompoundNBT();

    StatsNBT statsNBT = StatsNBT.readFromNBT(wrongNbt);

    assertThat(statsNBT).isEqualTo(StatsNBT.EMPTY);
  }
}
