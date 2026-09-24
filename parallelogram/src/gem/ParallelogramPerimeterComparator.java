package gem;

import java.util.Comparator;;

public class ParallelogramPerimeterComparator implements Comparator<Parallelogram> {
    public int compare(Parallelogram a, Parallelogram b){
        double areaA = a.GetPerimeter();
        double areaB = b.GetPerimeter();

        if (areaA > areaB) return 1;
        if (areaA == areaB) return 0;
        return -1;
    }
}















