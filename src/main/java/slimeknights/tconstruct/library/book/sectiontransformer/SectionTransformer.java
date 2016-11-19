package slimeknights.tconstruct.library.book.sectiontransformer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.library.book.content.ContentMaterial;

@SideOnly(Side.CLIENT)
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

  protected PageData addPage(SectionData data, String name, String type, PageContent content) {
    PageData page = new PageData(true);
    page.source = data.source;
    page.parent = data;
    page.name = name;
    page.type = type;
    page.content = content;
    page.load();

    data.pages.add(page);

    return page;
  }
}
