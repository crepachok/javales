package gem;

import java.util.Comparator;

public class ParallelogramAreaComparator implements Comparator<Parallelogram> {
    public int compare(Parallelogram a, Parallelogram b){
        return Double.compare(a.GetArea(), b.GetArea());
    }
}
