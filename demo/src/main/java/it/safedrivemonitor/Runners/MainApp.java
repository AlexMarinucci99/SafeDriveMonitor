// Dichiarazione del package in cui risiede la classe MainApp.
package it.safedrivemonitor.Runners;

// Dichiarazione della classe principale dell'applicazione.
public class MainApp {
    // Metodo main, punto d'ingresso dell'applicazione.
    public static void main(String[] args) {
        // Avvia l'applicazione chiamando il metodo launch della classe App.
        // Passa il riferimento della classe App e gli argomenti della riga di comando.
        App.launch(App.class, args);
    }
}
