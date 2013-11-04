package bits.thicket;

/**
 * @author decamp
 */
public class RepulsePhaseBruteForce implements SolverPhase {

    private RepulseFunc mFunc;
    
    
    @Override
    public void init( LayoutParams params, Graph graph ) {
        RepulseEq eq = params.mRepulseEq;
        if( eq == null ) {
            eq = RepulseEq.INV_LINEAR_DIST;
        }
        
        mFunc = RepulseEq.newFunc( eq, params.mDim );
        mFunc.init( params, graph );
    }

    
    @Override
    public void step( LayoutParams params, Graph graph ) {
        for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
            for( Vert u = v.mGraphNext; u != null; u = u.mGraphNext ) {
                mFunc.appleVertForce( u, v );
                mFunc.appleVertForce( v, u );
            }
        }
    }

    
    @Override
    public void dispose( LayoutParams params, Graph graph ) {}

}
