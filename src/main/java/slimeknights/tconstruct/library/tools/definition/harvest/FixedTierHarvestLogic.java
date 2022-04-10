package slimeknights.tconstruct.library.tools.definition.harvest;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.netty.handler.codec.DecoderException;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;
import slimeknights.tconstruct.library.utils.HarvestTiers;

import java.util.Objects;

/** Harvest logic that is effective if the tool has the correct tag with a fixed tier */
@RequiredArgsConstructor
public class FixedTierHarvestLogic implements IHarvestLogic {
  public static final Loader LOADER = new Loader();

  protected final TagKey<Block> tag;
  protected final Tier tier;

  @Override
  public boolean isEffective(IToolStackView tool, BlockState state) {
    return state.is(tag) && TierSortingRegistry.isCorrectTierForDrops(getTier(tool), state);
  }

  @Override
  public Tier getTier(IToolStackView tool) {
    return HarvestTiers.min(this.tier, tool.getStats().get(ToolStats.HARVEST_TIER));
  }

  @Override
  public IGenericLoader<? extends IHarvestLogic> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<FixedTierHarvestLogic> {
    @Override
    public FixedTierHarvestLogic deserialize(JsonObject json) {
      TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, JsonHelper.getResourceLocation(json, "effective"));
      ResourceLocation tierName = JsonHelper.getResourceLocation(json, "tier");
      Tier tier = TierSortingRegistry.byName(tierName);
      if (tier == null) {
        throw new JsonSyntaxException("Unknown harvest tier " + tierName);
      }
      return new FixedTierHarvestLogic(tag, tier);
    }

    @Override
    public FixedTierHarvestLogic fromNetwork(FriendlyByteBuf buffer) {
      TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, buffer.readResourceLocation());
      ResourceLocation name = buffer.readResourceLocation();
      Tier tier = TierSortingRegistry.byName(name);
      if (tier == null) {
        throw new DecoderException("Read unknown tier " + name + " from network");
      }
      return new FixedTierHarvestLogic(tag, tier);
    }

    /** Helper to get tier name or error */
    private static ResourceLocation getTierName(Tier tier) {
      return Objects.requireNonNull(TierSortingRegistry.getName(tier), "Attempt to serialize unregistered tier");
    }

    @Override
    public void serialize(FixedTierHarvestLogic object, JsonObject json) {
      json.addProperty("effective", object.tag.location().toString());
      json.addProperty("tier", getTierName(object.tier).toString());
    }

    @Override
    public void toNetwork(FixedTierHarvestLogic object, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(object.tag.location());
      buffer.writeResourceLocation(getTierName(object.tier));
    }
  }
}
