package slimeknights.tconstruct.library.materials.stats.types;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Tier;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.tools.stat.ToolTierStat;

public class StatTypes {
    public enum Operator {
        UPDATE, ADD, PERCENT, MULTIPLY, MULTIPLY_ALL;

        public static byte getIndex(Operator operator) {
            switch (operator) {
                case UPDATE:
                    return 0;
                case ADD:
                    return 1;
                case PERCENT:
                    return 2;
                case MULTIPLY:
                    return 3;
                case MULTIPLY_ALL:
                    return 4;
            }
            return -1;
        }
        public static Operator fromIndex(byte index) {
            switch (index) {
                case 0:
                    return UPDATE;
                case 1:
                    return ADD;
                case 2:
                    return PERCENT;
                case 3:
                    return MULTIPLY;
                case 4:
                    return MULTIPLY_ALL;
            }
            throw new IndexOutOfBoundsException("No operator with index " + index);
        }
    }

    public static abstract class Stat<T, S extends IToolStat<T>> {
        public T value;

        public final Operator operator;

        public T getValue() {
            return value;
        }

        public abstract String getStatType();

        public abstract void setValue(Object value);

        public abstract void apply(ModifierStatsBuilder builder, S stat, float scale);

        public S getToolStatOrThrow(ToolStatId statName) {
            var stat = ToolStats.getToolStat(statName);
            if (stat == null)
                throw new NoSuchToolStatException(statName);
            try {
                return (S) stat;
            } catch (ClassCastException e) {
                throw new ToolStatTypeNotMatchException(statName, getStatType());
            }
        }

        @SuppressWarnings("unchecked")
        public void apply(ModifierStatsBuilder builder, ToolStatId statName, float scale) {
            apply(builder, getToolStatOrThrow(statName), scale);
        }

        public void apply(ModifierStatsBuilder builder, String statName, float scale) {
            apply(builder, new ToolStatId(statName), scale);
        }

        public String infoToolTip;
        public String descToolTip;

        public Component format(S stat) {
            return stat.formatValue(value);
        }

        public Component format(ToolStatId statName) {
            return format(getToolStatOrThrow(statName));
        }

        public Component format(String statName) {
            return format(new ToolStatId(statName));
        }

        public Stat(T value, Operator operator, String descToolTip, String infoToolTip) {
            this.value = value;
            this.operator = operator;
            this.descToolTip = descToolTip;
            this.infoToolTip = infoToolTip;
        }

        public abstract RecordField<T, FlexMaterialStat> getLoadable(String statName);

    }

    public static abstract class NumericStat<T extends Number, S extends INumericToolStat<T>> extends Stat<T, S> {

        public NumericStat(T value, Operator operator, String descToolTip, String infoToolTip) {
            super(value, operator, descToolTip, infoToolTip);
        }
    }

    public static class TierStat extends Stat<Tier, ToolTierStat> {
        public TierStat(Tier value, String descToolTip, String infoToolTip) {
            super(value, Operator.UPDATE, descToolTip, infoToolTip);
        }

        @Override
        public RecordField<Tier, FlexMaterialStat> getLoadable(String statName) {
            return TinkerLoadables.TIER.defaultField(statName, value, true,
                    (FlexMaterialStat flex) -> {
                        return (Tier) flex.get(statName);
                    });
        }

        @Override
        public void setValue(Object value) {
            if (value instanceof Tier)
                this.value = (Tier) value;
            else
                throw new IllegalArgumentException("StatTypes.TierStat: value must be an Tier");
        }

        @Override
        public void apply(ModifierStatsBuilder builder, ToolTierStat tier, float scale) {
            tier.update(builder, value);
        }

        @Override
        public String getStatType() {
            return "Tier";
        }
    }

    public static class FloatStat extends NumericStat<Float, FloatToolStat> {
        public FloatStat(Float value, Operator operator, String descToolTip, String infoToolTip) {
            super(value, operator, descToolTip, infoToolTip);
        }

        public String prefix = "";

        @Override
        public RecordField<Float, FlexMaterialStat> getLoadable(String statName) {
            return FloatLoadable.ANY.defaultField(statName, value, true,
                    (FlexMaterialStat flex) -> {
                        return (Float) flex.get(statName);
                    });
        }

        @Override
        public void setValue(Object value) {
            if (value instanceof Float)
                this.value = (Float) value;
            else
                throw new IllegalArgumentException("StatTypes.FloatStat: value must be a Float");
        }

        @Override
        public void apply(ModifierStatsBuilder builder, FloatToolStat flt, float scale) {
            switch (operator) {
                case UPDATE -> flt.update(builder, value.floatValue() * scale);
                case ADD -> flt.add(builder, value.doubleValue() * scale);
                case PERCENT -> flt.percent(builder, value.doubleValue() * scale);
                case MULTIPLY -> flt.multiply(builder, value.doubleValue() * scale);
                case MULTIPLY_ALL -> flt.multiplyAll(builder, value.doubleValue() * scale);
            }
        }

        @Override
        public Component format(FloatToolStat stat) {
            if (operator == Operator.PERCENT)
                return IToolStat.formatColoredPercentBoost(infoToolTip, value);
            return stat.formatValue(value);
        }

        @Override
        public String getStatType() {
            return "Float";
        }
    }

}
