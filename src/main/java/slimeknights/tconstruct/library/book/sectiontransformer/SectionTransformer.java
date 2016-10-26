package slimeknights.tconstruct.library.book.sectiontransformer;

import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.SectionData;

public abstract class SectionTransformer extends BookTransformer {

  protected final String sectionName;

  public SectionTransformer(String sectionName) {
    this.sectionName = sectionName;
  }


  @Override
  public final void transform(BookData book) {
    SectionData data = null;
    for(SectionData section : book.sections) {
      if(sectionName.equals(section.name)) {
        data = section;
        break;
      }
    }

    if(data != null) {
      transform(book, data);
    }
  }

  public abstract void transform(BookData book, SectionData section);
}
