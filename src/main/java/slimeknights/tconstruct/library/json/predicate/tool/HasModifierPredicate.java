package slimeknights.tconstruct.library.json.predicate.tool;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.predicate.IJsonPredicate;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.json.IntRange;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.nbt.IToolContext;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.Locale;

/**
 * Predicate that checks a tool for the given modifier.
 * @param modifier  Modifier to check for
 * @param level     Range of levels to check for, use {@link #DEFAULT_RANGE} for simply checking for any level on the tool, 0 means not on the tool.
 * @param check     Whether to check upgrades or all modifiers
 */
public record HasModifierPredicate(ModifierId modifier, IntRange level, ModifierCheck check) implements ToolContextPredicate {
  /** Valid bounds of the modifier level, 0 being allowed means you can condition on a modifier not being present if you wish */
  public static final IntRange MAX_RANGE = new IntRange(0, Short.MAX_VALUE);
  /** Default bounds of the modifier level */
  public static final IntRange DEFAULT_RANGE = MAX_RANGE.min(1);

  public HasModifierPredicate(ModifierId modifier, ModifierCheck check) {
    this(modifier, DEFAULT_RANGE, check);
  }

  @Override
  public boolean matches(IToolContext tool) {
    return level.test(check.getModifiers(tool).getLevel(modifier));
  }

  @Override
  public IGenericLoader<? extends IJsonPredicate<IToolContext>> getLoader() {
    return LOADER;
  }

  /** Enum of modifier type */
  public enum ModifierCheck {
    UPGRADES {
      @Override
      public ModifierNBT getModifiers(IToolContext tool) {
        return tool.getUpgrades();
      }
    },
    ALL {
      @Override
      public ModifierNBT getModifiers(IToolContext tool) {
        return tool.getModifiers();
      }
    };

    public abstract ModifierNBT getModifiers(IToolContext tool);
  }

  public static final IGenericLoader<HasModifierPredicate> LOADER = new IGenericLoader<>() {
    @Override
    public HasModifierPredicate deserialize(JsonObject json) {
      ModifierId modifier = ModifierId.getFromJson(json, "modifier");
      IntRange level = DEFAULT_RANGE;
      if (json.has("level")) {
        level = MAX_RANGE.getAndDeserialize(json, "level");
      }
      ModifierCheck check = JsonHelper.getAsEnum(json, "check", ModifierCheck.class);
      return new HasModifierPredicate(modifier, level, check);
    }

    @Override
    public void serialize(HasModifierPredicate object, JsonObject json) {
      json.addProperty("modifier", object.modifier.toString());
      json.add("level", MAX_RANGE.serialize(object.level));
      json.addProperty("check", object.check.name().toLowerCase(Locale.ROOT));
    }

    @Override
    public HasModifierPredicate fromNetwork(FriendlyByteBuf buffer) {
      ModifierId modifier = ModifierId.fromNetwork(buffer);
      IntRange level = IntRange.fromNetwork(buffer);
      ModifierCheck check = buffer.readEnum(ModifierCheck.class);
      return new HasModifierPredicate(modifier, level, check);
    }

    @Override
    public void toNetwork(HasModifierPredicate object, FriendlyByteBuf buffer) {
      object.modifier.toNetwork(buffer);
      object.level.toNetwork(buffer);
      buffer.writeEnum(object.check);
    }
  };
}
