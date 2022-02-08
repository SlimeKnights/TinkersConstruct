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
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.client.materials.MaterialTooltipCache;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.part.IToolPart;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;

/** Element that contains data about a single tool part */
@Data
public abstract class PartRequirement {
  public static final Serializer SERIALIZER = new Serializer();

  /** Creates a new part requirement for a part */
  public static PartRequirement ofPart(IToolPart part, int weight) {
    return new PartRequirement.ToolPart(part, weight);
  }

  /** Creates a new part requirement for a stat type */
  public static PartRequirement ofStat(MaterialStatsId statsId, int weight) {
    return new PartRequirement.StatType(statsId, weight);
  }

  /** Weight of this part for the stat builder */
  private final int weight;

  /** Gets the part for this requirement (if present) */
  @Nullable
  public abstract IToolPart getPart();

  /** If true, this part requirement matches the given item */
  public abstract boolean matches(Item item);

  /** If true, this requirement can use the given material */
  public abstract boolean canUseMaterial(MaterialVariantId material);

  /** Gets the name of this part for the given material */
  public abstract Component nameForMaterial(MaterialVariantId material);

  /** Gets the stat type for this part */
  public abstract MaterialStatsId getStatType();


  /* Serializing */

  /** Writes a tool definition stat object to a packet buffer */
  public abstract void write(FriendlyByteBuf buffer);

  /** Writes a tool definition stat object to a packet buffer */
  public abstract JsonObject serialize();

  /** Reads a tool definition stat object from a packet buffer */
  public static PartRequirement read(FriendlyByteBuf buffer) {
    if (buffer.readBoolean()) {
      IToolPart part = RecipeHelper.readItem(buffer, IToolPart.class);
      int weight = buffer.readVarInt();
      return ofPart(part, weight);
    } else {
      MaterialStatsId statsId = new MaterialStatsId(buffer.readResourceLocation());
      int weight = buffer.readVarInt();
      return ofStat(statsId, weight);
    }
  }

  /** Serializer logic */
  protected static class Serializer implements JsonDeserializer<PartRequirement>, JsonSerializer<PartRequirement> {
    @Override
    public PartRequirement deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "part");
      int weight = GsonHelper.getAsInt(jsonObject, "weight", 1);
      // part item
      if (jsonObject.has("item")) {
        ResourceLocation name = JsonHelper.getResourceLocation(jsonObject, "item");
        if (!ForgeRegistries.ITEMS.containsKey(name)) {
          throw new JsonSyntaxException("Invalid item '" + name + "' for tool part, does not exist");
        }
        Item item = Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(name));
        if (!(item instanceof IToolPart)) {
          throw new JsonSyntaxException("Invalid item '" + name + "' for tool part, must implement IToolPart");
        }
        return ofPart((IToolPart) item, weight);
      }
      // part stat
      if (jsonObject.has("stat")) {
        MaterialStatsId stat = new MaterialStatsId(JsonHelper.getResourceLocation(jsonObject, "stat"));
        return ofStat(stat, weight);
      }
      throw new JsonSyntaxException("Invalid part, must have either 'item' or 'stat'");
    }

    @Override
    public JsonElement serialize(PartRequirement part, Type typeOfSrc, JsonSerializationContext context) {
      return part.serialize();
    }
  }

  /** Implementation that contains a tool part */
  private static class ToolPart extends PartRequirement {
    @Getter
    private final IToolPart part;
    public ToolPart(IToolPart part, int weight) {
      super(weight);
      this.part = part;
    }

    @Override
    public boolean matches(Item item) {
      return part.asItem() == item;
    }

    @Override
    public boolean canUseMaterial(MaterialVariantId material) {
      return part.canUseMaterial(material.getId());
    }

    @Override
    public Component nameForMaterial(MaterialVariantId material) {
      return part.withMaterial(material).getHoverName();
    }

    @Override
    public MaterialStatsId getStatType() {
      return part.getStatType();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
      buffer.writeBoolean(true);
      RecipeHelper.writeItem(buffer, part);
      buffer.writeVarInt(getWeight());
    }

    @Override
    public JsonObject serialize() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("item", Objects.requireNonNull(part.asItem().getRegistryName()).toString());
      if (getWeight() != 1) {
        jsonObject.addProperty("weight", getWeight());
      }
      return jsonObject;
    }
  }

  /** Implementation specifying a stat type with no part */
  private static class StatType extends PartRequirement {
    @Getter
    private final MaterialStatsId statType;
    public StatType(MaterialStatsId statType, int weight) {
      super(weight);
      this.statType = statType;
    }

    @Nullable
    @Override
    public IToolPart getPart() {
      return null;
    }

    @Override
    public boolean matches(Item item) {
      return false;
    }

    @Override
    public boolean canUseMaterial(MaterialVariantId material) {
      return MaterialRegistry.getInstance().getMaterialStats(material.getId(), statType).isPresent();
    }

    @Override
    public Component nameForMaterial(MaterialVariantId material) {
      return MaterialTooltipCache.getDisplayName(material);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
      buffer.writeBoolean(false);
      buffer.writeResourceLocation(statType);
      buffer.writeVarInt(getWeight());
    }

    @Override
    public JsonObject serialize() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("stat", statType.toString());
      if (getWeight() != 1) {
        jsonObject.addProperty("weight", getWeight());
      }
      return jsonObject;
    }
  }
}
