package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Recipe for casting a fluid onto an item, copying the fluid NBT to the item
 */
@RequiredArgsConstructor
public class PotionCastingRecipe implements ICastingRecipe, IMultiRecipe<DisplayCastingRecipe> {
  @Getter
  private final RecipeType<?> type;
  @Getter
  private final RecipeSerializer<?> serializer;
  @Getter
  private final ResourceLocation id;
  @Getter
  private final String group;
  /** Input on the casting table, always consumed */
  private final Ingredient bottle;
  /** Potion ingredient, typically just the potion tag */
  private final FluidIngredient fluid;
  /** Potion item result, will be given the proper NBT */
  private final Item result;
  /** Cooling time, used for arrows */
  private final int coolingTime;

  private List<DisplayCastingRecipe> displayRecipes = null;

  @Override
  public boolean matches(ICastingContainer inv, Level level) {
    return bottle.test(inv.getStack()) && fluid.test(inv.getFluid());
  }

  @Override
  public int getFluidAmount(ICastingContainer inv) {
    return fluid.getAmount(inv.getFluid());
  }

  @Override
  public boolean isConsumed() {
    return true;
  }

  @Override
  public boolean switchSlots() {
    return false;
  }

  @Override
  public int getCoolingTime(ICastingContainer inv) {
    return coolingTime;
  }

  @Override
  public ItemStack assemble(ICastingContainer inv) {
    ItemStack result = new ItemStack(this.result);
    result.setTag(inv.getFluidTag());
    return result;
  }

  @Override
  public List<DisplayCastingRecipe> getRecipes() {
    if (displayRecipes == null) {
      // create a subrecipe for every potion variant
      List<ItemStack> bottles = List.of(bottle.getItems());
      displayRecipes = ForgeRegistries.POTIONS.getValues().stream()
        .map(potion -> {
          ItemStack result = PotionUtils.setPotion(new ItemStack(this.result), potion);
          return new DisplayCastingRecipe(type, bottles, fluid.getFluids().stream()
                                                              .map(fluid -> new FluidStack(fluid.getFluid(), fluid.getAmount(), result.getTag()))
                                                              .toList(),
                                          result, coolingTime, true);
        }).toList();
    }
    return displayRecipes;
  }


  /* Recipe interface methods */

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.of(Ingredient.EMPTY, bottle);
  }

  /** @deprecated use {@link #assemble(Container)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return new ItemStack(this.result);
  }

  @RequiredArgsConstructor
  public static class Serializer extends LoggingRecipeSerializer<PotionCastingRecipe> {
    private final Supplier<RecipeType<ICastingRecipe>> type;

    @Override
    public PotionCastingRecipe fromJson(ResourceLocation id, JsonObject json) {
      String group = GsonHelper.getAsString(json, "group", "");
      Ingredient bottle = Ingredient.fromJson(JsonHelper.getElement(json, "bottle"));
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      Item result = JsonHelper.getAsEntry(ForgeRegistries.ITEMS, json, "result");
      int coolingTime = GsonHelper.getAsInt(json, "cooling_time");
      return new PotionCastingRecipe(type.get(), this, id, group, bottle, fluid, result, coolingTime);
    }

    @Nullable
    @Override
    protected PotionCastingRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      String group = buffer.readUtf(Short.MAX_VALUE);
      Ingredient bottle = Ingredient.fromNetwork(buffer);
      FluidIngredient fluid = FluidIngredient.read(buffer);
      Item result = buffer.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
      int coolingTime = buffer.readVarInt();
      return new PotionCastingRecipe(type.get(), this, id, group, bottle, fluid, result, coolingTime);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, PotionCastingRecipe recipe) {
      buffer.writeUtf(recipe.group);
      recipe.bottle.toNetwork(buffer);
      recipe.fluid.write(buffer);
      buffer.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, recipe.result);
      buffer.writeVarInt(recipe.coolingTime);
    }
  }
}
