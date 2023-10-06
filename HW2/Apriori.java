import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Apriori {


    public static void main(String args[]) throws IOException{

        String filename = args[0];
        int minSupportPercentage = Integer.parseInt(args[1]);
        List<Set<String>> db = Util.readTransactionsFromFile(filename); 

        float minSupport= (float) minSupportPercentage / 100 ;
        float minSupportValue = minSupport * db.size();
        System.out.println("Total DB size = "+db.size()+", Frequency % = "+minSupportPercentage+", Minimum Support Value = "+minSupportValue);


        Set<String> uniqueItems = Util.getUniqueItems(db);    // find unique items from database

        int k = 1;  // k represents Level

        List<Set<String>> candidates = new ArrayList<Set<String>>();
        List<List<Set<String>>> allLevelFrequentItems = new ArrayList<List<Set<String>>>();

        while(true){
            System.out.println("\n At Level "+k+": ");
            if(k==1){
                candidates = Util.generateFirstCandidate(uniqueItems);   // generates candidates from database based on unique items , candiates of size 1    k=1,   candidates = [ {1}, {2}, {3} ]
                System.out.println("Candidates = "+candidates);
            }else{
                candidates = Util.generateCandidates(allLevelFrequentItems.get(k-2),k);          // candidate set of size 2, when k=2,    candidates = [ {1,2} {1,3} ..]  generated from first frequentitemSet
                System.out.println("Candidates = "+candidates);
            }

            if(candidates.isEmpty())
                break;

            //each candidate set, scan against db and if candiadte support count >= min support, then add it to a List<Set<String>>
            List<Set<String>> currentLevelFrequentItems = new ArrayList<Set<String>>();
            for(Set<String> candidate : candidates){
                int candidateSupport = Util.getCandidateSupport(candidate,db);
                System.out.println(candidate + " --> " + candidateSupport);
                if( candidateSupport >= minSupportValue){
                    currentLevelFrequentItems.add(candidate);
                }
            }

            System.out.println("Current Level Frequent Items with (Minimum Support Value "+minSupportValue+") = "+currentLevelFrequentItems);

            if(currentLevelFrequentItems.isEmpty())
                break;

            // add the frquent items to  frequentitems[k-1]
            allLevelFrequentItems.add(k-1, currentLevelFrequentItems);            
            k=k+1;
            candidates = null;
        }
        //print frequentItems that we collected at each level.
        System.out.println("\n\nFinal frequent itemsets = "+allLevelFrequentItems);
    }
}


    


class Util{


    public static List<Set<String>> readTransactionsFromFile(String filePath) throws IOException {
		//This method read the lines from the file and create a Set<String> for each line
        List<Set<String>> transactions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] elements = line.split(" ");
                Set<String> transaction = new HashSet<>();
                for (String element : elements) {
                    transaction.add(element);
                }
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    public static int getCandidateSupport(Set<String> candidate, List<Set<String>> db){
        int count = 0;
        for (Set<String> record : db) {
            if (record.containsAll(candidate)) {
                // The candidate is a complete record in the database
                count++;
            } else {
                boolean isSubset = true;
                for (String element : candidate) {
                    if (!record.contains(element)) {
                        isSubset = false;
                        break;
                    }
                }
                if (isSubset) {
                    // The candidate is a subset of the record in the database
                    count++;
                }
            }
        }
        return count;
    }
    public static List<Set<String>> generateCombinations(Set<String> values, int size) {
        List<Set<String>> combinations = new ArrayList<>();
        generateCombinationsHelper(values, size, new ArrayList<>(), combinations);
        return combinations;
    }
    private static void generateCombinationsHelper(Set<String> values, int size, List<String> currentCombination, List<Set<String>> combinations) {
        if (size == 0) {
            combinations.add(new HashSet<>(currentCombination));
            return;
        }
        if (values.isEmpty()) {
            return;
        }
        String[] valuesArray = values.toArray(new String[0]);
        for (int i = 0; i < valuesArray.length; i++) {
            String currentValue = valuesArray[i];
            List<String> remainingValues = new ArrayList<>(Arrays.asList(valuesArray).subList(i + 1, valuesArray.length));
            currentCombination.add(currentValue);
            generateCombinationsHelper(new HashSet<>(remainingValues), size - 1, currentCombination, combinations);
            currentCombination.remove(currentValue);
        }
    }

    public static List<Set<String>> generateCandidates(List<Set<String>> previousfrequentItemSet, int sizeOfCandidateSet){
        //get unique values from the last frequentitemSet (Eg: at level 3, we use 2nd level frequnet items to generate candiadte set)
        Set<String> uniqueItems =  getUniqueItems(previousfrequentItemSet);
        //generate combinations based on the size (Eg: from unique values, if size is 2, we generate [{1,2} {1,3} ..], if size is 3, we generate [{1,2,3} {1,2,4}..]
        List<Set<String>> combinations = generateCombinations(uniqueItems, sizeOfCandidateSet);
        return combinations;
    }

    public static List<Set<String>> generateFirstCandidate(Set<String> uniqueItems){		
		//Generate the first candiate itemset based on the unique values 
        List<Set<String>> resultList = new ArrayList<>();
        for (String uniqueItem : uniqueItems) {
            Set<String> singleElementSet = new HashSet<>();
            singleElementSet.add(uniqueItem);
            resultList.add(singleElementSet);
        }
        return resultList;
	}


    public static Set<String> getUniqueItems(List<Set<String>> db){
        // This method finds the unique items from the database. This is used for first candidate generation

        Set<String> uniqueItems = new HashSet<String>();
        for(Set<String> transaction : db){
            for(String item : transaction){
                uniqueItems.add(item);
            }
        }
        return  uniqueItems;
    }

}
