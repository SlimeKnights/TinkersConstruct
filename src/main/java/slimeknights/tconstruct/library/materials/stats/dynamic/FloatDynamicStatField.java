package slimeknights.tconstruct.library.materials.stats.dynamic;

import static slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.withDefaultNamespace;

import javax.annotation.Nonnull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.primitive.FloatLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public record FloatDynamicStatField(
        String name, String localizedDescription, String tooltipKey, String toolStat, float defaultValue, Operation operation) implements DynamicStatField<FloatDynamicStatField.FloatDynamicStat, FloatToolStat> {
            
    public static final ResourceLocation TYPE = TConstruct.getResource("float");
    public static final RecordLoadable<FloatDynamicStatField> LOADER = RecordLoadable.create(
            NAME_FIELD,
            DESC_FIELD,
            TOOLTIP_FIELD,
            TOOL_STAT_FIELD,
            FloatLoadable.ANY.requiredField(DynamicStatField.DEFAULT_VALUE, FloatDynamicStatField::defaultValue),
            EnumLoadable.of(Operation.UPDATE, Operation.ADD, Operation.PERCENT, Operation.MULTIPLY, Operation.MULTIPLY_ALL).requiredField("operation", FloatDynamicStatField::operation),
            FloatDynamicStatField::new);

    public FloatDynamicStatField(String name, String localizedDescription, String tooltipKey, String toolStat, float defaultValue, Operation operation) {
            this.name = name;
            this.localizedDescription = localizedDescription;
            this.tooltipKey = tooltipKey;
            this.toolStat = toolStat;
            this.defaultValue = defaultValue;
            this.operation = operation;
        if(getToolStat() == null) {
            throw new JsonParseException("Cannot Find Float Tool Stat: " + toolStat);
        }
    }

    public static record FloatDynamicStat(float value, FloatToolStat toolStat, Operation operation,
            FloatDynamicStatField loader) implements DynamicStatField.DynamicStat<FloatDynamicStat> {

        @Override
        public RecordLoadable<FloatDynamicStat> getLoader() {
            return loader;
        }

        @Override
        public void apply(ModifierStatsBuilder builder, float scale) {
            switch (operation) {
                case UPDATE -> toolStat.update(builder, value * scale);
                case ADD -> toolStat.add(builder, value * scale);
                case PERCENT -> toolStat.percent(builder, value * scale);
                case MULTIPLY -> toolStat.multiply(builder, value * scale);
                case MULTIPLY_ALL -> toolStat.multiplyAll(builder, value * scale);
            }
        }

        @Override
        public Component getLocalizedInfo(float scale) {
            return loader.getLocalizedInfo(scale, this);
        }

        @Override
        public Component getLocalizedDescription() {
            return loader.getLocalizedDescription();
        }
    }

    /**
     * Operation to perform on the stat.
     */
    public static enum Operation {
        UPDATE,
        ADD,
        PERCENT,
        MULTIPLY,
        MULTIPLY_ALL
    }

    @Override
    public FloatDynamicStat deserialize(JsonObject json, TypedMap context) {
        float value = GsonHelper.getAsFloat(json, name, defaultValue);
        return new FloatDynamicStat(value, getToolStat(), operation, this);
    }

    @Override
    public void serialize(FloatDynamicStat object, JsonObject json) {
        json.addProperty(name, object.value);
    }

    @Override
    public FloatDynamicStat decode(FriendlyByteBuf buffer, TypedMap context) {
        float value = buffer.readFloat();
        return new FloatDynamicStat(value, getToolStat(), operation, this);
    }

    @Override
    public void encode(FriendlyByteBuf buffer, FloatDynamicStat value) {
        buffer.writeFloat(value.value);
    }

    @Override
    public RecordLoadable<? extends IHaveLoader> getLoader() {
        return LOADER;
    }

    @Override
    public Component getLocalizedInfo(float scale, FloatDynamicStat value) {
        return tooltipKey=="" ? getToolStat().formatValue(value.value * scale):
            switch (operation) {
                case UPDATE -> IToolStat.formatNumber(tooltipKey, getToolStat().getColor(), value.value * scale);
                case PERCENT -> IToolStat.formatColoredPercentBoost(tooltipKey, value.value * scale);
                case ADD -> IToolStat.formatColoredBonus(tooltipKey, value.value * scale);
                case MULTIPLY, MULTIPLY_ALL -> IToolStat.formatColoredMultiplier(tooltipKey, value.value * scale);
            };
    }

    @Override
    @Nonnull
    public FloatToolStat getToolStat() {
        return (FloatToolStat) ToolStats.getToolStat(withDefaultNamespace(toolStat));
    }
}
