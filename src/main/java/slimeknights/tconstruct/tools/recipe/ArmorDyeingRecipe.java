package slimeknights.tconstruct.tools.recipe;

import lombok.Getter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags.Items;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.util.RegistryHelper;
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

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/** Recipe to dye travelers gear */
public class ArmorDyeingRecipe implements ITinkerStationRecipe, IMultiRecipe<IDisplayModifierRecipe> {
  @Getter
  private final ResourceLocation id;

  public ArmorDyeingRecipe(ResourceLocation id) {
    this.id = id;
    ModifierRecipeLookup.addRecipeModifier(null, TinkerModifiers.dyed);
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    // ensure this modifier can be applied
    if (!inv.getTinkerableStack().is(TinkerTags.Items.DYEABLE)) {
      return false;
    }
    // slots must be only dyes, and have at least 1 dye
    boolean found = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack input = inv.getInput(i);
      if (!input.isEmpty()) {
        if (!input.is(Items.DYES)) {
          return false;
        }
        found = true;
      }
    }
    return found;
  }

  @Override
  public RecipeResult<LazyToolStack> getValidatedResult(ITinkerStationContainer inv, RegistryAccess access) {
    ToolStack tool = inv.getTinkerable().copy();

    ModDataNBT persistentData = tool.getPersistentData();
    ModifierId key = TinkerModifiers.dyed.getId();
    int nr = 0, nb = 0, ng = 0;
    int brightness = 0;
    int count = 0;

    // copy existing color
    if (persistentData.contains(key, Tag.TAG_INT)) {
      int color = persistentData.getInt(key);
      int r = color >> 16 & 255;
      int g = color >>  8 & 255;
      int b = color       & 255;
      brightness = Math.max(r, Math.max(g, b));
      nr = r;
      nb = b;
      ng = g;
      count++;
    }

    // copy color from each dye
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        DyeColor dye = DyeColor.getColor(stack);
        if (dye != null) {
          float[] color = dye.getTextureDiffuseColors();
          int r = (int)(color[0] * 255);
          int g = (int)(color[1] * 255);
          int b = (int)(color[2] * 255);
          brightness += Math.max(r, Math.max(g, b));
          nr += r;
          ng += g;
          nb += b;
          count++;
        }
      }
    }

    // should never happen, but lets not crash
    if (count == 0) {
      return RecipeResult.pass();
    }

    // build the final color
    nr /= count;
    ng /= count;
    nb /= count;
    float scaledBrightness = (float)brightness / (float)count;
    brightness = Math.max(nr, Math.max(ng, nb));
    nr = (int)((float)nr * scaledBrightness / brightness);
    ng = (int)((float)ng * scaledBrightness / brightness);
    nb = (int)((float)nb * scaledBrightness / brightness);
    int finalColor = (nr << 16) | (ng << 8) | nb;
    persistentData.putInt(key, finalColor);

    // add the modifier if missing
    if (tool.getModifierLevel(key) == 0) {
      tool.addModifier(key, 1);
    }
    return ITinkerStationRecipe.success(tool, inv);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.armorDyeingSerializer.get();
  }


  /* JEI */

  @Nullable
  private List<IDisplayModifierRecipe> displayRecipes;

  @Override
  public List<IDisplayModifierRecipe> getRecipes(RegistryAccess access) {
    if (displayRecipes == null) {
      List<ItemStack> toolInputs = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, TinkerTags.Items.DYEABLE)
        .map(item -> {
          ItemStack stack = IModifiableDisplay.getDisplayStack(item);
          if (stack.getMaxStackSize() > 1) {
            stack = stack.copyWithCount(Math.min(stack.getMaxStackSize(), DEFAULT_TOOL_STACK_SIZE));
          }
          return stack;
        }).toList();
      ResourceLocation id = getId();
      displayRecipes = Arrays.stream(DyeColor.values()).map(dye -> new DisplayRecipe(id, toolInputs, dye)).collect(Collectors.toList());
    }
    return displayRecipes;
  }

  private static class DisplayRecipe implements IDisplayModifierRecipe {
    /** Cache of tint colors to save calculating it twice */
    private static final int[] TINT_COLORS = new int[DyeColor.values().length];
    private static final IntRange LEVELS = new IntRange(1, 1);
    private final ModifierEntry RESULT = new ModifierEntry(TinkerModifiers.dyed, 1);

    /** Gets the tint color for the given dye */
    private static int getTintColor(DyeColor color) {
      int id = color.getId();
      // protect against the dye color being too large by bypassing cache
      boolean illegal = id >= TINT_COLORS.length;
      // taking advantage of the fact no color is pure black
      if (illegal || TINT_COLORS[id] == 0) {
        float[] colors = color.getTextureDiffuseColors();
        int combinedColor = ((int)(colors[0] * 255) << 16) | ((int)(colors[1] * 255) << 8) | (int)(colors[2] * 255);
        if (illegal) {
          return combinedColor;
        }
        TINT_COLORS[id] = combinedColor;
      }
      return TINT_COLORS[id];
    }

    @Getter
    private final ResourceLocation recipeId;
    private final List<ItemStack> dyes;
    @Getter
    private final List<ItemStack> toolWithoutModifier;
    @Getter
    private final List<ItemStack> toolWithModifier;
    @Getter
    private final Component variant;
    public DisplayRecipe(ResourceLocation recipeId, List<ItemStack> tools, DyeColor color) {
      this.recipeId = recipeId;
      this.toolWithoutModifier = tools;
      this.dyes = RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, color.getTag()).map(ItemStack::new).toList();
      this.variant = Component.translatable("color.minecraft." + color.getSerializedName());

      ResourceLocation modID = RESULT.getId();
      int tintColor = getTintColor(color);
      List<ModifierEntry> results = List.of(RESULT);
      toolWithModifier = tools.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, DEFAULT_TOOL_STACK_SIZE, results, data -> data.putInt(modID, tintColor))).toList();
    }

    @Override
    public ModifierEntry getDisplayResult() {
      return RESULT;
    }

    @Override
    public int getInputCount() {
      return 1;
    }

    @Override
    public List<ItemStack> getDisplayItems(int slot) {
      if (slot == 0) {
        return dyes;
      }
      return Collections.emptyList();
    }

    @Override
    public IntRange getLevel() {
      return LEVELS;
    }
  }
}
