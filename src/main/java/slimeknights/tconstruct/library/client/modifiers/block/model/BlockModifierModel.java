package slimeknights.tconstruct.library.client.modifiers.block.model;

import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.client.modifiers.block.IBakedBlockModifierModel;
import slimeknights.tconstruct.library.client.modifiers.block.ModifierBakingContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;


/** Represents a 3d model defined for the given tool */
public interface BlockModifierModel extends IBakedBlockModifierModel, IHaveLoader{
  BlockModifierModel EMPTY = SingletonLoader.singleton(loader -> new BlockModifierModel() {
    @Override
    public void addParts(IToolStackView tool, ModifierEntry modifier, ModifierBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, IQuadTransformer quadTransformer, SimpleBakedModel.Builder builder) {}

    @Nullable
    @Override
    public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
      return null;
    }

    @Override
    public RecordLoadable<? extends BlockModifierModel> getLoader() {
      return loader;
    }

    @Override
    public void validate() {}
  });

  /** Loader for registering block modifier models */
  GenericLoaderRegistry<BlockModifierModel> LOADER = new GenericLoaderRegistry<>("Block Modifier Model", false);

  @Override
  RecordLoadable<? extends BlockModifierModel> getLoader();

  /** Validates that all sub-models in this model exist.
   *  We can't fully resolve the texture until the texture parsing chain in ModifierBakingContext is complete, so we might as well not do it.*/
  void validate();

  /** Public face baker for all block modifier models */
  FaceBakery BAKER = new FaceBakery();
  /** Empty Model State for baking */
  ModelState DEFAULT_MODEL_STATE = new ModelState() {};
}
