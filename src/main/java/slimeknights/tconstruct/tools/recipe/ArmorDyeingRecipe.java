package slimeknights.tconstruct.tools.recipe;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags.Items;
import net.minecraftforge.common.util.Constants.NBT;
import slimeknights.mantle.recipe.IMultiRecipe;
import slimeknights.tconstruct.common.recipe.LoggingRecipeSerializer;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationInventory;
import slimeknights.tconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** Recipe to dye travelers gear */
@RequiredArgsConstructor
public class ArmorDyeingRecipe implements ITinkerStationRecipe, IMultiRecipe<IDisplayModifierRecipe> {
  @Getter
  private final ResourceLocation id;
  private final Ingredient toolRequirement;


  @Override
  public boolean matches(ITinkerStationInventory inv, World world) {
    // ensure this modifier can be applied
    if (!this.toolRequirement.test(inv.getTinkerableStack())) {
      return false;
    }
    // slots must be only dyes, and have at least 1 dye
    boolean found = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack input = inv.getInput(i);
      if (!input.isEmpty()) {
        if (!Items.DYES.contains(input.getItem())) {
          return false;
        }
        found = true;
      }
    }
    return found;
  }

  @Override
  public ItemStack getCraftingResult(ITinkerStationInventory inv) {
    ToolStack tool = ToolStack.copyFrom(inv.getTinkerableStack());

    ModDataNBT persistentData = tool.getPersistentData();
    ResourceLocation key = TinkerModifiers.dyed.getId();
    int nr = 0, nb = 0, ng = 0;
    int brightness = 0;
    int count = 0;

    // copy existing color
    if (persistentData.contains(key, NBT.TAG_INT)) {
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
          float[] color = dye.getColorComponentValues();
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
    Modifier modifier = TinkerModifiers.dyed.get();
    if (tool.getModifierLevel(modifier) == 0) {
      tool.addModifier(modifier, 1);
    }
    return tool.createStack();
  }

  @Override
  public IRecipeSerializer<?> getSerializer() {
    return TinkerModifiers.armorDyeingSerializer.get();
  }


  /* JEI */

  @Nullable
  private List<IDisplayModifierRecipe> displayRecipes;

  @Override
  public List<IDisplayModifierRecipe> getRecipes() {
    if (displayRecipes == null) {
      List<ItemStack> toolInputs = Arrays.stream(this.toolRequirement.getMatchingStacks()).map(stack -> {
        if (stack.getItem() instanceof IModifiableDisplay) {
          return ((IModifiableDisplay)stack.getItem()).getRenderTool();
        }
        return stack;
      }).collect(Collectors.toList());
      ModifierEntry result = new ModifierEntry(TinkerModifiers.dyed.get(), 1);
      displayRecipes = Arrays.stream(DyeColor.values()).map(dye -> new DisplayRecipe(result, toolInputs, dye)).collect(Collectors.toList());
    }
    return displayRecipes;
  }


  /* Required */

  /** @deprecated use {@link #getCraftingResult(ITinkerStationInventory)}  */
  @Deprecated
  @Override
  public ItemStack getRecipeOutput() {
    return ItemStack.EMPTY;
  }

  /** Serializer logic */
  public static class Serializer extends LoggingRecipeSerializer<ArmorDyeingRecipe> {
    @Nullable
    @Override
    protected ArmorDyeingRecipe readSafe(ResourceLocation id, PacketBuffer buffer) {
      Ingredient toolRequirement = Ingredient.read(buffer);
      return new ArmorDyeingRecipe(id, toolRequirement);
    }

    @Override
    protected void writeSafe(PacketBuffer buffer, ArmorDyeingRecipe recipe) {
      recipe.toolRequirement.write(buffer);
    }

    @Override
    public ArmorDyeingRecipe read(ResourceLocation id, JsonObject json) {
      Ingredient toolRequirement = Ingredient.deserialize(json.get("tools"));
      return new ArmorDyeingRecipe(id, toolRequirement);
    }
  }

  /** Finished recipe */
  @RequiredArgsConstructor
  public static class Finished implements IFinishedRecipe {
    @Getter
    private final ResourceLocation ID;
    private final Ingredient toolRequirement;

    @Override
    public void serialize(JsonObject json) {
      json.add("tools", toolRequirement.serialize());
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
      return TinkerModifiers.armorDyeingSerializer.get();
    }

    @Nullable
    @Override
    public JsonObject getAdvancementJson() {
      return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementID() {
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
        float[] colors = color.getColorComponentValues();
        TINT_COLORS[id] = ((int)(colors[0] * 255) << 16) | ((int)(colors[1] * 255) << 8) | (int)(colors[2] * 255);
      }
      return TINT_COLORS[id];
    }

    @Getter
    private final ModifierEntry displayResult;
    @Getter
    private final List<List<ItemStack>> displayItems;
    @Getter
    private final List<ItemStack> toolWithModifier;
    public DisplayRecipe(ModifierEntry result, List<ItemStack> tools, DyeColor color) {
      this.displayResult = result;
      ImmutableList.Builder<List<ItemStack>> builder = ImmutableList.builder();
      builder.add(tools);
      builder.add(color.getTag().getAllElements().stream().map(ItemStack::new).collect(Collectors.toList()));
      this.displayItems = builder.build();

      ResourceLocation id = result.getModifier().getId();
      int tintColor = getTintColor(color);
      this.toolWithModifier = tools.stream().map(stack -> IDisplayModifierRecipe.withModifiers(stack, null, result, data -> data.putInt(id, tintColor))).collect(Collectors.toList());
    }

    @Override
    public int getMaxLevel() {
      return 1;
    }
  }
}
