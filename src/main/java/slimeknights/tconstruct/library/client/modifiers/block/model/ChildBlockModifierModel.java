package slimeknights.tconstruct.library.client.modifiers.block.model;

import java.util.Map;
import java.util.function.Function;

import com.mojang.datafixers.util.Either;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.modifiers.block.ModifierBakingContext;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

@Accessors(fluent = true)
public record ChildBlockModifierModel(BlockModifierModel parent, Map<String, Either<Material, String>> texture) implements TexturedBlockModifierModel {

  public static final RecordLoadable<ChildBlockModifierModel> LOADER = RecordLoadable.create(
      BlockModifierModelLoadable.DEFAULT.requiredField("parent", ChildBlockModifierModel::parent),
      TEXTURE_FIELD,
      ChildBlockModifierModel::new);

  @Override
  public RecordLoadable<? extends BlockModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate() {
    parent.validate();
  }

  @Override
  public void addParts(IToolStackView tool, ModifierEntry modifier, ModifierBakingContext context, Function<Material, TextureAtlasSprite> spriteGetter, IQuadTransformer quadTransformer, SimpleBakedModel.Builder builder) {
    parent.addParts(tool, modifier, context.with(tool, modifier, this), spriteGetter, quadTransformer, builder);
  }

  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
      return parent.getCacheKey(tool, modifier);
  }

}
