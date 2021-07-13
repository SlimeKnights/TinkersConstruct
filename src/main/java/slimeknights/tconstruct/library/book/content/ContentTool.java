package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.annotations.SerializedName;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.element.ImageData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.mantle.client.screen.book.element.ImageElement;
import slimeknights.mantle.client.screen.book.element.TextElement;
import slimeknights.tconstruct.library.book.TinkerPage;
import slimeknights.tconstruct.library.book.elements.TinkerItemElement;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.IModifiableDisplay;
import slimeknights.tconstruct.library.tools.part.IToolPart;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class ContentTool extends TinkerPage {

  public static final transient String ID = "tool";

  public static final transient ImageData IMG_SLOT_1 = ContentModifier.IMG_SLOT_1;
  public static final transient ImageData IMG_SLOT_2 = ContentModifier.IMG_SLOT_2;
  public static final transient ImageData IMG_SLOT_3 = ContentModifier.IMG_SLOT_3;
  public static final transient ImageData IMG_SLOT_4 = ContentModifier.IMG_SLOT_4;
  public static final transient ImageData IMG_SLOT_5 = ContentModifier.IMG_SLOT_5;

  public static final transient ImageData IMG_TABLE = ContentModifier.IMG_TABLE;

  public static final transient ImageData[] IMG_SLOTS = new ImageData[]{IMG_SLOT_1, IMG_SLOT_2, IMG_SLOT_3, IMG_SLOT_4, IMG_SLOT_5};

  public static final transient int[] SLOTS_X = new int[]{3, 21, 39, 12, 30};
  public static final transient int[] SLOTS_Y = new int[]{3, 3, 3, 22, 22};
  public static final transient int[] SLOTS_X_4 = new int[]{3, 21, 3, 21};
  public static final transient int[] SLOTS_Y_4 = new int[]{3, 3, 22, 22};

  private transient IModifiableDisplay tool;
  private transient List<ItemStack> parts;

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

    if (this.tool == null) {
      Item tool = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.toolName));
      if (!(tool instanceof IModifiableDisplay)) {
        // TODO: this is a confusing default
        this.tool = TinkerTools.pickaxe.get();
      } else
        this.tool = (IModifiableDisplay) tool;
    }

    if (this.parts == null) {
      ImmutableList.Builder<ItemStack> partBuilder = ImmutableList.builder();
      List<IToolPart> required = tool.getToolDefinition().getRequiredComponents();
      for (int i = 0; i < required.size(); i++) {
        partBuilder.add(required.get(i).withMaterialForDisplay(ToolBuildHandler.getRenderMaterial(i)));
      }
      this.parts = partBuilder.build();
    }
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean brightSide) {
    this.addTitle(list, tool.getLocalizedName().getString());

    int padding = 5;

    // description
    int h = BookScreen.PAGE_WIDTH / 3 - 10;
    int y = 16;
    list.add(new TextElement(padding, y, BookScreen.PAGE_WIDTH - padding * 2, h, text));

    ImageData img = IMG_SLOTS[this.parts.size() - 1];
    int[] slotsX = SLOTS_X;
    int[] slotsY = SLOTS_Y;

    if (this.parts.size() == 4) {
      slotsX = SLOTS_X_4;
      slotsY = SLOTS_Y_4;
    }

    int imgX = BookScreen.PAGE_WIDTH / 2 + 20;
    int imgY = BookScreen.PAGE_HEIGHT / 2 + 30;

    imgX = imgX + 29 - img.width / 2;
    imgY = imgY + 20 - img.height / 2;

    if (properties.length > 0) {
      TextData head = new TextData(parent.translate("tool.properties"));
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

    list.add(new ImageElement(imgX + (img.width - IMG_TABLE.width) / 2, imgY + 28, -1, -1, IMG_TABLE));
    list.add(new ImageElement(imgX, imgY, -1, -1, img, book.appearance.slotColor));

    ItemStack demo = tool.getRenderTool();

    TinkerItemElement toolItem = new TinkerItemElement(imgX + (img.width - 16) / 2, imgY - 24, 1f, demo);
    toolItem.noTooltip = true;

    list.add(toolItem);
    list.add(new ImageElement(imgX + (img.width - 22) / 2, imgY - 27, -1, -1, IMG_SLOT_1, 0xffffff));

    for (int i = 0; i < this.parts.size(); i++) {
      TinkerItemElement partItem = new TinkerItemElement(imgX + slotsX[i], imgY + slotsY[i], 1f,  this.parts.get(i));
      partItem.noTooltip = true;

      list.add(partItem);
    }
  }
}
