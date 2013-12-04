package bits.thicket;


/**
 * @author decamp
 */
class BasicUpdatePhase implements UpdatePhase {

    private static final float EPS = 0x0.000002P-60f;
    
    private float mStepStart  = 0.5f;   // 0.5f
    private float mStepUpdate = 0.9f;   // 0.9f
    private float mStepStop   = 0.005f; // 0.005f
    
    private UpdateFunc mFunc;
    
    private float mPrevEnergy  = Float.POSITIVE_INFINITY;
    private int mProgressCount = 0;
    private float mStep;
    private boolean mConverged = false;
    
    
    BasicUpdatePhase() {}
    
    
    
    public void init( LayoutParams params, Graph graph ) {
        mStepStart  = params.mScale * params.mUpdateInitialStep;
        mStepUpdate = params.mUpdateIncrementStep;
        
        if( graph.mCoarseLevel > 0 && params.mUpdateCoarseTol > 0f ) {
            mStepStop = params.mScale * params.mUpdateCoarseTol;
        } else {
            mStepStop = params.mScale * params.mUpdateTol;
        }
        
        mPrevEnergy    = Float.POSITIVE_INFINITY;
        mProgressCount = 0;
        mConverged     = false;
        
        mFunc = params.mDim == 2 ? FUNC2 : FUNC3;
        mStep = mStepStart;
    }
    
    
    public void step( LayoutParams params, Graph graph ) {
        final UpdateFunc func = mFunc;
        float step = mStep;
        float totalEnergy = 0f;
        
        for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
            totalEnergy += func.update( v, step );
        }
        
        // Update step length.
        if( totalEnergy < mPrevEnergy ) {
            if( ++mProgressCount >= 5 ) {
                mProgressCount = 0;
                mStep /= mStepUpdate;
            }
        } else {
            mProgressCount = 0;
            mStep *= mStepUpdate;
        }
        
        mPrevEnergy = totalEnergy;
        mConverged  = mStep < mStepStop;
    }
    
    
    public void dispose( LayoutParams params, Graph graph ) {}
    
    
    public boolean converged() {
        return mConverged;
    }
    
    
    public double cost() {
        return mPrevEnergy;
    }

    
    
    private interface UpdateFunc {
        public float update( Vert vert, float stepLength );
    }
    
    
    private static final UpdateFunc FUNC2 = new UpdateFunc() {
        @Override
        public float update( Vert v, float stepLength ) {
            float fx = v.mForceX;
            float fy = v.mForceY;
            float energy = fx * fx + fy * fy + EPS;
            float scale  = stepLength / (float)Math.sqrt( energy );
            v.mX += scale * fx;
            v.mY += scale * fy;
            return energy;
        }
    };

    
    private static final UpdateFunc FUNC3 = new UpdateFunc() {
        @Override
        public float update( Vert v, float stepLength ) {
            float fx = v.mForceX;
            float fy = v.mForceY;
            float fz = v.mForceZ;
            float energy = fx * fx + fy * fy + fz * fz + EPS;
            float scale  = stepLength / (float)Math.sqrt( energy );
            v.mX += scale * fx;
            v.mY += scale * fy;
            v.mZ += scale * fz;
            return energy;
        }
    };
    
    
}
