package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
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
    ResourceLocation advancementId = this.buildAdvancement(id, "casting");
    consumerIn.accept(new ContainerFillingRecipeBuilder.Result(id, this.group, this.fluidAmount, this.result, this.advancementBuilder, advancementId, this.recipeSerializer));
  }

  @AllArgsConstructor
  private static class Result implements IFinishedRecipe {
    @Getter
    protected final ResourceLocation ID;
    private final String group;
    private final int fluidAmount;
    private final Item result;
    private final Advancement.Builder advancementBuilder;
    @Getter
    private final ResourceLocation advancementID;
    @Getter
    private final IRecipeSerializer<? extends ContainerFillingRecipe> serializer;

    @Override
    public void serialize(JsonObject json) {
      if (!this.group.isEmpty()) {
        json.addProperty("group", this.group);
      }
      json.addProperty("fluid_amount", this.fluidAmount);
      json.addProperty("container", Objects.requireNonNull(this.result.asItem().getRegistryName()).toString());
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }
  }
}
