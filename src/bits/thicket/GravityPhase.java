package bits.thicket;

import cogmac.math3d.Tol;


/**
 * @author decamp
 */
class GravityPhase implements SolverPhase {
    
    private float mX;
    private float mY;
    private float mZ;
    private float mForce;
    
    private Func mFunc = ON2;
    private int mDim;
    private float[] mBounds = new float[6]; 
    
    
    GravityPhase() {}
    

    @Override
    public void init( LayoutParams params, Graph graph ) {
        mDim = params.mDim;
        mX = params.mGravityCenter[0];
        mY = params.mGravityCenter[1];
        mZ = params.mGravityCenter[2];
        mForce = params.mGravityForce;
        
        if( mForce <= 0f ) {
            mFunc = mDim == 2 ? OFF2 : OFF3;
        } else {
            mFunc = mDim == 2 ? ON2 : ON3;
        }
    }

    
    @Override
    public void step( LayoutParams params, Graph graph ) {
        mFunc.apply( mX, mY, mZ, mForce, graph.mVerts, mBounds );
    }

    
    @Override
    public void dispose( LayoutParams params, Graph graph ) {}

    
    public void graphBounds( float[] outBounds ) {
        System.arraycopy( mBounds, 0, outBounds, 0, mDim * 2 );
    }
    
    
   
    private interface Func {
        public void apply( float gx, float gy, float gz, float gf, Vert verts, float[] outBounds );
    }
    
    
    private static final Func ON2 = new Func() {
        public void apply( float gx, float gy, float gz, float gf, Vert verts, float[] outBounds ) {
            if( verts == null ) {
                outBounds[0] = gx - 0.5f;
                outBounds[1] = gy - 0.5f;
                outBounds[2] = gx + 0.5f;
                outBounds[3] = gy + 0.5f;
                return;
            }
            
            float x0, x1;
            float y0, y1;
            x0 = x1 = verts.mX;
            y0 = y1 = verts.mY;
            
            do {
                if(      verts.mX < x0 ) x0 = verts.mX;
                else if( verts.mX > x1 ) x1 = verts.mX;
                if(      verts.mY < y0 ) y0 = verts.mY;
                else if( verts.mY > y1 ) y1 = verts.mY;
                
                float dx = gx - verts.mX;
                float dy = gy - verts.mY;
                float scale = gf * verts.mWeight / ( (float)Math.sqrt( dx * dx + dy * dy ) + Tol.FSQRT_ABS_ERR );
                
                verts.mForceX = scale * dx;
                verts.mForceY = scale * dy;
                verts = verts.mGraphNext;
            } while( verts != null );
            
            outBounds[0] = x0;
            outBounds[1] = y0;
            outBounds[2] = x1;
            outBounds[3] = y1;
        }
    };
    

    private static final Func OFF2 = new Func() {
        public void apply( float gx, float gy, float gz, float gf, Vert verts, float[] outBounds ) {
            if( verts == null ) {
                outBounds[0] = gx - 0.5f;
                outBounds[1] = gy - 0.5f;
                outBounds[2] = gx + 0.5f;
                outBounds[3] = gy + 0.5f;
                return;
            }
            
            float x0, x1;
            float y0, y1;
            x0 = x1 = verts.mX;
            y0 = y1 = verts.mY;
            
            do {
                if(      verts.mX < x0 ) x0 = verts.mX;
                else if( verts.mX > x1 ) x1 = verts.mX;
                if(      verts.mY < y0 ) y0 = verts.mY;
                else if( verts.mY > y1 ) y1 = verts.mY;
                
                verts.mForceX = 0f;
                verts.mForceY = 0f;
                verts = verts.mGraphNext;
            } while( verts != null );
            
            outBounds[0] = x0;
            outBounds[1] = y0;
            outBounds[2] = x1;
            outBounds[3] = y1;
        }
    };

    
    private static final Func ON3 = new Func() {
        public void apply( float gx, float gy, float gz, float gf, Vert verts, float[] outBounds ) {
            if( verts == null ) {
                outBounds[0] = gx - 0.5f;
                outBounds[1] = gy - 0.5f;
                outBounds[2] = gz - 0.5f;
                outBounds[3] = gx + 0.5f;
                outBounds[4] = gy + 0.5f;
                outBounds[5] = gz + 0.5f;
                return;
            }
            
            float x0, x1;
            float y0, y1;
            float z0, z1;
            x0 = x1 = verts.mX;
            y0 = y1 = verts.mY;
            z0 = z1 = verts.mZ;
            
            do {
                if(      verts.mX < x0 ) x0 = verts.mX;
                else if( verts.mX > x1 ) x1 = verts.mX;
                if(      verts.mY < y0 ) y0 = verts.mY;
                else if( verts.mY > y1 ) y1 = verts.mY;
                if(      verts.mZ < z0 ) z0 = verts.mZ;
                else if( verts.mZ > z1 ) z1 = verts.mZ;

                float dx = gx - verts.mX;
                float dy = gy - verts.mY;
                float dz = gz - verts.mZ;
                float scale = gf * verts.mWeight / ( (float)Math.sqrt( dx * dx + dy * dy + dz * dz ) + Tol.FSQRT_ABS_ERR );
                
                verts.mForceX = scale * dx;
                verts.mForceY = scale * dy;
                verts.mForceZ = scale * dz;
                verts = verts.mGraphNext;
            } while( verts != null );
            
            outBounds[0] = x0;
            outBounds[1] = y0;
            outBounds[2] = z0;
            outBounds[3] = x1;
            outBounds[4] = y1;
            outBounds[5] = z1;
        }
    };

    
    private static final Func OFF3 = new Func() {
        public void apply( float gx, float gy, float gz, float gf, Vert verts, float[] outBounds ) {
            if( verts == null ) {
                outBounds[0] = gx - 0.5f;
                outBounds[1] = gy - 0.5f;
                outBounds[2] = gz - 0.5f;
                outBounds[3] = gx + 0.5f;
                outBounds[4] = gy + 0.5f;
                outBounds[5] = gz + 0.5f;
                return;
            }
            
            float x0, x1;
            float y0, y1;
            float z0, z1;
            x0 = x1 = verts.mX;
            y0 = y1 = verts.mY;
            z0 = z1 = verts.mZ;
            
            do {
                if(      verts.mX < x0 ) x0 = verts.mX;
                else if( verts.mX > x1 ) x1 = verts.mX;
                if(      verts.mY < y0 ) y0 = verts.mY;
                else if( verts.mY > y1 ) y1 = verts.mY;
                if(      verts.mZ < z0 ) z0 = verts.mZ;
                else if( verts.mZ > z1 ) z1 = verts.mZ;

                verts.mForceX = 0f;
                verts.mForceY = 0f;
                verts.mForceZ = 0f;
                verts = verts.mGraphNext;
            } while( verts != null );
            
            outBounds[0] = x0;
            outBounds[1] = y0;
            outBounds[2] = z0;
            outBounds[3] = x1;
            outBounds[4] = y1;
            outBounds[5] = z1;
        }
    };
    
}
