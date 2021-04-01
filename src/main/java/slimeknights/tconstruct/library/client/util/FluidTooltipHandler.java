package slimeknights.tconstruct.library.client.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import slimeknights.mantle.recipe.FluidIngredient;
import slimeknights.mantle.recipe.RecipeHelper;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FluidTooltipHandler {
  private static final Map<Fluid,List<FluidGuiEntry>> CACHE = new HashMap<>();
  public static final ITextComponent HOLD_SHIFT = new TranslationTextComponent(Util.makeTranslationKey("gui", "fluid.hold_shift")).mergeStyle(TextFormatting.GRAY);

  /*
   * Base units
   */
  private static final FluidGuiEntry KILOBUCKET = new FluidGuiEntry("kilobucket", 1000000);
  private static final FluidGuiEntry BUCKET = new FluidGuiEntry("bucket", 1000);
  private static final FluidGuiEntry MILLIBUCKET = new FluidGuiEntry("millibucket", 1);
  private static final FluidGuiEntry INGOT = new FluidGuiEntry("ingot", MaterialValues.INGOT);
  private static final FluidGuiEntry BLOCK = new FluidGuiEntry("block", MaterialValues.METAL_BLOCK);
  private static final FluidGuiEntry PANE = new FluidGuiEntry("pane", MaterialValues.GLASS_PANE);
  private static final FluidGuiEntry SLIMEBALL = new FluidGuiEntry("slimeball", MaterialValues.SLIMEBALL);

  /** List of options to check for table cast recipes */
  private static final Map<Item,FluidGuiEntry> TOOLTIP_OPTIONS = new IdentityHashMap<>();

  /** Initializes the tooltip handler */
  public static void init() {
    MinecraftForge.EVENT_BUS.addListener(FluidTooltipHandler::onRecipesUpdated);
    TOOLTIP_OPTIONS.put(TinkerSmeltery.ingotCast.get(), INGOT);
    TOOLTIP_OPTIONS.put(TinkerSmeltery.nuggetCast.get(), new FluidGuiEntry("nugget", MaterialValues.NUGGET));
    TOOLTIP_OPTIONS.put(TinkerSmeltery.gemCast.get(), new FluidGuiEntry("gem", MaterialValues.GEM));
  }

  /**
   * Called when recipes are synced from the server to the client
   * @param event  Event instance
   */
  private static void onRecipesUpdated(RecipesUpdatedEvent event) {
    CACHE.clear();
  }

  /**
   * Gets the tooltip for a fluid stack
   * @param fluid  Fluid stack instance
   * @return  Fluid tooltip
   */
  public static List<ITextComponent> getFluidTooltip(FluidStack fluid) {
    return getFluidTooltip(fluid, fluid.getAmount());
  }

  /**
   * Gets the tooltip for a fluid stack
   * @param fluid  Fluid stack instance
   * @param amount Amount override
   * @return  Fluid tooltip
   */
  public static List<ITextComponent> getFluidTooltip(FluidStack fluid, int amount) {
    List<ITextComponent> tooltip = new ArrayList<>();
    // fluid name, not sure if there is a cleaner way to do this
    tooltip.add(fluid.getDisplayName().copyRaw().mergeStyle(TextFormatting.WHITE));
    // material
    appendMaterial(fluid.getFluid(), amount, tooltip);
    // add mod display name
    ModList.get().getModContainerById(Objects.requireNonNull(fluid.getFluid().getRegistryName()).getNamespace())
           .map(container -> container.getModInfo().getDisplayName())
           .ifPresent(name -> tooltip.add(new StringTextComponent(name).mergeStyle(TextFormatting.BLUE, TextFormatting.ITALIC)));
    return tooltip;
  }

  /**
   * Adds information for the tooltip based on material units
   * @param fluid    Input fluid stack
   * @param tooltip  Tooltip to append information
   */
  public static void appendMaterial(FluidStack fluid, List<ITextComponent> tooltip) {
    appendMaterial(fluid.getFluid(), fluid.getAmount(), tooltip);
  }

  /**
   * Adds information for the tooltip based on material units
   * @param fluid      Input fluid
   * @param original   Input amount
   * @param tooltip    Tooltip to append information
   */
  public static void appendMaterial(Fluid fluid, int original, List<ITextComponent> tooltip) {
    if (appendMaterialNoShift(fluid, original, tooltip)) {
      appendShift(tooltip);
    }
  }

  /**
   * Adds information for the tooltip based on material units, does not show "hold shift for buckets"
   * @param fluid      Input fluid
   * @param original   Input amount
   * @param tooltip    Tooltip to append information
   * @return  True if the amount is not in buckets
   */
  public static boolean appendMaterialNoShift(Fluid fluid, int original, List<ITextComponent> tooltip) {
    int amount = original;

    // if holding shift, skip specific units
    if(!Screen.hasShiftDown()) {
      List<FluidGuiEntry> entries = CACHE.computeIfAbsent(fluid, FluidTooltipHandler::calcFluidEntries);
      for(FluidGuiEntry entry : entries) {
        amount = entry.getText(tooltip, amount);
      }
    }

    // standard display stuff: bucket amounts
    appendBuckets(amount, tooltip);

    //
    return amount != original;
  }

  /**
   * Appends the hold shift message to the tooltip
   * @param tooltip  Tooltip to append information
   */
  public static void appendShift(List<ITextComponent> tooltip) {
    if(!Screen.hasShiftDown()) {
      tooltip.add(StringTextComponent.EMPTY);
      tooltip.add(HOLD_SHIFT);
    }
  }

  /**
   * Adds information to the tooltip based on ingot units
   * @param amount   Fluid amount
   * @param tooltip  Tooltip to append information
   */
  public static void appendIngots(int amount, List<ITextComponent> tooltip) {
    amount = INGOT.getText(tooltip, amount);
    appendBuckets(amount, tooltip);
  }

  /**
   * Adds information to the tooltip based on the fluid using bucket units
   * @param amount     Fluid amount
   * @param tooltip  Tooltip to append information
   */
  public static void appendBuckets(int amount, List<ITextComponent> tooltip) {
    amount = KILOBUCKET.getText(tooltip, amount);
    amount = BUCKET.getText(tooltip, amount);
    MILLIBUCKET.getText(tooltip, amount);
  }

  /**
   * Gets all relevant entries for a fluid
   * @param fluid  Relevant fluid
   * @return  List of entries for the fluid
   */
  private static List<FluidGuiEntry> calcFluidEntries(Fluid fluid) {
    assert Minecraft.getInstance().world != null;
    RecipeManager manager = Minecraft.getInstance().world.getRecipeManager();

    // first, search casting recipes for cast items
    List<FluidGuiEntry> list = new ArrayList<>();
    for (ItemCastingRecipe recipe : RecipeHelper.getRecipes(manager, RecipeTypes.CASTING_TABLE, ItemCastingRecipe.class)) {
      // if the fluid matches, move onto cast search
      FluidIngredient ingredient = recipe.getFluid();
      if (ingredient.test(fluid)) {
        Ingredient cast = recipe.getCast();
        // if empty, add an entry if a table recipe matches an expected unit
        if (cast == Ingredient.EMPTY) {
          FluidGuiEntry entry = fluid.isIn(TinkerTags.Fluids.SLIMELIKE) ? SLIMEBALL : PANE;
          list.add(entry.withAmount(ingredient.getAmount(fluid)));
        } else {
          // if a cast, check for a matching item in the map
          Arrays.stream(recipe.getCast().getMatchingStacks())
                .map(stack -> TOOLTIP_OPTIONS.get(stack.getItem()))
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(entry -> list.add(entry.withAmount(ingredient.getAmount(fluid))));
        }
      }
    }

    // next, iterate basin recipes to find block amounts
    for (ItemCastingRecipe recipe : RecipeHelper.getRecipes(manager, RecipeTypes.CASTING_BASIN, ItemCastingRecipe.class)) {
      // no cast, copy amount
      FluidIngredient ingredient = recipe.getFluid();
      if (recipe.getCast() == Ingredient.EMPTY && ingredient.test(fluid)) {
        list.add(BLOCK.withAmount(ingredient.getAmount(fluid)));
      }
    }

    // sort using the fluid entry comparable
    list.sort(null);
    return list;
  }

  private static class FluidGuiEntry implements Comparable<FluidGuiEntry> {
    private final String translationKey;
    @Getter
    private final int needed;

    /**
     * Creates a new fluid GUI entry
     * @param name    Base translation name
     * @param needed  Amount needed
     */
    private FluidGuiEntry(String name, int needed) {
      this.translationKey = Util.makeTranslationKey("gui", "fluid." + name);
      this.needed = needed;
    }

    /**
     * Copies an entry into another amount
     * @param parent  Parent entry
     * @param needed  New needed amount
     */
    private FluidGuiEntry(FluidGuiEntry parent, int needed) {
      this.translationKey = parent.translationKey;
      this.needed = needed;
    }

    /**
     * Gets an entry with the given amount
     * @param amount  Amount
     * @return  this if amount matches, new entry if no match
     */
    private FluidGuiEntry withAmount(int amount) {
      if (amount == this.needed) {
        return this;
      }
      return new FluidGuiEntry(this, amount);
    }

    /**
     * Gets the display text for this fluid entry
     * @return  Display text
     */
    private int getText(List<ITextComponent> tooltip, int amount) {
      int full = amount / needed;
      if (full > 0) {
        tooltip.add(new TranslationTextComponent(translationKey, full).mergeStyle(TextFormatting.GRAY));
      }
      return amount % needed;
    }

    @Override
    public int compareTo(FluidGuiEntry other) {
      if (this.needed != other.needed) {
        // reverse order so highest sorts first
        return Integer.compare(other.needed, this.needed);
      }
      // fallback to translation key, so ingot sorts before pane if both are present
      return this.translationKey.compareTo(other.translationKey);
    }
  }
}
