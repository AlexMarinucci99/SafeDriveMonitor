
# SafeDriveMonitor

**Prototipo desktop per la gestione e il monitoraggio di controlli legati alla sicurezza stradale.**

SafeDriveMonitor è un'applicazione sviluppata in Java e JavaFX per esplorare un sistema di gestione dei conducenti, simulazione di controlli alcolemici e tossicologici e consultazione delle informazioni attraverso un pannello amministrativo.

Il progetto ha finalità dimostrative e di sviluppo software. Non costituisce uno strumento certificato per effettuare misurazioni, determinare l'idoneità alla guida o verificare il rispetto delle normative vigenti.

## Stato del progetto

**Prototipo in sviluppo.** Alcune funzionalità sono simulate o predisposte per integrazioni future. Le caratteristiche descritte di seguito si riferiscono al perimetro funzionale documentato nel progetto e non implicano una validazione per l'utilizzo in produzione.

## Funzionalità

### Area conducente

- Accesso tramite interfaccia dedicata.
- Simulazione di controlli alcolemici e tossicologici.
- Consultazione e gestione dei dati relativi ai controlli.
- Predisposizione all'integrazione con dispositivi di rilevamento esterni.
- Flussi di recupero dell'identificativo tramite email o SMS previsti nel progetto, ma attualmente disabilitati.

### Area amministratore

- Interfaccia per la consultazione delle informazioni relative ai conducenti.
- Visualizzazione dello storico e dei dettagli dei controlli.
- Funzioni di riepilogo e analisi dei dati previste nell'interfaccia amministrativa.

## Stack tecnologico

| Componente | Tecnologia |
| --- | --- |
| Linguaggio | Java 21 |
| Interfaccia desktop | JavaFX 20, FXML e CSS |
| Gestione del progetto | Apache Maven |
| Persistenza | SQLite, tramite JDBC |
| Test | JUnit 4 |
| Altre librerie | Gson, Apache HttpClient, Jakarta Mail |

Le versioni riportate fanno riferimento alle dipendenze dichiarate in `demo/pom.xml`.

## Struttura della repository

```text
SafeDriveMonitor/
├── README.md
├── monitoring.db
└── demo/
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/it/safedrivemonitor/
        │   │   ├── Runners/
        │   │   ├── controller/
        │   │   ├── model/
        │   │   └── service/
        │   └── resources/
        │       ├── css/
        │       ├── fxml/
        │       └── images/
        └── test/
```

L'applicazione JavaFX viene inizializzata da `App.java`; `MainApp.java` fornisce un ulteriore punto di ingresso. La classe `DatabaseManager` gestisce l'inizializzazione del database all'avvio.

## Requisiti

- JDK 21.
- Apache Maven.
- Un ambiente desktop compatibile con JavaFX.
- Un IDE Java, consigliato per l'avvio e il debug del prototipo.

## Installazione e verifica

Clonare la repository e accedere al modulo Maven:

```bash
git clone https://github.com/AlexMarinucci99/SafeDriveMonitor.git
cd SafeDriveMonitor/demo
```

Per compilare il progetto ed eseguire i test configurati:

```bash
mvn clean test
```

Per avviare l'interfaccia grafica, importare `demo/pom.xml` nell'IDE ed eseguire la classe `it.safedrivemonitor.Runners.MainApp`, configurando JavaFX se richiesto dall'ambiente.

L'esecuzione su differenti sistemi operativi non è stata verificata in questa documentazione.

## Dati e sicurezza

Il progetto include un database SQLite dimostrativo. Non utilizzare dati personali reali o informazioni sensibili negli ambienti di prova.

Prima di un eventuale impiego al di fuori dell'ambiente di sviluppo è necessario rivedere:

- La gestione delle credenziali predefinite.
- I meccanismi di autenticazione e autorizzazione.
- La protezione del database.
- La gestione delle configurazioni applicative.
- Il trattamento dei dati personali.

Eventuali credenziali dimostrative non devono essere riutilizzate in ambienti pubblici o di produzione.

## Limiti e sviluppi futuri

- Le verifiche alcolemiche e tossicologiche sono simulate e non equivalgono a misurazioni effettuate da dispositivi certificati.
- L'integrazione con hardware esterno richiede implementazione e validazione specifiche.
- Le funzioni di recupero tramite email o SMS risultano disabilitate nella documentazione attuale.
- Prestazioni, scalabilità, accuratezza delle misurazioni e sicurezza non sono state certificate per l'utilizzo in produzione.
- Sono necessari test funzionali, di sicurezza e di compatibilità prima di ipotizzare un rilascio pubblico.

## Finalità

SafeDriveMonitor è un progetto dimostrativo orientato allo studio dello sviluppo di applicazioni desktop, della persistenza dei dati e della progettazione di interfacce per la consultazione di informazioni legate alla sicurezza stradale.
