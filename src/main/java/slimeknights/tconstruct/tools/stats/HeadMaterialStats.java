package slimeknights.tconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.materials.stats.BaseMaterialStats;
import slimeknights.tconstruct.library.materials.stats.IRepairableMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

import java.util.List;
import java.util.Objects;

@ToString
public class HeadMaterialStats extends BaseMaterialStats implements IRepairableMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(TConstruct.getResource("head"));
  public static final HeadMaterialStats DEFAULT = new HeadMaterialStats(1, 1f, Tiers.WOOD, 1f);
  // tooltip descriptions
  private static final List<Component> DESCRIPTION = ImmutableList.of(ToolStats.DURABILITY.getDescription(), ToolStats.HARVEST_TIER.getDescription(), ToolStats.MINING_SPEED.getDescription(), ToolStats.ATTACK_DAMAGE.getDescription());
  // default tier for invalid datapacks, prevents a hard to read crash
  private static final ResourceLocation WOOD_TIER = new ResourceLocation("wood");


  @Getter
  private final int durability;
  @Getter
  private final float miningSpeed;
  private final ResourceLocation harvestTier;
  @Getter
  private final float attack;

  /** Cached tier fetched from the sorting registry */
  private transient Tier tier;

  public HeadMaterialStats(int durability, float miningSpeed, Tier tier, float attack) {
    this.durability = durability;
    this.miningSpeed = miningSpeed;
    this.harvestTier = Objects.requireNonNull(TierSortingRegistry.getName(tier), "Cannot create head material stats with unsorted tier");
    this.tier = tier;
    this.attack = attack;
  }

  public HeadMaterialStats(FriendlyByteBuf buffer) {
    this.durability = buffer.readInt();
    this.miningSpeed = buffer.readFloat();
    this.harvestTier = buffer.readResourceLocation();
    this.attack = buffer.readFloat();
  }

  /** Gets the ID of the tier from the tier sorting registry */
  public ResourceLocation getTierId() {
    return harvestTier;
  }

  /** Gets the tier stored in this object */
  public Tier getTier() {
    if (this.tier == null) {
      // fetch by name, fallback to the first tier
      if (this.harvestTier != null) {
        this.tier = TierSortingRegistry.byName(harvestTier);
      }
      if (this.tier == null) {
        TConstruct.LOG.error("Failed to find tool tier by name " + harvestTier);
        this.tier = DEFAULT.getTier();
      }
    }
    return this.tier;
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(this.durability);
    buffer.writeFloat(this.miningSpeed);
    if (this.harvestTier == null) {
      TConstruct.LOG.error("Unset harvest tier for head stats");
      buffer.writeResourceLocation(WOOD_TIER);
    } else {
      buffer.writeResourceLocation(this.harvestTier);
    }
    buffer.writeFloat(this.attack);
  }

  @Override
  public MaterialStatsId getIdentifier() {
    return ID;
  }

  @Override
  public List<Component> getLocalizedInfo() {
    List<Component> info = Lists.newArrayList();
    info.add(ToolStats.DURABILITY.formatValue(this.durability));
    info.add(ToolStats.HARVEST_TIER.formatValue(this.getTier()));
    info.add(ToolStats.MINING_SPEED.formatValue(this.miningSpeed));
    info.add(ToolStats.ATTACK_DAMAGE.formatValue(this.attack));
    return info;
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HeadMaterialStats that = (HeadMaterialStats)o;
    return this.durability == that.durability && this.miningSpeed == that.miningSpeed && this.attack == that.attack && Objects.equals(this.harvestTier, that.harvestTier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(durability, miningSpeed, harvestTier, attack);
  }
}
