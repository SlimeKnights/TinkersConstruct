package slimeknights.tconstruct.library.registration;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.registration.object.EnumObject;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TileEntityTypeDeferredRegister extends RegisterWrapper<TileEntityType<?>> {
  public TileEntityTypeDeferredRegister(String modID) {
    super(ForgeRegistries.TILE_ENTITIES, modID);
  }

  /**
   *
   * @param name
   * @param factory
   * @param block
   * @param <T>
   * @return
   */
  public <T extends TileEntity> RegistryObject<TileEntityType<T>> register(final String name, Supplier<? extends T> factory, Supplier<? extends Block> block) {
    // TODO: data fixer type fetching, if possible
    return register.register(name, () ->  TileEntityType.Builder.<T>create(factory, block.get()).build(null));
  }

  /**
   * Registers a new tile entity type using a tile entity factory and a block supplier
   * @param name     Tile entity name
   * @param factory  Tile entity factory
   * @param blocks   Enum object
   * @param <T>      Tile entity type
   * @return  Tile entity type registry object
   */
  public <T extends TileEntity> RegistryObject<TileEntityType<T>> register(final String name, Supplier<? extends T> factory, EnumObject<?, ? extends Block> blocks) {
    // TODO: data fixer type fetching, if possible
    return register.register(name, () ->  new TileEntityType<>(factory, ImmutableSet.copyOf(blocks.values()), null));
  }

  /**
   * Registers a new tile entity type using a tile entity factory and a block supplier
   * @param name             Tile entity name
   * @param factory          Tile entity factory
   * @param blockCollector   Function to get block list
   * @param <T>              Tile entity type
   * @return  Tile entity type registry object
   */
  public <T extends TileEntity> RegistryObject<TileEntityType<T>> register(final String name, Supplier<? extends T> factory, Consumer<ImmutableSet.Builder<Block>> blockCollector) {
    return register.register(name, () ->  {
      ImmutableSet.Builder<Block> blocks = new ImmutableSet.Builder<>();
      blockCollector.accept(blocks);
      // TODO: data fixer type fetching, if possible
      return new TileEntityType<>(factory, blocks.build(), null);
    });
  }
}
