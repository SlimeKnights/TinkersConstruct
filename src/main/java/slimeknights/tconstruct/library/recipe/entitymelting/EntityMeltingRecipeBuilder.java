package slimeknights.tconstruct.library.recipe.entitymelting;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/** Builder for entity melting recipes */
@RequiredArgsConstructor(staticName = "melting")
public class EntityMeltingRecipeBuilder extends AbstractRecipeBuilder<EntityMeltingRecipeBuilder> {
  private final EntityIngredient ingredient;
  private final FluidStack output;
  private final int damage;

  /** Creates a new builder doing 2 damage */
  public static EntityMeltingRecipeBuilder melting(EntityIngredient ingredient, FluidStack output) {
    return melting(ingredient, output, 2);
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    build(consumer, Objects.requireNonNull(output.getFluid().getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation resourceLocation) {
    consumer.accept(new FinishedRecipe(resourceLocation));
  }

  @RequiredArgsConstructor
  private class FinishedRecipe implements IFinishedRecipe {
    @Getter
    private final ResourceLocation ID;

    @Override
    public void serialize(JsonObject json) {
      json.add("entity", ingredient.serialize());
      json.add("output", RecipeHelper.serializeFluidStack(output));
      json.addProperty("damage", damage);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.entityMeltingSerializer.get();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
      return null;
    }
  }
}
