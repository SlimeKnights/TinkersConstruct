package slimeknights.tconstruct.common.data.render;

import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import slimeknights.mantle.client.render.FluidCuboid;
import slimeknights.mantle.data.datamap.BlockStateDataMapProvider;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.smeltery.block.FaucetBlock;
import slimeknights.tconstruct.smeltery.block.component.SearedTankBlock.TankType;

import java.util.List;

/** Provides fluid cuboids for block entity renderers */
public class RenderFluidProvider extends BlockStateDataMapProvider<List<FluidCuboid>> {
  public RenderFluidProvider(PackOutput output) {
    super(output, Target.RESOURCE_PACK, FluidCuboid.REGISTRY, TConstruct.MOD_ID);
  }

  @Override
  protected void addEntries() {
    // casting table lists 1 fluid cube on the top
    String castingTable = "templates/casting_table";
    entry(castingTable, List.of(
      FluidCuboid.builder()
                 .from(1, 15, 1)
                 .to(15, 15.9f, 15)
                 .face(Direction.UP)
                 .build()));
    block(TinkerSmeltery.searedTable).variant(castingTable);
    block(TinkerSmeltery.scorchedTable).variant(castingTable);

    // casting basin lists a large cube a bit further from the edges
    Direction[] horizontal = { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST };
    String castingBasin = "templates/casting_basin";
    entry(castingBasin, List.of(
      FluidCuboid.builder()
                 .from(2.1f, 4, 2.1f)
                 .to(13.9f, 15.9f, 13.9f)
                 .face(Direction.UP, horizontal)
                 .build()));
    block(TinkerSmeltery.searedBasin).variant(castingBasin);
    block(TinkerSmeltery.scorchedBasin).variant(castingBasin);

    // melter
    block(TinkerSmeltery.searedMelter).variant(List.of(
      FluidCuboid.builder()
                 .from(0.08f, 8.08f, 0.08f)
                 .to(15.92f, 15.92f, 15.92f)
                 .build()));
    // alloyer
    block(TinkerSmeltery.scorchedAlloyer).variant(List.of(
      FluidCuboid.builder()
                 .from(0.08f, 5.08f, 0.08f)
                 .to(15.92f, 15.92f, 15.92f)
                 .build()));

    // tanks
    String tank = "templates/tank";
    entry(tank, List.of(
      FluidCuboid.builder()
                 .from(0.08f, 0.08f, 0.08f)
                 .to(15.92f, 15.92f, 15.92f)
                 .build()));
    for (TankType type : TankType.values()) {
      block(TinkerSmeltery.searedTank.get(type)).variant(tank);
      block(TinkerSmeltery.scorchedTank.get(type)).variant(tank);
    }

    // faucets
    String faucet = "templates/faucet";
    String faucetUp = "templates/faucet_up";
    entry(faucet, List.of(
      FluidCuboid.builder()
                 .from(6, 6, 0)
                 .to(10, 9, 6)
                 .face(true, 0, Direction.UP).face(Direction.NORTH).build(),
      FluidCuboid.builder()
                 .from(6, 0, 6)
                 .to(10, 9, 8)
                 .face(true, 0, Direction.UP, horizontal).build()
    ));
    entry(faucetUp, List.of(
      FluidCuboid.builder()
                 .from(6, 0, 6)
                 .to(10, 16, 10)
                 .face(Direction.UP)
                 .face(true, 0, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST).build()
    ));
    block(TinkerSmeltery.searedFaucet)
      .variant(faucet).end()
      .variant(faucetUp).when(FaucetBlock.FACING, Direction.DOWN);
    block(TinkerSmeltery.scorchedFaucet)
      .variant(faucet).end()
      .variant(faucetUp).when(FaucetBlock.FACING, Direction.DOWN);
  }

  @Override
  public String getName() {
    return "Tinkers' Construct block render fluid provider";
  }
}
