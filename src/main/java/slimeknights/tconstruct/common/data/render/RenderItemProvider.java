package slimeknights.tconstruct.common.data.render;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import slimeknights.mantle.client.render.RenderItem;
import slimeknights.mantle.data.datamap.BlockStateDataMapProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerItemDisplays;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

/** Provides fluid cuboids for block entity renderers */
public class RenderItemProvider extends BlockStateDataMapProvider<List<RenderItem>> {
  public RenderItemProvider(PackOutput output) {
    super(output, Target.RESOURCE_PACK, RenderItem.STATE_REGISTRY, TConstruct.MOD_ID);
  }

  @Override
  protected void addEntries() {
    // casting table lists 1 fluid cube on the top
    String castingTable = "templates/casting_table";
    block(TinkerSmeltery.searedTable).variant(castingTable);
    block(TinkerSmeltery.scorchedTable).variant(castingTable);
    RenderItem.Builder itemBuilder = RenderItem.builder().center(8, 15.5f, 8).size(14).x(270).y(180).transform(TinkerItemDisplays.CASTING_TABLE);
    entry(castingTable, List.of(
      itemBuilder.build(),
      itemBuilder.size(14.1f).build()
    ));

    // casting basin lists a large cube a bit further from the edges
    String castingBasin = "templates/casting_basin";
    block(TinkerSmeltery.searedBasin).variant(castingBasin);
    block(TinkerSmeltery.scorchedBasin).variant(castingBasin);
    itemBuilder = RenderItem.builder().center(8, 10, 8).size(11.95f).transform(TinkerItemDisplays.CASTING_BASIN);
    entry(castingBasin, List.of(
      itemBuilder.build(),
      itemBuilder.size(12).build()
    ));

    // tables
    // crafting station
    itemBuilder = RenderItem.builder().size(2).transform(TinkerItemDisplays.TABLE);
    block(TinkerTables.craftingStation).variant(List.of(
      itemBuilder.center( 5, 17,  5).build(),
      itemBuilder.center( 8, 17,  5).build(),
      itemBuilder.center(11, 17,  5).build(),
      itemBuilder.center( 5, 17,  8).build(),
      itemBuilder.center( 8, 17,  8).build(),
      itemBuilder.center(11, 17,  8).build(),
      itemBuilder.center( 5, 17, 11).build(),
      itemBuilder.center( 8, 17, 11).build(),
      itemBuilder.center(11, 17, 11).build()
    ));
    // modifier worktable
    block(TinkerTables.modifierWorktable).variant(List.of(
      itemBuilder.center(5, 16.25f, 8).size(7.5f).x(270).build(),
      itemBuilder.center(11.5f, 17.5f, 3.5f).size(3).x(0).build(),
      itemBuilder.center(11.5f, 17.5f, 12.5f).build()
    ));
    // part builder
    block(TinkerTables.partBuilder).variant(List.of(
      itemBuilder.center(4.5f, 17.5f, 4.5f).size(3).build(),
      itemBuilder.center(4.5f, 16.3125f, 11.5f).size(5).x(270).build(),
      itemBuilder.center(11.5f, 17.5f, 4.5f).size(3).x(0).build()
    ));
    // anvils
    String anvil = "templates/tinkers_anvil";
    block(TinkerTables.tinkersAnvil).variant(anvil);
    block(TinkerTables.scorchedAnvil).variant(anvil);
    entry(anvil, List.of(
      itemBuilder.center(8, 16.25f, 9).size(7.5f).x(270).build(),
      itemBuilder.center( 2.5f, 17.5f, 10.5f).size(3).x(0).build(),
      itemBuilder.center( 2.5f, 17.5f,  5.5f).build(),
      itemBuilder.center( 8,    17.5f,  4.5f).build(),
      itemBuilder.center(13.5f, 17.5f,  5.5f).build(),
      itemBuilder.center(13.5f, 17.5f, 10.5f).build()
    ));
    // tinker station
    block(TinkerTables.tinkerStation).variant(List.of(
      itemBuilder.center(11, 16.25f, 8).size(7.5f).x(270).build(),
      itemBuilder.center(2.5f, 17.5f,  8).size(3).x(0).build(),
      itemBuilder.center(4.5f, 17.5f,  3.5f).build(),
      itemBuilder.center(4.5f, 17.5f, 12.5f).build()
    ));
    // melter
    itemBuilder = RenderItem.builder().size(7.5f).transform(TinkerItemDisplays.MELTER);
    block(TinkerSmeltery.searedMelter).variant(List.of(
      itemBuilder.center( 8, 12, 12).build(),
      itemBuilder.center( 4, 12,  4).build(),
      itemBuilder.center(12, 12,  4).build()
    ));
    String fluidCannon = "templates/fluid_cannon";
    String fluidCannonUp = "templates/fluid_cannon_up";
    String fluidCannonDown = "templates/fluid_cannon_down";
    itemBuilder = RenderItem.builder().size(7.5f).transform(TinkerItemDisplays.FLUID_CANNON);
    entry(fluidCannon, List.of(itemBuilder.center(8, 4, 16).build()));
    entry(fluidCannonUp, List.of(itemBuilder.center(8, 16, 8).x(270).build()));
    entry(fluidCannonDown, List.of(itemBuilder.center(8, 0, 8).x(90).build()));
    block(TinkerSmeltery.searedFluidCannon)
      .variant(fluidCannon).end()
      .variant(fluidCannonUp).when(FACING, Direction.UP).end()
      .variant(fluidCannonDown).when(FACING, Direction.DOWN);
    block(TinkerSmeltery.scorchedFluidCannon)
      .variant(fluidCannon).end()
      .variant(fluidCannonUp).when(FACING, Direction.UP).end()
      .variant(fluidCannonDown).when(FACING, Direction.DOWN);

    // proxy tank
    block(TinkerSmeltery.scorchedProxyTank).variant(List.of(RenderItem.builder().size(12f).transform(TinkerItemDisplays.MELTER).center(8, 9, 8).build())).end();

    // casting tank
    block(TinkerSmeltery.searedCastingTank).variant(castingTable);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct block render item provider";
  }
}
