package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.library.MaterialRegistryExtension;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MaterialRegistryExtension.class)
class StatsNBTTest extends BaseMcTest {

  private final StatsNBT testStatsNBT = StatsNBT.builder()
                                                .set(ToolStats.DURABILITY, 1)
                                                .set(ToolStats.HARVEST_LEVEL, 2)
                                                .set(ToolStats.ATTACK_DAMAGE, 3)
                                                .set(ToolStats.MINING_SPEED, 4)
                                                .set(ToolStats.ATTACK_SPEED, 5)
                                                .set(ToolStats.REACH, 6)
                                                .build();

  @Test
  void serialize() {
    CompoundNBT nbt = testStatsNBT.serializeToNBT();
    
    assertThat(nbt.getInt(ToolStats.DURABILITY.getName().toString())).isEqualTo(1);
    assertThat(nbt.getInt(ToolStats.HARVEST_LEVEL.getName().toString())).isEqualTo(2);
    assertThat(nbt.getFloat(ToolStats.ATTACK_DAMAGE.getName().toString())).isEqualTo(3);
    assertThat(nbt.getFloat(ToolStats.MINING_SPEED.getName().toString())).isEqualTo(4);
    assertThat(nbt.getFloat(ToolStats.ATTACK_SPEED.getName().toString())).isEqualTo(5);
    assertThat(nbt.getFloat(ToolStats.REACH.getName().toString())).isEqualTo(6);
  }

  @Test
  void serializeEmpty_emptyList() {
    CompoundNBT nbt = StatsNBT.EMPTY.serializeToNBT();

    assertThat(nbt.size()).isEqualTo(0);
  }

  @Test
  void deserialize() {
    CompoundNBT nbt = new CompoundNBT();
    nbt.putInt(ToolStats.DURABILITY.getName().toString(), 6);
    nbt.putInt(ToolStats.HARVEST_LEVEL.getName().toString(), 5);
    nbt.putFloat(ToolStats.ATTACK_DAMAGE.getName().toString(), 4);
    nbt.putFloat(ToolStats.MINING_SPEED.getName().toString(), 3.5f);
    nbt.putFloat(ToolStats.ATTACK_SPEED.getName().toString(), 2);
    nbt.putFloat(ToolStats.REACH.getName().toString(), 1);

    StatsNBT statsNBT = StatsNBT.readFromNBT(nbt);

    assertThat(statsNBT.getInt(ToolStats.DURABILITY)).isEqualTo(6);
    assertThat(statsNBT.getInt(ToolStats.HARVEST_LEVEL)).isEqualTo(5);
    assertThat(statsNBT.getFloat(ToolStats.ATTACK_DAMAGE)).isEqualTo(4);
    assertThat(statsNBT.getFloat(ToolStats.MINING_SPEED)).isEqualTo(3.5f);
    assertThat(statsNBT.getFloat(ToolStats.ATTACK_SPEED)).isEqualTo(2);
    assertThat(statsNBT.getFloat(ToolStats.REACH)).isEqualTo(1);
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

  @Test
  void missing_isDefault() {
    StatsNBT partialStatsNBT = StatsNBT.builder()
                                       .set(ToolStats.DURABILITY, 1)
                                       .set(ToolStats.HARVEST_LEVEL, 2)
                                       .set(ToolStats.ATTACK_DAMAGE, 3)
                                       .set(ToolStats.MINING_SPEED, 4)
                                       .build();
    assertThat(partialStatsNBT.getFloat(ToolStats.REACH)).isEqualTo(ToolStats.REACH.getDefaultValue());
  }
}
