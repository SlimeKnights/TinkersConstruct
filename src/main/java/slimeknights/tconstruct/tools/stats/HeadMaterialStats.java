package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.client.renderer.font.CustomFontColor;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.utils.HarvestLevels;

import java.util.List;

public class HeadMaterialStats extends BaseMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(Util.getResource("head"));
  public static final HeadMaterialStats DEFAULT = new HeadMaterialStats(1, 1f, 0, 1f);
  // tooltip prefixes
  public static final String DURABILITY_PREFIX = makeTooltipKey("head.durability");
  private static final String MINING_SPEED_PREFIX = makeTooltipKey("head.mining_speed");
  private static final String ATTACK_PREFIX = makeTooltipKey("head.attack");
  private static final String HARVEST_LEVEL_PREFIX = makeTooltipKey("head.harvest_level");
  // tooltip descriptions
  private static final Text DURABILITY_DESCRIPTION = makeTooltip("head.durability.description");
  private static final Text MINING_SPEED_DESCRIPTION = makeTooltip("head.mining_speed.description");
  private static final Text ATTACK_DESCRIPTION = makeTooltip("head.attack.description");
  private static final Text HARVEST_LEVEL_DESCRIPTION = makeTooltip("head.harvest_level.description");
  private static final List<Text> DESCRIPTION = ImmutableList.of(DURABILITY_DESCRIPTION, HARVEST_LEVEL_DESCRIPTION, MINING_SPEED_DESCRIPTION, ATTACK_DESCRIPTION);
  /** Formateed broken string */
  private static final Text TOOLTIP_BROKEN = Util.makeTranslation("tooltip", "tool.broken").formatted(Formatting.BOLD, Formatting.DARK_RED);
  /** Prefixed broken string */
  private static final Text TOOLTIP_BROKEN_PREFIXED = new TranslatableText(HeadMaterialStats.DURABILITY_PREFIX).append(TOOLTIP_BROKEN);

  public final static TextColor DURABILITY_COLOR = TextColor.fromRgb(0xFF47cc47);
  public final static TextColor MINING_SPEED_COLOR = TextColor.fromRgb(0xFF78A0CD);
  public final static TextColor ATTACK_COLOR = TextColor.fromRgb(0xFFD76464);

  private int durability;
  private float miningSpeed;
  private int harvestLevel;
  private float attack;

    public HeadMaterialStats(int durability, float miningSpeed, int harvestLevel, float attack) {
        this.durability = durability;
        this.miningSpeed = miningSpeed;
        this.harvestLevel = harvestLevel;
        this.attack = attack;
    }

    public HeadMaterialStats() {
    }

    @Override
  public void encode(PacketByteBuf buffer) {
    buffer.writeInt(this.durability);
    buffer.writeFloat(this.miningSpeed);
    buffer.writeInt(this.harvestLevel);
    buffer.writeFloat(this.attack);
  }

  @Override
  public void decode(PacketByteBuf buffer) {
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
  public List<Text> getLocalizedInfo() {
    List<Text> info = Lists.newArrayList();
    info.add(formatDurability(this.durability));
    info.add(formatHarvestLevel(this.harvestLevel));
    info.add(formatMiningSpeed(this.miningSpeed));
    info.add(formatAttack(this.attack));
    return info;
  }

  @Override
  public List<Text> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  /** Applies formatting for durability */
  public static Text formatDurability(int durability) {
    return formatNumber(DURABILITY_PREFIX, DURABILITY_COLOR, durability);
  }

  /** Applies formatting for durability with a reference durability */
  public static Text formatDurability(int durability, int ref, boolean textIfBroken) {
    if (textIfBroken && durability == 0) {
      return TOOLTIP_BROKEN_PREFIXED;
    }
    return new TranslatableText(DURABILITY_PREFIX).append(CustomFontColor.formatPartialAmount(durability, ref));
  }

  /** Applies formatting for harvest level */
  public static Text formatHarvestLevel(int level) {
    return new TranslatableText(HARVEST_LEVEL_PREFIX).append(HarvestLevels.getHarvestLevelName(level));
  }

  /** Applies formatting for mining speed */
  public static Text formatMiningSpeed(float speed) {
    return formatNumber(MINING_SPEED_PREFIX, MINING_SPEED_COLOR, speed);
  }

  /** Applies formatting for attack */
  public static Text formatAttack(float attack) {
    return formatNumber(ATTACK_PREFIX, ATTACK_COLOR, attack);
  }

    public int getDurability() {
        return this.durability;
    }

    public float getMiningSpeed() {
        return this.miningSpeed;
    }

    public int getHarvestLevel() {
        return this.harvestLevel;
    }

    public float getAttack() {
        return this.attack;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof HeadMaterialStats)) return false;
        final HeadMaterialStats other = (HeadMaterialStats) o;
        if (!other.canEqual((Object) this)) return false;
        if (!super.equals(o)) return false;
        if (this.getDurability() != other.getDurability()) return false;
        if (Float.compare(this.getMiningSpeed(), other.getMiningSpeed()) != 0) return false;
        if (this.getHarvestLevel() != other.getHarvestLevel()) return false;
        if (Float.compare(this.getAttack(), other.getAttack()) != 0) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof HeadMaterialStats;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        result = result * PRIME + this.getDurability();
        result = result * PRIME + Float.floatToIntBits(this.getMiningSpeed());
        result = result * PRIME + this.getHarvestLevel();
        result = result * PRIME + Float.floatToIntBits(this.getAttack());
        return result;
    }

    public String toString() {
        return "HeadMaterialStats(durability=" + this.getDurability() + ", miningSpeed=" + this.getMiningSpeed() + ", harvestLevel=" + this.getHarvestLevel() + ", attack=" + this.getAttack() + ")";
    }
}
