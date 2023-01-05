package slimeknights.tconstruct.tools.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.recipe.ingredient.SizedIngredient;
import slimeknights.mantle.util.JsonHelper;
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
import slimeknights.tconstruct.library.recipe.worktable.AbstractSizedIngredientRecipeBuilder;
import slimeknights.tconstruct.library.recipe.worktable.AbstractWorktableRecipe;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.item.ModifierCrystalItem;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Recipe for converting enchanted books into modifier crystals */
public class EnchantmentConvertingRecipe extends AbstractWorktableRecipe {
  private static final String BASE_KEY = TConstruct.makeTranslationKey("recipe", "enchantment_converting");
  private static final Component DESCRIPTION_LOST = TConstruct.makeTranslation("recipe", "enchantment_converting.description.lost");
  private static final Component DESCRIPTION_KEEP = TConstruct.makeTranslation("recipe", "enchantment_converting.description.keep");
  private static final Component NO_ENCHANTMENT = TConstruct.makeTranslation("recipe", "enchantment_converting.no_enchantments");
  private static final RecipeResult<ToolStack> TOO_FEW = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "enchantment_converting.too_few"));

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
    this.title = new TranslatableComponent(BASE_KEY + "." + name + ".title");
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

  @Override
  public List<ModifierEntry> getModifierOptions(@Nullable ITinkerableContainer inv) {
    if (inv != null) {
      // map all enchantments to an equal level modifier
      return getEnchantments(inv.getTinkerableStack()).entrySet().stream().map(entry -> {
        Modifier modifier = ModifierManager.INSTANCE.get(entry.getKey());
        if (modifier != null && modifierPredicate.matches(modifier.getId())) {
          return new ModifierEntry(modifier, entry.getValue());
        }
        return null;
      }).filter(Objects::nonNull).toList();
    }
    if (displayModifiers == null) {
      displayModifiers = ModifierRecipeLookup.getAllRecipeModifiers().filter(modifier -> modifierPredicate.matches(modifier.getId())).map(mod -> new ModifierEntry(mod, 1)).toList();
    }
    return displayModifiers;
  }

  @Override
  public RecipeResult<ToolStack> getResult(ITinkerableContainer inv, ModifierEntry modifier) {
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
    // TODO 1.19: this is a pretty big hack, converting it into a tool stack when its an item stack. Consider whether we should use item stack output for 1.19
    return RecipeResult.success(ToolStack.from(ModifierCrystalItem.withModifier(modifier.getId())));
  }

  @Override
  public int toolResultSize(ITinkerableContainer inv, ModifierEntry selected) {
    return selected.getLevel();
  }

  @Override
  public void updateInputs(IToolStackView result, ITinkerableContainer.Mutable inv, ModifierEntry selected, boolean isServer) {
    // consume inputs once per selected item
    for (int i = 0; i < selected.getLevel(); i++) {
      ModifierRecipe.updateInputs(inv, inputs);
    }
    // give back unenchanted item if requested
    if (returnInput && isServer) {
      ItemStack current = inv.getTinkerableStack();
      ItemStack unenchanted;
      if (matchBook) {
        unenchanted = new ItemStack(Items.BOOK);
        if (current.hasCustomHoverName()) {
          unenchanted.setHoverName(current.getHoverName());
        }
      } else {
        unenchanted = current.copy();
        EnchantmentHelper.setEnchantments(getEnchantments(unenchanted).entrySet().stream()
                                                                      .filter(entry -> entry.getKey().isCurse())
                                                                      .collect(Collectors.toMap(Entry::getKey, Entry::getValue)),
                                          unenchanted);
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
      Set<ModifierId> modifiers = getModifierOptions(null).stream().map(ModifierEntry::getId).collect(Collectors.toSet());
      tools = ModifierManager.INSTANCE.getEquivalentEnchantments(modifiers::contains)
                                      .flatMap(enchantment -> IntStream.rangeClosed(1, enchantment.getMaxLevel())
                                                                       .mapToObj(level -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level))))
                                      .toList();
    }
    return tools;
  }

  public static class Serializer extends LoggingRecipeSerializer<EnchantmentConvertingRecipe> {
    @Override
    public EnchantmentConvertingRecipe fromJson(ResourceLocation id, JsonObject json) {
      String name = GsonHelper.getAsString(json, "name");
      List<SizedIngredient> ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      boolean matchBook = GsonHelper.getAsBoolean(json, "match_book");
      boolean returnInput = GsonHelper.getAsBoolean(json, "return_unenchanted");
      IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.LOADER.getAndDeserialize(json, "modifier_predicate");
      return new EnchantmentConvertingRecipe(id, name, ingredients, matchBook, returnInput, modifierPredicate);
    }

    @Nullable
    @Override
    public EnchantmentConvertingRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      String name = buffer.readUtf(Short.MAX_VALUE);
      int size = buffer.readVarInt();
      ImmutableList.Builder<SizedIngredient> ingredients = ImmutableList.builder();
      for (int i = 0; i < size; i++) {
        ingredients.add(SizedIngredient.read(buffer));
      }
      boolean matchBook = buffer.readBoolean();
      boolean returnInput = buffer.readBoolean();
      IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.LOADER.fromNetwork(buffer);
      return new EnchantmentConvertingRecipe(id, name, ingredients.build(), matchBook, returnInput, modifierPredicate);
    }

    @Override
    public void toNetworkSafe(FriendlyByteBuf buffer, EnchantmentConvertingRecipe recipe) {
      buffer.writeUtf(recipe.name);
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
      buffer.writeBoolean(recipe.matchBook);
      buffer.writeBoolean(recipe.returnInput);
      ModifierPredicate.LOADER.toNetwork(recipe.modifierPredicate, buffer);
    }
  }

  @RequiredArgsConstructor(staticName = "converting")
  public static class Builder extends AbstractSizedIngredientRecipeBuilder<Builder> {
    private final String name;
    private final boolean matchBook;
    private boolean returnInput = false;
    @Setter @Accessors(fluent = true)
    private IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.ALWAYS;

    /** If true, returns the unenchanted form of the item as an extra result */
    public Builder returnInput() {
      returnInput = true;
      return this;
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer) {
      save(consumer, TConstruct.getResource(name));
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
      if (inputs.isEmpty()) {
        throw new IllegalStateException("Must have at least one input");
      }
      ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
      consumer.accept(new Finished(id, advancementId));
    }

    private class Finished extends SizedFinishedRecipe {
      public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
        super(ID, advancementID);
      }

      @Override
      public void serializeRecipeData(JsonObject json) {
        json.addProperty("name", name);
        super.serializeRecipeData(json);
        json.addProperty("match_book", matchBook);
        json.addProperty("return_unenchanted", returnInput);
        json.add("modifier_predicate", ModifierPredicate.LOADER.serialize(modifierPredicate));
      }

      @Override
      public RecipeSerializer<?> getType() {
        return TinkerModifiers.enchantmentConvertingSerializer.get();
      }
    }
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
