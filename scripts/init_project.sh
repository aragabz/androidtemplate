#!/bin/bash
# Run in "strict" mode: exit on error, unset var, or pipe failure.
set -euo pipefail

if [ "$#" -lt 4 ]; then
    echo "Usage: ./init_project.sh <new_package_name> <new_feature_name> <new_project_name> <target_path>"
    echo "Example: ./init_project.sh com.example.myapp tasks MyCoolApp ~/AndroidProjects/MyCoolApp"
    exit 1
fi

NEW_PACKAGE=$1
NEW_FEATURE=$2
NEW_PROJECT_NAME=$3
TARGET_PATH=$4

# Validate inputs early to avoid destructive operations halfway through.
if ! [[ "$NEW_FEATURE" =~ ^[a-z][a-zA-Z0-9]*$ ]]; then
    echo "Error: New feature name must be lowerCamelCase (e.g. tasks, userProfile). Got: $NEW_FEATURE" >&2
    exit 1
fi
if ! [[ "$NEW_PACKAGE" =~ ^[a-z][a-z0-9_]*(\.[a-z][a-z0-9_]*)*$ ]]; then
    echo "Error: Package name must be dot-separated lowerCamelCase, e.g. com.example.myapp. Got: $NEW_PACKAGE" >&2
    exit 1
fi
if [ -z "$NEW_PROJECT_NAME" ]; then
    echo "Error: Project name must not be empty." >&2
    exit 1
fi

# Resolve template directory (parent of where this script lives)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
TEMPLATE_DIR="$( cd "$SCRIPT_DIR/.." && pwd )"
OLD_PACKAGE="com.aragabz.androidtemplate"
OLD_FEATURE="todos"
OLD_PROJECT_NAME="AndroidTemplate"

OLD_PKG_PATH=$(echo "$OLD_PACKAGE" | tr '.' '/')
NEW_PKG_PATH=$(echo "$NEW_PACKAGE" | tr '.' '/')

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

# Copy files excluding build artifacts, git metadata, and helper scripts.
if command -v rsync >/dev/null 2>&1; then
    rsync -a \
        --exclude='.git' \
        --exclude='.gradle' \
        --exclude='.idea' \
        --exclude='build' \
        --exclude='.agent' \
        --exclude='scripts/' \
        --exclude='*.sh' \
        --exclude='local.properties' \
        --exclude='.env' \
        "$TEMPLATE_DIR/" "$TARGET_PATH/"
else
    cp -R "$TEMPLATE_DIR/." "$TARGET_PATH/"
    rm -rf "$TARGET_PATH/.git" "$TARGET_PATH/.gradle" "$TARGET_PATH/.idea" "$TARGET_PATH/build" "$TARGET_PATH/.agent" "$TARGET_PATH/scripts"
    rm -f "$TARGET_PATH/init_project.sh" "$TARGET_PATH/create_feature.sh"
    rm -f "$TARGET_PATH/local.properties" "$TARGET_PATH/.env"
fi

cd "$TARGET_PATH" || exit 1

# 2. Global text replacement
echo "Replacing text in files..."
LAST_PART=$(echo "$NEW_PACKAGE" | rev | cut -d. -f1 | rev)

find . -type f \( -name "*.kt" -o -name "*.java" -o -name "*.xml" -o -name "*.kts" -o -name "*.md" -o -name "*.pro" -o -name "*.properties" -o -name "*.toml" -o -name "*.yml" -o -name "*.yaml" \) \
    -not -path "*/.*" -not -path "*/build/*" -print0 | xargs -0 perl -pi -e "
    s/\Q$OLD_PACKAGE\E/$NEW_PACKAGE/g;
    s/\Q$OLD_PROJECT_NAME\E/$NEW_PROJECT_NAME/g;
    s/\bandroidtemplate\b/$LAST_PART/g;
    s/:feature:\Q$OLD_FEATURE\E/:feature:$NEW_FEATURE/g;
    s/\.feature\.\Q$OLD_FEATURE\E/.feature.$NEW_FEATURE/g;
    s/\b\Q$OLD_FEATURE\E\b/$NEW_FEATURE/g;
    s/\b$(printf '%s' "${OLD_FEATURE:0:1}" | tr '[:lower:]' '[:upper:]')${OLD_FEATURE:1}\b/$(printf '%s' "${NEW_FEATURE:0:1}" | tr '[:lower:]' '[:upper:]')${NEW_FEATURE:1}/g;
"

# 3. Rename feature module directory
echo "Renaming feature module..."
if [ -d "feature/$OLD_FEATURE" ] && [ "$OLD_FEATURE" != "$NEW_FEATURE" ]; then
    mv "feature/$OLD_FEATURE" "feature/$NEW_FEATURE"
fi

# 4. Rename package directories (deepest-first so nested matches still exist when renamed)
echo "Renaming package directories..."
find . -type d -depth -path "*/$OLD_PKG_PATH" -print0 | while IFS= read -r -d '' dir; do
    BASE_SRC_DIR=${dir%/$OLD_PKG_PATH}
    NEW_DIR="$BASE_SRC_DIR/$NEW_PKG_PATH"

    # Safety: never copy into itself (would delete the tree afterwards).
    if [ "$dir" = "$NEW_DIR" ]; then
        continue
    fi

    mkdir -p "$NEW_DIR"
    cp -R "$dir"/. "$NEW_DIR"/
    rm -rf "$dir"
done

# 5. Specifically handle the renamed feature's internal package paths (domain, data, ui)
echo "Handling feature sub-packages..."
find . -type d -depth -name "$OLD_FEATURE" -print0 2>/dev/null | { grep -z "feature/$NEW_FEATURE" 2>/dev/null || true; } | while IFS= read -r -d '' dir; do
    PARENT=$(dirname "$dir")
    TARGET_DIR="$PARENT/$NEW_FEATURE"

    if [ "$dir" = "$TARGET_DIR" ]; then
        continue
    fi

    mkdir -p "$TARGET_DIR"
    cp -R "$dir"/. "$TARGET_DIR"/
    rm -rf "$dir"
done

# 6. Rename files containing feature name
echo "Renaming files containing feature name..."
OLD_FEATURE_CAP="$(tr '[:lower:]' '[:upper:]' <<< "${OLD_FEATURE:0:1}")${OLD_FEATURE:1}"
NEW_FEATURE_CAP="$(tr '[:lower:]' '[:upper:]' <<< "${NEW_FEATURE:0:1}")${NEW_FEATURE:1}"

find . -type f \( -name "*$OLD_FEATURE*" -o -name "*$OLD_FEATURE_CAP*" \) -not -path "*/.*" -not -path "*/build/*" -print0 | while IFS= read -r -d '' file; do
    NEW_FILE=$(echo "$file" | sed "s/$OLD_FEATURE_CAP/$NEW_FEATURE_CAP/g" | sed "s/$OLD_FEATURE/$NEW_FEATURE/g")
    if [ "$file" != "$NEW_FILE" ] && [ ! -e "$NEW_FILE" ]; then
        mv "$file" "$NEW_FILE"
    fi
done

echo -e "\nProject successfully initialized at: $TARGET_PATH"
echo "Next steps:"
echo "1. Open the project in Android Studio from $TARGET_PATH"
echo "2. Sync Gradle and Build"