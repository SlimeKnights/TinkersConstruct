package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.utils.RestrictedCompoundTag;

import java.util.Objects;

/** @deprecated use {@link EnchantmentModule.Constant} */
@Deprecated
@RequiredArgsConstructor
public class EnchantmentModifier extends Modifier {
  private final Enchantment enchantment;
  private final int enchantmentLevel;
  private final ModifierLevelDisplay levelDisplay;

  @Override
  public Component getDisplayName(int level) {
    return levelDisplay.nameForLevel(this, level);
  }

  /** @deprecated use {@link EnchantmentModule#setEnchantmentLevel(RestrictedCompoundTag, Enchantment, int)} */
  @Deprecated
  public static void addEnchantmentData(RestrictedCompoundTag tag, Enchantment enchantment, int level) {
    EnchantmentModule.setEnchantmentLevel(tag, enchantment, level);
  }

  /** @deprecated use {@link EnchantmentModule#removeEnchantment(RestrictedCompoundTag, Enchantment)} */
  @Deprecated
  public static void removeEnchantmentData(RestrictedCompoundTag tag, Enchantment enchantment) {
    EnchantmentModule.removeEnchantment(tag, enchantment);
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
