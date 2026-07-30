package slimeknights.tconstruct.library.client.modifiers.block.model;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.primitive.ResourceLocationLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.data.registry.GenericLoaderRegistry;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.modifiers.model.ModifierModel;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

/** Represents a 3d model defined for the given tool */
public interface BlockModifierModel extends ModifierModel {
  BlockModifierModel EMPTY = SingletonLoader.singleton(loader -> new BlockModifierModel() {
    @Override
    public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {}

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
    public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {}
  });

  /** Loader for registering block modifier models */
  GenericLoaderRegistry<BlockModifierModel> LOADER = new GenericLoaderRegistry<>("Block Modifier Model", false);
  /** Parser for either a BlockModifierModel or a ResourceLocation reference */
  Loadable<Either<BlockModifierModel, ResourceLocation>> PARSER = TinkerLoadables.EitherLoadable.create(LOADER,ResourceLocationLoadable.DEFAULT);

  /** Public face baker for all block modifier models */
  FaceBakery BAKER = new FaceBakery();
  /** Empty Model State for baking */
  ModelState DEFAULT_MODEL_STATE = new ModelState() {};

  @Override
  RecordLoadable<? extends BlockModifierModel> getLoader();
}
