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
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.LazyToolStack;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.modules.cosmetic.BannerModule;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

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
    CompoundTag bannerData = BlockItem.getBlockEntityData(banner);
    ListTag patterns;
    if (bannerData != null) {
      patterns = bannerData.getList("Patterns", Tag.TAG_COMPOUND);
    } else {
      patterns = new ListTag();
    }
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
      if (!toolInputs.isEmpty()) {
        ListTag noPatterns = new ListTag();
        ResourceLocation id = getId();
        Stream<IDisplayModifierRecipe> recipes = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, ItemTags.BANNERS).flatMap(item -> {
          if (item instanceof BannerItem banner) {
            return Stream.of(new DisplayRecipe(id, toolInputs, banner.getColor(), List.of(new ItemStack(banner)), List.of(), noPatterns));
          }
          return Stream.empty();
        });
        // if we have an ingredient that makes it clear, add the clear recipes
        if (clearInput != Ingredient.EMPTY) {
          // we want a pattern on it to make it more clear what it does
          ListTag singlePattern = new ListTag();
          CompoundTag stackTag;
          BannerPattern cross = BuiltInRegistries.BANNER_PATTERN.get(BannerPatterns.CROSS);
          if (cross != null) {
            CompoundTag pattern = new CompoundTag();
            pattern.putString("Pattern", cross.getHashname());
            pattern.putInt("Color", DyeColor.BLACK.getId());
            singlePattern.add(pattern);

            // create NBT for the banner stacks
            CompoundTag blockEntityData = new CompoundTag();
            blockEntityData.put("Patterns", singlePattern);
            BlockEntity.addEntityType(blockEntityData, BlockEntityType.BANNER);
            stackTag = new CompoundTag();
            stackTag.put("BlockEntityTag", blockEntityData);
          } else {
            stackTag = null;
          }
          // add the recipe to the end of the stream
          recipes = Stream.concat(recipes, Stream.of(new DisplayRecipe(
            id, toolInputs, null,
            RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, ItemTags.BANNERS).map(item -> {
              ItemStack stack = new ItemStack(item);
              stack.setTag(stackTag);
              return stack;
            }).toList(),
            List.of(clearInput.getItems()), singlePattern
          )));
        }
        displayRecipes = recipes.toList();
      } else {
        displayRecipes = List.of();
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
    private final List<ItemStack> banner;
    private final List<ItemStack> clearInput;
    @Getter
    private final List<ItemStack> toolWithoutModifier;
    @Getter
    private final List<ItemStack> toolWithModifier;
    @Getter
    private final Component variant;
    public DisplayRecipe(ResourceLocation recipeId, List<ItemStack> tools, @Nullable DyeColor dye, List<ItemStack> banner, List<ItemStack> clearInput, ListTag patterns) {
      this.recipeId = recipeId;
      this.toolWithoutModifier = tools;
      this.banner = banner;
      this.clearInput = clearInput;
      if (dye != null) {
        this.variant = Component.translatable("color.minecraft." + dye.getSerializedName());
      } else {
        this.variant = TConstruct.makeTranslation("recipe", "banner.clear");
      }
      ModifierId key = RESULT.getId();
      List<ModifierEntry> results = List.of(RESULT);
      toolWithModifier = tools.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, DEFAULT_TOOL_STACK_SIZE, results, data -> BannerModule.copyPatterns(data, key, dye, patterns))).toList();
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
        return banner;
      }
      if (slot == 1) {
        return clearInput;
      }
      return List.of();
    }

    @Override
    public IntRange getLevel() {
      return LEVELS;
    }
  }
}
