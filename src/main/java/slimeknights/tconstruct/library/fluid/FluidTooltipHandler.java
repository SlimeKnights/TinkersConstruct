package slimeknights.tconstruct.library.fluid;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.ModList;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.library.utils.SafeClientAccess;
import slimeknights.tconstruct.library.utils.TooltipKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static slimeknights.tconstruct.common.TinkerTags.Fluids.CLAY_TOOLTIPS;
import static slimeknights.tconstruct.common.TinkerTags.Fluids.GLASS_TOOLTIPS;
import static slimeknights.tconstruct.common.TinkerTags.Fluids.LARGE_GEM_TOOLTIPS;
import static slimeknights.tconstruct.common.TinkerTags.Fluids.METAL_TOOLTIPS;
import static slimeknights.tconstruct.common.TinkerTags.Fluids.SLIME_TOOLTIPS;
import static slimeknights.tconstruct.common.TinkerTags.Fluids.SMALL_GEM_TOOLTIPS;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FluidTooltipHandler {
  public static final Component HOLD_SHIFT = new TranslatableComponent(TConstruct.makeTranslationKey("gui", "fluid.hold_shift")).withStyle(ChatFormatting.GRAY);

  /*
   * Base units
   */
  private static final FluidGuiEntry KILOBUCKET = new FluidGuiEntry("kilobucket", 1000000);
  private static final FluidGuiEntry BUCKET = new FluidGuiEntry("bucket", 1000);
  private static final FluidGuiEntry MILLIBUCKET = new FluidGuiEntry("millibucket", 1);
  // metal
  private static final FluidGuiEntry METAL_BLOCK = new FluidGuiEntry("block", FluidValues.METAL_BLOCK);
  private static final FluidGuiEntry INGOT = new FluidGuiEntry("ingot", FluidValues.INGOT);
  private static final FluidGuiEntry NUGGET = new FluidGuiEntry("nugget", FluidValues.NUGGET);
  // gems
  private static final FluidGuiEntry LARGE_GEM_BLOCK = new FluidGuiEntry("block", FluidValues.LARGE_GEM_BLOCK);
  private static final FluidGuiEntry SMALL_GEM_BLOCK = new FluidGuiEntry("block", FluidValues.SMALL_GEM_BLOCK);
  private static final FluidGuiEntry GEM = new FluidGuiEntry("gem", FluidValues.GEM);
  private static final FluidGuiEntry SHARDS = new FluidGuiEntry("shard", FluidValues.GEM_SHARD);
  // clay
  private static final FluidGuiEntry BRICK_BLOCK = new FluidGuiEntry("block", FluidValues.BRICK_BLOCK);
  private static final FluidGuiEntry BRICK = new FluidGuiEntry("brick", FluidValues.BRICK);
  // slime
  private static final FluidGuiEntry SLIMEBLOCK = new FluidGuiEntry("block", FluidValues.SLIME_CONGEALED);
  private static final FluidGuiEntry SLIMEBALL = new FluidGuiEntry("slimeball", FluidValues.SLIMEBALL);
  // glass
  private static final FluidGuiEntry GLASS_BLOCK = new FluidGuiEntry("block", FluidValues.GLASS_BLOCK);
  private static final FluidGuiEntry PANE = new FluidGuiEntry("pane", FluidValues.GLASS_PANE);

  /**
   * Gets the tooltip for a fluid stack
   * @param fluid  Fluid stack instance
   * @return  Fluid tooltip
   */
  public static List<Component> getFluidTooltip(FluidStack fluid) {
    return getFluidTooltip(fluid, fluid.getAmount());
  }

  /**
   * Gets the tooltip for a fluid stack
   * @param fluid  Fluid stack instance
   * @param amount Amount override
   * @return  Fluid tooltip
   */
  public static List<Component> getFluidTooltip(FluidStack fluid, int amount) {
    List<Component> tooltip = new ArrayList<>();
    // fluid name, not sure if there is a cleaner way to do this
    tooltip.add(fluid.getDisplayName().plainCopy().withStyle(ChatFormatting.WHITE));
    // material
    appendMaterial(fluid.getFluid(), amount, tooltip);
    // add mod display name
    ModList.get().getModContainerById(Objects.requireNonNull(fluid.getFluid().getRegistryName()).getNamespace())
           .map(container -> container.getModInfo().getDisplayName())
           .ifPresent(name -> tooltip.add(new TextComponent(name).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)));
    return tooltip;
  }

  /**
   * Adds information for the tooltip based on material units
   * @param fluid    Input fluid stack
   * @param tooltip  Tooltip to append information
   */
  public static void appendMaterial(FluidStack fluid, List<Component> tooltip) {
    appendMaterial(fluid.getFluid(), fluid.getAmount(), tooltip);
  }

  /**
   * Adds information for the tooltip based on material units
   * @param fluid      Input fluid
   * @param original   Input amount
   * @param tooltip    Tooltip to append information
   */
  public static void appendMaterial(Fluid fluid, int original, List<Component> tooltip) {
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
  public static boolean appendMaterialNoShift(Fluid fluid, int original, List<Component> tooltip) {
    int amount = original;

    // if holding shift, skip specific units
    if(SafeClientAccess.getTooltipKey() != TooltipKey.SHIFT) {
      if (fluid.is(METAL_TOOLTIPS)) {
        amount = METAL_BLOCK.getText(tooltip, amount);
        amount = INGOT.getText(tooltip, amount);
        amount = NUGGET.getText(tooltip, amount);
      } else if (fluid.is(LARGE_GEM_TOOLTIPS)) {
        amount = LARGE_GEM_BLOCK.getText(tooltip, amount);
        amount = GEM.getText(tooltip, amount);
        amount = SHARDS.getText(tooltip, amount);
      } else if (fluid.is(SMALL_GEM_TOOLTIPS)) {
        amount = SMALL_GEM_BLOCK.getText(tooltip, amount);
        amount = GEM.getText(tooltip, amount);
        amount = SHARDS.getText(tooltip, amount);
      } else if (fluid.is(GLASS_TOOLTIPS)) {
        amount = GLASS_BLOCK.getText(tooltip, amount);
        amount = PANE.getText(tooltip, amount);
      } else if (fluid.is(SLIME_TOOLTIPS)) {
        amount = SLIMEBLOCK.getText(tooltip, amount);
        amount = SLIMEBALL.getText(tooltip, amount);
      } else if (fluid.is(CLAY_TOOLTIPS)) {
        amount = BRICK_BLOCK.getText(tooltip, amount);
        amount = BRICK.getText(tooltip, amount);
      }
    }

    // standard display stuff: bucket amounts
    appendBuckets(amount, tooltip);

    return amount != original;
  }

  /**
   * Appends the hold shift message to the tooltip
   * @param tooltip  Tooltip to append information
   */
  public static void appendShift(List<Component> tooltip) {
    if(!SafeClientAccess.getTooltipKey().isShiftOrUnknown()) {
      tooltip.add(TextComponent.EMPTY);
      tooltip.add(HOLD_SHIFT);
    }
  }

  /**
   * Adds information to the tooltip based on ingot units
   * @param amount   Fluid amount
   * @param tooltip  Tooltip to append information
   */
  public static void appendIngots(int amount, List<Component> tooltip) {
    amount = INGOT.getText(tooltip, amount);
    appendBuckets(amount, tooltip);
  }

  /**
   * Adds information to the tooltip based on the fluid using bucket units
   * @param amount     Fluid amount
   * @param tooltip  Tooltip to append information
   */
  public static void appendBuckets(int amount, List<Component> tooltip) {
    amount = KILOBUCKET.getText(tooltip, amount);
    amount = BUCKET.getText(tooltip, amount);
    MILLIBUCKET.getText(tooltip, amount);
  }

  /** Single entry for text options */
  private record FluidGuiEntry(String translationKey, int needed) implements Comparable<FluidGuiEntry> {
    /**
     * Creates a new fluid GUI entry
     * @param translationKey  Base translation name
     * @param needed          Amount needed
     */
    private FluidGuiEntry(String translationKey, int needed) {
      this.translationKey = TConstruct.makeTranslationKey("gui", "fluid." + translationKey);
      this.needed = needed;
    }

    /**
     * Gets the display text for this fluid entry
     * @return  Display text
     */
    private int getText(List<Component> tooltip, int amount) {
      int full = amount / needed;
      if (full > 0) {
        tooltip.add(new TranslatableComponent(translationKey, full).withStyle(ChatFormatting.GRAY));
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
