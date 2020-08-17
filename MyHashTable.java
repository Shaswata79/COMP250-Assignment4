import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;


public class MyHashTable<K,V> implements Iterable<HashPair<K,V>>{
    // num of entries to the table
    private int numEntries;
    // num of buckets 
    private int numBuckets;
    // load factor needed to check for rehashing 
    private static final double MAX_LOAD_FACTOR = 0.75;
    // ArrayList of buckets. Each bucket is a LinkedList of HashPair
    private ArrayList<LinkedList<HashPair<K,V>>> buckets;

    // constructor
    public MyHashTable(int initialCapacity) {
        this.numEntries = 0;
        this.numBuckets = initialCapacity;
        this.buckets = new ArrayList<>(initialCapacity);
        for(int i = 0; i < initialCapacity; i++){
            LinkedList<HashPair<K,V>> ThisBucket = new LinkedList<>();
            this.buckets.add(i, ThisBucket);
        }
    }

    public int size() {
        return this.numEntries;
    }

    public boolean isEmpty() {
        return this.numEntries == 0;
    }

    public int numBuckets() {
        return this.numBuckets;
    }

    /**
     * Returns the buckets variable. Useful for testing  purposes.
     */
    public ArrayList<LinkedList< HashPair<K,V> > > getBuckets(){
        return this.buckets;
    }

    /**
     * Given a key, return the bucket position for the key. 
     */
    public int hashFunction(K key) {
        int hashValue = Math.abs(key.hashCode())%this.numBuckets;
        return hashValue;
    }

    /**
     * Takes a key and a value as input and adds the corresponding HashPair
     * to this HashTable. Expected average run time  O(1)
     */
    public V put(K key, V value) {
        try{
            this.numEntries++;
            if((this.numEntries/this.numBuckets) >= MAX_LOAD_FACTOR){
                rehash();
            }
            int HashValue = hashFunction(key);
            HashPair NewPair = new HashPair(key, value);
            V OldValue = get(key);
            if(OldValue == null){
                LinkedList ThisBucket = this.buckets.get(HashValue);
                ThisBucket.add(NewPair);
                return null;
            }
            else{
                remove(key);
                LinkedList ThisBucket = this.buckets.get(HashValue);
                ThisBucket.add(NewPair);
                return OldValue;
            }
        }
        catch (Exception e){
            if(key == null){
                System.out.println("Null keys are not accepted!!");
            }
        }
        finally {
            return null;
        }
    }


    /**
     * Get the value corresponding to key. Expected average runtime O(1)
     */

    public V get(K key) {
        int Index = 0;
        HashPair ThisPair = null;
        boolean Found = false;
        int HashValue = hashFunction(key);
        LinkedList<HashPair<K,V>> ThisBucket = this.buckets.get(HashValue);
        for (int i = 0; i < ThisBucket.size(); i++){
            ThisPair = ThisBucket.get(i);
            if(ThisPair.getKey().equals(key)){
                Found = true;
                break;
            }
            Index++;
        }
        if(Found == true){
            return ThisBucket.get(Index).getValue();
        }
        else {
            return null;
        }
    }

    /**
     * Remove the HashPair corresponding to key . Expected average runtime O(1) 
     */
    public V remove(K key) {
        int Index = 0;
        boolean Found = false;
        int HashValue = hashFunction(key);
        LinkedList<HashPair<K,V>> ThisBucket = this.buckets.get(HashValue);
        for (int i = 0; i < ThisBucket.size(); i++){
            HashPair ThisPair = ThisBucket.get(i);
            if(ThisPair.getKey().equals(key)){
                Found = true;
                break;
            }
            Index++;
        }
        if(Found == true){
            this.numEntries--;
            return ThisBucket.remove(Index).getValue();
        }
        else {
            return null;
        }
    }


    /**
     * Method to double the size of the hashtable if load factor increases
     * beyond MAX_LOAD_FACTOR.
     * Made public for ease of testing.
     * Expected average runtime is O(m), where m is the number of buckets
     */
    public void rehash() {
        int NewSize = this.buckets.size() * 2;
        ArrayList<LinkedList<HashPair<K,V>>> NewMap = new ArrayList<LinkedList<HashPair<K,V>>>(NewSize);
        for(int k = 0; k < NewSize; k++){
            NewMap.add(new LinkedList<HashPair<K,V>>());
        }
        LinkedList<HashPair<K,V>> ThisBucket;
        int HashValue;
        this.numBuckets = this.numBuckets * 2;
        HashPair<K,V> ThisPair;
        for(int i = 0; i < this.buckets.size(); i++){
            ThisBucket = this.buckets.get(i);
            for(int j = 0; j < ThisBucket.size(); j++){
                ThisPair = ThisBucket.get(j);
                HashValue = hashFunction(ThisPair.getKey());
                NewMap.get(HashValue).add(ThisPair);
            }
        }
        this.buckets = NewMap;
    }


    /**
     * Return a list of all the keys present in this hashtable.
     * Expected average runtime is O(m), where m is the number of buckets
     */

    public ArrayList<K> keys() {
        ArrayList<K> KeyList = new ArrayList<K>();
        LinkedList<HashPair<K,V>> ThisBucket;
        for(int i = 0; i < this.buckets.size(); i++){
            ThisBucket = this.buckets.get(i);
            for(int j = 0; j < ThisBucket.size(); j++){
                KeyList.add(ThisBucket.get(j).getKey());
            }
        }
        return KeyList;
    }

    /**
     * Returns an ArrayList of unique values present in this hashtable.
     * Expected average runtime is O(m) where m is the number of buckets
     */
    public ArrayList<V> values() {
        MyHashTable<V, K> ValueTable = new MyHashTable<>(1);
        LinkedList<HashPair<K,V>> ThisBucket;
        for(int i = 0; i < this.buckets.size(); i++){
            ThisBucket = this.buckets.get(i);
            for(int j = 0; j < ThisBucket.size(); j++){
                HashPair<K,V> ThisPair = ThisBucket.get(j);
                ValueTable.put(ThisPair.getValue(), ThisPair.getKey());
            }
        }
        ArrayList<V> ValueList = new ArrayList<>();
        ValueList = ValueTable.keys();

        return ValueList;
    }


    /**
     * This method takes as input an object of type MyHashTable with values that
     * are Comparable. It returns an ArrayList containing all the keys from the map,
     * ordered in descending order based on the values they mapped to.
     *
     * The time complexity for this method is O(n^2), where n is the number
     * of pairs in the map.
     */
    public static  <K, V extends Comparable<V>> ArrayList<K> slowSort (MyHashTable<K, V> results) {
        ArrayList<K> sortedResults = new ArrayList<>();
        for (HashPair<K, V> entry : results) {
            V element = entry.getValue();
            K toAdd = entry.getKey();
            int i = sortedResults.size() - 1;
            V toCompare = null;
            while (i >= 0) {
                toCompare = results.get(sortedResults.get(i));
                if (element.compareTo(toCompare) <= 0 )
                    break;
                i--;
            }
            sortedResults.add(i+1, toAdd);
        }
        return sortedResults;
    }


    /**
     * This method takes as input an object of type MyHashTable with values that
     * are Comparable. It returns an ArrayList containing all the keys from the map,
     * ordered in descending order based on the values they mapped to.
     *
     * The time complexity for this method is O(n*log(n)), where n is the number
     * of pairs in the map.
     */


    /*
    public static <K, V extends Comparable<V>> ArrayList<K> fastSort(MyHashTable<K, V> results){
        ArrayList<HashPair<K,V>> HashList = new ArrayList<HashPair<K,V>>();
        LinkedList<HashPair<K,V>> ThisBucket;
        for(int i = 0; i < results.buckets.size(); i++){
            ThisBucket = results.buckets.get(i);
            HashList.addAll(ThisBucket);
        }
        ArrayList<HashPair<K,V>> SortedList = heapSort(HashList);
        ArrayList<K> SortedKeyList = new ArrayList<K>();
        for(int i = 1; i < HashList.size(); i++){
            SortedKeyList.add(i-1, SortedList.get(i).getKey());
        }
        return SortedKeyList;
    }

    private static <V extends Comparable<V>, K> ArrayList<HashPair<K,V>> heapSort(ArrayList<HashPair<K,V>> hashList) {
        int n = hashList.size();
        ArrayList<HashPair<K,V>> Heap = new ArrayList<>();
        Heap = buildHeap(hashList);
        for(int i = 1; i < n; i++){
            swapElements(Heap, 1, n+1-i);
            downHeap(Heap, n - i);
        }
        return Heap;
    }

    private static <V extends Comparable<V>, K> void swapElements(ArrayList<HashPair<K,V>> heap, int a, int b) {
        HashPair<K,V> temp = heap.get(a);
        heap.set(a, heap.get(b));
        heap.set(b, temp);
    }


    private static <K,V extends Comparable<V>> void downHeap(ArrayList<HashPair<K,V>> heap, int n) {
        int i = 1;
        while(2*i <= n){
            int Child = 2*i;
            if (Child < n){
                if(heap.get(Child+1).getValue().compareTo(heap.get(Child).getValue()) < 0){
                    Child++;
                }
            }
            if(heap.get(Child).getValue().compareTo(heap.get(i).getValue()) < 0){
                swapElements(heap, i, Child);
                i = Child;
            }
        }
    }

    private static <V extends Comparable<V>, K> ArrayList<HashPair<K,V>> buildHeap(ArrayList<HashPair<K,V>> hashList) {
        ArrayList<HashPair<K,V>> Heap = new ArrayList<>(hashList.size() + 1);
        for(int j = 0; j<hashList.size() + 1; j++){
            Heap.add(j,null);
        }
        for (int k = 1; k < hashList.size(); k++){
            Heap.set(k, hashList.get(k-1));
            upHeap(Heap, k);
        }
        return Heap;
    }

    private static <K, V extends Comparable<V>> void upHeap(ArrayList<HashPair<K,V>> heap, int k) {
        int i = k;
        while ((i>1) && (heap.get(i).getValue().compareTo(heap.get(i/2).getValue()) < 0)){
            swapElements(heap, i, i/2);
            i = i/2;
        }
    }

    */


    public static <K, V extends Comparable<V>> ArrayList<K> fastSort(MyHashTable<K, V> results) {
        ArrayList<HashPair<K,V>> HashList = new ArrayList<HashPair<K,V>>();
        LinkedList<HashPair<K,V>> ThisBucket;
        for(int i = 0; i < results.buckets.size(); i++){
            ThisBucket = results.buckets.get(i);
            HashList.addAll(ThisBucket);
        }
        ArrayList<HashPair<K,V>> SortedList = results.mergeSort(HashList);

        ArrayList<K> SortedKeys = new ArrayList<K>();
        for(int i = 0; i < SortedList.size(); i++){
            SortedKeys.add(i, SortedList.get(i).getKey());
        }

        return SortedKeys;
    }

    private <K,V extends Comparable<V>> ArrayList<HashPair<K,V>> mergeSort(ArrayList<HashPair<K,V>> MyList){

        if(MyList.size() <= 1){

            return MyList;
        }
        else{
            if(MyList.size() != 2){

                int Mid = (MyList.size() - 1) / 2;
                ArrayList<HashPair<K,V>> List1 = new ArrayList<>();
                ArrayList<HashPair<K,V>> List2 = new ArrayList<>();

                for(int i = 0; i < Mid; i++){
                    List1.add(MyList.get(i));
                }

                for(int i = Mid; i < MyList.size(); i++){
                    List2.add(MyList.get(i));
                }

                List1 = mergeSort(List1);
                List2 = mergeSort(List2);
                return merge(List1,List2);
            }
            else {

                ArrayList<HashPair<K,V>> List1 = new ArrayList<>();
                List1.add(MyList.get(0));
                ArrayList<HashPair<K,V>> List2 = new ArrayList<>();
                List2.add(MyList.get(1));
                List1 = mergeSort(List1);
                List2 = mergeSort(List2);
                return merge(List1,List2);
            }
        }
    }

    private <K,V extends Comparable<V>> ArrayList<HashPair<K,V>> merge(ArrayList<HashPair<K,V>> List1, ArrayList<HashPair<K,V>> List2){
        ArrayList<HashPair<K,V>> NewList = new ArrayList<HashPair<K,V>>();
        while((!List1.isEmpty()) && (!List2.isEmpty())){
            if(List1.get(0).getValue().compareTo(List2.get(0).getValue()) > 0){
                NewList.add(List1.remove(0));
            }
            else {
                NewList.add(List2.remove(0));
            }
        }
        while(!List1.isEmpty()){
            NewList.add(List1.remove(0));
        }
        while(!List2.isEmpty()){
            NewList.add(List2.remove(0));
        }
        return NewList;
    }


    @Override
    public MyHashIterator iterator() {
        return new MyHashIterator();
    }

    private class MyHashIterator implements Iterator<HashPair<K,V>> {
        private ArrayList<HashPair<K,V>> HashList;
        private int Index;
        /**
         * Expected average runtime is O(m) where m is the number of buckets
         */
        private MyHashIterator() {
            this.Index = -1;
            this.HashList = new ArrayList<HashPair<K,V>>();
            LinkedList<HashPair<K,V>> ThisBucket;
            for(int i = 0; i < buckets.size(); i++){
                ThisBucket = buckets.get(i);
                for(int j = 0; j < ThisBucket.size(); j++){
                    this.Index++;
                    this.HashList.add(ThisBucket.get(j));
                }
            }
        }

        @Override
        /**
         * Expected average runtime is O(1)
         */
        public boolean hasNext() {
            if (this.Index < 0){
                return false;
            }
            else{
                return true;
            }
        }

        @Override
        /**
         * Expected average runtime is O(1)
         */
        public HashPair<K,V> next() {
            if(this.hasNext() == true){
                this.Index--;
                return this.HashList.get(this.Index + 1);
            }
            else{
                throw new NoSuchElementException();
            }
        }

    }
}