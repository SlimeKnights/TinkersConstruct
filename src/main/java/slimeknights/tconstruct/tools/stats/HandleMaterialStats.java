package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
@With
public class HandleMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("handle"));
  public static final HandleMaterialStats DEFAULT = new HandleMaterialStats(1f, 1f, 1f, 1f);
  // tooltip prefixes
  private static final String DURABILITY_PREFIX = makeTooltipKey("handle.durability");
  private static final String ATTACK_DAMAGE_PREFIX = makeTooltipKey("handle.attack_damage");
  private static final String ATTACK_SPEED_PREFIX = makeTooltipKey("handle.attack_speed");
  private static final String MINING_SPEED_PREFIX = makeTooltipKey("handle.mining_speed");
  // tooltip descriptions
  private static final ITextComponent DURABILITY_DESCRIPTION = makeTooltip("handle.durability.description");
  private static final ITextComponent ATTACK_DAMAGE_DESCRIPTION = makeTooltip("handle.attack_damage.description");
  private static final ITextComponent ATTACK_SPEED_DESCRIPTION = makeTooltip("handle.attack_speed.description");
  private static final ITextComponent MINING_SPEED_DESCRIPTION = makeTooltip("handle.mining_speed.description");
  private static final List<ITextComponent> DESCRIPTION = ImmutableList.of(DURABILITY_DESCRIPTION, ATTACK_DAMAGE_DESCRIPTION, ATTACK_SPEED_DESCRIPTION, MINING_SPEED_DESCRIPTION);

  // multipliers
  private float durability;
  private float miningSpeed;
  private float attackSpeed;
  private float attackDamage;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeFloat(this.durability);
    buffer.writeFloat(this.attackDamage);
    buffer.writeFloat(this.attackSpeed);
    buffer.writeFloat(this.miningSpeed);
  }

  @Override
  public void decode(PacketBuffer buffer) {
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
  public List<ITextComponent> getLocalizedInfo() {
    List<ITextComponent> list = new ArrayList<>();
    list.add(formatDurability(this.durability));
    list.add(formatAttackDamage(this.attackDamage));
    list.add(formatAttackSpeed(this.attackSpeed));
    list.add(formatMiningSpeed(this.miningSpeed));
    return list;
  }

  @Override
  public List<ITextComponent> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  /** Applies formatting for durability */
  public static ITextComponent formatDurability(float quality) {
    return formatColoredMultiplier(DURABILITY_PREFIX, quality);
  }

  /** Applies formatting for attack speed */
  public static ITextComponent formatAttackDamage(float quality) {
    return formatColoredMultiplier(ATTACK_DAMAGE_PREFIX, quality);
  }

  /** Applies formatting for attack speed */
  public static ITextComponent formatAttackSpeed(float quality) {
    return formatColoredMultiplier(ATTACK_SPEED_PREFIX, quality);
  }

  /** Applies formatting for mining speed */
  public static ITextComponent formatMiningSpeed(float quality) {
    return formatColoredMultiplier(MINING_SPEED_PREFIX, quality);
  }
}
