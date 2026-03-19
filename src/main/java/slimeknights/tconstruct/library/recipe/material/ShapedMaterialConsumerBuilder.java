package slimeknights.tconstruct.library.recipe.material;

import com.google.gson.JsonObject;
import lombok.NoArgsConstructor;
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

/** Special variant of {@link ConsumerWrapperBuilder} for {@link ShapedMaterialRecipe} */
@Deprecated
@NoArgsConstructor(staticName = "wrap")
public class ShapedMaterialConsumerBuilder {
  private final List<MaterialVariantId> materials = new ArrayList<>();

  /** Adds a material to the builder */
  public ShapedMaterialConsumerBuilder material(MaterialVariantId material) {
    materials.add(material);
    return this;
  }

  /** Builds the wrapped consumer */
  public Consumer<FinishedRecipe> build(Consumer<FinishedRecipe> consumer) {
    return (recipe) -> consumer.accept(new Wrapped(recipe, materials));
  }

  private record Wrapped(FinishedRecipe original, List<MaterialVariantId> materials) implements FinishedRecipe {
    @Override
    public ResourceLocation getId() {
      return original.getId();
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerTables.shapedMaterialRecipeSerializer.get();
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      original.serializeRecipeData(json);
      if (!materials.isEmpty()) {
        json.add(ShapedMaterialRecipe.Serializer.MATERIAL_FIELD.key(), ShapedMaterialRecipe.Serializer.EXTRA_MATERIALS.serialize(materials));
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
