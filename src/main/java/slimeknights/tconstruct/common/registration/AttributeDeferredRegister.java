package slimeknights.tconstruct.common.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.DeferredRegisterWrapper;

public class AttributeDeferredRegister extends DeferredRegisterWrapper<Attribute> {
  public AttributeDeferredRegister(String modID) {
    super(Registries.ATTRIBUTE, modID);
  }

  /**
   * Registers a new ranged attribute
   * @param name          Attribute name
   * @param defaultValue  Default attribute value
   * @param min           Minimum value
   * @param max           Maximum value
   * @param syncable      If true, this attribute syncs to the client
   * @return  Registered attribute
   */
  public RegistryObject<Attribute> register(String name, double defaultValue, double min, double max, boolean syncable) {
    return register.register(name, () -> new RangedAttribute("attribute.name." + modID + "." + name, defaultValue, min, max).setSyncable(syncable));
  }

  /**
   * Registers an attribute with a range from 0 to 1
   * @param name          Attribute name
   * @param defaultValue  Default attribute value
   * @param syncable      If true, this attribute syncs to the client
   * @return  Registered attribute
   */
  public RegistryObject<Attribute> registerPercent(String name, double defaultValue, boolean syncable) {
    return register(name, defaultValue, 0, 1, syncable);
  }

  /**
   * Registers an attribute with default value 1 and range of 0 to 100.
   * @param name          Attribute name
   * @param syncable      If true, this attribute syncs to the client
   * @return  Registered attribute
   */
  public RegistryObject<Attribute> registerMultiplier(String name, boolean syncable) {
    return register(name, 1, 0, 100, syncable);
  }
}
