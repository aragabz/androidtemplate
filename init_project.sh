#!/bin/bash

if [ "$#" -lt 4 ]; then
    echo "Usage: ./init_project.sh <new_package_name> <new_feature_name> <new_project_name> <target_path>"
    echo "Example: ./init_project.sh com.example.myapp tasks MyCoolApp ~/AndroidProjects/MyCoolApp"
    exit 1
fi

NEW_PACKAGE=$1
NEW_FEATURE=$2
NEW_PROJECT_NAME=$3
TARGET_PATH=$4

# Resolve template directory (where this script is located)
TEMPLATE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
OLD_PACKAGE="com.aragabz.androidtemplate"
OLD_FEATURE="todos"
OLD_PROJECT_NAME="AndroidTemplate"

OLD_PKG_PATH=$(echo $OLD_PACKAGE | tr '.' '/')
NEW_PKG_PATH=$(echo $NEW_PACKAGE | tr '.' '/')

echo "--- Project Initialization ---"
echo "Template: $TEMPLATE_DIR"
echo "Target Path: $TARGET_PATH"
echo "New Project Name: $NEW_PROJECT_NAME"
echo "New Package: $NEW_PACKAGE"
echo "New Feature: $NEW_FEATURE"
echo "------------------------------"

# 1. Create target directory and copy template
if [ -d "$TARGET_PATH" ]; then
    echo "Error: Target path already exists. Please provide a new path or delete the existing one."
    exit 1
fi

echo "Copying template to target path..."
mkdir -p "$TARGET_PATH"

# Copy files excluding build artifacts and git
if command -v rsync >/dev/null 2>&1; then
    rsync -a --exclude={'.git','.gradle','.idea','build','.agent','*.sh'} "$TEMPLATE_DIR/" "$TARGET_PATH/"
else
    cp -R "$TEMPLATE_DIR/." "$TARGET_PATH/"
    rm -rf "$TARGET_PATH/.git" "$TARGET_PATH/.gradle" "$TARGET_PATH/.idea" "$TARGET_PATH/build" "$TARGET_PATH/.agent"
    rm -f "$TARGET_PATH/init_project.sh"
fi

cd "$TARGET_PATH" || exit 1

# 2. Global text replacement
echo "Replacing text in files..."
LAST_PART=$(echo $NEW_PACKAGE | rev | cut -d. -f1 | rev)

find . -type f \( -name "*.kt" -o -name "*.java" -o -name "*.xml" -o -name "*.kts" -o -name "*.md" -o -name "*.pro" -o -name "*.properties" -o -name "gradlew" \) \
    -not -path "*/.*" -not -path "*/build/*" | xargs perl -pi -e "
    s/\Q$OLD_PACKAGE\E/$NEW_PACKAGE/g;
    s/\Q$OLD_PROJECT_NAME\E/$NEW_PROJECT_NAME/g;
    s/androidtemplate/$LAST_PART/g;
    s/:feature:\Q$OLD_FEATURE\E/:feature:$NEW_FEATURE/g;
    s/\.feature\.\Q$OLD_FEATURE\E/.feature.$NEW_FEATURE/g;
    s/\b\Q$OLD_FEATURE\E\b/$NEW_FEATURE/g;
    s/\b$(tr '[:lower:]' '[:upper:]' <<< ${OLD_FEATURE:0:1})${OLD_FEATURE:1}\b/$(tr '[:lower:]' '[:upper:]' <<< ${NEW_FEATURE:0:1})${NEW_FEATURE:1}/g;
"

# 3. Rename feature module directory
echo "Renaming feature module..."
if [ -d "feature/$OLD_FEATURE" ]; then
    mv "feature/$OLD_FEATURE" "feature/$NEW_FEATURE"
fi

# 4. Rename package directories
echo "Renaming package directories..."
find . -type d -path "*/$OLD_PKG_PATH" | while read -r dir; do
    BASE_SRC_DIR=${dir%/$OLD_PKG_PATH}
    NEW_DIR="$BASE_SRC_DIR/$NEW_PKG_PATH"

    mkdir -p "$NEW_DIR"
    cp -R "$dir/"* "$NEW_DIR/"
    rm -rf "$dir"
done

# 5. Specifically handle the renamed feature's internal package paths (domain, data, ui)
echo "Handling feature sub-packages..."
find . -type d -name "$OLD_FEATURE" | grep "feature/$NEW_FEATURE" | while read -r dir; do
    PARENT=$(dirname "$dir")
    TARGET_DIR="$PARENT/$NEW_FEATURE"
    
    mkdir -p "$TARGET_DIR"
    cp -R "$dir/"* "$TARGET_DIR/"
    rm -rf "$dir"
done

# 6. Rename files containing feature name
echo "Renaming files containing feature name..."
OLD_FEATURE_CAP="$(tr '[:lower:]' '[:upper:]' <<< ${OLD_FEATURE:0:1})${OLD_FEATURE:1}"
NEW_FEATURE_CAP="$(tr '[:lower:]' '[:upper:]' <<< ${NEW_FEATURE:0:1})${NEW_FEATURE:1}"

find . -type f \( -name "*$OLD_FEATURE*" -o -name "*$OLD_FEATURE_CAP*" \) -not -path "*/.*" -not -path "*/build/*" | while read -r file; do
    NEW_FILE=$(echo "$file" | sed "s/$OLD_FEATURE_CAP/$NEW_FEATURE_CAP/g" | sed "s/$OLD_FEATURE/$NEW_FEATURE/g")
    if [ "$file" != "$NEW_FILE" ]; then
        mv "$file" "$NEW_FILE"
    fi
done

echo -e "\nProject successfully initialized at: $TARGET_PATH"
echo "Next steps:"
echo "1. Open the project in Android Studio from $TARGET_PATH"
echo "2. Sync Gradle and Build"
