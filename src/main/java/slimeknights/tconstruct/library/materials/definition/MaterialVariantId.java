package slimeknights.tconstruct.library.materials.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import net.minecraft.ResourceLocationException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.data.loadable.Loadable;
import slimeknights.mantle.data.loadable.field.ContextKey;
import slimeknights.mantle.data.loadable.primitive.StringLoadable;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.utils.IdParser;

import javax.annotation.Nullable;

/** Represents a material that possibly has a variant. Variants are simply a different texture with the same material properties */
public sealed interface MaterialVariantId permits MaterialId, MaterialVariantIdImpl {
  Loadable<MaterialVariantId> LOADABLE = StringLoadable.DEFAULT.comapFlatMap((text, error) -> {
    MaterialVariantId location = tryParse(text);
    if (location == null) {
      throw error.create("Expected a material variant ID, was '" + text + "'");
    }
    return location;
  }, MaterialVariantId::toString);
  ContextKey<MaterialVariantId> CONTEXT_KEY = new ContextKey<>("material_variant");
  EntityDataSerializer<MaterialVariantId> DATA_ACCESSOR = EntityDataSerializer.simple((buffer, material) -> material.toNetwork(buffer), MaterialVariantId::fromNetwork);

  /** Variant ID that will match normal {@link MaterialId} with no variant, to allow checking for non-variant materials specifically. */
  String DEFAULT_VARIANT = "default";

  /** Gets the material ID */
  MaterialId getId();

  /** Gets the variant */
  String getVariant();

  /** Returns true if this ID has a variant */
  boolean hasVariant();

  /**
   * Gets the path for this material
   * @param separator  Variant separator
   * @return  Resource location path
   */
  ResourceLocation getLocation(char separator);

  /** Gets the texture suffix for this material */
  String getSuffix();


  /* Match methods */

  /** Checks if two material variants match. If this has no variant, matches any variant of the same material */
  boolean matchesVariant(MaterialVariantId other);

  /** Checks if two material variants match. If this has no variant, matches any variant of the same material */
  default boolean matchesVariant(MaterialVariant other) {
    return matchesVariant(other.getVariant());
  }

  /** Checks if two material variants match. If this has no variant, matches any variant of the same material */
  default boolean matchesVariant(ItemStack stack) {
    return matchesVariant(IMaterialItem.getMaterialFromStack(stack));
  }

  /** Checks if two material variants match */
  default boolean sameVariant(MaterialVariantId other) {
    return this.getId().equals(other.getId()) && this.getVariant().equals(other.getVariant());
  }

  /* Constructors */

  /** Creates a material variant instance */
  static MaterialVariantId create(String domain, String path, String variant) {
    return create(new MaterialId(domain, path), variant);
  }

  /** Creates a material variant instance */
  static MaterialVariantId create(MaterialId id, String variant) {
    if (variant.isEmpty()) {
      return id;
    }
    if (!ResourceLocation.isValidPath(variant)) {
      throw new ResourceLocationException("Non [a-z0-9/._-] character in variant of material variant ID: " + id + "#" + variant);
    }
    return new MaterialVariantIdImpl(id, variant);
  }

  /**
   * Attempts to parse the variant ID from the given string.
   * @param defaultDomain  Domain to use if unspecified in the string
   * @param string         String to parse
   * @return Variant ID, or null if invalid
   */
  @Nullable
  static MaterialVariantId tryParse(String defaultDomain, String string) {
    int index = string.indexOf('#');
    String variant = "";
    if (index >= 0) {
      variant = string.substring(index + 1);
      if (!ResourceLocation.isValidPath(variant)) {
        return null;
      }
      string = string.substring(0, index);
    }
    String[] parts = IdParser.decompose(defaultDomain, string);
    MaterialId materialId = MaterialId.tryBuild(parts[0], parts[1]);
    if (materialId == null) {
      return null;
    }
    return create(materialId, variant);
  }

  /**
   * Attempts to parse the variant ID from the given string.
   * @param string  String to parse
   * @return Variant ID, or null if invalid
   */
  @Nullable
  static MaterialVariantId tryParse(String string) {
    return tryParse("minecraft", string);
  }

  /** Checks if the given character is valid */
  private static boolean isAllowed(char ch) {
    return ch == '#' || ResourceLocation.isAllowedInResourceLocation(ch);
  }

  /**
   * Attempts to parse the variant ID from the given string reader.
   * @param reader  Reader to parse. Will set the position to after the material variant, or leave it unchanged if invalid.
   * @return Variant ID, or null if invalid
   */
  @Nullable
  static MaterialVariantId read(String defaultDomain, StringReader reader) {
    int start = reader.getCursor();
    while(reader.canRead() && isAllowed(reader.peek())) {
      reader.skip();
    }
    String substring = reader.getString().substring(start, reader.getCursor());
    MaterialVariantId variant = MaterialVariantId.tryParse(defaultDomain, substring);
    if (variant == null) {
      reader.setCursor(start);
      return null;
    }
    return variant;
  }

  /** @deprecated use {@link #read(String, StringReader)} */
  @Deprecated(forRemoval = true)
  @Nullable
  static MaterialVariantId tryParse(StringReader reader) {
    return read("minecraft", reader);
  }

  /**
   * Parses a material variant ID, throwing if invalid
   * @param text  Text to parse
   * @return  Variant ID
   */
  static MaterialVariantId parse(String text) {
    MaterialVariantId location = tryParse(text);
    if (location == null) {
      throw new JsonSyntaxException("Expected a material variant ID, was '" + text + "'");
    }
    return location;
  }


  /* JSON */

  /** Shared logic for {@link #fromJson(JsonObject, String)} and {@link #convertJson(JsonElement, String)} */
  private static MaterialVariantId parse(String text, String key) {
    MaterialVariantId location = tryParse(text);
    if (location == null) {
      throw new JsonSyntaxException("Expected " + key + " to be a material variant ID, was '" + text + "'");
    }
    return location;
  }

  /**
   * Gets a resource location from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  static MaterialVariantId fromJson(JsonObject json, String key) {
    String text = GsonHelper.getAsString(json, key);
    return parse(text, key);
  }

  /**
   * Gets a resource location from JSON, throwing a nice exception if invalid
   * @param json  JSON object
   * @param key   Key to fetch
   * @return  Resource location parsed
   */
  static MaterialVariantId convertJson(JsonElement json, String key) {
    String text = GsonHelper.convertToString(json, key);
    return parse(text, key);
  }


  /* Networking */

  /** Writes an ID to the packet buffer */
  default void toNetwork(FriendlyByteBuf buf) {
    buf.writeUtf(toString());
  }

  /** Reads an ID from the packet buffer */
  static MaterialVariantId fromNetwork(FriendlyByteBuf buf) {
    return parse(buf.readUtf(Short.MAX_VALUE));
  }
}
