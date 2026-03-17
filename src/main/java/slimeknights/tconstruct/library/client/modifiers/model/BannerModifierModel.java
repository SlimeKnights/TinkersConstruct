package slimeknights.tconstruct.library.client.modifiers.model;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BannerPattern;
import slimeknights.mantle.client.model.util.MantleItemLayerModel;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.ItemLayerPixels;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IModDataView;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.modules.cosmetic.BannerModule;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/** Modifier model that renders all banner patterns on a tool */
public record BannerModifierModel(@Nullable ResourceLocation smallPrefix, @Nullable ResourceLocation largePrefix) implements ModifierModel {
  public static final RecordLoadable<BannerModifierModel> LOADER = RecordLoadable.create(
    Loadables.RESOURCE_LOCATION.nullableField("prefix", BannerModifierModel::smallPrefix),
    Loadables.RESOURCE_LOCATION.nullableField("prefix_large", BannerModifierModel::largePrefix),
    BannerModifierModel::new);

  @Override
  public RecordLoadable<? extends ModifierModel> getLoader() {
    return LOADER;
  }

  @Override
  public void validate(Function<Material, TextureAtlasSprite> spriteGetter) {
    // since these are dynamically loaded, condition based on the config option
    if (Config.CLIENT.logMissingModifierTextures.get()) {
      for (ResourceKey<BannerPattern> key : Sheets.SHIELD_MATERIALS.keySet()) {
        String suffix = MaterialRenderInfo.getSuffix(key.location());
        if (smallPrefix != null) {
          spriteGetter.apply(ModifierModel.blockAtlas(smallPrefix.withSuffix(suffix)));
        }
        if (largePrefix != null) {
          spriteGetter.apply(ModifierModel.blockAtlas(largePrefix.withSuffix(suffix)));
        }
      }
    }
  }

  @Override
  public Object getCacheKey(IToolStackView tool, ModifierEntry modifier) {
    return tool.getPersistentData().getInt(BannerModule.cacheKey(modifier.getId()));
  }

  @Override
  public void addQuads(IToolStackView tool, ModifierEntry modifier, Function<Material, TextureAtlasSprite> spriteGetter, Transformation transforms, boolean isLarge, int startTintIndex, Consumer<Collection<BakedQuad>> quadConsumer, @Nullable ItemLayerPixels pixels) {
    ResourceLocation prefix = isLarge ? largePrefix : smallPrefix;
    if (prefix != null) {
      IModDataView modData = tool.getPersistentData();
      ResourceLocation key = BannerModule.patternKey(modifier.getId());
      if (modData.contains(key, CompoundTag.TAG_LIST)) {
        ListTag list = modData.getList(key, ListTag.TAG_COMPOUND);
        List<BakedQuad> quads = new ArrayList<>(list.size());
        // iterate all patterns
        for (int i = 0; i < list.size(); i++) {
          // patterns are stored as short strings for some reason, for consistency we also store as hashes
          // map that back to the pattern
          CompoundTag tag = list.getCompound(i);
          Holder<BannerPattern> pattern = BannerPattern.byHash(tag.getString(BannerModule.KEY_PATTERN));
          int color = tag.getInt(BannerModule.KEY_COLOR);
          if (pattern != null) {
            // why must holders be such a pain?
            // TODO 1.21: will need to switch from using the ID to using the asset root for the texture
            pattern.unwrapKey().ifPresent(id -> {
              TextureAtlasSprite sprite = spriteGetter.apply(ModifierModel.blockAtlas(prefix.withSuffix(MaterialRenderInfo.getSuffix(id.location()))));
              // skip if sprite is missing - deals with modded patterns that we haven't made textures for
              if (!MissingTextureAtlasSprite.getLocation().equals(sprite.contents().name())) {
                quads.add(MantleItemLayerModel.getQuadForGui(color, -1, sprite, transforms, 0));
              }
            });
          }
        }
        if (!quads.isEmpty()) {
          quadConsumer.accept(quads);
        }
      }
    }
  }
}
