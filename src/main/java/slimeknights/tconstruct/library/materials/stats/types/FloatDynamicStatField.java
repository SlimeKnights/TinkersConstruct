package slimeknights.tconstruct.library.materials.stats.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.types.DynamicStatField.DynamicStat;
import slimeknights.tconstruct.library.materials.stats.types.FloatDynamicStatField.FloatDynamicStat;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import static slimeknights.tconstruct.library.materials.stats.types.DynamicStatField.withDefaultNamespace;
import static slimeknights.tconstruct.library.materials.stats.IMaterialStats.makeTooltipKey;

@AllArgsConstructor
public class FloatDynamicStatField implements DynamicStatField<FloatDynamicStat> {

    public static final ResourceLocation TYPE = TConstruct.getResource("float");

    private static enum Operator {
        UPDATE,
        ADD,
        PERCENT,
        MULTIPLY,
        MULTIPLY_ALL
    }

    private final String name;
    private final FloatToolStat stat;
    private final float defaultValue;
    private final Operator operator;
    private final String localizedDescription;
    private final String localizedInfoPrefix;

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
        buffer.writeUtf(name);
        buffer.writeUtf(stat.getName().toString());
        buffer.writeFloat(defaultValue);
        buffer.writeEnum(operator);
        buffer.writeUtf(localizedDescription);
        buffer.writeUtf(localizedInfoPrefix);
    }

    @Override
    public void serializeSelf(JsonObject json) {
        json.addProperty("name", name);
        json.addProperty("stat", stat.getName().toString());
        json.addProperty("default_value", defaultValue);
        json.addProperty("operator", operator.toString().toLowerCase());
        json.addProperty("desc", localizedDescription);
        json.addProperty("info", localizedInfoPrefix);
    }

    @Override
    public FloatDynamicStat decode(FriendlyByteBuf buffer) {
        return new FloatDynamicStat(stat, buffer.readFloat(), operator, Component.translatable(localizedDescription), localizedInfoPrefix);
    }

    @Override
    public void encode(FriendlyByteBuf buffer, FloatDynamicStat value) {
        buffer.writeFloat(value.value);
    }

    @Override
    public FloatDynamicStat deserialize(JsonObject json) {
        return new FloatDynamicStat(stat, GsonHelper.getAsFloat(json, name, defaultValue), operator, Component.translatable(localizedDescription), localizedInfoPrefix);
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
        private final Operator operator;
        @Getter
        private final Component localizedDescription;
        private final String localizedInfoPrefix;

        @Override
        public void apply(ModifierStatsBuilder builder, float scale) {
            switch (operator) {
                case UPDATE -> stat.update(builder, value * scale);
                case ADD -> stat.add(builder, value * scale);
                case PERCENT -> stat.percent(builder, value);
                case MULTIPLY -> stat.multiply(builder, value);
                case MULTIPLY_ALL -> stat.multiplyAll(builder, value);
            }
        }

        @Override
        public Component getLocalizedInfo() {
            if (operator != Operator.PERCENT) {
                return stat.formatValue(value);
            }
            return IToolStat.formatColoredPercentBoost(localizedInfoPrefix, value);
        }
    }

    public static class FloatDynamicStatDecoder implements DynamicStatDecoder<FloatDynamicStatField> {

        @Override
        public ResourceLocation getId() {
            return TYPE;
        }

        @Override
        public FloatDynamicStatField deserialize(JsonObject json, ResourceLocation path) {
            String name = json.get("name").getAsString();
            ToolStatId statId = new ToolStatId(withDefaultNamespace(json.get("stat").getAsString()));
            float defaultValue = json.get("default_value").getAsFloat();
            Operator operator = Operator.valueOf(json.get("operator").getAsString().toUpperCase());
            IToolStat<?> stat = ToolStats.getToolStat(statId);
            String localizedDescription = GsonHelper.getAsString(json, "desc",makeTooltipKey(new ResourceLocation(path.getNamespace(), path.getPath()+"."+name+".description")));
            String localizedInfoPrefix = GsonHelper.getAsString(json, "info",makeTooltipKey(new ResourceLocation(path.getNamespace(), name)));
            if (stat != null && stat instanceof FloatToolStat floatStat) {
                return new FloatDynamicStatField(name, floatStat, defaultValue, operator, localizedDescription, localizedInfoPrefix);
            }
            throw new JsonParseException("Could not find float stat: " + statId);
        }

        @Override
        public FloatDynamicStatField decode(FriendlyByteBuf buffer) {
            String name = buffer.readUtf();
            ToolStatId statId = new ToolStatId(buffer.readUtf());
            float defaultValue = buffer.readFloat();
            Operator operator = buffer.readEnum(Operator.class);
            IToolStat<?> stat = ToolStats.getToolStat(statId);
            String localizedDescription = buffer.readUtf();
            String localizedInfoPrefix = buffer.readUtf();
            if (stat != null && stat instanceof FloatToolStat floatStat) {
                return new FloatDynamicStatField(name, floatStat, defaultValue, operator, localizedDescription, localizedInfoPrefix);
            }
            throw new JsonParseException("Could not find float stat: " + statId);
        }
    }
}
