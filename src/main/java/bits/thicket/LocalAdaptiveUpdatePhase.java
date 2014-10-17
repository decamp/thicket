/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;


/**
 * More similar to the ForceAtlas2 update phase in which the speed of each node 
 * is determined separately.
 * 
 * @author decamp
 */
public class LocalAdaptiveUpdatePhase implements UpdatePhase {

    private static final float EPS = 0x0.000002P-60f;

    private static final float LOCAL_SPEED_FACTOR = 0.5f;
    private static final float LOCAL_SPEED_MAX    = Float.POSITIVE_INFINITY;
    private static final float GLOBAL_SPEED_TOL   = 1f;
    
    //private float mStepStart  = 0.5f;   // 0.5f
    //private float mStepUpdate = 0.9f;   // 0.9f
    //private float mStepStop   = 0.005f; // 0.005f
    
    private float mSpeedStart = 0.5f;
    private float mSpeedStop  = 0.005f;
    
    private int mProgressCount = 0;
    private float mSpeedGlobal;
    private boolean mConverged = false;
    
    
    public LocalAdaptiveUpdatePhase() {}
    
    
    
    public void init( LayoutParams params, Graph graph ) {
        mSpeedStart = params.mScale * params.mUpdateInitialStep;
        
        if( graph.mCoarseLevel > 0 && params.mUpdateCoarseTol > 0f ) {
            mSpeedStop = params.mScale * params.mUpdateCoarseTol;
        } else {
            mSpeedStop = params.mScale * params.mUpdateTol;
        }
        
        mConverged   = false;
        mSpeedGlobal = mSpeedStart;
    }
    
    
    public void step( LayoutParams params, Graph graph ) {
        float speedGlobal = mSpeedGlobal;
        float sumSwing    = 0f;
        float sumTraction = 0f;
        
        for( Vert v = graph.mVerts; v != null; v = v.mGraphNext ) {
            float fx = v.mForceX;
            float fy = v.mForceY;
            float fz = v.mForceZ;
            float dx = fx - v.mPrevForceX;
            float dy = fy - v.mPrevForceY;
            float dz = fz - v.mPrevForceZ;
            float tx = fx + v.mPrevForceX;
            float ty = fy + v.mPrevForceY;
            float tz = fz + v.mPrevForceZ;
            
            float swing = (float)Math.sqrt( dx * dx + dy * dy + dz * dz );
            float force = (float)Math.sqrt( fx * fx + fy * fy + fz * fz );
            float tract = (float)Math.sqrt( tx * tx + ty * ty + tz * tz );
            float speed = ( LOCAL_SPEED_FACTOR * speedGlobal ) / ( 1f + speedGlobal * (float)Math.sqrt( swing ) );
            float max   = LOCAL_SPEED_MAX / ( force + EPS );
            if( speed > max ) {
                speed = max;
            }
            
            speed = speed / ( force + EPS );
            
            v.mX += speed * fx;
            v.mY += speed * fy;
            v.mZ += speed * fz;
            v.mPrevForceX = fx;
            v.mPrevForceY = fy;
            v.mPrevForceZ = fz;
            
            sumSwing    += v.mWeight * swing;
            sumTraction += v.mWeight * tract;
        }
        
        float newSpeedGlobal = GLOBAL_SPEED_TOL * sumTraction * 0.5f / ( sumSwing + EPS );
        if( newSpeedGlobal > speedGlobal * 1.5f ) {
            newSpeedGlobal = speedGlobal * 1.5f;
        }
        
        mSpeedGlobal = newSpeedGlobal;
        mConverged  = mSpeedGlobal < mSpeedStop;
        
        //System.out.println( mSpeedGlobal );
    }
    
    
    public void dispose( LayoutParams params, Graph graph ) {}
    
    
    public boolean converged() {
        return mConverged;
    }
    
    
    public double cost() {
        return mSpeedGlobal;
        //return 0.0;
    }

    
}
