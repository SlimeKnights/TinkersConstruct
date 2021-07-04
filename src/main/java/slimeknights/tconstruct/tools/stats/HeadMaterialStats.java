package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class HeadMaterialStats extends BaseMaterialStats implements IRepairableMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("head"));
  public static final HeadMaterialStats DEFAULT = new HeadMaterialStats(1, 1f, 0, 1f);
  // tooltip descriptions
  private static final List<ITextComponent> DESCRIPTION = ImmutableList.of(ToolStats.DURABILITY.getDescription(), ToolStats.HARVEST_LEVEL.getDescription(), ToolStats.MINING_SPEED.getDescription(), ToolStats.ATTACK_DAMAGE.getDescription());

  public final static Color DURABILITY_COLOR = Color.fromInt(0xFF47cc47);
  public final static Color MINING_SPEED_COLOR = Color.fromInt(0xFF78A0CD);
  public final static Color ATTACK_COLOR = Color.fromInt(0xFFD76464);

  private int durability;
  private float miningSpeed;
  private int harvestLevel;
  private float attack;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeInt(this.durability);
    buffer.writeFloat(this.miningSpeed);
    buffer.writeInt(this.harvestLevel);
    buffer.writeFloat(this.attack);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    this.durability = buffer.readInt();
    this.miningSpeed = buffer.readFloat();
    this.harvestLevel = buffer.readInt();
    this.attack = buffer.readFloat();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<ITextComponent> getLocalizedInfo() {
    List<ITextComponent> info = Lists.newArrayList();
    info.add(ToolStats.DURABILITY.formatValue(this.durability));
    info.add(ToolStats.HARVEST_LEVEL.formatValue(this.harvestLevel));
    info.add(ToolStats.MINING_SPEED.formatValue(this.miningSpeed));
    info.add(ToolStats.ATTACK_DAMAGE.formatValue(this.attack));
    return info;
  }

  @Override
  public List<ITextComponent> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

}
