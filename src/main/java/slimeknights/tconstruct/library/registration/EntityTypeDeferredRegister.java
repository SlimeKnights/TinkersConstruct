package slimeknights.tconstruct.library.registration;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class EntityTypeDeferredRegister extends RegisterWrapper<EntityType<?>> {

  private static final Item.Properties EGG_PROPS = new Item.Properties().group(ItemGroup.MISC);
  private final DeferredRegister<Item> itemRegistry;
  public EntityTypeDeferredRegister(String modID) {
    super(ForgeRegistries.ENTITIES, modID);
    itemRegistry = new DeferredRegister<>(ForgeRegistries.ITEMS, modID);
  }

  @Override
  public void register(IEventBus bus) {
    super.register(bus);
    itemRegistry.register(bus);
  }

  /**
   * Registers a entity type for the given entity
   * @param name  Entity name
   * @param sup   Entity builder instance
   * @param <T>   Entity class type
   * @return  Entity registry object
   */
  public <T extends Entity> RegistryObject<EntityType<T>> register(final String name, final Supplier<EntityType.Builder<T>> sup) {
    return register.register(name, () -> sup.get().build(locationString(name)));
  }

  /**
   * Registers a entity type for the given entity, and registers a spawn egg for it
   * @param name       Entity name
   * @param sup        Entity builder instance
   * @param primary    Primary egg color
   * @param secondary  Secondary egg color
   * @param <T>   Entity class type
   * @return  Entity registry object
   */
  public <T extends Entity> RegistryObject<EntityType<T>> registerWithEgg(final String name, final Supplier<EntityType.Builder<T>> sup, int primary, int secondary) {
    Lazy<EntityType<T>> lazy = Lazy.of(() -> sup.get().build(locationString(name)));
    itemRegistry.register(name + "_spawn_egg", () -> new SpawnEggItem(lazy.get(), primary, secondary, EGG_PROPS));
    return register.register(name, lazy);
  }
}
