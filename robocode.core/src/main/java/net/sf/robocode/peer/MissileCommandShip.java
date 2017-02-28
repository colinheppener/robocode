package net.sf.robocode.peer;

/**
 * Exactly the same as MissileCommand. It just has the added bonus that you can also retrieve the component's index from this one.
 * This is needed to know which weapon is even being fired
 * @author Thales B.V./ Thomas Hakkers
 */
public class MissileCommandShip extends MissileCommand{

    private static final long serialVersionUID = -6420971602929346456L;

    private int indexComponent;
    public MissileCommandShip(double power, boolean fireAssistValid, double fireAssistAngle, int missileId, int indexComponent) {
        super(power, fireAssistValid, fireAssistAngle, missileId);
        this.indexComponent = indexComponent;
    }

    public int getIndexComponent(){
        return indexComponent;
    }

}
