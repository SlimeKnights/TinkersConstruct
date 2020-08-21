package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class HandleMaterialStats extends BaseMaterialStats {

  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("handle"));
  public static final HandleMaterialStats DEFAULT = new HandleMaterialStats(1f, 0);

  public final static String MULTIPLIER_LOCALIZATION = "stat.handle.modifier.name";
  public final static String DURABILITY_LOCALIZATION = "stat.handle.durability.name";

  public final static String MULTIPLIER_DESCRIPTION_LOCALIZATION = "stat.handle.modifier.description";
  public final static String DURABILITY_DESCRIPTION_LOCALIZATION = "stat.handle.durability.description";

  public final static Color DURABILITY_COLOR = HeadMaterialStats.DURABILITY_COLOR;
  public final static Color MODIFIER_COLOR = Color.func_240743_a_(0xFFB9B95A);

  private float modifier;
  private int durability;

  @Override
  public void encode(PacketBuffer buffer) {
    buffer.writeFloat(this.modifier);
    buffer.writeInt(this.durability);
  }

  @Override
  public void decode(PacketBuffer buffer) {
    this.modifier = buffer.readFloat();
    this.durability = buffer.readInt();
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<ITextComponent> getLocalizedInfo() {
    return ImmutableList.of(formatModifier(this.modifier), formatDurability(this.durability));
  }

  @Override
  public List<ITextComponent> getLocalizedDescriptions() {
    return ImmutableList.of(new TranslationTextComponent(MULTIPLIER_DESCRIPTION_LOCALIZATION), new TranslationTextComponent(DURABILITY_DESCRIPTION_LOCALIZATION));
  }


  public static ITextComponent formatModifier(float quality) {
    return formatNumber(MULTIPLIER_LOCALIZATION, MODIFIER_COLOR, quality);
  }

  public static ITextComponent formatDurability(int durability) {
    return formatNumber(DURABILITY_LOCALIZATION, DURABILITY_COLOR, durability);
  }
}
