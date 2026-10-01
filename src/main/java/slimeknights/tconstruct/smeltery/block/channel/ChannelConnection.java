package slimeknights.tconstruct.smeltery.block.channel;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * Base interface for possible channel connections on a side.
 */
public sealed interface ChannelConnection extends StringRepresentable {
  /**
   * Checks if the channel can flow on this side
   *
   * @return True if the channel can flow
   */
  boolean canFlow();

  /**
   * Gets this channel connection as a two-way connection (for flow direction normalisation)
   * @return This connection as {@link TwoWay}
   */
  TwoWay asTwoWay();

  /**
   * Gets the next side in the cycle for interaction.
   * <br/>
   * Should return <b>an object of same class as {@code this}</b>
   * @param reverse If true, reverse cycle order
   * @return Next side to cycle
   */
  ChannelConnection getNext(boolean reverse);

  /** Helper for calling the above method without the unchecked warning */
  @SuppressWarnings("unchecked")
  static <C extends ChannelConnection> C getNext(ChannelConnection c, boolean reverse) {
    return (C) c.getNext(reverse);
  }

  @Override
  default String getSerializedName() {
    return this.toString().toLowerCase(Locale.US);
  }

  enum TwoWay implements ChannelConnection {
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

    @Override
    public TwoWay asTwoWay() {
      return this;
    }

    /**
     * Gets the opposite flow direction to this side
     *
     * @return Opposite direction
     */
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
  enum OneWay implements ChannelConnection {
    /**
     * Does not flow this way
     */
    FALSE,
    /**
     * Flows out this way
     */
    TRUE;

    @Override
    public boolean canFlow() {
      return this == TRUE;
    }

    @Override
    public TwoWay asTwoWay() {
      return this == TRUE ? TwoWay.OUT : TwoWay.NONE;
    }

    @Override
    public OneWay getNext(boolean reverse) {
      return this == TRUE ? FALSE : TRUE;
    }
  }
}
