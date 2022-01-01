package slimeknights.tconstruct.library.tools.nbt;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.library.materials.MaterialRegistryExtension;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BaseMcTest;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MaterialRegistryExtension.class)
class MultiplierNBTTest extends BaseMcTest {
  @Test
  void empty_ensureEmpty() {
    // ensure no stats present
    assertThat(MultiplierNBT.EMPTY.getContainedStats()).isEmpty();
    // ensure defaults
    assertThat(MultiplierNBT.EMPTY.get(ToolStats.DURABILITY)).isEqualTo(1f);
    assertThat(MultiplierNBT.EMPTY.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(1f);
  }

  @Test
  void builder_builds() {
    MultiplierNBT multipliers = MultiplierNBT
      .builder()
      .set(ToolStats.DURABILITY, 6)
      .set(ToolStats.ATTACK_DAMAGE, 4)
      .set(ToolStats.MINING_SPEED, 3.5f)
      .set(ToolStats.ATTACK_SPEED, 2)
      .set(ToolStats.ARMOR, 1)
      .build();

    // multiplier of 1 is skipped
    assertThat(multipliers.getContainedStats()).hasSize(4);
    assertThat(multipliers.get(ToolStats.DURABILITY)).isEqualTo(6);
    assertThat(multipliers.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(4);
    assertThat(multipliers.get(ToolStats.MINING_SPEED)).isEqualTo(3.5f);
    assertThat(multipliers.get(ToolStats.ATTACK_SPEED)).isEqualTo(2);
    assertThat(multipliers.get(ToolStats.ARMOR)).isEqualTo(1);
  }

  @Test
  void buffer_readsWrites() {
    MultiplierNBT multipliers = MultiplierNBT
      .builder()
      .set(ToolStats.DURABILITY, 4)
      .set(ToolStats.ATTACK_DAMAGE, 3.5f)
      .set(ToolStats.MINING_SPEED, 8)
      .set(ToolStats.ATTACK_SPEED, 2)
      .build();

    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    multipliers.toNetwork(buffer);
    MultiplierNBT decoded = MultiplierNBT.fromNetwork(buffer);

    // multiplier of 1 is skipped
    assertThat(decoded.getContainedStats()).hasSize(4);
    assertThat(decoded.get(ToolStats.DURABILITY)).isEqualTo(4);
    assertThat(decoded.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(3.5f);
    assertThat(decoded.get(ToolStats.MINING_SPEED)).isEqualTo(8);
    assertThat(decoded.get(ToolStats.ATTACK_SPEED)).isEqualTo(2);
  }

  @Test
  void serialize() {
    MultiplierNBT testStatsNBT = MultiplierNBT
      .builder()
      .set(ToolStats.DURABILITY, 1)
      .set(ToolStats.ATTACK_DAMAGE, 3)
      .set(ToolStats.MINING_SPEED, 4)
      .set(ToolStats.ATTACK_SPEED, 5)
      .build();
    CompoundTag nbt = testStatsNBT.serializeToNBT();

    // 1 is ignored as default
    assertThat(nbt.getAllKeys()).hasSize(3);
    assertThat(nbt.contains(ToolStats.DURABILITY.getName().toString())).isFalse();
    assertThat(nbt.getFloat(ToolStats.ATTACK_DAMAGE.getName().toString())).isEqualTo(3);
    assertThat(nbt.getFloat(ToolStats.MINING_SPEED.getName().toString())).isEqualTo(4);
    assertThat(nbt.getFloat(ToolStats.ATTACK_SPEED.getName().toString())).isEqualTo(5);
  }

  @Test
  void serializeEmpty_emptyList() {
    CompoundTag nbt = MultiplierNBT.EMPTY.serializeToNBT();
    assertThat(nbt.size()).isEqualTo(0);
  }

  @Test
  void deserialize() {
    CompoundTag nbt = new CompoundTag();
    nbt.putInt(ToolStats.DURABILITY.getName().toString(), 6);
    nbt.putFloat(ToolStats.ATTACK_DAMAGE.getName().toString(), 4);
    nbt.putFloat(ToolStats.MINING_SPEED.getName().toString(), 3.5f);
    nbt.putFloat(ToolStats.ATTACK_SPEED.getName().toString(), 2);
    nbt.putFloat(ToolStats.ARMOR.getName().toString(), 1);

    MultiplierNBT multipliers = MultiplierNBT.readFromNBT(nbt);

    // multiplier of 1 is skipped
    assertThat(multipliers.getContainedStats()).hasSize(4);
    assertThat(multipliers.get(ToolStats.DURABILITY)).isEqualTo(6);
    assertThat(multipliers.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(4);
    assertThat(multipliers.get(ToolStats.MINING_SPEED)).isEqualTo(3.5f);
    assertThat(multipliers.get(ToolStats.ATTACK_SPEED)).isEqualTo(2);
    assertThat(multipliers.get(ToolStats.ARMOR)).isEqualTo(1);
  }

  @Test
  void deserializeNoData_empty() {
    CompoundTag nbt = new CompoundTag();
    MultiplierNBT multipliers = MultiplierNBT.readFromNBT(nbt);
    assertThat(multipliers).isEqualTo(MultiplierNBT.EMPTY);
  }

  @Test
  void wrongNbtType_empty() {
    Tag wrongNbt = new CompoundTag();
    StatsNBT statsNBT = StatsNBT.readFromNBT(wrongNbt);
    assertThat(statsNBT).isEqualTo(StatsNBT.EMPTY);
  }

  @Test
  void missing_isDefault() {
    MultiplierNBT partialStatsNBT = MultiplierNBT
      .builder()
      .set(ToolStats.DURABILITY, 1f)
      .set(ToolStats.ATTACK_DAMAGE, 3f)
      .build();
    assertThat(partialStatsNBT.get(ToolStats.MINING_SPEED)).isEqualTo(1f);
  }
}
