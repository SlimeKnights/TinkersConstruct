package slimeknights.tconstruct.fluids;

import net.minecraft.block.material.Material;
import net.minecraft.item.Items;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.fluids.fluids.SlimeFluid;
import slimeknights.tconstruct.fluids.fluids.UnplaceableFluid;
import slimeknights.tconstruct.library.Util;

/**
 * Contains all fluids used throughout the mod
 */
public final class TinkerFluids extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_fluids");

  // basic
  public static final RegistryObject<UnplaceableFluid> milk = FLUIDS.registerFluid("milk", () -> new UnplaceableFluid(() -> Items.MILK_BUCKET, FluidAttributes.builder(FluidIcons.MILK_STILL, FluidIcons.MILK_FLOWING).density(1050).viscosity(1050).temperature(320)));
  public static final FluidObject<ForgeFlowingFluid> blood = FLUIDS.register("blood", FluidAttributes.builder(FluidIcons.LIQUID_STILL, FluidIcons.LIQUID_FLOWING).color(0xff540000).density(1200).viscosity(1200).temperature(336), Material.WATER, 0);

  // slime
  public static final FluidObject<ForgeFlowingFluid> slime = FLUIDS.register("slime", FluidAttributes.builder(FluidIcons.LIQUID_STILL, FluidIcons.LIQUID_FLOWING).color(0xef3bff3b).density(1500).viscosity(1500).temperature(310), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<ForgeFlowingFluid> blueSlime = FLUIDS.register("blue_slime", FluidAttributes.builder(FluidIcons.LIQUID_STILL, FluidIcons.LIQUID_FLOWING).color(0xef67f0f5).density(1500).viscosity(1500).temperature(310), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<ForgeFlowingFluid> magmaSlime = FLUIDS.register("magma_slime", FluidAttributes.builder(FluidIcons.LIQUID_STILL, FluidIcons.LIQUID_FLOWING).color(0xefc67604).density(800).viscosity(800).temperature(900), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 15);
  public static final FluidObject<ForgeFlowingFluid> purpleSlime = FLUIDS.register("purple_slime", FluidAttributes.builder(FluidIcons.LIQUID_STILL, FluidIcons.LIQUID_FLOWING).color(0xefd236ff).density(1600).viscosity(1600).temperature(370), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 0);
  public static final FluidObject<ForgeFlowingFluid> rainbowSlime = FLUIDS.register("rainbow_slime", FluidAttributes.builder(FluidIcons.LIQUID_STILL, FluidIcons.LIQUID_FLOWING).color(0xdfffffff).density(2400).viscosity(2400).temperature(630), SlimeFluid.Source::new, SlimeFluid.Flowing::new, Material.WATER, 8);

  // molten
  public static final FluidObject<ForgeFlowingFluid> searedStone = FLUIDS.register("seared_stone", stoneBuilder().color(0xff777777).temperature(800), Material.LAVA, 7);
  public static final FluidObject<ForgeFlowingFluid> moltenGlass = FLUIDS.register("molten_glass", moltenBuilder().color(0xffc0f5fe).temperature(625), Material.LAVA, 10);
  public static final FluidObject<ForgeFlowingFluid> moltenObsidian = FLUIDS.register("molten_obsidian", stoneBuilder().color(0xff2c0d59).temperature(1000), Material.LAVA, 11);

  // metals
  public static final FluidObject<ForgeFlowingFluid> moltenIron = FLUIDS.register("molten_iron", moltenBuilder().color(0xffa81212).temperature(769), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenGold = FLUIDS.register("molten_gold", moltenBuilder().color(0xfff6d609).temperature(532), Material.LAVA, 13);
  public static final FluidObject<ForgeFlowingFluid> moltenCopper = FLUIDS.register("molten_copper", moltenBuilder().color(0xffed9f07).temperature(542), Material.LAVA, 11);
  public static final FluidObject<ForgeFlowingFluid> moltenCobalt = FLUIDS.register("molten_cobalt", moltenBuilder().color(0xff2882d4).temperature(950), Material.LAVA, 10);
  public static final FluidObject<ForgeFlowingFluid> moltenArdite = FLUIDS.register("molten_ardite", moltenBuilder().color(0xffd14210).temperature(860), Material.LAVA, 8);
  // alloys
  public static final FluidObject<ForgeFlowingFluid> moltenRoseGold = FLUIDS.register("molten_rose_gold", moltenBuilder().color(0xffbf8a71).temperature(537), Material.LAVA, 12);
  public static final FluidObject<ForgeFlowingFluid> moltenManyullyn = FLUIDS.register("molten_manyullyn", moltenBuilder().color(0xffa15cf8).temperature(1000), Material.LAVA, 9);
  public static final FluidObject<ForgeFlowingFluid> moltenPigIron = FLUIDS.register("molten_pig_iron", moltenBuilder().color(0xffef9e9b).temperature(600), Material.LAVA, 10);
  public static final FluidObject<ForgeFlowingFluid> moltenKnightslime = FLUIDS.register("molten_knightslime", moltenBuilder().color(0xfff18ff0).temperature(520), Material.LAVA, 9);

  public static final FluidObject<ForgeFlowingFluid> moltenBronze = FLUIDS.register("molten_bronze", moltenBuilder().color(0xffd2a300).temperature(478), Material.LAVA, 9);
  public static final FluidObject<ForgeFlowingFluid> moltenRavagerSteel = FLUIDS.register("molten_ravager_steel", moltenBuilder().color(0xffb2cbcd).temperature(769), Material.LAVA, 9);
  public static final FluidObject<ForgeFlowingFluid> moltenSoulSteel = FLUIDS.register("molten_soul_steel", moltenBuilder().color(0xff96917e).temperature(839), Material.LAVA, 9);
  public static final FluidObject<ForgeFlowingFluid> moltenHeptazion = FLUIDS.register("molten_heptazion", moltenBuilder().color(0xff6c1291).temperature(980), Material.LAVA, 9);
  public static final FluidObject<ForgeFlowingFluid> moltenSlimeBronze = FLUIDS.register("molten_slime_bronze", moltenBuilder().color(0xffd2a300).temperature(478), Material.LAVA, 9);
  public static final FluidObject<ForgeFlowingFluid> moltenKnightMetal = FLUIDS.register("molten_knight_metal", moltenBuilder().color(0xff96917e).temperature(620), Material.LAVA, 9);
  public static final FluidObject<ForgeFlowingFluid> moltenSlimeSteel = FLUIDS.register("molten_slime_steel", moltenBuilder().color(0xffb2cbcd).temperature(478), Material.LAVA, 9);

  /** Creates a builder for a molten fluid */
  private static FluidAttributes.Builder stoneBuilder() {
    return FluidAttributes.builder(FluidIcons.STONE_STILL, FluidIcons.STONE_FLOWING).density(2000).viscosity(10000).temperature(1000);
  }

  /** Creates a builder for a molten fluid */
  private static FluidAttributes.Builder moltenBuilder() {
    return FluidAttributes.builder(FluidIcons.MOLTEN_STILL, FluidIcons.MOLTEN_FLOWING).density(2000).viscosity(10000).temperature(1000);
  }
}
