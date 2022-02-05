package slimeknights.tconstruct.library.tools.layout;

import com.google.gson.JsonElement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.VanillaIngredientSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.test.BaseMcTest;
import slimeknights.tconstruct.test.JsonFileLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class StationSlotLayoutLoaderTest extends BaseMcTest {
  private static final JsonFileLoader fileLoader = new JsonFileLoader(StationSlotLayoutLoader.GSON, StationSlotLayoutLoader.FOLDER);

  @BeforeAll
  static void setup() {
    try {
      CraftingHelper.register(new ResourceLocation("minecraft", "item"), VanillaIngredientSerializer.INSTANCE);
    } catch (Exception e) {
      // just need to ensure its registered
    }
  }

  @Test
  void minimal_noTool() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist("minimal");
    StationSlotLayoutLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));

    StationSlotLayout layout = StationSlotLayoutLoader.getInstance().get(TConstruct.getResource("minimal"));
    assertThat(layout.getName().toString()).isEqualTo("tconstruct:minimal");
    assertThat(layout.getTranslationKey()).isEmpty();
    assertThat(layout.getIcon()).isSameAs(LayoutIcon.EMPTY);
    assertThat(layout.getSortIndex()).isEqualTo(255);
    assertThat(layout.isMain()).isTrue();
    assertThat(layout.getToolSlot().isHidden()).isTrue();
    List<LayoutSlot> slots = layout.getInputSlots();
    assertThat(slots).hasSize(2);
    assertThat(slots.get(0).getX()).isEqualTo(0);
    assertThat(slots.get(0).getY()).isEqualTo(1);
    assertThat(slots.get(0).getIcon()).isNull();
    assertThat(slots.get(0).getFilter()).isNull();
    assertThat(slots.get(0).getTranslationKey()).isEmpty();
    assertThat(slots.get(1).getX()).isEqualTo(2);
    assertThat(slots.get(1).getY()).isEqualTo(3);
    assertThat(slots.get(1).getIcon()).isNull();
    assertThat(slots.get(1).getFilter()).isNull();
    assertThat(slots.get(1).getTranslationKey()).isEmpty();
  }

  @Test
  void minimal_withTool() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist("minimal_with_tool");
    StationSlotLayoutLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));

    StationSlotLayout layout = StationSlotLayoutLoader.getInstance().get(TConstruct.getResource("minimal_with_tool"));
    assertThat(layout.getName().toString()).isEqualTo("tconstruct:minimal_with_tool");
    assertThat(layout.getTranslationKey()).isEmpty();
    assertThat(layout.getIcon()).isSameAs(LayoutIcon.EMPTY);
    assertThat(layout.getSortIndex()).isEqualTo(255);
    // slots
    assertThat(layout.isMain()).isTrue();
    assertThat(layout.getToolSlot().getX()).isEqualTo(1);
    assertThat(layout.getToolSlot().getY()).isEqualTo(2);
    assertThat(layout.getToolSlot().getIcon()).isNull();
    assertThat(layout.getToolSlot().getFilter()).isNull();
    assertThat(layout.getToolSlot().getTranslationKey()).isEmpty();
    List<LayoutSlot> slots = layout.getInputSlots();
    assertThat(slots).hasSize(1);
    assertThat(slots.get(0).getX()).isEqualTo(3);
    assertThat(slots.get(0).getY()).isEqualTo(4);
    assertThat(slots.get(0).getIcon()).isNull();
    assertThat(slots.get(0).getFilter()).isNull();
    assertThat(slots.get(0).getTranslationKey()).isEmpty();
  }

  /** Checks the given ingredient is just the given item */
  private static void ingredientIsItem(Ingredient ingredient, Item item) {
    ItemStack[] stacks = ingredient.getItems();
    assertThat(stacks).hasSize(1);
    assertThat(stacks[0].getItem()).isEqualTo(item);
    assertThat(stacks[0].getTag()).isNull();
  }

  @Test
  void full() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist("full");
    StationSlotLayoutLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));

    StationSlotLayout layout = StationSlotLayoutLoader.getInstance().get(TConstruct.getResource("full"));
    assertThat(layout.getName().toString()).isEqualTo("tconstruct:full");
    assertThat(layout.getTranslationKey()).isEqualTo("translation_key");
    // icon
    ItemStack stack = layout.getIcon().getValue(ItemStack.class);
    assertThat(stack).isNotNull();
    assertThat(stack.getItem()).isEqualTo(Items.IRON_INGOT);
    CompoundTag nbt = stack.getTag();
    assertThat(nbt).isNotNull();
    assertThat(nbt.getAllKeys()).hasSize(1);
    assertThat(nbt.getInt("test")).isEqualTo(1);
    // sort key
    assertThat(layout.getSortIndex()).isEqualTo(55);
    assertThat(layout.isMain()).isFalse();
    // slots - tool
    LayoutSlot slot = layout.getToolSlot();
    assertThat(slot.getX()).isEqualTo(1);
    assertThat(slot.getY()).isEqualTo(2);
    assertThat(slot.getIcon()).isNotNull();
    assertThat(slot.getIcon().toString()).isEqualTo("test:pattern_1");
    assertThat(slot.getTranslationKey()).isEqualTo("name_1");
    assertThat(slot.getFilter()).isNotNull();
    ingredientIsItem(slot.getFilter(), Items.IRON_NUGGET);
    // inputs
    List<LayoutSlot> slots = layout.getInputSlots();
    assertThat(slots).hasSize(3);
    // input 0
    slot = slots.get(0);
    assertThat(slot.getX()).isEqualTo(3);
    assertThat(slot.getY()).isEqualTo(4);
    assertThat(slot.getIcon()).isNotNull();
    assertThat(slot.getIcon().toString()).isEqualTo("test:pattern_2");
    assertThat(slot.getTranslationKey()).isEqualTo("name_2");
    assertThat(slot.getFilter()).isNotNull();
    ingredientIsItem(slot.getFilter(), Items.DIAMOND);
    // input 1
    slot = slots.get(1);
    assertThat(slot.getX()).isEqualTo(5);
    assertThat(slot.getY()).isEqualTo(6);
    assertThat(slot.getIcon()).isNotNull();
    assertThat(slot.getIcon().toString()).isEqualTo("test:pattern_3");
    assertThat(slot.getTranslationKey()).isEqualTo("name_3");
    assertThat(slot.getFilter()).isNotNull();
    ingredientIsItem(slot.getFilter(), Items.EMERALD);
    // input 2
    slot = slots.get(2);
    assertThat(slot.getX()).isEqualTo(7);
    assertThat(slot.getY()).isEqualTo(8);
    assertThat(slot.getIcon()).isNotNull();
    assertThat(slot.getIcon().toString()).isEqualTo("test:pattern_4");
    assertThat(slot.getTranslationKey()).isEqualTo("name_4");
    assertThat(slot.getFilter()).isNotNull();
    ingredientIsItem(slot.getFilter(), Items.STONE);
  }

  @Test
  void missing_defaults() {
    StationSlotLayoutLoader.getInstance().apply(Collections.emptyMap(), mock(ResourceManager.class), mock(ProfilerFiller.class));
    StationSlotLayout layout = StationSlotLayoutLoader.getInstance().get(TConstruct.getResource("missing"));
    assertThat(layout).isSameAs(StationSlotLayout.EMPTY);
  }

  @Test
  void tooFewSlots_defaults() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist("too_few_slots");
    StationSlotLayoutLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));
    StationSlotLayout layout = StationSlotLayoutLoader.getInstance().get(TConstruct.getResource("too_few_slots"));
    assertThat(layout).isSameAs(StationSlotLayout.EMPTY);
  }

  @Test
  void tooFewSlots_withTool_defaults() {
    Map<ResourceLocation,JsonElement> splashList = fileLoader.loadFilesAsSplashlist("too_few_slots_with_tool");
    StationSlotLayoutLoader.getInstance().apply(splashList, mock(ResourceManager.class), mock(ProfilerFiller.class));
    StationSlotLayout layout = StationSlotLayoutLoader.getInstance().get(TConstruct.getResource("too_few_slots_with_tool"));
    assertThat(layout).isSameAs(StationSlotLayout.EMPTY);
  }
}
