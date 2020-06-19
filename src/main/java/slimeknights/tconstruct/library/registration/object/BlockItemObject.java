package slimeknights.tconstruct.library.registration.object;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.function.Supplier;

public class BlockItemObject<B extends Block> implements Supplier<B>, IItemProvider {
  protected final Supplier<B> block;
  protected final Supplier<Item> item;

  /**
   * Constructor for class
   * @param block  Block instance
   * @param item   Item instance
   */
  public BlockItemObject(Supplier<B> block, Supplier<Item> item) {
    this.block = block;
    this.item = item;
  }

  /**
   * Creates a block item object for a registered block
   * @param block  Block instance
   */
  @SuppressWarnings("unchecked")
  public static <B extends Block> BlockItemObject<B> fromBlock(B block) {
    IRegistryDelegate<Block> delegate = block.delegate;
    return new BlockItemObject<>(() -> (B)delegate.get(), () -> delegate.get().asItem());
  }

  /**
   * Gets the block for this pair
   * @return  Block instance
   */
  @Override
  public B get() {
    return block.get();
  }

  @Override
  public Item asItem() {
    return item.get();
  }

  /**
   * Gets the resource location for the given block
   * @return  Resource location for the given block
   */
  public ResourceLocation getRegistryName() {
    return block.get().getRegistryName();
  }
}
