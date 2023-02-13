package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.With;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

import static slimeknights.tconstruct.tools.stats.LimbMaterialStats.ACCURACY_PREFIX;

/** Secondary stats for a bow */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
@With
public class GripMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("grip"));
  public static final GripMaterialStats DEFAULT = new GripMaterialStats(1f, 0f, 0f);

  // tooltip prefixes
  private static final String DURABILITY_PREFIX = makeTooltipKey(TConstruct.getResource("durability"));
  // description
  private static final List<Component> DESCRIPTION = ImmutableList.of(
    makeTooltip(TConstruct.getResource("handle.durability.description")),
    ToolStats.ACCURACY.getDescription(),
    ToolStats.ATTACK_DAMAGE.getDescription());

  private final float durability;
  private final float accuracy;
  private final float meleeAttack;

  public GripMaterialStats(FriendlyByteBuf buffer) {
    this.durability = buffer.readFloat();
    this.accuracy = buffer.readFloat();
    this.meleeAttack = buffer.readFloat();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeFloat(durability);
    buffer.writeFloat(accuracy);
    buffer.writeFloat(meleeAttack);
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<Component> getLocalizedInfo() {
    List<Component> info = Lists.newArrayList();
    info.add(IToolStat.formatColoredMultiplier(DURABILITY_PREFIX, this.durability));
    info.add(IToolStat.formatColoredBonus(ACCURACY_PREFIX, this.accuracy, 0.5f));
    info.add(ToolStats.ATTACK_DAMAGE.formatValue(this.meleeAttack));
    return info;
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }
}
