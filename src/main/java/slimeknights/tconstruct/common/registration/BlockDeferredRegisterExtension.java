package slimeknights.tconstruct.common.registration;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.BlockDeferredRegister;
import slimeknights.tconstruct.common.registration.GeodeItemObject.BudSize;

import java.util.Map;

/** Additional methods in deferred register extension */
@SuppressWarnings("UnusedReturnValue")
public class BlockDeferredRegisterExtension extends BlockDeferredRegister {
  private static final BlockBehaviour.Properties POTTED_PROPS = BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY);
  public BlockDeferredRegisterExtension(String modID) {
    super(modID);
  }

  /**
   * Registers a geode block
   * @param name         Geode name
   * @param color        Color of the geode
   * @param blockSound   Sound of the block and budding block
   * @param props        Item props
   * @return The geode block
   */
  public GeodeItemObject registerGeode(String name, MapColor color, SoundType blockSound, SoundEvent chimeSound, Map<BudSize,SoundType> clusterSounds, int baseLight, Item.Properties props) {
    RegistryObject<Item> shard = itemRegister.register(name, () -> new Item(props));
    return new GeodeItemObject(shard, this, color, blockSound, chimeSound, clusterSounds, baseLight, props);
  }
}
