package slimeknights.tconstruct.library.client.materials;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.loadable.field.RecordField;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.mantle.util.typed.TypedMap;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;
import java.util.Objects;

/** Field handling the three state material texture */
enum MaterialTextureField implements RecordField<ResourceLocation, MaterialRenderInfo> {
  INSTANCE;

  @Nullable
  @Override
  public ResourceLocation get(JsonObject json, TypedMap context) {
    // if we have a texture, use that
    if (json.has("texture")) {
      return JsonHelper.getResourceLocation(json, "texture", null);
    }
    MaterialVariantId material = Objects.requireNonNull(context.get(MaterialVariantId.CONTEXT_KEY), "Unable to fetch material variant from context, this usually implements a broken JSON deserializer");
    // legacy support for old skip unique texture boolean, remove at some point in the future
    if (GsonHelper.getAsBoolean(json, "skipUniqueTexture", false)) {
      TConstruct.LOG.warn("Using deprecated boolean skipUniqueTexture on material " + material + ", just set 'texture' to 'null'");
      return null;
    }
    return material.getLocation('_');
  }

  @Override
  public void serialize(MaterialRenderInfo parent, JsonObject json) {
    ResourceLocation texture = parent.texture();
    if (texture == null) {
      json.add("texture", JsonNull.INSTANCE);
    } else if (!texture.equals(parent.id().getLocation('_'))) {
      json.addProperty("texture", texture.toString());
    }
  }

  /** Enum representing options for the texture */
  private enum TextureType {
    /** Uses the material ID for texture */
    DEFAULT,
    /** No texture */
    NONE,
    /** Uses a specific texture name */
    NAME
  }

  @Nullable
  @Override
  public ResourceLocation decode(FriendlyByteBuf buffer, TypedMap context) {
    return switch (buffer.readEnum(TextureType.class)) {
      case NONE -> null;
      case DEFAULT -> Objects.requireNonNull(context.get(MaterialVariantId.CONTEXT_KEY)).getLocation('_');
      case NAME -> buffer.readResourceLocation();
    };
  }

  @Override
  public void encode(FriendlyByteBuf buffer, MaterialRenderInfo parent) {
    ResourceLocation texture = parent.texture();
    // save some network traffic if the texture is the ID, since we already need an extra byte to specify null
    if (texture == null) {
      buffer.writeEnum(TextureType.NONE);
    } else if (texture.equals(parent.id().getLocation('_'))) {
      buffer.writeEnum(TextureType.DEFAULT);
    } else {
      buffer.writeEnum(TextureType.NAME);
      buffer.writeResourceLocation(texture);
    }
  }
}
