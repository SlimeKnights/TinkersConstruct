package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.List;

/** Recipe defining casting and composite fluids for a given input */
public class MaterialFluidRecipe implements ICustomOutputRecipe<ICastingContainer> {
  @Getter
  private final ResourceLocation id;
  private final FluidIngredient fluid;
  @Getter
  private final int temperature;
  /** Material base for composite */
  @Getter @Nullable
  private final MaterialId inputId;
  /** Output material ID */
  @Getter
  private final MaterialId outputId;

  /** Cached input material */
  private IMaterial input;
  /** Cached output material */
  private IMaterial output;

  public MaterialFluidRecipe(ResourceLocation id, FluidIngredient fluid, int temperature, @Nullable MaterialId inputId, MaterialId outputId) {
    this.id = id;
    this.fluid = fluid;
    this.temperature = temperature;
    this.inputId = inputId;
    this.outputId = outputId;
    MaterialCastingLookup.registerFluid(this);
  }

  /** Checks if the recipe matches the given inventory */
  public boolean matches(ICastingContainer inv) {
    if (getOutput() == IMaterial.UNKNOWN || !fluid.test(inv.getFluid())) {
      return false;
    }
    if (inputId != null) {
      // if the input ID is null, want to avoid checking this
      // not null means we should have a material and it failed to find
      if (getInput() == IMaterial.UNKNOWN) {
        return false;
      }
      ItemStack stack = inv.getStack();
      return !stack.isEmpty() && IMaterialItem.getMaterialIdFromStack(stack).equals(getInputId());
    }
    return true;
  }

  /** Gets the amount of fluid to cast this recipe */
  public int getFluidAmount(Fluid fluid) {
    return this.fluid.getAmount(fluid);
  }

  /** Gets the material output for this recipe */
  public IMaterial getOutput() {
    if (!MaterialRegistry.isFullyLoaded()) {
      return IMaterial.UNKNOWN;
    }
    if (output == null) {
      output = MaterialRegistry.getMaterial(outputId);
    }
    return output;
  }

  /** Gets the material input for this recipe */
  @Nullable
  public IMaterial getInput() {
    if (inputId == null) {
      return null;
    }
    // prevent caching if the registry is not loaded
    if (!MaterialRegistry.isFullyLoaded()) {
      return IMaterial.UNKNOWN;
    }
    if (input == null) {
      input = MaterialRegistry.getMaterial(inputId);
    }
    return input;
  }

  /** Gets a list of fluids for display */
  public List<FluidStack> getFluids() {
    return fluid.getFluids();
  }

  @Override
  public final boolean matches(ICastingContainer inv, Level worldIn) {
    return matches(inv);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.materialFluidRecipe.get();
  }

  @Override
  public RecipeType<?> getType() {
    return RecipeTypes.DATA;
  }

  public static class Serializer extends LoggingRecipeSerializer<MaterialFluidRecipe> {
    @Override
    public MaterialFluidRecipe fromJson(ResourceLocation id, JsonObject json) {
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      int temperature = GsonHelper.getAsInt(json, "temperature");
      MaterialId input = null;
      if (json.has("input")) {
        input = new MaterialId(GsonHelper.getAsString(json, "input"));
      }
      MaterialId output = new MaterialId(GsonHelper.getAsString(json, "output"));
      return new MaterialFluidRecipe(id, fluid, temperature, input, output);
    }

    @Nullable
    @Override
    protected MaterialFluidRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      FluidIngredient fluid = FluidIngredient.read(buffer);
      int temperature = buffer.readInt();
      MaterialId input = null;
      if (buffer.readBoolean()) {
        input = new MaterialId(buffer.readUtf(Short.MAX_VALUE));
      }
      MaterialId output = new MaterialId(buffer.readUtf(Short.MAX_VALUE));
      return new MaterialFluidRecipe(id, fluid, temperature, input, output);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, MaterialFluidRecipe recipe) {
      recipe.fluid.write(buffer);
      buffer.writeInt(recipe.temperature);
      if (recipe.inputId != null) {
        buffer.writeBoolean(true);
        buffer.writeUtf(recipe.inputId.toString());
      } else {
        buffer.writeBoolean(false);
      }
      buffer.writeUtf(recipe.outputId.toString());
    }
  }
}
