package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import lombok.Getter;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.MaterialRegistry;
import slimeknights.tconstruct.library.materials.IMaterial;
import slimeknights.tconstruct.library.materials.MaterialId;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.tinkering.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.recipe.ICastingInventory;

import javax.annotation.Nullable;
import java.util.List;

/** Recipe defining casting and composite fluids for a given input */
public class MaterialFluidRecipe implements ICustomOutputRecipe<ICastingInventory> {
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
  public boolean matches(ICastingInventory inv) {
    if (!fluid.test(inv.getFluid())) {
      return false;
    }
    if (inputId != null) {
      ItemStack stack = inv.getStack();
      return !stack.isEmpty() && IMaterialItem.getMaterialFromStack(stack) == getInput();
    }
    return true;
  }

  /** Gets the amount of fluid to cast this recipe */
  public int getFluidAmount(Fluid fluid) {
    return this.fluid.getAmount(fluid);
  }

  /** Gets the material output for this recipe */
  public IMaterial getOutput() {
    if (output == null) {
      output = MaterialRegistry.getMaterial(outputId);
    }
    return output;
  }

  /** Gets the material input for this recipe */
  @Nullable
  public IMaterial getInput() {
    if (input == null && inputId != null) {
      input = MaterialRegistry.getMaterial(inputId);
    }
    return input;
  }

  /** Gets a list of fluids for display */
  public List<FluidStack> getFluids() {
    return fluid.getFluids();
  }

  @Override
  public final boolean matches(ICastingInventory inv, World worldIn) {
    return matches(inv);
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.materialFluidRecipe.get();
  }

  @Override
  public IRecipeType<?> getType() {
    return RecipeTypes.DATA;
  }

  public static class Serializer extends LoggingRecipeSerializer<MaterialFluidRecipe> {
    @Override
    public MaterialFluidRecipe read(ResourceLocation id, JsonObject json) {
      FluidIngredient fluid = FluidIngredient.deserialize(json, "fluid");
      int temperature = JSONUtils.getInt(json, "temperature");
      MaterialId input = null;
      if (json.has("input")) {
        input = new MaterialId(JSONUtils.getString(json, "input"));
      }
      MaterialId output = new MaterialId(JSONUtils.getString(json, "output"));
      return new MaterialFluidRecipe(id, fluid, temperature, input, output);
    }

    @Nullable
    @Override
    protected MaterialFluidRecipe readSafe(ResourceLocation id, PacketBuffer buffer) {
      FluidIngredient fluid = FluidIngredient.read(buffer);
      int temperature = buffer.readInt();
      MaterialId input = null;
      if (buffer.readBoolean()) {
        input = new MaterialId(buffer.readString(Short.MAX_VALUE));
      }
      MaterialId output = new MaterialId(buffer.readString(Short.MAX_VALUE));
      return new MaterialFluidRecipe(id, fluid, temperature, input, output);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, MaterialFluidRecipe recipe) {
      recipe.fluid.write(buffer);
      buffer.writeInt(recipe.temperature);
      if (recipe.inputId != null) {
        buffer.writeBoolean(true);
        buffer.writeString(recipe.inputId.toString());
      } else {
        buffer.writeBoolean(false);
      }
      buffer.writeString(recipe.outputId.toString());
    }
  }
}
