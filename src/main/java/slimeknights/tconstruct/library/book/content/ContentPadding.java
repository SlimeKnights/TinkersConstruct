package slimeknights.tconstruct.library.book.content;

/** @deprecated use {@link slimeknights.mantle.client.book.data.content.ContentPadding} */
@Deprecated
public abstract class ContentPadding extends slimeknights.mantle.client.book.data.content.ContentPadding {

  /** @deprecated use {@link slimeknights.mantle.client.book.data.content.ContentPadding.ContentLeftPadding} */
  @Deprecated
  public static class ContentLeftPadding extends ContentPadding {
    @Override
    public boolean isLeft() {
      return true;
    }
  }

  /** @deprecated use {@link slimeknights.mantle.client.book.data.content.ContentPadding.ContentRightPadding} */
  @Deprecated
  public static class ContentRightPadding extends ContentPadding {
    @Override
    public boolean isLeft() {
      return false;
    }
  }

  /** @deprecated use {@link slimeknights.mantle.client.book.data.content.ContentPadding.ContentRightPadding.PaddingBookTransformer} */
  @Deprecated
  public static class PaddingBookTransformer {
    /** @deprecated use {@link slimeknights.mantle.client.book.data.content.ContentPadding.ContentRightPadding.PaddingBookTransformer#INSTANCE} */
    @Deprecated
    public static final slimeknights.mantle.client.book.data.content.ContentPadding.PaddingBookTransformer INSTANCE = slimeknights.mantle.client.book.data.content.ContentPadding.PaddingBookTransformer.INSTANCE;
  }
}
