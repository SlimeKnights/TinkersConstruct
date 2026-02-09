package slimeknights.tconstruct.library.materials.stats.dynamic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.DynamicStat;
import slimeknights.tconstruct.library.materials.stats.dynamic.FloatDynamicStatField.FloatDynamicStat;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static slimeknights.tconstruct.library.materials.stats.IMaterialStats.makeTooltipKey;
import static slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.*;

@AllArgsConstructor
public class FloatDynamicStatField implements DynamicStatField<FloatDynamicStat> {

    public static final ResourceLocation TYPE = TConstruct.getResource("float");

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

    private final String name;
    private final FloatToolStat stat;
    private final float defaultValue;
    private final Operation operation;
    private final String localizedDescription;
    private final String tooltipKey;

    @Override
    public String getStatType() {
        return TYPE.toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void encodeSelf(FriendlyByteBuf buffer) {
        buffer.writeUtf(TYPE.toString());
        buffer.writeUtf(name);
        buffer.writeUtf(stat.getName().toString());
        buffer.writeFloat(defaultValue);
        buffer.writeEnum(operation);
        buffer.writeUtf(localizedDescription);
        buffer.writeUtf(tooltipKey);
    }

    @Override
    public void serializeSelf(JsonObject json) {
        json.addProperty("type", TYPE.toString());
        json.addProperty("name", name);
        json.addProperty("stat", stat.getName().toString());
        json.addProperty("default_value", defaultValue);
        json.addProperty("operation", operation.toString().toLowerCase());
        json.addProperty("desc", localizedDescription);
        json.addProperty("info", tooltipKey);
    }

    @Override
    public FloatDynamicStat decode(FriendlyByteBuf buffer) {
        return new FloatDynamicStat(stat, buffer.readFloat(), operation, Component.translatable(localizedDescription), tooltipKey);
    }

    @Override
    public void encode(FriendlyByteBuf buffer, FloatDynamicStat value) {
        buffer.writeFloat(value.value);
    }

    @Override
    public FloatDynamicStat deserialize(JsonObject json) {
        return new FloatDynamicStat(stat, GsonHelper.getAsFloat(json, name, defaultValue), operation, Component.translatable(localizedDescription), tooltipKey);
    }

    @Override
    public void serialize(FloatDynamicStat object, JsonObject json) {
        json.addProperty(name, object.value);
    }

    @AllArgsConstructor
    public static class FloatDynamicStat implements DynamicStat {

        private final FloatToolStat stat;
        @Getter
        private final float value;
        private final Operation operation;
        @Getter
        private final Component localizedDescription;
        private final String tooltipKey;

        @Override
        public void apply(ModifierStatsBuilder builder, float scale) {
            switch (operation) {
                case UPDATE -> stat.update(builder, value * scale);
                case ADD -> stat.add(builder, value * scale);
                case PERCENT -> stat.percent(builder, value);
                case MULTIPLY -> stat.multiply(builder, value);
                case MULTIPLY_ALL -> stat.multiplyAll(builder, value);
            }
        }

        @Override
        public Component getLocalizedInfo() {
            if(tooltipKey.isEmpty())
                return stat.formatValue(value);
            if (operation == Operation.PERCENT) {
                return IToolStat.formatColoredPercentBoost(tooltipKey, value);
            }
            return IToolStat.formatNumber(tooltipKey, stat.getColor(), value);
        }
    }

    public static class FloatDynamicStatDecoder implements DynamicStatDecoder<FloatDynamicStatField> {

        @Override
        public ResourceLocation getId() {
            return TYPE;
        }

        @Override
        public FloatDynamicStatField deserialize(JsonObject json, ResourceLocation path) {
            String name = GsonHelper.getAsString(json, "name");
            ToolStatId statId = new ToolStatId(withDefaultNamespace(GsonHelper.getAsString(json, "stat")));
            float defaultValue = GsonHelper.getAsFloat(json, "default_value", 0.0f);
            Operation operation = Operation.valueOf(GsonHelper.getAsString(json, "operation").toUpperCase());
            IToolStat<?> stat = ToolStats.getToolStat(statId);
            String localizedDescription = GsonHelper.getAsString(json, "description",makeTooltipKey(new ResourceLocation(path.getNamespace(), path.getPath()+"."+name+".description")));
            String tooltipKey = GsonHelper.getAsString(json, "tooltip","");
            if (stat != null && stat instanceof FloatToolStat floatStat) {
                return new FloatDynamicStatField(name, floatStat, defaultValue, operation, localizedDescription, tooltipKey);
            }
            throw new JsonParseException("Could not find float stat: " + statId);
        }

        @Override
        public FloatDynamicStatField decode(FriendlyByteBuf buffer) {
            String name = buffer.readUtf();
            ToolStatId statId = new ToolStatId(buffer.readUtf());
            float defaultValue = buffer.readFloat();
            Operation operation = buffer.readEnum(Operation.class);
            IToolStat<?> stat = ToolStats.getToolStat(statId);
            String localizedDescription = buffer.readUtf();
            String tooltipKey = buffer.readUtf();
            if (stat != null && stat instanceof FloatToolStat floatStat) {
                return new FloatDynamicStatField(name, floatStat, defaultValue, operation, localizedDescription, tooltipKey);
            }
            throw new JsonParseException("Could not find float stat: " + statId);
        }
    }
}
