package slimeknights.tconstruct.library.registration;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.tconstruct.library.registration.object.RecipeSerializerObject;

import java.util.function.Supplier;

public class RecipeSerializerDeferredRegister extends RegisterWrapper<IRecipeSerializer<?>> {
  private final DeferredRegister<IRecipeSerializer<?>> recipeSerializerRegister;
  public RecipeSerializerDeferredRegister(String modID) {
    super(ForgeRegistries.RECIPE_SERIALIZERS, modID);
    this.recipeSerializerRegister = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, modID);
  }

  @Override
  public void register(IEventBus bus) {
    super.register(bus);
    recipeSerializerRegister.register(bus);
  }

  /**
   * Registers a new recipe serializer using a recipe serializer factory and
   * @param name
   * @param factory
   * @param <T>
   * @return
   */
  public <T extends IRecipeSerializer<?>> RecipeSerializerObject<T> register(final String name, final Supplier<? extends T> factory) {
    return new RecipeSerializerObject<>(register.register(name, factory));
  }
}
