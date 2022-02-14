import dsa.LinkedQueue;
import dsa.MaxPQ;
import dsa.Point2D;
import dsa.RectHV;
import stdlib.StdIn;
import stdlib.StdOut;

public class KdTreePointST<Value> implements PointST<Value> {
    // Instance variables
    // Reference to the root of a 2dTree, Node root
    private Node root;
    // Number of nodes in the tree, int n
    private int n;

    // Constructs an empty symbol table.
    public KdTreePointST() {
        // Initialize instance variables root and n appropriately.
        root = null;
        n = 0;
    }

    // Returns true if this symbol table is empty, and false otherwise.
    public boolean isEmpty() {
        return n == 0;
    }

    // Returns the number of key-value pairs in this symbol table.
    public int size() {
        return n;
    }

    // Inserts the given point and value into this symbol table.
    public void put(Point2D p, Value value) {
        // check value and p for corner cases.
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        // Call the private put() method with appropriate arguments to insert the given point and
        // value. It is at beginning.

        RectHV rect = new RectHV(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        root = put(root, p, value, rect, true);
    }

    // Returns the value associated with the given point in this symbol table, or null.
    public Value get(Point2D p) {
        // check corner case.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // Call the private get() method with appropriate arguments to find the value corresponding
        // to the given point
        return get(root, p, true);
    }

    // Returns true if this symbol table contains the given point, and false otherwise.
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        if (root == null) {
            return false;
        }
        return get(p) != null;
    }

    // Returns all the points in this symbol table.
    public Iterable<Point2D> points() {
        // Return all the points in the 2dTree, collected using a level-order traversal of the tree;
        // use two queues, one to aid in the traversal and the other to collect the points
        LinkedQueue<Node> s = new LinkedQueue<Node>();
        LinkedQueue<Point2D> p = new LinkedQueue<Point2D>();
        s.enqueue(root);
        while (!s.isEmpty()) {
            Node x = s.dequeue();
            if (x != null) {
                p.enqueue(x.p); // enqueue
                s.enqueue(x.lb); // enqueue.
                s.enqueue(x.rt); // enqueue.
            }
        }
        return p; // return p.
    }


    // Returns all the points in this symbol table that are inside the given rectangle.
    public Iterable<Point2D> range(RectHV rect) {
        // Call the private range() method with appropriate arguments, the last one being an empty
        // queue of Point2D objects,
        if (rect == null) {
            throw new NullPointerException("rect is null");
        }
        LinkedQueue<Point2D> q = new LinkedQueue<Point2D>();
        range(root, rect, q);
        // and return the queue.
        return q;
    }

    // Returns the point in this symbol table that is different from and closest to the given point,
    // or null.
    public Point2D nearest(Point2D p) {
        // Return a point from the 2dTree that is different from and closest to the given point by
        // calling the private method nearest() with appropriate arguments
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        return nearest(root, p, root.p, true);
    }

    // Returns up to k points from this symbol table that are different from and closest to the
    // given point.
    public Iterable<Point2D> nearest(Point2D p, int k) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        MaxPQ<Point2D> PQ = new MaxPQ<Point2D>(p.distanceToOrder());
        nearest(root, p, k, PQ, true);
        return PQ;
    }

    // Note: In the helper methods that have lr as a parameter, its value specifies how to
    // compare the point p with the point x.p. If true, the points are compared by their
    // x-coordinates; otherwise, the points are compared by their y-coordinates. If the
    // comparison of the coordinates (x or y) is true, the recursive call is made on x.lb;
    // otherwise, the call is made on x.rt.

    // Inserts the given point and value into the KdTree x having rect as its axis-aligned
    // rectangle, and returns a reference to the modified tree.
    private Node put(Node x, Point2D p, Value value, RectHV rect, boolean lr) {

        // corner case.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // corner case
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        // If x = null, return a new Node object built appropriately
        if (x == null) {
            n++;
            return new Node(p, value, rect);
        }
        // If the point in x is the same as the given point, update the value in x to the given
        // value
        if (p.equals(x.p)) {
            x.value = value;
        } else {
            // Otherwise, make a recursive call to put() with appropriate arguments to insert the
            // given point and value into the left subtree x.lb or the right subtree x.rt
            // depending on how x.p
            // and p compare (use lr to decide which coordinate to consider)
            if (lr) {
                // put to the left.
                if (p.x() < x.p.x()) {
                    RectHV newRect = new RectHV(rect.xMin(), rect.yMin(), x.p.x(), rect.yMax());
                    x.lb = put(x.lb, p, value, newRect, !lr);
                } else {   // put to the right.
                    RectHV newRect = new RectHV(x.p.x(), rect.yMin(), rect.xMax(), rect.yMax());
                    x.rt = put(x.rt, p, value, newRect, !lr);
                }
            } else {
                // put to the left
                if (p.y() < x.p.y()) {
                    RectHV newRect = new RectHV(rect.xMin(), rect.yMin(), rect.xMax(), x.p.y());
                    x.lb = put(x.lb, p, value, newRect, !lr);
                } else {
                    // put to the right.
                    RectHV newRect = new RectHV(rect.xMin(), x.p.y(), rect.xMax(), rect.yMax());
                    x.rt = put(x.rt, p, value, newRect, !lr);
                }

            }

        }
        // Ã¢â‚¬Â¢ Return x
        return x;
    }

    // Returns the value associated with the given point in the KdTree x, or null.
    private Value get(Node x, Point2D p, boolean lr) {
        // If x = null, return null
        if (x == null) {
            return null;
        }
        // If the point in x is the same as the given point, return the value in x
        if (p.equals(x.p)) {
            return x.value;
        }
        // Ã¢â‚¬Â¢ Make a recursive call to get() with appropriate arguments to find the value
        // corresponding
        // to the given point in the left subtree x.lb or the right subtree x.rt depending on how
        // x.p and p compare
        if (lr) {
            if (p.x() < x.p.x()) {
                return get(x.lb, p, !lr);
            }
        } else if (p.y() < x.p.y()) {
            return get(x.lb, p, !lr);
        }
        return get(x.rt, p, !lr);
    }


    // Collects in the given queue all the points in the KdTree x that are inside rect.
    private void range(Node x, RectHV rect, LinkedQueue<Point2D> q) {
        // If x = null, simply return
        if (x == null) {
            return;
        }
        //Ã¢â‚¬Â¢ If rect contains the point in x, enqueue the point into q
        if (rect.contains(x.p)) {
            q.enqueue(x.p);
        }
        // Ã¢â‚¬Â¢ Make recursive calls to range() on the left subtree x.lb and on the right subtree
        // x.rt

        if (x.lb != null) {
            range(x.lb, rect, q);
        }
        if (x.rt != null) {
            range(x.rt, rect, q);
        }
        // Ã¢â‚¬Â¢ Incorporate the range search pruning rule mentioned in the project writeup
    }

    // Returns the point in the KdTree x that is closest to p, or null; nearest is the closest
    // point discovered so far.
    private Point2D nearest(Node x, Point2D p, Point2D nearest, boolean lr) {
        double distance = Double.POSITIVE_INFINITY;
        // If x = null, return nearest
        Point2D close = nearest;
        if (x == null) {
            return close;
        }
        if (close == null) {
            distance = p.distanceSquaredTo(close);
        }
        // Ã¢â‚¬Â¢ If the point x.p is different from the given point p and the squared distance
        // between the two is
        // smaller than the squared distance between nearest and p, update nearest to x.p
        if (!x.p.equals(p) && x.p.distanceSquaredTo(p) < close.distanceSquaredTo(p)) {
            close = x.p;
        }
        //Ã¢â‚¬Â¢ Make a recursive call to nearest() on the left subtree x.lb
        if (lr) {
            if (p.x() < x.p.x()) {
                return nearest(x.lb, p, close, !lr);
            } else {
                //Ã¢â‚¬Â¢ Make a recursive call to nearest() on the right subtree x.rt, using the
                // value returned by the
                return nearest(x.rt, p, close, !lr);
            }
        } else {
            if (p.y() < x.p.y()) {
                return nearest(x.lb, p, close, !lr);
            } else {
                //Ã¢â‚¬Â¢ Make a recursive call to nearest() on the right subtree x.rt, using the
                // value returned by the
                return nearest(x.rt, p, close, !lr);
            }
        }
    }

    // Collects in the given max-PQ up to k points from the KdTree x that are different from and
    // closest to p.
    private void nearest(Node x, Point2D p, int k, MaxPQ<Point2D> pq, boolean lr) {
        // corner case.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        // Call the private nearest() method passing it an empty maxPQ of Point2D objects (built
        // with a suitable comparator from Point2D) as one of the arguments, and return the PQ
        // If x = null or if the size of pq is greater than k, simply return
        if (x == null || pq.size() > k) {
            return;
        }
        // If the point in x is different from the given point, insert it into pq
        if (!x.p.equals(p)) {
            pq.insert(x.p);
        }
        // If the size of pq exceeds k, remove the maximum point from the pq
        if (pq.size() > k) {
            pq.delMax();
        }
        //  Make recursive calls to nearest() on the left subtree x.lb and on the right
        // subtree
        // x.rt  Incorporate the nearest neighbor pruning rules mentioned in the project
        // writeup
        if (lr) {
            // vertical aligned
            if (p.x() < x.p.x()) {
                nearest(x.lb, p, k, pq, !lr);
                nearest(x.rt, p, k, pq, !lr);
            } else {
                nearest(x.rt, p, k, pq, !lr);
                nearest(x.lb, p, k, pq, !lr);
            }
        } else {
            // horizontal aligned
            if (p.y() < x.p.y()) {
                nearest(x.lb, p, k, pq, !lr);
                nearest(x.rt, p, k, pq, !lr);
            } else {
                nearest(x.rt, p, k, pq, !lr);
                nearest(x.lb, p, k, pq, !lr);
            }
        }

    }

    // A representation of node in a KdTree in two dimensions (ie, a 2dTree). Each node stores a
// 2d point (the key), a value, an axis-aligned rectangle, and references to the left/bottom
// and right/top subtrees.
    private class Node {
        private Point2D p;   // the point (key)
        private Value value; // the value
        private RectHV rect; // the axis-aligned rectangle
        private Node lb;     // the left/bottom subtree
        private Node rt;     // the right/top subtree

        // Constructs a node given the point (key), the associated value, and the
        // corresponding axis-aligned rectangle.
        Node(Point2D p, Value value, RectHV rect) {
            this.p = p;
            this.value = value;
            this.rect = rect;
        }

    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        KdTreePointST<Integer> st = new KdTreePointST<>();
        double qx = Double.parseDouble(args[0]);
        double qy = Double.parseDouble(args[1]);
        int k = Integer.parseInt(args[2]);
        Point2D query = new Point2D(qx, qy);
        RectHV rect = new RectHV(-1, -1, 1, 1);
        int i = 0;
        while (!StdIn.isEmpty()) {
            double x = StdIn.readDouble();
            double y = StdIn.readDouble();
            Point2D p = new Point2D(x, y);
            st.put(p, i++);
        }
        StdOut.println("st.empty()? " + st.isEmpty());
        StdOut.println("st.size() = " + st.size());
        StdOut.printf("st.contains(%s)? %s\n", query, st.contains(query));
        StdOut.printf("st.range(%s):\n", rect);
        for (Point2D p : st.range(rect)) {
            StdOut.println("  " + p);
        }
        StdOut.printf("st.nearest(%s) = %s\n", query, st.nearest(query));
        StdOut.printf("st.nearest(%s, %d):\n", query, k);
        for (Point2D p : st.nearest(query, k)) {
            StdOut.println("  " + p);
        }
    }
}