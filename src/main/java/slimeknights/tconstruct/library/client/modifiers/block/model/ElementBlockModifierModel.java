package slimeknights.tconstruct.library.client.modifiers.block.model;

import com.mojang.datafixers.util.Either;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.ClientLoadables;
import slimeknights.tconstruct.library.client.modifiers.block.ModifierBakingContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Default block modifier model loader, loads element blocks.
 * Basically re-implements
 * {@link net.minecraftforge.client.model.ElementsModel}.
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class ElementBlockModifierModel implements TexturedBlockModifierModel {
  /** Loadable for element block modifier models */
  public static final Loadable<List<BlockElement>> LIST_LOADABLE = ClientLoadables.BLOCK_ELEMENT.list(1);

  public static final RecordLoadable<ElementBlockModifierModel> LOADER = RecordLoadable.create(
      LIST_LOADABLE.requiredField("elements", ElementBlockModifierModel::elements),
      TEXTURE_FIELD, ElementBlockModifierModel::new);

  protected final List<BlockElement> elements;
  protected final Map<String, Either<Material, String>> texture;

  @Override
  public RecordLoadable<ElementBlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate() {
  }

  @Override
  public void addParts(IToolStackView tool, ModifierEntry modifier, ModifierBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, IQuadTransformer quadTransformer, SimpleBakedModel.Builder builder) {
    elements.forEach(
        element -> SimpleBlockModel.bakePart(builder, context.with(tool, modifier, this).getResolver(tool, modifier), element, spriteGetter, context.transform, quadTransformer, context.location));
  }
}
