package slimeknights.tconstruct.library.client.modifiers.block.model;

import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.QuadTransformers;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.client.modifiers.block.BlockModifierModelManager;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/** Modifier model that composes multiple modifier models together. */
@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public class CompoundBlockModifierModel implements TransformableBlockModifierModel {
  public static final Loadable<List<Either<BlockModifierModel, ResourceLocation>>> LIST_LOADABLE = BlockModifierModel.PARSER.list(1);
  public static final RecordLoadable<CompoundBlockModifierModel> LOADER = RecordLoadable.create(LIST_LOADABLE.requiredField("models", CompoundBlockModifierModel::models), COLOR_FIELD, LUMINOSITY_FIELD, TRANSFORM_FIELD, CompoundBlockModifierModel::new);

  private final List<Either<BlockModifierModel, ResourceLocation>> models;
  private final int color;
  private final int luminosity;
  private final Transformation transform;
  @Nullable
  private List<BlockModifierModel> resolvedModels;

  public List<Either<BlockModifierModel, ResourceLocation>> models() {
    return models;
  }

  /** Creates a model for the given list of models with no color or transform. */
  public static BlockModifierModel create(List<Either<BlockModifierModel, ResourceLocation>> models) {
    return new CompoundBlockModifierModel(models, -1, 0, Transformation.identity());
  }

  /** Resolves all Either entries to actual models */
  private List<BlockModifierModel> getModels() {
    if (resolvedModels == null) {
      resolvedModels = BlockModifierModelManager.INSTANCE.getModels(this.models);
      if (resolvedModels.isEmpty() && !this.models.isEmpty()) {
        TConstruct.LOG.warn("CompoundBlockModifierModel: all {} Either entries failed to resolve: {}", this.models.size(), this.models);
      }
    }
    return resolvedModels;
  }

  @Override
  public RecordLoadable<CompoundBlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    for (BlockModifierModel model : getModels()) {
      model.validate(spriteGetter);
    }
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    List<BlockModifierModel> resolved = getModels();
    List<Object> cacheKey = new ArrayList<>();
    boolean nonNull = false;
    for (int i = 0; i < resolved.size(); i++) {
      Object key = resolved.get(i).getCacheKey(tool, modifier);
      if (key != null) {
        nonNull = true;
        cacheKey.add(key);
      }
    }
    // TODO: Only Return Key 0, or all?
    return nonNull ? cacheKey : null;
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    Transformation combined = transforms.compose(this.transform());
    Consumer<Collection<BakedQuad>> newQuadConsumer = quadConsumer;
    if(color() != -1 || luminosity() > 0) {
      newQuadConsumer = quads -> {
        quadConsumer.accept(ColoredBlockModel.applyColorQuadTransformer(color()).andThen(QuadTransformers.settingEmissivity(luminosity())).process(new ArrayList<>(quads)));
      };
    }
    List<BlockModifierModel> resolved = getModels();
    for (BlockModifierModel model : resolved) {
      model.addQuads(tool, modifier, spriteGetter, combined, isLarge, startTintIndex, newQuadConsumer, pixels);
    }
  }
}
