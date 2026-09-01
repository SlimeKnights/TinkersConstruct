package slimeknights.tconstruct.library.client.modifiers.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.data.loadable.primitive.BooleanLoadable;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo.TintedSprite;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.client.model.tools.MaterialModel;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.util.ModuleWithKey;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/** Implementation of modifier models that have variants based on a material texture */
public interface MaterialModifierModel extends SimpleModifierModel {
  @Override
  default void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    SimpleModifierModel.super.validate(spriteGetter);
    if (Config.CLIENT.logMissingMaterialTextures.get()) {
      Material small = small();
      Material large = large();
      for (MaterialRenderInfo info : MaterialRenderInfoLoader.INSTANCE.getAllRenderInfos()) {
        if (small != null) info.getSprite(small, spriteGetter);
        if (large != null) info.getSprite(large, spriteGetter);
      }
    }
  }

  /** Gets the material to use to render this modifier */
  @Nullable
  MaterialVariantId getMaterial(IToolStackView tool, ModifierEntry entry);

  /**
   * Gets the color to tint the given material texture
   * @param tool     Tool instance
   * @param entry    Modifier entry
   * @param sprite   Current material sprite
   * @return  Color to tint
   */
  default int getColor(IToolStackView tool, ModifierEntry entry, TintedSprite sprite) {
    return sprite.color();
  }

  @Override
  default void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material,TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    Material texture = isLarge ? large() : small();
    if (texture != null) {
      MaterialVariantId material = getMaterial(tool, modifier);
      if (material != null) {
        TintedSprite sprite = MaterialModel.getMaterialSprite(spriteGetter, texture, material);
        quadConsumer.accept(MantleItemLayerModel.getQuadsForSprite(getColor(tool, modifier, sprite), -1, sprite.sprite(), transforms, sprite.emissivity(), pixels));
      }
    }
  }

  /** Common code between {@link Index} and {@link Dyed} */
  interface WithIndex extends MaterialModifierModel {
    RecordField<Integer, WithIndex> INDEX_FIELD = IntLoadable.FROM_ZERO.requiredField("index", WithIndex::index);

    /** Material index */
    int index();

    @Override
    default MaterialVariantId getMaterial(IToolStackView tool, ModifierEntry entry) {
      return tool.getMaterial(index()).getVariant();
    }
  }

  /** Fetches the material from an index on the tool materials */
  record Index(@Nullable Material small, @Nullable Material large, int index) implements MaterialModifierModel.WithIndex {
    public static final RecordLoadable<Index> LOADER = RecordLoadable.create(TEXTURE_FIELD, LARGE_TEXTURE_FIELD, INDEX_FIELD, Index::new);

    @Override
    public RecordLoadable<Index> getLoader() {
      return LOADER;
    }
  }

  /** Fetches the material from persistent data on the tool under the specified key, or the modifier ID. */
  record PersistentData(@Nullable Material small, @Nullable Material large, @Nullable ResourceLocation key) implements MaterialModifierModel, ModuleWithKey {
    public static final RecordLoadable<PersistentData> LOADER = RecordLoadable.create(TEXTURE_FIELD, LARGE_TEXTURE_FIELD, FIELD, PersistentData::new);

    /** Creates a model using the modifier's ID as the key. */
    public PersistentData(@Nullable Material small, @Nullable Material large) {
      this(small, large, null);
    }

    @Override
    public RecordLoadable<PersistentData> getLoader() {
      return LOADER;
    }

    @Nullable
    @Override
    public MaterialVariantId getMaterial(IToolStackView tool, ModifierEntry entry) {
      String material = tool.getPersistentData().getString(getKey(entry));
      if (!material.isEmpty()) {
        return MaterialVariantId.tryParse(material);
      }
      return null;
    }

    @Override
    public Object getCacheKey(IToolStackView tool, ModifierEntry entry) {
      // since we are using a key in NBT for the material, need to include it in the cache key
      return new CacheKey(entry.getId(), tool.getPersistentData().getString(getKey(entry)));
    }

    /** Data class to cache a material texture */
    private record CacheKey(ModifierId modifier, String material) {}
  }

  /** Module using a material index, but applying a custom dye color to it. */
  record Dyed(@Nullable Material small, @Nullable Material large, int index, boolean useMaterialColor, @Nullable ResourceLocation key) implements MaterialModifierModel.WithIndex, ModuleWithKey {
    public static final RecordLoadable<Dyed> LOADER = RecordLoadable.create(
      TEXTURE_FIELD, LARGE_TEXTURE_FIELD, INDEX_FIELD,
      BooleanLoadable.INSTANCE.defaultField("use_material_color", true, Dyed::useMaterialColor),
      FIELD, Dyed::new);

    public Dyed(@Nullable Material small, @Nullable Material large, int index, boolean useMaterialColor) {
      this(small, large, index, useMaterialColor, null);
    }

    @Override
    public RecordLoadable<Dyed> getLoader() {
      return LOADER;
    }

    @Override
    public int getColor(IToolStackView tool, ModifierEntry modifier, TintedSprite sprite) {
      int color = useMaterialColor ? sprite.color() : -1;
      // fetch the dye color
      IModDataView data = tool.getPersistentData();
      ResourceLocation key = getKey(modifier);
      if (data.contains(key, Tag.TAG_INT)) {
        // if we have a dye color, need to mix the two
        int dyed = 0xFF000000 | data.getInt(key);
        // no material color makes mixing easy
        if (color == -1) {
          color = dyed;
        } else if (dyed != -1) {
          // otherwise, let Minecraft figure it out
          color = FastColor.ARGB32.multiply(color, dyed);
        }
      }
      return color;
    }

    @Override
    public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
      ResourceLocation key = getKey(modifier);
      IModDataView data = tool.getPersistentData();
      int color = -1;
      if (data.contains(key, Tag.TAG_INT)) {
        color = data.getInt(key);
      }
      if (modifier == ModifierEntry.EMPTY) {
        return color;
      }
      return new CacheKey(modifier.getId(), color);
    }

    /** Data class to cache a colored texture */
    private record CacheKey(ModifierId modifier, int color) {}
  }
}
