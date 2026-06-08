# emottak-utils
Dette biblioteket inneholder funksjoner og domenemodeller som flere prosjekter har til felles. Eksempler på dette er:
* Lesing av miljøvariabler
* Domenemodell for hendelser (Events)
* Oppsett av Kafka, inkludert Event-publisher for å logge hendelser
* Interaksjon mot Vault

## Publisering

Biblioteket publiseres til [GitHub Packages](https://maven.pkg.github.com/navikt/emottak-utils) (`maven.pkg.github.com/navikt/emottak-utils`). Versjonen settes automatisk fra release-taggen. 
Det er ikke nødvendig å oppdatere versjon i `gradle.properties`. Dette gjøres automatisk ved release.

### Release-prosess

**For å publisere en ny release:**
1. Naviger til [Releases](https://github.com/navikt/emottak-utils/releases) og klikk `Draft a new release`
2. Opprett en ny tag (f.eks. `v1.2.3`) ved hjelp av semantisk versjonering
3. Klikk `Generate release notes` for å opprette en automatisk changelog basert på pull requests siden forrige release
4. Gjennomgå og rediger release notes etter behov
5. Publiser releasen. Dette starter automatisk publish-workflowen

**For å publisere en testrelease (snapshot eller pre-release):**
1. Gå til [Actions](https://github.com/navikt/emottak-utils/actions/workflows/publish.yaml) og åpne `Publish emottak-utils library`
2. Klikk `Run workflow`, velg din branch
3. Oppgi ønsket versjonsnummer (f.eks. `1.2.3-SNAPSHOT` eller `1.2.3-test.1`)

### Semantisk versjonering

Vi følger semantisk versjonering (**Major.Minor.Patch**):
- **Major:** Breaking changes (flyttet/omdøpte klasser, refaktorering av offentlig API, endret delte domenemodeller)
- **Minor:** Nye features (nye klasser, nye offentlige metoder)
- **Patch:** Feilrettinger i eksisterende kode
