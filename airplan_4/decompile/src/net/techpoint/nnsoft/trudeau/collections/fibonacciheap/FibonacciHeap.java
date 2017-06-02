/*
 * Decompiled with CFR 0_121.
 */
package net.techpoint.nnsoft.trudeau.collections.fibonacciheap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import net.techpoint.nnsoft.trudeau.collections.fibonacciheap.FibonacciHeapNode;

public final class FibonacciHeap<E>
implements Queue<E> {
    private static final double LOG_PHI = Math.log((1.0 + Math.sqrt(5.0)) / 2.0);
    private final Set<E> elementsIndex = new HashSet();
    private final Comparator<? super E> comparator;
    private int size = 0;
    private int trees = 0;
    private int markedNodes = 0;
    private FibonacciHeapNode<E> minimumNode;

    public FibonacciHeap() {
        this(null);
    }

    public FibonacciHeap(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    private void moveToRoot(FibonacciHeapNode<E> node) {
        if (this.isEmpty()) {
            this.moveToRootAssist(node);
        } else {
            this.moveToRootAdviser(node);
        }
    }

    private void moveToRootAdviser(FibonacciHeapNode<E> node) {
        node.getLeft().setRight(node.getRight());
        node.getRight().setLeft(node.getLeft());
        node.setLeft(this.minimumNode);
        node.setRight(this.minimumNode.getRight());
        this.minimumNode.setRight(node);
        node.getRight().setLeft(node);
        if (this.compare(node, this.minimumNode) < 0) {
            this.moveToRootAdviserCoordinator(node);
        }
    }

    private void moveToRootAdviserCoordinator(FibonacciHeapNode<E> node) {
        this.minimumNode = node;
    }

    private void moveToRootAssist(FibonacciHeapNode<E> node) {
        this.minimumNode = node;
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new IllegalArgumentException("Null elements not allowed in this FibonacciHeap implementation.");
        }
        FibonacciHeapNode<E> node = new FibonacciHeapNode<E>(e);
        this.moveToRoot(node);
        ++this.size;
        this.elementsIndex.add(e);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        for (E element : c) {
            this.add(element);
        }
        return true;
    }

    @Override
    public void clear() {
        this.minimumNode = null;
        this.size = 0;
        this.trees = 0;
        this.markedNodes = 0;
        this.elementsIndex.clear();
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        return this.elementsIndex.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        if (c == null) {
            return false;
        }
        for (Object o : c) {
            if (!this.containsAllEngine(o)) continue;
            return false;
        }
        return true;
    }

    private boolean containsAllEngine(Object o) {
        if (!this.contains(o)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return this.minimumNode == null;
    }

    @Override
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E element() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.peek();
    }

    @Override
    public boolean offer(E e) {
        return this.add(e);
    }

    @Override
    public E peek() {
        if (this.isEmpty()) {
            return null;
        }
        return this.minimumNode.getElement();
    }

    @Override
    public E poll() {
        if (this.isEmpty()) {
            return null;
        }
        FibonacciHeapNode<E> z = this.minimumNode;
        FibonacciHeapNode x = z.getChild();
        for (int numOfKids = z.getDegree(); numOfKids > 0; --numOfKids) {
            FibonacciHeapNode<E> tempLast = x.getRight();
            this.moveToRoot(x);
            x.setParent(null);
            x = tempLast;
        }
        z.getLeft().setRight(z.getRight());
        z.getRight().setLeft(z.getLeft());
        if (z == z.getRight()) {
            this.minimumNode = null;
        } else {
            this.pollCoordinator(z);
        }
        --this.size;
        E minimum = z.getElement();
        this.elementsIndex.remove(minimum);
        return minimum;
    }

    private void pollCoordinator(FibonacciHeapNode<E> z) {
        this.minimumNode = z.getRight();
        this.consolidate();
    }

    @Override
    public E remove() {
        if (this.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.poll();
    }

    private void consolidate() {
        if (this.isEmpty()) {
            return;
        }
        int arraySize = (int)Math.floor(Math.log(this.size) / LOG_PHI);
        ArrayList<FibonacciHeapNode<E>> nodeSequence = new ArrayList<>(arraySize);
        for (int q = 0; q < arraySize; ++q) {
            this.consolidateHome(nodeSequence, q);
        }
        int numRoots = 0;
        FibonacciHeapNode<E> x = this.minimumNode;
        if (x != null) {
            ++numRoots;
            for (x = x.getRight(); x != this.minimumNode; x = x.getRight()) {
                ++numRoots;
            }
        }
        while (numRoots > 0) {
            int degree = x.getDegree();
            FibonacciHeapNode<E> next = x.getRight();
            while (nodeSequence.get(degree) != null) {
                FibonacciHeapNode<E> y = nodeSequence.get(degree);
                if (this.compare(x, y) > 0) {
                    FibonacciHeapNode<E> pointer = y;
                    y = x;
                    x = pointer;
                }
                this.link(y, x);
                nodeSequence.set(degree, null);
                ++degree;
            }
            nodeSequence.set(degree, x);
            x = next;
            --numRoots;
        }
        this.minimumNode = null;
        for (int q = 0; q < nodeSequence.size(); ++q) {
            this.consolidateExecutor(nodeSequence, q);
        }
    }

    private void consolidateExecutor(List<FibonacciHeapNode<E>> nodeSequence, int k) {
        FibonacciHeapNode<E> pointer = nodeSequence.get(k);
        if (pointer == null) {
            return;
        }
        if (this.minimumNode == null) {
            this.minimumNode = pointer;
        }
        if (this.minimumNode != null) {
            this.consolidateExecutorExecutor(pointer);
        }
    }

    private void consolidateExecutorExecutor(FibonacciHeapNode<E> pointer) {
        this.moveToRoot(pointer);
    }

    private void consolidateHome(List<FibonacciHeapNode<E>> nodeSequence, int p) {
        nodeSequence.add(p, null);
    }

    private void link(FibonacciHeapNode<E> y, FibonacciHeapNode<E> x) {
        y.getLeft().setRight(y.getRight());
        y.getRight().setLeft(y.getLeft());
        y.setParent(x);
        if (x.getChild() == null) {
            this.linkExecutor(y, x);
        } else {
            y.setLeft(x.getChild());
            y.setRight(x.getChild().getRight());
            x.getChild().setRight(y);
            y.getRight().setLeft(y);
        }
        x.incraeseDegree();
        y.setMarked(false);
        ++this.markedNodes;
    }

    private void linkExecutor(FibonacciHeapNode<E> y, FibonacciHeapNode<E> x) {
        x.setChild(y);
        y.setRight(y);
        y.setLeft(y);
    }

    private void cut(FibonacciHeapNode<E> x, FibonacciHeapNode<E> y) {
        this.moveToRoot(x);
        y.decraeseDegree();
        x.setParent(null);
        x.setMarked(false);
        --this.markedNodes;
    }

    private void cascadingCut(FibonacciHeapNode<E> y) {
        FibonacciHeapNode<E> z = y.getParent();
        if (z != null) {
            if (!y.isMarked()) {
                new FibonacciHeapService(y).invoke();
            } else {
                this.cascadingCutGuide(y, z);
            }
        }
    }

    private void cascadingCutGuide(FibonacciHeapNode<E> y, FibonacciHeapNode<E> z) {
        this.cut(y, z);
        this.cascadingCut(z);
    }

    public int potential() {
        return this.trees + 2 * this.markedNodes;
    }

    private int compare(FibonacciHeapNode<E> o1, FibonacciHeapNode<E> o2) {
        if (this.comparator != null) {
            return this.comparator.compare(o1.getElement(), o2.getElement());
        }
        Comparable o1Comparable = (Comparable)o1.getElement();
        return o1Comparable.compareTo(o2.getElement());
    }

    public String toString() {
        if (this.minimumNode == null) {
            return "FibonacciHeap=[]";
        }
        Stack stack = new Stack();
        stack.push(this.minimumNode);
        StringBuilder buf = new StringBuilder("FibonacciHeap=[");
        while (!stack.empty()) {
            FibonacciHeapNode curr = (FibonacciHeapNode)stack.pop();
            buf.append(curr);
            buf.append(", ");
            if (curr.getChild() != null) {
                stack.push(curr.getChild());
            }
            FibonacciHeapNode start = curr;
            for (curr = curr.getRight(); curr != start; curr = curr.getRight()) {
                buf.append(curr);
                buf.append(", ");
                if (curr.getChild() == null) continue;
                new FibonacciHeapAssist(stack, curr).invoke();
            }
        }
        buf.append(']');
        return buf.toString();
    }

    private class FibonacciHeapAssist {
        private Stack<FibonacciHeapNode<E>> stack;
        private FibonacciHeapNode<E> curr;

        public FibonacciHeapAssist(Stack<FibonacciHeapNode<E>> stack, FibonacciHeapNode<E> curr) {
            this.stack = stack;
            this.curr = curr;
        }

        public void invoke() {
            this.stack.push(this.curr.getChild());
        }
    }

    private class FibonacciHeapService {
        private FibonacciHeapNode<E> y;

        public FibonacciHeapService(FibonacciHeapNode<E> y) {
            this.y = y;
        }

        public void invoke() {
            this.y.setMarked(true);
            FibonacciHeap.this.markedNodes++;
        }
    }

}

