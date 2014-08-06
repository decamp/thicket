/*
 * Copyright (c) 2014. Philip DeCamp
 * Released under the BSD 2-Clause License
 * http://opensource.org/licenses/BSD-2-Clause
 */

package bits.thicket;

public final class QuadtreeCell {
    
    // Centered position
    public float mX;
    public float mY;
    public float mZ;
    
    // Half-size of cell on any dimension.
    public float mHalfSize;
    
    // List of points. This is never null for leaves, and always null for non-leaves.
    public Vert mVerts;
    
    // First moment of weighted points.
    public float mMeanX;
    public float mMeanY;
    public float mMeanZ;
    
    // Weight of points in subtree.
    public float mWeight;
    
    // Children. If leaf, all children are null. If not leaf, at least one will be non-null.
    public final QuadtreeCell[] mChildren;
    
    // Children as linked list on child.mNextSibling. Children list is in arbitrary order.
    public QuadtreeCell mChildList;
    
    // Link to next child with same parent as this cell. 
    public QuadtreeCell mNextSibling;
    
    
    public QuadtreeCell( int childCount ) {
        mChildren = new QuadtreeCell[ childCount ];
    }
    
}
