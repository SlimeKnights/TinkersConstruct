package slimeknights.tconstruct.tools.recipe;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.helper.LoadableRecipeSerializer;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/** Recipe for transforming tipped arrows into a tool */
public class TippedToolTransformRecipe extends ToolBuildingRecipe {
  /** Loader instance */
  public static final RecordLoadable<TippedToolTransformRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP, RESULT_FIELD, LAYOUT_FIELD,
    IngredientLoadable.DISALLOW_EMPTY.requiredField("input", r -> r.ingredients.get(0)),
    MaterialVariantId.LOADABLE.list(0).defaultField("materials", List.of(), false, r -> r.materials),
    ModifierId.PARSER.requiredField("modifier", r -> r.modifier),
    TippedToolTransformRecipe::new);

  protected final ModifierId modifier;
  public TippedToolTransformRecipe(ResourceLocation id, String group, IModifiable output, @Nullable ResourceLocation layoutSlot, Ingredient ingredient, List<MaterialVariantId> materials, ModifierId modifier) {
    super(id, group, output, 1, layoutSlot, List.of(ingredient), List.of(), materials);
    this.modifier = modifier;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.tippedToolTransformRecipeSerializer.get();
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    RecipeResult<LazyToolStack> result = super.getValidatedResult(inv, access);
    if (result.isSuccess()) {
      // tool must have modifier, else we are adding bad data
      IToolStackView tool = result.getResult().getTool();
      if (tool.getModifierLevel(modifier) > 0) {
        // find the potion, should be first non-empty stack
        ItemStack stack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getInputCount(); i++) {
          stack = inv.getInput(i);
          if (!stack.isEmpty()) {
            break;
          }
        }
        // if we found one, set its NBT into the result tool
        if (!stack.isEmpty()) {
          CompoundTag tag = stack.getTag();
          if (tag != null && tag.contains(PotionUtils.TAG_POTION, Tag.TAG_STRING)) {
            tool.getPersistentData().putString(modifier, tag.getString(PotionUtils.TAG_POTION));
          }
        }
      }
    }
    return result;
  }

  @Override
  public List<ItemStack> getDisplayOutput() {
    if (displayOutput == null) {
      ItemStack result = super.getDisplayOutput().get(0);
      displayOutput = Arrays.stream(ingredients.get(0).getItems())
        .map(stack -> {
          CompoundTag tag = stack.getTag();
          if (tag != null) {
            ItemStack copy = result.copy();
            ToolStack.from(copy).getPersistentData().putString(modifier, tag.getString(PotionUtils.TAG_POTION));
            return copy;
          }
          return result;
        }).toList();
    }
    return displayOutput;
  }
}
