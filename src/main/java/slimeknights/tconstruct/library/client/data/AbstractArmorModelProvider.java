package slimeknights.tconstruct.library.client.data;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.GenericDataProvider;
import slimeknights.mantle.registration.object.IdAwareObject;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager;
import slimeknights.tconstruct.library.client.armor.ArmorModelManager.ArmorModel;
import slimeknights.tconstruct.library.client.armor.texture.ArmorTextureSupplier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/** Data provider for armor models */
public abstract class AbstractArmorModelProvider extends GenericDataProvider {
  private final Map<ResourceLocation,ArmorModel> models = new HashMap<>();

  public AbstractArmorModelProvider(PackOutput packOutput) {
    super(packOutput, Target.RESOURCE_PACK, ArmorModelManager.FOLDER);
  }

  /** Add all models to the manager */
  protected abstract void addModels();

  @Override
  public CompletableFuture<?> run(CachedOutput output) {
    addModels();
    return allOf(models.entrySet().stream().map(entry -> saveJson(output, entry.getKey(), ArmorModel.LOADABLE.serialize(entry.getValue()))));
  }

  /** Adds a model to the generator */
  protected void addModel(ResourceLocation name, ArmorTextureSupplier... layers) {
    ArmorModel existing = this.models.putIfAbsent(name, new ArmorModel(List.of(layers)));
    if (existing != null) {
      throw new IllegalArgumentException("Duplicate armor model at " + name + ", previous value " + existing);
    }
  }

  /** Adds a model to the generator */
  protected void addModel(IdAwareObject name, ArmorTextureSupplier... layers) {
    addModel(name.getId(), layers);
  }

  /** Adds a model to the generator */
  protected void addModel(IdAwareObject name, Function<ResourceLocation,ArmorTextureSupplier[]> layers) {
    addModel(name.getId(), layers.apply(name.getId()));
  }
}
