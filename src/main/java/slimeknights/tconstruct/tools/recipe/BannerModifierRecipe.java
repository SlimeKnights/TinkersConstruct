package slimeknights.tconstruct.tools.recipe;

import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import slimeknights.mantle.data.loadable.common.IngredientLoadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.RecipeResult;
import slimeknights.tconstruct.library.recipe.RecipeSlot;
import slimeknights.tconstruct.library.recipe.RecipeSlots;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modules.cosmetic.BannerModule;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Recipe to add a banner to a shield */
public class BannerModifierRecipe implements ITinkerStationRecipe, IMultiRecipe<IDisplayModifierRecipe> {
  private static final RecipeResult<LazyToolStack> NO_PATTERNS = RecipeResult.failure(TConstruct.makeTranslationKey("recipe", "banner.clear.no_patterns"));
  public static final RecordLoadable<BannerModifierRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    IngredientLoadable.ALLOW_EMPTY.defaultField("clear_input", Ingredient.EMPTY, false, r -> r.clearInput),
    BannerModifierRecipe::new);

  @Getter
  private final ResourceLocation id;
  private final Ingredient clearInput;

  public BannerModifierRecipe(ResourceLocation id, Ingredient clearInput) {
    this.id = id;
    this.clearInput = clearInput;
    ModifierRecipeLookup.addRecipeModifier(null, TinkerModifiers.banner);
  }

  @Deprecated(forRemoval = true)
  public BannerModifierRecipe(ResourceLocation id) {
    this(id, Ingredient.EMPTY);
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    // ensure this modifier can be applied
    if (!inv.getTinkerableStack().is(TinkerTags.Items.BANNER)) {
      return false;
    }
    // slots must be only banner
    boolean banner = false;
    boolean clear = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack input = inv.getInput(i);
      if (!input.isEmpty()) {
        if (clearInput.test(input)) {
          // multiple clears
          if (clear) return false;
          clear = true;
        }
        else if (input.getItem() instanceof BannerItem) {
          // multiple banners
          if (banner) return false;
          banner = true;
        } else {
          // non-banner input
          return false;
        }
      }
    }
    // clear is not required, just one copy allowed
    return banner;
  }

  /** Gets the list of patterns from the banner stack */
  private static ListTag getBannerPatterns(ItemStack banner) {
    // get the banner data
    CompoundTag bannerData = BlockItem.getBlockEntityData(banner);
    ListTag patterns;
    if (bannerData != null) {
      patterns = bannerData.getList("Patterns", Tag.TAG_COMPOUND);
    } else {
      patterns = new ListTag();
    }
    return patterns;
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    ToolStack tool = inv.getTinkerable().copy();

    ModifierId key = TinkerModifiers.banner.getId();

    // locate the banner
    ItemStack banner = ItemStack.EMPTY;
    DyeColor dye = DyeColor.BLACK;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty() && stack.getItem() instanceof BannerItem bannerItem) {
        banner = stack;
        dye = bannerItem.getColor();
        // only need 1
        break;
      }
    }
    // should never happen
    if (banner.isEmpty()) {
      return RecipeResult.pass();
    }
    // remove the dye if clear
    if (clearInput != Ingredient.EMPTY) {
      for (int i = 0; i < inv.getInputCount(); i++) {
        ItemStack stack = inv.getInput(i);
        if (!stack.isEmpty() && clearInput.test(stack)) {
          dye = null;
          break;
        }
      }
    }

    // get the banner data
    ListTag patterns = getBannerPatterns(banner);
    // disallow no patterns when going clear
    if (dye == null && patterns.isEmpty()) {
      return NO_PATTERNS;
    }

    // apply the pattern
    BannerModule.copyPatterns(tool.getPersistentData(), key, dye, patterns);

    // add the modifier if missing
    if (tool.getModifierLevel(key) == 0) {
      tool.addModifier(key, 1);
    }
    return ITinkerStationRecipe.success(tool, inv);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.bannerModifierSerializer.get();
  }


  /* JEI */

  @Nullable
  private List<IDisplayModifierRecipe> displayRecipes;

  /** Creates the tag for a single pattern with the given color */
  private static CompoundTag createDisplayPatternTag(BannerPattern pattern, DyeColor color) {
    ListTag singlePattern = new ListTag();
    CompoundTag patternTag = new CompoundTag();
    patternTag.putString("Pattern", pattern.getHashname());
    patternTag.putInt("Color", color.getId());
    singlePattern.add(patternTag);

    // create NBT for the banner stacks
    CompoundTag blockEntityData = new CompoundTag();
    blockEntityData.put("Patterns", singlePattern);
    BlockEntity.addEntityType(blockEntityData, BlockEntityType.BANNER);
    CompoundTag stackTag = new CompoundTag();
    stackTag.put("BlockEntityTag", blockEntityData);
    return stackTag;
  }

  @Override
  public List<IDisplayModifierRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      List<ItemStack> toolInputs = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, TinkerTags.Items.BANNER)
        .map(item -> {
          ItemStack stack = IModifiableDisplay.getDisplayStack(item);
          if (stack.getMaxStackSize() > 1) {
            stack = stack.copyWithCount(Math.min(stack.getMaxStackSize(), DEFAULT_TOOL_STACK_SIZE));
          }
          return stack;
        }).toList();
      List<ItemStack> banners = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, ItemTags.BANNERS)
        .filter(item -> item instanceof BannerItem)
        .map(ItemStack::new).toList();
      if (toolInputs.isEmpty()) {
        // emptying banner tag is fine, if a little weird
        TConstruct.LOG.debug("Skipping creating banner modifier display recipes {}: No tools in tag {}", id, TinkerTags.Items.BANNER);
        displayRecipes = List.of();
      } else if (banners.isEmpty()) {
        TConstruct.LOG.error("Unable to create banner modifier display recipes {}: Banner tag {} has no BannerItem", id, ItemTags.BANNERS);
        displayRecipes = List.of();
      } else {
        ResourceLocation id = getId();
        List<IDisplayModifierRecipe> recipes = new ArrayList<>(clearInput != Ingredient.EMPTY ? 2 : 1);
        // create standard recipe
        recipes.add(new DisplayRecipe(id, toolInputs, banners, List.of()));
        // if we have an ingredient that makes it clear, add the clear recipe
        if (clearInput != Ingredient.EMPTY) {
          // we want a pattern on it to make it more clear what it does
          // but put a white pattern on the black banner for visibility
          CompoundTag stackTag, stackTagBlack;
          BannerPattern cross = BuiltInRegistries.BANNER_PATTERN.get(BannerPatterns.CROSS);
          if (cross != null) {
            stackTag = createDisplayPatternTag(cross, DyeColor.BLACK);
            stackTagBlack = createDisplayPatternTag(cross, DyeColor.WHITE);
          } else {
            stackTag = null;
            stackTagBlack = null;
          }
          // add the recipe to the end of the stream
          List<ItemStack> bannersWithPattern = banners.stream().map(stack -> {
            stack = stack.copy();
            stack.setTag(stack.getItem() == Items.BLACK_BANNER ? stackTagBlack : stackTag);
            return stack;
          }).toList();
          recipes.add(new DisplayRecipe(id, toolInputs, bannersWithPattern, List.of(clearInput.getItems())));
        }
        displayRecipes = List.copyOf(recipes);
      }
    }
    return displayRecipes;
  }

  /** Display recipe instance */
  private static class DisplayRecipe implements IDisplayModifierRecipe {
    private static final IntRange LEVELS = new IntRange(1, 1);
    private final ModifierEntry RESULT = new ModifierEntry(TinkerModifiers.banner, 1);

    @Getter
    private final ResourceLocation recipeId;
    private final List<ItemStack> banners;
    private final List<ItemStack> clearInput;
    @Getter
    private final List<ItemStack> toolWithoutModifier;
    @Getter
    private final List<ItemStack> toolWithModifier;
    @Getter
    private final Component variant;
    public DisplayRecipe(ResourceLocation recipeId, List<ItemStack> tools, List<ItemStack> banners, List<ItemStack> clearInput) {
      this.recipeId = recipeId;
      this.toolWithoutModifier = tools;
      this.banners = banners;
      this.clearInput = clearInput;
      if (!clearInput.isEmpty()) {
        this.variant = TConstruct.makeTranslation("recipe", "banner.clear");
      } else {
        this.variant = TConstruct.makeTranslation("recipe", "banner.solid");
      }
      // build tools with modifier
      List<ModifierEntry> results = List.of(RESULT);
      ModifierId key = RESULT.getId();
      // apply a default pattern to the result tools for the sake of offbrand JEI. You will never see it in real JEI
      DyeColor defaultColor = clearInput.isEmpty() ? DyeColor.WHITE : null;
      ListTag defaultPatterns = getBannerPatterns(banners.get(0));
      toolWithModifier = tools.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, DEFAULT_TOOL_STACK_SIZE, results, data -> BannerModule.copyPatterns(data, key, defaultColor, defaultPatterns))).toList();
    }

    @Override
    public ModifierEntry getDisplayResult() {
      return RESULT;
    }

    @Override
    public int getInputCount() {
      return clearInput.isEmpty() ? 1 : 2;
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot) {
      if (slot == 0) {
        return banners;
      }
      if (slot == 1) {
        return clearInput;
      }
      return List.of();
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot, ItemStack focus, boolean focusOutput) {
      // if focus input is a banner with patterns, use that
      if (slot == 0 && !focusOutput && !focus.isEmpty() && focus.getItem() instanceof BannerItem
          // skip using it if the example pattern lacks patterns, as that won't give a useful result
          && (clearInput.isEmpty() || !getBannerPatterns(focus).isEmpty())) {
        return List.of(focus.copyWithCount(1));
      }
      return getDisplayItems(slot);
    }

    @Override
    public IntRange getLevel() {
      return LEVELS;
    }

    @Override
    public boolean isTool(ItemStack check) {
      return check.is(TinkerTags.Items.BANNER);
    }

    @Nullable
    @Override
    public Component canApply(IToolStackView tool) {
      return null;
    }

    @Override
    public void applyModifier(ToolStack tool) {
      // you aren't really going to see this tool, but offbrand recipe viewers may not call the other hook
      ModifierId modifier = TinkerModifiers.banner.getId();
      BannerModule.copyPatterns(tool.getPersistentData(), modifier, clearInput.isEmpty() ? DyeColor.WHITE : null, getBannerPatterns(banners.get(0)));

      // add the modifier if missing
      if (tool.getModifierLevel(modifier) == 0) {
        tool.addModifier(modifier, 1);
      }
    }

    @Override
    public boolean isSlotsDynamic() {
      return true;
    }

    @Override
    public void onDisplayUpdate(RecipeSlot<ItemStack> toolSlot, RecipeSlots<ItemStack> inputs, RecipeSlot<ItemStack> output) {
      // add banner to the currently displayed tool from the currently displayed banner
      ItemStack bannerStack = inputs.get(0);
      // apply banner to display tool
      ItemStack toolStack = toolSlot.get();
      if (!toolStack.isEmpty() && bannerStack.getItem() instanceof BannerItem banner) {
        ListTag patterns = getBannerPatterns(bannerStack);
        // if clear, skip the color. Still better that we have a BannerItem though
        DyeColor dye = clearInput.isEmpty() ? banner.getColor() : null;
        ToolStack tool = ToolStack.copyFrom(toolStack);
        ModifierId modifier = RESULT.getId();
        BannerModule.copyPatterns(tool.getPersistentData(), modifier, dye, patterns);

        // add the modifier if missing
        if (tool.getModifierLevel(modifier) == 0) {
          tool.addModifier(modifier, 1);
        }
        // build the display stack
        output.set(tool.copyStack(toolStack));
      }
    }

    @Override
    public boolean shouldDisplayValidate() {
      return false;
    }
  }
}
