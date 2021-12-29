package slimeknights.tconstruct.library.tools.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.library.materials.MaterialRegistryExtension;
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
                                                .build();

  @Test
  void serialize() {
    CompoundTag nbt = testStatsNBT.serializeToNBT();
    
    assertThat(nbt.getInt(ToolStats.DURABILITY.getName().toString())).isEqualTo(1);
    assertThat(nbt.getInt(ToolStats.HARVEST_LEVEL.getName().toString())).isEqualTo(2);
    assertThat(nbt.getFloat(ToolStats.ATTACK_DAMAGE.getName().toString())).isEqualTo(3);
    assertThat(nbt.getFloat(ToolStats.MINING_SPEED.getName().toString())).isEqualTo(4);
    assertThat(nbt.getFloat(ToolStats.ATTACK_SPEED.getName().toString())).isEqualTo(5);
  }

  @Test
  void serializeEmpty_emptyList() {
    CompoundTag nbt = StatsNBT.EMPTY.serializeToNBT();

    assertThat(nbt.size()).isEqualTo(0);
  }

  @Test
  void deserialize() {
    CompoundTag nbt = new CompoundTag();
    nbt.putInt(ToolStats.DURABILITY.getName().toString(), 6);
    nbt.putInt(ToolStats.HARVEST_LEVEL.getName().toString(), 5);
    nbt.putFloat(ToolStats.ATTACK_DAMAGE.getName().toString(), 4);
    nbt.putFloat(ToolStats.MINING_SPEED.getName().toString(), 3.5f);
    nbt.putFloat(ToolStats.ATTACK_SPEED.getName().toString(), 2);

    StatsNBT statsNBT = StatsNBT.readFromNBT(nbt);

    assertThat(statsNBT.getInt(ToolStats.DURABILITY)).isEqualTo(6);
    assertThat(statsNBT.getInt(ToolStats.HARVEST_LEVEL)).isEqualTo(5);
    assertThat(statsNBT.getFloat(ToolStats.ATTACK_DAMAGE)).isEqualTo(4);
    assertThat(statsNBT.getFloat(ToolStats.MINING_SPEED)).isEqualTo(3.5f);
    assertThat(statsNBT.getFloat(ToolStats.ATTACK_SPEED)).isEqualTo(2);
  }

  @Test
  void deserializeNoData_empty() {
    CompoundTag nbt = new CompoundTag();

    StatsNBT statsNBT = StatsNBT.readFromNBT(nbt);

    assertThat(statsNBT).isEqualTo(StatsNBT.EMPTY);
  }

  @Test
  void wrongNbtType_empty() {
    Tag wrongNbt = new CompoundTag();

    StatsNBT statsNBT = StatsNBT.readFromNBT(wrongNbt);

    assertThat(statsNBT).isEqualTo(StatsNBT.EMPTY);
  }

  @Test
  void missing_isDefault() {
    StatsNBT partialStatsNBT = StatsNBT.builder()
                                       .set(ToolStats.DURABILITY, 1)
                                       .set(ToolStats.HARVEST_LEVEL, 2)
                                       .set(ToolStats.ATTACK_DAMAGE, 3)
                                       .build();
    assertThat(partialStatsNBT.getFloat(ToolStats.MINING_SPEED)).isEqualTo(ToolStats.MINING_SPEED.getDefaultValue());
  }
}
