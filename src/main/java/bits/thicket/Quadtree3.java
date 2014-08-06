/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

import java.util.*;


/**
 * @author decamp
 */
public class Quadtree3 implements Quadtree {

    private float mX;
    private float mY;
    private float mZ;
    private float mHalfSize;
    private int mMaxDepth = 0;
    
    private QuadtreeCell mRoot = null;
    private QuadtreeCell[] mPool = null;
    private int mPoolPos = 0;
    private final float[] mWork = new float[4];
    
    
    public Quadtree3() {
        reallocPool( 16 );
    }
    
    
    
    public int dims() {
        return 3;
    }
    
    
    public void rebuild( Vert verts, int maxDepth, float[] optCenter, float optSize ) {
        if( optCenter != null ) {
            init( maxDepth, optCenter, optSize );
        } else {
            float[] box = mWork;
            Graphs.computeBounds2( verts, box );
            float dx = box[2] - box[0];
            float dy = box[3] - box[1];
            box[0] = ( box[0] + box[2] ) * 0.5f;
            box[1] = ( box[1] + box[3] ) * 0.5f;
            init( maxDepth, box, ( dx > dy ? dx : dy ) );
        }
        
        for( Vert v = verts; v != null; v = v.mGraphNext ) {
            insert( v );
        }
        
        // Finish cells by computing center of gravity.
        for( int i = 0; i < mPoolPos; i++ ) {
            QuadtreeCell cell = mPool[i];
            float w = 1f / cell.mWeight;
            cell.mMeanX *= w;
            cell.mMeanY *= w;
        }
    }
    
    
    public void init( int maxDepth, float[] center, float size ) {
        mX = center[0];
        mY = center[1];
        mZ = center[2];
        mHalfSize = 0.5f * size;
        mMaxDepth = maxDepth;
        
        mRoot = null;
        mPoolPos = 0;
    }
    
    
    public void insert( Vert vert ) {
        QuadtreeCell cell = mRoot;
        
        if( cell != null ) {
            final float weight = vert.mWeight;

            for( int depth = mMaxDepth; depth > 0; depth-- ) {
                if( cell.mVerts != null ) {
                    divideCell( cell );
                }
                
                addWeightToCell( vert, cell );
                int idx = childIndexFor( cell, vert );
                QuadtreeCell child = cell.mChildren[idx];
                if( child == null ) {
                    cell = newLeaf( cell, idx );
                    break;
                }
                
                cell = child;
            }
            
            addVertToCell( vert, cell );
            
        } else {
            cell = allocCell();
            cell.mX = mX;
            cell.mY = mY;
            cell.mZ = mZ;
            cell.mHalfSize = mHalfSize;
            mRoot = cell;
            
            addVertToCell( vert, cell );
        }
    }
    
    
    public QuadtreeCell root() {
        return mRoot;
    }

    
    public QuadtreeCell childFor( QuadtreeCell cell, Vert vert ) {
        return cell.mChildren[ childIndexFor( cell, vert ) ];
    }
    
    
    public int childIndexFor( QuadtreeCell cell, Vert vert ) { 
        return ( vert.mX < cell.mX ? 0 : 1 ) + 
               ( vert.mY < cell.mY ? 0 : 2 ) +
               ( vert.mZ < cell.mZ ? 0 : 4 );
    }
    
    
    
    private void addVertToCell( Vert vert, QuadtreeCell cell ) {
        addWeightToCell( vert, cell );
        vert.mTempNext = cell.mVerts;
        cell.mVerts    = vert;
    }

    
    private void addWeightToCell( Vert vert, QuadtreeCell cell ) {
        float w = vert.mWeight;
        cell.mMeanX  += w * vert.mX;
        cell.mMeanY  += w * vert.mY;
        cell.mMeanZ  += w * vert.mZ;
        cell.mWeight += w;
    }
    
    
    private QuadtreeCell allocCell() {
        QuadtreeCell ret;
        
        if( mPoolPos < mPool.length ) {
            ret = mPool[mPoolPos++];
        } else {
            int cap = ( mPool.length * 3 ) / 2;
            reallocPool( cap );
            ret = mPool[mPoolPos++];
        }
        
        ret.mX      = 0f;
        ret.mY      = 0f;
        ret.mZ      = 0f;
        ret.mWeight = 0f;
        ret.mMeanX  = 0f;
        ret.mMeanY  = 0f;
        ret.mMeanZ  = 0f;
        ret.mVerts  = null;
        ret.mChildren[0] = null;
        ret.mChildren[1] = null;
        ret.mChildren[2] = null;
        ret.mChildren[3] = null;
        ret.mChildren[4] = null;
        ret.mChildren[5] = null;
        ret.mChildren[6] = null;
        ret.mChildren[7] = null;
        ret.mChildList   = null;
        ret.mNextSibling = null;
        
        return ret;
    }
    
    
    private void divideCell( QuadtreeCell cell ) {
        Vert vert   = cell.mVerts;
        cell.mVerts = null;
        
        while( vert != null ) {
            int idx = childIndexFor( cell, vert );
            QuadtreeCell leaf = cell.mChildren[idx];
            if( leaf == null ) {
                leaf = newLeaf( cell, idx );
            }
            
            Vert nextPoint = vert.mTempNext;
            addVertToCell( vert, leaf );
            vert = nextPoint;
        }
    }
        
    /**
     * Add child to cell. 
     * <p>
     * PRECONDITION: Child should be null.
     */
    private QuadtreeCell newLeaf( QuadtreeCell cell, int index ) {
        QuadtreeCell child = allocCell();
        float size = cell.mHalfSize * 0.5f;
        child.mX = cell.mX + ( ( index << 1 & 0x2 ) - 1 ) * size;
        child.mY = cell.mY + ( ( index      & 0x2 ) - 1 ) * size;
        child.mZ = cell.mZ + ( ( index >> 1 & 0x2 ) - 1 ) * size;
        child.mHalfSize = size;
        
        cell.mChildren[index] = child;
        child.mNextSibling = cell.mChildList;
        cell.mChildList = child;
        
        return child;
    }

    
    private void reallocPool( int size ) {
        int prev = 0;
        
        if( mPool == null ) {
            mPool = new QuadtreeCell[size];
        } else {
            prev = mPool.length;
            mPool = Arrays.copyOf( mPool, size );
        }
        
        for( int i = prev; i < size; i++ ) {
            mPool[i] = new QuadtreeCell( 8 );
        }
    }
    
}
