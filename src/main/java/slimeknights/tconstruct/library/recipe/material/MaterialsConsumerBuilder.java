package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.tables.TinkerTables;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Special variant of {@link ConsumerWrapperBuilder} for {@link ShapedMaterialsRecipe} and {@link ShapelessMaterialsRecipe} */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialsConsumerBuilder {
  private final String parts;
  private final int partCount;
  private final List<MaterialVariantId> materials = new ArrayList<>();

  /** Creates a new shaped recipe with the given ingredients as parts */
  public static MaterialsConsumerBuilder shaped(String parts) {
    if (parts.isEmpty()) {
      throw new IllegalArgumentException("Parts may not be empty");
    }
    return new MaterialsConsumerBuilder(parts, 0);
  }

  /** Creates a new shapeless recipe with the first ingredients as parts */
  public static MaterialsConsumerBuilder shapeless(int parts) {
    if (parts <= 0) {
      throw new IllegalArgumentException("Parts must be greater than 0");
    }
    return new MaterialsConsumerBuilder("", parts);
  }

  /** Adds a material to the builder */
  public MaterialsConsumerBuilder material(MaterialVariantId material) {
    materials.add(material);
    return this;
  }

  /** Builds the wrapped consumer */
  public Consumer<FinishedRecipe> build(Consumer<FinishedRecipe> consumer) {
    return (recipe) -> consumer.accept(new Wrapped(recipe, materials, parts, partCount));
  }

  private record Wrapped(FinishedRecipe original, List<MaterialVariantId> materials, String parts, int partCount) implements FinishedRecipe {
    @Override
    public ResourceLocation getId() {
      return original.getId();
    }

    @Override
    public RecipeSerializer<?> getType() {
      return partCount > 0 ? TinkerTables.shapelessMaterialsRecipeSerializer.get() : TinkerTables.shapedMaterialsRecipeSerializer.get();
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      original.serializeRecipeData(json);
      if (!materials.isEmpty()) {
        json.add(ShapedMaterialsRecipe.Serializer.MATERIAL_FIELD.key(), ShapedMaterialsRecipe.Serializer.EXTRA_MATERIALS.serialize(materials));
      }
      if (!parts.isEmpty()) {
        json.addProperty("parts", parts);
      } else {
        json.addProperty("parts", partCount);
      }
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
      return original.serializeAdvancement();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
      return original.getAdvancementId();
    }
  }
}
