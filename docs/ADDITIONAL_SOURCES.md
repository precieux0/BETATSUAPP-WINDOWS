Liste de sources recommandées et instructions d'intégration

But: Ce document rassemble des sources que l'on peut vouloir intégrer au projet et décrit les approches possibles pour les ajouter.

Sources recommandées
- anime-sama.store
- mangadex.org
- mangapark.net
- manganelo.com (aussi connu sous manga4life / manganelo)
- mangahere.cc
- tachiyomi extensions (voir référentiel Tachiyomi pour idées)

Approches d'intégration

1) Si un parser existe déjà (artifact Maven/JitPack/JAR)
- Ajouter la dépendance (Gradle) au module `app` ou à un module `parsers`.
- Créer une `MangaParserSource` entrée (ou étendre l'enum existant) pour exposer le parser.
- S'assurer que `MangaLoaderContext` et `MangaRepository` peuvent instancier le parser (via `loaderContext.newParserInstance(source)`).

2) Implémenter un parser local (squelette)
- Créer une classe qui implémente `org.koitharu.kotatsu.parsers.MangaParser`.
- Fournir l'implémentation minimale des méthodes (liste, détails, pages) ou stub avec exceptions si non supportées.
- Déclarer/registrer la source dans `MangaParserSource` (si accessible) ou exposer via un `ExternalMangaSource` provider.

3) Extension externe (APK)
- Créer une app séparée qui expose `app.kotatsu.parser.PROVIDE_MANGA` content provider.
- L'application principale détectera la présence via `getExternalSources()` et pourra utiliser le provider.

Étapes pratiques pour `anime-sama.store` (recommandé)
- Vérifier si un parser Tachiyomi ou autre existe (recherche GitHub). Si trouvé, préférer réutiliser.
- Sinon, créer un petit parser local `AnimeSamaParser` implémentant `MangaParser` et basique.
- Ajouter l'entrée source et incrémenter `BuildConfig.VERSION_CODE` si nécessaire pour forcer assimilation.

Notes et limitations
- `MangaParserSource` semble provenir du module `parsers`. Si l'enum est dans un module externe, il faudra soit modifier ce module, soit exposer la source via une extension externe.
- L'ajout de parsers non-testés peut casser la compilation si les interfaces changent; il est recommandé d'ajouter d'abord des stubs non référencés par l'enum avant d'essayer d'enregistrer officiellement la source.

Besoin d'action automatique effectuée ici
- J'ai listé les sources et décrit les actions concrètes pour les intégrer.
- Prochaine étape automatique possible: créer des squelettes de parser locaux dans `app/src/main/kotlin/.../parsers/` sans les enregistrer dans l'enum afin d'éviter des erreurs de compilation. Voulez-vous que je crée ces squelettes maintenant ?
 
Quick note: the app now supports adding custom catalog URLs at runtime.
- Use the settings -> Manga sources -> Add custom source and paste the exact URL (the app creates a `CUSTOM:` source and enables it).
