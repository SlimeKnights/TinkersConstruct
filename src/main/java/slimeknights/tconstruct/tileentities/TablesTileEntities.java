package slimeknights.tconstruct.tileentities;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.TableBlocks;
import slimeknights.tconstruct.library.registration.TileEntityTypeDeferredRegister;
import slimeknights.tconstruct.tables.tileentity.chest.PartChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.chest.PatternChestTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.CraftingStationTileEntity;
import slimeknights.tconstruct.tables.tileentity.table.PartBuilderTileEntity;

@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TablesTileEntities {
  private static final TileEntityTypeDeferredRegister TILE_ENTITIES = new TileEntityTypeDeferredRegister(TConstruct.modID);

  public static void init() {
    TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
  }

  public static final RegistryObject<TileEntityType<CraftingStationTileEntity>> crafting_station = TILE_ENTITIES.register("crafting_station", CraftingStationTileEntity::new, () -> TableBlocks.crafting_station);
  public static final RegistryObject<TileEntityType<PartBuilderTileEntity>> part_builder = TILE_ENTITIES.register("part_builder", PartBuilderTileEntity::new, () -> TableBlocks.part_builder);
  public static final RegistryObject<TileEntityType<PatternChestTileEntity>> pattern_chest = TILE_ENTITIES.register("pattern_chest", PatternChestTileEntity::new, () -> TableBlocks.pattern_chest);
  public static final RegistryObject<TileEntityType<PartChestTileEntity>> part_chest = TILE_ENTITIES.register("part_chest", PartChestTileEntity::new, () -> TableBlocks.part_chest);
}
