package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import java.util.Objects;

/**
 * Modifier that adds an enchantment. The current implementation is a bit of a hack, and will clobber enchantments from other sources.
 * TODO 1.19: switch to new hook to make this less of a hack
 */
@RequiredArgsConstructor
public class EnchantmentModifier extends Modifier {
  private final Enchantment enchantment;
  private final int enchantmentLevel;
  private final ModifierLevelDisplay levelDisplay;

  @Override
  public Component getDisplayName(int level) {
    return levelDisplay.nameForLevel(this, level);
  }

  /** Adds an enchantment to the given tool, for use in {@link #addRawData(IToolStackView, int, RestrictedCompoundTag)} */
  public static void addEnchantmentData(RestrictedCompoundTag tag, Enchantment enchantment, int level) {
    // first, find the enchantment tag
    ListTag enchantments;
    if (tag.contains(ModifierUtil.TAG_ENCHANTMENTS, Tag.TAG_LIST)) {
      enchantments = tag.getList(ModifierUtil.TAG_ENCHANTMENTS, Tag.TAG_COMPOUND);
    } else {
      enchantments = new ListTag();
      tag.put(ModifierUtil.TAG_ENCHANTMENTS, enchantments);
    }
    // first, see if it already exists, if so we need to replace it
    String id = Objects.requireNonNull(enchantment.getRegistryName()).toString();
    for (int i = 0; i < enchantments.size(); i++) {
      CompoundTag enchantmentTag = enchantments.getCompound(i);
      if (id.equals(enchantmentTag.getString("id"))) {
        EnchantmentHelper.setEnchantmentLevel(enchantmentTag, level);
        return;
      }
    }
    // none of the existing tags match the enchant, so add it
    enchantments.add(EnchantmentHelper.storeEnchantment(enchantment.getRegistryName(), level));
  }

  /** Adds an enchantment to the given tool, for use in {@link #beforeRemoved(IToolStackView, RestrictedCompoundTag)} */
  public static void removeEnchantmentData(RestrictedCompoundTag tag, Enchantment enchantment) {
    // when removing the modifier, remove the enchant
    // this will clobber anyone else trying to remove it, not much we can do
    if (tag.contains(ModifierUtil.TAG_ENCHANTMENTS, Tag.TAG_LIST)) {
      ListTag enchantments = tag.getList(ModifierUtil.TAG_ENCHANTMENTS, Tag.TAG_COMPOUND);
      String id = Objects.requireNonNull(enchantment.getRegistryName()).toString();
      for (int i = 0; i < enchantments.size(); i++) {
        CompoundTag enchantmentTag = enchantments.getCompound(i);
        if (id.equals(enchantmentTag.getString("id"))) {
          enchantments.remove(i);
          if (enchantments.isEmpty()) {
            tag.remove(ModifierUtil.TAG_ENCHANTMENTS);
          }
          break;
        }
      }
    }
  }

  @Override
  public void addRawData(IToolStackView tool, int level, RestrictedCompoundTag tag) {
    addEnchantmentData(tag, enchantment, level * enchantmentLevel);
  }

  @Override
  public void beforeRemoved(IToolStackView tool, RestrictedCompoundTag tag) {
    removeEnchantmentData(tag, enchantment);
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  public static final IGenericLoader<EnchantmentModifier> LOADER = new IGenericLoader<>() {
    @Override
    public EnchantmentModifier deserialize(JsonObject json) {
      JsonObject enchantmentJson = GsonHelper.getAsJsonObject(json, "enchantment");
      Enchantment enchantment = JsonHelper.getAsEntry(ForgeRegistries.ENCHANTMENTS, enchantmentJson, "name");
      int level = GsonHelper.getAsInt(enchantmentJson, "level", 1);
      ModifierLevelDisplay display = ModifierLevelDisplay.LOADER.getAndDeserialize(json, "level_display");
      return new EnchantmentModifier(enchantment, level, display);
    }

    @Override
    public void serialize(EnchantmentModifier object, JsonObject json) {
      json.add("level_display", ModifierLevelDisplay.LOADER.serialize(object.levelDisplay));
      JsonObject enchantmentJson = new JsonObject();
      enchantmentJson.addProperty("name", Objects.requireNonNull(object.enchantment.getRegistryName()).toString());
      enchantmentJson.addProperty("level", object.enchantmentLevel);
      json.add("enchantment", enchantmentJson);
    }

    @Override
    public EnchantmentModifier fromNetwork(FriendlyByteBuf buffer) {
      Enchantment enchantment = buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS);
      int level = buffer.readVarInt();
      ModifierLevelDisplay display = ModifierLevelDisplay.LOADER.fromNetwork(buffer);
      return new EnchantmentModifier(enchantment, level, display);
    }

    @Override
    public void toNetwork(EnchantmentModifier object, FriendlyByteBuf buffer) {
      buffer.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, object.enchantment);
      buffer.writeVarInt(object.enchantmentLevel);
      ModifierLevelDisplay.LOADER.toNetwork(object.levelDisplay, buffer);
    }
  };
}
