package robocode.control.snapshot;

/**
 * @author Thales B.V. / Colin Heppener (Naval Robocode contributor)
 */
public enum MissileState {

    /** The missile has just been fired this turn and hence just been created. This state only last one turn. */
    FIRED(0),

    /** The missile is now moving across the battlefield, but has not hit anything yet. */
    MOVING(1),

    ARRIVED(2),

    EXPLODING(3),
    /** The missile currently represents a robot explosion, i.e. a robot death. */
    EXPLODED(4),

    /** The missile is currently inactive. Hence, it is not active or visible on the battlefield. */
    INACTIVE(5);

    private final int value;

    private MissileState(int value) {
        this.value = value;
    }

    /**
     * Returns the state as an integer value.
     *
     * @return an integer value representing this state.
     *
     * @see #toState(int)
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns a MissileState based on an integer value that represents a MissileState.
     *
     * @param value the integer value that represents a specific MissileState.
     * @return a MissileState that corresponds to the specific integer value.
     *
     * @see #getValue()
     *
     * @throws IllegalArgumentException if the specified value does not correspond
     *                                  to a MissileState and hence is invalid.
     */
    public static MissileState toState(int value) {
        switch (value) {
            case 0:
                return FIRED;

            case 1:
                return MOVING;

            case 2:
                return ARRIVED;

            case 3:
                return EXPLODING;

            case 4:
                return EXPLODED;

            case 5:
                return INACTIVE;

            default:
                throw new IllegalArgumentException("unknown value");
        }
    }

    public boolean isActive() {
        return this == FIRED || this == MOVING;
    }
}