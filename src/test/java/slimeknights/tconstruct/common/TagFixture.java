package slimeknights.tconstruct.common;

/** Helper for all our tag needs, right now we just mark tags as loaded always as we don't use them in tests. */
public class TagFixture {
  public static void init() {
    TinkerTags.tagsLoaded = true;
  }
}
