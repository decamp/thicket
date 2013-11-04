package bits.thicket;

import javax.media.opengl.GL;
import static javax.media.opengl.GL.*;

import cogmac.glui.*;
import cogmac.glui.event.*;
import cogmac.math3d.*;


/**
 * @author decamp
 */
public class GraphPanel extends GPanel {
    
    private static final int LAYOUT_SPEED = 1;
    
    
    private float mLineAlpha;
    private Graph mGraph;
    private final float[] mBounds = new float[6];
    private final float[] mOrtho  = new float[6];
    
    private final LayoutParams mParams;
    private final LayoutSolver mSolver = new LayoutSolver();
    private int mGraphLevel = -1;
    private boolean mConverged = false;
    
    private final GlGraphRenderer mRend;
    private float[] mRot = new float[16];
    private float[][] mWork = new float[2][16];
    
    
    public GraphPanel( LayoutParams params, Graph graph, float lineAlpha ) {
        mLineAlpha = lineAlpha;
        mGraph     = graph;
        
        int dim = params.mDim;
        
        mRend = new GlGraphRenderer( dim, lineAlpha );
        mRend.init( graph );
        
        mParams = params;
        mSolver.init( mParams, graph ); 
        
        MouseHandler mh = new MouseHandler();
        addMouseListener( mh );
        addMouseMotionListener( mh );
        
        Mat4.identity( mRot );
    }
    
    
    @Override
    public void paintComponent( GGraphics g ) {
        GL gl = g.gl();
        if( !mSolver.converged() ) {
            for( int i = 0; i < LAYOUT_SPEED; i++ ) {
                mSolver.step();
            }
        }
        
        Graph graph = mSolver.currentGraph();
        
        if( graph.mCoarseLevel != mGraphLevel ) {
            mGraphLevel = graph.mCoarseLevel;
            mRend.init( graph );
            mBounds[0] = 0f;
        }
        
        if( mParams.mDim >= 3 ) {
            Graphs.computeBounds3( graph.mVerts, mBounds);
        } else {
            Graphs.computeBounds2( graph.mVerts, mBounds );
            mBounds[4] = mBounds[3];
            mBounds[3] = mBounds[2];
            mBounds[2] = 0;
            mBounds[5] = 0;
        }
        
        float dim = 0.8f * Math.max( mBounds[3] - mBounds[0], Math.max( mBounds[4] - mBounds[1], mBounds[5] - mBounds[2] ) );
        float cx  = 0.5f * ( mBounds[0] + mBounds[3] );
        float cy  = 0.5f * ( mBounds[1] + mBounds[4] );
        float cz  = 0.5f * ( mBounds[2] + mBounds[5] );
        
        mOrtho[0] = cx - dim;
        mOrtho[1] = cy - dim;
        mOrtho[2] = cz - dim;
        mOrtho[3] = cx + dim;
        mOrtho[4] = cy + dim;
        mOrtho[5] = cz + dim;
        
        gl.glMatrixMode( GL_MODELVIEW );
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslated( cx, cy, cz );
        gl.glMultMatrixf( mRot, 0 );
        gl.glTranslated( -cx, -cy, -cz );
        
        gl.glMatrixMode( GL_PROJECTION );
        gl.glPushMatrix();
        gl.glLoadIdentity();
        
        gl.glOrtho( mOrtho[0], mOrtho[3], mOrtho[1], mOrtho[4], -1000.0, 1000.0 ); //-mOrtho[2], -mOrtho[5] );
        
        mRend.render( g );
        
        gl.glMatrixMode( GL_PROJECTION );
        gl.glPopMatrix();
        
        gl.glMatrixMode( GL_MODELVIEW );
        gl.glPopMatrix();
    }
    
    
    private final class MouseHandler extends GMouseAdapter {

        private boolean mDraggable = false;
        private int mX = -1;
        private int mY = -1;
        
        public void mousePressed( GMouseEvent e ) {
            mX = e.getX();
            mY = e.getY();
        }
        
        public void mouseExited( GMouseEvent e ) {
            mDraggable = false;
        }
        
        public void mouseEntered( GMouseEvent e ) {
            mDraggable = true;
            mX = e.getX();
            mY = e.getY();
        }
                
        public void mouseDragged( GMouseEvent e ) {
            if( !mDraggable ) {
                return;
            }

            int dx = e.getX() - mX;
            int dy = e.getY() - mY;
            if( dx == 0 && dy == 0 ) {
                return;
            }
            
            float dist = (float)Math.sqrt( dx * dx + dy * dy ) / height();
            Mat4.rotation( dist * (float)( Math.PI * 2.0 ), -dy, dx, 0, mWork[0] );
            Mat4.mult( mWork[0], mRot, mWork[1] );
            Mat4.normalizeRotationMatrix( mWork[1] );
            Arr.put( mWork[1], mRot );
            
            mX = e.getX();
            mY = e.getY();
        }
        
    }
    
}
