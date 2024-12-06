package slimeknights.tconstruct.library.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.loadable.Loadable;

/** Simple loadable mapping GSON to loadable. Uses NBT for networking */
public record GsonLoadable<T>(Gson gson, Class<T> classType) implements Loadable<T> {
  @Override
  public T convert(JsonElement json, String s) {
    return gson.fromJson(json, classType);
  }

  @Override
  public JsonElement serialize(T object) {
    return gson.toJsonTree(object, classType);
  }

  @Override
  public T decode(FriendlyByteBuf buffer) {
    CompoundTag tag = buffer.readAnySizeNbt();
    if (tag != null) {
      return gson.fromJson(NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, tag), classType);
    }
    throw new DecoderException("Failed to decode: " + classType.getSimpleName());
  }

  @Override
  public void encode(FriendlyByteBuf buffer, T object) {
    // TODO: do we need to support lists here? probably not as loadable gives us lists
    Tag tag = JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, gson.toJsonTree(object, classType));
    if (tag.getId() == Tag.TAG_COMPOUND) {
      buffer.writeNbt((CompoundTag)tag);
    } else {
      throw new EncoderException("Serialized wrong NBT tag type " + tag);
    }
  }
}
