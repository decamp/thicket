/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

import java.io.IOException;
import java.nio.*;

import bits.draw3d.*;
import bits.util.Resources;

import static com.jogamp.opengl.GL2ES3.*;


/**
 * @author decamp
 */
public class GlGraphRenderer {

    private final Bo  mVbo;
    private final Bo  mIbo;
    private final Vao mVao;

    private final Program mProg;
    private int mColorUni;

    private final int mDim;

    private float mLineAlpha;
    private Graph   mGraph     = null;
    private boolean mInit      = true;
    private int     mVertCount = 0;
    private int     mEdgeCount = 0;


    public GlGraphRenderer( int dim, float lineAlpha ) {
        mDim = dim;
        mLineAlpha = lineAlpha;

        mVbo = Bo.createArrayBuffer( GL_STREAM_DRAW );
        mIbo = Bo.createElementBuffer( GL_DYNAMIC_DRAW );
        mVao = new Vao( mVbo, mIbo );
        mVao.addAttribute( 0, dim, GL_FLOAT, false );
        mVao.packFormat();

        try {
            mProg = new AutoloadProgram();
            mProg.addShader(
                    new Shader(
                            GL_VERTEX_SHADER,
                            Resources.readString( "bits/thicket/Pos.vert" )
                    )
            );
            mProg.addShader(
                    new Shader(
                            GL_FRAGMENT_SHADER,
                            Resources.readString( "bits/thicket/PosuniColor.frag" )
                    )
            );

        } catch( IOException e ) {
            throw new RuntimeException( "Unable to load shaders", e );
        }
    }


    public void init( Graph graph ) {
        mGraph = graph;
        mInit  = true;
    }
    
        
    public void render( DrawEnv d ) {
        if( mGraph == null ) {
            return;
        }
        
        if( mInit ) {
            mInit = false;
            int vertCount = 0;
            int edgeCount = mGraph.mEdgeNo;
            
            for( Vert v = mGraph.mVerts; v != null; v = v.mGraphNext ) {
                v.mTempDist = vertCount++;
            }
            
            if( vertCount != mVertCount ) {
                mVertCount = vertCount;
                mVbo.alloc( vertCount * 4 * mDim );
            }
            if( edgeCount != mEdgeCount ) {
                mEdgeCount = edgeCount;
                mIbo.alloc( edgeCount * 4 * mDim );
            }
            
            mIbo.pushDraw( d );
            ByteBuffer buf = mIbo.map( d, GL_WRITE_ONLY );
            for( Edge e = mGraph.mEdges; e != null; e = e.mGraphNext ) {
                buf.putInt( (int)( e.mA.mTempDist + 0.5f ) );
                buf.putInt( (int)( e.mB.mTempDist + 0.5f ) );
            }
            
            mIbo.unmap( d );
            mIbo.popDraw( d );
            //System.out.println( "Loaded Verts: " + mVertCount + "  Edges: " + mEdgeCount );

            mProg.bind( d );
            mColorUni = d.mGl.glGetUniformLocation( mProg.id(), "COLOR" );
            mProg.unbind( d );
        }

        // Write points to draw buffer.
        mVbo.bind( d );
        ByteBuffer buf = mVbo.map( d, GL_WRITE_ONLY );
        
        switch( mDim ) {
        case 2:
            for( Vert v = mGraph.mVerts; v != null; v = v.mGraphNext ) {
                buf.putFloat( v.mX );
                buf.putFloat( v.mY );
            }
            break;
        case 3:
        default:
            for( Vert v = mGraph.mVerts; v != null; v = v.mGraphNext ) {
                buf.putFloat( v.mX );
                buf.putFloat( v.mY );
                buf.putFloat( v.mZ );
            }
            break;
        }

        mVbo.unmap( d );
        mVbo.unbind( d );

        // Execute line commands
        float lineAlpha = mLineAlpha * (float)( 1.0 / Math.max( 1.0, Math.min( 200.0, Math.log( mEdgeCount ) / Math.log( 3.0 ) ) ) ); 
        d.mLineWidth.apply( 1f );

        mVao.bind( d );
        mProg.bind( d );

        d.mGl.glUniform4f( mColorUni, 1f, 1f, 1f, lineAlpha );
        d.mGl.glDrawElements( GL_LINES, mEdgeCount * 2, GL_UNSIGNED_INT, 0 );

        // Execute node geometry.
        d.mGl.glPointSize( 4f );
        d.mGl.glUniform4f( mColorUni, 0.8f, 0f, 0f, 0.7f );
        d.mGl.glDrawArrays( GL_POINTS, 0, mVertCount );

        mVao.unbind( d );

    }
    
}
