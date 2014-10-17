//package bits.thicket;
//
//import bits.math3d.Tol;
//
///**
// * Same scheme as used in ForceAtlas2. Quality is good, but 
// * seems to require a lot of parameter tuning. 
// * 
// * @author decamp
// */
//class UpdatePhaseLocalAdaptive implements UpdatePhase {
//
//    private static final float MAX_LOCAL_SPEEDUP = 10f;
//    
//    private float mGlobalSwingTol       = 0.2f;
//    private float mMaxGlobalSpeedChange = 0.90f;
//    private float mGlobalSpeedConverged = 0.001f;
//    
//    private final float mLocalSpeedFactor      = 0.1f;
//    private final float mLocalSpeedMaxFraction = 10f;
//   
//    private float mGlobalSpeed = 0f;
//    private float mGlobalSwing = 0f;
//    private boolean mConverged = false;
//    
//    
//    
//    public UpdatePhaseLocalAdaptive() {}
//    
//    
//    public void init( LayoutParams params, Graph graph ) {
//        mGlobalSpeed          = params.mScale * params.mStepInitFactor;
//        mGlobalSpeedConverged = params.mScale * params.mConvergeTol * 5f;
//        mConverged = false;
//    }
//    
//    
//    public void step( LayoutParams params, Graph graph ) {
//        // Compute and aggragate swing and traction values for each vert.
//        float globalSwing    = 0f;
//        float globalTraction = 0f;
//        float globalWeight   = 0f;
//        
//        for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
//            float swingX  = v.mForceX - v.mPrevForceX;
//            float tractX  = 0.5f * ( v.mForceX + v.mPrevForceX );
//            v.mPrevForceX = v.mForceX;
//            
//            float swingY  = v.mForceY - v.mPrevForceY;
//            float tractY  = 0.5f * ( v.mForceY + v.mPrevForceY );
//            v.mPrevForceY = v.mForceY;
//            
//            float swing   = (float)Math.sqrt( swingX * swingX + swingY * swingY );
//            float tract   = (float)Math.sqrt( tractX * tractX + tractY * tractY );
//            
//            v.mForceX = swing;
//            v.mForceY = 0f;
//            
//            globalSwing    += v.mWeight * swing;
//            globalTraction += v.mWeight * tract;
//            globalWeight   += v.mWeight;
//        }
//        
//        float globalSpeed = mGlobalSwingTol * mGlobalSwingTol * globalTraction / ( globalSwing + Tol.FSQRT_ABS_ERR );
//        if( globalSpeed > mGlobalSpeed / mMaxGlobalSpeedChange ) {
//            globalSpeed = mGlobalSpeed / mMaxGlobalSpeedChange;
//        }
//        
//        for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
//            float localSwing = v.mForceX;
//            v.mForceX = 0f;
//            float factor = mLocalSpeedFactor * globalSpeed / ( 1.0f + globalSpeed * (float)Math.sqrt( localSwing ) );
//            float df = (float)Math.sqrt( v.mPrevForceX * v.mPrevForceX + v.mPrevForceY * v.mPrevForceY );
//            factor = Math.min( factor * df, MAX_LOCAL_SPEEDUP ) / df;
//            
//            v.mX += factor * v.mPrevForceX;
//            v.mY += factor * v.mPrevForceY;
//        }
//        
//        mGlobalSpeed = globalSpeed;
//        mGlobalSwing = globalSwing;
//        mConverged   = mGlobalSpeed < mGlobalSpeedConverged;
//    }
//    
//    
//    public void dispose( LayoutParams params, Graph graph ) {}
//    
//    
//    public boolean converged() {
//        return mConverged;
//    }
//    
//    
//    public double cost() {
//        return mGlobalSwing;
//    }
//    
//}
