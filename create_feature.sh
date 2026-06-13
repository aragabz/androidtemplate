#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

# Check if feature name is provided
if [ -z "$1" ]; then
    echo "Usage: ./create_feature.sh <FeatureName>"
    echo "Example: ./create_feature.sh Profile"
    exit 1
fi

FEATURE_NAME=$1
# Lowercase for module name
MODULE_NAME=$(echo "$FEATURE_NAME" | tr '[:upper:]' '[:lower:]')
# Package name base
PACKAGE_BASE="com.aragabz.androidtemplate.feature.$MODULE_NAME"
# Base path
BASE_PATH="feature/$MODULE_NAME"

echo "🚀 Creating feature module: $MODULE_NAME (ui, data, domain)..."

# 1. Create directory structure
mkdir -p "$BASE_PATH/domain/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/domain/model"
mkdir -p "$BASE_PATH/domain/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/domain/repository"
mkdir -p "$BASE_PATH/domain/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/domain/usecase"

mkdir -p "$BASE_PATH/data/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/data/repository"
mkdir -p "$BASE_PATH/data/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/data/di"
mkdir -p "$BASE_PATH/data/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/data/local/entity"
mkdir -p "$BASE_PATH/data/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/data/local/dao"
mkdir -p "$BASE_PATH/data/src/main/resources/META-INF/services"

mkdir -p "$BASE_PATH/ui/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/ui/presentation/$MODULE_NAME"
mkdir -p "$BASE_PATH/ui/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/ui/presentation/navigation"

# 2. Create build.gradle.kts for each module

# DOMAIN
cat <<EOF > "$BASE_PATH/domain/build.gradle.kts"
plugins {
    id("androidtemplate.android.library")
}

android {
    namespace = "$PACKAGE_BASE.domain"
}

dependencies {
    api(project(":core:common"))
    api(project(":core:domain"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.javax.inject)
}
EOF

# DATA
cat <<EOF > "$BASE_PATH/data/build.gradle.kts"
plugins {
    id("androidtemplate.android.library")
    id("androidtemplate.android.hilt")
    id("androidtemplate.android.room")
}

android {
    namespace = "$PACKAGE_BASE.data"
}

dependencies {
    implementation(project(":feature:$MODULE_NAME:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.javax.inject)
}
EOF

# UI
cat <<EOF > "$BASE_PATH/ui/build.gradle.kts"
plugins {
    id("androidtemplate.android.feature")
    id("androidtemplate.android.hilt")
}

android {
    namespace = "$PACKAGE_BASE.ui"
}

dependencies {
    implementation(project(":feature:$MODULE_NAME:domain"))
    implementation(project(":feature:$MODULE_NAME:data"))
    
    implementation(project(":core:common"))
    implementation(project(":core:ui"))
    implementation(project(":core:navigation"))

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.navigation.compose)
}
EOF

# 3. Create initial files

# Entity and DAO
cat <<EOF > "$BASE_PATH/data/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/data/local/entity/${FEATURE_NAME}Entity.kt"
package $PACKAGE_BASE.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "${MODULE_NAME}s")
data class ${FEATURE_NAME}Entity(
    @PrimaryKey
    val id: String,
    val data: String
)
EOF

cat <<EOF > "$BASE_PATH/data/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/data/local/dao/${FEATURE_NAME}Dao.kt"
package $PACKAGE_BASE.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import $PACKAGE_BASE.data.local.entity.${FEATURE_NAME}Entity
import kotlinx.coroutines.flow.Flow

@Dao
interface ${FEATURE_NAME}Dao {
    @Upsert
    suspend fun upsert(entity: ${FEATURE_NAME}Entity)

    @Delete
    suspend fun delete(entity: ${FEATURE_NAME}Entity)

    @Query("SELECT * FROM ${MODULE_NAME}s")
    fun getAll(): Flow<List<${FEATURE_NAME}Entity>>
}
EOF

# EntityContributor
cat <<EOF > "$BASE_PATH/data/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/data/local/${FEATURE_NAME}EntityContributor.kt"
package $PACKAGE_BASE.data.local

import com.aragabz.androidtemplate.core.database.EntityContributor
import $PACKAGE_BASE.data.local.entity.${FEATURE_NAME}Entity
import kotlin.reflect.KClass

class ${FEATURE_NAME}EntityContributor : EntityContributor {
    override fun getEntities(): List<KClass<*>> = listOf(${FEATURE_NAME}Entity::class)
}
EOF

# Register service
echo "$PACKAGE_BASE.data.local.${FEATURE_NAME}EntityContributor" > "$BASE_PATH/data/src/main/resources/META-INF/services/com.aragabz.androidtemplate.core.database.EntityContributor"

# DI Module
cat <<EOF > "$BASE_PATH/data/src/main/kotlin/com/aragabz/androidtemplate/feature/$MODULE_NAME/data/di/${FEATURE_NAME}DataModule.kt"
package $PACKAGE_BASE.data.di

import $PACKAGE_BASE.data.repository.${FEATURE_NAME}RepositoryImpl
import $PACKAGE_BASE.domain.repository.${FEATURE_NAME}Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface ${FEATURE_NAME}DataModule {
    @Binds
    fun bind${FEATURE_NAME}Repository(impl: ${FEATURE_NAME}RepositoryImpl): ${FEATURE_NAME}Repository
}
EOF

# Note: Manual steps required:
# 1. Add abstract fun ${MODULE_NAME}Dao(): ${FEATURE_NAME}Dao to AppDatabase in :app module
# 2. Add ${FEATURE_NAME}Entity to entities list in AppDatabase in :app module
# 3. Add @Provides fun provide${FEATURE_NAME}Dao(db: AppDatabase): ${FEATURE_NAME}Dao = db.${MODULE_NAME}Dao() to DatabaseModule in :app module

# Navigation and UI files omitted for brevity in this log, but they follow the 3-module pattern.
# ... (rest of create_feature.sh) ...
