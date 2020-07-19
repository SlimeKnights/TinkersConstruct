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
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.recipe.AbstractRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;

public class ContainerFillingRecipeBuilder extends AbstractRecipeBuilder<ContainerFillingRecipeBuilder> {
  private final ContainerFillingRecipeSerializer<?> recipeSerializer;
  private String group;
  private final int fluidAmount;
  private final Item result;

  private ContainerFillingRecipeBuilder(IItemProvider result, int fluidAmount, ContainerFillingRecipeSerializer<?> recipeSerializer) {
    this.result = result.asItem();
    this.fluidAmount = fluidAmount;
    this.recipeSerializer = recipeSerializer;
  }

  public static ContainerFillingRecipeBuilder castingRecipe(IItemProvider result, int fluidAmount, ContainerFillingRecipeSerializer<?> recipeSerializer) {
    return new ContainerFillingRecipeBuilder(result, fluidAmount, recipeSerializer);
  }

  public static ContainerFillingRecipeBuilder basinRecipe(IItemProvider result, int fluidAmount) {
    return castingRecipe(result, fluidAmount, TinkerSmeltery.basinFillingRecipeSerializer.get());
  }

  public static ContainerFillingRecipeBuilder tableRecipe(IItemProvider result, int fluidAmount) {
    return castingRecipe(result, fluidAmount, TinkerSmeltery.tableFillingRecipeSerializer.get());
  }

  public ContainerFillingRecipeBuilder setGroup(String group) {
    this.group = group;
    return this;
  }

  public void build(Consumer<IFinishedRecipe> consumer) {
    this.build(consumer, Objects.requireNonNull(this.result.getRegistryName()));
  }

  @Override
  public void build(Consumer<IFinishedRecipe> consumerIn, ResourceLocation id) {
    ResourceLocation advancementId = this.buildAdvancement(id, "casting");
    consumerIn.accept(new ContainerFillingRecipeBuilder.Result(id, this.group == null ? "" : this.group, this.fluidAmount, this.result, this.advancementBuilder, advancementId, this.recipeSerializer));
  }

  @AllArgsConstructor
  public static class Result implements IFinishedRecipe {
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
      json.addProperty("container", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(this.result.asItem())).toString());
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return this.advancementBuilder.serialize();
    }
  }
}
