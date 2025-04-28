# emottak-utils
Dette biblioteket inneholder funksjoner og domenemodeller som flere prosjekter har til felles. Eksempler på dette er:
* Lesing av miljøvariabler
* Domenemodell for hendelser (Events)
* Oppsett av Kafka, inkludert Event-publisher for å logge hendelser
* Interaksjon mot Vault

### Release-prosess
Release utføres på følgende måte:
1. Sett ønsket versjonsnummer i `build.gradle.kts` og kjør commit/push.
2. Velg korrekt release-metode:
   * **For å utføre en offentlig release:**
     - a) Naviger til https://github.com/navikt/emottak-utils/releases og klikk på `Draft a new release`. 
     - b) Klikk på `Choose a tag`, tast inn et versjonsnummer (f.eks. `v1.2.3`) og klikk `+ Create new tag: ... on publish`.
         <details>
         <summary>Vi følger semantisk versjonering</summary>
         
         Semantisk versjonering som betyr at vi skal kunne lese ut om en endring er bakoverkompatibel eller ikke i forhold til forrige versjon: **Major.Minor.Patch**
         
         * **Eksempel på ikke-bakoverkompatibel endring (ny major-versjon):** Flyttet klasser/filer, utført refaktorisering av ikke-private klasser/felter, endret delte domenemodeller.
         * **Eksempel på bakoverkompatibel endring (ny minor-versjon):** Lagt til nye klasser/filer, eventuelt nye funksjoner i eksisterende klasser/filer.
         * **Eksempel på patch-endring:** Rettet feil i eksisterende logikk/kode.
         </details>
     - c) Velg kodestrøm det skal releases fra (helst bør vi _kun_ release fra main).
     - d) `Previous tag`: Velg auto hvis release er høyeste versjon, ellers til ønsket versjon som den skal etterfølge. Hvis sistnevnte: Fjern hake nederst på `Set as the latest release`.
     - e) Tast inn tag som `Release title` (f.eks. `v1.2.3`).
     - f) Legg inn en god beskrivelse på hva som er nytt, hva som er endret, osv.
     - g) Klikk på `Publish release` for å starte release-prosessen.
   
   * **For å utføre en testrelease:**
     - a) Naviger til Actions, og klikk på jobben `Publish emottak-utils library` i lista til venstre: https://github.com/navikt/emottak-utils/actions/workflows/publish.yaml.
     - b) Klikk `Run workflow`, og velg din branch.
