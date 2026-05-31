package slimeknights.tconstruct.compat.neoforged.neoforge.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import slimeknights.mantle.compat.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.conditions.ICondition;

public final class ConditionalRecipe {
  private ConditionalRecipe() {}

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private final List<Consumer<Consumer<FinishedRecipe>>> recipes = new ArrayList<>();

    public Builder addCondition(ICondition condition) {
      return this;
    }

    public Builder addRecipe(Consumer<Consumer<FinishedRecipe>> recipe) {
      recipes.add(recipe);
      return this;
    }

    public Builder generateAdvancement() {
      return this;
    }

    public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
      if (!recipes.isEmpty()) {
        recipes.get(recipes.size() - 1).accept(recipe -> consumer.accept(recipe));
      }
    }
  }
}
