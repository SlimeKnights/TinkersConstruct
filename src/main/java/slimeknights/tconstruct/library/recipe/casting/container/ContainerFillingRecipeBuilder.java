package slimeknights.tconstruct.library.recipe.casting.container;

import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
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

  private ContainerFillingRecipeBuilder(IItemProvider result, int fluidAmount, ContainerFillingRecipeSerializer<?> recipeSerializer) {
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
  public static ContainerFillingRecipeBuilder castingRecipe(IItemProvider result, int fluidAmount, ContainerFillingRecipeSerializer<?> recipeSerializer) {
    return new ContainerFillingRecipeBuilder(result, fluidAmount, recipeSerializer);
  }

  /**
   * Creates a new basin recipe builder using the given result, amount, and serializer
   * @param result            Recipe result
   * @param fluidAmount       Container size
   * @return  Builder instance
   */
  public static ContainerFillingRecipeBuilder basinRecipe(IItemProvider result, int fluidAmount) {
    return castingRecipe(result, fluidAmount, TinkerSmeltery.basinFillingRecipeSerializer.get());
  }

  /**
   * Creates a new table recipe builder using the given result, amount, and serializer
   * @param result            Recipe result
   * @param fluidAmount       Container size
   * @return  Builder instance
   */
  public static ContainerFillingRecipeBuilder tableRecipe(IItemProvider result, int fluidAmount) {
    return castingRecipe(result, fluidAmount, TinkerSmeltery.tableFillingRecipeSerializer.get());
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumer) {
    this.build(consumer, Objects.requireNonNull(this.result.getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "casting");
    consumerIn.accept(new ContainerFillingRecipeBuilder.Result(id, advancementId));
  }

  private class Result extends AbstractFinishedRecipe {
    public Result(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
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
