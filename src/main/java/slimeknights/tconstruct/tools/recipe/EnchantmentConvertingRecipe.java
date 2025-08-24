package slimeknights.tconstruct.tools.recipe;

import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.json.predicate.modifier.ModifierPredicate;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.recipe.ITinkerableContainer;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import slimeknights.tconstruct.library.recipe.worktable.AbstractWorktableRecipe;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Recipe for converting enchanted books into modifier crystals */
public class EnchantmentConvertingRecipe extends AbstractWorktableRecipe {
  private static final Component DESCRIPTION_LOST = TConstruct.makeTranslation("recipe", "enchantment_converting.description.lost");
  private static final Component DESCRIPTION_KEEP = TConstruct.makeTranslation("recipe", "enchantment_converting.description.keep");
  private static final Component NO_ENCHANTMENT = TConstruct.makeTranslation("recipe", "enchantment_converting.no_enchantments");
  private static final RecipeResult<LazyToolStack> TOO_FEW = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "enchantment_converting.too_few"));
  /** Loader instance */
  public static final RecordLoadable<EnchantmentConvertingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    StringLoadable.DEFAULT.requiredField("name", r -> r.name),
    INPUTS_FIELD,
    BooleanLoadable.INSTANCE.requiredField("match_book", r -> r.matchBook),
    BooleanLoadable.INSTANCE.requiredField("return_unenchanted", r -> r.returnInput),
    ModifierPredicate.LOADER.defaultField("modifier_predicate", false, r -> r.modifierPredicate),
    EnchantmentConvertingRecipe::new);

  /** Name of recipe, used for title */
  private final String name;
  /** Cached title component */
  @Getter
  private final Component title;
  /** If true, matches enchanted books. If false, matches tools */
  private final boolean matchBook;
  /** If true, the input book/tool is returned, disenchanted */
  private final boolean returnInput;

  /** Modifiers valid for this recipe */
  private final IJsonPredicate<ModifierId> modifierPredicate;

  private List<ModifierEntry> displayModifiers;

  public EnchantmentConvertingRecipe(ResourceLocation id, String name, List<SizedIngredient> inputs, boolean matchBook, boolean returnInput, IJsonPredicate<ModifierId> modifierPredicate) {
    super(id, inputs);
    this.name = name;
    this.title = Component.translatable(ExtractModifierRecipe.BASE_KEY + "." + name);
    this.matchBook = matchBook;
    this.returnInput = returnInput;
    this.modifierPredicate = modifierPredicate;
  }

  /** Gets the enchantment map from the given stack */
  private Map<Enchantment,Integer> getEnchantments(ItemStack stack) {
    return EnchantmentHelper.deserializeEnchantments(matchBook ? EnchantedBookItem.getEnchantments(stack) : stack.getEnchantmentTags());
  }


  /* Text */

  @Override
  public Component getDescription(@Nullable ITinkerableContainer inv) {
    // ensure we have at least one supported enchantment
    if (inv != null && getEnchantments(inv.getTinkerableStack()).entrySet().stream().noneMatch(entry -> {
      Modifier modifier = ModifierManager.INSTANCE.get(entry.getKey());
      return modifier != null && modifierPredicate.matches(modifier.getId());
    })) {
      return NO_ENCHANTMENT;
    }
    return returnInput ? DESCRIPTION_KEEP : DESCRIPTION_LOST;
  }


  /* Logic */

  @Override
  public boolean matches(ITinkerableContainer inv, Level world) {
    ItemStack tool = inv.getTinkerableStack();
    if (matchBook) {
      if (!tool.is(Items.ENCHANTED_BOOK)) {
        return false;
      }
      // call the method directly on item as the method on itemstack conisiders if its current enchanted
      // we want to match even unenchanted items, better error
    } else if (!tool.getItem().isEnchantable(tool)) {
      return false;
    }
    return ModifierRecipe.checkMatch(inv, inputs);
  }

  /** Gets a list of all modifiers that match this recipe, set to level 1 for display */
  private List<ModifierEntry> getMatchingModifiers() {
    return ModifierRecipeLookup.getAllRecipeModifiers()
      .filter(modifier -> modifierPredicate.matches(modifier.getId()) && ModifierManager.INSTANCE.hasEnchantment(modifier))
      .map(mod -> new ModifierEntry(mod, 1)).toList();
  }

  @Override
  public List<ModifierEntry> getModifierOptions(@Nullable ITinkerableContainer inv) {
    if (inv != null) {
      // map all enchantments to an equal level modifier
      return getEnchantments(inv.getTinkerableStack()).entrySet().stream().map(entry -> {
        Modifier modifier = ModifierManager.INSTANCE.get(entry.getKey());
        if (modifier != null && modifierPredicate.matches(modifier.getId())) {
          return new ModifierEntry(modifier, returnInput ? 1 : entry.getValue());
        }
        return null;
      }).filter(Objects::nonNull).distinct().toList();
    }
    if (displayModifiers == null) {
      if (matchBook) {
        Set<ModifierId> modifiers = getMatchingModifiers().stream().map(ModifierEntry::getId).collect(Collectors.toSet());
        Modifier defaultModifier = ModifierManager.INSTANCE.getDefaultValue();
        displayModifiers = ModifierManager.INSTANCE.getEquivalentEnchantments(modifiers::contains)
          .flatMap(enchantment -> IntStream.rangeClosed(1, enchantment.getMaxLevel())
            .mapToObj(level -> new ModifierEntry(Objects.requireNonNullElse(ModifierManager.INSTANCE.get(enchantment), defaultModifier), level)))
          .toList();
      } else {
        displayModifiers = getMatchingModifiers();
      }
    }
    return displayModifiers;
  }

  @Override
  public RecipeResult<LazyToolStack> getResult(ITinkerableContainer inv, ModifierEntry modifier) {
    // first, ensure we have enough items for counts above 1
    int level = modifier.getLevel();
    if (level > 1) {
      int used = -1;
      inputLoop:
      for (SizedIngredient ingredient : inputs) {
        for (int i = 0; i < inv.getInputCount(); i++) {
          if (i != used) {
            ItemStack stack = inv.getInput(i);
            if (!stack.isEmpty() && ingredient.getAmountNeeded() * level <= stack.getCount() && ingredient.test(stack)) {
              used = i;
              continue inputLoop;
            }
          }
        }
        return TOO_FEW;
      }
    }
    return LazyToolStack.success(ModifierCrystalItem.withModifier(modifier.getId(), level));
  }

  @Override
  public int shrinkToolSlotBy(LazyToolStack result) {
    return 1;
  }

  @Override
  public void updateInputs(LazyToolStack result, ITinkerableContainer.Mutable inv, ModifierEntry selected, boolean isServer) {
    // consume inputs once per selected item
    for (int i = 0; i < selected.getLevel(); i++) {
      ModifierRecipe.updateInputs(inv, inputs);
    }
    // give back unenchanted item if requested
    if (returnInput && isServer) {
      ModifierId modifier = ModifierCrystalItem.getModifier(result.getStack());
      assert modifier != null;
      ItemStack current = inv.getTinkerableStack();
      // returnInput drops just 1 level of the enchantment
      // worth noting, its possible multiple match, if thats the case we just extract the first we find
      Map<Enchantment,Integer> enchantments = getEnchantments(current);
      for (Entry<Enchantment,Integer> entry : enchantments.entrySet()) {
        Enchantment enchantment = entry.getKey();
        Modifier enchantmentModifier = ModifierManager.INSTANCE.get(enchantment);
        if (enchantmentModifier != null && enchantmentModifier.getId().equals(modifier)) {
          int newLevel = entry.getValue() - 1;
          if (newLevel <= 0) {
            enchantments.remove(enchantment);
          } else {
            enchantments.put(enchantment, newLevel);
          }
          break;
        }
      }

      ItemStack unenchanted;
      if (matchBook && enchantments.isEmpty()) {
        unenchanted = new ItemStack(Items.BOOK);
        if (current.hasCustomHoverName()) {
          unenchanted.setHoverName(current.getHoverName());
        }
      } else {
        unenchanted = current.copy();
        if (matchBook) {
          // for some dumb reason setEnchantments for a book just adds them instead of setting them
          unenchanted.removeTagKey("StoredEnchantments");
        }
        EnchantmentHelper.setEnchantments(enchantments, unenchanted);
      }
      inv.giveItem(unenchanted);
    }
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.enchantmentConvertingSerializer.get();
  }


  /* Display */

  @Override
  public boolean isToolInput() {
    return true;
  }

  @Override
  public boolean linkToolsModifiers() {
    return matchBook;
  }

  @Override
  public boolean isModifierOutput() {
    return true;
  }

  @Override
  public List<ItemStack> getInputTools() {
    // tools are cached globally, as we just display them directly
    if (!matchBook) {
      return getAllEnchantableTools();
    }
    // for books, cache per recipe as we show the enchants
    if (tools == null) {
      // don't use the cached value from getModifierOptions as that is going to contain some redundant listings
      Set<ModifierId> modifiers = getMatchingModifiers().stream().map(ModifierEntry::getId).collect(Collectors.toSet());
      tools = ModifierManager.INSTANCE.getEquivalentEnchantments(modifiers::contains)
        .flatMap(enchantment -> IntStream.rangeClosed(1, enchantment.getMaxLevel())
          .mapToObj(level -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level))))
        .toList();
    }
    return tools;
  }


  /* Helpers */

  /** Cached list of all enchantable tools, since its item instance controlled only needs to be computed once per launch */
  private static List<ItemStack> ALL_ENCHANTABLE_TOOLS;

  /** Gets a list of all enchantable tools. This is expensive, but only needs to be done once fortunately. */
  private static List<ItemStack> getAllEnchantableTools() {
    if (ALL_ENCHANTABLE_TOOLS == null) {
      ALL_ENCHANTABLE_TOOLS = ForgeRegistries.ITEMS.getValues().stream().map(item -> {
        if (item != Items.BOOK) {
          ItemStack stack = new ItemStack(item);
          if (stack.isEnchantable()) {
            return stack;
          }
        }
        return ItemStack.EMPTY;
      }).filter(stack -> !stack.isEmpty()).toList();
    }
    return ALL_ENCHANTABLE_TOOLS;
  }
}
