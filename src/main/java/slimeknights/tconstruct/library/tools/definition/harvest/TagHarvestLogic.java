package slimeknights.tconstruct.library.tools.definition.harvest;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import slimeknights.mantle.data.GenericLoaderRegistry.IGenericLoader;
import slimeknights.mantle.util.JsonHelper;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

/** Harvest logic that is effective if the tool has the correct tag */
@RequiredArgsConstructor
public class TagHarvestLogic implements IHarvestLogic {
  public static final Loader LOADER = new Loader();

  protected final TagKey<Block> tag;

  @Override
  public boolean isEffective(IToolStackView tool, BlockState state) {
    return state.is(tag) && TierSortingRegistry.isCorrectTierForDrops(tool.getStats().get(ToolStats.HARVEST_TIER), state);
  }

  @Override
  public float getDestroySpeed(IToolStackView tool, BlockState state) {
    // destroy speed does not require right tier to boost
    return state.is(tag) ? tool.getStats().get(ToolStats.MINING_SPEED) : 1.0f;
  }

  @Override
  public IGenericLoader<? extends IHarvestLogic> getLoader() {
    return LOADER;
  }

  private static class Loader implements IGenericLoader<TagHarvestLogic> {
    @Override
    public TagHarvestLogic deserialize(JsonObject json) {
      TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, JsonHelper.getResourceLocation(json, "effective"));
      return new TagHarvestLogic(tag);
    }

    @Override
    public TagHarvestLogic fromNetwork(FriendlyByteBuf buffer) {
      TagKey<Block> tag = TagKey.create(Registry.BLOCK_REGISTRY, buffer.readResourceLocation());
      return new TagHarvestLogic(tag);
    }

    @Override
    public void serialize(TagHarvestLogic object, JsonObject json) {
      json.addProperty("effective", object.tag.location().toString());
    }

    @Override
    public void toNetwork(TagHarvestLogic object, FriendlyByteBuf buffer) {
      buffer.writeResourceLocation(object.tag.location());
    }
  }
}
