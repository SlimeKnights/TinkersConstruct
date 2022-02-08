package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
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
  @Nullable @Getter
  private final MaterialVariant input;
  /** Output material ID */
  @Getter
  private final MaterialVariant output;

  public MaterialFluidRecipe(ResourceLocation id, FluidIngredient fluid, int temperature, @Nullable MaterialVariantId inputId, MaterialVariantId outputId) {
    this.id = id;
    this.fluid = fluid;
    this.temperature = temperature;
    this.input = inputId == null ? null : MaterialVariant.of(inputId);
    this.output = MaterialVariant.of(outputId);
    MaterialCastingLookup.registerFluid(this);
  }

  /** Checks if the recipe matches the given inventory */
  public boolean matches(ICastingContainer inv) {
    if (output.isUnknown() || !fluid.test(inv.getFluid())) {
      return false;
    }
    if (input != null) {
      // if the input ID is null, want to avoid checking this
      // not null means we should have a material and it failed to find
      if (input.isUnknown()) {
        return false;
      }
      return input.matchesVariant(inv.getStack());
    }
    return true;
  }

  /** Gets the amount of fluid to cast this recipe */
  public int getFluidAmount(Fluid fluid) {
    return this.fluid.getAmount(fluid);
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
      MaterialVariantId input = null;
      if (json.has("input")) {
        input = MaterialVariantId.fromJson(json, "input");
      }
      MaterialVariantId output = MaterialVariantId.fromJson(json, "output");
      return new MaterialFluidRecipe(id, fluid, temperature, input, output);
    }

    @Nullable
    @Override
    protected MaterialFluidRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      FluidIngredient fluid = FluidIngredient.read(buffer);
      int temperature = buffer.readInt();
      MaterialVariantId input = null;
      if (buffer.readBoolean()) {
        input = MaterialVariantId.parse(buffer.readUtf(Short.MAX_VALUE));
      }
      MaterialVariantId output = MaterialVariantId.parse(buffer.readUtf(Short.MAX_VALUE));
      return new MaterialFluidRecipe(id, fluid, temperature, input, output);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, MaterialFluidRecipe recipe) {
      recipe.fluid.write(buffer);
      buffer.writeInt(recipe.temperature);
      if (recipe.input != null) {
        buffer.writeBoolean(true);
        buffer.writeUtf(recipe.input.getVariant().toString());
      } else {
        buffer.writeBoolean(false);
      }
      buffer.writeUtf(recipe.output.getVariant().toString());
    }
  }
}
