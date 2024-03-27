package slimeknights.tconstruct.library.recipe.casting.material;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ICustomOutputRecipe;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.List;

/** Recipe defining casting and composite fluids for a given input */
public class MaterialFluidRecipe implements ICustomOutputRecipe<ICastingContainer> {
  public static final RecordLoadable<MaterialFluidRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    FluidIngredient.LOADABLE.requiredField("fluid", r -> r.fluid),
    IntLoadable.FROM_ONE.requiredField("temperature", r -> r.temperature),
    MaterialVariantId.LOADABLE.nullableField("input", r -> r.input != null ? r.input.getVariant() : null),
    MaterialVariantId.LOADABLE.nullableField("output", r -> r.output.getVariant()),
    MaterialFluidRecipe::new);

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
    return TinkerRecipeTypes.DATA.get();
  }
}
