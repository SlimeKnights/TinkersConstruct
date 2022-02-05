package slimeknights.tconstruct.library.json;

import com.google.gson.JsonObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.TConstruct;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/** Tag wrapper which lazily resolves a tag from the serialization. Ensures your recipe does not need the tag to exist to sync */
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class LazyTag<T> implements Tag.Named<T> {
  @Getter
  private final ResourceLocation name;
  @Nullable
  private ResourceKey<? extends Registry<T>> key;
  @Nullable
  private Tag<T> internal;

  /** Creates an instance using a tag */
  public static <T> LazyTag<T> of(Tag.Named<T> named) {
    return new LazyTag<>(named.getName(), null, named);
  }

  /** Creates an instance for the given type and name */
  public static <T> LazyTag<T> of(ResourceKey<? extends Registry<T>> key, ResourceLocation name) {
    return new LazyTag<>(name, key, null);
  }

  /** Reads a lazy tag from Json */
  public static <T> LazyTag<T> fromJson(ResourceKey<? extends Registry<T>> key, JsonObject parent, String jsonKey) {
    return of(key, JsonHelper.getResourceLocation(parent, jsonKey));
  }

  /** Reads a lazy tag from the buffer */
  public static <T> LazyTag<T> fromNetwork(ResourceKey<? extends Registry<T>> key, FriendlyByteBuf buffer) {
    return of(key, buffer.readResourceLocation());
  }

  /** Reads a lazy tag from the buffer */
  @Nullable
  public static <T> LazyTag<T> fromNetworkOptional(ResourceKey<? extends Registry<T>> key, FriendlyByteBuf buffer) {
    if (buffer.readBoolean()) {
      return of(key, buffer.readResourceLocation());
    }
    return null;
  }

  /** Fetchs the tag */
  @Nullable
  private Tag<T> getTag() {
    if (internal == null) {
      if (key == null) {
        return null;
      }
      internal = SerializationTags.getInstance().getOrEmpty(key).getTag(name);
      if (internal == null) {
        TConstruct.LOG.warn("Unknwon tag {} for registry {}", name, key);
      }
      key = null;
    }
    return internal;
  }

  @Override
  public boolean contains(T pValue) {
    Tag<T> tag = getTag();
    return tag != null && tag.contains(pValue);
  }

  @Override
  public List<T> getValues() {
    Tag<T> tag = getTag();
    return tag == null ? Collections.emptyList() : tag.getValues();
  }

  /** Writes this object to the buffer */
  public void toNetwork(FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(name);
  }

  /** Writes the tag to the network if present */
  public static void toNetworkOptional(@Nullable LazyTag<?> tag, FriendlyByteBuf buffer) {
    if (tag != null) {
      buffer.writeBoolean(true);
      tag.toNetwork(buffer);
    } else {
      buffer.writeBoolean(false);
    }
  }
}
