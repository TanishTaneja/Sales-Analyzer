import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;  
import java.util.*;  

public class SalesAnalyzer1 {  
    static class SalesEntry {  
        String date;  
        String sku;  
        double unitPrice;  
        int quantity;  
        double totalPrice;  

        public SalesEntry(String date, String sku, double unitPrice, int quantity, double totalPrice) {  
            this.date = date;  
            this.sku = sku;  
            this.unitPrice = unitPrice;  
            this.quantity = quantity;  
            this.totalPrice = totalPrice;  
        }  
    } 

    public static void main(String[] args) { 
        List<SalesEntry> salesEntries = new ArrayList<>();  

        try (BufferedReader br = new BufferedReader(new FileReader("ice-cream-sales.csv"))) {  
            br.readLine();  

            String line;  
            while ((line = br.readLine()) != null) {  
                String[] parts = line.split(",");  
                SalesEntry entry = new SalesEntry(  
                    parts[0],  
                    parts[1],  
                    Double.parseDouble(parts[2]),  
                    Integer.parseInt(parts[3]),  
                    Double.parseDouble(parts[4])  
                );  
                salesEntries.add(entry);  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
            return;  
        }  

        System.out.println("Total Store Sales = "+totalSales(salesEntries));
        System.out.println();
        System.out.println("Monthly Analysis");  

        Map<String, List<SalesEntry>> monthlySales = new HashMap<>();  
        for (SalesEntry entry : salesEntries) {  
            String month = entry.date.substring(0, 7);  
            if (!monthlySales.containsKey(month)) {  
                monthlySales.put(month, new ArrayList<>());  
            }  
            monthlySales.get(month).add(entry);  
        }  

        List<String> sortedMonths = new ArrayList<>(monthlySales.keySet());  
        Collections.sort(sortedMonths);  
 
        for (String month : sortedMonths) {  
            List<SalesEntry> monthSales = monthlySales.get(month);   
            System.out.println("\nMonth: " + month);  
            System.out.println("Total Monthly Sales = " + totalSales(monthSales));  

            Map<String, Double> itemQuantities = new HashMap<>();  
            for (SalesEntry entry : monthSales) {
                itemQuantities.put(entry.sku, itemQuantities.getOrDefault(entry.sku, 0.0) + entry.quantity); 
            }  

            Object[] mostPopular=getMaxKeyValue(itemQuantities);
            System.out.println("Most Popular Item = " + mostPopular[0] + ", Quantity = " + mostPopular[1]);  
            
            Map<String, Double> itemRevenues = new HashMap<>();  
            for (SalesEntry entry : monthSales) {  
                itemRevenues.put(entry.sku, itemRevenues.getOrDefault(entry.sku, 0.0) + entry.totalPrice);  
            }  

            Object[] highestRevenue=getMaxKeyValue(itemRevenues);
            System.out.println("Highest Revenue Item = " + highestRevenue[0] +", Highest Revenue = "+ highestRevenue[1]);  

            int minOrders = Integer.MAX_VALUE;  
            int maxOrders = Integer.MIN_VALUE;  
            double totalOrders = 0;  
            int orderCount = 0;  

            for (SalesEntry entry : monthSales) {  
                if (entry.sku.equals(mostPopular[0])) {  
                    minOrders = Math.min(minOrders, entry.quantity);  
                    maxOrders = Math.max(maxOrders, entry.quantity);  
                    totalOrders += entry.quantity;  
                    orderCount++;  
                }  
            }  

            double avgOrders = orderCount > 0 ? totalOrders / orderCount : 0;  
            System.out.println("Most Popular Item = " + mostPopular[0] + "Order Stats:");  
            System.out.println("  Minimum Orders = " + minOrders);  
            System.out.println("  Maximum Orders = " + maxOrders);  
            System.out.println("  Average Orders = " + String.format("%.2f", avgOrders));  
        }  
    }

    public static double totalSales(List<SalesEntry> salesEntries){
        double total = 0;  
        for (SalesEntry entry : salesEntries) {  
            total += entry.totalPrice;  
        }  
        return total;
    }

    public static Object[] getMaxKeyValue(Map<String, Double> dataMap) {
        String maxKey = null;
        double maxValue = 0;
        
        for (Map.Entry<String, Double> entry : dataMap.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                maxKey = entry.getKey();
            }
        }
        return new Object[] {maxKey, maxValue};
    }
}