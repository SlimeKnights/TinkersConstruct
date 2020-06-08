package slimeknights.tconstruct.library.registration;

import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerDeferredRegister extends RegisterWrapper<ContainerType<?>> {

  public ContainerDeferredRegister(String modID) {
    super(ForgeRegistries.CONTAINERS, modID);
  }

  /**
   * Registers a container type
   * @param name     Container name
   * @param factory  Container factory
   * @param <C>      Container type
   * @return  Registry object containing the container type
   */
  public <C extends Container> RegistryObject<ContainerType<C>> register(final String name, IContainerFactory<C> factory) {
    return register.register(name, () -> IForgeContainerType.create(factory));
  }
}
