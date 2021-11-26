package slimeknights.tconstruct.library.book.sectiontransformer;

import com.google.common.collect.Lists;
import lombok.Getter;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.screen.book.BookScreen;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentPadding.ContentRightPadding;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Extended version of {@link ContentListingSectionTransformer} which supports putting entries in subgroups
 */
public class ContentGroupingSectionTransformer extends SectionTransformer {
  public ContentGroupingSectionTransformer(String sectionName) {
    super(sectionName);
  }

  @Override
  public void transform(BookData book, SectionData data) {
    String title = book.translate(sectionName);
    String subtextKey = sectionName + ".subtext";
    String subText = null;
    if (book.strings.containsKey(subtextKey)) {
      subText = book.translate(subtextKey);
    }

    // start building the listing
    GroupingBuilder builder = new GroupingBuilder(data, title, subText);
    data.pages.removeIf(sectionPage -> !processPage(book, builder, sectionPage));

    // create pages for each listing if any exist
    if (builder.hasEntries()) {
      // add padding page to keep indexes stretching over two pages together
      int i = 0;
      List<ContentListing> finishedListings = builder.getFinishedListings();
      if (finishedListings.size() % 2 == 0) {
        PageData padding = new PageData(true);
        padding.source = data.source;
        padding.parent = data;
        padding.content = new ContentRightPadding();
        padding.load();
        // hack: add padding to the previous section so section links start at the index
        int sectionIndex = data.parent.sections.indexOf(data);
        if (sectionIndex > 0) {
          data.parent.sections.get(sectionIndex - 1).pages.add(padding);
        } else {
          data.pages.add(padding);
          i++;
        }
      }

      // add a page for each finished listing
      for (ContentListing listing : finishedListings) {
        PageData listingPage = new PageData(true);
        listingPage.name = sectionName;
        listingPage.source = data.source;
        listingPage.parent = data;
        listingPage.content = listing;
        listingPage.load();
        data.pages.add(i, listingPage);
        i++;
      }
    }
  }

  /** Overridable method to process a single page */
  protected boolean processPage(BookData book, GroupingBuilder builder, PageData page) {
    if (!page.name.equals("hidden")) {
      builder.addPage(page.getTitle(), page);
    }
    return true;
  }

  /** Builder to create a all content listing pages */
  public static class GroupingBuilder {
    private static final int COLUMN_WIDTH = BookScreen.PAGE_WIDTH / 3;

    /** Section containing this grouping */
    private final SectionData section;
    /** Number of columns on the page, makes a new page if its 3 */
    private int columns = 1;
    /** Number of entries in the current column, when too big a new column starts */
    private int entriesInColumn = 0;
    /** Max number of entries in a column */
    private int maxInColumn;
    /** Listing that is currently being built */
    private ContentListing currentListing = new ContentListing();
    /** All listings to include in the book */
    @Getter
    private final List<ContentListing> finishedListings = Lists.newArrayList(currentListing);

    public GroupingBuilder(SectionData section, @Nullable String title, @Nullable String subText) {
      this.section = section;
      currentListing.title = title;
      currentListing.subText = subText;
      maxInColumn = currentListing.getEntriesInColumn(section);
    }

    /** If true, entries were added */
    public boolean hasEntries() {
      return entriesInColumn > 0; // should be sufficient, only 0 when nothing is added
    }

    /** Increases the number of entries in the column */
    private void incrementColumns(String text) {
      entriesInColumn += section.parent.fontRenderer.getWordWrappedHeight(text, COLUMN_WIDTH) / 9;
    }

    /** Starts a new column */
    private void startNewColumn(boolean forceBreak) {
      // already have 3 columns? start a new one
      if (columns == 3) {
        currentListing = new ContentListing();
        currentListing.title = finishedListings.get(0).title;
        maxInColumn = currentListing.getEntriesInColumn(section);
        finishedListings.add(currentListing);
        columns = 1;
      } else {
        // 1 or 2 columns? force break
        columns++;
        if (forceBreak) {
          currentListing.addColumnBreak();
        }
      }
      entriesInColumn = 0;
    }

    /** Adds a group to this listing */
    public void addGroup(String name, @Nullable PageData data) {
      // if a group already exists, start a new column
      if (entriesInColumn != 0) {
        startNewColumn(true);
      }

      // add the title entry to the column
      incrementColumns(name);
      currentListing.addEntry(name, data, true);
    }

    /** Adds a page to the current group in the listing */
    public void addPage(String name, PageData data) {
      if (entriesInColumn == maxInColumn) {
        startNewColumn(false);
      }
      incrementColumns("- " + name);
      currentListing.addEntry(name, data, false);
    }
  }
}
