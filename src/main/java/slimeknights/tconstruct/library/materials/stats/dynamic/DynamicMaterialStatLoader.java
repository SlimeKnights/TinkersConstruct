package slimeknights.tconstruct.library.materials.stats.dynamic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.DynamicStat;

/**
 * A record class that represents a dynamic material stat.
 */
public record DynamicMaterialStatLoader(DynamicMaterialStatType type, List<DynamicStatField<?>> statFields) implements RecordLoadable<DynamicMaterialStats> {

    @Override
    public DynamicMaterialStats decode(FriendlyByteBuf buffer, TypedMap context) {
        Map<String, DynamicStat> stats = new LinkedHashMap<>();
        statFields.forEach(field -> stats.put(field.getName(), field.decode(buffer)));
        if (type.canRepair()) {
            return new RepairableDynamicMaterialStats(type, stats, (int)((FloatDynamicStatField.FloatDynamicStat)stats.get(type.getDurabilityField())).getValue());
        }
        return new DynamicMaterialStats(type, stats);
    }

    @Override
    public void encode(FriendlyByteBuf buffer, DynamicMaterialStats value) {
        for (DynamicStatField<?> field : statFields) {
            field.encode(buffer, value);
        }
    }

    @Override
    public DynamicMaterialStats deserialize(JsonObject json, TypedMap context) {
        Map<String, DynamicStat> stats = new LinkedHashMap<>();
        statFields.forEach(field -> stats.put(field.getName(), field.deserialize(json)));
        if (type.canRepair()) {
            return new RepairableDynamicMaterialStats(type, stats, (int)((FloatDynamicStatField.FloatDynamicStat)stats.get(type.getDurabilityField())).getValue());
        }
        return new DynamicMaterialStats(type, stats);
    }

    @Override
    public void serialize(DynamicMaterialStats object, JsonObject json) {
        for (DynamicStatField<?> field : statFields) {
            field.serialize(object, json);
        }
    }
}
