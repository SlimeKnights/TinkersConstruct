package slimeknights.tconstruct.library.client.modifiers.block.model;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.math.Transformation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import slimeknights.mantle.client.model.util.ColoredBlockModel;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Default block modifier model loader, loads element blocks.
 * Basically re-implements {@link net.minecraftforge.client.model.ElementsModel}.
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
@Log4j2
public class ElementBlockModifierModel implements TransformableBlockModifierModel, ParentModel {
  /** Loadable for element block modifier models */
  public static final Loadable<List<BlockElement>> LIST_LOADABLE = TinkerLoadables.BLOCK_ELEMENT.list(1);
  public static final Loadable<Map<String, Either<Material, String>>> SPRITE_LOADABLE = StringLoadable.DEFAULT
      .mapWithValues(TinkerLoadables.EitherLoadable.create(MATERIAL_LOADABLE, StringLoadable.DEFAULT));
  public static final RecordLoadable<ElementBlockModifierModel> LOADER = RecordLoadable.create(COLOR_FIELD,
      LUMINOSITY_FIELD, TRANSFORM_FIELD,
      LIST_LOADABLE.requiredField("elements", ElementBlockModifierModel::elements),
      SPRITE_LOADABLE.requiredField("textures", ElementBlockModifierModel::textures),
      ElementBlockModifierModel::new);

  protected final int color;
  protected final int luminosity;
  protected final Transformation transform;
  protected final List<BlockElement> elements;
  protected final Map<String, Either<Material, String>> textures;

  @Override
  public RecordLoadable<ElementBlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    for (String key : textures.keySet()) {
      spriteGetter.apply(getMaterial(key));
    }
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    IQuadTransformer postTransform = QuadTransformers.applying(transforms.compose(this.transform())).andThen(ColoredBlockModel.applyColorQuadTransformer(color)).andThen(QuadTransformers.settingEmissivity(luminosity));
    List<BakedQuad> quads = new ArrayList<>();
    for (BlockElement element : elements) {
      for (Direction direction : element.faces.keySet()) {
        BlockElementFace face = element.faces.get(direction);
        TextureAtlasSprite sprite = spriteGetter.apply(getMaterial(face.texture));
        @SuppressWarnings("null") // Note: ResourceLocation only used when ModelState is uv locked. We can also re-implement this method.
        BakedQuad quad = BAKER.bakeQuad(element.from, element.to, face, sprite, direction, DEFAULT_MODEL_STATE, element.rotation, element.shade, (ResourceLocation) null);
        quads.add(quad);
      }
    }
    quadConsumer.accept(postTransform.process(quads));
  }

  /** Resolves a texture string to a material. Copied from {@link net.minecraft.client.renderer.block.model.BlockModel#getMaterial} */
  protected Material getMaterial(String textureString) {
    List<String> list = new ArrayList<>();
    while (true) {
      if (textureString.startsWith("#")) {
        textureString = textureString.substring(1);
      }
      Either<Material, String> either = textures.get(textureString);
      Optional<Material> optional = either.left();
      if (optional.isPresent()) {
        return optional.get();
      }
      textureString = either.right().get();
      if (list.contains(textureString)) {
        log.warn("Unable to resolve texture due to reference chain {}->{} in modifier model", Joiner.on("->").join(list), textureString);
        return new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
      }
      list.add(textureString);
    }
  }

  @Override
  public ParentModel mergeChild(JsonObject override) {
    Map<String, Either<Material, String>> merged = new HashMap<>(textures);
    if (override.has("textures")) {
      merged.putAll(SPRITE_LOADABLE.convert(override.getAsJsonObject("textures"), "child.override.textures"));
    }
    return new ElementBlockModifierModel(color, luminosity, transform, elements, merged);
  }
}
