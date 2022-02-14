import dsa.LinkedStack;
import dsa.LinkedQueue;
import dsa.MaxPQ;
import dsa.Point2D;
import dsa.RectHV;
import dsa.RedBlackBinarySearchTreeST;
import stdlib.StdIn;
import stdlib.StdOut;

public class BrutePointST<Value> implements PointST<Value> {
    // Instance variable
    // An underlying data structure to store the 2d points (keys) and their corresponding values,
    // RedBlackBST<Point2D, Value> bst
    RedBlackBinarySearchTreeST<Point2D, Value> bst;

    // Constructs an empty symbol table.
    public BrutePointST() {
        bst = new RedBlackBinarySearchTreeST<Point2D, Value>();
    }

    // Returns true if this symbol table is empty, and false otherwise.
    public boolean isEmpty() {
        return bst.isEmpty();
    }

    // Returns the number of key-value pairs in this symbol table.
    public int size() {
        return bst.size();
    }

    // Inserts the given point and value into this symbol table.
    public void put(Point2D p, Value value) {
        if (value == null) {
            throw new NullPointerException("value is null");
        }
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        bst.put(p, value);
    }

    // Returns the value associated with the given point in this symbol table, or null.
    public Value get(Point2D p) {
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        return bst.get(p);  // return value associate with given point.
    }

    // Returns true if this symbol table contains the given point, and false otherwise.
    public boolean contains(Point2D p) {
        // corner case.
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        return bst.contains(p); // return true if symbol table contains given point, otherwise
        // false.
    }

    // Returns all the points in this symbol table.
    public Iterable<Point2D> points() {
        return bst.keys();
    }

    // Returns all the points in this symbol table that are inside the given rectangle.
    public Iterable<Point2D> range(RectHV rect) {
        // corner case.
        if (rect == null) {
            throw new NullPointerException("rect is null");
        }
        LinkedQueue<Point2D> q = new LinkedQueue<Point2D>();
        for (Point2D p : points()) {
            if (rect.contains(p)) {
                q.enqueue(p); // enqueue the point if is contained in rect.
            }
        }
        return q; // return q.
    }

    // Returns the point in this symbol table that is different from and closest to the given point,
    // or null.
    public Point2D nearest(Point2D p) {
        // corner case
        if (p == null) {
            throw new NullPointerException("p is null");
        }
        double minDistance = Double.POSITIVE_INFINITY;
        Point2D nearPoint = null;
        double distance;
        // use for loop .
        for (Point2D q : points()) {
            distance = p.distanceSquaredTo(q);
            if (distance < minDistance && distance != 0) {
                minDistance = distance; // update minimum distance.
                nearPoint = q; // update near point.
            }
        }
        return nearPoint; // return near point.
    }

    // Returns up to k points from this symbol table that are different from and closest to the
    // given point.
    public Iterable<Point2D> nearest(Point2D p, int k) {
        MaxPQ<Point2D> pq = new MaxPQ<Point2D>(size(), p.distanceToOrder());
        for (Point2D point : points()) {
            if (p.equals(point)) {
                continue;
            }
            pq.insert(point); // insert the point to pq.
            if (pq.size() > k) { // delete the max value if it crosses the k points.
                pq.delMax();
            }
        }
        LinkedStack<Point2D> nearPoint = new LinkedStack<Point2D>();
        for (int i = 0; i < k; i++) {
            nearPoint.push(pq.delMax());
        }
        return nearPoint;
    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        BrutePointST<Integer> st = new BrutePointST<Integer>();
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
