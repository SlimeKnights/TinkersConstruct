package slimeknights.tconstruct.library.materials.stats.dynamic;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.JsonObject;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.dynamic.DynamicStatField.DynamicStat;
import slimeknights.tconstruct.library.materials.stats.dynamic.FloatDynamicStatField.FloatDynamicStat;

/**
 * Loader of {@link DynamicMaterialStats}
 */
public record DynamicMaterialStatLoader(DynamicMaterialStatType type, List<DynamicStatField<?,?>> statFields) implements RecordLoadable<IMaterialStats> {

    @Override
    public IMaterialStats decode(FriendlyByteBuf buffer, TypedMap context) {
        List<DynamicStat<?>> statList = new ArrayList<>();
        List<Component> localizedInfo = new ArrayList<>();
        List<Component> localizedDescriptions = new ArrayList<>();
        int durability = 0;
        for(DynamicStatField<?,?> field : statFields) {
            DynamicStat<?> stat = field.decode(buffer);
            statList.add(stat);
            localizedInfo.add(stat.getLocalizedInfo());
            localizedDescriptions.add(stat.getLocalizedDescription());
            if(field.name().equals(type.getDurabilityField())) {
                durability = (int) ((FloatDynamicStat)stat).value();
            }
        }
        DynamicMaterialStats stats=new DynamicMaterialStats(type, statList, localizedInfo, localizedDescriptions);
        if(type.canRepair())
            return new RepairableDynamicMaterialStats(stats, durability);
        return stats;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void encode(FriendlyByteBuf buffer, IMaterialStats value) {
        DynamicMaterialStats stats = value instanceof RepairableDynamicMaterialStats ? ((RepairableDynamicMaterialStats)value).stats() : (DynamicMaterialStats) value;
        for(DynamicStat stat:stats.stats()) {
            stat.getLoader().encode(buffer, stat);
        }
    }

    @Override
    public IMaterialStats deserialize(JsonObject json, TypedMap context) {
        List<DynamicStat<?>> statList = new ArrayList<>();
        List<Component> localizedInfo = new ArrayList<>();
        List<Component> localizedDescriptions = new ArrayList<>();
        int durability = 0;
        for(DynamicStatField<?,?> field : statFields) {
            DynamicStat<?> stat = field.deserialize(json);
            statList.add(stat);
            localizedInfo.add(stat.getLocalizedInfo());
            localizedDescriptions.add(stat.getLocalizedDescription());
            if(field.name().equals(type.getDurabilityField())) {
                durability = (int) ((FloatDynamicStat)stat).value();
            }
        }
        DynamicMaterialStats stats=new DynamicMaterialStats(type, statList, localizedInfo, localizedDescriptions);
        if(type.canRepair())
            return new RepairableDynamicMaterialStats(stats, durability);
        return stats;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void serialize(IMaterialStats value, JsonObject json) {
        DynamicMaterialStats stats = value instanceof RepairableDynamicMaterialStats ? ((RepairableDynamicMaterialStats)value).stats() : (DynamicMaterialStats) value;
        for(DynamicStat stat:stats.stats()) {
            stat.getLoader().serialize(stat, json);
        }
    }
}
