package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Casting recipe taking a part of a material and a fluid and outputting the part with a new material
 */
public abstract class CompositeCastingRecipe extends MaterialCastingRecipe {
  public CompositeCastingRecipe(RecipeType<?> type, ResourceLocation id, String group, IMaterialItem result, int itemCost) {
    super(type, id, group, Ingredient.of(result), itemCost, result, true, false);
  }

  @Override
  protected Optional<MaterialFluidRecipe> getMaterialFluid(ICastingContainer inv) {
    return MaterialCastingLookup.getCompositeFluid(inv);
  }

  /* JEI display */
  @Override
  public List<IDisplayableCastingRecipe> getRecipes() {
    if (multiRecipes == null) {
      RecipeType<?> type = getType();
      multiRecipes = MaterialCastingLookup
        .getAllCompositeFluids().stream()
        .filter(recipe -> {
          MaterialVariant output = recipe.getOutput();
          MaterialVariant input = recipe.getInput();
          return !output.isUnknown() && input != null && !input.isUnknown()
            && !output.get().isHidden() && !input.get().isHidden() && result.canUseMaterial(output.getId()) && result.canUseMaterial(input.getId());
        })
        .map(recipe -> {
          List<FluidStack> fluids = resizeFluids(recipe.getFluids());
          int fluidAmount = fluids.stream().mapToInt(FluidStack::getAmount).max().orElse(0);
          return new DisplayCastingRecipe(type, Collections.singletonList(result.withMaterial(Objects.requireNonNull(recipe.getInput()).getVariant())), fluids, result.withMaterial(recipe.getOutput().getId()),
                                          ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * fluidAmount), consumed);
        })
        .collect(Collectors.toList());
    }
    return multiRecipes;
  }

  /** Basin implementation */
  public static class Basin extends CompositeCastingRecipe {
    public Basin(ResourceLocation id, String group, IMaterialItem result, int itemCost) {
      super(RecipeTypes.CASTING_BASIN, id, group, result, itemCost);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.basinCompositeSerializer.get();
    }
  }

  /** Table implementation */
  public static class Table extends CompositeCastingRecipe {
    public Table(ResourceLocation id, String group, IMaterialItem result, int itemCost) {
      super(RecipeTypes.CASTING_TABLE, id, group, result, itemCost);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return TinkerSmeltery.tableCompositeSerializer.get();
    }
  }

  /**
   * Interface representing a composite casting recipe constructor
   * @param <T>  Recipe class type
   */
  public interface IFactory<T extends CompositeCastingRecipe> {
    T create(ResourceLocation id, String group, IMaterialItem result, int itemCost);
  }

  /** Shared serializer logic */
  @RequiredArgsConstructor
  public static class Serializer<T extends CompositeCastingRecipe> extends LoggingRecipeSerializer<T> {
    private final IFactory<T> factory;

    @Override
    public T fromJson(ResourceLocation id, JsonObject json) {
      String group = GsonHelper.getAsString(json, "group", "");
      IMaterialItem result = RecipeHelper.deserializeItem(GsonHelper.getAsString(json, "result"), "result", IMaterialItem.class);
      int itemCost = GsonHelper.getAsInt(json, "item_cost");
      return factory.create(id, group, result, itemCost);
    }

    @Nullable
    @Override
    protected T fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      String group = buffer.readUtf(Short.MAX_VALUE);
      IMaterialItem result = RecipeHelper.readItem(buffer, IMaterialItem.class);
      int itemCost = buffer.readVarInt();
      return factory.create(id, group, result, itemCost);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, T recipe) {
      buffer.writeUtf(recipe.group);
      RecipeHelper.writeItem(buffer, recipe.result);
      buffer.writeVarInt(recipe.itemCost);
    }
  }
}
