package slimeknights.tconstruct.tools.recipe;

import lombok.Getter;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.armortrim.TrimPatterns;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modifiers.slotless.TrimModifier;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ArmorTrimRecipe implements ITinkerStationRecipe, IMultiRecipe<IDisplayModifierRecipe> {
  protected static final String KEY_INVALID_MATERIAL = TConstruct.makeTranslationKey("recipe", "modifier.armor_trim.invalid_material");
  protected static final String KEY_INVALID_PATTERN = TConstruct.makeTranslationKey("recipe", "modifier.armor_trim.invalid_pattern");


  @Getter
  private final ResourceLocation id;

  public ArmorTrimRecipe(ResourceLocation id) {
    this.id = id;
    ModifierRecipeLookup.addRecipeModifier(null, TinkerModifiers.trim);
  }

  /** Match for the trim item finding method */
  private record TrimItems(ItemStack template, ItemStack material) {}

  /** Finds the trim template and material */
  @Nullable
  private static TrimItems findInputs(ITinkerStationContainer inv) {
    ItemStack template = ItemStack.EMPTY;
    ItemStack material = ItemStack.EMPTY;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        // find the two matching tags, but ensure no duplicates
        if (stack.is(ItemTags.TRIM_TEMPLATES)) {
          if (!template.isEmpty()) {
            return null;
          }
          template = stack;
        }
        if (stack.is(ItemTags.TRIM_MATERIALS)) {
          if (!material.isEmpty()) {
            return null;
          }
          material = stack;
        }
      }
    }
    // if we found both, we match
    if (!material.isEmpty() && !template.isEmpty()) {
      return new TrimItems(template, material);
    }
    return null;
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    // ensure this modifier can be applied
    if (!inv.getTinkerableStack().is(TinkerTags.Items.TRIM)) {
      return false;
    }
    // need to locate two things: the trim material, and the trim template
    return findInputs(inv) != null;
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    // first need to find our trim and material instances
    TrimItems trimItems = findInputs(inv);
    // should never happen
    if (trimItems == null) {
      return RecipeResult.pass();
    }
    // validate the material nad pattern items
    Optional<Reference<TrimMaterial>> material = TrimMaterials.getFromIngredient(access, trimItems.material);
    if (material.isEmpty()) {
      return RecipeResult.failure(KEY_INVALID_MATERIAL, trimItems.material.getDisplayName());
    }
    Optional<Reference<TrimPattern>> pattern = TrimPatterns.getFromTemplate(access, trimItems.template);
    if (pattern.isEmpty()) {
      return RecipeResult.failure(KEY_INVALID_PATTERN, trimItems.template.getDisplayName());
    }

    // store into tool NBT
    ToolStack tool = inv.getTinkerable().copy();
    ModDataNBT persistentData = tool.getPersistentData();
    persistentData.putString(TrimModifier.TRIM_MATERIAL, material.get().key().location().toString());
    persistentData.putString(TrimModifier.TRIM_PATTERN, pattern.get().key().location().toString());

    // add the modifier if missing
    ModifierId modifier = TinkerModifiers.trim.getId();
    if (tool.getModifierLevel(modifier) == 0) {
      tool.addModifier(modifier, 1);
    }

    return RecipeResult.success(LazyToolStack.from(tool, Math.min(inv.getTinkerableSize(), shrinkToolSlotBy())));
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.armorTrimSerializer.get();
  }


  /* JEI */

  private List<IDisplayModifierRecipe> displayRecipes = null;

  @SuppressWarnings("deprecation")
  @Override
  public List<IDisplayModifierRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      List<ItemStack> trims = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, ItemTags.TRIM_TEMPLATES)
                                            .map(ItemStack::new).toList();
      List<ItemStack> toolInputs = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, TinkerTags.Items.TRIM)
                                                 .map(IModifiableDisplay::getDisplayStack).toList();
      displayRecipes = access.registryOrThrow(Registries.TRIM_MATERIAL).holders()
                             .map(material -> new DisplayRecipe(toolInputs, trims, material))
                             .collect(Collectors.toList());
    }
    return displayRecipes;
  }

  private static class DisplayRecipe implements IDisplayModifierRecipe {
    private static final IntRange LEVELS = new IntRange(1, 1);
    private final ModifierEntry RESULT = new ModifierEntry(TinkerModifiers.trim, 1);

    @Getter
    private final List<ItemStack> toolWithoutModifier;
    @Getter
    private final List<ItemStack> toolWithModifier;
    private final List<ItemStack> trim;
    private final List<ItemStack> material;
    @Getter
    private final Component variant;

    public DisplayRecipe(List<ItemStack> tools, List<ItemStack> trim, Reference<TrimMaterial> holder) {
      TrimMaterial material = holder.get();
      toolWithoutModifier = tools;
      this.trim = trim;
      this.material = List.of(new ItemStack(material.ingredient().get()));
      this.variant = material.description().plainCopy();

      String materialName = holder.key().location().toString();
      List<ModifierEntry> results = List.of(RESULT);
      toolWithModifier = tools.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, results, data -> data.putString(TrimModifier.TRIM_MATERIAL, materialName))).toList();

    }

    @Override
    public int getInputCount() {
      return 2;
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot) {
      return switch (slot) {
        case 0 -> trim;
        case 1 -> material;
        default -> List.of();
      };
    }

    @Override
    public ModifierEntry getDisplayResult() {
      return RESULT;
    }

    @Override
    public IntRange getLevel() {
      return LEVELS;
    }
  }
}
