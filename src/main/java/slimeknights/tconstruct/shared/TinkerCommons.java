package slimeknights.tconstruct.shared;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.ObjectHolder;

import org.apache.logging.log4j.Logger;

import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.item.GeneratedItem;
import slimeknights.mantle.pulsar.pulse.Pulse;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.blocks.CommonBlocks;
import slimeknights.tconstruct.common.ServerProxy;
import slimeknights.tconstruct.common.TinkerPulse;
import slimeknights.tconstruct.common.conditions.ConfigOptionEnabledCondition;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.common.registry.BaseRegistryAdapter;
import slimeknights.tconstruct.common.registry.ItemRegistryAdapter;
import slimeknights.tconstruct.items.CommonItems;
import slimeknights.tconstruct.items.TinkerFoodItems;
import slimeknights.tconstruct.library.TinkerPulseIds;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.Util;

/**
 * Contains items and blocks and stuff that is shared by multiple pulses, but might be required individually
 */
@Pulse(id = TinkerPulseIds.TINKER_COMMONS_PULSE_ID, forced = true)
@ObjectHolder(TConstruct.modID)
public class TinkerCommons extends TinkerPulse {

  static final Logger log = Util.getLogger(TinkerPulseIds.TINKER_COMMONS_PULSE_ID);

  public static ServerProxy proxy = DistExecutor.runForDist(() -> CommonsClientProxy::new, () -> ServerProxy::new);


  @SubscribeEvent
  public void registerBlocks(final RegistryEvent.Register<Block> event) {
    BaseRegistryAdapter<Block> registry = new BaseRegistryAdapter<>(event.getRegistry());
    boolean forced = Config.forceRegisterAll; // causes to always register all items

    // Metals
    if (isToolsLoaded() || isSmelteryLoaded() || forced) {
      CommonBlocks.registerMetalBlocks(event);
    }

    if (isToolsLoaded() || isGadgetsLoaded() || forced) {
      CommonBlocks.registerGlowBlock(event);
    }
  }

  @SubscribeEvent
  public void registerItems(final RegistryEvent.Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabGeneral);

    registry.register(new TinkerBookItem(), "book");

    // Metals
    if (isToolsLoaded() || isSmelteryLoaded() || forced) {
      CommonItems.registerMetalBlockItems(event);
    }

    if (isToolsLoaded() || isGadgetsLoaded() || forced) {
      CommonItems.registerGlowBlockItems(event);
    }

    TinkerFoodItems.registerCommonFoodItems(event);

    if (isSmelteryLoaded() || forced) {
      registry.register(new GeneratedItem(tabGeneral), "seared_brick");
      registry.register(new GeneratedItem(tabGeneral), "mud_brick");
    }

    // Ingots and nuggets
    if (isToolsLoaded() || isSmelteryLoaded() || forced) {
      registry.register(new GeneratedItem(tabGeneral), "cobalt_nugget");
      registry.register(new GeneratedItem(tabGeneral), "cobalt_ingot");
      registry.register(new GeneratedItem(tabGeneral), "ardite_nugget");
      registry.register(new GeneratedItem(tabGeneral), "ardite_ingot");
      registry.register(new GeneratedItem(tabGeneral), "manyullyn_nugget");
      registry.register(new GeneratedItem(tabGeneral), "manyullyn_ingot");
      registry.register(new GeneratedItem(tabGeneral), "pigiron_nugget");
      registry.register(new GeneratedItem(tabGeneral), "pigiron_ingot");
      registry.register(new GeneratedItem(tabGeneral), "alubrass_nugget");
      registry.register(new GeneratedItem(tabGeneral), "alubrass_ingot");
    }

    if (isToolsLoaded() || forced) {
      TinkerFoodItems.registerToolsFoodItems(event);

      registry.register(new GeneratedItem(tabGeneral), "green_slime_crystal");
      registry.register(new GeneratedItem(tabGeneral), "blue_slime_crystal");
      registry.register(new GeneratedItem(tabGeneral), "magma_slime_crystal");
      registry.register(new GeneratedItem(tabGeneral), "width_expander");
      registry.register(new GeneratedItem(tabGeneral), "height_expander");
      registry.register(new GeneratedItem(tabGeneral), "reinforcement");
      registry.register(new GeneratedItem(tabGeneral), "silky_cloth");
      registry.register(new GeneratedItem(tabGeneral), "silky_jewel");
      registry.register(new GeneratedItem(tabGeneral), "necrotic_bone");
      registry.register(new GeneratedItem(tabGeneral), "moss");
      registry.register(new GeneratedItem(tabGeneral), "mending_moss");
      registry.register(new GeneratedItem(tabGeneral), "creative_modifier");

      registry.register(new GeneratedItem(tabGeneral), "knightslime_nugget");
      registry.register(new GeneratedItem(tabGeneral), "knightslime_ingot");
    }

    if (isGadgetsLoaded() || forced) {
      registry.register(new GeneratedItem(tabGeneral), "dried_brick");

      TinkerFoodItems.registerGadgetFoodItems(event);
    }
  }

  @SubscribeEvent
  public void registerRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
    CraftingHelper.register(ConfigOptionEnabledCondition.Serializer.INSTANCE);
  }

  @SubscribeEvent
  public void preInit(final FMLCommonSetupEvent event) {
    proxy.preInit();
  }

  @SubscribeEvent
  public void init(final InterModEnqueueEvent event) {
    proxy.init();

    MinecraftForge.EVENT_BUS.register(new AchievementEvents());
    MinecraftForge.EVENT_BUS.register(new BlockEvents());
    MinecraftForge.EVENT_BUS.register(new PlayerDataEvents());
  }

  @SubscribeEvent
  public void postInit(final InterModProcessEvent event) {
    proxy.postInit();
    TinkerRegistry.tabGeneral.setDisplayIcon(new ItemStack(CommonItems.blue_slime_ball));
  }
}
