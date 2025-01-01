package slimeknights.tconstruct.library.client.armor.texture;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.data.loadable.common.ColorLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.util.IdExtender.LocationExtender;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

import javax.annotation.Nullable;
import java.util.Objects;

import static slimeknights.tconstruct.library.client.armor.texture.FixedArmorTextureSupplier.getTexture;

/**
 * Armor texture supplier that supplies a fixed texture that is colored using the given persistent data key
 */
public class DyedArmorTextureSupplier implements ArmorTextureSupplier {
  public static final RecordLoadable<DyedArmorTextureSupplier> LOADER = RecordLoadable.create(
    Loadables.RESOURCE_LOCATION.requiredField("prefix", s -> s.prefix),
    ModifierId.PARSER.requiredField("modifier", s -> s.modifier),
    ColorLoadable.NO_ALPHA.nullableField("default_color", s -> s.alwaysRender ? s.defaultColor : null),
    DyedArmorTextureSupplier::new);

  private final ResourceLocation prefix;
  private final ModifierId modifier;
  private final boolean alwaysRender;
  private final int defaultColor;
  private final TintedArmorTexture[] textures;

  public DyedArmorTextureSupplier(ResourceLocation prefix, ModifierId modifier, @Nullable Integer defaultColor) {
    this.prefix = prefix;
    this.modifier = modifier;
    this.alwaysRender = defaultColor != null;
    this.defaultColor = Objects.requireNonNullElse(defaultColor, -1);
    this.textures = new TintedArmorTexture[] {
      getTexture(prefix, "armor", -1),
      getTexture(prefix, "leggings", -1),
      getTexture(prefix, "wings", -1),
    };
  }

  public DyedArmorTextureSupplier(ResourceLocation base, String variant, ModifierId modifier, @Nullable Integer defaultColor) {
    this(LocationExtender.INSTANCE.suffix(base, variant), modifier, defaultColor);
  }

  @Override
  public ArmorTexture getArmorTexture(ItemStack stack, TextureType textureType, RegistryAccess access) {
    TintedArmorTexture texture = textures[textureType.ordinal()];
    if (texture != null && (alwaysRender || ModifierUtil.getModifierLevel(stack, modifier) > 0)) {
      int color = ModifierUtil.getPersistentInt(stack, modifier, defaultColor);
      return texture.color(0xFF000000 | color);
    }
    return ArmorTexture.EMPTY;
  }

  @Override
  public RecordLoadable<DyedArmorTextureSupplier> getLoader() {
    return LOADER;
  }
}
