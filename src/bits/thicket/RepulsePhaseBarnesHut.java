package bits.thicket;

/**
 * @author decamp
 */
public class RepulsePhaseBarnesHut implements SolverPhase {

    private static final float FORCE_COST_FACTOR = 4.0f;
    
    private int mDim;
    private RepulseFunc mFunc;
    
    private Quadtree mTree;
    private float mApproxThreshSq;
    
    private int mDepth;
    
    private boolean mDepthTune;
    private int mDepthTunePos; /** 0 = init, 1 = increased once, 2 = increased twice... **/
    private int mDepthBest;    /** Best depth tested. **/
    private float mCostBest;   /** Cost of best depth tested. **/
    private int mCostTraversal;
    private int mCostForceCalc;
    
    private float[] mWork = new float[6];
    
    
    @Override
    public void init( LayoutParams params, Graph graph ) {
        mDim = params.mDim;
        mApproxThreshSq = params.mRepulseApproxThresh * params.mRepulseApproxThresh;
        
        mDepth = params.mRepulseApproxMaxTreeDepth;
        if( mDepth <= 0 ) {
            mDepth = 9;
            mDepthTune = true;
            mDepthTunePos = 0;
        } else {
            mDepthTune = false;
        }
        
        RepulseEq eq = params.mRepulseEq;
        if( eq == null ) {
            eq = RepulseEq.INV_LINEAR_DIST;
        }
        
        mFunc = RepulseEq.newFunc( eq, params.mDim );
        mFunc.init( params, graph );

        switch( params.mDim ) {
        case 2:
            mTree = new Quadtree2();
            break;
        case 3:
            mTree = new Quadtree3();
            break;
        default:
            throw new IllegalArgumentException( "LayoutParams.mDim = " + params.mDim );
        }
    }
    
    
    @Override
    public void step( LayoutParams params, Graph graph ) {
        step( params, graph, null );
    }
    
    
    public void step( LayoutParams params, Graph graph, float[] optGraphBounds ) {
        if( optGraphBounds == null ) {
            mTree.rebuild( graph.mVerts, mDepth, null, 0f );
            
        } else if( mDim == 2 ) {
            float[] cent = mWork;
            cent[0] = ( optGraphBounds[2] + optGraphBounds[0] ) * 0.5f;
            cent[1] = ( optGraphBounds[3] + optGraphBounds[1] ) * 0.5f;
            float dx = optGraphBounds[2] - optGraphBounds[0];
            float dy = optGraphBounds[3] - optGraphBounds[1];   
            float size = dx >= dy ? dx : dy;
            mTree.rebuild( graph.mVerts, mDepth, cent, size );
            
        } else {
            float[] cent = mWork;
            cent[0] = ( optGraphBounds[3] + optGraphBounds[0] ) * 0.5f;
            cent[1] = ( optGraphBounds[4] + optGraphBounds[1] ) * 0.5f;
            cent[2] = ( optGraphBounds[5] + optGraphBounds[2] ) * 0.5f;
            float dx = optGraphBounds[3] - optGraphBounds[0];
            float dy = optGraphBounds[4] - optGraphBounds[1];   
            float dz = optGraphBounds[5] - optGraphBounds[2]; 
            float size = dx >= dy ? dx : dy;
            if( dz > size ) {
                size = dz;
            }
            mTree.rebuild( graph.mVerts, mDepth, cent, size );
        }
        
        final QuadtreeCell root = mTree.root();
        
        if( !mDepthTune ) {
            if( mDim == 2 ) {
                for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
                    apply2_r( root, v );
                }
            } else {
                for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
                    apply3_r( root, v );
                }
            }
            return;
        }
        
        mCostTraversal = 0;
        mCostForceCalc = 0;
        
        if( mDim == 2 ) {
            for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
                applyWithTuning2_r( root, v );
            }
        } else {
            for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
                applyWithTuning3_r( root, v );
            }
        }
        
        float cost = mCostTraversal + FORCE_COST_FACTOR * mCostForceCalc;

        if( mDepthTunePos == 0 ) {
            // First iteration.
            mCostBest  = cost;
            mDepthBest = mDepth--;
            mDepthTunePos--;
        } else if( cost < mCostBest ) {
            // If cost decreased, keep searching in same direction.
            mCostBest  = cost;
            mDepthBest = mDepth;

            if( mDepthTunePos > 0 ) {
                mDepth++;
                mDepthTunePos++;
            } else if( mDepth > 1 ) {
                mDepth--;
                mDepthTunePos--;
            } else {
                // Nowhere to go. We're done. 
                // This is a weird thing to happen, and probably means that most of the points are 
                // at about the same position. 
                mDepthTune = false;
            }
        } else {
            // Cost has decreased. If this occurred on our first attemp to decrease
            // depth, switch directions. Otherwise, the search is complete.
            if( mDepthTunePos == -1 ) {
                mDepth += 2;
                mDepthTunePos = 1;
            } else {
                mDepth = mDepthBest;
                mDepthTune = false;
            }
        }

        //if( !mDepthTune && graph.mCoarseLevel == 0 ) {
        //    System.out.println( "#DEPTH: " + mDepthBest + " " + mDepth + "\t" + mCostBest );
        //}
    }

    
    @Override
    public void dispose( LayoutParams params, Graph graph ) {}
    
    
    
    
    private void apply2_r( QuadtreeCell cell, Vert v ) {
        float dx = cell.mX - v.mX;
        float dy = cell.mY - v.mY;
        float dist = dx * dx + dy * dy;
        
        // Check if cell is far away.
        if( 4.0f * cell.mHalfSize * cell.mHalfSize < mApproxThreshSq * dist ) {
            mFunc.applyCellForce( cell, v );
            return;
        }

        // Check if cell is leaf.
        if( cell.mVerts != null ) {
            for( Vert u = cell.mVerts; u != null; u = u.mTempNext ) {
                if( u != v ) {
                    mFunc.appleVertForce( u, v );
                }
            }
            return;
        }

        for( QuadtreeCell child = cell.mChildList; child != null; child = child.mNextSibling ) {
            apply2_r( child, v );
        }
    }
    
    
    private void apply3_r( QuadtreeCell cell, Vert v ) {
        float dx = cell.mX - v.mX;
        float dy = cell.mY - v.mY;
        float dz = cell.mZ - v.mZ;
        float dist = dx * dx + dy * dy + dz * dz;
        
        // Check if cell is far away.
        if( 4.0f * cell.mHalfSize * cell.mHalfSize < mApproxThreshSq * dist ) {
            mFunc.applyCellForce( cell, v );
            return;
        }

        // Check if cell is leaf.
        if( cell.mVerts != null ) {
            for( Vert u = cell.mVerts; u != null; u = u.mTempNext ) {
                if( u != v ) {
                    mFunc.appleVertForce( u, v );
                }
            }
            return;
        }

        for( QuadtreeCell child = cell.mChildList; child != null; child = child.mNextSibling ) {
            apply3_r( child, v );
        }
    }
    
    
    private void applyWithTuning2_r( QuadtreeCell cell, Vert v ) {
        mCostTraversal++;
        
        float dx = cell.mX - v.mX;
        float dy = cell.mY - v.mY;
        float dist = dx * dx + dy * dy;
        
        // Check if cell is far away.
        if( 4.0f * cell.mHalfSize * cell.mHalfSize < mApproxThreshSq * dist ) {
            mCostForceCalc++;
            mFunc.applyCellForce( cell, v );
            return;
        }

        // Check if cell is leaf.
        if( cell.mVerts != null ) {
            for( Vert u = cell.mVerts; u != null; u = u.mTempNext ) {
                if( u != v ) {
                    mCostForceCalc++;
                    mFunc.appleVertForce( u, v );
                }
            }
            return;
        }

        for( QuadtreeCell child = cell.mChildList; child != null; child = child.mNextSibling ) {
            applyWithTuning2_r( child, v );
        }
    }
    
    
    private void applyWithTuning3_r( QuadtreeCell cell, Vert v ) {
        mCostTraversal++;
        
        float dx = cell.mX - v.mX;
        float dy = cell.mY - v.mY;
        float dz = cell.mZ - v.mZ;
        float dist = dx * dx + dy * dy + dz * dz;
        
        // Check if cell is far away.
        if( 4.0f * cell.mHalfSize * cell.mHalfSize < mApproxThreshSq * dist ) {
            mCostForceCalc++;
            mFunc.applyCellForce( cell, v );
            return;
        }

        // Check if cell is leaf.
        if( cell.mVerts != null ) {
            for( Vert u = cell.mVerts; u != null; u = u.mTempNext ) {
                if( u != v ) {
                    mCostForceCalc++;
                    mFunc.appleVertForce( u, v );
                }
            }
            return;
        }

        for( QuadtreeCell child = cell.mChildList; child != null; child = child.mNextSibling ) {
            applyWithTuning3_r( child, v );
        }
        
    }

    
}
