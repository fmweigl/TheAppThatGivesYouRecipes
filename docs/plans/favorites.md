# Plan: "Favorites" feature

Branch: `feature/favorites`. Status: steps 1–6 of the commit order done.

Goal: the user marks recipes as favorites with a button; favorites are saved in a Room database; a new bottom
navigation tab "Favorites" lists teasers of them; a teaser can be removed (deleted from the database); clicking a
teaser shows the full recipe.

Since `master` releases every build (GitHub Release + optional Play upload), keep the branch rebased onto `master`
and merge only once the whole feature is done and verified.

## Decisions (confirmed 2026-10-02)

- Removal: deletes immediately, with an undo snackbar (no confirmation dialog).
- Un-favoriting on a recipe screen: the screen stays open and the heart can re-add the recipe; it's gone from the
  favorites list after going back.
- Android backup: the database is **excluded** from Android's backup; favorites stay strictly on the device.
- Modularization: the recipe is the app's central feature. `feature/randomrecipe` is renamed to `feature/recipe`
  and owns everything about a single recipe (random or by id, the database, the heart). `feature/favorites` is
  only a list UI that opens recipes by id through navigation.

## 1. Structure

```
feature/recipe/          (renamed from randomrecipe, extended)
  domain   Recipe, Ingredient, RecipeRepository, FavoritesRepository
  data     TheMealDB API + Room database          (meals.kmp.room, new plugin)
  ui       RandomRecipeNavKey (random tab) + RecipeNavKey(id), heart button
feature/favorites/
  ui       the teaser list only                   (like about: only the layer it needs)
```

| Module | Plugin | Depends on (project modules) | Change |
|---|---|---|---|
| `:core:domain` | `meals.kmp.domain` | none | + `DataError.Storage`, `DataError.NotFound` |
| `:core:data` | `meals.kmp.data` | `core:domain` (api) | none |
| `:core:network` | `meals.kmp.data` | none | none |
| `:core:designsystem` | `meals.kmp.compose` | none | + `BackTopAppBar` (moved from about), Compose resources ("Back") |
| `:feature:recipe:domain` | `meals.kmp.domain` | `core:domain` (api) | renamed; `RecipeRepository` with `getRecipe(id)`; + `FavoritesRepository` |
| `:feature:recipe:data` | `meals.kmp.room` | `recipe:domain`, `core:data`, `core:network` | renamed; + Room, `lookup.php?i=`, database-first `getRecipe(id)` |
| `:feature:recipe:ui` | `meals.kmp.feature.ui` | `recipe:domain`, `core:designsystem` (plugin) | renamed; + `RecipeNavKey(id)`, `RecipeScreen`, heart |
| `:feature:favorites:ui` | `meals.kmp.feature.ui` | `recipe:domain`, `core:designsystem` (plugin) | new |
| `:feature:about:ui` | `meals.kmp.feature.ui` | `core:designsystem` (plugin) | uses `BackTopAppBar` from the design system |
| `:composeApp` | (direct) | feature `ui` modules, `recipe:data`, `core:network`, `core:designsystem` | + favorites tab, nav keys, Koin modules |
| `:androidApp` | `com.android.application` | `composeApp`; debug: `recipe:domain` | + backup rules; screenshot mode adapted |
| `:desktopApp` | `kotlin("jvm")` | `composeApp` | none |

- The only dependency between features is `favorites:ui → recipe:domain`, pointing toward the central feature.
  `recipe` depends on no other feature.
- Only `:composeApp` sees all features; it connects them through navigation (`onOpenRecipe(id)` →
  `RecipeNavKey(id)`).
- `recipe:data`'s only public declaration stays its Koin module; UI modules see only `recipe:domain`'s interfaces.
- Only `recipe:data` gets an `android` target among the data modules (Room needs a `Context` and Android-specific
  generated code); the others stay jvm + iOS.
- Future list features (search, categories, ...) follow the same pattern: own `domain`/`data`/`ui`, depend on
  `recipe:domain`, open recipes via `RecipeNavKey(id)`.
- If another feature ever needs its own tables, move the database to `:core:database`.

## 2. Rename `randomrecipe` → `recipe` (pure refactor, first commit)

- `git mv feature/randomrecipe feature/recipe`; `settings.gradle.kts`; project accessors
  (`projects.feature.recipe.*`) in `composeApp` and `androidApp`.
- Packages `…yetanothermealsapp.randomrecipe.{domain,data,ui}` → `…yetanothermealsapp.recipe.{domain,data,ui}`,
  including tests and `packageOfResClass = "…recipe.ui.resources"`; Android namespaces.
- `RandomRecipeRepository` → `RecipeRepository` (still only `getRandomRecipe()` in this commit),
  `RandomRecipeRepositoryImpl` → `RecipeRepositoryImpl`, Koin modules `randomRecipeDataModule` → `recipeDataModule`,
  `randomRecipeUiModule` → `recipeUiModule`. The random screen's names stay (`RandomRecipeNavKey`,
  `RandomRecipeRoute`/`Screen`/`ViewModel`/`UiState`, `randomRecipeEntry()`), since they describe the random tab.
- `androidApp` debug: `DebugModules.kt`, `ScreenshotRecipeRepository.kt`, `ScreenshotRecipes.kt`.
- Docs: CLAUDE.md, README.md (module list and test commands); the detekt config comment can stay.
- Check: `./gradlew detekt jvmTest :androidApp:assembleDebug` passes with no behavior change.

## 3. `BackTopAppBar` into `:core:designsystem`

- Move "Android resources enabled + `components-resources`" from `meals.kmp.feature.ui` into `meals.kmp.compose`,
  so the design system can have strings; set its `packageOfResClass`
  (`…core.designsystem.resources`).
- Move `BackTopAppBar` and its "Back" string from `:feature:about:ui`; it becomes the design system's second public
  declaration (update CLAUDE.md's "its only public declaration").

## 4. Build logic for Room

- Catalog: versions `room3` (3.0.3), `sqlite` (2.7.1, the version room3 depends on), `ksp` (2.3.12; works with
  Kotlin 2.4.20 and AGP 9.1); libraries `room3-runtime`, `room3-compiler`, `sqlite-bundled`; plugins `ksp`,
  `room3`. Both plugins also go into `build-logic` as `compileOnly`.
- New convention plugin `meals.kmp.room` (a module still applies exactly one plugin):
  - everything `meals.kmp.data` does, plus `com.android.kotlin.multiplatform.library` with
    `androidLibraryDefaults()`;
  - KSP and the Room plugin with `room { schemaDirectory("$projectDir/schemas") }` (schemas committed);
  - `room3-runtime` and `sqlite-bundled` in commonMain;
  - `room3-compiler` on `kspAndroid`, `kspJvm`, `kspIosArm64`, `kspIosSimulatorArm64`.
- `recipe:data` switches from `meals.kmp.data` to `meals.kmp.room`. Its Android consumers now get the `android`
  variant instead of `jvm`.
- Detekt: generated KSP code is under `build/`, detekt scans only `src/`; nothing to change.

## 5. `:feature:recipe:domain`

```kotlin
interface RecipeRepository {
    suspend fun getRandomRecipe(): Result<Recipe, DataError>
    /** From the database if saved, otherwise from TheMealDB. */
    suspend fun getRecipe(id: String): Result<Recipe, DataError>
}

interface FavoritesRepository {
    fun observeFavorites(): Flow<Result<List<Recipe>, DataError>>   // newest first; Storage on read errors
    fun observeIsFavorite(recipeId: String): Flow<Boolean>          // false on read errors
    suspend fun addFavorite(recipe: Recipe): Result<Unit, DataError>
    suspend fun removeFavorite(recipeId: String): Result<Unit, DataError>
}
```

- `:core:domain`: `DataError.Storage` (database errors) and `DataError.NotFound` (`lookup.php` returns no meal);
  `DataError.toMessage()` gets cases for both.
- Depends on `kotlinx-coroutines-core` for `Flow` (allowed in domain modules).

## 6. `:feature:recipe:data`

- Network: `getRecipe(id)` calls `lookup.php?i=<id>` and reuses `MealMapper`; `{"meals": null}` →
  `DataError.NotFound`. `MockEngine` tests: found, not found, errors.
- Room:
  - table `favorite_recipe`: `id` (PK, TheMealDB id), `name`, `category`, `area`, `instructions`, `imageUrl`,
    `youtubeUrl`, `sourceUrl`, `tags`, `ingredients`, `savedAt: Long`; `tags` and `ingredients` as JSON via a
    `@TypeConverter` with kotlinx.serialization (never queried, keeps order, no extra table);
  - `FavoriteRecipeDao`: `@Upsert`; `@Query DELETE WHERE id`; `Flow<List<…>> ORDER BY savedAt DESC`;
    `Flow<Boolean> SELECT EXISTS(…)`; `suspend getById`;
  - `RecipeDatabase`: `@Database(version = 1, exportSchema = true)`,
    `@ConstructedBy(RecipeDatabaseConstructor::class)` (Room generates the `expect object`'s actuals);
  - location per platform, via an `expect`/`actual` Koin module providing the `RoomDatabase.Builder`:
    - Android: `get<Context>().getDatabasePath("recipes.db")` (`androidContext()` is set in `MealsApplication`);
    - JVM/Desktop: an app data directory (`~/.local/share/YetAnotherMealsApp`, `%APPDATA%`,
      `~/Library/Application Support`);
    - iOS: `NSDocumentDirectory`;
    - all: `.setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO)`.
  - `FavoriteRecipeMapper.kt`: `Recipe` ↔ entity.
- `RecipeRepositoryImpl.getRecipe(id)`: the DAO first, then the network.
- `FavoritesRepositoryImpl`: writes wrapped in a `safeDbCall` mapping `SQLiteException` to `DataError.Storage`,
  rethrowing `CancellationException`.
- Keep files separate (API calls, DAO/database, the two repositories, mappers); the only public declaration stays
  `recipeDataModule` (database `single`, DAO, both repository bindings).
- Tests (jvmTest, in-memory Room via `Room.inMemoryDatabaseBuilder` + `BundledSQLiteDriver`): round trip with
  ingredients and tags, ordering, `observeIsFavorite` updates, remove, upsert of an existing id, `getRecipe` from
  the database without a request (a `MockEngine` that fails if called) and from the network otherwise; Koin module
  test.

## 7. `:feature:recipe:ui`

- Shared, internal: `RecipeDetails` (image, title row, ingredients, instructions) gets a title-row slot for the
  heart; `FavoriteButton` (`IconToggleButton`, `Icons.Filled.Favorite` / `Icons.Filled.FavoriteBorder` from
  material-icons-core, `contentDescription` "Favorite", on/off state via toggleable semantics).
- Random tab (`RandomRecipeNavKey`, tab root), as today plus the heart:
  - `RandomRecipeUiState` gets `isFavorite: Boolean`;
  - `RandomRecipeViewModel` gets `FavoritesRepository`, keeps a `MutableStateFlow<Recipe?>` of the shown recipe,
    `flatMapLatest { observeIsFavorite(it.id) }`, combined into `uiState`; `toggleFavorite()` adds or removes.
    The heart always reflects the database, so a failed write leaves it unchanged;
  - Previous/Next bar unchanged.
- Recipe by id (`RecipeNavKey(val recipeId: String)`, pushed onto the current tab's stack):
  - `RecipeRoute` / `RecipeScreen` / `RecipeViewModel` (`recipeId` via `koinViewModel { parametersOf(...) }`);
  - loads the recipe once with `getRecipe(id)`, observes `isFavorite` separately (un-favoriting keeps the screen,
    the heart can re-add the loaded recipe);
  - `BackTopAppBar` (recipe name; while loading or on error a generic title) with back arrow, then `RecipeDetails`;
    no bottom bar;
  - states: loading, error with retry, not found ("Recipe not found").
- Public declarations: `RandomRecipeNavKey`, `RecipeNavKey`, the entry registration
  `recipeEntries(onBack: () -> Unit)` (replaces `randomRecipeEntry()`), `recipeUiModule`.
- Tests: ViewModels with fakes (random: toggling, state follows the shown recipe when going back and forth;
  by id: success, not found, error, toggle); accessibility (`RandomRecipeScreenAccessibilityTest` extended with the
  heart; a test for `RecipeScreen`: heading, back button label, `paneTitle` for errors).

## 8. `:feature:favorites:ui`

- `FavoritesNavKey` (`@Serializable data object`, tab root); `favoritesEntry(onOpenRecipe: (String) -> Unit)`;
  the feature never navigates itself and never imports `recipe:ui`.
- `FavoritesRoute` / `FavoritesScreen` / `FavoritesViewModel` (observes `FavoritesRepository`):
  - UI state `Loading | Empty | Content(List<RecipeTeaser>)`, UI model `RecipeTeaser(id, name, subtitle, imageUrl)`;
  - teaser `Card`: thumbnail (Coil, decorative), name (`titleMedium`), "Category · Area", `IconButton` with
    `Icons.Filled.Delete` and `contentDescription` "Remove ‹name› from favorites"; card click → `onOpenRecipe(id)`;
    row is `mergeDescendants`;
  - removal deletes immediately; snackbar "Removed ‹name›" with Undo (re-inserts the recipe kept in memory);
    own `Scaffold` with `TopAppBar` "Favorites" and `SnackbarHost`;
  - empty state: "No favorites yet. Tap the heart on a recipe to save it."
- `favoritesUiModule`: `viewModelOf(::FavoritesViewModel)`. Strings in its own `composeResources`,
  `packageOfResClass = "…favorites.ui.resources"`. Coil as in `recipe:ui`.
- Tests: ViewModel with a fake repository (mapping, remove + undo); jvmTest accessibility (heading, merged teaser
  row, remove button label, `paneTitle` for the empty state).

## 9. Wiring in `:composeApp` and `:androidApp`

- `topLevelDestinations`: `FavoritesNavKey → (Icons.Filled.Favorite, Res.string.tab_favorites)` between
  "Random" and "About".
- `navKeyConfiguration`: register `RecipeNavKey` and `FavoritesNavKey`.
- Entry provider: `recipeEntries(onBack = navigator::goBack)`,
  `favoritesEntry(onOpenRecipe = { navigator.navigate(RecipeNavKey(it)) })`.
- `initKoin()`: `recipeDataModule`, `recipeUiModule`, `favoritesUiModule`.
- Dependency on `favorites:ui`; `settings.gradle.kts` includes it.
- `androidApp`:
  - backup: keep `allowBackup="true"` for the rest, exclude the database:
    `android:dataExtractionRules="@xml/data_extraction_rules"` (API 31+; in `<cloud-backup>` and
    `<device-transfer>`) and `android:fullBackupContent="@xml/backup_rules"` (API 24–30), excluding
    `domain="database"` paths `recipes.db`, `recipes.db-wal`, `recipes.db-shm`; verify with
    `adb shell bmgr backupnow io.github.fmweigl.yetanothermealsapp`;
  - screenshot mode: the fake `RecipeRepository` also implements `getRecipe(id)` from the screenshot recipes.

## 10. Docs and listing (same branch, per CLAUDE.md)

- `PRIVACY.md`: favorites are stored only on the device, are not part of Android's backup, never leave the device
  and are deleted with the app data or on uninstall; recipes opened from favorites may still load images (and, if
  not saved, recipe data) from TheMealDB; update "Last updated".
- `CLAUDE.md` and `README.md`: the `recipe` feature (rename, both nav keys, database), `favorites:ui`,
  `meals.kmp.room`, `BackTopAppBar` in the design system, the `favorites:ui → recipe:domain` dependency, schema
  export, test commands.
- Store listing (needs the user's sign-off): `TAGLINE` in `render_store_graphics.py` (re-render),
  `short_description.txt`, `full_description.txt`, likely a favorites screenshot (screenshot mode with a
  pre-filled favorites list).

## 11. Verification

1. `./gradlew detekt jvmTest :androidApp:assembleDebug` passes after every commit.
2. Android and desktop: favorite and unfavorite on the random tab; favorites list, remove + undo; open a favorite
   (also offline); un-favorite on its screen and go back; tab switching keeps each stack; favorites survive a
   restart; restore after process death (`RecipeNavKey` has an argument).
3. Signed release build, same run-through (Room under R8 only fails at runtime).
4. iOS: build in Xcode, check the database is created in Documents (CI doesn't cover iOS).

## 12. Commit order

1. Rename `randomrecipe` → `recipe` (no behavior change).
2. `BackTopAppBar` into `:core:designsystem` (resources in `meals.kmp.compose`).
3. `getRecipe(id)` over the network (`lookup.php`), `DataError.NotFound`.
4. Build logic: catalog and `meals.kmp.room`.
5. Room database, `FavoritesRepository`, database-first `getRecipe(id)`, `DataError.Storage`, with tests.
6. `recipe:ui`: heart on the random tab, `RecipeNavKey` + `RecipeScreen`.
7. `favorites:ui`.
8. Wiring in `composeApp`, backup rules and screenshot mode in `androidApp`.
9. Privacy policy, CLAUDE.md, README, and (after sign-off) the store listing.
