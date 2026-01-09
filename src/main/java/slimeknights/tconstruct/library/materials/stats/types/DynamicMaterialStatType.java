package slimeknights.tconstruct.library.materials.stats.types;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

/**
 * A material stat type that has dynamic stat fields.
 */
public class DynamicMaterialStatType extends MaterialStatType<DynamicMaterialStat> {

    private final List<DynamicStatField<?>> statFields;
    private final boolean canRepair;

    /**
     * Constructs a dynamic material stat type.
     * 
     * @param id          The material stats ID.
     * @param canRepair   Whether the tool with this material stat type can be repaired.
     * @param statFields  The dynamic stat fields.
     */
    public DynamicMaterialStatType(MaterialStatsId id, boolean canRepair, List<DynamicStatField<?>> statFields) {
        super(id, new DynamicMaterialStat(null, new LinkedHashMap<>()), new DynamicMaterialStatRecord(null, statFields));
            // I have no way to make all these parameters nonnull.
        this.canRepair = canRepair;
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
        boolean canRepair = buffer.readBoolean();
        List<DynamicStatField<?>> statFields = new java.util.ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            statFields.add(DynamicStatField.decodeSelf(buffer));
        }
        return new DynamicMaterialStatType(id, canRepair, statFields);
    }

    /**
     * Encodes the material stat type to the given byte buffer.
     * 
     * @param buffer The byte buffer to encode to.
     */
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.getId().toString());
        buffer.writeInt(statFields.size());
        buffer.writeBoolean(canRepair);
        statFields.forEach(field -> {
            field.encodeSelf(buffer);
        });
    }

    @Override
    public boolean canRepair() {
        return canRepair;
    }

    @Override
    public DynamicMaterialStat getDefaultStats() {
        Map<String,DynamicStatField.DynamicStat> stats = new LinkedHashMap<>();
        statFields.forEach(field -> {
            stats.put(field.getName(), field.getDefaultStat());
        });
        return new DynamicMaterialStat(this, stats);
    }

    @Override
    public RecordLoadable<DynamicMaterialStat> getLoadable() {
        return new DynamicMaterialStatRecord(this, statFields);
    }
}