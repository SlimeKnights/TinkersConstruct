package slimeknights.tconstruct.library.recipe.casting.material;

import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.mantle.recipe.helper.TypeAwareRecipeSerializer;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.json.predicate.material.MaterialPredicate;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.recipe.casting.CastingRecipeLookup;
import slimeknights.tconstruct.library.recipe.casting.DisplayCastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ICastingContainer;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.MaterialNBT;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

/** Recipe for casting a tool using molten metal on either a tool part or a non-tool part (2 materials or 1) */
public class ToolCastingRecipe extends PartSwapCastingRecipe implements IMultiRecipe<IDisplayableCastingRecipe> {
  public static final RecordLoadable<ToolCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(),
    ContextKey.ID.requiredField(), LoadableRecipeSerializer.RECIPE_GROUP, CAST_FIELD, ITEM_COST_FIELD,
    new EnumLoadable<>(CastPurpose.class).defaultField("cast_purpose", CastPurpose.MAYBE_MATERIAL, true, r -> r.castPurpose),
    TinkerLoadables.MODIFIABLE_ITEM.requiredField("result", r -> r.result),
    MATERIALS_FIELD,
    MaterialVariantId.LOADABLE.list(0).defaultField("extra_materials", List.of(), false, r -> r.extraMaterials),
    ToolCastingRecipe::new);

  private final IModifiable result;
  private final CastPurpose castPurpose;
  /** List of materials to add after the cast and fluid */
  private final List<MaterialVariantId> extraMaterials;

  protected ToolCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, CastPurpose castPurpose, IModifiable result, IJsonPredicate<MaterialVariantId> allowedMaterials, List<MaterialVariantId> extraMaterials) {
    super(serializer, id, group, cast, itemCost, castPurpose.swapIndex, allowedMaterials);
    this.result = result;
    this.castPurpose = castPurpose;
    this.extraMaterials = extraMaterials;
    CastingRecipeLookup.registerCastable(result);
  }

  /** @deprecated use {@link #ToolCastingRecipe(TypeAwareRecipeSerializer, ResourceLocation, String, Ingredient, int, CastPurpose, IModifiable, IJsonPredicate, List)} */
  @Deprecated(forRemoval = true)
  public ToolCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, int itemCost, IModifiable result) {
    this(serializer, id, group, cast, itemCost, CastPurpose.MAYBE_MATERIAL, result, MaterialPredicate.ANY, List.of());
  }

  @Override
  protected MaterialFluidRecipe getFluidRecipe(ICastingContainer inv) {
    // if its not part swapping, original lookup is best
    if (inv.getStack().getItem() != result.asItem()) {
      return MaterialCastingLookup.getCastingFluid(inv.getFluid(), materials);
    }
    return super.getFluidRecipe(inv);
  }

  @Override
  public boolean matches(ICastingContainer inv, Level level) {
    ItemStack cast = inv.getStack();
    // if the tool matches, perform part swapping
    if (cast.getItem() == result.asItem()) {
      return canPartSwap(inv);
    }
    // no tool match? need to check cast and fluid
    if (!this.getCast().test(cast)) {
      return false;
    }
    // if we have a material item input, must have exactly 2 materials, else exactly 1
    List<MaterialStatsId> requirements = ToolMaterialHook.stats(result.getToolDefinition());
    // last material is the part, may be index 0 or 1
    MaterialFluidRecipe recipe = getFluidRecipe(inv);
    return recipe != MaterialFluidRecipe.EMPTY && requirements.get(castPurpose == CastPurpose.MAYBE_MATERIAL ? requirements.size() - 1 : castPurpose.swapIndex).canUseMaterial(recipe.getOutput().getId());
  }

  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return new ItemStack(result);
  }

  @Override
  public ItemStack assemble(ICastingContainer inv, RegistryAccess access) {
    // if the cast is the result, we are part swapping, replace the last material
    ItemStack cast = inv.getStack();
    if (cast.getItem() == result) {
      return super.assemble(inv, access);
    } else {
      // figure out how to apply our materials
      MaterialVariant fluidMaterial = getFluidRecipe(inv).getOutput();
      MaterialNBT.Builder materials = MaterialNBT.builder();

      // if the cast material goes second, need our material now
      if (castPurpose == CastPurpose.SECOND_MATERIAL) {
        materials.add(fluidMaterial);
      }
      // add cast material if relevant
      if (castPurpose == CastPurpose.FIRST_MATERIAL || castPurpose == CastPurpose.SECOND_MATERIAL
        || castPurpose == CastPurpose.MAYBE_MATERIAL && ToolMaterialHook.stats(result.getToolDefinition()).size() > 1) {
        materials.add(IMaterialItem.getMaterialFromStack(cast));
      }
      // add fluid material
      if (castPurpose != CastPurpose.SECOND_MATERIAL) {
        materials.add(fluidMaterial);
      }
      // add extra materials
      materials.add(extraMaterials);
      return ToolBuildHandler.buildItemFromMaterials(result, materials.build());
    }
  }

  @Override
  public boolean isConsumed(ICastingContainer inv) {
    // if part swapping, always consume the input
    return castPurpose != CastPurpose.CATALYST || inv.getStack().getItem() == result.asItem();
  }


  /* JEI display */

  @Override
  public List<IDisplayableCastingRecipe> getRecipes(RegistryAccess access) {
    if (multiRecipes == null) {
      List<MaterialStatsId> requirements = ToolMaterialHook.stats(result.getToolDefinition());
      if (requirements.isEmpty()) {
        multiRecipes = List.of();
      } else {
        MaterialVariant dummyRequirement = MaterialVariant.of(ToolBuildHandler.getRenderMaterial(0));
        // if we have two item requirements, fill in the part in display
        BiFunction<MaterialVariant,List<ItemStack>,List<ItemStack>> materials;
        MaterialNBT.Builder partSwapMaterials = new MaterialNBT.Builder();

        // legacy support: determine the function of the cast when set to maybe
        CastPurpose castPurpose = this.castPurpose;
        MaterialStatsId requirement;
        if (castPurpose == CastPurpose.MAYBE_MATERIAL) {
          castPurpose = requirements.size() > 1 ? CastPurpose.FIRST_MATERIAL : CastPurpose.CONSUMED;
          requirement = requirements.get(requirements.size() - 1);
          // if the cast is the first material, use index 1 for the output requirement, though skip if invalid tool definition
        } else if (castPurpose == CastPurpose.FIRST_MATERIAL && requirements.size() > 1) {
          requirement = requirements.get(1);
        } else {
          requirement = requirements.get(0);
        }

        // if we have a cast material, add it to display stacks
        boolean first = castPurpose == CastPurpose.FIRST_MATERIAL;
        if (first || castPurpose == CastPurpose.SECOND_MATERIAL) {
          MaterialVariant castMaterial = MaterialVariant.of(MaterialRegistry.firstWithStatType(requirements.get(1 - castPurpose.swapIndex)));
          materials = (mat, casts) -> casts.stream().map(cast -> {
            MaterialNBT.Builder builder = MaterialNBT.builder();
            // if the cast is second, add the fluid material first
            if (!first) {
              builder.add(mat);
            }
            // if the material is unknown, just use the first; deals with the fact the tool is an extra cast for showing part swapping
            MaterialVariantId id = IMaterialItem.getMaterialFromStack(cast);
            builder.add(id == IMaterial.UNKNOWN_ID ? castMaterial : MaterialVariant.of(id));
            // if the cast is first, add the fluid material second
            if (first) {
              builder.add(mat);
            }
            return ToolBuildHandler.buildItemFromMaterials(result, builder.add(extraMaterials).build());
          }).toList();
          // add materials to the part swap marker
          if (first) {
            partSwapMaterials.add(castMaterial).add(dummyRequirement);
          } else {
            partSwapMaterials.add(dummyRequirement).add(castMaterial);
          }
        } else {
          // no cast material? just show the fluid material
          materials = (mat, casts) -> List.of(ToolBuildHandler.buildItemFromMaterials(result, MaterialNBT.builder().add(mat).add(extraMaterials).build()));
          partSwapMaterials.add(dummyRequirement);
        }

        // build part swap tool, mark as display so tooltip does not show useless stats
        ItemStack partSwapDisplay = ToolBuildHandler.buildItemFromMaterials(result, partSwapMaterials.add(extraMaterials).build());
        partSwapDisplay.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);

        List<ItemStack> casts = List.of(getCast().getItems());
        // if the cast is consumed, add the tool to the list of cast items to show that part swapping is an option
        boolean consumed = castPurpose != CastPurpose.CATALYST;
        List<ItemStack> castsWithTool = consumed ? Streams.concat(casts.stream(), Stream.of(partSwapDisplay)).toList() : casts;
        List<ItemStack> partSwapList = consumed ? List.of() : List.of(partSwapDisplay);

        // start building recipes
        List<IDisplayableCastingRecipe> recipes = new ArrayList<>();
        Predicate<MaterialFluidRecipe> validRecipe = recipe -> {
          MaterialVariant output = recipe.getOutput();
          return recipe.isVisible() && requirement.canUseMaterial(output.getId()) && this.materials.matches(output.getVariant());
        };

        // show recipes for creating the tool from all castable fluids
        List<MaterialFluidRecipe> validCasting = MaterialCastingLookup.getAllCastingFluids().stream().filter(validRecipe).toList();
        for (MaterialFluidRecipe recipe : validCasting) {
          List<FluidStack> fluids = resizeFluids(recipe.getFluids());
          int amount = itemCost * getFluidAmount(fluids);
          recipes.add(new DisplayCastingRecipe(getId(), getType(), castsWithTool, fluids, materials.apply(recipe.getOutput(), castsWithTool),
            ICastingRecipe.calcCoolingTime(recipe.getTemperature(), amount), consumed));

          // if the cast is not consumed, then part swapping will have to be done separately for the proper consumed flag
          if (!consumed) {
            recipes.add(new DisplayCastingRecipe(getId(), getType(), partSwapList, fluids, materials.apply(recipe.getOutput(), partSwapList),
              ICastingRecipe.calcCoolingTime(recipe.getTemperature(), amount), true));
          }
        }

        // all composite fluids become special composite swapping recipes
        MaterialCastingLookup.getAllCompositeFluids().stream()
          .filter(validRecipe)
          .map(recipe -> {
            List<FluidStack> fluids = resizeFluids(recipe.getFluids());
            return new DisplayCastingRecipe(getId(), getType(), materials.apply(recipe.getInput(), casts), fluids, materials.apply(recipe.getOutput(), casts),
              ICastingRecipe.calcCoolingTime(recipe.getTemperature(), itemCost * getFluidAmount(fluids)), true);
          }).forEach(recipes::add);
        multiRecipes = List.copyOf(recipes);
      }
    }
    return multiRecipes;
  }

  /** Enum describing the function of the cast in this recipe */
  @RequiredArgsConstructor
  public enum CastPurpose {
    /**
     * Based on the material definition stat count, cast is either the first material or has no material purpose.
     * @deprecated use {@link #CONSUMED} or {@link #FIRST_MATERIAL}.
     */
    @Deprecated
    MAYBE_MATERIAL(-1),
    /** Cast is not consumed by the recipe */
    CATALYST(0),
    /** Cast is consumed, but has no material purpose */
    CONSUMED(0),
    /** Cast is consumed, and becomes the first material with the fluid the second. */
    FIRST_MATERIAL(1),
    /** Cast is consumed, and becomes the second material with the fluid the first. */
    SECOND_MATERIAL(0);

    /** Index for part swapping */
    private final int swapIndex;
  }
}
