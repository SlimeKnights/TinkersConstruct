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
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ForgeI18n;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.library.materials.MaterialValues;
import slimeknights.tconstruct.library.recipe.FluidIngredient;
import slimeknights.tconstruct.library.recipe.RecipeTypes;
import slimeknights.tconstruct.library.recipe.RecipeUtil;
import slimeknights.tconstruct.library.recipe.casting.ICastingRecipe;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipe;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FluidTooltipHandler {
  private static final Map<Fluid,List<FluidGuiEntry>> CACHE = new HashMap<>();
  private static final String HOLD_SHIFT = Util.makeTranslationKey("gui", "fluid.hold_shift");

  /*
   * Base units
   */
  private static final FluidGuiEntry KILOBUCKET = new FluidGuiEntry("kilobucket", 1000000);
  private static final FluidGuiEntry BUCKET = new FluidGuiEntry("bucket", 1000);
  private static final FluidGuiEntry MILLIBUCKET = new FluidGuiEntry("millibucket", 1);
  private static final FluidGuiEntry INGOT = new FluidGuiEntry("ingot", MaterialValues.VALUE_Ingot);
  private static final FluidGuiEntry BLOCK = new FluidGuiEntry("block", MaterialValues.VALUE_Block);

  /** List of options to check for table cast recipes */
  private static final Map<Item,FluidGuiEntry> TOOLTIP_OPTIONS = new IdentityHashMap<>();
  /** List of options to check for table with no cast recipes */
  private static final Map<Integer,FluidGuiEntry> TABLE_TOP_OPTIONS = new HashMap<>();

  /** Initializes the tooltip handler */
  public static void init() {
    MinecraftForge.EVENT_BUS.addListener(FluidTooltipHandler::onRecipesUpdated);
    TOOLTIP_OPTIONS.put(TinkerSmeltery.ingotCast.get(), INGOT);
    TOOLTIP_OPTIONS.put(TinkerSmeltery.nuggetCast.get(), new FluidGuiEntry("nugget", MaterialValues.VALUE_Nugget));
    TOOLTIP_OPTIONS.put(TinkerSmeltery.gemCast.get(), new FluidGuiEntry("gem", MaterialValues.VALUE_Gem));
    for (FluidGuiEntry entry : new FluidGuiEntry[] {
      new FluidGuiEntry("pane", MaterialValues.VALUE_Pane),
      new FluidGuiEntry("slimeball", MaterialValues.VALUE_SlimeBall)
    }) {
      TABLE_TOP_OPTIONS.put(entry.needed, entry);
    }
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
  public static List<String> getFluidTooltip(FluidStack fluid) {
    List<String> tooltip = new ArrayList<>();
    // fluid name
    tooltip.add(fluid.getDisplayName().applyTextStyle(TextFormatting.WHITE).getString());
    // material
    appendMaterial(fluid, tooltip);
    // add mod display name
    ModList.get().getModContainerById(Objects.requireNonNull(fluid.getFluid().getRegistryName()).getNamespace())
           .map(container -> container.getModInfo().getDisplayName())
           .ifPresent(name -> tooltip.add(TextFormatting.BLUE + (TextFormatting.ITALIC + name)));
    return tooltip;
  }

  /**
   * Adds information for the tooltip based on material units
   * @param fluid    Input fluid stack
   * @param tooltip  Tooltip to append information
   */
  public static void appendMaterial(FluidStack fluid, List<String> tooltip) {
    int original = fluid.getAmount();
    int amount = original;

    // if holding shift, skip specific units
    if(!Screen.hasShiftDown()) {
      List<FluidGuiEntry> entries = CACHE.computeIfAbsent(fluid.getFluid(), FluidTooltipHandler::calcFluidEntries);
      for(FluidGuiEntry entry : entries) {
        amount = entry.getText(tooltip, amount);
      }
    }

    // standard display stuff: bucket amounts
    appendBuckets(amount, tooltip);

    // add hold shift message
    if (amount != original) {
      appendShift(tooltip);
    }
  }

  /**
   * Appends the hold shift message to the tooltip
   * @param tooltip  Tooltip to append information
   */
  public static void appendShift(List<String> tooltip) {
    if(!Screen.hasShiftDown()) {
      tooltip.add("");
      tooltip.add(TextFormatting.GRAY + ForgeI18n.getPattern(HOLD_SHIFT));
    }
  }

  /**
   * Adds information to the tooltip based on ingot units
   * @param amount   Fluid amount
   * @param tooltip  Tooltip to append information
   */
  public static void appendIngots(int amount, List<String> tooltip) {
    amount = INGOT.getText(tooltip, amount);
    appendBuckets(amount, tooltip);
  }

  /**
   * Adds information to the tooltip based on the fluid using bucket units
   * @param amount   Fluid amount
   * @param tooltip  Tooltip to append information
   */
  public static void appendBuckets(int amount, List<String> tooltip) {
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
    for (ItemCastingRecipe recipe : RecipeUtil.getRecipes(manager, RecipeTypes.CASTING_TABLE, ItemCastingRecipe.class)) {
      // if the fluid matches, move onto cast search
      FluidIngredient ingredient = recipe.getFluid();
      if (ingredient.test(fluid)) {
        Ingredient cast = recipe.getCast();
        // if empty, add an entry if a table recipe matches an expected unit
        if (cast == Ingredient.EMPTY) {
          Optional.ofNullable(TABLE_TOP_OPTIONS.get(ingredient.getAmount(fluid))).ifPresent(list::add);
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
    for (ItemCastingRecipe recipe : RecipeUtil.getRecipes(manager, RecipeTypes.CASTING_BASIN, ItemCastingRecipe.class)) {
      // no cast, copy amount
      FluidIngredient ingredient = recipe.getFluid();
      if (recipe.getCast() == Ingredient.EMPTY && ingredient.test(fluid)) {
        list.add(BLOCK.withAmount(ingredient.getAmount(fluid)));
      }
    }

    // important that the largest value is first, as that is how the entries are processed
    list.sort(Collections.reverseOrder(Comparator.comparingInt(FluidGuiEntry::getNeeded)));
    return list;
  }

  private static class FluidGuiEntry {
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
    public FluidGuiEntry withAmount(int amount) {
      if (amount == this.needed) {
        return this;
      }
      return new FluidGuiEntry(this, amount);
    }

    /**
     * Gets the display text for this fluid entry
     * @return  Display text
     */
    private int getText(List<String> tooltip, int amount) {
      int full = amount / needed;
      if (full > 0) {
        tooltip.add(TextFormatting.GRAY + ForgeI18n.parseMessage(translationKey, full));
      }
      return amount % needed;
    }
  }
}
