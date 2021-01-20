package slimeknights.tconstruct.library.recipe.casting;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.RecipeSerializer;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import javax.annotation.Nullable;
import java.util.List;

@AllArgsConstructor
public abstract class AbstractCastingRecipe implements ICastingRecipe {
  @Getter
  protected final IRecipeType<?> type;
  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  /** 'cast' item for recipe (doesn't have to be an actual 'cast') */
  @Getter
  protected final Ingredient cast;
  @Getter // For JEI
  protected final FluidIngredient fluid;
  @Getter
  protected final int coolingTime;
  @Getter
  protected final boolean consumed;
  @Getter @Accessors(fluent = true)
  protected final boolean switchSlots;

  @Override
  public boolean matches(ICastingInventory inv, World worldIn) {
    return this.getCast().test(inv.getStack()) && this.fluid.test(inv.getFluid());
  }

  @Override
  public int getFluidAmount(ICastingInventory inv) {
    return this.fluid.getAmount(inv.getFluid());
  }

  @Override
  public abstract ItemStack getRecipeOutput();

  @Override
  public int getCoolingTime(ICastingInventory inv) {
    return this.coolingTime;
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.from(Ingredient.EMPTY, this.cast);
  }

  /**
   * Gets a list of valid fluid inputs for this recipe, for display in JEI
   * @return  List of fluids
   */
  public List<FluidStack> getFluids() {
    return this.fluid.getFluids();
  }

  /**
   * Seralizer for {@link ItemCastingRecipe}.
   * @param <T>  Casting recipe class type
   */
  @AllArgsConstructor
  public abstract static class Serializer<T extends AbstractCastingRecipe> extends RecipeSerializer<T> {
    /** Creates a new instance from JSON */
    protected abstract T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, FluidIngredient fluidIn, int coolingTime, boolean consumed, boolean switchSlots, JsonObject json);

    /** Creates a new instance from the packet buffer */
    protected abstract T create(ResourceLocation idIn, String groupIn, @Nullable Ingredient cast, FluidIngredient fluidIn, int coolingTime, boolean consumed, boolean switchSlots, PacketBuffer buffer);

    /** Writes extra data to the packet buffer */
    public abstract void writeExtra(PacketBuffer buffer, T recipe);

    @Override
    public T read(ResourceLocation recipeId, JsonObject json) {
      Ingredient cast = Ingredient.EMPTY;
      String group = JSONUtils.getString(json, "group", "");
      boolean consumed = false;
      boolean switchSlots = JSONUtils.getBoolean(json, "switch_slots", false);
      if (json.has("cast")) {
        cast = Ingredient.deserialize(JsonHelper.getElement(json, "cast"));
        consumed = JSONUtils.getBoolean(json, "cast_consumed", false);
      }
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      int coolingTime = JSONUtils.getInt(json, "cooling_time");
      return create(recipeId, group, cast, fluid, coolingTime, consumed, switchSlots, json);
    }

    @Nullable
    @Override
    public T read(ResourceLocation recipeId, PacketBuffer buffer) {
      try {
        String group = buffer.readString(Short.MAX_VALUE);
        Ingredient cast = Ingredient.read(buffer);
        FluidIngredient fluid = FluidIngredient.read(buffer);
        int coolingTime = buffer.readInt();
        boolean consumed = buffer.readBoolean();
        boolean switchSlots = buffer.readBoolean();
        return create(recipeId, group, cast, fluid, coolingTime, consumed, switchSlots, buffer);
      } catch (Exception e) {
        TConstruct.log.error("Error reading item casting recipe from packet.", e);
        throw e;
      }
    }

    @Override
    public void write(PacketBuffer buffer, T recipe) {
      try {
        buffer.writeString(recipe.group);
        recipe.cast.write(buffer);
        recipe.fluid.write(buffer);
        buffer.writeInt(recipe.coolingTime);
        buffer.writeBoolean(recipe.consumed);
        buffer.writeBoolean(recipe.switchSlots);
        writeExtra(buffer, recipe);
      } catch (Exception e) {
        TConstruct.log.error("Error writing item casting recipe to packet.", e);
        throw e;
      }
    }
  }
}
