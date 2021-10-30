package slimeknights.tconstruct.library.tools.definition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import lombok.Data;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import java.lang.reflect.Type;
import java.util.Objects;

/** Element that contains data about a single tool part */
@Data
public class PartRequirement {
  public static final Serializer SERIALIZER = new Serializer();

  /** Tool part */
  private final IToolPart part;
  /** Weight of this part for the stat builder */
  private final int weight;


  /* Packet buffers */

  /** Writes a tool definition stat object to a packet buffer */
  public void write(PacketBuffer buffer) {
    RecipeHelper.writeItem(buffer, part);
    buffer.writeVarInt(weight);
  }

  /** Reads a tool definition stat object from a packet buffer */
  public static PartRequirement read(PacketBuffer buffer) {
    IToolPart part = RecipeHelper.readItem(buffer, IToolPart.class);
    int weight = buffer.readVarInt();
    return new PartRequirement(part, weight);
  }

  /** Gets the stat type for this part */
  public MaterialStatsId getStatType() {
    return part.getStatType();
  }

  /** Serializer logic */
  protected static class Serializer implements JsonDeserializer<PartRequirement>, JsonSerializer<PartRequirement> {
    @Override
    public PartRequirement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonObject = JSONUtils.getJsonObject(json, "part");
      ResourceLocation name = JsonHelper.getResourceLocation(jsonObject, "item");
      if (!ForgeRegistries.ITEMS.containsKey(name)) {
        throw new JsonSyntaxException("Invalid item '" + name + "' for tool part, does not exist");
      }
      Item item = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(name));
      if (!(item instanceof IToolPart)) {
        throw new JsonSyntaxException("Invalid item '" + name + "' for tool part, must implement IToolPart");
      }
      int weight = JSONUtils.getInt(jsonObject, "weight", 1);
      return new PartRequirement((IToolPart) item, weight);
    }

    @Override
    public JsonElement serialize(PartRequirement part, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("item", Objects.requireNonNull(part.part.asItem().getRegistryName()).toString());
      if (part.weight != 1) {
        jsonObject.addProperty("weight", part.weight);
      }
      return jsonObject;
    }
  }
}
