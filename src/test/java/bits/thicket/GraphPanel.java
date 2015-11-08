/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

import bits.draw3d.DrawEnv;

import bits.glui.*;
import bits.glui.event.*;
import bits.math3d.*;
import bits.microtime.ManualClock;

/**
 * @author decamp
 */
public class GraphPanel extends GPanel {

    private static final int LAYOUT_SPEED = 2;

    private final LayoutParams mParams;
    private final LayoutSolver mSolver     = new LayoutSolver();
    private       int          mGraphLevel = -1;
    private       boolean      mConverged  = false;

    private final MouseHandler mMouseHandler;
    private final GlGraphRenderer mRend;

    private final VideoExportNode mExporter;
    private final ManualClock mClock = new ManualClock();
    private long mFrame = 0;

    private float mLineAlpha;
    private Graph mGraph;
    private Mat3 mRot   = new Mat3();
    private final float[] mBounds = new float[6];
    private final float[] mOrtho  = new float[6];

    public GraphPanel( LayoutParams params, Graph graph, float lineAlpha ) {
        mLineAlpha = lineAlpha;
        mGraph = graph;

        int dim = params.mDim;

        mRend = new GlGraphRenderer( dim, lineAlpha );
        mRend.init( graph );

        mParams = params;
        mSolver.init( mParams, graph );

        mMouseHandler = new MouseHandler();
        addMouseListener( mMouseHandler );
        addMouseMotionListener( mMouseHandler );

        Mat.identity( mRot );

        mExporter = new VideoExportNode( mClock );
        addChild( mExporter );
        setLayout( new GLayout() {
            public void layoutPane( GComponent pane ) {
                mExporter.setBounds( 0, 0, width(), height() );
            }
        } );
    }


    @Override
    public void paintComponent( DrawEnv d ) {
        if( !mSolver.converged() ) {
            for( int i = 0; i < LAYOUT_SPEED; i++ ) {
                mSolver.step();
            }
        } else if( !mMouseHandler.isDragging() ) {
            Mat.rotate( mRot, (float)Math.PI / 45f, 0f, 1f, 0f, mRot );
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

        float dim = 0.6f * Math.max( mBounds[3] - mBounds[0], Math.max( mBounds[4] - mBounds[1], mBounds[5] - mBounds[2] ) );
        float cx  = 0.5f * ( mBounds[0] + mBounds[3] );
        float cy  = 0.5f * ( mBounds[1] + mBounds[4] );
        float cz  = 0.5f * ( mBounds[2] + mBounds[5] );

        mOrtho[0] = cx - dim;
        mOrtho[1] = cy - dim;
        mOrtho[2] = cz - dim;
        mOrtho[3] = cx + dim;
        mOrtho[4] = cy + dim;
        mOrtho[5] = cz + dim;

        d.mView.push();
        d.mView.identity();
        d.mView.translate( cx, cy, cz );
        d.mView.mult( mRot );
        d.mView.translate( -cx, -cy, -cz );

        d.mProj.push();
        d.mProj.setOrtho( mOrtho[0], mOrtho[3], mOrtho[1], mOrtho[4], -1000f, 1000f ); //-mOrtho[2], -mOrtho[5] );

        mRend.render( d );

        d.mProj.pop();
        d.mView.pop();

        //mExporter.pushDraw( d );
        //mExporter.popDraw( d );
        mClock.micros( mFrame++ * 2997 / 100 );
    }


    public VideoExportNode exporter() {
        return mExporter;
    }


    private final class MouseHandler extends GMouseAdapter {

        boolean mDragging = false;
        private int mX = -1;
        private int mY = -1;

        public void mousePressed( GMouseEvent e ) {
            mDragging = true;
            mX = e.getX();
            mY = e.getY();
        }

        public void mouseExited( GMouseEvent e ) {
            mDragging = false;
        }

        public void mouseEntered( GMouseEvent e ) {
            mX = e.getX();
            mY = e.getY();
        }

        public void mouseRelease( GMouseEvent e ) {
            mDragging = false;
        }

        public void mouseDragged( GMouseEvent e ) {
            if( !mDragging ) {
                return;
            }

            int dx = e.getX() - mX;
            int dy = e.getY() - mY;
            if( dx == 0 && dy == 0 ) {
                return;
            }

            float dist = (float)Math.sqrt( dx * dx + dy * dy ) / height();

            Mat.preRotate( dist * (float)( Math.PI * 2.0 ), -dy, dx, 0, mRot, mRot );
            Mat.normalizeRotationMatrix( mRot );

            mX = e.getX();
            mY = e.getY();
        }

        public boolean isDragging() {
            return mDragging;
        }
    }

}
