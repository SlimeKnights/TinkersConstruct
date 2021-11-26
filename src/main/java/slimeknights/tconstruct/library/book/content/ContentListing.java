package slimeknights.tconstruct.library.book.content;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.action.protocol.ProtocolGoToPage;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.mantle.client.screen.book.element.BookElement;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.book.TinkerPage;
import slimeknights.tconstruct.library.book.elements.ListingLeftElement;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ContentListing extends TinkerPage {
  public static final int LINE_HEIGHT = 10;

  public String title = null;
  public String subText = null;
  /** Outer list represents all columns, inner list represents entries in a column */
  private transient final List<List<TextData>> entries = Util.make(() -> {
    List<List<TextData>> lists = new ArrayList<>(1);
    lists.add(new ArrayList<>());
    return lists;
  });

  /**
   * Adds an entry to the list
   * @param text        Title of entry
   * @param link        Page to link to
   * @param subSection  If true, this entry is a subsection and will be bold with no bullet point
   */
  public void addEntry(String text, @Nullable PageData link, boolean subSection) {
    TextData data = new TextData(text);
    data.bold = subSection;
    if (link != null) {
      data.action = ProtocolGoToPage.GO_TO_RTN + ":" + link.parent.name + "." + link.name;
    }
    this.entries.get(this.entries.size() - 1).add(data);
  }

  /**
   * Adds an entry to the list that is not a subsection
   * @param text        Title of entry
   * @param link        Page to link to
   */
  public void addEntry(String text, @Nullable PageData link) {
    addEntry(text, link, false);
  }

  /** Forces a column break */
  public void addColumnBreak() {
    if (!this.entries.get(this.entries.size() - 1).isEmpty()) {
      if (this.entries.size() == 3) {
        TConstruct.LOG.warn("Too many columns in content listing, you should create a second listing instead");
      }
      this.entries.add(new ArrayList<>());
    }
  }

  /** If true, there are entries in this listing */
  public boolean hasEntries() {
    return this.entries.get(0).size() > 0;
  }

  /** Gets the height for a column in pixels */
  private static int getColumnHeight(int yOff) {
    int columnHeight = BookScreen.PAGE_HEIGHT - yOff - 16;
    if (columnHeight % LINE_HEIGHT != 0) {
      columnHeight -= columnHeight % LINE_HEIGHT;
    }
    return columnHeight;
  }

  /** Gets the number of elements that fits in a column, inefficient so suggest not calling this frequently */
  public int getEntriesInColumn(SectionData sectionData) {
    int yOff = 0;
    if (this.title != null) {
      yOff = 16;
    }
    if (this.subText != null) {
      yOff += sectionData.parent.fontRenderer.getWordWrappedHeight(this.subText, BookScreen.PAGE_WIDTH) * 12 / 9;
    }
    return getColumnHeight(yOff) / LINE_HEIGHT;
  }

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    int yOff = 0;
    if (this.title != null) {
      this.addTitle(list, this.title, false);
      yOff = 16;
    }
    if (this.subText != null) {
      int height = this.addText(list, this.subText, false, 0, yOff);
      yOff += height;
    }

    // 16 gives space for the bottom and ensures a round number, yOff ensures the top is not counted
    int columnHeight = getColumnHeight(yOff);

    // determine how wide we can make each column, support up to 3
    int width = BookScreen.PAGE_WIDTH;
    int finalColumns = this.entries.size();
    int entriesPerColumn = columnHeight / LINE_HEIGHT;
    if (finalColumns < 3) {
      for (List<TextData> column : this.entries) {
        int totalEntries = column.size();
        while (totalEntries > entriesPerColumn) {
          finalColumns++;
          if (finalColumns == 3) {
            break;
          }
          totalEntries -= entriesPerColumn;
        }
      }
    }
    if (finalColumns > 3) {
      finalColumns = 3;
    }
    width /= finalColumns;

    int x = 0;
    int y = 0;
    for (List<TextData> column : this.entries) {
      // add each page to the column
      for (TextData data : column) {
        String text = data.text;
        if (text.isEmpty()) {
          y += LINE_HEIGHT;
        } else {
          if (!data.bold) text = "- " + text;
          int height = this.parent.parent.parent.fontRenderer.getWordWrappedHeight(text, width) * LINE_HEIGHT / 9;
          list.add(new ListingLeftElement(x, y + yOff, width, height, data.bold, data));
          y += height;
        }
        if (y >= columnHeight) {
          x += width;
          y = 0;
        }
      }
      // reset column
      x += width;
      y = 0;
    }
  }
}
