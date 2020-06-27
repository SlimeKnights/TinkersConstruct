package slimeknights.tconstruct.tables.client.model;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import slimeknights.mantle.block.ConnectedTextureBlock;

import java.util.EnumMap;
import java.util.Map;

public class ModelProperties {

  //public static final ModelProperty<TableItems> items = new ModelProperty<>();

  public static final ModelProperty<Direction> DIRECTION = new ModelProperty<>();
  public static final ModelProperty<CompoundNBT> TEXTURE = new ModelProperty<>();
  public static final ModelProperty<FluidTank> FLUID_TANK = new ModelProperty<>();

  // connected block directions
  // TODO: move to mantle
  public static final Map<Direction,BooleanProperty> CONNECTED_DIRECTIONS;
  static {
    CONNECTED_DIRECTIONS = new EnumMap<>(Direction.class);
    CONNECTED_DIRECTIONS.put(Direction.UP, ConnectedTextureBlock.CONNECTED_UP);
    CONNECTED_DIRECTIONS.put(Direction.DOWN, ConnectedTextureBlock.CONNECTED_DOWN);
    CONNECTED_DIRECTIONS.put(Direction.NORTH, ConnectedTextureBlock.CONNECTED_NORTH);
    CONNECTED_DIRECTIONS.put(Direction.SOUTH, ConnectedTextureBlock.CONNECTED_SOUTH);
    CONNECTED_DIRECTIONS.put(Direction.WEST, ConnectedTextureBlock.CONNECTED_WEST);
    CONNECTED_DIRECTIONS.put(Direction.EAST, ConnectedTextureBlock.CONNECTED_EAST);
  }
}
