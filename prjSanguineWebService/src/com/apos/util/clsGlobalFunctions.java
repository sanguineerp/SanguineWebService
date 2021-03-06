package com.apos.util;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.apos.bean.clsSalesFlashColumns;

public class clsGlobalFunctions
{
    
    public <K extends Comparable, V extends Comparable> Map<K, V> funSortMapOnValues(Map<K, V> map)
    {
	List<Map.Entry<K, V>> entries = new LinkedList<Map.Entry<K, V>>(map.entrySet());
	
	Collections.sort(entries, new Comparator<Map.Entry<K, V>>()
	{
	    
	    @Override
	    public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
	    {
		return o1.getValue().compareTo(o2.getValue());
	    }
	});
	
	// LinkedHashMap will keep the keys in the order they are inserted
	// which is currently sorted on natural ordering
	Map<K, V> sortedMap = new LinkedHashMap<K, V>();
	
	for (Map.Entry<K, V> entry : entries)
	{
	    sortedMap.put(entry.getKey(), entry.getValue());
	}
	
	return sortedMap;
    }
    
    
    public static Comparator<clsSalesFlashColumns> COMPARATOR = new Comparator<clsSalesFlashColumns>()
    {
        // This is where the sorting happens.
        public int compare(clsSalesFlashColumns o1, clsSalesFlashColumns o2)
        {
         return (int) (o2.getSeqNo() - o1.getSeqNo());
        }
    };
    
    
    public static DecimalFormat funGetGlobalDecimalFormatter(int noOfDecimalPlace)
    {
	
		DecimalFormat gDecimalFormat = new DecimalFormat(funGetGlobalDecimalFormatString(noOfDecimalPlace));
		return gDecimalFormat;
    }

    public static String funGetGlobalDecimalFormatString(int noOfDecimalPlace)
    {
		StringBuilder decimalFormatBuilderForDoubleValue = new StringBuilder("0");
		for (int i = 0; i < noOfDecimalPlace; i++)
		{
		    if (i == 0)
		    {
			decimalFormatBuilderForDoubleValue.append(".0");
		    }
		    else
		    {
			decimalFormatBuilderForDoubleValue.append("0");
		    }
	}
	return decimalFormatBuilderForDoubleValue.toString();
    }
    
}
