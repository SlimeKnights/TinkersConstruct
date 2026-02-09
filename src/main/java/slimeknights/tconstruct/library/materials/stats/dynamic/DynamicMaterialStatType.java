package slimeknights.tconstruct.library.materials.stats.dynamic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

/**
 * A material stat type that has dynamic stat fields.
 */
public class DynamicMaterialStatType extends MaterialStatType<DynamicMaterialStats> {

    private final List<DynamicStatField<?>> statFields;
    @Getter
    private final String durabilityField;

    /**
     * Constructs a dynamic material stat type.
     * 
     * @param id          The material stats ID.
     * @param durabilityField  The name of the repair amount field.
     * @param statFields  The dynamic stat fields.
     */
    public DynamicMaterialStatType(MaterialStatsId id, String durabilityField, List<DynamicStatField<?>> statFields) {
        super(id, (type) -> new DynamicMaterialStats(type, statFields.stream().collect(java.util.stream.Collectors.toMap(DynamicStatField::getName, field -> field.getDefaultStat()))), new DynamicMaterialStatLoader(null, statFields));
        this.durabilityField = durabilityField;
        this.statFields = statFields;
    }

    /**
     * Decodes the material stat type from the given byte buffer.
     * 
     * @param buffer The byte buffer to decode from.
     * @return The decoded material stat type.
     */
    public static DynamicMaterialStatType decode(FriendlyByteBuf buffer) {
        MaterialStatsId id = new MaterialStatsId(buffer.readUtf());
        int size = buffer.readInt();
        String durabilityField = buffer.readUtf();
        List<DynamicStatField<?>> statFields = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            statFields.add(DynamicStatField.decodeSelf(buffer));
        }
        return new DynamicMaterialStatType(id, durabilityField, statFields);
    }

    /**
     * Encodes the material stat type to the given byte buffer.
     * 
     * @param buffer The byte buffer to encode to.
     */
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.getId().toString());
        buffer.writeInt(statFields.size());
        buffer.writeUtf(durabilityField);
        statFields.forEach(field -> {
            field.encodeSelf(buffer);
        });
    }

    @Override
    public boolean canRepair() {
        return !durabilityField.isEmpty();
    }

    @Override
    public DynamicMaterialStats getDefaultStats() {
        Map<String,DynamicStatField.DynamicStat> stats = new LinkedHashMap<>();
        statFields.forEach(field -> {
            stats.put(field.getName(), field.getDefaultStat());
        });
        if (canRepair()) {
            return new RepairableDynamicMaterialStats(this, stats, (int)((FloatDynamicStatField.FloatDynamicStat)stats.get(durabilityField)).getValue());
        }
        return new DynamicMaterialStats(this, stats);
    }

    @Override
    public RecordLoadable<DynamicMaterialStats> getLoadable() {
        return new DynamicMaterialStatLoader(this, statFields);
    }
}