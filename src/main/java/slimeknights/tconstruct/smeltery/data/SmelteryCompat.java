package slimeknights.tconstruct.smeltery.data;

import lombok.Getter;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.fluids.TinkerFluids;
import slimeknights.tconstruct.library.materials.MaterialRegistry;
import slimeknights.tconstruct.library.materials.definition.IMaterial;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.tools.data.material.MaterialIds;

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
  ALUMINUM(TinkerFluids.moltenAluminum, CompatType.ORE, MaterialIds.aluminum),
  LEAD    (TinkerFluids.moltenLead,     CompatType.ORE, MaterialIds.lead),
  SILVER  (TinkerFluids.moltenSilver,   CompatType.ORE, MaterialIds.silver),
  NICKEL  (TinkerFluids.moltenNickel,   CompatType.ORE),
  ZINC    (TinkerFluids.moltenZinc,     CompatType.ORE),
  PLATINUM(TinkerFluids.moltenPlatinum, CompatType.ORE),
  TUNGSTEN(TinkerFluids.moltenTungsten, CompatType.ORE),
  OSMIUM  (TinkerFluids.moltenOsmium,   CompatType.ORE, MaterialIds.osmium),
  URANIUM (TinkerFluids.moltenUranium,  CompatType.ORE, MaterialIds.necronium),
  CHROMIUM(TinkerFluids.moltenChromium, CompatType.ORE),
  CADMIUM (TinkerFluids.moltenCadmium,  CompatType.ORE),
  // alloys
  BRONZE    (TinkerFluids.moltenBronze,     CompatType.ALLOY, MaterialIds.bronze),
  BRASS     (TinkerFluids.moltenBrass,      CompatType.ALLOY, MaterialIds.platedSlimewood),
  ELECTRUM  (TinkerFluids.moltenElectrum,   CompatType.ALLOY, MaterialIds.electrum),
  INVAR     (TinkerFluids.moltenInvar,      CompatType.ALLOY, MaterialIds.invar),
  CONSTANTAN(TinkerFluids.moltenConstantan, CompatType.ALLOY, MaterialIds.constantan),
  PEWTER    (TinkerFluids.moltenPewter,     CompatType.ALLOY, MaterialIds.pewter),
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
  STEELEAF(TinkerFluids.moltenSteeleaf, CompatType.NONE,  MaterialIds.steeleaf),
  FIERY   (TinkerFluids.fieryLiquid,    CompatType.ALLOY, MaterialIds.fiery),;

  @Getter
  private final String name = this.name().toLowerCase(Locale.US);
  private final FluidObject<? extends ForgeFlowingFluid> fluid;
  @Getter
  private final CompatType type;
  /** @deprecated use {@link #isPresent()}. No longer does anything. */
  @Deprecated(forRemoval = true)
  @Getter
  private final String[] tags = new String[0];
  /** Material that must be present to show this compat. */
  @Nullable
  private final MaterialId material;

  /** Creates a compat using a material for the JEI condition. Will show fluids if the material is present. */
  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, CompatType type, @Nullable MaterialId material) {
    this.fluid = fluid;
    this.type = type;
    this.material = material;
  }

  /** Creates compat using just the local name as JEI condition. */
  SmelteryCompat(FluidObject<? extends ForgeFlowingFluid> fluid, CompatType type) {
    this(fluid, type, null);
  }

  /** @deprecated use {@link #getType()} */
  @Deprecated(forRemoval = true)
  public boolean isOre() {
    return type == CompatType.ORE;
  }

  /** @deprecated use {@link #isPresent()} */
  @Deprecated(forRemoval = true)
  public String getAltTag() {
    return "";
  }

  /** Gets teh fluid for this compat */
  public FluidObject<?> getFluid() {
    return fluid;
  }

  /** Checks if this compat is present */
  public boolean isPresent() {
    // if given a material, that being present means we show
    // though still allow the ingot to be present for the sake of disabling materials
    if (material != null && MaterialRegistry.getMaterial(material) != IMaterial.UNKNOWN) {
      return true;
    }

    // if our ingot is present, we good
    return ingotPresent(this.name);
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
