package slimeknights.tconstruct.containers;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.tables.inventory.chest.PartChestContainer;
import slimeknights.tconstruct.tables.inventory.chest.PatternChestContainer;
import slimeknights.tconstruct.tables.inventory.table.PartBuilderContainer;
import slimeknights.tconstruct.tables.inventory.table.crafting.CraftingStationContainer;
import slimeknights.tconstruct.tables.tileentity.chest.PartChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.chest.PatternChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TableContainerTypes {

  private static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, TConstruct.modID);

  public static void init() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

    CONTAINERS.register(bus);
  }

  public static final RegistryObject<ContainerType<CraftingStationContainer>> crafting_station = CONTAINERS.register("crafting_station", () -> IForgeContainerType.create(((windowId, inv, data) -> {
    BlockPos pos = data.readBlockPos();
    return new CraftingStationContainer(windowId, inv, new CraftingStationTileEntity());
  })));
  public static final RegistryObject<ContainerType<PartBuilderContainer>> part_builder = CONTAINERS.register("part_builder", () -> IForgeContainerType.create(((windowId, inv, data) -> {
    BlockPos pos = data.readBlockPos();
    return new PartBuilderContainer(windowId, inv, new PartBuilderTileEntity());
  })));
  public static final RegistryObject<ContainerType<PatternChestContainer>> pattern_chest = CONTAINERS.register("pattern_chest", () -> IForgeContainerType.create(((windowId, inv, data) -> {
    BlockPos pos = data.readBlockPos();
    return new PatternChestContainer(windowId, inv, new PatternChestTileEntity());
  })));
  public static final RegistryObject<ContainerType<PartChestContainer>> part_chest = CONTAINERS.register("part_chest", () -> IForgeContainerType.create(((windowId, inv, data) -> {
    BlockPos pos = data.readBlockPos();
    return new PartChestContainer(windowId, inv, new PartChestTileEntity());
  })));
}
