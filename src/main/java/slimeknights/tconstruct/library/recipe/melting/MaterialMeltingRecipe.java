package slimeknights.tconstruct.library.recipe.melting;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.Comparator;
import java.util.List;

/**
 * Recipe to melt all castable tool parts of a given material
 */
public class MaterialMeltingRecipe implements IMeltingRecipe, IMultiRecipe<IDisplayableMeltingRecipe> {
  public static final RecordLoadable<MaterialMeltingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    MaterialVariantId.LOADABLE.requiredField("input", r -> r.input.getVariant()),
    IntLoadable.FROM_ONE.requiredField("temperature", r -> r.temperature),
    FluidOutput.Loadable.REQUIRED.requiredField("result", r -> r.result),
    FluidOutput.Loadable.REQUIRED.list(0).defaultField("byproducts", List.of(), false, r -> r.byproducts),
    MaterialMeltingRecipe::new);

  @Getter
  private final ResourceLocation id;
  private final MaterialVariant input;
  private final int temperature;
  private final FluidOutput result;
  private final List<FluidOutput> byproducts;

  public MaterialMeltingRecipe(ResourceLocation id, MaterialVariantId input, int temperature, FluidOutput result, List<FluidOutput> byproducts) {
    this.id = id;
    this.input = MaterialVariant.of(input);
    this.temperature = temperature;
    this.result = result;
    this.byproducts = byproducts;
  }

  /** @deprecated use {@link #MaterialMeltingRecipe(ResourceLocation,MaterialVariantId,int,FluidOutput,List)} */
  @Deprecated(forRemoval = true)
  public MaterialMeltingRecipe(ResourceLocation id, MaterialVariantId input, int temperature, FluidOutput result) {
    this(id, input, temperature, result, List.of());
  }

  @Override
  public boolean matches(IMeltingContainer inv, Level worldIn) {
    if (input.isUnknown()) {
      return false;
    }
    ItemStack stack = inv.getStack();
    if (stack.isEmpty() || MaterialCastingLookup.getItemCost(stack.getItem()) == 0) {
      return false;
    }
    return input.matchesVariant(stack);
  }

  @Override
  public int getTemperature(IMeltingContainer inv) {
    return temperature;
  }

  @Override
  public int getTime(IMeltingContainer inv) {
    int cost = MaterialCastingLookup.getItemCost(inv.getStack().getItem());
    return IMeltingRecipe.calcTimeForAmount(temperature, result.getAmount() * cost);
  }

  @Override
  public FluidStack getOutput(IMeltingContainer inv) {
    int cost = MaterialCastingLookup.getItemCost(inv.getStack().getItem());
    return new FluidStack(result.get(), result.getAmount() * cost);
  }

  @Override
  public void handleByproducts(IMeltingContainer inv, IFluidHandler handler) {
    if (!byproducts.isEmpty()) {
      int cost = MaterialCastingLookup.getItemCost(inv.getStack().getItem());
      for (FluidOutput byproduct : byproducts) {
        handler.fill(new FluidStack(byproduct.get(), byproduct.getAmount() * cost), FluidAction.EXECUTE);
      }
    }
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.materialMeltingSerializer.get();
  }


  /* JEI display */
  private List<IDisplayableMeltingRecipe> multiRecipes = null;

  @Override
  public List<IDisplayableMeltingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      if (input.get().isHidden()) {
        multiRecipes = List.of();
      } else {
        // grab and sort all parts that work
        MaterialVariantId inputId = input.getVariant();
        List<Entry<IMaterialItem>> entries = MaterialCastingLookup
          .getAllItemCosts().stream()
          .filter(entry -> entry.getKey().canUseMaterial(inputId.getId()))
          .sorted(Comparator.<Entry<IMaterialItem>,Integer>comparing(Entry::getIntValue).thenComparing(entry -> Loadables.ITEM.getKey(entry.getKey().asItem())))
          .toList();
        // if we found nothing, do nothing. Should never happen so error
        if (entries.isEmpty()) {
          TConstruct.LOG.error("Failed to create display recipe for {}: found no tool parts that support {}", id, inputId);
          multiRecipes = List.of();
        } else {
          // start building the recipe
          DisplayMeltingRecipe.Builder recipe = DisplayMeltingRecipe.id(id).temperature(temperature).timeDynamic();
          // input items just use the material
          recipe.inputs(entries.stream().map(entry -> entry.getKey().withMaterialForDisplay(inputId)).toList());
          // fluids
          FluidStack output = this.result.get();
          recipe.outputs(entries.stream().map(entry -> new FluidStack(output, output.getAmount() * entry.getIntValue())).toList());
          // if we have byproducts, scale those too
          for (FluidOutput byproduct : this.byproducts) {
            FluidStack fluid = byproduct.get();
            recipe.byproduct(entries.stream().map(entry -> new FluidStack(fluid, fluid.getAmount() * entry.getIntValue())).toList());
          }
          this.multiRecipes = List.of(recipe.build());
        }
      }
    }
    return multiRecipes;
  }
}
