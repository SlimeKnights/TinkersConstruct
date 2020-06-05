package slimeknights.tconstruct.shared;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.Logger;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.items.FoodItems;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;
import slimeknights.tconstruct.shared.block.SlimeBlock;

/**
 * Contains items and blocks and stuff that is shared by multiple modules, but might be required individually
 */
public class TinkerCommons extends TinkerModule {
  static final Logger log = Util.getLogger("tinker_commons");

  @SubscribeEvent
  public void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    CraftingHelper.register(ConfigOptionEnabledCondition.Serializer.INSTANCE);
  }
  
  @SubscribeEvent
  public void commonSetup(final FMLCommonSetupEvent event) {
    TinkerRegistry.tabGeneral.setDisplayIcon(new ItemStack(FoodItems.slime_ball.get(SlimeBlock.SlimeType.BLUE)));
  }
}
