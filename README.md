# SafeDriveMonitor

Benvenuti in **SAFEDRIVEMONITOR**. Questo progetto mira a migliorare la sicurezza stradale tramite un sistema di monitoraggio del tasso alcolemico dei conducenti, applicabile a tutti i mezzi di trasporto su strada. L’obiettivo è garantire il rispetto delle normative e prevenire incidenti causati da alcol e sostanze stupefacenti.

Attualmente l’applicazione è in fase di sviluppo e questa è una prima versione.

## Parte del Conducente

**Componenti fittizie:**
- Rilevamento dei dispositivi di monitoraggio (espandibile con dispositivi reali).  
- Test dei conducenti con risultati casuali (alcol e droghe), rispettando soglie veritiere.

**Componenti reali:**
- Conducente identificato tramite Nome, Cognome e ID univoci.
- Database individuale con i risultati dei test precedenti.
- Reset ID Conducente funzionante via email (sms implementato ma non funzionate attualmente),per attuare una verifica , fare richiesta dell'api key ai collaboratori della repository.

## Parte Admin

- Login con credenziali predefinite:  
    - **username**: admin  
    - **password**: admin
- Schermata di gestione per visualizzare i log di ogni conducente e i relativi risultati dei test.
