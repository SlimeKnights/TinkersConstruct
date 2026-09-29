package slimeknights.tconstruct.smeltery.block.channel;

import net.minecraft.util.StringRepresentable;
import org.apache.commons.lang3.NotImplementedException;

import java.util.Locale;

/**
 * Base interface for possible channel connections on a side.
 * Extenders must be enums, and the first value in the enum should be a default 'disabled' state, with the last a default 'enabled' state.
 * @param <E> Self type
 */
public interface ChannelConnection<E extends Enum<E> & ChannelConnection<E>> extends StringRepresentable {

  static <C extends Enum<C> & ChannelConnection<C>> C getDefaultOn(Class<C> clss) {
    var all = clss.getEnumConstants();
    return all[all.length - 1];
  }

  static <C extends Enum<C> & ChannelConnection<C>> C getDefaultOff(Class<C> clss) {
    var all = clss.getEnumConstants();
    return all[0];
  }

  /**
   * Checks if the channel can flow on this side
   *
   * @return True if the channel can flow
   */
  boolean canFlow();

  /**
   * Gets the opposite flow direction to this side
   *
   * @return Opposite direction
   */
  E reverseFlow();

  /**
   * Gets the next side in the cycle for interaction
   *
   * @param reverse If true, reverse cycle order
   * @return Next side to cycle
   */
  E getNext(boolean reverse);

  @Override
  default String getSerializedName() {
    return this.toString().toLowerCase(Locale.US);
  }

  enum TwoWay implements ChannelConnection<TwoWay> {
    /**
     * No connection on this side
     */
    NONE,
    /**
     * Channel is flowing inwards on this side
     */
    IN,
    /**
     * Channel is flowing outwards on this side
     */
    OUT;

    public boolean canFlow() {
      return this == IN || this == OUT;
    }

    public TwoWay reverseFlow() {
      return switch (this) {
        case IN -> OUT;
        case OUT -> IN;
        default -> NONE;
      };
    }

    public TwoWay getNext(boolean reverse) {
      if (reverse) {
        return switch (this) {
          case NONE -> OUT;
          case OUT -> IN;
          case IN -> NONE;
        };
      } else {
        return switch (this) {
          case NONE -> IN;
          case IN -> OUT;
          case OUT -> NONE;
        };
      }
    }
  }

  /**
   * For connections that are single mode (ie can only push fluid).
   * Names are for serialization level compatibility with {@link net.minecraft.world.level.block.state.properties.BooleanProperty BooleanProperty}.
   */
  enum OneWay implements ChannelConnection<OneWay> {
    /**
     * Does not flow this way
     */
    FALSE,
    /**
     * Flows this way
     */
    TRUE;

    @Override
    public boolean canFlow() {
      return this == TRUE;
    }

    @Override
    public OneWay reverseFlow() {
      return this;
    }

    @Override
    public OneWay getNext(boolean reverse) {
      return this == TRUE ? FALSE : TRUE;
    }
  }

  /** Enum for fudging generics when a channel cannot connect in that direction. */
  enum NoWay implements ChannelConnection<NoWay> {;
    @Override
    public boolean canFlow() { throw new NotImplementedException("Void channel connection has no members"); }

    @Override
    public NoWay reverseFlow() {throw new NotImplementedException("Void channel connection has no members"); }

    @Override
    public NoWay getNext(boolean reverse) { throw new NotImplementedException("Void channel connection has no members"); }
  }
}
