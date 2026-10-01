# Plan: "Favorites" feature

Branch: `feature/favorites`. Status: planned, nothing implemented yet.

Goal: the user marks recipes from `randomrecipe` as favorites with a button; favorites are saved in a
Room database; a new bottom navigation tab "Favorites" lists teasers of them; a teaser can be removed
(deleted from the database); clicking a teaser shows the full recipe.

## Open decisions (confirm before implementing)

- Removal: undo snackbar (proposed, removes immediately) or a confirmation dialog?
- Un-favoriting on the detail screen: screen stays open and the heart can re-add it (proposed), or close it?
- Android backup (`android:allowBackup="true"`): mention favorites in `PRIVACY.md` (proposed) or exclude the database with backup rules?

## 1. Main decisions

1. **Store the whole recipe in Room, not just its id.** The detail screen works offline and needs no extra
   API call (`lookup.php?i=`). Images still load through Coil's cache or the network.
2. **Shared recipe model and detail UI.** Both features show a full recipe, but `Recipe` and `RecipeDetails`
   belong to `randomrecipe` today. Move them into `core` so neither feature's UI depends on the other's.
3. **The favorites data module needs a real `android` target.** Data modules are jvm + iOS only and Android
   uses their `jvm` variant; Room on Android needs a `Context` and Android-specific generated code.
4. **Room 3** (`androidx.room3`, latest stable 3.0.3): KMP-first, KSP-only, coroutines only,
   `BundledSQLiteDriver` on every platform.

## 2. Module changes

```
core/
  domain       + Recipe, Ingredient (moved from randomrecipe:domain)
  ui           NEW (meals.kmp.compose + resources): RecipeDetails, BackTopAppBar (moved from about)
feature/
  randomrecipe/domain   only RandomRecipeRepository left
  randomrecipe/ui       + favorite toggle; depends on favorites:domain
  favorites/domain      NEW  meals.kmp.domain
  favorites/data        NEW  meals.kmp.room (new plugin)
  favorites/ui          NEW  meals.kmp.feature.ui
```

The one cross-feature link: `randomrecipe:ui` depends on `favorites:domain` (its interface only).
Document it in CLAUDE.md.

## 3. Build logic

- Catalog: versions `room3`, `sqlite` (androidx.sqlite), `ksp` (check which KSP works with Kotlin 2.4.20 and
  AGP 9.1); libraries `room3-runtime`, `room3-compiler`, `sqlite-bundled`; plugins `ksp`, `room3`. Both plugins
  also go into `build-logic` as `compileOnly`.
- New convention plugin `meals.kmp.room` for data modules with a database (still exactly one plugin per module):
  - everything `meals.kmp.data` does, plus `com.android.kotlin.multiplatform.library` with `androidLibraryDefaults()`;
  - KSP and the Room plugin with `room { schemaDirectory("$projectDir/schemas") }` (schemas committed);
  - `room3-runtime` and `sqlite-bundled` in commonMain;
  - `room3-compiler` on `kspAndroid`, `kspJvm`, `kspIosArm64`, `kspIosSimulatorArm64`.
- Move "Android resources enabled + `components-resources`" from `meals.kmp.feature.ui` into `meals.kmp.compose`,
  so `:core:ui` can have strings ("Ingredients", "Instructions", "Back").
- Detekt: generated KSP code is under `build/`, detekt scans only `src/`; nothing to change.

## 4. Shared code first (pure refactor, own commit)

1. `Recipe` and `Ingredient` move to `:core:domain` (package `…core.domain.recipe`). Update imports in
   randomrecipe and in `androidApp`'s debug `ScreenshotRecipes.kt`.
2. `:core:ui` gets:
   - `RecipeDetails` and `SectionTitle` from `RandomRecipeScreen.kt`; `RecipeDetails` gets a
     `titleAction: @Composable () -> Unit = {}` slot (for the heart) and an optional `paneTitle` flag (the detail
     screen doesn't need the "a new recipe appeared" announcement);
   - `BackTopAppBar` from `:feature:about:ui`;
   - their strings.
3. `RandomRecipeScreenAccessibilityTest` must still pass unchanged.

## 5. `:feature:favorites:domain`

```kotlin
interface FavoritesRepository {
    fun observeFavorites(): Flow<List<Recipe>>          // newest first
    fun observeIsFavorite(recipeId: String): Flow<Boolean>
    suspend fun getFavorite(recipeId: String): Recipe?
    suspend fun addFavorite(recipe: Recipe): Result<Unit, DataError>
    suspend fun removeFavorite(recipeId: String): Result<Unit, DataError>
}
```

- Writes return `Result` ("repositories never throw"). Disk errors map to a new `DataError.Storage`;
  `DataError.toMessage()` gets a case for it.
- Depends on `kotlinx-coroutines-core` for `Flow` (allowed in domain modules).

## 6. `:feature:favorites:data` (Room)

- Table `favorite_recipe`: `id` (PK, TheMealDB id), `name`, `category`, `area`, `instructions`, `imageUrl`,
  `youtubeUrl`, `sourceUrl`, `tags`, `ingredients`, `savedAt: Long`. `tags` and `ingredients` stored as JSON via a
  `@TypeConverter` with kotlinx.serialization (never queried, keeps order, no extra table).
- `FavoriteRecipeDao`: `@Upsert`; `@Query DELETE WHERE id`; `Flow<List<…>> ORDER BY savedAt DESC`;
  `Flow<Boolean> SELECT EXISTS(…)`; `suspend getById`.
- `FavoritesDatabase`: `@Database(version = 1, exportSchema = true)`,
  `@ConstructedBy(FavoritesDatabaseConstructor::class)` (Room generates the `expect object`'s actuals).
- Database location per platform, via an `expect`/`actual` Koin module `platformFavoritesDatabaseModule` that
  provides the `RoomDatabase.Builder`:
  - Android: `get<Context>().getDatabasePath("favorites.db")` (`androidContext()` is already set in `MealsApplication`);
  - JVM/Desktop: an app data directory (`~/.local/share/YetAnotherMealsApp`, `%APPDATA%`, `~/Library/Application Support`);
  - iOS: `NSDocumentDirectory`;
  - all: `.setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO)`.
- `FavoriteRecipeMapper.kt`: `Recipe` ↔ entity.
- `FavoritesRepositoryImpl`: writes wrapped in a `safeDbCall` mapping `SQLiteException` to `DataError.Storage`,
  rethrowing `CancellationException`.
- Only public declaration: `favoritesDataModule` (database `single`, DAO, `FavoritesRepository` binding).
- Tests (jvmTest, in-memory Room with `Room.inMemoryDatabaseBuilder` + `BundledSQLiteDriver`): round trip with
  ingredients and tags, ordering, `observeIsFavorite` updates, remove, upsert of an existing id; Koin module test
  like `RandomRecipeDataModuleTest`.

## 7. Favorite button in `randomrecipe`

- `RandomRecipeUiState` gets `isFavorite: Boolean`.
- ViewModel gets `FavoritesRepository`; keeps a `MutableStateFlow<Recipe?>` of the shown recipe,
  `flatMapLatest { observeIsFavorite(it.id) }`, combined into `uiState`. `toggleFavorite()` adds or removes.
  The heart always reflects the database, so a failed write leaves it unchanged.
- Screen: `IconToggleButton` (`Icons.Filled.Favorite` / `Icons.Filled.FavoriteBorder`, in material-icons-core)
  in `RecipeDetails`' title slot; `contentDescription` "Favorite", on/off state via toggleable semantics.
- Tests: ViewModel with a fake repository (toggling, state follows the shown recipe when going back and forth);
  accessibility test (toggle exists, labeled, reports its state).

## 8. `:feature:favorites:ui`

- Nav keys (`@Serializable`): `FavoritesNavKey` (data object, tab root), `FavoriteRecipeNavKey(val recipeId: String)`.
- `favoritesEntries(onNavigate: (NavKey) -> Unit, onBack: () -> Unit)`, like `aboutEntries`; the feature never
  navigates itself.
- List (`FavoritesRoute` / `FavoritesScreen` / `FavoritesViewModel`):
  - UI state `Loading | Empty | Content(List<RecipeTeaser>)`, UI model `RecipeTeaser(id, name, subtitle, imageUrl)`;
  - teaser `Card`: thumbnail (Coil, decorative), name (`titleMedium`), "Category · Area", `IconButton` with
    `Icons.Filled.Delete` and `contentDescription` "Remove ‹name› from favorites"; card click opens the detail;
    row is `mergeDescendants`;
  - removal deletes immediately, snackbar "Removed ‹name›" with Undo (re-inserts the recipe kept in memory);
    own `Scaffold` with `TopAppBar` "Favorites" and `SnackbarHost`;
  - empty state: "No favorites yet. Tap the heart on a recipe to save it."
- Detail (`FavoriteRecipeRoute` / `Screen` / `ViewModel`):
  - `recipeId` via `koinViewModel { parametersOf(key.recipeId) }`;
  - loads the recipe once with `getFavorite`, observes `isFavorite` separately (un-favoriting keeps the screen,
    the heart can re-add; it's gone from the list after going back);
  - `BackTopAppBar` (recipe name) + heart, then shared `RecipeDetails`;
  - not found: "Recipe not found" + back button.
- `favoritesUiModule`: `viewModelOf(::FavoritesViewModel)`, `FavoriteRecipeViewModel`.
- Strings in own `composeResources`, `packageOfResClass = "…favorites.ui.resources"`.
- Tests: ViewModels with a fake repository (list mapping, remove + undo, detail not found); jvmTest accessibility
  test (headings, merged teaser row, remove button label, `paneTitle` for the empty state).

## 9. Wiring in `:composeApp`

- `topLevelDestinations`: `FavoritesNavKey → (Icons.Filled.Favorite, Res.string.tab_favorites)` between
  "Random" and "About".
- `navKeyConfiguration`: register `FavoritesNavKey` and `FavoriteRecipeNavKey`.
- Entry provider: `favoritesEntries(onNavigate = navigator::navigate, onBack = navigator::goBack)`.
- `initKoin()`: add `favoritesDataModule`, `favoritesUiModule`.
- Dependencies on `favorites:ui` and `favorites:data`; `settings.gradle.kts` includes the new modules and `:core:ui`.

## 10. Docs and listing (same change, per CLAUDE.md)

- `PRIVACY.md`: favorites stored only on the device, deleted with app data or on uninstall; mention Android backup
  (see open decisions); update "Last updated".
- `CLAUDE.md`: new modules, `meals.kmp.room`, `:core:ui`, the `randomrecipe:ui → favorites:domain` dependency,
  schema export, test commands.
- Store listing (needs the user's sign-off): `TAGLINE` in `render_store_graphics.py` (re-render),
  `short_description.txt`, `full_description.txt`, likely a favorites screenshot (extend `debugModules()` with a
  favorites repository pre-filled with the two screenshot recipes).

## 11. Verification

1. `./gradlew detekt jvmTest :androidApp:assembleDebug` passes.
2. Android and desktop: favorite, unfavorite, remove + undo, open detail, tab switching keeps each stack,
   favorites survive a restart, restore after process death (`FavoriteRecipeNavKey` has an argument).
3. Signed release build, same run-through (Room under R8 only fails at runtime).
4. iOS: build in Xcode, check the database is created in Documents (CI doesn't cover iOS).

## 12. Commit order

1. Refactor: `Recipe` to `:core:domain`, `:core:ui` with `RecipeDetails` and `BackTopAppBar`.
2. Build logic: catalog and `meals.kmp.room`.
3. `favorites:domain` and `favorites:data` with tests.
4. `favorites:ui` with tests.
5. Favorite toggle in `randomrecipe`.
6. Navigation and Koin wiring in `composeApp`.
7. Privacy policy, CLAUDE.md, and (after sign-off) the store listing.
