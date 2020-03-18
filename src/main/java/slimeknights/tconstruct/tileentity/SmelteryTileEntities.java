package slimeknights.tconstruct.tileentity;

import com.google.common.collect.Sets;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.SmelteryBlocks;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.smeltery.tileentity.SmelteryComponentTileEntity;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
@Mod.EventBusSubscriber(modid = TConstruct.modID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SmelteryTileEntities {

  public static final TileEntityType<SmelteryComponentTileEntity> SMELTERY_COMPONENT = injected();

  @SubscribeEvent
  static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
    BaseRegistryAdapter<TileEntityType<?>> registry = new BaseRegistryAdapter<>(event.getRegistry());

    registry.register(new TileEntityType<>(SmelteryComponentTileEntity::new, Sets.newHashSet(SmelteryBlocks.seared_stone, SmelteryBlocks.seared_cobble, SmelteryBlocks.seared_bricks,
      SmelteryBlocks.seared_cracked_bricks, SmelteryBlocks.seared_fancy_bricks, SmelteryBlocks.seared_square_bricks, SmelteryBlocks.seared_small_bricks,
      SmelteryBlocks.seared_triangle_bricks, SmelteryBlocks.seared_creeper, SmelteryBlocks.seared_paver, SmelteryBlocks.seared_road, SmelteryBlocks.seared_tile,
      SmelteryBlocks.seared_stone_slab, SmelteryBlocks.seared_cobble_slab, SmelteryBlocks.seared_bricks_slab, SmelteryBlocks.seared_cracked_bricks_slab, SmelteryBlocks.seared_fancy_bricks_slab,
      SmelteryBlocks.seared_square_bricks_slab, SmelteryBlocks.seared_small_bricks_slab, SmelteryBlocks.seared_triangle_bricks_slab, SmelteryBlocks.seared_creeper_slab, SmelteryBlocks.seared_paver_slab,
      SmelteryBlocks.seared_road_slab, SmelteryBlocks.seared_tile_slab, SmelteryBlocks.seared_stone_stairs, SmelteryBlocks.seared_cobble_stairs, SmelteryBlocks.seared_bricks_stairs, SmelteryBlocks.seared_cracked_bricks_stairs,
      SmelteryBlocks.seared_fancy_bricks_stairs, SmelteryBlocks.seared_square_bricks_stairs, SmelteryBlocks.seared_small_bricks_stairs, SmelteryBlocks.seared_triangle_bricks_stairs, SmelteryBlocks.seared_creeper_stairs, SmelteryBlocks.seared_paver_stairs,
      SmelteryBlocks.seared_road_stairs, SmelteryBlocks.seared_tile_stairs, SmelteryBlocks.seared_glass), null), "smeltery_component");
  }
}
