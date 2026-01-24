package curs4.functionalinterface.built_inFunctionalInterface;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MapEx {
    public static void main(String[] args) {
        BiFunction<String, String, String> mapper =
                (v1, v2)-> v1.length() > v2.length() ? v1: v2;

        Map<String, String> favorites = new HashMap<>();
        favorites.put("Jenny", "Bus Tour");
        favorites.put("Tom", "Tram");

        String jenny = favorites. merge ("Jenny", "Skyride",
              mapper );
        String tom = favorites. merge ("Tom", "Skyride",
                mapper );

//        System.out.println(favorites); // {Tom=Skyride, Jenny=Bus Tour
//        System.out.println(jenny); // Bus Tour
//        System.out.println(tom); // Skyride

//
//        BiFunction<String, Integer, Integer> mapper2 =
//                (v1, v2)-> v2+1;
//
//        Map<String, Integer> counts = new HashMap<>();
//        counts.put("Jenny", 9);
//        Integer jenny2=counts.computeIfPresent("Jenny",mapper2);
//        System.out.println(counts); //{Jenny=10}
//        System.out.println(jenny2);


        Function<String, Integer> mapper3 =
                (v)-> 1;

        Map<String, Integer> countsIfAbs = new HashMap<>();
        countsIfAbs.put("John", 10);

        Integer res=countsIfAbs.computeIfAbsent("Don",mapper3);
        System.out.println(res);
        System.out.println(countsIfAbs);




    }
}
