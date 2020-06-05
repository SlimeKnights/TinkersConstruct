package slimeknights.tconstruct.tileentity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.SmelteryBlocks;
import slimeknights.tconstruct.library.registration.TileEntityTypeDeferredRegister;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;

@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SmelteryTileEntities {

  private static final TileEntityTypeDeferredRegister TILE_ENTITIES = new TileEntityTypeDeferredRegister(TConstruct.modID);

  public static void init() {
    TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
  }

  public static final RegistryObject<TileEntityType<SmelteryComponentTileEntity>> SMELTERY_COMPONENT = TILE_ENTITIES.register("smeltery_component", SmelteryComponentTileEntity::new, (set) -> {
    set.addAll(SmelteryBlocks.seared_stone.values());
    set.addAll(SmelteryBlocks.seared_cobble.values());
    set.addAll(SmelteryBlocks.seared_bricks.values());
    set.addAll(SmelteryBlocks.seared_cracked_bricks.values());
    set.addAll(SmelteryBlocks.seared_fancy_bricks.values());
    set.addAll(SmelteryBlocks.seared_square_bricks.values());
    set.addAll(SmelteryBlocks.seared_small_bricks.values());
    set.addAll(SmelteryBlocks.seared_triangle_bricks.values());
    set.addAll(SmelteryBlocks.seared_creeper.values());
    set.addAll(SmelteryBlocks.seared_paver.values());
    set.addAll(SmelteryBlocks.seared_road.values());
    set.addAll(SmelteryBlocks.seared_tile.values());
  });
}
