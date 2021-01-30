package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MaterialRegistryExtension.class)
class StatsNBTTest extends BaseMcTest {

  private final StatsNBT testStatsNBT = new StatsNBT(1, 2, 3, 4, 5);

  @Test
  void serialize() {
    CompoundNBT nbt = testStatsNBT.serializeToNBT();
    
    assertThat(nbt.getInt(StatsNBT.TAG_DURABILITY)).isEqualTo(1);
    assertThat(nbt.getInt(StatsNBT.TAG_HARVEST_LEVEL)).isEqualTo(2);
    assertThat(nbt.getFloat(StatsNBT.TAG_ATTACK)).isEqualTo(3);
    assertThat(nbt.getFloat(StatsNBT.TAG_MINING_SPEED)).isEqualTo(4);
    assertThat(nbt.getFloat(StatsNBT.TAG_ATTACK_SPEED_MULTIPLIER)).isEqualTo(5);
  }

  @Test
  void serializeEmpty_emptyList() {
    CompoundNBT nbt = StatsNBT.EMPTY.serializeToNBT();

    assertThat(nbt.size()).isEqualTo(5);
  }

  @Test
  void deserialize() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(StatsNBT.TAG_DURABILITY, 6);
    nbt.putInt(StatsNBT.TAG_HARVEST_LEVEL, 5);
    nbt.putFloat(StatsNBT.TAG_ATTACK, 4);
    nbt.putFloat(StatsNBT.TAG_MINING_SPEED, 3);
    nbt.putFloat(StatsNBT.TAG_ATTACK_SPEED_MULTIPLIER, 2);

    StatsNBT statsNBT = StatsNBT.readFromNBT(nbt);

    assertThat(statsNBT.durability).isEqualTo(6);
    assertThat(statsNBT.harvestLevel).isEqualTo(5);
    assertThat(statsNBT.attackDamage).isEqualTo(4);
    assertThat(statsNBT.miningSpeed).isEqualTo(3);
    assertThat(statsNBT.attackSpeedMultiplier).isEqualTo(2);
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
