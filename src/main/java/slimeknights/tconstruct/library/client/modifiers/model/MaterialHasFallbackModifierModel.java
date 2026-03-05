package slimeknights.tconstruct.library.client.modifiers.model;

import com.google.common.collect.ImmutableSet;
import com.mojang.math.Transformation;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import slimeknights.mantle.data.loadable.array.ArrayLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/** Modifier model that swaps variant based on the material at the given index having the given fallback. */
@RequiredArgsConstructor
public final class MaterialHasFallbackModifierModel implements ModifierModel, Function<MaterialVariantId, Boolean> {
  public static final RecordLoadable<MaterialHasFallbackModifierModel> LOADER = RecordLoadable.create(
    IntLoadable.FROM_ZERO.requiredField("index", m -> m.index),
    StringLoadable.DEFAULT.set(ArrayLoadable.COMPACT).requiredField("fallback", m -> m.fallback),
    ModifierModel.LOADER.requiredField("if_true", m -> m.ifTrue),
    ModifierModel.LOADER.requiredField("if_false", m -> m.ifFalse),
    MaterialHasFallbackModifierModel::new);

  private final int index;
  private final Set<String> fallback;
  private final ModifierModel ifTrue;
  private final ModifierModel ifFalse;
  /** Cache of the predicate for each seen material */
  private final Map<MaterialVariantId,Boolean> cache = new HashMap<>();

  public MaterialHasFallbackModifierModel(int index, ModifierModel ifTrue, ModifierModel ifFalse, String... fallback) {
    this(index, ImmutableSet.copyOf(fallback), ifTrue, ifFalse);
  }

  @Override
  public RecordLoadable<? extends ModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    ifTrue.validate(spriteGetter);
    ifFalse.validate(spriteGetter);
  }

  @Override
  public Boolean apply(MaterialVariantId material) {
    return MaterialRenderInfoLoader.INSTANCE.hasFallback(material, fallback);
  }

  /** Checks the cache for the given material having the fallback */
  private boolean hasFallback(IToolStackView tool) {
    return cache.computeIfAbsent(tool.getMaterials().get(index).getVariant(), this);
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    return (hasFallback(tool) ? ifTrue : ifFalse).getCacheKey(tool, modifier);
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    (hasFallback(tool) ? ifTrue : ifFalse).addQuads(tool, modifier, spriteGetter, transforms, isLarge, startTintIndex, quadConsumer, pixels);
  }

  @Override
  public int getTintIndexes() {
    return Math.max(ifTrue.getTintIndexes(), ifFalse.getTintIndexes());
  }

  @Override
  public int getTint(IToolStackView tool, ModifierEntry entry, int index) {
    return (hasFallback(tool) ? ifTrue : ifFalse).getTint(tool, entry, index);
  }
}
