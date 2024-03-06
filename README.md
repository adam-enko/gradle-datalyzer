# Gradle Project Data Extractor

Gradle Project Data Extractor (GPDE) is an _experimental_ tool for gathering information about a Gradle project.

It is used to answer questions about a Gradle project in a:

Required information:

1. How many Kotlin/Groovy scripts used in the build?
2. Gradle version ✅
3. Kotlin version ✅
4. Java version ✅
5. project structure 
   - how many subprojects? ✅
   - what is dependency graph? ✅
6. How much code is generated? ✅
7. Convention plugins location (none, remote, local?) ✅
8. "Custom logic" (maybe we can measure cyclomatic complexity?) ✅
9. Task graph for typical builds, tests, local dev, CI
10. Are there custom lifecycle tasks? 🟠(can see all tasks, but it might not be clear which are custom or not)
11. What Gradle features are enabled?
    * build cache (local or remote?) (🟠 can be inferred)
    * config cache
    * build scans
    * parallel
    * Test parallelization
12. Dependency management: ❌
    * version catalog
    * BOMs
    * java-platform
    * dynamic versioning
    * dependency locking
13. What plugins are applied? ✅
14. Dependency graph (how many) 🟠 not sure
15. What dependency repositories are defined? In what order? ✅
