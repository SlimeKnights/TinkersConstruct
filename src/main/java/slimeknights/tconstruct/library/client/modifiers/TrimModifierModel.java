package slimeknights.tconstruct.library.client.modifiers;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.library.client.modifiers.model.TrimModifierModel.Armor;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Modifier model adding trim overlays to an item
 * @deprecated use {@link slimeknights.tconstruct.library.client.modifiers.model.TrimModifierModel.Armor}
 */
@Deprecated
public enum TrimModifierModel implements IBakedModifierModel {
  INSTANCE;

  public static final RecordLoadable<TrimModifierModel> LOADER = new SingletonLoader<>(INSTANCE);

  /** @deprecated use {@link Armor#getRoot()} */
  @Deprecated(forRemoval = true)
  public static final ResourceLocation[] TRIM_TEXTURES = new ResourceLocation[4];
  static {
    for (Armor type : Armor.values()) {
      TRIM_TEXTURES[type.ordinal()] = type.getRoot();
    }
  }

  /** @deprecated legacy system, use {@link #LOADER} */
  @Deprecated
  public static final IUnbakedModifierModel UNBAKED_INSTANCE = (smallGetter, largeGetter) -> {
    // if we are loading the model, then we are reloading resources
    for (Armor type : Armor.values()) {
      type.clearCache();
    }
    return INSTANCE;
  };

  @Nullable
  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    // cache key does not change per type
    return Armor.HELMET.getCacheKey(tool, modifier);
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    if (!isLarge && tool.getItem() instanceof ArmorItem armor) {
      int ordinal = armor.getType().ordinal();
      if (ordinal < 4) {
        Armor model = Armor.values()[ordinal];
        model.addQuads(tool, modifier, spriteGetter, transforms, isLarge, startTintIndex, quadConsumer, pixels);
      }
    }
  }
}
