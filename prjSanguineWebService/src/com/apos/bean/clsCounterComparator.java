package com.apos.bean;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class clsCounterComparator implements Comparator<clsCounterDtlBean>
{

    private List<Comparator<clsCounterDtlBean>> listComparators;

    @SafeVarargs
    public clsCounterComparator(Comparator<clsCounterDtlBean>... comparators)
    {
        this.listComparators = Arrays.asList(comparators);
    }

    @Override
    public int compare(clsCounterDtlBean o1, clsCounterDtlBean o2)
    {
        for (Comparator<clsCounterDtlBean> comparator : listComparators)
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