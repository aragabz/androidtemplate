# Core Modules Public API Audit
Generated: Wed Jul 29 00:23:57 EEST 2026

## :core/common

- Public classes/interfaces: 7
- Public functions: 16

**Key Public APIs:**
  - sealed class UiText {
  - sealed class AppError : Throwable() {
  - sealed class AppResult<out T> {
  - class LocalizationContextWrapper(
  - interface EntityContributor {
  - class ConnectivityManagerNetworkMonitor
  - interface NetworkMonitor {

## :core/network

- Public classes/interfaces: 8
- Public functions: 0

**Key Public APIs:**
  - class MockInterceptor
  - class ApiResultCallAdapterFactory(
  - class RetryInterceptor
  - class AuthInterceptor
  - class CachePolicyInterceptor
  - class SessionManager
  - class DefaultAuthTokenProvider
  - interface AuthTokenProvider {

## :core/database

- Public classes/interfaces: 2
- Public functions: 0

**Key Public APIs:**
  - interface BaseDao<T> {
  - class EntityContributor

## :core/datastore

- Public classes/interfaces: 6
- Public functions: 0

**Key Public APIs:**
  - class CachePolicyStoreImpl
  - interface UserPreferencesRepository {
  - class SecureSessionStorageImpl(
  - class UserPreferencesRepositoryImpl
  - interface CachePolicyStore {
  - interface SecureSessionStorage {

## :core/domain

- Public classes/interfaces: 0
- Public functions: 0

**Key Public APIs:**

## :core/ui

- Public classes/interfaces: 0
- Public functions: 10

**Key Public APIs:**

## :core/designsystem

- Public classes/interfaces: 0
- Public functions: 7

**Key Public APIs:**

## :core/navigation

- Public classes/interfaces: 1
- Public functions: 4

**Key Public APIs:**
  - sealed interface Route {

## :core/analytics

- Public classes/interfaces: 4
- Public functions: 0

**Key Public APIs:**
  - class TimberAnalyticsTracker
  - interface PerformanceMonitor {
  - interface AnalyticsTracker {
  - class DefaultPerformanceMonitor

## :core/sync

- Public classes/interfaces: 4
- Public functions: 0

**Key Public APIs:**
  - interface SyncModule {
  - interface SyncManager {
  - class WorkManagerSyncManager
  - class SyncWorker

## :core/crash

- Public classes/interfaces: 2
- Public functions: 0

**Key Public APIs:**
  - interface CrashReporter {
  - class TimberCrashReporter

## :core/flags

- Public classes/interfaces: 2
- Public functions: 0

**Key Public APIs:**
  - interface FeatureFlagManager {
  - class DebugFeatureFlagManager

