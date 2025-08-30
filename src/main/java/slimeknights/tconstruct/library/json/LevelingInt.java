package slimeknights.tconstruct.library.json;

import slimeknights.mantle.data.loadable.primitive.IntLoadable;
import slimeknights.mantle.data.loadable.record.RecordLoadable;

/**
 * Represents an int value that has a part that scales with level and a part that does not scale.
 * @param flat      Value that does not change with level
 * @param eachLevel  Value that changes each level
 * @see RandomLevelingValue
 */
public record LevelingInt(int flat, int eachLevel) {
  /** Instance that returns zero regardless */
  public static final LevelingInt ZERO = new LevelingInt(0, 0);
  /** Loadable instance for parsing */
  public static final RecordLoadable<LevelingInt> LOADABLE = RecordLoadable.create(
      IntLoadable.ANY_SHORT.defaultField("flat", 0, LevelingInt::flat),
      IntLoadable.ANY_SHORT.defaultField("each_level", 0, LevelingInt::eachLevel),
      LevelingInt::new);

  /** Computes the value for the given level */
  public int compute(int level) {
    return this.flat + this.eachLevel * level;
  }

  /** Computes the value for the given float level */
  public int compute(float level) {
    return (int)(this.flat + this.eachLevel * level);
  }

  /** Computes the value for the given level but returns 0 if level is 0 */
  public int computeForLevel(int level) {
    return level > 0 ? compute(level) : 0;
  }

  /** Computes the value for the given float level but returns 0 if level is 0 */
  public int computeForLevel(float level) {
    return level > 0 ? compute(level) : 0;
  }


  /* Construction */

  /** Creates a value that stays constant each level */
  public static LevelingInt flat(int flat) {
    return new LevelingInt(flat, 0);
  }

  /** Creates a value that is multiplied by the level */
  public static LevelingInt eachLevel(int eachLevel) {
    return new LevelingInt(0, eachLevel);
  }


  /** Trait to mix into a builder using leveling values */
  public interface Builder<M> {
    /** Creates an instance with a flat value and a leveling value*/
    M amount(int flat, int eachLevel);

    /** Creates an instance with a value that ignores level */
    default M flat(int flat) {
      return amount(flat, 0);
    }

    /** Creates an instance with a value that increases each level */
    default M eachLevel(int eachLevel) {
      return amount(0, eachLevel);
    }
  }
}
