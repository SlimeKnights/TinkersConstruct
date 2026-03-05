package slimeknights.tconstruct.library.client.modifiers;

import com.mojang.math.Transformation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.modifiers.model.SimpleModifierModel;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Modifier model that renders the textured tinted based on the active potion color.
 * TODO 1.21: move to {@link slimeknights.tconstruct.library.modifiers.modules}
 */
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class PotionModifierModel implements SimpleModifierModel {
  public static final RecordLoadable<PotionModifierModel> LOADER = SimpleModifierModel.loader(PotionModifierModel::new);
  /** @deprecated legacy system, use {@link #LOADER} */
  @Deprecated
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    Material smallTexture = smallGetter.apply("");
    Material largeTexture = largeGetter.apply("");
    if (smallTexture != null || largeTexture != null) {
      return new PotionModifierModel(smallTexture, largeTexture);
    }
    return null;
  };

  /** Textures to show */
  @Nullable
  private final Material small;
  @Nullable
  private final Material large;

  @Override
  public RecordLoadable<? extends PotionModifierModel> getLoader() {
    return LOADER;
  }

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry entry) {
    ModifierId modifier = entry.getId();
    return new CacheKey(modifier, tool.getPersistentData().getString(modifier));
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    Material texture = isLarge ? large : small;
    if (texture != null) {
      ResourceLocation key = modifier.getId();
      IModDataView toolData = tool.getPersistentData();
      if (toolData.contains(key, Tag.TAG_STRING)) {
        ResourceLocation id = ResourceLocation.tryParse(toolData.getString(key));
        if (id != null) {
          Potion potion = BuiltInRegistries.POTION.get(id);
          if (potion != Potions.EMPTY) {
            quadConsumer.accept(MantleItemLayerModel.getQuadsForSprite(0xFF000000 | PotionUtils.getColor(potion), -1, spriteGetter.apply(texture), transforms, 0, pixels));
          }
        }
      }
    }
  }

  /** Data class to cache a colored texture */
  private record CacheKey(ModifierId modifier, String potion) {}
}
