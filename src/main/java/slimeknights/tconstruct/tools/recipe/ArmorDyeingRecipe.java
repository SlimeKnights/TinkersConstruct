package slimeknights.tconstruct.tools.recipe;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags.Items;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.mantle.recipe.helper.LoggingRecipeSerializer;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
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
  private final Ingredient toolRequirement;

  public ArmorDyeingRecipe(ResourceLocation id, Ingredient toolRequirement) {
    this.id = id;
    this.toolRequirement = toolRequirement;
    ModifierRecipeLookup.addRecipeModifier(null, TinkerModifiers.dyed);
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    // ensure this modifier can be applied
    if (!this.toolRequirement.test(inv.getTinkerableStack())) {
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
  public ItemStack assemble(ITinkerStationContainer inv) {
    ItemStack tinkerable = inv.getTinkerableStack();
    ToolStack tool = ToolStack.copyFrom(tinkerable);

    ModDataNBT persistentData = tool.getPersistentData();
    ResourceLocation key = TinkerModifiers.dyed.getId();
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
      return ItemStack.EMPTY;
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
    ModifierId modifier = TinkerModifiers.dyed.getId();
    if (tool.getModifierLevel(modifier) == 0) {
      tool.addModifier(modifier, 1);
    }
    return tool.createStack(Math.min(tinkerable.getCount(), shrinkToolSlotBy()));
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.armorDyeingSerializer.get();
  }


  /* JEI */

  @Nullable
  private List<IDisplayModifierRecipe> displayRecipes;

  @Override
  public List<IDisplayModifierRecipe> getRecipes() {
    if (displayRecipes == null) {
      List<ItemStack> toolInputs = Arrays.stream(this.toolRequirement.getItems()).map(stack -> {
        if (stack.getItem() instanceof IModifiableDisplay) {
          return ((IModifiableDisplay)stack.getItem()).getRenderTool();
        }
        return stack;
      }).toList();
      ModifierEntry result = new ModifierEntry(TinkerModifiers.dyed.get(), 1);
      displayRecipes = Arrays.stream(DyeColor.values()).map(dye -> new DisplayRecipe(result, toolInputs, dye)).collect(Collectors.toList());
    }
    return displayRecipes;
  }


  /* Required */

  /** @deprecated use {@link #assemble(ITinkerStationContainer)}  */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  /** Serializer logic */
  public static class Serializer extends LoggingRecipeSerializer<ArmorDyeingRecipe> {
    @Nullable
    @Override
    protected ArmorDyeingRecipe fromNetworkSafe(ResourceLocation id, FriendlyByteBuf buffer) {
      Ingredient toolRequirement = Ingredient.fromNetwork(buffer);
      return new ArmorDyeingRecipe(id, toolRequirement);
    }

    @Override
    protected void toNetworkSafe(FriendlyByteBuf buffer, ArmorDyeingRecipe recipe) {
      recipe.toolRequirement.toNetwork(buffer);
    }

    @Override
    public ArmorDyeingRecipe fromJson(ResourceLocation id, JsonObject json) {
      Ingredient toolRequirement = Ingredient.fromJson(json.get("tools"));
      return new ArmorDyeingRecipe(id, toolRequirement);
    }
  }

  /** Finished recipe */
  @SuppressWarnings("ClassCanBeRecord")
  @RequiredArgsConstructor
  public static class Finished implements FinishedRecipe {
    @Getter
    private final ResourceLocation id;
    private final Ingredient toolRequirement;

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.add("tools", toolRequirement.toJson());
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerModifiers.armorDyeingSerializer.get();
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

  private static class DisplayRecipe implements IDisplayModifierRecipe {
    /** Cache of tint colors to save calculating it twice */
    private static final int[] TINT_COLORS = new int[16];

    /** Gets the tint color for the given dye */
    private static int getTintColor(DyeColor color) {
      int id = color.getId();
      // taking advantage of the fact no color is pure black
      if (TINT_COLORS[id] == 0) {
        float[] colors = color.getTextureDiffuseColors();
        TINT_COLORS[id] = ((int)(colors[0] * 255) << 16) | ((int)(colors[1] * 255) << 8) | (int)(colors[2] * 255);
      }
      return TINT_COLORS[id];
    }

    private final List<ItemStack> dyes;
    @Getter
    private final ModifierEntry displayResult;
    @Getter
    private final List<ItemStack> toolWithoutModifier;
    @Getter
    private final List<ItemStack> toolWithModifier;
    public DisplayRecipe(ModifierEntry result, List<ItemStack> tools, DyeColor color) {
      this.displayResult = result;
      this.toolWithoutModifier = tools;
      this.dyes = RegistryHelper.getTagValueStream(Registry.ITEM, color.getTag()).map(ItemStack::new).toList();

      ResourceLocation id = result.getModifier().getId();
      int tintColor = getTintColor(color);
      toolWithModifier = tools.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, null, result, data -> data.putInt(id, tintColor))).toList();
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
    public int getMaxLevel() {
      return 1;
    }
  }
}
