package slimeknights.tconstruct.library.materials.stats.types;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.types.DynamicStatField.DynamicStat;

public record DynamicMaterialStatRecord(MaterialStatType<?> type, List<DynamicStatField<?>> statFields)
        implements RecordLoadable<DynamicMaterialStat> {

    @Override
    public DynamicMaterialStat decode(FriendlyByteBuf buffer, TypedMap context) {
        Map<String, DynamicStat> stats = new LinkedHashMap<>();
        statFields.forEach(field -> stats.put(field.getName(), field.decode(buffer)));
        return new DynamicMaterialStat(type, stats);
    }

    @Override
    public void encode(FriendlyByteBuf buffer, DynamicMaterialStat value) {
        for (DynamicStatField<?> field : statFields) {
            field.encode(buffer, value);
        }
    }

    @Override
    public DynamicMaterialStat deserialize(JsonObject json, TypedMap context) {
        Map<String, DynamicStat> stats = new LinkedHashMap<>();
        statFields.forEach(field -> stats.put(field.getName(), field.deserialize(json)));
        return new DynamicMaterialStat(type, stats);
    }

    @Override
    public void serialize(DynamicMaterialStat object, JsonObject json) {
        for (DynamicStatField<?> field : statFields) {
            field.serialize(object, json);
        }
    }
}
