package slimeknights.tconstruct.library.registration.object;

import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ItemObject<I extends Item> implements Supplier<I>, IItemProvider {
  private RegistryObject<I> item;

  public ItemObject(RegistryObject<I> item) {
    this.item = item;
  }

  @Override
  public I get() {
    return item.get();
  }

  @Override
  public I asItem() {
    return item.get();
  }

  /**
   * Gets the resource location for the given item
   * @return  Resource location for the given item
   */
  public ResourceLocation getRegistryName() {
    return item.get().getRegistryName();
  }
}
