package vista;

import java.util.Scanner;

public class InOut {
    
    private Scanner sc;
    
    public InOut() {
        
        this.sc = new Scanner(System.in);
        
    }
    
    public void imprimirMensaje(String mensaje) {
        
        System.out.println(mensaje);
        
    }
    
    public String leerString(String mensaje) {
        
        System.out.print(mensaje);
        return sc.nextLine();
        
    }
    
    public int leerEntero(String mensaje) {
        
        System.out.print(mensaje);
        int valor = sc.nextInt();
        sc.nextLine(); 
        return valor;
        
    }
    
    public double leerDouble(String mensaje) {
        
        System.out.print(mensaje);
        double valor = sc.nextDouble();
        sc.nextLine(); 
        return valor;
        
    }
    
}