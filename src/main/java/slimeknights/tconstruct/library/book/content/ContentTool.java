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
import slimeknights.tconstruct.library.tools.IToolPart;
import slimeknights.tconstruct.library.tools.ToolBuildHandler;
import slimeknights.tconstruct.library.tools.item.ToolCore;
import slimeknights.tconstruct.tools.TinkerTools;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ContentTool extends TinkerPage {

  public static final transient String ID = "tool";

  public static final transient int TEX_SIZE = 256;
  public static final transient ImageData IMG_SLOTS = new ImageData(ContentModifier.BOOK_MODIFY, 0, 0, 72, 72, TEX_SIZE, TEX_SIZE);
  public static final transient ImageData IMG_SLOT_1 = ContentModifier.IMG_SLOT_1;
  public static final transient ImageData IMG_TABLE = ContentModifier.IMG_TABLE;

  private transient ToolCore tool;
  private transient List<ItemStack> parts;

  public TextData[] text = new TextData[0];
  public String[] properties = new String[0];

  @SerializedName("tool")
  public String toolName;

  public ContentTool() {
  }

  public ContentTool(ToolCore tool) {
    this.tool = tool;
    this.toolName = tool.getRegistryName().toString();
  }

  @Override
  public void load() {
    if (this.toolName == null) {
      this.toolName = this.parent.name;
    }

    if (this.tool == null) {
      Item tool = ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.toolName));
      if (!(tool instanceof ToolCore)) {
        this.tool = TinkerTools.pickaxe.get();
      } else
        this.tool = (ToolCore) tool;
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

    ImageData img = IMG_SLOTS;
    int imgX = BookScreen.PAGE_WIDTH - img.width - 8;
    int imgY = BookScreen.PAGE_HEIGHT - img.height - 16;

    int toolX = imgX + (img.width - 16) / 2;
    int toolY = imgY + 28;

    y = imgY - 6;

    if (properties.length > 0) {
      TextData head = new TextData(parent.translate("tool.properties"));
      head.underlined = true;
      list.add(new TextElement(padding, y, 86 - padding, BookScreen.PAGE_HEIGHT - h - 20, head));

      List<TextData> effectData = Lists.newArrayList();
      for (String e : properties) {
        effectData.add(new TextData("\u25CF "));
        effectData.add(new TextData(e));
        effectData.add(new TextData("\n"));
      }

      y += 10;
      list.add(new TextElement(padding, y, BookScreen.PAGE_WIDTH / 2 + 5, BookScreen.PAGE_HEIGHT - h - 20, effectData));
    }

    int[] slotX = new int[]{-21, -25, 0, 25, 21};
    int[] slotY = new int[]{22, -4, -25, -4, 22};

    list.add(new ImageElement(imgX + (img.width - IMG_TABLE.width) / 2, imgY + 28, -1, -1, IMG_TABLE));
    list.add(new ImageElement(imgX, imgY, -1, -1, img, book.appearance.slotColor));

    ItemStack demo = tool.buildToolForRendering();

    TinkerItemElement toolItem = new TinkerItemElement(toolX, toolY, 1f, demo);
    toolItem.noTooltip = true;

    list.add(toolItem);
    list.add(new ImageElement(toolX - 3, toolY - 3, -1, -1, IMG_SLOT_1, 0xffffff));

    for (int i = 0; i < this.parts.size(); i++) {
      TinkerItemElement partItem = new TinkerItemElement(toolX + slotX[i], toolY + slotY[i], 1f,  this.parts.get(i));
      partItem.noTooltip = true;

      list.add(partItem);
    }
  }
}
