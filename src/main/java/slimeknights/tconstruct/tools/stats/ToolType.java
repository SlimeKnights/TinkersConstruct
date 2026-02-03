package slimeknights.tconstruct.tools.stats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.common.TinkerTags;

import javax.annotation.Nullable;
import java.util.Locale;

/** Helper for registering the different effects for modifiers that change behavior based on the tool type */
@RequiredArgsConstructor
@Getter
public enum ToolType implements StringRepresentable {
  /** Held melee weapons such as swords, does not include unarmed. */
  MELEE(TinkerTags.Items.MELEE_WEAPON),
  /** Block breaking tools such as pickaxes or swords */
  HARVEST(TinkerTags.Items.HARVEST),
  /** Ranged tools that support velocity and drawspeed */
  RANGED(TinkerTags.Items.RANGED),
  /** Ranged tools that support velocity, drawspeed, and power */
  LAUNCHER(TinkerTags.Items.LAUNCHERS),
  /** Defensive items, including held and worn armor */
  ARMOR(TinkerTags.Items.ARMOR);

  public static final ToolType[] NO_MELEE = {HARVEST, RANGED, ARMOR};

  private final TagKey<Item> tag;
  private final String serializedName = name().toLowerCase(Locale.ROOT);

  @Nullable
  public static ToolType from(Item item, ToolType... types) {
    for (ToolType type : types) {
      if (RegistryHelper.contains(type.tag, item)) {
        return type;
      }
    }
    return null;
  }
}
