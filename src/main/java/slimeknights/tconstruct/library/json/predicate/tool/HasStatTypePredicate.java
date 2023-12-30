package slimeknights.tconstruct.library.json.predicate.tool;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Checks if the tool has the given stat type.
 * @param statType    Stat type to locate.
 * @param material    If non-null, requires the given material in that stat type. If null, materials are ignored.
 */
public record HasStatTypePredicate(MaterialStatsId statType, @Nullable MaterialVariantId material) implements ToolContextPredicate {
  public HasStatTypePredicate(MaterialStatsId statType) {
    this(statType, null);
  }

  @Override
  public boolean matches(IToolContext tool) {
    List<PartRequirement> parts = tool.getDefinition().getData().getParts();
    for (int i = 0; i < parts.size(); i++) {
      if (statType.equals(parts.get(i).getStatType()) && (material == null || material.matchesVariant(tool.getMaterial(i)))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<IToolContext>> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<HasStatTypePredicate> LOADER = new IGenericLoader<>() {
    @Override
    public HasStatTypePredicate deserialize(JsonObject json) {
      MaterialStatsId statType = MaterialStatsId.PARSER.getFromJson(json, "stat_type");
      MaterialVariantId material = null;
      if (json.has("material")) {
        material = MaterialVariantId.fromJson(json, "material");
      }
      return new HasStatTypePredicate(statType, material);
    }

    @Override
    public void serialize(HasStatTypePredicate object, JsonObject json) {
      json.addProperty("stat_type", object.statType.toString());
      if (object.material != null) {
        json.addProperty("material", object.material.toString());
      }
    }

    @Override
    public HasStatTypePredicate fromNetwork(FriendlyByteBuf buffer) {
      MaterialStatsId statType = MaterialStatsId.PARSER.fromNetwork(buffer);
      MaterialVariantId material = null;
      if (buffer.readBoolean()) {
        material = MaterialVariantId.fromNetwork(buffer);
      }
      return new HasStatTypePredicate(statType, material);
    }

    @Override
    public void toNetwork(HasStatTypePredicate object, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(object.statType);
      if (object.material != null) {
        buffer.writeBoolean(true);
        object.material.toNetwork(buffer);
      } else {
        buffer.writeBoolean(false);
      }
    }
  };
}
