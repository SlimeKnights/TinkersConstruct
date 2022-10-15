package slimeknights.tconstruct.library.modifiers.util;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.GsonHelper;
import slimeknights.mantle.data.GenericLoaderRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.data.GenericLoaderRegistry.IHaveLoader;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.utils.RomanNumeralHelper;

import static slimeknights.mantle.data.GenericLoaderRegistry.SingletonLoader.singleton;

public interface ModifierLevelDisplay extends IHaveLoader<ModifierLevelDisplay> {
  /** Default display, listing name followed by a roman numeral for level */
  ModifierLevelDisplay DEFAULT = singleton(loader -> new ModifierLevelDisplay() {
    @Override
    public Component nameForLevel(Modifier modifier, int level) {
      return modifier.applyStyle(new TranslatableComponent(modifier.getTranslationKey())
                                   .append(" ")
                                   .append(RomanNumeralHelper.getNumeral(level)));
    }

    @Override
    public IGenericLoader<? extends ModifierLevelDisplay> getLoader() {
      return loader;
    }
  });

  /** Loader instance */
  GenericLoaderRegistry<ModifierLevelDisplay> LOADER = new GenericLoaderRegistry<>(DEFAULT, true);

  /** Gets the name for a modifier for the given level */
  Component nameForLevel(Modifier modifier, int level);


  /* Non-default implementations */

  /** Displays just the name, for modifiers where multiple levels has no effect */
  ModifierLevelDisplay NO_LEVELS = singleton(loader -> new ModifierLevelDisplay() {
    @Override
    public Component nameForLevel(Modifier modifier, int level) {
      return modifier.getDisplayName();
    }

    @Override
    public IGenericLoader<? extends ModifierLevelDisplay> getLoader() {
      return loader;
    }
  });

  /** Displays just the name for the first level, for modifiers that can have multiple levels but don't by design */
  ModifierLevelDisplay SINGLE_LEVEL = singleton(loader -> new ModifierLevelDisplay() {
    @Override
    public Component nameForLevel(Modifier modifier, int level) {
      if (level == 1) {
        return modifier.getDisplayName();
      }
      return DEFAULT.nameForLevel(modifier, level);
    }

    @Override
    public IGenericLoader<? extends ModifierLevelDisplay> getLoader() {
      return loader;
    }
  });

  /** Displays level with pluses instead of numbers */
  ModifierLevelDisplay PLUSES = singleton(loader -> new ModifierLevelDisplay() {
    @Override
    public Component nameForLevel(Modifier modifier, int level) {
      if (level > 1) {
        return modifier.applyStyle(new TranslatableComponent(modifier.getTranslationKey()).append("+".repeat(level - 1)));
      }
      return modifier.getDisplayName();
    }

    @Override
    public IGenericLoader<? extends ModifierLevelDisplay> getLoader() {
      return loader;
    }
  });

  /**
   * Name that is unique for the first several levels
   */
  record UniqueForLevels(int unique) implements ModifierLevelDisplay {
    @Override
    public Component nameForLevel(Modifier modifier, int level) {
      if (level <= unique) {
        return modifier.applyStyle(new TranslatableComponent(modifier.getTranslationKey() + "." + level));
      }
      return DEFAULT.nameForLevel(modifier, level);
    }

    @Override
    public IGenericLoader<? extends ModifierLevelDisplay> getLoader() {
      return LOADER;
    }

    /** Loader for a unique for levels display */
    public static final IGenericLoader<UniqueForLevels> LOADER = new IGenericLoader<>() {
      @Override
      public UniqueForLevels deserialize(JsonObject json) {
        return new UniqueForLevels(GsonHelper.getAsInt(json, "unique_until"));
      }

      @Override
      public void serialize(UniqueForLevels object, JsonObject json) {
        json.addProperty("unique_until", object.unique);
      }

      @Override
      public UniqueForLevels fromNetwork(FriendlyByteBuf buffer) {
        return new UniqueForLevels(buffer.readVarInt());
      }

      @Override
      public void toNetwork(UniqueForLevels object, FriendlyByteBuf buffer) {
        buffer.writeVarInt(object.unique);
      }
    };
  }
}
