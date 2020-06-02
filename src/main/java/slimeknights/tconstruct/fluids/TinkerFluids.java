package slimeknights.tconstruct.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.Util;

@Pulse(id = TinkerPulseIds.TINKER_FLUIDS_PULSE_ID, forced = true)
public class TinkerFluids extends TinkerPulse {

  private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, TConstruct.modID);
  private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, TConstruct.modID);
  private static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, TConstruct.modID);

  public static RegistryObject<FlowingFluid> blue_slime_fluid = FLUIDS.register("blue_slime_fluid", () -> new SlimeFluid.Source(TinkerFluids.blue_slime_fluid_properties));
  public static RegistryObject<FlowingFluid> blue_slime_fluid_flowing = FLUIDS.register("blue_slime_fluid_flowing", () -> new SlimeFluid.Flowing(TinkerFluids.blue_slime_fluid_properties));
  public static RegistryObject<FlowingFluidBlock> blue_slime_fluid_block = BLOCKS.register("blue_slime_fluid_block", () -> new FlowingFluidBlock(blue_slime_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
  public static RegistryObject<Item> blue_slime_fluid_bucket = ITEMS.register("blue_slime_bucket", () -> new BucketItem(blue_slime_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC)));
  public static final SlimeFluid.Properties blue_slime_fluid_properties = new SlimeFluid.Properties(blue_slime_fluid, blue_slime_fluid_flowing, FluidAttributes.builder(FluidIcons.FLUID_STILL, FluidIcons.FLUID_FLOWING).color(0xef67f0f5).density(1500).viscosity(1500).temperature(310)).explosionResistance(100.0F).bucket(blue_slime_fluid_bucket).block(blue_slime_fluid_block);

  public static RegistryObject<FlowingFluid> purple_slime_fluid = FLUIDS.register("purple_slime_fluid", () -> new SlimeFluid.Source(TinkerFluids.purple_slime_fluid_properties));
  public static RegistryObject<FlowingFluid> purple_slime_fluid_flowing = FLUIDS.register("purple_slime_fluid_flowing", () -> new SlimeFluid.Flowing(TinkerFluids.purple_slime_fluid_properties));
  public static RegistryObject<FlowingFluidBlock> purple_slime_fluid_block = BLOCKS.register("purple_slime_fluid_block", () -> new FlowingFluidBlock(purple_slime_fluid, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()));
  public static RegistryObject<Item> purple_slime_fluid_bucket = ITEMS.register("purple_slime_bucket", () -> new BucketItem(purple_slime_fluid, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC)));
  public static final SlimeFluid.Properties purple_slime_fluid_properties = new SlimeFluid.Properties(purple_slime_fluid, purple_slime_fluid_flowing, FluidAttributes.builder(FluidIcons.FLUID_STILL, FluidIcons.FLUID_FLOWING).color(0xefd236ff).density(1600).viscosity(1600).temperature(370)).explosionResistance(100.0F).bucket(purple_slime_fluid_bucket).block(purple_slime_fluid_block);

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_FLUIDS_PULSE_ID);

  public TinkerFluids() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    BLOCKS.register(modEventBus);
    ITEMS.register(modEventBus);
    FLUIDS.register(modEventBus);
  }

  public static int applyAlphaIfNotPresent(int color) {
    if (((color >> 24) & 0xFF) == 0) {
      color |= 0xFF << 24;
    }

    return color;
  }
}
