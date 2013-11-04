package bits.thicket;

/**
 * @author decamp
 */
public interface SolverPhase {
    public void init( LayoutParams params, Graph graph );
    public void step( LayoutParams params, Graph graph );
    public void dispose( LayoutParams params, Graph graph );
}
