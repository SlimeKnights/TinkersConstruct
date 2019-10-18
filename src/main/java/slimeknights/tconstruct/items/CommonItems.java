package slimeknights.tconstruct.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.ObjectHolder;

import slimeknights.mantle.item.EdibleItem;
import slimeknights.mantle.item.GeneratedItem;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.item.TinkerBookItem;
import slimeknights.tconstruct.common.registry.ItemRegistryAdapter;
import slimeknights.tconstruct.library.TinkerRegistry;

import static slimeknights.tconstruct.common.TinkerPulse.injected;

@SuppressWarnings("unused")
@ObjectHolder(TConstruct.modID)
public class CommonItems {

  public static final TinkerBookItem book = injected();

  /* Bricks */
  public static final GeneratedItem seared_brick = injected();
  public static final GeneratedItem mud_brick = injected();

  /* Slime Balls */
  public static final EdibleItem blue_slime_ball = injected();
  public static final EdibleItem purple_slime_ball = injected();
  public static final EdibleItem blood_slime_ball = injected();
  public static final EdibleItem magma_slime_ball = injected();
  public static final EdibleItem pink_slime_ball = injected();

  /* Metals */
  public static final GeneratedItem cobalt_nugget = injected();
  public static final GeneratedItem cobalt_ingot = injected();
  public static final GeneratedItem ardite_nugget = injected();
  public static final GeneratedItem ardite_ingot = injected();
  public static final GeneratedItem manyullyn_nugget = injected();
  public static final GeneratedItem manyullyn_ingot = injected();
  public static final GeneratedItem pigiron_nugget = injected();
  public static final GeneratedItem pigiron_ingot = injected();
  public static final GeneratedItem alubrass_nugget = injected();
  public static final GeneratedItem alubrass_ingot = injected();

  public static final GeneratedItem green_slime_crystal = injected();
  public static final GeneratedItem blue_slime_crystal = injected();
  public static final GeneratedItem magma_slime_crystal = injected();
  public static final GeneratedItem width_expander = injected();
  public static final GeneratedItem height_expander = injected();
  public static final GeneratedItem reinforcement = injected();
  public static final GeneratedItem silky_cloth = injected();
  public static final GeneratedItem silky_jewel = injected();
  public static final GeneratedItem necrotic_bone = injected();
  public static final GeneratedItem moss = injected();
  public static final GeneratedItem mending_moss = injected();
  public static final GeneratedItem creative_modifier = injected();

  public static final GeneratedItem knightslime_nugget = injected();
  public static final GeneratedItem knightslime_ingot = injected();

  public static final GeneratedItem dried_brick = injected();

  static void registerItems(final RegistryEvent.Register<Item> event) {
    ItemRegistryAdapter registry = new ItemRegistryAdapter(event.getRegistry(), TinkerRegistry.tabGeneral);


  }


}
