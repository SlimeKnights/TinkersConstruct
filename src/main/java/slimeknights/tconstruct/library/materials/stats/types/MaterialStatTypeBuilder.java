package slimeknights.tconstruct.library.materials.stats.types;

import java.util.LinkedHashMap;

import com.google.common.base.Supplier;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import dev.gigaherz.jsonthings.things.parsers.ThingParseException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Tiers;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.materials.stats.types.StatTypes.Stat;

public class MaterialStatTypeBuilder {

    protected MaterialStatTypeBuilder(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public static MaterialStatTypeBuilder begin(
            ResourceLocation registryName) {
        return new MaterialStatTypeBuilder(registryName);
    }

    private final ResourceLocation registryName;
    private LinkedHashMap<String, Supplier<Stat<?, ?>>> stats = new LinkedHashMap<>();
    @Setter
    private boolean canRepair = false;
    @Getter
    @Setter
    @Accessors(fluent = true)
    private boolean shouldBuild = true;

    /**
     * Sets the stats of the material stat type.
     *
     * @param rawStats The raw stats JSON object.
     *  should be a map of stat name to stat JSON object.
     *  e.g. {
        "durability": {
            "type": "float",
            "value": 1.0,
            "operator": "update",
            "infoToolTip":"",
            "descToolTip":""
        }
    }
     */
    public void setStats(JsonObject rawStats) {
        try {
            rawStats.asMap().forEach((str, val) -> {
                // TODO: this loads after Common Setup, so we can get instance of ToolStats instead of Resource Location
                var jo = val.getAsJsonObject();
                var type = jo.get("type").getAsString();
                Supplier<Stat<?, ?>> stat = null;
                var regName = this.registryName;
                var descToolTip = GsonHelper.getAsString(jo, "descToolTip",
                        new StringBuilder().append(regName.getPath()).append('.')
                                .append(regName.getNamespace()).append('.')
                                .append(str.replace(':', '.').replace('/', '.')).append(".description")
                                .toString());
                var infoToolTip = GsonHelper.getAsString(jo, "infoToolTip",
                        net.minecraft.Util.makeDescriptionId("tool_stat", regName)
                                + str.replace(':', '.').replace('/', '.'));

                switch (type) {
                    case "float":
                        var value = jo.get("value").getAsFloat();
                        var operator = StatTypes.Operator.valueOf(jo.get("operator").getAsString().toUpperCase());
                        stat = () -> new StatTypes.FloatStat(value, operator, descToolTip, infoToolTip);
                        break;
                    case "tier":
                        var tier = Tiers.valueOf(jo.get("value").getAsString().toUpperCase());
                        stat = () -> new StatTypes.TierStat(tier, descToolTip, infoToolTip);
                        break;
                }

                stats.put(str.indexOf(':') == -1 ? TConstruct.getResource(str).toString() : str, stat);
            });
        } catch (Exception e) {
            throw new JsonSyntaxException("Parse Material Stat Type Fail, " + e.getMessage(), e);
        }
    }

    public FlexMaterialStatType build(ResourceLocation rl) {
        return new FlexMaterialStatType(new MaterialStatsId(rl), this.canRepair, stats);
    }
}
