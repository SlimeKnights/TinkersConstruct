package slimeknights.tconstruct.tables.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.recipe.partbuilder.DisplayPartRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderContainer;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.definition.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolMaterialHook;
import slimeknights.tconstruct.library.tools.definition.module.material.ToolPartsHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Recipe to break a tool into tool parts.
 * TODO 1.21: move to {@link slimeknights.tconstruct.library.recipe.partbuilder.recycle}.
 */
@SuppressWarnings("deprecation")  // Forge is dumb
@RequiredArgsConstructor
public class PartBuilderToolRecycle implements IPartBuilderRecipe, IMultiRecipe<DisplayPartRecipe> {
  /** Title for the screen */
  private static final Component TOOL_RECYCLING = TConstruct.makeTranslation("recipe", "tool_recycling");
  /** General instructions for recycling */
  private static final List<Component> INSTRUCTIONS = Collections.singletonList(TConstruct.makeTranslation("recipe", "tool_recycling.info"));
  /** Error for trying to recycle a tool that cannot be */
  public static final List<Component> NO_MODIFIERS = Collections.singletonList(TConstruct.makeTranslation("recipe", "tool_recycling.no_modifiers").withStyle(ChatFormatting.RED));
  /** Default tool field */
  public static final SizedIngredient DEFAULT_TOOLS = SizedIngredient.fromTag(TinkerTags.Items.MULTIPART_TOOL);

  /** Loader instance */
  public static final RecordLoadable<PartBuilderToolRecycle> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    SizedIngredient.LOADABLE.defaultField("tools", DEFAULT_TOOLS, true, r -> r.toolRequirement),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("pattern", r -> r.pattern),
    TinkerLoadables.MATERIAL_ITEM.list(0).defaultField("parts", List.of(), r -> r.parts),
    PartBuilderToolRecycle::new);

  @Getter
  private final ResourceLocation id;
  private final SizedIngredient toolRequirement;
  private final Ingredient pattern;
  private final List<IMaterialItem> parts;

  /** @deprecated use {@link FinishedRecipe} */
  @Deprecated(forRemoval = true)
  public PartBuilderToolRecycle(ResourceLocation id, SizedIngredient toolRequirement, Ingredient pattern) {
    this(id, toolRequirement, pattern, List.of());
  }

  @Override
  public Pattern getPattern() {
    return MISSING;
  }

  @Override
  public Stream<Pattern> getPatterns(IPartBuilderContainer inv) {
    // use the parts override if set
    List<? extends IMaterialItem> parts = this.parts;
    if (parts.isEmpty() && inv.getStack().getItem() instanceof IModifiable modifiable) {
      parts = ToolPartsHook.parts(modifiable.getToolDefinition());
    }
    return parts.stream()
      .map(part -> BuiltInRegistries.ITEM.getKey(part.asItem()))
      .distinct()
      .map(Pattern::new);
  }

  @Override
  public int getCost() {
    return 0;
  }

  @Override
  public int getItemsUsed(IPartBuilderContainer inv) {
    return toolRequirement.getAmountNeeded();
  }

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    return pattern.test(inv.getPatternStack()) && toolRequirement.test(inv.getStack());
  }

  @Override
  public boolean matches(IPartBuilderContainer inv, Level pLevel) {
    return partialMatch(inv) && ToolStack.from(inv.getStack()).getUpgrades().isEmpty();
  }

  @Override
  public ItemStack assemble(IPartBuilderContainer inv, RegistryAccess access, Pattern pattern) {
    ToolStack tool = ToolStack.from(inv.getStack());
    // find our parts list, either set or override
    ToolDefinition definition = tool.getDefinition();
    List<? extends IMaterialItem> parts = this.parts;
    if (parts.isEmpty()) {
      parts = ToolPartsHook.parts(definition);
    }
    // ensure parts list is not greater than material count
    int materials = Math.min(ToolMaterialHook.stats(definition).size(), parts.size());
    // find part matching pattern
    IMaterialItem match = null;
    int matchIndex = -1;
    for (int i = 0; i < materials; i++) {
      IMaterialItem part = parts.get(i);
      if (pattern.equals(BuiltInRegistries.ITEM.getKey(part.asItem()))) {
        matchIndex = i;
        match = part;
        break;
      }
    }
    // failed to find part? should never happen but safety return
    if (match == null) {
      return ItemStack.EMPTY;
    }
    // special handling for oddball cases with ancient tool recycling
    MaterialVariantId variant = tool.getMaterial(matchIndex).getVariant();
    if (!match.canUseMaterial(variant.getId())) {
      return ItemStack.EMPTY;
    }
    return match.withMaterial(variant);
  }

  @Override
  public ItemStack getLeftover(IPartBuilderContainer inv, Pattern pattern) {
    ToolStack tool = ToolStack.from(inv.getStack());

    // if the tool is damaged, we only have a chance of a second tool part
    int damage = tool.getDamage();
    if (damage > 0) {
      int max = tool.getStats().getInt(ToolStats.DURABILITY);
      if (TConstruct.RANDOM.nextInt(max) < damage) {
        return ItemStack.EMPTY;
      }
    }

    // find our parts list, either set or override
    ToolDefinition definition = tool.getDefinition();
    List<? extends IMaterialItem> requirements = this.parts;
    if (requirements.isEmpty()) {
      requirements = ToolPartsHook.parts(definition);
    }
    // ensure parts list is not greater than material count
    int materials = Math.min(ToolMaterialHook.stats(definition).size(), requirements.size());
    // find all parts that did not match the pattern
    IntList indices = new IntArrayList();
    boolean found = false;
    List<IMaterialItem> parts = new ArrayList<>();
    for (int i = 0; i < materials; i++) {
      IMaterialItem part = requirements.get(i);
      if (found || !pattern.equals(BuiltInRegistries.ITEM.getKey(part.asItem()))) {
        parts.add(part);
        indices.add(i);
      } else {
        found = true;
      }
    }
    if (parts.isEmpty()) {
      return ItemStack.EMPTY;
    }
    int index = TConstruct.RANDOM.nextInt(parts.size());
    return parts.get(index).withMaterial(tool.getMaterial(indices.getInt(index)).getVariant());
  }

  /** @deprecated use {@link IPartBuilderRecipe#assemble(IPartBuilderContainer, RegistryAccess, Pattern)} */
  @Deprecated
  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.partBuilderToolRecycling.get();
  }

  @Nullable
  @Override
  public Component getTitle() {
    return TOOL_RECYCLING;
  }

  @Override
  public List<Component> getText(IPartBuilderContainer inv) {
    return ModifierUtil.hasUpgrades(inv.getStack()) ? NO_MODIFIERS : INSTRUCTIONS;
  }


  /* JEI */
  private List<DisplayPartRecipe> displayRecipes;

  private record PartIndex(IMaterialItem part, int index) {};

  /** Helper handling both cases of making recipes */
  private Stream<DisplayPartRecipe> makeRecipes(List<? extends IMaterialItem> parts, List<ItemStack> patternItems, List<ItemStack> tool) {
    Collection<PartIndex> displayParts = IntStream.range(0, parts.size()).mapToObj(i -> new PartIndex(parts.get(i), i)).collect(Collectors.toMap(PartIndex::part, Function.identity(), (a, b) -> a)).values();
    return displayParts.stream().map(pi -> {
      ItemStack part = pi.part.withMaterialForDisplay(ToolBuildHandler.getRenderMaterial(pi.index));
      part.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
      return new DisplayPartRecipe(id, MaterialVariant.UNKNOWN, new Pattern(Loadables.ITEM.getKey(pi.part.asItem())), patternItems, 0, tool, List.of(part));
    });
  }

  @Override
  public List<DisplayPartRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      List<ItemStack> patternItems = List.of(this.pattern.getItems());
      // if we have parts, will be using the same list for all tools, so make just 1 recipe per part
      if (!parts.isEmpty()) {
        List<ItemStack> tools = toolRequirement.getMatchingStacks().stream().map(IModifiableDisplay::getDisplayStack).toList();
        displayRecipes = makeRecipes(parts, patternItems, tools).toList();
      } else {
        // no parts? make a recipe per tool per part
        displayRecipes = toolRequirement.getMatchingStacks().stream().flatMap(stack -> {
          if (stack.getItem() instanceof IModifiable modifiable) {
            return makeRecipes(ToolPartsHook.parts(modifiable.getToolDefinition()), patternItems, List.of(IModifiableDisplay.getDisplayStack(stack)));
          }
          return Stream.empty();
        }).toList();
      }
    }
    return displayRecipes;
  }

  /** @deprecated use {@link slimeknights.tconstruct.library.recipe.partbuilder.recycle.PartBuilderToolRecycleBuilder} */
  @Deprecated(forRemoval = true)
  public record Finished(ResourceLocation getId, SizedIngredient tools, Ingredient pattern) implements FinishedRecipe {
    @Override
    public void serializeRecipeData(JsonObject json) {
      json.add("tools", SizedIngredient.LOADABLE.serialize(tools));
      json.add("pattern", pattern.toJson());
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerTables.partBuilderToolRecycling.get();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
      return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
      return null;
    }
  }
}
