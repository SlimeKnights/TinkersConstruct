package slimeknights.tconstruct.library.recipe.partbuilder.recycle;

import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.materials.definition.MaterialVariant;
import slimeknights.tconstruct.library.recipe.partbuilder.DisplayPartRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderContainer;
import slimeknights.tconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import slimeknights.tconstruct.library.recipe.partbuilder.Pattern;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;

import static slimeknights.tconstruct.tables.recipe.PartBuilderToolRecycle.NO_MODIFIERS;

/** Recipe for recycling a vanilla tool or a tinkers tool with no materials in the part builder */
public class PartBuilderRecycle implements IPartBuilderRecipe, IMultiRecipe<DisplayPartRecipe> {
  /** Title for the screen */
  private static final Component RECYCLING = TConstruct.makeTranslation("recipe", "recycling");
  /** General instructions for recycling */
  private static final List<Component> INSTRUCTIONS = List.of(TConstruct.makeTranslation("recipe", "recycling.info"));
  /** Error when durability is too low */
  private static final List<Component> NOT_ENOUGH_DURABILITY = List.of(TConstruct.makeTranslation("recipe", "recycling.not_enough_durability").withStyle(ChatFormatting.RED));
  /** Error for trying to recycle a tool with enchantments */
  private static final List<Component> NO_ENCHANTMENTS = List.of(TConstruct.makeTranslation("recipe", "recycling.no_enchantments").withStyle(ChatFormatting.RED));

  /** Loader instance */
  public static final RecordLoadable<PartBuilderRecycle> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("tool", r -> r.tool),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("pattern", r -> r.pattern),
    Pattern.PARSER.mapWithValues(ItemOutput.Loadable.REQUIRED_STACK).requiredField("results", r -> r.results),
    PartBuilderRecycle::new);

  @Getter
  private final ResourceLocation id;
  private final Ingredient tool;
  private final Ingredient pattern;
  private final Map<Pattern,ItemOutput> results;
  private final int resultCount;

  public PartBuilderRecycle(ResourceLocation id, Ingredient tool, Ingredient pattern, Map<Pattern,ItemOutput> results) {
    this.id = id;
    this.tool = tool;
    this.pattern = pattern;
    this.results = results;
    this.resultCount = results.values().stream().mapToInt(ItemOutput::getCount).sum();
  }

  @Override
  public int getItemsUsed(IPartBuilderContainer inv) {
    return 1;
  }

  @Override
  public Pattern getPattern() {
    return MISSING;
  }

  @Override
  public Stream<Pattern> getPatterns(IPartBuilderContainer inv) {
    return results.keySet().stream();
  }

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    return pattern.test(inv.getPatternStack()) && tool.test(inv.getStack());
  }

  /** Scales the amount based on the tack damage */
  private static int getAmount(ItemStack stack, int max) {
    int maxDamage = stack.getMaxDamage();
    if (maxDamage == 0) {
      return max;
    }
    return max * (maxDamage - stack.getDamageValue()) / maxDamage;
  }

  @Override
  public boolean matches(IPartBuilderContainer inv, Level level) {
    ItemStack stack = inv.getStack();
    return partialMatch(inv) && getAmount(stack, resultCount) > 0 && stack.is(TinkerTags.Items.MODIFIABLE) ? !ModifierUtil.hasUpgrades(stack) : !stack.isEnchanted();
  }

  @Override
  public ItemStack assemble(IPartBuilderContainer inv, RegistryAccess access, Pattern pattern) {
    int maxCount = getAmount(inv.getStack(), resultCount);
    ItemOutput result = results.get(pattern);
    // should never happen
    if (result == null) {
      return ItemStack.EMPTY;
    }
    return result.get().copyWithCount(Math.min(maxCount, result.getCount()));
  }

  @Override
  public ItemStack getLeftover(IPartBuilderContainer inv, Pattern pattern) {
    // maximum items to return
    int maxCount = getAmount(inv.getStack(), resultCount);
    ItemOutput result = results.get(pattern);
    if (result != null) {
      maxCount -= result.getCount();
    }
    // if we have remaining items after removing the main choice, randomly choose a second
    if (maxCount > 0 && results.size() > 1) {
      List<ItemOutput> alternatives = results.entrySet().stream().filter(p -> p.getKey() != pattern).map(Entry::getValue).toList();
      if (!alternatives.isEmpty()) {
        ItemOutput chosen = alternatives.get(TConstruct.RANDOM.nextInt(alternatives.size()));
        return chosen.get().copyWithCount(Math.min(maxCount, chosen.getCount()));
      }
    }
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.partBuilderDamageableRecycling.get();
  }


  /* Unused */

  @Override
  public int getCost() {
    return 0;
  }

  /** @deprecated use {@link #assemble(IPartBuilderContainer, RegistryAccess, Pattern)} */
  @Deprecated
  @Override
  public ItemStack getResultItem(RegistryAccess access) {
    return ItemStack.EMPTY;
  }


  /* Display */

  @Nullable
  @Override
  public Component getTitle() {
    return RECYCLING;
  }

  @Override
  public List<Component> getText(IPartBuilderContainer inv) {
    ItemStack stack = inv.getStack();
    if (getAmount(stack, resultCount) <= 0) {
      return NOT_ENOUGH_DURABILITY;
    }
    if (stack.is(TinkerTags.Items.MODIFIABLE)) {
      if (ModifierUtil.hasUpgrades(stack)) {
        return NO_MODIFIERS;
      }
    } else if (stack.isEnchanted()) {
      return NO_ENCHANTMENTS;
    }
    return INSTRUCTIONS;
  }


  /* JEI */
  private List<DisplayPartRecipe> displayRecipes;

  @Override
  public List<DisplayPartRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      List<ItemStack> patternItems = List.of(pattern.getItems());
      List<ItemStack> toolItems = List.of(tool.getItems());
      displayRecipes = results.entrySet().stream()
        .map(entry -> new DisplayPartRecipe(id, MaterialVariant.UNKNOWN, entry.getKey(), patternItems, 0, toolItems, List.of(entry.getValue().get()))).toList();
    }
    return displayRecipes;
  }
}
