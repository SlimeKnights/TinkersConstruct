package slimeknights.tconstruct.library.client.armor.texture;

import lombok.RequiredArgsConstructor;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.field.LoadableField;
import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfoLoader;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import java.util.Optional;
import java.util.function.Function;

/** Logic to create material texture variants for armor */
@RequiredArgsConstructor
public abstract class MaterialArmorTextureSupplier implements ArmorTextureSupplier {
  /** Field for parsing the variant from JSON */
  private static final LoadableField<ResourceLocation,MaterialArmorTextureSupplier> PREFIX_FIELD = Loadables.RESOURCE_LOCATION.requiredField("prefix", m -> m.prefix);

  /** Makes a texture for the given variant and material, returns null if its missing */
  private static ArmorTexture tryTexture(ResourceLocation name, int color, int luminosity, String material) {
    ResourceLocation texture = name.withSuffix(material);
    if (TEXTURE_VALIDATOR.test(texture)) {
      return new TintedArmorTexture(ArmorTextureSupplier.getTexturePath(texture), color, luminosity);
    }
    return ArmorTexture.EMPTY;
  }

  /** Makes a material getter for the given base and type */
  public static Function<String,ArmorTexture> materialGetter(ResourceLocation name) {
    // if the base texture does not exist, means we decided to skip this piece. Notably used for skipping some layers of wings
    if (!TEXTURE_VALIDATOR.test(name)) {
      return material -> ArmorTexture.EMPTY;
    }
    // TODO: consider memoizing these functions, as if the same name appears twice in different models we can reuse it
    return Util.memoize(materialStr -> {
      if (!materialStr.isEmpty()) {
        MaterialVariantId material = MaterialVariantId.tryParse(materialStr);
        int color = -1;
        int luminosity = 0;
        if (material != null) {
          Optional<MaterialRenderInfo> infoOptional = MaterialRenderInfoLoader.INSTANCE.getRenderInfo(material);
          if (infoOptional.isPresent()) {
            MaterialRenderInfo info = infoOptional.get();
            ResourceLocation untinted = info.texture();
            luminosity = info.luminosity();
            if (untinted != null) {
              ArmorTexture texture = tryTexture(name, -1, luminosity, '_' + untinted.getNamespace() + '_' + untinted.getPath());
              if (texture != ArmorTexture.EMPTY) {
                return texture;
              }
            }
            color = info.vertexColor();
            for (String fallback : info.fallbacks()) {
              ArmorTexture texture = tryTexture(name, color, luminosity, '_' + fallback);
              if (texture != ArmorTexture.EMPTY) {
                return texture;
              }
            }
          }
        }
        // base texture guaranteed to exist, else we would not be in this function
        return new TintedArmorTexture(ArmorTextureSupplier.getTexturePath(name), color, luminosity);
      }
      return ArmorTexture.EMPTY;
    });
  }

  private final ResourceLocation prefix;
  private final Function<String, ArmorTexture>[] textures;
  @SuppressWarnings("unchecked")
  public MaterialArmorTextureSupplier(ResourceLocation prefix) {
    this.prefix = prefix;
      this.textures = new Function[] {
      materialGetter(prefix.withSuffix("armor")),
      materialGetter(prefix.withSuffix("leggings")),
      materialGetter(prefix.withSuffix("wings"))
    };
  }

  /** Gets the material from a given stack */
  protected abstract String getMaterial(ItemStack stack);

  @Override
  public ArmorTexture getArmorTexture(ItemStack stack, TextureType textureType, RegistryAccess access) {
    String material = getMaterial(stack);
    if (!material.isEmpty()) {
      return textures[textureType.ordinal()].apply(material);
    }
    return ArmorTexture.EMPTY;
  }

  /** Material supplier using persistent data */
  public static class PersistentData extends MaterialArmorTextureSupplier {
    public static final RecordLoadable<PersistentData> LOADER = RecordLoadable.create(
      PREFIX_FIELD,
      Loadables.RESOURCE_LOCATION.requiredField("material_key", d -> d.key),
      PersistentData::new);

    private final ResourceLocation key;

    public PersistentData(ResourceLocation prefix, ResourceLocation key) {
      super(prefix);
      this.key = key;
    }

    public PersistentData(ResourceLocation base, String suffix, ResourceLocation key) {
      this(base.withSuffix(suffix), key);
    }

    @Override
    protected String getMaterial(ItemStack stack) {
      return ModifierUtil.getPersistentString(stack, key);
    }

    @Override
    public RecordLoadable<PersistentData> getLoader() {
      return LOADER;
    }
  }

  /** Material supplier using material data */
  public static class Material extends MaterialArmorTextureSupplier {
    public static final RecordLoadable<Material> LOADER = RecordLoadable.create(
      PREFIX_FIELD,
      IntLoadable.FROM_ZERO.requiredField("index", m -> m.index),
      Material::new);

    private final int index;
    public Material(ResourceLocation prefix, int index) {
      super(prefix);
      this.index = index;
    }

    public Material(ResourceLocation base, String variant, int index) {
      this(base.withSuffix(variant), index);
    }

    @Override
    protected String getMaterial(ItemStack stack) {
      CompoundTag tag = stack.getTag();
      if (tag != null && tag.contains(ToolStack.TAG_MATERIALS, Tag.TAG_LIST)) {
        return tag.getList(ToolStack.TAG_MATERIALS, Tag.TAG_STRING).getString(index);
      }
      return "";
    }

    @Override
    public RecordLoadable<Material> getLoader() {
      return LOADER;
    }
  }
}
