package slimeknights.tconstruct.library.materials.stats.types;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import lombok.NonNull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import slimeknights.mantle.data.registry.IdAwareComponentRegistry;
import slimeknights.mantle.registration.object.IdAwareObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;
import net.minecraft.resources.ResourceLocation;

public interface DynamicStatField<S extends DynamicStatField.DynamicStat> {

    public static final IdAwareComponentRegistry<DynamicStatDecoder<?>> REGISTRY = new IdAwareComponentRegistry<>(
            "Unknown Dynamic Stat Field Type");

    /**
     * Gets the decoder for the given ID, or throws an JsonParseException if not
     * found
     * 
     * @param id ID of the decoder
     * @return Decoder for the ID
     */
    @NonNull
    public static DynamicStatDecoder<?> getDecoderOrThrow(ResourceLocation id) {
        DynamicStatDecoder<?> decoder = REGISTRY.getValue(id);
        if (decoder == null) {
            throw new JsonParseException("Unknown Dynamic Stat Field Type: " + id);
        }
        return decoder;
    }

    /**
     * Decode the stat field from the network
     * 
     * @param buffer Network buffer
     * @return Decoded stat field
     */
    @NonNull
    public static DynamicStatField<?> decodeSelf(FriendlyByteBuf buffer) {
        ResourceLocation id = new ResourceLocation(buffer.readUtf());
        return getDecoderOrThrow(id).decode(buffer);
    }

    /**
     * Deserialize the stat field from JSON
     * 
     * @param json JSON object
     * @return Deserialized stat field
     */
    @NonNull
    public static DynamicStatField<?> deserializeSelf(JsonObject json, ResourceLocation path) {
        String statType = json.get("type").getAsString();
        return getDecoderOrThrow(new ResourceLocation(withDefaultNamespace(statType))).deserialize(json, path);
    }

    public static String withDefaultNamespace(String path) {
        if (!path.contains(":")) {
            return TConstruct.MOD_ID + ":" + path;
        }
        return path;
    }

    public static interface DynamicStatDecoder<F extends DynamicStatField<?>> extends IdAwareObject {
        /**
         * Decode the stat field from the network
         * 
         * @param buffer Network buffer
         * @return Decoded stat field
         */
        public F decode(FriendlyByteBuf buffer);

        /**
         * Deserialize the stat field from JSON
         * 
         * @param json JSON object
         * @param path Path of the stat type
         * @return Deserialized stat field
         */
        public F deserialize(JsonObject json, ResourceLocation path);
    }

    /**
     * Get the name of StatType
     * 
     * @return Stat type
     */
    public String getStatType();

    /**
     * Get the name of the stat field
     * 
     * @return Name of the stat field
     */
    public String getName();

    public static interface DynamicStat {

        /**
         * Applies the stat field to the stats builder
         * 
         * @param builder Stats builder to apply to
         * @param scale   Scale to apply
         */
        public void apply(ModifierStatsBuilder builder, float scale);

        /**
         * Formats the stat field into a component
         * 
         * @return Formatted component
         */
        public Component getLocalizedInfo();

        /**
         * Formats the stat field into a component
         * 
         * @return Formatted component
         */
        public Component getLocalizedDescription();
    }

    /**
     * Encode the stat field to the network
     * 
     * @param buffer Network buffer
     */
    public void encodeSelf(FriendlyByteBuf buffer);

    /**
     * Serialize the stat field to JSON
     * 
     * @param json JSON object
     */
    public void serializeSelf(JsonObject json);

    /**
     * Decode the stat from the network
     * 
     * @param buffer Network buffer
     * @return Decoded stat
     */
    public S decode(FriendlyByteBuf buffer);

    /**
     * Encode the stat to the network
     * 
     * @param buffer Network buffer
     * @param value  Dynamic Stat instance
     */
    public void encode(FriendlyByteBuf buffer, S value);

    default void encode(FriendlyByteBuf buffer, DynamicMaterialStat value) {
        this.encode(buffer, (S) value.getStat(this.getName()));
    }

    /**
     * Deserialize the stat field from JSON
     * 
     * @param json JSON object
     * @return Deserialized stat field
     */
    public S deserialize(JsonObject json);

    /**
     * Serialize the stat field to JSON
     * 
     * @param object Stat instance
     * @param json   JSON object
     */
    public void serialize(S object, JsonObject json);

    default void serialize(DynamicMaterialStat object, JsonObject json) {
        this.serialize((S) object.getStat(this.getName()), json);
    }

    /**
     * Get the default stat for this field
     * 
     * @return Default stat
     */
    default S getDefaultStat() {
        return deserialize(new JsonObject());
    }

}
