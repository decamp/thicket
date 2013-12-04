package bits.thicket;

/**
 * @author decamp
 */
public interface UpdatePhase extends SolverPhase {
    public boolean converged();
    public double cost();
}
