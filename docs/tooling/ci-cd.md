# CI/CD & Automation

## GitHub Actions

### CI Workflow (`.github/workflows/ci.yml`)

Triggered on pushes and PRs to `main`:

```yaml
Jobs:
  1. Architecture Validation  → ./gradlew assertModuleGraph
  2. Android Lint             → ./gradlew lintDebug
  3. Detekt                   → ./gradlew detektAll
  4. Unit Tests               → ./gradlew testDebugUnitTest
  5. Build                    → ./gradlew assembleDebug
```

**Artifacts uploaded:**
- SARIF reports → GitHub Code Scanning (Security tab)
- Lint XML reports → Build artifacts
- Detekt XML reports → Build artifacts

**Environment:**
- Ubuntu latest
- JDK 21 (Temurin)
- Gradle build caching enabled

## Dependabot

Automated dependency updates configured in `.github/dependabot.yml`:

| Ecosystem | Schedule | Scope |
|-----------|----------|-------|
| Gradle | Weekly | All Gradle dependencies |
| GitHub Actions | Weekly | CI action versions |

## Fastlane

Ruby-based build automation tool. Install with:

```bash
gem install bundler
bundle install
```

### Available Lanes

| Lane | Command | Purpose |
|------|---------|---------|
| `build_debug` | `bundle exec fastlane build_debug` | Build debug APK |
| `build_release` | `bundle exec fastlane build_release` | Build release APK |
| `build_bundle` | `bundle exec fastlane build_bundle` | Build release AAB |
| `test` | `bundle exec fastlane test` | Run unit tests |
| `lint_kotlin` | `bundle exec fastlane lint_kotlin` | Run ktlint check |
| `format_kotlin` | `bundle exec fastlane format_kotlin` | Auto-format code |
| `analyze` | `bundle exec fastlane analyze` | Run detekt |
| `quality` | `bundle exec fastlane quality` | All quality checks (ktlint + detekt + lint) |
| `ci` | `bundle exec fastlane ci` | Full CI pipeline (clean + all checks + tests) |

### Configuration

- **Fastfile:** `fastlane/Fastfile` — Lane definitions
- **Appfile:** `fastlane/Appfile` — App metadata (package name)
- **Gemfile:** Root `Gemfile` — Ruby dependencies

## Baseline Profiles

Performance optimization via the `:baselineprofile` module:

```bash
# Generate baseline profiles
./gradlew :baselineprofile:connectedBenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.class=BaselineProfileGenerator

# Run startup benchmarks
./gradlew :baselineprofile:connectedBenchmarkAndroidTest -P android.testInstrumentationRunnerArguments.class=StartupBenchmarks
```

Generated profiles are included in the release build for faster startup and reduced jank.

## Compose Metrics

Compose compiler metrics are configured for performance analysis:

```bash
./gradlew assembleRelease -P enableComposeCompilerReports=true
```

Reports output to `build/compose_metrics/` showing:
- Composable stability
- Skip-ability analysis
- Restart scopes

## Scripts

| Script | Purpose |
|--------|---------|
| `create_feature.sh` | Scaffolds a new feature with domain/data/ui modules |
| `init_project.sh` | Initializes a new project from this template (package renaming) |
