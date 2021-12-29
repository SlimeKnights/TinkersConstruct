package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@With
public class HandleMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("handle"));
  public static final HandleMaterialStats DEFAULT = new HandleMaterialStats(1f, 1f, 1f, 1f);
  // tooltip prefixes
  private static final String DURABILITY_PREFIX = makeTooltipKey(TConstruct.getResource("durability"));
  private static final String ATTACK_DAMAGE_PREFIX = makeTooltipKey(TConstruct.getResource("attack_damage"));
  private static final String ATTACK_SPEED_PREFIX = makeTooltipKey(TConstruct.getResource("attack_speed"));
  private static final String MINING_SPEED_PREFIX = makeTooltipKey(TConstruct.getResource("mining_speed"));
  // tooltip descriptions
  private static final Component DURABILITY_DESCRIPTION = makeTooltip(TConstruct.getResource("handle.durability.description"));
  private static final Component ATTACK_DAMAGE_DESCRIPTION = makeTooltip(TConstruct.getResource("handle.attack_damage.description"));
  private static final Component ATTACK_SPEED_DESCRIPTION = makeTooltip(TConstruct.getResource("handle.attack_speed.description"));
  private static final Component MINING_SPEED_DESCRIPTION = makeTooltip(TConstruct.getResource("handle.mining_speed.description"));
  private static final List<Component> DESCRIPTION = ImmutableList.of(DURABILITY_DESCRIPTION, ATTACK_DAMAGE_DESCRIPTION, ATTACK_SPEED_DESCRIPTION, MINING_SPEED_DESCRIPTION);

  // multipliers
  private float durability;
  private float miningSpeed;
  private float attackSpeed;
  private float attackDamage;

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeFloat(this.durability);
    buffer.writeFloat(this.attackDamage);
    buffer.writeFloat(this.attackSpeed);
    buffer.writeFloat(this.miningSpeed);
  }

  @Override
  public void decode(FriendlyByteBuf buffer) {
    this.durability = buffer.readFloat();
    this.attackDamage = buffer.readFloat();
    this.attackSpeed = buffer.readFloat();
    this.miningSpeed = buffer.readFloat();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<Component> getLocalizedInfo() {
    List<Component> list = new ArrayList<>();
    list.add(formatDurability(this.durability));
    list.add(formatAttackDamage(this.attackDamage));
    list.add(formatAttackSpeed(this.attackSpeed));
    list.add(formatMiningSpeed(this.miningSpeed));
    return list;
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  /** Applies formatting for durability */
  public static Component formatDurability(float quality) {
    return IToolStat.formatColoredMultiplier(DURABILITY_PREFIX, quality);
  }

  /** Applies formatting for attack speed */
  public static Component formatAttackDamage(float quality) {
    return IToolStat.formatColoredMultiplier(ATTACK_DAMAGE_PREFIX, quality);
  }

  /** Applies formatting for attack speed */
  public static Component formatAttackSpeed(float quality) {
    return IToolStat.formatColoredMultiplier(ATTACK_SPEED_PREFIX, quality);
  }

  /** Applies formatting for mining speed */
  public static Component formatMiningSpeed(float quality) {
    return IToolStat.formatColoredMultiplier(MINING_SPEED_PREFIX, quality);
  }
}
