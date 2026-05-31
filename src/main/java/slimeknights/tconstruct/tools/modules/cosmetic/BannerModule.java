package slimeknights.tconstruct.tools.modules.cosmetic;

import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.TinkerTooltipFlags;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/** Module for banner pattern tooltips */
public enum BannerModule implements ModifierModule, DisplayNameModifierHook, TooltipModifierHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<BannerModule>defaultHooks(ModifierHooks.DISPLAY_NAME, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<BannerModule> LOADER = new SingletonLoader<>(INSTANCE);
  /** Key for a dye color, stored as its ID */
  public static final String KEY_DYE = "dye";
  /** Key for a pattern color, as a 24 bit integer */
  public static final String KEY_COLOR = "color";
  /** Key for a pattern ID or legacy short pattern hash. */
  public static final String KEY_PATTERN = "pattern";
  /** Tooltip key saying hold shift for patterns */
  private static final Component HOLD_SHIFT = TConstruct.makeTranslation("modifier", "banner.hold_shift").withStyle(ChatFormatting.GRAY);
  /** Vanilla banner pattern IDs used for texture validation. */
  public static final List<ResourceLocation> VANILLA_PATTERN_IDS = List.of(
    BannerPatterns.BASE.location(),
    BannerPatterns.SQUARE_BOTTOM_LEFT.location(),
    BannerPatterns.SQUARE_BOTTOM_RIGHT.location(),
    BannerPatterns.SQUARE_TOP_LEFT.location(),
    BannerPatterns.SQUARE_TOP_RIGHT.location(),
    BannerPatterns.STRIPE_BOTTOM.location(),
    BannerPatterns.STRIPE_TOP.location(),
    BannerPatterns.STRIPE_LEFT.location(),
    BannerPatterns.STRIPE_RIGHT.location(),
    BannerPatterns.STRIPE_CENTER.location(),
    BannerPatterns.STRIPE_MIDDLE.location(),
    BannerPatterns.STRIPE_DOWNRIGHT.location(),
    BannerPatterns.STRIPE_DOWNLEFT.location(),
    BannerPatterns.STRIPE_SMALL.location(),
    BannerPatterns.CROSS.location(),
    BannerPatterns.STRAIGHT_CROSS.location(),
    BannerPatterns.TRIANGLE_BOTTOM.location(),
    BannerPatterns.TRIANGLE_TOP.location(),
    BannerPatterns.TRIANGLES_BOTTOM.location(),
    BannerPatterns.TRIANGLES_TOP.location(),
    BannerPatterns.DIAGONAL_LEFT.location(),
    BannerPatterns.DIAGONAL_RIGHT.location(),
    BannerPatterns.DIAGONAL_LEFT_MIRROR.location(),
    BannerPatterns.DIAGONAL_RIGHT_MIRROR.location(),
    BannerPatterns.CIRCLE_MIDDLE.location(),
    BannerPatterns.RHOMBUS_MIDDLE.location(),
    BannerPatterns.HALF_VERTICAL.location(),
    BannerPatterns.HALF_HORIZONTAL.location(),
    BannerPatterns.HALF_VERTICAL_MIRROR.location(),
    BannerPatterns.HALF_HORIZONTAL_MIRROR.location(),
    BannerPatterns.BORDER.location(),
    BannerPatterns.CURLY_BORDER.location(),
    BannerPatterns.GRADIENT.location(),
    BannerPatterns.GRADIENT_UP.location(),
    BannerPatterns.BRICKS.location(),
    BannerPatterns.GLOBE.location(),
    BannerPatterns.CREEPER.location(),
    BannerPatterns.SKULL.location(),
    BannerPatterns.FLOWER.location(),
    BannerPatterns.MOJANG.location(),
    BannerPatterns.PIGLIN.location(),
    BannerPatterns.FLOW.location(),
    BannerPatterns.GUSTER.location()
  );
  /** Mapping of legacy NBT banner hashes to modern pattern IDs. */
  private static final Map<String,ResourceLocation> LEGACY_PATTERNS = Map.ofEntries(
    pattern("b", BannerPatterns.BASE),
    pattern("bl", BannerPatterns.SQUARE_BOTTOM_LEFT),
    pattern("br", BannerPatterns.SQUARE_BOTTOM_RIGHT),
    pattern("tl", BannerPatterns.SQUARE_TOP_LEFT),
    pattern("tr", BannerPatterns.SQUARE_TOP_RIGHT),
    pattern("bs", BannerPatterns.STRIPE_BOTTOM),
    pattern("ts", BannerPatterns.STRIPE_TOP),
    pattern("ls", BannerPatterns.STRIPE_LEFT),
    pattern("rs", BannerPatterns.STRIPE_RIGHT),
    pattern("cs", BannerPatterns.STRIPE_CENTER),
    pattern("ms", BannerPatterns.STRIPE_MIDDLE),
    pattern("drs", BannerPatterns.STRIPE_DOWNRIGHT),
    pattern("dls", BannerPatterns.STRIPE_DOWNLEFT),
    pattern("ss", BannerPatterns.STRIPE_SMALL),
    pattern("cr", BannerPatterns.CROSS),
    pattern("sc", BannerPatterns.STRAIGHT_CROSS),
    pattern("bt", BannerPatterns.TRIANGLE_BOTTOM),
    pattern("tt", BannerPatterns.TRIANGLE_TOP),
    pattern("bts", BannerPatterns.TRIANGLES_BOTTOM),
    pattern("tts", BannerPatterns.TRIANGLES_TOP),
    pattern("ld", BannerPatterns.DIAGONAL_LEFT),
    pattern("rd", BannerPatterns.DIAGONAL_RIGHT),
    pattern("lud", BannerPatterns.DIAGONAL_LEFT_MIRROR),
    pattern("rud", BannerPatterns.DIAGONAL_RIGHT_MIRROR),
    pattern("mc", BannerPatterns.CIRCLE_MIDDLE),
    pattern("mr", BannerPatterns.RHOMBUS_MIDDLE),
    pattern("vh", BannerPatterns.HALF_VERTICAL),
    pattern("hh", BannerPatterns.HALF_HORIZONTAL),
    pattern("vhr", BannerPatterns.HALF_VERTICAL_MIRROR),
    pattern("hhb", BannerPatterns.HALF_HORIZONTAL_MIRROR),
    pattern("bo", BannerPatterns.BORDER),
    pattern("cbo", BannerPatterns.CURLY_BORDER),
    pattern("gra", BannerPatterns.GRADIENT),
    pattern("gru", BannerPatterns.GRADIENT_UP),
    pattern("bri", BannerPatterns.BRICKS),
    pattern("glb", BannerPatterns.GLOBE),
    pattern("cre", BannerPatterns.CREEPER),
    pattern("sku", BannerPatterns.SKULL),
    pattern("flo", BannerPatterns.FLOWER),
    pattern("moj", BannerPatterns.MOJANG),
    pattern("pig", BannerPatterns.PIGLIN),
    pattern("flw", BannerPatterns.FLOW),
    pattern("gus", BannerPatterns.GUSTER)
  );

  @Override
  public RecordLoadable<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    // color the tooltip the color of the first pattern
    ListTag patterns = tool.getPersistentData().getList(patternKey(entry.getId()), ListTag.TAG_COMPOUND);
    if (!patterns.isEmpty()) {
      return name.copy().withStyle(name.getStyle().withColor(DyeColor.byId(patterns.getCompound(0).getInt(KEY_DYE)).getTextColor()));
    }
    return name;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    // add all patterns in a tinker station when holding
    if (tooltipFlag == TinkerTooltipFlags.TINKER_STATION) {
      if (tooltipKey == TooltipKey.SHIFT) {
        ListTag patterns = tool.getPersistentData().getList(patternKey(modifier.getId()), ListTag.TAG_COMPOUND);
        for (int i = 0; i < patterns.size(); i++) {
          CompoundTag tag = patterns.getCompound(i);
          DyeColor dye = DyeColor.byId(tag.getInt(KEY_DYE));
          ResourceLocation patternId = patternId(tag.getString(KEY_PATTERN));
          tooltip.add(Component.translatable("block.minecraft.banner." + patternId.toShortLanguageKey() + '.' + dye.getName()).withStyle(ChatFormatting.GRAY));
        }
      } else {
        tooltip.add(HOLD_SHIFT);
      }
    }
  }

  /** Gets the key for the cache used in the model */
  public static ResourceLocation cacheKey(ModifierId modifier) {
    return modifier.withSuffix("_cache");
  }

  /** Gets the key for the pattern list in NBT */
  public static ResourceLocation patternKey(ModifierId modifier) {
    return modifier.withSuffix("_patterns");
  }

  /** Converts a legacy short pattern hash or modern ID string to a modern pattern ID. */
  public static ResourceLocation patternId(String pattern) {
    ResourceLocation id = LEGACY_PATTERNS.get(pattern);
    if (id != null) {
      return id;
    }
    ResourceLocation parsed = ResourceLocation.tryParse(pattern);
    if (parsed != null) {
      return parsed;
    }
    return BannerPatterns.BASE.location();
  }

  private static Map.Entry<String,ResourceLocation> pattern(String legacy, ResourceKey<BannerPattern> pattern) {
    return Map.entry(legacy, pattern.location());
  }

  /** Copies the given list of patterns from banner format to the tool's NBT */
  public static void copyPatterns(ModDataNBT data, ModifierId id, DyeColor dye, ListTag banner) {
    int baseColor = Util.getColor(dye);
    ListTag patterns = new ListTag();

    // add in the base pattern, it only exists on shields and we copy from banners
    CompoundTag basePattern = new CompoundTag();
    basePattern.putString(KEY_PATTERN, BannerPatterns.BASE.location().toString());
    basePattern.putInt(KEY_DYE, dye.getId());
    basePattern.putInt(KEY_COLOR, baseColor);
    patterns.add(basePattern);

    // need a cache key, but it's just going to get hashed anyway, so store its hash
    int hashCode = baseColor;

    // add in all other patterns
    for (int i = 0; i < banner.size(); i++) {
      CompoundTag original = banner.getCompound(i);
      CompoundTag copy = new CompoundTag();
      // copy the pattern as is
      String pattern = original.getString("Pattern");
      copy.putString(KEY_PATTERN, pattern);
      // convert the color from a dye color to an integer
      dye = DyeColor.byId(original.getInt("Color"));
      int color = Util.getColor(dye);
      copy.putInt(KEY_DYE, dye.getId()); // dye for the tooltip
      copy.putInt(KEY_COLOR, color); // color for the model
      // add the values
      patterns.add(copy);
      // update the hash code with the new information
      hashCode = 31 * (31 * hashCode + color) + pattern.hashCode();
    }

    // add to tool NBT
    data.put(patternKey(id), patterns);
    data.putInt(cacheKey(id), hashCode);
  }
}
