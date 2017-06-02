/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.nnsoft.trudeau.collections.fibonacciheap;

final class FibonacciHeapNode<E> {
    private final E element;
    private FibonacciHeapNode<E> parent;
    private FibonacciHeapNode<E> left;
    private FibonacciHeapNode<E> right;
    private FibonacciHeapNode<E> child;
    private int degree;
    private boolean marked;

    public FibonacciHeapNode(E element) {
        this.left = this;
        this.right = this;
        this.degree = 0;
        this.setParent(null);
        this.setChild(null);
        this.setLeft(this);
        this.setRight(this);
        this.setMarked(false);
        this.element = element;
    }

    public FibonacciHeapNode<E> getParent() {
        return this.parent;
    }

    public void setParent(FibonacciHeapNode<E> parent) {
        this.parent = parent;
    }

    public FibonacciHeapNode<E> getLeft() {
        return this.left;
    }

    public void setLeft(FibonacciHeapNode<E> left) {
        this.left = left;
    }

    public FibonacciHeapNode<E> getRight() {
        return this.right;
    }

    public void setRight(FibonacciHeapNode<E> right) {
        this.right = right;
    }

    public FibonacciHeapNode<E> getChild() {
        return this.child;
    }

    public void setChild(FibonacciHeapNode<E> child) {
        this.child = child;
    }

    public int getDegree() {
        return this.degree;
    }

    public void incraeseDegree() {
        ++this.degree;
    }

    public void decraeseDegree() {
        --this.degree;
    }

    public boolean isMarked() {
        return this.marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public E getElement() {
        return this.element;
    }

    public String toString() {
        return this.element.toString();
    }
}

