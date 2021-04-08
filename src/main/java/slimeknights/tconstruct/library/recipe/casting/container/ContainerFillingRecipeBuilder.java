package slimeknights.tconstruct.library.recipe.casting.container;

import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Builder for a container filling recipe. Takes an arbitrary fluid for a specific amount to fill a Forge {@link net.minecraftforge.fluids.capability.IFluidHandlerItem}
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class ContainerFillingRecipeBuilder extends AbstractRecipeBuilder<ContainerFillingRecipeBuilder> {
  private final ContainerFillingRecipeSerializer<?> recipeSerializer;
  private final int fluidAmount;
  private final Item result;

  private ContainerFillingRecipeBuilder(ItemConvertible result, int fluidAmount, ContainerFillingRecipeSerializer<?> recipeSerializer) {
    this.result = result.asItem();
    this.fluidAmount = fluidAmount;
    this.recipeSerializer = recipeSerializer;
  }

  /**
   * Creates a new builder instance using the given result, amount, and serializer
   * @param result            Recipe result
   * @param fluidAmount       Container size
   * @param recipeSerializer  Serializer
   * @return  Builder instance
   */
  public static ContainerFillingRecipeBuilder castingRecipe(ItemConvertible result, int fluidAmount, ContainerFillingRecipeSerializer<?> recipeSerializer) {
    return new ContainerFillingRecipeBuilder(result, fluidAmount, recipeSerializer);
  }

  /**
   * Creates a new basin recipe builder using the given result, amount, and serializer
   * @param result            Recipe result
   * @param fluidAmount       Container size
   * @return  Builder instance
   */
  public static ContainerFillingRecipeBuilder basinRecipe(ItemConvertible result, int fluidAmount) {
    return castingRecipe(result, fluidAmount, TinkerSmeltery.basinFillingRecipeSerializer.get());
  }

  /**
   * Creates a new table recipe builder using the given result, amount, and serializer
   * @param result            Recipe result
   * @param fluidAmount       Container size
   * @return  Builder instance
   */
  public static ContainerFillingRecipeBuilder tableRecipe(ItemConvertible result, int fluidAmount) {
    return castingRecipe(result, fluidAmount, TinkerSmeltery.tableFillingRecipeSerializer.get());
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumer) {
    this.build(consumer, Objects.requireNonNull(this.result.getRegistryName()));
  }

  @Override
  public void build(Consumer<RecipeJsonProvider> consumerIn, Identifier id) {
    Identifier advancementId = this.buildOptionalAdvancement(id, "casting");
    consumerIn.accept(new Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(Identifier ID, @Nullable Identifier advancementID) {
      super(ID, advancementID);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return recipeSerializer;
    }

    @Override
    public void serialize(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      json.addProperty("fluid_amount", fluidAmount);
      json.addProperty("container", Objects.requireNonNull(result.asItem().getRegistryName()).toString());
    }
  }
}
