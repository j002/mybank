### MyBank

Application Kotlin Multiplatform (KMP) permettant d’afficher des banques, leurs comptes, et les opérations associées, avec une UI partagée en Compose Multiplatform pour Android et iOS.
Le projet met l’accent sur :
- une architecture claire et testable
- une séparation stricte des responsabilités
- des tests multiplateformes (commonTest)
###  🧱 Architecture
Architecture inspirée de Clean Architecture / MVVM, adaptée au KMP.
<img width="320" height="532" alt="Capture d’écran 2026-02-10 à 17 10 17" src="https://github.com/user-attachments/assets/5469719a-357e-4114-9141-49a1f28a688b" />


### 🔁 Flux de données
<img width="256" height="332" alt="Capture d’écran 2026-02-10 à 17 11 36" src="https://github.com/user-attachments/assets/5bc52e1a-5398-44b2-93b2-0150d39d2386" />

### 📦 Couches en détail
### Domain

- Indépendant de toute technologie
- Contient uniquement la logique métier

### Exemples :

- Bank
- Account
- Operation
- GetBanksUseCase
### Data
Responsable de la récupération et transformation des données.

- DTO : représentation brute de l’API
- Mapper : DTO → Domain
- Repository : point d’entrée côté domaine
- Service : appels réseau (Ktor)
- DataSource : abstraction du service
###  UI

UI partagée 100 % Compose Multiplatform.
- Aucun parsing
- Aucun formatage métier
- Affichage uniquement à partir de modèles déjà prêts

Exemples :
- BanksScreen
- OperationsScreen
- BanksStore
- OperationsStore

###  Utils

Logique transverse, testable et réutilisable.

- DateUtils
- AmountUtils
- OperationSorter
- BankSorter
###  🎨 UI & Thème

- Material 3
- Couleurs basées sur l’identité MyBank / Crédit Agricole
- Thème commun Android / iOS
- Pas de hardcoding dans les écrans

Les couleurs et le thème sont définis dans :

```shell
  ui/theme/
  ```

### 🧪 Tests

Philosophie

- Tests KMP compatibles Android + iOS
- Pas de dépendance Android (JUnit, Dispatchers.Main, etc.)
- Pas de MockK (non compatible iOS natif)
- Utilisation de fakes simples

### Emplacement

```shell
  composeApp/src/commonTest/
  ```

### Tests couverts

- Use cases (GetBanksUseCase)
- Repositories
- Stores (BanksStore, OperationsStore)
- Mappers
- Utils

Exemple :
```shell
  @Test
  fun `Given usecase returns banks When store init Then UI state is populated`() { ... }
  ```

### 🌍 Multiplatform
Plateforme
- Android	✅
- iOS Simulator	✅
- iOS Device	✅

### Réseau

- Ktor
- Engine Android : OkHttp
- Engine iOS : Darwin

### ⚙️ Build & Run

### Android
```shell
  ./gradlew :composeApp:installDebug
  ```
### iOS
- Ouvrir iosApp/iosApp.xcodeproj
- Lancer sur simulateur ou device

### ⚠️ Notes importantes

- ui-tooling-preview uniquement côté Android
- Aucune dépendance Android dans commonMain
- Les Dispatchers sont injectés dans les stores pour les tests
- Architecture prête pour :

  - pagination
  - cache local
  - navigation plus complexe

###   📌 Choix techniques

- Kotlin Multiplatform
- Compose Multiplatform
- Material 3
- Ktor
- Coroutines / Flow
- Clean Architecture
- Tests multiplateformes

### 🚀 Améliorations possibles

- Cache local (SQLDelight)
- Design tokens
- Analytics
- Deep links
- Mode offline
- Pagination des opérations

### Démo Android et Screen IOS

https://github.com/user-attachments/assets/6ffee437-e9b0-4042-bd05-f02b9a962eac

<img width="300" height="600" alt="Simulator Screenshot - iPhone 17 - 2026-02-10 at 18 12 10" src="https://github.com/user-attachments/assets/49f3d11e-3d7c-4af1-b7b3-53460693e67c" />
<img width="300" height="600" alt="Simulator Screenshot - iPhone 17 - 2026-02-10 at 18 12 17" src="https://github.com/user-attachments/assets/a7743b7d-d5b6-42f8-b812-3d4507976163" />
<img width="300" height="600" alt="Simulator Screenshot - iPhone 17 - 2026-02-10 at 18 12 32" src="https://github.com/user-attachments/assets/8d0fe007-ad78-4736-9826-a5979b7dfcd6" />
<img width="300" height="600" alt="Simulator Screenshot - iPhone 17 - 2026-02-10 at 18 12 37" src="https://github.com/user-attachments/assets/fd2b9884-538a-455b-9caf-e91a14bbd158" />




### 👤 Auteur

Djibril Diop

Senior Kotlin / Android / KMP Developer
