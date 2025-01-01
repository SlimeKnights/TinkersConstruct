package slimeknights.tconstruct.common.data.render;

import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.world.level.block.Block;
import slimeknights.mantle.client.render.RenderItem;
import slimeknights.mantle.data.datamap.RegistryDataMapProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.TinkerItemDisplays;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;

import java.util.List;

/** Provides fluid cuboids for block entity renderers */
public class RenderItemProvider extends RegistryDataMapProvider<Block,List<RenderItem>> {
  public RenderItemProvider(PackOutput output) {
    super(output, Target.RESOURCE_PACK, RenderItem.REGISTRY, TConstruct.MOD_ID);
  }

  @Override
  protected void addEntries() {
    // casting table lists 1 fluid cube on the top
    String castingTable = "templates/casting_table";
    redirect(TinkerSmeltery.searedTable, castingTable);
    redirect(TinkerSmeltery.scorchedTable, castingTable);
    RenderItem.Builder itemBuilder = RenderItem.builder().center(8, 15.5f, 8).size(14).x(270).y(180).transform(TinkerItemDisplays.CASTING_TABLE);
    entry(castingTable, List.of(
      itemBuilder.build(),
      itemBuilder.size(14.1f).build()
    ));

    // casting basin lists a large cube a bit further from the edges
    String castingBasin = "templates/casting_basin";
    redirect(TinkerSmeltery.searedBasin, castingBasin);
    redirect(TinkerSmeltery.scorchedBasin, castingBasin);
    itemBuilder = RenderItem.builder().center(8, 10, 8).size(11.95f).transform(TinkerItemDisplays.CASTING_BASIN);
    entry(castingBasin, List.of(
      itemBuilder.build(),
      itemBuilder.size(12).build()
    ));

    // tables
    // crafting station
    itemBuilder = RenderItem.builder().size(2).transform(TinkerItemDisplays.TABLE);
    entry(TinkerTables.craftingStation, List.of(
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
    entry(TinkerTables.modifierWorktable, List.of(
      itemBuilder.center(5, 16.25f, 8).size(7.5f).x(270).build(),
      itemBuilder.center(11.5f, 17.5f, 3.5f).size(3).x(0).build(),
      itemBuilder.center(11.5f, 17.5f, 12.5f).build()
    ));
    // part builder
    entry(TinkerTables.partBuilder, List.of(
      itemBuilder.center(4.5f, 17.5f, 4.5f).size(3).build(),
      itemBuilder.center(4.5f, 16.3125f, 11.5f).size(5).x(270).build(),
      itemBuilder.center(11.5f, 17.5f, 4.5f).size(3).x(0).build()
    ));
    // anvils
    String anvil = "templates/tinkers_anvil";
    redirect(TinkerTables.tinkersAnvil, anvil);
    redirect(TinkerTables.scorchedAnvil, anvil);
    entry(anvil, List.of(
      itemBuilder.center(8, 16.25f, 9).size(7.5f).x(270).build(),
      itemBuilder.center( 2.5f, 17.5f, 10.5f).size(3).x(0).build(),
      itemBuilder.center( 2.5f, 17.5f,  5.5f).build(),
      itemBuilder.center( 8,    17.5f,  4.5f).build(),
      itemBuilder.center(13.5f, 17.5f,  5.5f).build(),
      itemBuilder.center(13.5f, 17.5f, 10.5f).build()
    ));
    // tinker station
    entry(TinkerTables.tinkerStation, List.of(
      itemBuilder.center(11, 16.25f, 8).size(7.5f).x(270).build(),
      itemBuilder.center(2.5f, 17.5f,  8).size(3).x(0).build(),
      itemBuilder.center(4.5f, 17.5f,  3.5f).build(),
      itemBuilder.center(4.5f, 17.5f, 12.5f).build()
    ));
    // melter
    itemBuilder = RenderItem.builder().size(7.5f).transform(TinkerItemDisplays.MELTER);
    entry(TinkerSmeltery.searedMelter, List.of(
      itemBuilder.center( 8, 12, 12).build(),
      itemBuilder.center( 4, 12,  4).build(),
      itemBuilder.center(12, 12,  4).build()
    ));
  }

  @Override
  public String getName() {
    return "Tinkers' Construct block render item provider";
  }
}
