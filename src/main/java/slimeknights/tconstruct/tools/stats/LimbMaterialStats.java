package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;

/** Primary stats for a bow */
@RequiredArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = false)
@ToString
public class LimbMaterialStats extends BaseMaterialStats implements IRepairableMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("limb"));
  public static final LimbMaterialStats DEFAULT = new LimbMaterialStats(1, 1f, 1f, 1f);
  // tooltip descriptions
  private static final List<Component> DESCRIPTION = ImmutableList.of(ToolStats.DURABILITY.getDescription(), ToolStats.DRAW_SPEED.getDescription(), ToolStats.VELOCITY.getDescription(), ToolStats.ATTACK_DAMAGE.getDescription());

  private final int durability;
  private final float drawSpeed;
  private final float velocity;
  private final float meleeAttack;

  public LimbMaterialStats(FriendlyByteBuf buffer) {
    this.durability = buffer.readInt();
    this.drawSpeed = buffer.readFloat();
    this.velocity = buffer.readFloat();
    this.meleeAttack = buffer.readFloat();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(durability);
    buffer.writeFloat(drawSpeed);
    buffer.writeFloat(velocity);
    buffer.writeFloat(meleeAttack);
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<Component> getLocalizedInfo() {
    List<Component> info = Lists.newArrayList();
    info.add(ToolStats.DURABILITY.formatValue(this.durability));
    info.add(ToolStats.DRAW_SPEED.formatValue(this.drawSpeed));
    info.add(ToolStats.VELOCITY.formatValue(this.velocity));
    info.add(ToolStats.ATTACK_DAMAGE.formatValue(this.meleeAttack));
    return info;
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }
}
