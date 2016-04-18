package com.mobile.bolt.support;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neeraj on 4/16/2016.
 */
public class PresortedSearch {

    public static List<String> generateList(){
        List<String> list = new ArrayList<>();
        list.add("Show All");
        list.add("Show Gradable");
        list.add("Show Graded");
        list.add("Show Ouputted");
        return list;
    }
    public static Integer whatType(String type){
        if(type.matches("Show All")) return 0;
        if(type.matches("Show Gradable")) return 1;
        if(type.matches("Show Graded")) return 2;
        if(type.matches("Show Ouputted")) return 3;
        return 0;
    }

}
