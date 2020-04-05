package slimeknights.tconstruct.tileentities;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.TableBlocks;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.tables.tileentity.chest.PartChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.chest.PatternChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

import java.util.function.Supplier;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TablesTileEntities {

  public static final TileEntityType<CraftingStationTileEntity> crafting_station = injected();

  public static final TileEntityType<PartBuilderTileEntity> part_builder = injected();

  public static final TileEntityType<PatternChestTileEntity> pattern_chest = injected();
  public static final TileEntityType<PartChestTileEntity> part_chest = injected();

  @SubscribeEvent
  static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
    BaseRegistryAdapter<TileEntityType<?>> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(TileEntityType.Builder.create((Supplier<TileEntity>) CraftingStationTileEntity::new, TableBlocks.crafting_station).build(null), "crafting_station");

    registry.register(TileEntityType.Builder.create((Supplier<TileEntity>) PartBuilderTileEntity::new, TableBlocks.part_builder).build(null), "part_builder");

    registry.register(TileEntityType.Builder.create((Supplier<TileEntity>) PatternChestTileEntity::new, TableBlocks.pattern_chest).build(null), "pattern_chest");
    registry.register(TileEntityType.Builder.create((Supplier<TileEntity>) PartChestTileEntity::new, TableBlocks.pattern_chest).build(null), "part_chest");
  }
}
