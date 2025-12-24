package slimeknights.tconstruct.smeltery.data;

import lombok.Getter;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.ApiStatus.Internal;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.Locale;
import java.util.Optional;

import static slimeknights.mantle.Mantle.commonResource;

/**
 * Enum holding all relevant smeltery compat, used in datagen and JEI.
 * Internal usage - you can do all the same things this does through your own datagen easily.
 * @see slimeknights.tconstruct.library.data.recipe.SmelteryRecipeBuilder
 */
@Internal
public enum SmelteryCompat {
  // ores
  TIN     (TinkerFluids.moltenTin,      CompatType.ORE),
  ALUMINUM(TinkerFluids.moltenAluminum, CompatType.ORE),
  LEAD    (TinkerFluids.moltenLead,     CompatType.ORE),
  SILVER  (TinkerFluids.moltenSilver,   CompatType.ORE),
  NICKEL  (TinkerFluids.moltenNickel,   CompatType.ORE),
  ZINC    (TinkerFluids.moltenZinc,     CompatType.ORE),
  PLATINUM(TinkerFluids.moltenPlatinum, CompatType.ORE),
  TUNGSTEN(TinkerFluids.moltenTungsten, CompatType.ORE),
  OSMIUM  (TinkerFluids.moltenOsmium,   CompatType.ORE),
  URANIUM (TinkerFluids.moltenUranium,  CompatType.ORE),
  CHROMIUM(TinkerFluids.moltenChromium, CompatType.ORE),
  CADMIUM (TinkerFluids.moltenCadmium,  CompatType.ORE),
  // alloys
  BRONZE    (TinkerFluids.moltenBronze, "tin"),
  BRASS     (TinkerFluids.moltenBrass, "zinc"),
  ELECTRUM  (TinkerFluids.moltenElectrum, "silver"),
  INVAR     (TinkerFluids.moltenInvar, "nickel"),
  CONSTANTAN(TinkerFluids.moltenConstantan, "nickel"),
  PEWTER    (TinkerFluids.moltenPewter, "tin", "lead"),
  // thermal alloys
  ENDERIUM(TinkerFluids.moltenEnderium, CompatType.ALLOY),
  LUMIUM  (TinkerFluids.moltenLumium,   CompatType.ALLOY),
  SIGNALUM(TinkerFluids.moltenSignalum, CompatType.ALLOY),
  // mekanism alloys
  REFINED_GLOWSTONE(TinkerFluids.moltenRefinedGlowstone, CompatType.ALLOY),
  REFINED_OBSIDIAN (TinkerFluids.moltenRefinedObsidian,  CompatType.ALLOY),
  // cosmere
  NICROSIL(TinkerFluids.moltenNicrosil,   CompatType.ALLOY),
  DURALUMIN(TinkerFluids.moltenDuralumin, CompatType.ALLOY),
  BENDALLOY(TinkerFluids.moltenBendalloy, CompatType.ALLOY),
  // twilight
  STEELEAF(TinkerFluids.moltenSteeleaf, CompatType.NONE),
  FIERY   (TinkerFluids.fieryLiquid,    CompatType.ALLOY);

  @Getter
  private final String name = this.name().toLowerCase(Locale.US);
  private final FluidObject<? extends ForgeFlowingFluid> fluid;
  @Getter
  private final CompatType type;
  /** @deprecated use {@link #isPresent()} */
  @SuppressWarnings("DeprecatedIsStillUsed")
  @Deprecated
  @Getter
  private final String[] tags;

  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, CompatType type) {
    this.fluid = fluid;
    this.type = type;
    this.tags = new String[0];
  }

  /** Byproducts means its an ore, no byproucts are alloys */
  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, String... altTags) {
    this.fluid = fluid;
    this.type = CompatType.ALLOY;
    this.tags = altTags;
  }

  /** @deprecated use {@link #getType()} */
  @Deprecated(forRemoval = true)
  public boolean isOre() {
    return type == CompatType.ORE;
  }

  /** @deprecated use {@link #isPresent()} */
  @Deprecated(forRemoval = true)
  public String getAltTag() {
    return tags.length == 0 ? "" : tags[0];
  }

  /** Gets teh fluid for this compat */
  public FluidObject<?> getFluid() {
    return fluid;
  }

  /** Checks if this compat is present */
  public boolean isPresent() {
    // if our ingot is present, we good
    if (ingotPresent(this.name)) {
      return true;
    }
    // if any of the alloy components is present, only show if the config option is also enabled
    if (tags.length > 0 && Config.COMMON.allowIngotlessAlloys.get()) {
      for (String tag : tags) {
        if (ingotPresent(tag)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Helper for tracking types of ores */
  public enum CompatType {
    /** Fluid has ores, and should get support from lustrous */
    ORE,
    /** Fluid is an alloy, and should get an anvil variant */
    ALLOY,
    /** Fluid is neither ore nor alloy */
    NONE
  }

  /** Checks if the given tag exists */
  @SuppressWarnings("deprecation")
  private static boolean ingotPresent(String name) {
    Optional<Named<Item>> tag = BuiltInRegistries.ITEM.getTag(ItemTags.create(commonResource("ingots/" + name)));
    return tag.isPresent() && tag.get().size() > 0;
  }
}
