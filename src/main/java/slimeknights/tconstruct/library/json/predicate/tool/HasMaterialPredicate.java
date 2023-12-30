package slimeknights.tconstruct.library.json.predicate.tool;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;

/**
 * Tool predicate checking for the given material on the tool
 * @param material   Material variant to locate.
 * @param index      Index to check for the material. If -1, will check all materials on the tool.
 */
public record HasMaterialPredicate(MaterialVariantId material, int index) implements ToolContextPredicate {
  public HasMaterialPredicate(MaterialVariantId material) {
    this(material, -1);
  }

  @Override
  public boolean matches(IToolContext input) {
    // if given an index, use exact location match
    if (index >= 0) {
      return material.matchesVariant(input.getMaterial(index));
    }
    // otherwise, search each material
    for (MaterialVariant variant : input.getMaterials().getList()) {
      if (material.matchesVariant(variant)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<IToolContext>> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<HasMaterialPredicate> LOADER = new IGenericLoader<>() {
    @Override
    public HasMaterialPredicate deserialize(JsonObject json) {
      MaterialVariantId material = MaterialVariantId.fromJson(json, "material");
      int index = GsonHelper.getAsInt(json, "index", -1);
      return new HasMaterialPredicate(material, index);
    }

    @Override
    public void serialize(HasMaterialPredicate object, JsonObject json) {
      json.addProperty("material", object.material.toString());
    }

    @Override
    public HasMaterialPredicate fromNetwork(FriendlyByteBuf buffer) {
      MaterialVariantId material = MaterialVariantId.fromNetwork(buffer);
      int index = buffer.readShort();
      return new HasMaterialPredicate(material, index);
    }

    @Override
    public void toNetwork(HasMaterialPredicate object, FriendlyByteBuf buffer) {
      object.material.toNetwork(buffer);
      buffer.writeShort(object.index);
    }
  };
}
