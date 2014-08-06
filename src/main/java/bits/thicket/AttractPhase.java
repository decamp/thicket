/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

/**
 * @author decamp
 */
class AttractPhase implements SolverPhase {
    
    private AttractFunc mFunc;
    
    
    @Override
    public void init( LayoutParams params, Graph graph ) {
        AttractEq eq = params.mAttractEq;
        if( eq == null ) {
            eq = AttractEq.SQUARE_DIST;
        }
        
        mFunc = AttractEq.newFunc( eq, params.mDim );
        mFunc.init( params, graph );
    }
    
    
    @Override
    public void step( LayoutParams params, Graph graph ) {
        for( Edge e = graph.mEdges; e != null; e = e.mGraphNext ) {
            mFunc.apply( e );
        }
    }
    
    
    @Override
    public void dispose( LayoutParams params, Graph graph ) {}

}
