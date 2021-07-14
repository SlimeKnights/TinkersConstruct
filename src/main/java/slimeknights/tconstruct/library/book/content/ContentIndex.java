package slimeknights.tconstruct.library.book.content;

import com.google.common.collect.ImmutableSet;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.screen.book.element.BookElement;

import java.util.ArrayList;
import java.util.Set;

public class ContentIndex extends ContentListing {
  public static final transient String ID = "index";

  private transient boolean loaded = false;
  private String[] hidden;

  @Override
  public void build(BookData book, ArrayList<BookElement> list, boolean rightSide) {
    // load as late as possible, to ensure the index is properly filled
    if (!loaded) {
      loaded = true;
      Set<String> hiddenSet = hidden == null
                              ? ImmutableSet.of("hidden")
                              : ImmutableSet.<String>builder().add("hidden").add(hidden).build();
      parent.parent.pages.forEach(page -> {
        if (page != parent && !hiddenSet.contains(page.name)) {
          addEntry(book.translate(page.getTitle()), page);
        }
      });
    }
    super.build(book, list, rightSide);
  }
}
