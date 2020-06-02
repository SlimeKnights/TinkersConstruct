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

    registry.register(new TileEntityType<>(SmelteryComponentTileEntity::new, Sets.newHashSet(SmelteryBlocks.seared_stone.get(), SmelteryBlocks.seared_cobble.get(), SmelteryBlocks.seared_bricks.get(),
      SmelteryBlocks.seared_cracked_bricks.get(), SmelteryBlocks.seared_fancy_bricks.get(), SmelteryBlocks.seared_square_bricks.get(), SmelteryBlocks.seared_small_bricks.get(),
      SmelteryBlocks.seared_triangle_bricks.get(), SmelteryBlocks.seared_creeper.get(), SmelteryBlocks.seared_paver.get(), SmelteryBlocks.seared_road.get(), SmelteryBlocks.seared_tile.get(),
      SmelteryBlocks.seared_stone.getSlab(), SmelteryBlocks.seared_cobble.getSlab(), SmelteryBlocks.seared_bricks.getSlab(), SmelteryBlocks.seared_cracked_bricks.getSlab(), SmelteryBlocks.seared_fancy_bricks.getSlab(),
      SmelteryBlocks.seared_square_bricks.getSlab(), SmelteryBlocks.seared_small_bricks.getSlab(), SmelteryBlocks.seared_triangle_bricks.getSlab(), SmelteryBlocks.seared_creeper.getSlab(), SmelteryBlocks.seared_paver.getSlab(),
      SmelteryBlocks.seared_road.getSlab(), SmelteryBlocks.seared_tile.getSlab(), SmelteryBlocks.seared_stone.getStairs(), SmelteryBlocks.seared_cobble.getStairs(), SmelteryBlocks.seared_bricks.getStairs(), SmelteryBlocks.seared_cracked_bricks.getStairs(),
      SmelteryBlocks.seared_fancy_bricks.getStairs(), SmelteryBlocks.seared_square_bricks.getStairs(), SmelteryBlocks.seared_small_bricks.getStairs(), SmelteryBlocks.seared_triangle_bricks.getStairs(), SmelteryBlocks.seared_creeper.getStairs(), SmelteryBlocks.seared_paver.getStairs(),
      SmelteryBlocks.seared_road.getStairs(), SmelteryBlocks.seared_tile.getStairs(), SmelteryBlocks.seared_glass.get()), null), "smeltery_component");
  }
}
