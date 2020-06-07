package slimeknights.tconstruct.library.registration;

import net.minecraft.block.Block;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.registration.object.BlockItemObject;
import slimeknights.tconstruct.library.registration.object.BuildingBlockObject;
import slimeknights.tconstruct.library.registration.object.EnumObject;
import slimeknights.tconstruct.library.registration.object.FenceBuildingBlockObject;
import slimeknights.tconstruct.library.registration.object.WallBuildingBlockObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class BlockDeferredRegister extends RegisterWrapper<Block> {

  private final DeferredRegister<Item> itemRegister;
  public BlockDeferredRegister(String modID) {
    super(ForgeRegistries.BLOCKS, modID);
    this.itemRegister = new DeferredRegister<>(ForgeRegistries.ITEMS, modID);
  }

  @Override
  public void register(IEventBus bus) {
    super.register(bus);
    itemRegister.register(bus);
  }

  /* Blocks with no items */

  /**
   * Registers a block with the block registry
   * @param name   Block ID
   * @param block  Block supplier
   * @param <B>    Block class
   * @return  Block registry object
   */
  public <B extends Block> RegistryObject<B> registerNoItem(final String name, final Supplier<? extends B> block) {
    return register.register(name, block);
  }

  /**
   * Registers a block with the block registry
   * @param name   Block ID
   * @param props  Block properties
   * @return  Block registry object
   */
  public RegistryObject<Block> registerNoItem(final String name, final Block.Properties props) {
    return registerNoItem(name, () -> new Block(props));
  }


  /* Block item pairs */

  /**
   * Registers a block with the block registry, using the function for the BlockItem
   * @param name   Block ID
   * @param block  Block supplier
   * @param item   Function to create a BlockItem from a Block
   * @param <B>    Block class
   * @return  Block item registry object pair
   */
  public <B extends Block> BlockItemObject<B> register(final String name, final Supplier<? extends B> block, final Function<? super B, ? extends BlockItem> item) {
    RegistryObject<B> blockObj = registerNoItem(name, block);
    return new BlockItemObject<>(blockObj, itemRegister.register(name, () -> item.apply(blockObj.get())));
  }

  /**
   * Registers a block with the block registry, using the function for the BlockItem
   * @param name        Block ID
   * @param blockProps  Block supplier
   * @param item        Function to create a BlockItem from a Block
   * @return  Block item registry object pair
   */
  public BlockItemObject<Block> register(final String name, final Block.Properties blockProps, final Function<? super Block, ? extends BlockItem> item) {
    return register(name, () -> new Block(blockProps), item);
  }


  /* Specialty */

  /**
   * Registers a block with slab, and stairs
   * @param name      Name of the block
   * @param props     Block properties
   * @param item      Function to get an item from the block
   * @return  BuildingBlockObject class that returns different block types
   */
  public BuildingBlockObject registerBuilding(final String name, Block.Properties props, final Function<? super Block, ? extends BlockItem> item) {
    BlockItemObject<Block> blockObj = register(name, props, item);
    return new BuildingBlockObject(blockObj,
      register(name + "_slab", () -> new SlabBlock(props), item),
      register(name + "_stairs", () -> new StairsBlock(() -> blockObj.get().getDefaultState(), props), item)
    );
  }

  /**
   * Registers a block with slab, stairs, and wall
   * @param name      Name of the block
   * @param props     Block properties
   * @param item      Function to get an item from the block
   * @return  StoneBuildingBlockObject class that returns different block types
   */
  public WallBuildingBlockObject registerWallBuilding(final String name, final Block.Properties props, final Function<? super Block, ? extends BlockItem> item) {
    return new WallBuildingBlockObject(
      registerBuilding(name, props, item),
      register(name + "_wall", () -> new WallBlock(props), item)
    );
  }

  /**
   * Registers a block with slab, stairs, and fence
   * @param name      Name of the block
   * @param props     Block properties
   * @param item      Function to get an item from the block
   * @return  WoodBuildingBlockObject class that returns different block types
   */
  public FenceBuildingBlockObject registerFenceBuilding(final String name, final Block.Properties props, final Function<? super Block, ? extends BlockItem> item) {
    return new FenceBuildingBlockObject(
      registerBuilding(name, props, item),
      register(name + "_fence", () -> new FenceBlock(props), item)
    );
  }

  /**
   * Registers a block with slab, stairs, and walls
   * @param values    Enum values to use for this block
   * @param name      Name of the block
   * @param supplier  Function to get a block for the given enum value
   * @param item      Function to get an item from the block
   * @return  EnumObject mapping between different block types
   */
  @SuppressWarnings("unchecked")
  public <T extends Enum<T> & IStringSerializable, B extends Block> EnumObject<T,B> registerEnum(final T[] values, final String name, Function<T,? extends B> supplier, final Function<? super B, ? extends BlockItem> item) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Must have at least one value");
    }
    // note this cast only works because you cannot extend an enum
    Map<T, Supplier<? extends B>> map = new EnumMap<>((Class<T>)values[0].getClass());
    for (T value : values) {
      map.put(value, register(value.getName() + "_" + name, () -> supplier.apply(value), item));
    }
    return new EnumObject<>(map);
  }

  /**
   * Registers a block with slab, stairs, and walls
   * @param name      Name of the block
   * @param values    Enum values to use for this block
   * @param supplier  Function to get a block for the given enum value
   * @param item      Function to get an item from the block
   * @return  EnumObject mapping between different block types
   */
  @SuppressWarnings("unchecked")
  public <T extends Enum<T> & IStringSerializable, B extends Block> EnumObject<T,B> registerEnum(final String name, final T[] values, Function<T,? extends B> supplier, final Function<? super B, ? extends BlockItem> item) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Must have at least one value");
    }
    // note this cast only works because you cannot extend an enum
    Map<T, Supplier<? extends B>> map = new EnumMap<>((Class<T>)values[0].getClass());
    for (T value : values) {
      map.put(value, register(name + "_" + value.getName(), () -> supplier.apply(value), item));
    }
    return new EnumObject<>(map);
  }
}
