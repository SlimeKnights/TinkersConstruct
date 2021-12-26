package slimeknights.tconstruct.library.recipe;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/** Extended implementation of {@link net.minecraft.advancements.criterion.NBTPredicate} that is usable in recipes */
@RequiredArgsConstructor
public class TagPredicate implements Predicate<CompoundNBT> {
  /** Internal GSON instance */
  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
  /** Instance that matches any NBT */
  public static final TagPredicate ANY = new TagPredicate(null);

  /** Tag to match against */
  @Nullable
  private final CompoundNBT tag;

  @Override
  public boolean test(@Nullable CompoundNBT toTest) {
    return NBTUtil.compareNbt(this.tag, toTest, true);
  }

  /** Serializes this into JSON */
  public JsonElement serialize() {
    if (tag == null) {
      return JsonNull.INSTANCE;
    }
    return new JsonPrimitive(tag.toString());
  }

  /** Writes this to the packet buffer */
  public void write(PacketBuffer buffer) {
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
        CompoundNBT nbt;
        if (element.isJsonObject()) {
          nbt = JsonToNBT.parseTag(GSON.toJson(element));
        } else {
          nbt = JsonToNBT.parseTag(JSONUtils.convertToString(element, "predicate"));
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
  public static TagPredicate read(PacketBuffer buffer) {
    CompoundNBT tag = null;
    if (buffer.readBoolean()) {
      tag = buffer.readNbt();
    }
    return new TagPredicate(tag);
  }
}
