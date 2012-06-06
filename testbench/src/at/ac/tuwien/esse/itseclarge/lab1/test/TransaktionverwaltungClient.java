package at.ac.tuwien.esse.itseclarge.lab1.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

import org.json.JSONException;

public class TransaktionverwaltungClient extends CardClient {
	
	public TransaktionverwaltungClient() {
		super("keystore/transaktionen/keystore.jks");
	}
	
	
	public static void main(String[] args) throws IOException, APIException{
		
		TransaktionverwaltungClient client = new TransaktionverwaltungClient();
		InputStreamReader stdin = new InputStreamReader(System.in);
		BufferedReader in = new BufferedReader(stdin);
		String input = ""; 
		
		while (!(input.equals("3"))){
				
				printMenu();			
				input = in.readLine();  
				
				if (input.equals("1")){ //erstelle Karte
					
						System.out.println("Kartennummer:");
						String cardno = in.readLine();
						
						System.out.println("Gültig bis MM/YY:");
						String validity = in.readLine();	
												
						boolean valid;
						
						try{
							valid = client.isValid(cardno,validity);
						}catch (APIException e) {
							System.err.println(e.getMessage());
							continue;
						}
						
						if(valid){
							System.out.println("Karte ist gültig");
						}else{
							System.out.println("Karte ist ungültig");							
						}
					
					
					}else if(input.equals("2")){ //lösche Karte
						
						System.out.println("Kartennummer:");
						String cardno = in.readLine();
						
						System.out.println("Gültig bis MM/YY:");
						String validity = in.readLine();	
						
											
						BigDecimal limit;
						
						try{
							limit = client.limit(cardno,validity);
						}catch (APIException e) {
							System.err.println(e.getMessage());
							continue;
						}
						
						System.out.println("Limt ist " + limit);
						 
					}
				}
		
		

		System.exit(0);
	}
	
	
	private static void printMenu(){
		
		System.out.println("Transactionsverwaltungsclient");
		System.out.println("Optionen:\n");		
		System.out.println("\t1) Gültigkeitsprüfung");			
		System.out.println("\t2) Limitprüfung");		
		System.out.println("\t3) Beenden");
		
		System.out.println("\nAuswahl:");	
		
	}
	
}
