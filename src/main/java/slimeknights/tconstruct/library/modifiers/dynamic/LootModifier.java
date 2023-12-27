package slimeknights.tconstruct.library.modifiers.dynamic;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.TinkerHooks;
import slimeknights.tconstruct.library.modifiers.hook.HarvestEnchantmentsModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.LootingModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.build.EnchantmentModule;
import slimeknights.tconstruct.library.modifiers.modules.combat.LootingModule;
import slimeknights.tconstruct.library.modifiers.util.ModifierHookMap.Builder;
import slimeknights.tconstruct.library.modifiers.util.ModifierLevelDisplay;
import slimeknights.tconstruct.library.tools.context.ToolHarvestContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Modifier to boost loot, from mobs or blocks
 * @deprecated use {@link ComposableModifier} with {@link LootingModule} or {@link EnchantmentModule.Harvest}
 */
@Deprecated
@RequiredArgsConstructor
public class LootModifier extends Modifier implements LootingModifierHook, HarvestEnchantmentsModifierHook {
  @Nullable
  private final Enchantment enchantment;
  private final int enchantmentLevel;
  private final int lootingLevel;
  private final ModifierLevelDisplay levelDisplay;

  public LootModifier(Enchantment enchantment, int level, ModifierLevelDisplay levelDisplay) {
    this(enchantment, level, 0, levelDisplay);
  }

  public LootModifier(int lootingLevel, ModifierLevelDisplay levelDisplay) {
    this(null, 0, lootingLevel, levelDisplay);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, TinkerHooks.TOOL_LOOTING, TinkerHooks.LEGGINGS_LOOTING, TinkerHooks.PROJECTILE_LOOTING,
                        TinkerHooks.TOOL_HARVEST_ENCHANTMENTS, TinkerHooks.LEGGINGS_HARVEST_ENCHANTMENTS);
  }

  @Override
  public Component getDisplayName(int level) {
    return levelDisplay.nameForLevel(this, level);
  }

  @Override
  public int getLootingValue(IToolStackView tool, ModifierEntry modifier, LivingEntity holder, Entity target, @Nullable DamageSource damageSource, int looting) {
    return looting + (this.lootingLevel * modifier.getLevel());
  }

  @Override
  public void applyHarvestEnchantments(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context, BiConsumer<Enchantment,Integer> consumer) {
    if (enchantment != null && enchantmentLevel > 0) {
      consumer.accept(enchantment, enchantmentLevel * modifier.getLevel());
    }
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }

  /** Loader for this modifier */
  public static final IGenericLoader<LootModifier> LOADER = new IGenericLoader<>() {
    @Override
    public LootModifier deserialize(JsonObject json) {
      Enchantment enchantment = null;
      int enchantmentLevel = 0;
      if (json.has("enchantment")) {
        JsonObject enchantmentJson = GsonHelper.getAsJsonObject(json, "enchantment");
        enchantment = JsonHelper.getAsEntry(ForgeRegistries.ENCHANTMENTS, enchantmentJson, "name");
        enchantmentLevel = GsonHelper.getAsInt(enchantmentJson, "level", 1);
      }
      int looting = GsonHelper.getAsInt(json, "looting", 0);
      ModifierLevelDisplay display = ModifierLevelDisplay.LOADER.getAndDeserialize(json, "level_display");
      return new LootModifier(enchantment, enchantmentLevel, looting, display);
    }

    @Override
    public void serialize(LootModifier object, JsonObject json) {
      json.add("level_display", ModifierLevelDisplay.LOADER.serialize(object.levelDisplay));
      if (object.enchantmentLevel > 0 && object.enchantment != null) {
        JsonObject enchantment = new JsonObject();
        enchantment.addProperty("name", Objects.requireNonNull(object.enchantment.getRegistryName()).toString());
        enchantment.addProperty("level", object.enchantmentLevel);
        json.add("enchantment", enchantment);
      }
      if (object.lootingLevel > 0) {
        json.addProperty("looting", object.lootingLevel);
      }
    }

    @Override
    public LootModifier fromNetwork(FriendlyByteBuf buffer) {
      int enchantmentLevel = buffer.readVarInt();
      Enchantment enchantment = null;
      if (enchantmentLevel > 0) {
        enchantment = buffer.readRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS);
      }
      int lootingLevel = buffer.readVarInt();
      ModifierLevelDisplay display = ModifierLevelDisplay.LOADER.fromNetwork(buffer);
      return new LootModifier(enchantment, enchantmentLevel, lootingLevel, display);
    }

    @Override
    public void toNetwork(LootModifier object, FriendlyByteBuf buffer) {
      if (object.enchantmentLevel > 0 && object.enchantment != null) {
        buffer.writeVarInt(object.enchantmentLevel);
        buffer.writeRegistryIdUnsafe(ForgeRegistries.ENCHANTMENTS, object.enchantment);
      } else {
        buffer.writeVarInt(0);
      }
      buffer.writeVarInt(object.lootingLevel);
      ModifierLevelDisplay.LOADER.toNetwork(object.levelDisplay, buffer);
    }
  };
}
