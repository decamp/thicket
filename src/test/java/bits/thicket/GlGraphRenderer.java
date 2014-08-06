/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

import java.nio.*;
import javax.media.opengl.GL;

import bits.draw3d.nodes.*;
import bits.glui.*;
import static javax.media.opengl.GL.*;



/**
 * @author decamp
 */
public class GlGraphRenderer {
    
    private final BufferNode mVbo;
    private final BufferNode mIbo;
    private final DrawBufferParams mVertParams;
    private final DrawBufferParams mEdgeParams;
    private ByteBuffer mIndBuf;
    
    private final int mDim;
    
    private float mLineAlpha;
    private Graph mGraph = null;
    private boolean mInit = true;
    private int mVertCount = 0;
    private int mEdgeCount = 0;
    
    
    public GlGraphRenderer( int dim, float lineAlpha ) {
        mDim = dim;
        mLineAlpha = lineAlpha;
        
        mVbo = BufferNode.newVertexInstance( GL_STREAM_DRAW );
        mIbo = BufferNode.newElementInstance( GL_DYNAMIC_DRAW );
        mVertParams = DrawBufferParams.newInstance();
        mEdgeParams = DrawBufferParams.newInstance();
        
        mVertParams.enableVertexPointer( dim, GL_FLOAT, 4*dim, 0 );
        mEdgeParams.enableIndices( GL_UNSIGNED_INT );
        mEdgeParams.enableVertexPointer( dim, GL_FLOAT, 4*dim, 0 );
    }
    

    
    public void init( Graph graph ) {
        mGraph = graph;
        mInit  = true;
    }
    
        
    public void render( GGraphics g ) {
        GL gl = g.gl();
        
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
            
            mIbo.pushDraw( gl );
            ByteBuffer buf = mIbo.map( gl, GL_WRITE_ONLY );
            for( Edge e = mGraph.mEdges; e != null; e = e.mGraphNext ) {
                buf.putInt( (int)( e.mA.mTempDist + 0.5f ) );
                buf.putInt( (int)( e.mB.mTempDist + 0.5f ) );
            }
            
            mIbo.unmap( gl );
            mIbo.popDraw( gl );
            
            //System.out.println( "Loaded Verts: " + mVertCount + "  Edges: " + mEdgeCount );
        }
        
        // Write points to draw buffer.
        mVbo.pushDraw( gl );
        ByteBuffer buf = mVbo.map( gl, GL_WRITE_ONLY );
        
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
        
        mVbo.unmap( gl );
        
        // Execute line commands
        float lineAlpha = mLineAlpha * (float)( 1.0 / Math.max( 1.0, Math.min( 200.0, Math.log( mEdgeCount ) / Math.log( 3.0 ) ) ) ); 
        
        gl.glLineWidth( 1f );
        gl.glColor4f( 1f, 1f, 1f, lineAlpha );
        mIbo.pushDraw( gl );
        mEdgeParams.push( gl );
        mEdgeParams.execute( gl, GL_LINES, 0, mEdgeCount * 2 );
        mEdgeParams.pop( gl );
        mIbo.popDraw( gl );
        
        // Execute node geometry.
        gl.glPointSize( 4f );
        gl.glColor4f( 0.8f, 0f, 0f, 0.7f );
        mVertParams.push( gl );
        mVertParams.execute( gl, GL_POINTS, 0, mVertCount );
        mVertParams.pop( gl );
        
        mVbo.popDraw( gl );
    }
    
}
