package bits.thicket;


/**
 * @author decamp
 */
public class LayoutSolver {
    
    private static final int MODE_UNINITIALIZED = 0;
    private static final int MODE_COARSEN       = 1;
    private static final int MODE_LAYOUT_START  = 2;
    private static final int MODE_LAYOUT        = 3;
    private static final int MODE_LAYOUT_FINISH = 4;
    private static final int MODE_CONVERGED     = 5;
    
    private int mMode = MODE_UNINITIALIZED;
    
    private LayoutParams mParams;
    private Graph mGraph;
    private boolean mMultilevel = true;
    
    private GravityPhase mGravityPhase = new GravityPhase();
    private SolverPhase  mAttractPhase;
    private SolverPhase  mRepulsePhase;
    private UpdatePhase  mUpdatePhase;
    
    private final float[] mWork = new float[6];
        
    
    public void init( LayoutParams params, Graph graph ) {
        if( params.mDim != 2 && params.mDim != 3 ) {
            throw new IllegalArgumentException( "LayoutParams.mDim = " + params.mDim );
        }
        
        mAttractPhase = getAttractPhase( params );
        mRepulsePhase = getRepulsePhase( params );
        mUpdatePhase  = getUpdatePhase(  params );
        
        
        mParams     = params;
        mGraph      = graph;
        mMultilevel = params.mMultilevel;
        
        if( mMultilevel ) {
            mMode = MODE_COARSEN;
        } else {
            mMode = MODE_LAYOUT_START;
        }
        
        // Initialize vert weights.
        for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
            v.mWeight = params.mVertWeightModel.atomicWeight( v );
        }
    }
    
    
    public void step() {
        switch( mMode ) {
        case MODE_COARSEN:
            if( mGraph.mEdgeNo >= 2 ) {
                mGraph = Graphs.coarsen( mParams, mGraph );
                return;
            } else {
                mMode = MODE_LAYOUT_START;
                // Fallthrough
            }
            
        case MODE_LAYOUT_START:
            initPhases();
            mMode = MODE_LAYOUT;
            return;

        case MODE_LAYOUT:
            stepLayout();
            return;
        
        case MODE_LAYOUT_FINISH:
            if( mGraph.mFinerGraph != null ) {
                mGraph = Graphs.uncoarsen( mParams, mGraph );
                mMode = MODE_LAYOUT_START;
                return;
            }
            
            mMode = MODE_CONVERGED;
            return;
            
        case MODE_CONVERGED:
            return;
        
        case MODE_UNINITIALIZED:
        default:
            throw new IllegalStateException( "Uninitialized." );
        }
    }

    
    public void dispose( LayoutParams params, Graph graph ) {
        if( mMode == MODE_UNINITIALIZED ) {
            return;
        }
        
        mMode = MODE_UNINITIALIZED;
        mAttractPhase.dispose( params, graph );
        mRepulsePhase.dispose( params, graph );
        mUpdatePhase.dispose( params, graph );
    }

    
    public boolean converged() {
        return mMode == MODE_CONVERGED;
    }

    
    public Graph currentGraph() {
        return mGraph;
    }
    
    
    public double currentCost() {
        return mUpdatePhase.cost();
    }
    
    
    
    private void initPhases() {
        mGravityPhase.init( mParams, mGraph );
        mAttractPhase.init( mParams, mGraph );
        mRepulsePhase.init( mParams, mGraph );
        mUpdatePhase.init(  mParams, mGraph );
    }
    
    
    private void stepLayout() {
        mGravityPhase.step( mParams, mGraph );
        mAttractPhase.step( mParams, mGraph );
        
        if( mRepulsePhase instanceof RepulsePhaseBarnesHut ) {
            mGravityPhase.graphBounds( mWork );
            //System.out.println( "BOUNDS: " + Box3.format( mWork ) );
            ((RepulsePhaseBarnesHut)mRepulsePhase).step( mParams, mGraph, mWork );
        } else {
            mRepulsePhase.step( mParams, mGraph );
        }
            
        mUpdatePhase.step( mParams, mGraph );
        
        if( mUpdatePhase.converged() ) {
            if( mGraph.mFinerGraph == null || !mParams.mMultilevel ) {
                mMode = MODE_CONVERGED;
            } else {
                mMode = MODE_LAYOUT_FINISH;
            }
        }
    }
    
    
    private static SolverPhase getAttractPhase( LayoutParams params ) {
        if( params.mAttractPhase != null ) {
            return params.mAttractPhase;
        } else {
            return new AttractPhase(); 
        }
    }
    
    
    private static SolverPhase getRepulsePhase( LayoutParams params ) {
        if( params.mRepulsePhase != null ) {
            return params.mRepulsePhase;
        } 
        
        if( params.mRepulseApprox ) {
            return new RepulsePhaseBarnesHut();
        }
        
        return new RepulsePhaseBruteForce();
    }

    
    private static UpdatePhase getUpdatePhase( LayoutParams params ) {
        if( params.mUpdatePhase != null ) {
            return params.mUpdatePhase;
        }
        
        return new BasicUpdatePhase();
    }
    
}
