package slimeknights.tconstruct.tables.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialCastingLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.MaterialSwappingRecipe;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.BitSet;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Recipe that replaces a tool part with another
 */
public class TinkerStationPartSwapping extends MaterialSwappingRecipe {
  public static final RecordLoadable<TinkerStationPartSwapping> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), TOOLS_FIELD, STACK_SIZE_FIELD, EXTRA_REQUIREMENTS_FIELD, TinkerStationPartSwapping::new);

  protected TinkerStationPartSwapping(ResourceLocation id, Ingredient tools, int maxStackSize, List<SizedIngredient> extraRequirements) {
    super(id, tools, maxStackSize, extraRequirements);
  }

  /** @deprecated use {@link #TinkerStationPartSwapping(ResourceLocation, Ingredient, int, List)} */
  @Deprecated(forRemoval = true)
  public TinkerStationPartSwapping(ResourceLocation id, Ingredient tools, int maxStackSize) {
    this(id, tools, maxStackSize, List.of());
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    ItemStack tinkerable = inv.getTinkerableStack();
    if (tinkerable.isEmpty() || !tools.test(tinkerable) || !(tinkerable.getItem() instanceof IModifiable modifiable)) {
      return false;
    }
    // get the list of parts, empty means its not multipart
    List<IToolPart> parts = ToolPartsHook.parts(modifiable.getToolDefinition());
    if (parts.isEmpty()) {
      return false;
    }

    BitSet used = ModifierRecipe.makeBitset(inv);
    // we have two concerns on part swapping:
    // part must be valid in the tool
    boolean foundItem = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && stack.getItem() instanceof IToolPart part) {
        // if the part is not in the list, don't bother with this recipe even if another part mathces later
        if (!parts.contains(part)) {
          return false;
        }
        foundItem = true;
        used.set(i);
        break;
      }
    }
    // extra requiremenets ensures we do not try to swap multiple parts
    return foundItem && ModifierRecipe.checkMatch(inv, extraRequirements, used);
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    // copy the tool NBT to ensure the original tool is intact
    List<IToolPart> parts = ToolPartsHook.parts(inv.getTinkerable().getDefinition());

    // prevent part swapping on large tools in small tables
    if (parts.size() > inv.getInputCount()) {
      return TOO_MANY_PARTS;
    }

    // actual part swap logic
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && stack.getItem() instanceof IToolPart part) {
        // ensure the part is valid
        MaterialVariantId partVariant = part.getMaterial(stack);
        if (partVariant.equals(IMaterial.UNKNOWN_ID)) {
          return RecipeResult.pass();
        }

        // we have a part and its not at this index, find the first copy of this part
        // means slot only matters if a tool uses a part twice
        int index = i;
        if (i >= parts.size() || parts.get(i) != part) {
          index = IntStream.range(0, parts.size())
                           .filter(pi -> parts.get(pi) == part)
                           .findFirst().orElse(-1);
          if (index == -1) {
            return RecipeResult.pass();
          }
        }
        return swapMaterial(inv, partVariant, index, MaterialCastingLookup.getItemCost(part));
      }
    }
    // no item found, should never happen
    return RecipeResult.pass();
  }

  @Override
  protected boolean shrinkPart(IMutableTinkerStationContainer inv, int index, ItemStack stack) {
    if (stack.getItem() instanceof IToolPart) {
      inv.shrinkInput(index, 1);
      return true;
    }
    return false;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.tinkerStationPartSwappingSerializer.get();
  }
}
