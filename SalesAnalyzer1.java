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

        // Read data from file  
        try (BufferedReader br = new BufferedReader(new FileReader("ice-cream-sales.csv"))) {  
            br.readLine(); // Skip header  

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

        // 1. Calculate total store sales  
        double totalStoreSales = 0;  
        for (SalesEntry entry : salesEntries) {  
            totalStoreSales += entry.totalPrice;  
        }  
        System.out.println("Total Store Sales: " + totalStoreSales);  
        System.out.println("\n--- Monthly Analysis ---");  

        // 2. Group sales by month  
        Map<String, List<SalesEntry>> monthlySales = new HashMap<>();  
        for (SalesEntry entry : salesEntries) {  
            String month = entry.date.substring(0, 7);  
            if (!monthlySales.containsKey(month)) {  
                monthlySales.put(month, new ArrayList<>());  
            }  
            monthlySales.get(month).add(entry);  
        }  

        // 3. Sort months in chronological order  
        List<String> sortedMonths = new ArrayList<>(monthlySales.keySet());  
        Collections.sort(sortedMonths);  

        // 4. Process each month  
        for (String month : sortedMonths) {  
            List<SalesEntry> monthSales = monthlySales.get(month);  

            // 4.1 Calculate total sales for the month  
            double monthTotalSales = 0;  
            for (SalesEntry entry : monthSales) {  
                monthTotalSales += entry.totalPrice;  
            }  
            System.out.println("\nMonth: " + month);  
            System.out.println("Total Monthly Sales: $" + String.format("%.2f", monthTotalSales));  

            // 4.2 Find the most popular item (by quantity)  
            Map<String, Integer> itemQuantities = new HashMap<>();  
            for (SalesEntry entry : monthSales) {  
                if(itemQuantities.containsKey(entry.sku)) {
                    itemQuantities.put(entry.sku, itemQuantities.get(entry.sku) + entry.quantity);
                }
                else{
                    itemQuantities.put(entry.sku, entry.quantity);
                }
                // itemQuantities.put(entry.sku, itemQuantities.getOrDefault(entry.sku, 0) + entry.quantity); 
            }  

            String mostPopularItem = null;  
            int mostPopularQuantity = 0;  
            for (Map.Entry<String, Integer> item : itemQuantities.entrySet()) {  
                if (item.getValue() > mostPopularQuantity) {  
                    mostPopularQuantity = item.getValue();  
                    mostPopularItem = item.getKey();  
                }  
            }  
            System.out.println("Most Popular Item: " + mostPopularItem + " (Quantity: " + mostPopularQuantity + ")");  

            // 4.3 Find the item generating the highest revenue  
            Map<String, Double> itemRevenues = new HashMap<>();  
            for (SalesEntry entry : monthSales) {  
                itemRevenues.put(entry.sku, itemRevenues.getOrDefault(entry.sku, 0.0) + entry.totalPrice);  
            }  

            String highestRevenueItem = null;  
            double highestRevenue = 0;  
            for (Map.Entry<String, Double> item : itemRevenues.entrySet()) {  
                if (item.getValue() > highestRevenue) {  
                    highestRevenue = item.getValue();  
                    highestRevenueItem = item.getKey();  
                }  
            }  
            System.out.println("Highest Revenue Item: " + highestRevenueItem + " ($" + String.format("%.2f", highestRevenue) + ")");  

            // 4.4 Orders statistics for the most popular item  
            int minOrders = Integer.MAX_VALUE;  
            int maxOrders = Integer.MIN_VALUE;  
            double totalOrders = 0;  
            int orderCount = 0;  

            for (SalesEntry entry : monthSales) {  
                if (entry.sku.equals(mostPopularItem)) {  
                    minOrders = Math.min(minOrders, entry.quantity);  
                    maxOrders = Math.max(maxOrders, entry.quantity);  
                    totalOrders += entry.quantity;  
                    orderCount++;  
                }  
            }  

            double avgOrders = orderCount > 0 ? totalOrders / orderCount : 0;  
            System.out.println("Most Popular Item (" + mostPopularItem + ") Order Stats:");  
            System.out.println("  Minimum Orders: " + minOrders);  
            System.out.println("  Maximum Orders: " + maxOrders);  
            System.out.println("  Average Orders: " + String.format("%.2f", avgOrders));  
        }  
    }  
}
