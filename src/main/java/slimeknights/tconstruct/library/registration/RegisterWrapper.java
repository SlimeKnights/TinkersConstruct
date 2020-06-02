package slimeknights.tconstruct.library.registration;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class RegisterWrapper<T extends IForgeRegistryEntry<T>> {

  /** Registry instance, use this to provide register methods */
  protected DeferredRegister<T> register;
  protected String modID;
  protected RegisterWrapper(IForgeRegistry<T> reg, String modID) {
    register = new DeferredRegister<>(reg, modID);
    this.modID = modID;
  }

  /**
   * Initializes this registry wrapper. Needs to be called during mod construction
   */
  public void register(IEventBus bus) {
    register.register(bus);
  }

  /* Utilities */

  /**
   * Gets a resource location string for the given name
   * @param name  Name
   * @return  Resource location string
   */
  protected String locationString(String name) {
    return modID + ":" + name;
  }
}
