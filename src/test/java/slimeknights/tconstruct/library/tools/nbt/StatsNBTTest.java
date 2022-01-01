package slimeknights.tconstruct.library.tools.nbt;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.TierSortingRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import slimeknights.tconstruct.library.materials.MaterialRegistryExtension;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.test.BaseMcTest;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MaterialRegistryExtension.class)
class StatsNBTTest extends BaseMcTest {
  @BeforeAll
  static void setupTiers() {
    setupTierSorting();
  }

  @Test
  void empty_ensureEmpty() {
    // ensure no stats present
    assertThat(StatsNBT.EMPTY.getContainedStats()).isEmpty();
    // ensure defaults
    assertThat(StatsNBT.EMPTY.get(ToolStats.DURABILITY)).isEqualTo(ToolStats.DURABILITY.getDefaultValue());
    assertThat(StatsNBT.EMPTY.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(ToolStats.ATTACK_DAMAGE.getDefaultValue());
  }

  @Test
  void builder_builds() {
    StatsNBT stats = StatsNBT
      .builder()
      .set(ToolStats.DURABILITY, 6f)
      .set(ToolStats.HARVEST_TIER, Tiers.STONE)
      .set(ToolStats.ATTACK_DAMAGE, 4f)
      .set(ToolStats.MINING_SPEED, 3.5f)
      .set(ToolStats.ATTACK_SPEED, 2f)
      .set(ToolStats.ARMOR, 1f)
      .build();

    assertThat(stats.getContainedStats()).hasSize(6);
    assertThat(stats.get(ToolStats.DURABILITY)).isEqualTo(6);
    assertThat(stats.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.STONE);
    assertThat(stats.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(4);
    assertThat(stats.get(ToolStats.MINING_SPEED)).isEqualTo(3.5f);
    assertThat(stats.get(ToolStats.ATTACK_SPEED)).isEqualTo(2);
    assertThat(stats.get(ToolStats.ARMOR)).isEqualTo(1);
  }

  @Test
  void buffer_readsWrites() {
    StatsNBT stats = StatsNBT
      .builder()
      .set(ToolStats.DURABILITY, 4f)
      .set(ToolStats.HARVEST_TIER, Tiers.IRON)
      .set(ToolStats.ATTACK_DAMAGE, 3.5f)
      .set(ToolStats.MINING_SPEED, 8f)
      .set(ToolStats.ATTACK_SPEED, 2f)
      .build();

    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    stats.toNetwork(buffer);
    StatsNBT decoded = StatsNBT.fromNetwork(buffer);

    assertThat(decoded.getContainedStats()).hasSize(5);
    assertThat(decoded.get(ToolStats.DURABILITY)).isEqualTo(4f);
    assertThat(decoded.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.IRON);
    assertThat(decoded.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(3.5f);
    assertThat(decoded.get(ToolStats.MINING_SPEED)).isEqualTo(8);
    assertThat(decoded.get(ToolStats.ATTACK_SPEED)).isEqualTo(2);
  }

  @Test
  void serialize() {
    StatsNBT testStatsNBT = StatsNBT
      .builder()
      .set(ToolStats.DURABILITY, 1f)
      .set(ToolStats.HARVEST_TIER, Tiers.NETHERITE)
      .set(ToolStats.ATTACK_DAMAGE, 3f)
      .set(ToolStats.MINING_SPEED, 4f)
      .set(ToolStats.ATTACK_SPEED, 5f)
      .build();
    CompoundTag nbt = testStatsNBT.serializeToNBT();
    
    assertThat(nbt.getInt(ToolStats.DURABILITY.getName().toString())).isEqualTo(1);
    assertThat(nbt.getString(ToolStats.HARVEST_TIER.getName().toString())).isEqualTo(Objects.requireNonNull(TierSortingRegistry.getName(Tiers.NETHERITE)).toString());
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
    nbt.putString(ToolStats.HARVEST_TIER.getName().toString(), Objects.requireNonNull(TierSortingRegistry.getName(Tiers.GOLD)).toString());
    nbt.putFloat(ToolStats.ATTACK_DAMAGE.getName().toString(), 4);
    nbt.putFloat(ToolStats.MINING_SPEED.getName().toString(), 3.5f);
    nbt.putFloat(ToolStats.ATTACK_SPEED.getName().toString(), 2);

    StatsNBT statsNBT = StatsNBT.readFromNBT(nbt);

    assertThat(statsNBT.getContainedStats()).hasSize(5);
    assertThat(statsNBT.getInt(ToolStats.DURABILITY)).isEqualTo(6);
    assertThat(statsNBT.get(ToolStats.HARVEST_TIER)).isEqualTo(Tiers.GOLD);
    assertThat(statsNBT.get(ToolStats.ATTACK_DAMAGE)).isEqualTo(4);
    assertThat(statsNBT.get(ToolStats.MINING_SPEED)).isEqualTo(3.5f);
    assertThat(statsNBT.get(ToolStats.ATTACK_SPEED)).isEqualTo(2);
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
                                       .set(ToolStats.DURABILITY, 1f)
                                       .set(ToolStats.ATTACK_DAMAGE, 3f)
                                       .build();
    assertThat(partialStatsNBT.get(ToolStats.MINING_SPEED)).isEqualTo(ToolStats.MINING_SPEED.getDefaultValue());
    assertThat(partialStatsNBT.get(ToolStats.ARMOR)).isEqualTo(ToolStats.ARMOR.getDefaultValue());
    assertThat(partialStatsNBT.get(ToolStats.HARVEST_TIER)).isEqualTo(ToolStats.HARVEST_TIER.getDefaultValue());
  }
}
