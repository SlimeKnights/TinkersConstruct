package slimeknights.tconstruct.tools.stats;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tools.modules.ArmorModuleBuilder;
import slimeknights.tconstruct.tools.modules.ArmorModuleBuilder.ArmorShieldModuleBuilder;

import java.util.List;

/** Material stat class handling all four plating types */
public record PlatingMaterialStats(MaterialStatType<?> getType, int durability, float armor, float toughness, float knockbackResistance) implements IRepairableMaterialStats {
  private static final LoadableField<Float,PlatingMaterialStats> TOUGHNESS = FloatLoadable.FROM_ZERO.defaultField("toughness", 0f, PlatingMaterialStats::toughness);
  private static final LoadableField<Float,PlatingMaterialStats> KNOCKBACK_RESISTANCE = FloatLoadable.FROM_ZERO.defaultField("knockback_resistance", 0f, PlatingMaterialStats::knockbackResistance);
  private static final RecordLoadable<PlatingMaterialStats> LOADABLE = RecordLoadable.create(
    MaterialStatType.CONTEXT_KEY.requiredField(),
    IRepairableMaterialStats.DURABILITY_FIELD,
    FloatLoadable.FROM_ZERO.defaultField("armor", 0f, true, PlatingMaterialStats::armor),
    TOUGHNESS,
    KNOCKBACK_RESISTANCE,
    PlatingMaterialStats::new);
  private static final List<Component> DESCRIPTION = List.of(
    ToolStats.DURABILITY.getDescription(),
    ToolStats.ARMOR.getDescription(),
    ToolStats.ARMOR_TOUGHNESS.getDescription(),
    ToolStats.KNOCKBACK_RESISTANCE.getDescription());
  private static final List<Component> SHIELD_DESCRIPTION = List.of(
    ToolStats.DURABILITY.getDescription(),
    ToolStats.ARMOR_TOUGHNESS.getDescription(),
    ToolStats.KNOCKBACK_RESISTANCE.getDescription());
  /* Types */
  public static final MaterialStatType<PlatingMaterialStats> HELMET = makeType("plating_helmet");
  public static final MaterialStatType<PlatingMaterialStats> CHESTPLATE = makeType("plating_chestplate");
  public static final MaterialStatType<PlatingMaterialStats> LEGGINGS = makeType("plating_leggings");
  public static final MaterialStatType<PlatingMaterialStats> BOOTS = makeType("plating_boots");
  /** Shield loadable does not support armor */
  public static final MaterialStatType<PlatingMaterialStats> SHIELD = new MaterialStatType<PlatingMaterialStats>(new MaterialStatsId(TConstruct.MOD_ID, "plating_shield"), type -> new PlatingMaterialStats(type, 1, 0, 0, 0), RecordLoadable.create(
    MaterialStatType.CONTEXT_KEY.requiredField(), IRepairableMaterialStats.DURABILITY_FIELD, TOUGHNESS, KNOCKBACK_RESISTANCE,
    (type, durability, toughness, knockbackResistance) -> new PlatingMaterialStats(type, durability, 0, toughness, knockbackResistance)));
  /** All types including shield */
  public static final List<MaterialStatType<PlatingMaterialStats>> TYPES = List.of(HELMET, CHESTPLATE, LEGGINGS, BOOTS, SHIELD);

  @Override
  public List<Component> getLocalizedInfo() {
    Component durability = ToolStats.DURABILITY.formatValue(this.durability);
    Component toughness = ToolStats.ARMOR_TOUGHNESS.formatValue(this.toughness);
    Component knockbackResistance = ToolStats.KNOCKBACK_RESISTANCE.formatValue(this.knockbackResistance * 10); // multiply by 10 as vanilla multiplies toughness by 10 for display
    if (getType == SHIELD) {
      return List.of(durability, toughness, knockbackResistance);
    }
    return List.of(durability, ToolStats.ARMOR.formatValue(this.armor), toughness, knockbackResistance);
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return getType == SHIELD ? SHIELD_DESCRIPTION : DESCRIPTION;
  }

  @Override
  public void apply(ModifierStatsBuilder builder, float scale) {
    ToolStats.DURABILITY.update(builder, durability * scale);
    ToolStats.ARMOR.update(builder, armor * scale);
    ToolStats.ARMOR_TOUGHNESS.update(builder, toughness * scale);
    ToolStats.KNOCKBACK_RESISTANCE.update(builder, knockbackResistance * scale);
  }

  /** Makes a stat type for the given name */
  private static MaterialStatType<PlatingMaterialStats> makeType(String name) {
    return new MaterialStatType<PlatingMaterialStats>(new MaterialStatsId(TConstruct.MOD_ID, name), type -> new PlatingMaterialStats(type, 1, 0, 0, 0), LOADABLE);
  }


  public static Builder builder() {
    return new Builder();
  }

  /** Builder to create plating material stats for all four pieces */
  @Setter
  @Accessors(fluent = true)
  public static class Builder implements ArmorShieldModuleBuilder<PlatingMaterialStats> {
    private final int[] durability = new int[4];
    private int shieldDurability = 0;
    private final float[] armor = new float[4];
    private float toughness = 0;
    private float knockbackResistance = 0;

    private Builder() {}

    /** Sets the durability for the piece based on the given factor */
    public Builder durabilityFactor(float maxDamageFactor) {
      for (ArmorItem.Type slotType : ArmorItem.Type.values()) {
        int index = slotType.ordinal();
        durability[index] = (int)(ArmorModuleBuilder.MAX_DAMAGE_ARRAY[index] * maxDamageFactor);
      }
      if (shieldDurability == 0) {
        shieldDurability = (int)(maxDamageFactor * 18);
      }
      return this;
    }

    /** Sets the armor value for each piece */
    public Builder armor(float boots, float leggings, float chestplate, float helmet) {
      armor[ArmorItem.Type.BOOTS.ordinal()] = boots;
      armor[ArmorItem.Type.LEGGINGS.ordinal()] = leggings;
      armor[ArmorItem.Type.CHESTPLATE.ordinal()] = chestplate;
      armor[ArmorItem.Type.HELMET.ordinal()] = helmet;
      return this;
    }

    @Override
    public PlatingMaterialStats build(ArmorItem.Type slot) {
      int index = slot.ordinal();
      return new PlatingMaterialStats(TYPES.get(index), durability[index], armor[index], toughness, knockbackResistance);
    }

    @Override
    public PlatingMaterialStats buildShield() {
      return new PlatingMaterialStats(PlatingMaterialStats.SHIELD, shieldDurability, 0, toughness, knockbackResistance);
    }
  }
}
