package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ImageElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.mantle.util.ItemStackList;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.book.elements.TinkerItemElement;
import slimeknights.tconstruct.library.tools.ToolDefinition;
import slimeknights.tconstruct.library.tools.definition.PartRequirement;
import slimeknights.tconstruct.library.tools.helper.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.helper.TooltipUtil;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static slimeknights.tconstruct.library.book.content.ContentModifier.BOOK_MODIFY;
import static slimeknights.tconstruct.library.book.content.ContentModifier.IMG_SLOT_5;
import static slimeknights.tconstruct.library.book.content.ContentModifier.IMG_TABLE;
import static slimeknights.tconstruct.library.book.content.ContentModifier.TEX_SIZE;

@OnlyIn(Dist.CLIENT)
public class ContentTool extends PageContent {
  public static final transient String ID = "tool";
  private static final transient String KEY_PROPERTIES = TConstruct.makeTranslationKey("book", "tool.properties");

  /* Slot backgrounds */
  private static final transient ImageData IMG_SLOT_1x1 = ContentModifier.IMG_SLOT_1;
  private static final transient ImageData IMG_SLOT_1x2 = new ImageData(BOOK_MODIFY, 40, 75, 22, 40, TEX_SIZE, TEX_SIZE);
  private static final transient ImageData IMG_SLOT_1x3 = new ImageData(BOOK_MODIFY, 62, 75, 22, 58, TEX_SIZE, TEX_SIZE);
  private static final transient ImageData IMG_SLOT_2x1 = ContentModifier.IMG_SLOT_2;
  private static final transient ImageData IMG_SLOT_2x2 = ContentModifier.IMG_SLOT_4;
  private static final transient ImageData IMG_SLOT_2x3 = new ImageData(BOOK_MODIFY, 84, 75, 40, 58, TEX_SIZE, TEX_SIZE);
  private static final transient ImageData IMG_SLOT_3x1 = ContentModifier.IMG_SLOT_3;
  private static final transient ImageData IMG_SLOT_3x2 = new ImageData(BOOK_MODIFY, 58, 133, 58, 40, TEX_SIZE, TEX_SIZE);
  private static final transient ImageData IMG_SLOT_3x3 = new ImageData(BOOK_MODIFY, 58, 173, 58, 58, TEX_SIZE, TEX_SIZE);
  private static final transient ImageData[] IMG_SLOTS_SHAPELESS = {
    IMG_SLOT_1x1, IMG_SLOT_2x1, IMG_SLOT_3x1,
    IMG_SLOT_2x2, IMG_SLOT_5, IMG_SLOT_3x2,
    IMG_SLOT_3x3, IMG_SLOT_3x3, IMG_SLOT_3x3
  };
  private static final transient ImageData[][] IMG_SLOTS_SHAPED = {
    { IMG_SLOT_1x1, IMG_SLOT_2x1, IMG_SLOT_3x1, },
    { IMG_SLOT_1x2, IMG_SLOT_2x2, IMG_SLOT_3x2, },
    { IMG_SLOT_1x3, IMG_SLOT_2x3, IMG_SLOT_3x3, }
  };


  /* Slot positions */
  /** Locations for slots between 0 and 9 for a width of 3 */
  private static final transient SlotPos[] SLOTS_WIDTH_3 = {new SlotPos(3,  3), new SlotPos(21,  3), new SlotPos(39,  3),
                                                            new SlotPos(3, 22), new SlotPos(21, 22), new SlotPos(39, 22),
                                                            new SlotPos(3, 40), new SlotPos(21, 40), new SlotPos(39, 40)};
  /** Locations for slots between 0 and 6 in a 2x size grid */
  private static final transient SlotPos[] SLOTS_WIDTH_2 = {SLOTS_WIDTH_3[0], SLOTS_WIDTH_3[1], SLOTS_WIDTH_3[3], SLOTS_WIDTH_3[4], SLOTS_WIDTH_3[6], SLOTS_WIDTH_3[7]};
  /** Locations for slots between 0 and 3 in a 1x size grid */
  private static final transient SlotPos[] SLOTS_WIDTH_1 = {SLOTS_WIDTH_3[0], SLOTS_WIDTH_3[3], SLOTS_WIDTH_3[6]};
  /** Array of width to slot positions */
  private static final transient SlotPos[][] SLOTS_WIDTH = {SLOTS_WIDTH_1, SLOTS_WIDTH_2, SLOTS_WIDTH_3};
  /** Locations for slots between 0 and 5 in a 5 slot shapeless recipe */
  private static final transient SlotPos[] SLOTS_5 = {SLOTS_WIDTH_3[0], SLOTS_WIDTH_3[1], SLOTS_WIDTH_3[2], new SlotPos(12, 22), new SlotPos(30, 22)};

  /* Page computed data */
  private transient IModifiableDisplay tool;
  private transient List<ItemStackList> parts;
  private transient ImageData imgSlots;
  private transient SlotPos[] slotPos;

  public TextData[] text = new TextData[0];
  public String[] properties = new String[0];

  @SerializedName("tool")
  public String toolName;

  public ContentTool() {
  }

  public ContentTool(IModifiableDisplay tool) {
    this.tool = tool;
    this.toolName = Objects.requireNonNull(tool.asItem().getRegistryName()).toString();
  }

  @Override
  public void load() {
    if (this.toolName == null) {
      this.toolName = this.parent.name;
    }

    // find the tool from the registry, ensure proper interface
    if (this.tool == null) {
      Item tool = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.toolName));
      if (tool instanceof IModifiableDisplay) {
        this.tool = (IModifiableDisplay) tool;
      } else {
        this.tool = new Fallback(tool == null ? Items.BARRIER : tool);
      }
    }

    // determine the recipe to display
    if (this.parts == null || slotPos == null) {
      List<PartRequirement> required = tool.getToolDefinition().getData().getParts();
      // if no required components, do a crafting recipe lookup
      if (required.isEmpty()) {
        // get the stacks for the first crafting table recipe
        IRecipe<CraftingInventory> recipe = Optional.ofNullable(Minecraft.getInstance().world)
                                                    .flatMap(world -> world.getRecipeManager().getRecipes(IRecipeType.CRAFTING).values().stream()
                                                                           .filter(r -> r.getRecipeOutput().getItem() == tool.asItem())
                                                                           .findFirst())
                                                    .orElse(null);
        if (recipe != null) {
          // parts is just the items in the recipe
          this.parts = recipe.getIngredients().stream().map(ingredient -> ItemStackList.of(ingredient.getMatchingStacks())).collect(Collectors.toList());

          // if we have a shaped recipe, display slots in order
          if (recipe instanceof IShapedRecipe) {
            IShapedRecipe<?> shaped = (IShapedRecipe<?>) recipe;
            int width = MathHelper.clamp(shaped.getRecipeWidth() - 1, 0, 2);
            this.imgSlots = IMG_SLOTS_SHAPED[MathHelper.clamp(shaped.getRecipeHeight() - 1, 0, 2)][width];
            this.slotPos = SLOTS_WIDTH[width];
          }
        } else {
          this.parts = Collections.emptyList();
        }
      } else {
        ImmutableList.Builder<ItemStackList> partBuilder = ImmutableList.builder();
        for (int i = 0; i < required.size(); i++) {
          // mark the part as display to suppress the invalid material tooltip
          ItemStack stack = required.get(i).getPart().withMaterialForDisplay(ToolBuildHandler.getRenderMaterial(i));
          stack.getOrCreateTag().putBoolean(TooltipUtil.KEY_DISPLAY, true);
          partBuilder.add(ItemStackList.of(stack));
        }
        this.parts = partBuilder.build();
      }

      // for tool crafting and for shapeless recipes, select slots
      if (slotPos == null) {
        // determine the slot positions by number of slots
        int size = this.parts.size();
        switch (size) {
          case 4:  this.slotPos = SLOTS_WIDTH_2; break;
          case 5:  this.slotPos = SLOTS_5;       break;
          default: this.slotPos = SLOTS_WIDTH_3; break;
        }
        // slots is just the set matching the size
        if (size > 0) {
          this.imgSlots = IMG_SLOTS_SHAPELESS[Math.min(size - 1, 9)];
        }
      }
    }
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean brightSide) {
    this.addTitle(list, tool.getLocalizedName().getString());

    int padding = 5;

    // description
    int h = BookScreen.PAGE_WIDTH / 3 - 10;
    int y = getTitleHeight();
    list.add(new TextElement(padding, y, BookScreen.PAGE_WIDTH - padding * 2, h, text));

    // do we want to show the crafting recipe here perhaps? or just nothing?
    int imgWidth = 0;
    int imgHeight = 0;
    int partsSize = this.parts.size();
    if (partsSize > 0 && imgSlots != null) {
      imgWidth = imgSlots.width;
      imgHeight = imgSlots.height;
    }

    int imgX = BookScreen.PAGE_WIDTH / 2 + 20;
    int imgY = BookScreen.PAGE_HEIGHT / 2 + 30;

    imgX = imgX + 29 - imgWidth / 2;
    imgY = imgY + 20 - imgHeight / 2;

    if (properties.length > 0) {
      TextData head = new TextData(I18n.format(KEY_PROPERTIES));
      head.underlined = true;
      list.add(new TextElement(padding, 30 + h, 86 - padding, BookScreen.PAGE_HEIGHT - h - 20, head));

      List<TextData> effectData = Lists.newArrayList();
      for (String e : properties) {
        effectData.add(new TextData("\u25CF "));
        effectData.add(new TextData(e));
        effectData.add(new TextData("\n"));
      }

      list.add(new TextElement(padding, 44 + h, BookScreen.PAGE_WIDTH / 2 + 5, BookScreen.PAGE_HEIGHT - h - 20, effectData));
    }

    list.add(new ImageElement(imgX + (imgWidth - IMG_TABLE.width) / 2, imgY + 28, -1, -1, IMG_TABLE));
    if (imgSlots != null) {
      list.add(new ImageElement(imgX, imgY, -1, -1, imgSlots, book.appearance.slotColor));
    }

    ItemStack demo = tool.getRenderTool();

    TinkerItemElement toolItem = new TinkerItemElement(imgX + (imgWidth - 16) / 2, imgY - 24, 1f, demo);
    //toolItem.noTooltip = true;

    list.add(toolItem);
    list.add(new ImageElement(imgX + (imgWidth - 22) / 2, imgY - 27, -1, -1, IMG_SLOT_1x1, 0xffffff));

    for (int i = 0; i < partsSize; i++) {
      SlotPos pos = slotPos[i];
      TinkerItemElement partItem = new TinkerItemElement(imgX + pos.getX(), imgY + pos.getY(), 1f,  this.parts.get(i));
      //partItem.noTooltip = true;
      list.add(partItem);
    }
  }

  /** Simple record to hold a XY pair */
  @RequiredArgsConstructor
  @Getter
  private static class SlotPos {
    private final int x;
    private final int y;
  }

  /** Fallback for when a tool is missing the proper interface */
  private static class Fallback implements IModifiableDisplay {
    private final Item item;
    @Getter
    private final ItemStack renderTool;

    private Fallback(IItemProvider item) {
      this.item = item.asItem();
      this.renderTool = new ItemStack(item);
    }

    @Override
    public Item asItem() {
      return item;
    }

    @Override
    public ToolDefinition getToolDefinition() {
      return ToolDefinition.EMPTY;
    }
  }
}
