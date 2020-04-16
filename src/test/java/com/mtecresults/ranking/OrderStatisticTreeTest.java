package com.mtecresults.ranking;

import java.util.*;

import com.google.common.collect.HashMultiset;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class OrderStatisticTreeTest {
    
    private final OrderStatisticTree<Integer> tree = new OrderStatisticTree<>();
    private final HashMultiset<Integer> set = HashMultiset.create();
    
    @Before
    public void before() {
        tree.clear();
        set.clear();
    }
    
    @Test
    public void testAdd() {
        assertEquals(set.isEmpty(), tree.isEmpty());
        
        for (int i = 10; i < 30; i += 2) {
            assertTrue(tree.isHealthy());
            assertEquals(set.contains(i), tree.contains(i));
            assertEquals(set.add(i), tree.add(i));
            assertEquals(set.add(i), tree.add(i));
            assertEquals(set.contains(i), tree.contains(i));

            assertTrue(!tree.containsCycles());
            assertTrue(tree.heightsAreCorrect());
            assertTrue(tree.isBalanced());
            assertTrue(tree.isWellIndexed());
            assertTrue(tree.isHealthy());
        }
        
        assertEquals(set.isEmpty(), tree.isEmpty());
    }
    
    @Test
    public void testAddAll() {
        for (int i = 0; i < 10; ++i) {
            assertEquals(set.add(i), tree.add(i));
        }
        
        Collection<Integer> coll = Arrays.asList(10, 9, 7, 11, 12);
        
        assertEquals(set.addAll(coll), tree.addAll(coll));
        assertEquals(set.size(), tree.size());
        
        for (int i = -10; i < 20; ++i) {
            assertEquals(set.contains(i), tree.contains(i));
        }
    }

    @Test
    public void testClear() {
        for (int i = 0; i < 2000; ++i) {
            set.add(i);
            tree.add(i);
        }
        
        assertEquals(set.size(), tree.size());
        set.clear();
        tree.clear();
        assertEquals(set.size(), tree.size());
    }
    
    @Test
    public void testContains() {
        for (int i = 100; i < 200; i += 3) {
            assertTrue(tree.isHealthy());
            assertEquals(set.add(i), tree.add(i));
            assertTrue(tree.isHealthy());
        }
        
        assertEquals(set.size(), tree.size());
        
        for (int i = 0; i < 300; ++i) {
            assertEquals(set.contains(i), tree.contains(i));
        }
    }
    
    @Test 
    public void testContainsAll() {
        for (int i = 0; i < 50; ++i) {
            set.add(i);
            tree.add(i);
        }
        
        Collection<Integer> coll = new HashSet<>();
        
        for (int i = 10; i < 20; ++i) {
            coll.add(i);
        }
        
        assertEquals(set.containsAll(coll), tree.containsAll(coll));
        coll.add(100);
        assertEquals(set.containsAll(coll), tree.containsAll(coll));
    }

    @Test
    public void testRemove() {
        for (int i = 0; i < 200; ++i) {
            assertEquals(set.add(i), tree.add(i));
        }
        
        for (int i = 50; i < 150; i += 2) {
            assertEquals(set.remove(i), tree.remove(i));
            assertTrue(tree.isHealthy());
        }
        
        for (int i = -100; i < 300; ++i) {
            assertEquals(set.contains(i), tree.contains(i));
        }
    }

    @Test
    public void testRemoveLast() {
        tree.add(1);
        tree.remove(1);
        assertEquals(0, tree.size());
    }
    
    @Test
    public void testRemoveAll() {
        for (int i = 0; i < 40; ++i) {
            set.add(i);
            tree.add(i);
        }
        
        Collection<Integer> coll = new HashSet<>();
        
        for (int i = 10; i < 20; ++i) {
            coll.add(i);
        }
        
        assertEquals(set.removeAll(coll), tree.removeAll(coll));
        
        for (int i = -10; i < 50; ++i) {
            assertEquals(set.contains(i), tree.contains(i));
        }
        
        assertEquals(set.removeAll(coll), tree.removeAll(coll));
        
        for (int i = -10; i < 50; ++i) {
            assertEquals(set.contains(i), tree.contains(i));
        }
    }

    @Test
    public void testSize() {
        for (int i = 0; i < 200; ++i) {
            assertEquals(set.size(), tree.size());
            assertEquals(set.add(i), tree.add(i));
            assertEquals(set.size(), tree.size());
        }
    }
    
    @Test
    public void testIndexOf() {
        for (int i = 0; i < 100; ++i) {
            assertTrue(tree.add(i * 2));
        }
        
        for (int i = 0; i < 100; ++i) {
            assertEquals(i, tree.indexOf(2 * i));
        }
        
        for (int i = 100; i < 150; ++i) {
            assertEquals(-1, tree.indexOf(2 * i));
        }
    }
    
    @Test
    public void testEmpty() {
        assertEquals(set.isEmpty(), tree.isEmpty());
        set.add(0);
        tree.add(0);
        assertEquals(set.isEmpty(), tree.isEmpty());
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testEmptyTreeGetThrowsOnNegativeIndex() {
        tree.get(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testEmptyTreeSelectThrowsOnTooLargeIndex() {
        tree.get(0);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSelectThrowsOnNegativeIndex() {
        for (int i = 0; i < 5; ++i) {
            tree.add(i);
        }
        
        tree.get(-1);
    }
    
    @Test(expected = IndexOutOfBoundsException.class)
    public void testSelectThrowsOnTooLargeIndex() {
        for (int i = 0; i < 5; ++i) {
            tree.add(i);
        }
        
        tree.get(5);
    }
    
    @Test
    public void testGet() {
        for (int i = 0; i < 100; i += 3) {
            tree.add(i);
        }
        
        for (int i = 0; i < tree.size(); ++i) {
            Integer ints = tree.get(i);
            assertEquals(Integer.valueOf(3 * i), ints);
        }
    }
    
    @Test
    public void findBug() {
        tree.add(0);
        assertTrue(tree.isHealthy());
        
        tree.add(-1);
        tree.remove(-1);
        assertTrue(tree.isHealthy());
        
        tree.add(1);
        tree.remove(1);
        assertTrue(tree.isHealthy());
        
        tree.add(-1);
        tree.add(1);
        tree.remove(0);
        assertTrue(tree.isHealthy());
        
        tree.clear();
        tree.add(0);
        tree.add(-1);
        tree.add(10);
        tree.add(5);
        tree.add(15);
        tree.add(11);
        tree.add(30);
        tree.add(7);
        
        tree.remove(-1);
        
        assertTrue(tree.isHealthy());
    }
    
    @Test
    public void tryReproduceTheCounterBug() {
        long seed = System.nanoTime();
        Random random = new Random(seed);
        List<Integer> list = new ArrayList<>();
        
        System.out.println("tryReproduceTheCounterBug: seed = " + seed);
        
        for (int i = 0; i < 10; ++i) {
            int number = random.nextInt(1000);
            list.add(number);
            tree.add(number);
            assertTrue(tree.isHealthy());
        }
        
        for (Integer i : list) {
            tree.remove(i);
            boolean healthy = tree.isHealthy();
            assertTrue(healthy);
        }
    }

    @Test
    public void testDuplicatesRandom() {
        ArrayList<Integer> set = new ArrayList<>();
        OrderStatisticTree<Integer> tree = new OrderStatisticTree<>();

        int maxValue = 10_000;
        for(int i=0; i<100_000; i++){
            //decide randomly to add or delete, weighted towards adding
            boolean add = Math.random() > 0.3;
            if(add){
                Integer toAdd = i; //(int)(Math.random()*maxValue);
                set.add(toAdd);
                tree.add(toAdd);
            }
            else{
                if(!set.isEmpty()){
                    int index = (int)(Math.floor(Math.random()*set.size()));
                    Integer removed = set.remove(index);
                    tree.remove(removed);
                }
            }
            if(i % 1000 == 0){
                checkCounting(set, tree);
            }
            assertEquals(set.size(), tree.size());
        }
    }

    private void checkCounting(List<Integer> expected, OrderStatisticTree<Integer> tree){
        List<Integer> sortedExpected = new ArrayList<>(expected);
        Collections.sort(sortedExpected);
        int index = -1;
        int numSameRank = 1;
        int previousValue = -1;
        for(Integer value: sortedExpected){
            if(value != previousValue){
                //check previous rank size is correct
                if(index > -1) {
                    //skip uninitialized case
                    System.out.println("Check rank: "+(index+1)+" "+numSameRank);
                    assertEquals(numSameRank, tree.getDuplicateCount(index));
                }
                //update to start of new rank
                index += numSameRank;
                numSameRank = 0;
            }
            numSameRank++;
            previousValue = value;
            System.out.println("Check rank: "+(index+1));
            assertEquals(index+1, tree.indexOf(value) + 1);
        }
    }

}
