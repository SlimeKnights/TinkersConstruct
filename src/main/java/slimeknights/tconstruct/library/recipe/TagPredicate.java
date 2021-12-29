package slimeknights.tconstruct.library.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/** Extended implementation of {@link net.minecraft.advancements.criterion.NBTPredicate} that is usable in recipes */
@RequiredArgsConstructor
public class TagPredicate implements Predicate<CompoundTag> {
  /** Internal GSON instance */
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
  /** Instance that matches any NBT */
  public static final TagPredicate ANY = new TagPredicate(null);

  /** Tag to match against */
  @Nullable
  private final CompoundTag tag;

  @Override
  public boolean test(@Nullable CompoundTag toTest) {
    return NbtUtils.compareNbt(this.tag, toTest, true);
  }

  /** Serializes this into JSON */
  public JsonElement serialize() {
    if (tag == null) {
      return JsonNull.INSTANCE;
    }
    return new JsonPrimitive(tag.toString());
  }

  /** Writes this to the packet buffer */
  public void write(FriendlyByteBuf buffer) {
    if (tag != null) {
      buffer.writeBoolean(true);
      buffer.writeNbt(tag);
    } else {
      buffer.writeBoolean(false);
    }
  }

  /** Reads a predicate from JSON */
  public static TagPredicate deserialize(JsonElement element) {
    if (!element.isJsonNull()) {
      try {
        CompoundTag nbt;
        if (element.isJsonObject()) {
          nbt = TagParser.parseTag(GSON.toJson(element));
        } else {
          nbt = TagParser.parseTag(GsonHelper.convertToString(element, "predicate"));
        }
        return new TagPredicate(nbt);
      } catch (CommandSyntaxException ex) {
        throw new JsonSyntaxException("Invalid nbt tag: ", ex);
      }
    } else {
      return ANY;
    }
  }

  /** Reads a predicate from a packet buffer */
  public static TagPredicate read(FriendlyByteBuf buffer) {
    CompoundTag tag = null;
    if (buffer.readBoolean()) {
      tag = buffer.readNbt();
    }
    return new TagPredicate(tag);
  }
}
