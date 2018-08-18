package com.apos.bean;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class clsOperatorComparator implements Comparator<clsOperatorDtl> 
{

	
	 private List<Comparator<clsOperatorDtl>> listComparators;

	    @SafeVarargs
	    public clsOperatorComparator(Comparator<clsOperatorDtl>... comparators)
	    {
	        this.listComparators = Arrays.asList(comparators);
	    }

	    @Override
	    public int compare(clsOperatorDtl o1, clsOperatorDtl o2)
	    {
	        for (Comparator<clsOperatorDtl> comparator : listComparators)
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
