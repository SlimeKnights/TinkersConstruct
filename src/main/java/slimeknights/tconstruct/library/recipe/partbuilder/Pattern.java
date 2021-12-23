package slimeknights.tconstruct.library.recipe.partbuilder;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * This is a copy of resource location with a couple extra helpers
 */
public class Pattern extends ResourceLocation {
  public static final Serializer SERIALIZER = new Serializer();

  public Pattern(String resourceName) {
    super(resourceName);
  }

  public Pattern(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public Pattern(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }

  /**
   * Creates a new modifier ID from the given string
   * @param string  String
   * @return  Material ID, or null if invalid
   */
  @Nullable
  public static ModifierId tryCreate(String string) {
    try {
      return new ModifierId(string);
    } catch (ResourceLocationException resourcelocationexception) {
      return null;
    }
  }

  /**
   * Gets the display name for this pattern
   * @return  Display name
   */
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent(Util.makeTranslationKey("pattern", this));
  }

  /**
   * Gets the texture for this pattern for rendering
   * @return  Pattern texture
   */
  public ResourceLocation getTexture() {
    return new ResourceLocation(getNamespace(), "gui/tinker_pattern/" + getPath());
  }

  /** Type sensitive version of the resource location serializer */
  protected static class Serializer implements JsonDeserializer<Pattern>, JsonSerializer<Pattern> {
    @Override
    public Pattern deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
      return new Pattern(JSONUtils.getString(json, "location"));
    }

    @Override
    public JsonElement serialize(Pattern pattern, Type type, JsonSerializationContext context) {
      return new JsonPrimitive(pattern.toString());
    }
  }
}
