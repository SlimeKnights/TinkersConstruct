package slimeknights.tconstruct.library.materials.stats.dynamic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.DynamicStat;
import slimeknights.tconstruct.library.materials.stats.dynamic.TierDynamicStatField.TierDynamicStat;
import slimeknights.tconstruct.library.tools.stat.IToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import slimeknights.tconstruct.library.tools.stat.ToolStatId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.tools.stat.ToolTierStat;

import static slimeknights.tconstruct.library.materials.stats.IMaterialStats.makeTooltipKey;
import static slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.*;

@AllArgsConstructor
public class TierDynamicStatField implements DynamicStatField<TierDynamicStat> {

    public static final ResourceLocation TYPE = TConstruct.getResource("tier");

    private final String name;
    private final ToolTierStat stat;
    private final Tiers defaultValue;
    private final String localizedDescription;

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
        buffer.writeEnum(defaultValue);
        buffer.writeUtf(localizedDescription);
    }

    @Override
    public void serializeSelf(JsonObject json) {
        json.addProperty("type", TYPE.toString());
        json.addProperty("name", name);
        json.addProperty("stat", stat.getName().toString());
        json.addProperty("default_value", defaultValue.toString().toLowerCase());
        json.addProperty("desc", localizedDescription);
    }

    @Override
    public TierDynamicStat decode(FriendlyByteBuf buffer) {
        return new TierDynamicStat(stat, buffer.readEnum(Tiers.class), Component.translatable(localizedDescription));
    }

    @Override
    public void encode(FriendlyByteBuf buffer, TierDynamicStat value) {
        buffer.writeEnum(value.value);
    }

    @Override
    public TierDynamicStat deserialize(JsonObject json) {
        return new TierDynamicStat(stat,
                Tiers.valueOf(GsonHelper.getAsString(json, name, defaultValue.toString()).toUpperCase()),
                Component.translatable(localizedDescription));
    }

    @Override
    public void serialize(TierDynamicStat object, JsonObject json) {
        json.addProperty(name, object.value.toString().toLowerCase());
    }

    @AllArgsConstructor
    public static class TierDynamicStat implements DynamicStat {

        private final ToolTierStat stat;
        @Getter
        private final Tiers value;
        @Getter
        private final Component localizedDescription;

        @Override
        public void apply(ModifierStatsBuilder builder, float scale) {
            stat.update(builder, value);
        }

        @Override
        public Component getLocalizedInfo() {
                return stat.formatValue(value);
        }
    }

    public static class TierDynamicStatDecoder implements DynamicStatDecoder<TierDynamicStatField> {

        @Override
        public ResourceLocation getId() {
            return TYPE;
        }

        @Override
        public TierDynamicStatField deserialize(JsonObject json, ResourceLocation path) {
            String name = GsonHelper.getAsString(json, "name");
            ToolStatId statId = new ToolStatId(withDefaultNamespace(GsonHelper.getAsString(json, "stat")));
            Tiers defaultValue = Tiers.valueOf(GsonHelper.getAsString(json, "default_value").toUpperCase());
            IToolStat<?> stat = ToolStats.getToolStat(statId);
            String localizedDescription = GsonHelper.getAsString(json, "desc", makeTooltipKey( new ResourceLocation(path.getNamespace(), path.getPath()+"."+name+".description")));
            if (stat != null && stat instanceof ToolTierStat tierStat) {
                return new TierDynamicStatField(name, tierStat, defaultValue, localizedDescription);
            }
            throw new JsonParseException("Could not find tier stat: " + statId);
        }

        @Override
        public TierDynamicStatField decode(FriendlyByteBuf buffer) {
            String name = buffer.readUtf();
            ToolStatId statId = new ToolStatId(buffer.readUtf());
            Tiers defaultValue = buffer.readEnum(Tiers.class);
            IToolStat<?> stat = ToolStats.getToolStat(statId);
            String localizedDescription = buffer.readUtf();
            if (stat != null && stat instanceof ToolTierStat tierStat) {
                return new TierDynamicStatField(name, tierStat, defaultValue, localizedDescription);
            }
            throw new JsonParseException("Could not find tier stat: " + statId);
        }
    }
}
