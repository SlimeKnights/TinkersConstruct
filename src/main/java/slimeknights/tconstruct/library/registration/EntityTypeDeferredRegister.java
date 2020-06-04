package slimeknights.tconstruct.library.registration;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class EntityTypeDeferredRegister extends RegisterWrapper<EntityType<?>> {

  public EntityTypeDeferredRegister(String modID) {
    super(ForgeRegistries.ENTITIES, modID);
  }

  public <T extends Entity> RegistryObject<EntityType<T>> register(final String name, final Supplier<EntityType.Builder<T>> sup) {
    return register.register(name, () -> sup.get().build(locationString(name)));
  }
}
