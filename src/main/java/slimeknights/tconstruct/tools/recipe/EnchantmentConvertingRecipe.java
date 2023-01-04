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
import java.util.Objects;
import java.util.function.Consumer;

/** Recipe for converting enchanted books into modifier crystals */
public class EnchantmentConvertingRecipe extends AbstractWorktableRecipe {
  private static final String BASE_KEY = TConstruct.makeTranslationKey("recipe", "enchantment_converting");
  private static final Component DESCRIPTION = TConstruct.makeTranslation("recipe", "enchantment_converting.description");
  private static final Component NO_ENCHANTMENT = TConstruct.makeTranslation("recipe", "enchantment_converting.no_enchantments");
  private static final RecipeResult<ToolStack> TOO_FEW = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "enchantment_converting.too_few"));

  /** Name of recipe, used for title */
  private final String name;
  /** Cached title component */
  @Getter
  private final Component title;

  /** Modifiers valid for this recipe */
  private final IJsonPredicate<ModifierId> modifierPredicate;

  private List<ModifierEntry> displayModifiers;

  public EnchantmentConvertingRecipe(ResourceLocation id, String name, List<SizedIngredient> inputs, IJsonPredicate<ModifierId> modifierPredicate) {
    super(id, inputs);
    this.name = name;
    this.title = new TranslatableComponent(BASE_KEY + "." + name + ".title");
    this.modifierPredicate = modifierPredicate;
  }


  /* Text */

  @Override
  public Component getDescription(@Nullable ITinkerableContainer inv) {
    // ensure we have at least one supported enchantment
    if (inv != null && EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(inv.getTinkerableStack())).entrySet().stream().noneMatch(entry -> {
      Modifier modifier = ModifierManager.INSTANCE.get(entry.getKey());
      return modifier != null && modifierPredicate.matches(modifier.getId());
    })) {
      return NO_ENCHANTMENT;
    }
    return DESCRIPTION;
  }


  /* Logic */

  @Override
  public boolean matches(ITinkerableContainer inv, Level world) {
    if (!inv.getTinkerableStack().is(Items.ENCHANTED_BOOK)) {
      return false;
    }
    return ModifierRecipe.checkMatch(inv, inputs);
  }

  @Override
  public List<ModifierEntry> getModifierOptions(@Nullable ITinkerableContainer inv) {
    if (inv != null) {
      // map all enchantments to an equal level modifier
      return EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(inv.getTinkerableStack())).entrySet().stream().map(entry -> {
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
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.enchantmentConvertingSerializer.get();
  }


  /* Display */

  @Override
  public List<ItemStack> getInputTools() {
    if (tools == null) {
      tools = ForgeRegistries.ENCHANTMENTS.getValues().stream().map(enchantment -> EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, 1))).toList();
    }
    return tools;
  }

  public static class Serializer extends LoggingRecipeSerializer<EnchantmentConvertingRecipe> {
    @Override
    public EnchantmentConvertingRecipe fromJson(ResourceLocation id, JsonObject json) {
      String name = GsonHelper.getAsString(json, "name");
      List<SizedIngredient> ingredients = JsonHelper.parseList(json, "inputs", SizedIngredient::deserialize);
      IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.LOADER.getAndDeserialize(json, "modifier_predicate");
      return new EnchantmentConvertingRecipe(id, name, ingredients, modifierPredicate);
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
      IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.LOADER.fromNetwork(buffer);
      return new EnchantmentConvertingRecipe(id, name, ingredients.build(), modifierPredicate);
    }

    @Override
    public void toNetworkSafe(FriendlyByteBuf buffer, EnchantmentConvertingRecipe recipe) {
      buffer.writeUtf(recipe.name);
      buffer.writeVarInt(recipe.inputs.size());
      for (SizedIngredient ingredient : recipe.inputs) {
        ingredient.write(buffer);
      }
      ModifierPredicate.LOADER.toNetwork(recipe.modifierPredicate, buffer);
    }
  }

  @RequiredArgsConstructor(staticName = "converting")
  public static class Builder extends AbstractSizedIngredientRecipeBuilder<Builder> {
    private final String name;
    @Setter @Accessors(fluent = true)
    private IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.ALWAYS;

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
        json.add("modifier_predicate", ModifierPredicate.LOADER.serialize(modifierPredicate));
      }

      @Override
      public RecipeSerializer<?> getType() {
        return TinkerModifiers.enchantmentConvertingSerializer.get();
      }
    }
  }
}
