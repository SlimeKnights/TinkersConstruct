package slimeknights.tconstruct.library.recipe.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "fallback")
public class ShapedFallbackRecipeBuilder {
  private final ShapedRecipeBuilder base;
  private final List<ResourceLocation> alternatives = new ArrayList<>();

  /**
   * Adds a single alternative to this recipe. Any matching alternative causes this recipe to fail
   * @param location  Alternative
   * @return  Builder instance
   */
  public ShapedFallbackRecipeBuilder addAlternative(ResourceLocation location) {
    this.alternatives.add(location);
    return this;
  }

  /**
   * Adds a list of alternatives to this recipe. Any matching alternative causes this recipe to fail
   * @param locations  Alternative list
   * @return  Builder instance
   */
  public ShapedFallbackRecipeBuilder addAlternatives(Collection<ResourceLocation> locations) {
    this.alternatives.addAll(locations);
    return this;
  }

  /**
   * Builds the recipe using the output as the name
   * @param consumer  Recipe consumer
   */
  public void build(Consumer<IFinishedRecipe> consumer) {
    base.build(base -> consumer.accept(new Result(base, alternatives)));
  }

  /**
   * Builds the recipe using the given ID
   * @param consumer  Recipe consumer
   * @param id        Recipe ID
   */
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
    base.build(base -> consumer.accept(new Result(base, alternatives)), id);
  }

  @AllArgsConstructor
  public class Result implements IFinishedRecipe {
    private final IFinishedRecipe base;
    private final List<ResourceLocation> alternatives;

    @Override
    public void serialize(JsonObject json) {
      base.serialize(json);
      json.add("alternatives", alternatives.stream()
                                           .map(ResourceLocation::toString)
                                           .collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerCommons.shapedFallbackRecipe.get();
    }

    @Override
    public ResourceLocation getID() {
      return base.getID();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return base.getAdvancementJson();
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return base.getAdvancementID();
    }
  }
}
