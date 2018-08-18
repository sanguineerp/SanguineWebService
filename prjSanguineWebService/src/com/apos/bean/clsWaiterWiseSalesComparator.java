package com.apos.bean;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class clsWaiterWiseSalesComparator implements Comparator<clsBillDtl>
{

    private List<Comparator<clsBillDtl>> listComparators;

    @SafeVarargs
    public clsWaiterWiseSalesComparator(Comparator<clsBillDtl>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsBillDtl o1, clsBillDtl o2)
    {
        for (Comparator<clsBillDtl> comparator : listComparators)
        {
            int result = comparator.compare(o1, o2);
            if (result != 0)
            {
                return result;
            }
        }
        return 0;
    }
}