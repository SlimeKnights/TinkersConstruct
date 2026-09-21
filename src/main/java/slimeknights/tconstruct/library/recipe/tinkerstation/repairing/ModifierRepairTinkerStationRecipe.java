package slimeknights.tconstruct.library.recipe.tinkerstation.repairing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.ModifierManager;
import slimeknights.tconstruct.library.modifiers.hook.behavior.RepairFactorModifierHook;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayCraftingTinkering;
import slimeknights.tconstruct.library.recipe.tinkerstation.IDisplayToolModification;
import slimeknights.tconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.helper.ToolDamageUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;
import slimeknights.tconstruct.library.tools.nbt.StatsNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.tables.recipe.TinkerStationRepairRecipe;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;

/**
 * Recipe to repair tools with the given modifier using a specific item.
 * @see ModifierRepairRecipeBuilder
 * @see ModifierRepairCraftingRecipe
 * @see slimeknights.tconstruct.library.modifiers.modules.behavior.MaterialRepairModule
 */
@RequiredArgsConstructor
public class ModifierRepairTinkerStationRecipe implements ITinkerStationRecipe, IModifierRepairRecipe, IMultiRecipe<IDisplayToolModification> {
  private static final String TOOLTIP_KEY = TConstruct.makeTranslationKey("recipe", "tool_repair.modifier");
  private static final String KEY_AMOUNT = TConstruct.makeTranslationKey("recipe", "modifier.amount");
  public static final RecordLoadable<ModifierRepairTinkerStationRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), MODIFIER_FIELD, INGREDIENT_FIELD, REPAIR_AMOUNT_FIELD, ModifierRepairTinkerStationRecipe::new);

  @Getter
  private final ResourceLocation id;
  @Getter
  private final ModifierId modifier;
  @Getter
  private final Ingredient ingredient;
  @Getter
  private final int repairAmount;

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    ItemStack tinkerable = inv.getTinkerableStack();
    if (!tinkerable.is(TinkerTags.Items.DURABILITY)) {
      return false;
    }
    ToolStack tool = inv.getTinkerable();
    if (tool.getModifierLevel(modifier) == 0 || (!tool.isBroken() && tool.getDamage() == 0)) {
      return false;
    }
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    ToolStack tool = inv.getTinkerable();
    int amountPerItem = tool.getModifierLevel(modifier) * repairAmount;
    if (amountPerItem <= 0) {
      return RecipeResult.pass();
    }

    // apply modifiers to possibly boost it
    float repairFactor = RepairFactorModifierHook.getRepairFactor(tool, 1);
    if (repairFactor <= 0) {
      return RecipeResult.pass();
    }
    amountPerItem *= repairFactor;

    // get the max amount we can repair
    int available = IncrementalModifierRecipe.getAvailableAmount(inv, ingredient, amountPerItem);
    if (available <= 0) {
      return RecipeResult.pass();
    }
    // we will just repair the max possible here, no reason to try less
    tool = tool.copy();
    ToolDamageUtil.repair(tool, available);
    return LazyToolStack.successCopy(tool, 1, inv.getTinkerableStack());
  }

  @Override
  public int shrinkToolSlotBy() {
    return 1;
  }

  @Override
  public void updateInputs(LazyToolStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    ToolStack tool = inv.getTinkerable();

    // rescale the amount based on modifiers
    float repairFactor = RepairFactorModifierHook.getRepairFactor(tool, 1);
    if (repairFactor <= 0) {
      return;
    }
    // also scale by relevant modifier level
    int amountPerItem = (int)(tool.getModifierLevel(modifier) * repairAmount * repairFactor);
    if (amountPerItem < 0) {
      return;
    }
    // how much do we need to subtract from our inputs still
    int repairRemaining = tool.getDamage() - result.getTool().getDamage();
    IncrementalModifierRecipe.updateInputs(inv, ingredient, repairRemaining, amountPerItem, ItemStack.EMPTY);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierRepair.get();
  }


  /* JEI */
  private List<IDisplayToolModification> displayRecipes;

  @Override
  public List<IDisplayToolModification> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      displayRecipes = List.of(new DisplayRecipe(id, this, false));
    }
    return displayRecipes;
  }

  @Getter
  static class DisplayRecipe implements IDisplayCraftingTinkering {
    private final ResourceLocation id;
    private final ModifierId modifier;
    private final int repairAmount;
    private final Component tooltip;
    private final Component variant;
    private final List<ItemStack> inputs;
    private final List<ItemStack> toolWithoutModifier;
    private final List<ItemStack> toolWithModifier;

    public DisplayRecipe(ResourceLocation id, IModifierRepairRecipe recipe, boolean isCrafting) {
      this.id = id;
      this.modifier = recipe.getModifier();
      this.repairAmount = recipe.getRepairAmount();
      MutableComponent tooltip = Component.translatable(TOOLTIP_KEY, ModifierManager.getValue(modifier).getDisplayName());
      if (isCrafting) {
        tooltip = tooltip.withStyle(ChatFormatting.GRAY);
      }
      this.tooltip = tooltip;
      this.variant = Component.translatable(KEY_AMOUNT, repairAmount);
      this.inputs = List.of(recipe.getIngredient().getItems());

      // set durability on each tool to 250, covers most instances
      CompoundTag stats = StatsNBT.builder().set(ToolStats.DURABILITY, 250).build().serializeToNBT();
      ListTag modifiers = ModifierNBT.builder().add(modifier, 1).build().serializeToNBT();
      toolWithoutModifier = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, TinkerTags.Items.DURABILITY)
        .map(item -> {
          if (item instanceof IModifiableDisplay modifiable) {
            ItemStack stack = modifiable.getRenderTool().copy();
            CompoundTag tag = stack.getOrCreateTag();
            tag.put(ToolStack.TAG_STATS, stats);
            tag.put(ToolStack.TAG_UPGRADES, modifiers);
            tag.put(ToolStack.TAG_MODIFIERS, modifiers);
            tag.putInt(ToolStack.TAG_DAMAGE, 200); // mostly broken
            return stack;
          }
          return ItemStack.EMPTY;
        })
        .filter(stack -> !stack.isEmpty())
        .toList();
      toolWithModifier = toolWithoutModifier.stream().map(stack -> {
        stack = stack.copy();
        stack.getOrCreateTag().putInt(ToolStack.TAG_DAMAGE, 200 - repairAmount);
        return stack;
      }).toList();
    }

    @Override
    public Component getTitle() {
      return TinkerStationRepairRecipe.TITLE;
    }

    @Override
    public boolean isToolCatalyst() {
      return true;
    }

    @Override
    public boolean isFiltered() {
      return true;
    }

    @Override
    public boolean isTool(ItemStack check) {
      return check.is(TinkerTags.Items.DURABILITY);
    }

    @Override
    public boolean isVisibleFromItem(ItemStack focus, boolean output) {
      return !output && !isTool(focus) || ModifierUtil.getModifierLevel(focus, modifier) > 0;
    }

    @Override
    public int getMaxToolSize() {
      return 1;
    }

    @Override
    public int getInputCount() {
      return 1;
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot) {
      if (slot == 0) {
        return inputs;
      }
      return List.of();
    }

    @Override
    public RecipeResult<ItemStack> onFocused(ItemStack focus) {
      ToolStack tool = ToolStack.from(focus);
      if (tool.getDamage() == 0) {
        return RecipeResult.failure(TinkerStationRepairRecipe.REPAIRED);
      }
      // no repair? stick with what we have
      int amount = (int) RepairFactorModifierHook.getRepairFactor(tool, repairAmount * tool.getModifierLevel(modifier));
      if (amount <= 0) {
        return RecipeResult.success(focus);
      }
      tool = tool.copy();
      ToolDamageUtil.repair(tool, amount);
      return RecipeResult.success(tool.copyStack(focus));
    }
  }
}
