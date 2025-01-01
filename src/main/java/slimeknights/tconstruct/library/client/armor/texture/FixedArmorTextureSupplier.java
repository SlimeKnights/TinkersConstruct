package slimeknights.tconstruct.library.client.armor.texture;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.IdExtender.LocationExtender;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

import javax.annotation.Nullable;

/**
 * Armor texture supplier that supplies a fixed texture, optionally filtered on a modifier
 */
public class FixedArmorTextureSupplier implements ArmorTextureSupplier {
  public static final RecordLoadable<FixedArmorTextureSupplier> LOADER = RecordLoadable.create(
    Loadables.RESOURCE_LOCATION.requiredField("prefix", s -> s.prefix),
    StringLoadable.DEFAULT.defaultField("suffix", "", s -> s.suffix),
    ColorLoadable.ALPHA.defaultField("color", -1, s -> s.color),
    ModifierId.PARSER.nullableField("modifier", s -> s.modifier),
    FixedArmorTextureSupplier::new);

  private final ResourceLocation prefix;
  private final String suffix;
  private final int color;
  @Nullable
  private final ModifierId modifier;
  private final TintedArmorTexture[] textures;
  public FixedArmorTextureSupplier(ResourceLocation prefix, String suffix, int color, @Nullable ModifierId modifier) {
    this.prefix = prefix;
    this.suffix = suffix;
    this.color = color;
    this.modifier = modifier;
    // ensure the texture exists to add it. Not an issue during datagen as this section is not serialized
    this.textures = new TintedArmorTexture[] {
      getTexture(prefix, "armor" + suffix, color),
      getTexture(prefix, "leggings" + suffix, color),
      getTexture(prefix, "wings" + suffix, color),
    };
  }

  /** Gets the texture for the given name */
  @Nullable
  public static TintedArmorTexture getTexture(ResourceLocation base, String suffix, int color) {
    ResourceLocation name = base.withSuffix(suffix);
    if (TEXTURE_VALIDATOR.test(name)) {
      return new TintedArmorTexture(ArmorTextureSupplier.getTexturePath(name), color);
    }
    return null;
  }

  @Override
  public ArmorTexture getArmorTexture(ItemStack stack, TextureType textureType, RegistryAccess access) {
    ArmorTexture texture = textures[textureType.ordinal()];
    // skip the modifier check if the texture is not set, saves some effort
    if (texture != null && (modifier == null || ModifierUtil.getModifierLevel(stack, modifier) > 0)) {
      return texture;
    }
    return ArmorTexture.EMPTY;
  }

  @Override
  public RecordLoadable<FixedArmorTextureSupplier> getLoader() {
    return LOADER;
  }


  /* Builder */

  /** Creates a new builder instance */
  public static Builder builder(ResourceLocation base, String variant) {
    return new Builder(LocationExtender.INSTANCE.suffix(base, variant));
  }

  @Accessors(fluent = true)
  @Setter
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder {
    private final ResourceLocation name;
    @Nullable
    private ModifierId modifier;
    private int color = -1;
    private String suffix = "";

    /** Sets the suffix to a material variant */
    public Builder materialSuffix(MaterialVariantId id) {
      this.suffix = '_' + id.getSuffix();
      return this;
    }

    public FixedArmorTextureSupplier build() {
      return new FixedArmorTextureSupplier(name, suffix, color, modifier);
    }
  }
}
