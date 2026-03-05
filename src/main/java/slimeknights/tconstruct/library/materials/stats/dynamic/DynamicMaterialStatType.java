package slimeknights.tconstruct.library.materials.stats.dynamic;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;

/**
 * A material stat type that has dynamic stat fields.
 */
@Log4j2
public class DynamicMaterialStatType extends MaterialStatType<IMaterialStats> implements IHaveLoader {

    public static final DynamicMaterialStatTypeLoader LOADER = new DynamicMaterialStatTypeLoader();

    @Nonnull
    private final List<DynamicStatField<?,?>> statFields;
    @Getter
    private final String durabilityField;

    /**
     * Constructs a dynamic material stat type.
     * 
     * @param id          The material stats ID.
     * @param durabilityField  The name of the repair amount field.
     * @param statFields  The dynamic stat fields.
     */
    @SuppressWarnings("null")
    public DynamicMaterialStatType(MaterialStatsId id, String durabilityField, List<DynamicStatField<?,?>> statFields) {
        super(id,(IMaterialStats)null,null); // We cannot get the default stats before the stat type is loaded
        this.durabilityField = durabilityField;
        this.statFields = statFields;
    }

    @Override
    public boolean canRepair() {
        return !durabilityField.isEmpty();
    }

    @Override
    public IMaterialStats getDefaultStats() {
        return this.getLoadable().deserialize(new JsonObject());
    }

    @Override
    public RecordLoadable<IMaterialStats> getLoadable() {
        return new DynamicMaterialStatLoader(this, statFields);
    }

    @Override
    public DynamicMaterialStatTypeLoader getLoader() {
        return LOADER;
    }

    public static class DynamicMaterialStatTypeLoader implements RecordLoadable<DynamicMaterialStatType> {

        @Override
        public DynamicMaterialStatType decode(FriendlyByteBuf buffer, TypedMap context) {
            MaterialStatsId id = new MaterialStatsId(buffer.readResourceLocation());
            String durabilityField = buffer.readUtf();
            int statFieldsSize = buffer.readVarInt();
            List<DynamicStatField<?,?>> statFields = new ArrayList<>();
            for (int i = 0; i < statFieldsSize; i++) {
                statFields.add(DynamicStatField.REGISTRY.decode(buffer,context));
            }
            return new DynamicMaterialStatType(id, durabilityField, statFields);
        }

        @Override
        public void encode(FriendlyByteBuf buffer, DynamicMaterialStatType value) {
            buffer.writeResourceLocation(value.getId());
            buffer.writeUtf(value.durabilityField);
            buffer.writeVarInt(value.statFields.size());
            for (DynamicStatField<?,?> statField : value.statFields) {
                DynamicStatField.REGISTRY.encode(buffer, statField);
            }
        }

        @Override
        public DynamicMaterialStatType deserialize(JsonObject json, TypedMap context) {
            MaterialStatsId id = new MaterialStatsId(GsonHelper.getAsString(json, "id"));
            String durabilityField = GsonHelper.getAsString(json, "durability_field", "");
            List<JsonElement> statFieldsJson = GsonHelper.getAsJsonArray(json, "stats").asList();
            List<DynamicStatField<?,?>> statFields = new ArrayList<>();
            for (JsonElement statFieldJson : statFieldsJson) {
                statFields.add(DynamicStatField.REGISTRY.deserialize(GsonHelper.convertToJsonObject(statFieldJson, "stats[?]"), context));
            }
            return new DynamicMaterialStatType(id, durabilityField, statFields);
        }

        @Override
        public void serialize(DynamicMaterialStatType object, JsonObject json) {
            json.addProperty("id", object.getId().toString());
            if(!object.durabilityField.isEmpty()) json.addProperty("durability_field", object.durabilityField);
            JsonArray statFieldsJson = new JsonArray();
            for (DynamicStatField<?,?> statField : object.statFields) {
                statFieldsJson.add(DynamicStatField.REGISTRY.serialize(statField));
            }
            json.add("stats", statFieldsJson);
        }
    }
}