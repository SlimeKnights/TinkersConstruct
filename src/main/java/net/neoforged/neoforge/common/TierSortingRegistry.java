package slimeknights.tconstruct.compat.neoforged.neoforge.common;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;

public final class TierSortingRegistry {
  private static final List<Tier> SORTED_TIERS = List.of(Tiers.WOOD, Tiers.GOLD, Tiers.STONE, Tiers.IRON, Tiers.DIAMOND, Tiers.NETHERITE);

  private TierSortingRegistry() {}

  public static List<Tier> getSortedTiers() {
    return SORTED_TIERS;
  }

  @Nullable
  public static ResourceLocation getName(Tier tier) {
    if (tier instanceof Tiers vanilla) {
      return ResourceLocation.withDefaultNamespace(vanilla.name().toLowerCase(java.util.Locale.ROOT));
    }
    return null;
  }

  @Nullable
  public static Tier byName(ResourceLocation name) {
    if (!name.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
      return null;
    }
    return switch (name.getPath()) {
      case "wood", "wooden" -> Tiers.WOOD;
      case "gold", "golden" -> Tiers.GOLD;
      case "stone" -> Tiers.STONE;
      case "iron" -> Tiers.IRON;
      case "diamond" -> Tiers.DIAMOND;
      case "netherite" -> Tiers.NETHERITE;
      default -> null;
    };
  }

  public static boolean isCorrectTierForDrops(Tier tier, BlockState state) {
    return !state.is(tier.getIncorrectBlocksForDrops());
  }
}
