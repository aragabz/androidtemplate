#!/bin/bash
# Run in "strict" mode: exit on error, unset var, or pipe failure.
set -euo pipefail

if [ "$#" -lt 4 ] || [ "$#" -gt 5 ]; then
    echo "Usage: ./init_project.sh <new_package_name> <new_feature_name> <new_project_name> <target_path> [singular_feature_name]"
    echo "Example: ./init_project.sh com.example.myapp tasks MyCoolApp ~/AndroidProjects/MyCoolApp"
    echo "The sample feature 'todos' (singular 'todo') is renamed to <new_feature_name>. The singular form"
    echo "defaults to the feature name without a trailing 's' (tasks -> task)."
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
if ! [[ "$NEW_PROJECT_NAME" =~ ^[A-Za-z][A-Za-z0-9]*$ ]]; then
    echo "Error: Project name must be alphanumeric and start with a letter (it becomes class and theme names). Got: $NEW_PROJECT_NAME" >&2
    exit 1
fi

if [ "$#" -eq 5 ]; then
    NEW_FEATURE_SINGULAR=$5
elif [[ "$NEW_FEATURE" =~ [^s]s$ ]]; then
    NEW_FEATURE_SINGULAR=${NEW_FEATURE%s}
else
    NEW_FEATURE_SINGULAR=$NEW_FEATURE
fi
if ! [[ "$NEW_FEATURE_SINGULAR" =~ ^[a-z][a-zA-Z0-9]*$ ]]; then
    echo "Error: Singular feature name must be lowerCamelCase (e.g. task). Got: $NEW_FEATURE_SINGULAR" >&2
    exit 1
fi

# Resolve template directory (parent of where this script lives)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd -P )"
TEMPLATE_DIR="$( cd "$SCRIPT_DIR/.." && pwd -P )"

echo "--- Project Initialization ---"
echo "Template: $TEMPLATE_DIR"
echo "Target Path: $TARGET_PATH"
echo "New Project Name: $NEW_PROJECT_NAME"
echo "New Package: $NEW_PACKAGE"
echo "New Feature: $NEW_FEATURE (singular: $NEW_FEATURE_SINGULAR)"
echo "------------------------------"

# 1. Create target directory and copy template
if [ -e "$TARGET_PATH" ]; then
    echo "Error: Target path already exists. Please provide a new path or delete the existing one." >&2
    exit 1
fi

# Every rewrite below runs inside the copy; refuse targets inside the template so it is never modified.
# Resolve the nearest existing ancestor (symlinks included) before creating anything.
EXISTING_PARENT="$TARGET_PATH"
while [ ! -d "$EXISTING_PARENT" ]; do
    EXISTING_PARENT="$(dirname "$EXISTING_PARENT")"
done
case "$( cd "$EXISTING_PARENT" && pwd -P )/" in
    "$TEMPLATE_DIR/"*)
        echo "Error: Target path must be outside the template directory ($TEMPLATE_DIR)." >&2
        exit 1
        ;;
esac

mkdir -p "$TARGET_PATH"
TARGET_DIR="$( cd "$TARGET_PATH" && pwd -P )"

echo "Copying template to target path..."
# Copy files excluding build artifacts, IDE/git metadata, local secrets and this script
# (scripts/create_feature.sh is kept and rewritten for the new package).
EXCLUDES=(.git .gradle .idea .kotlin .vscode .DS_Store build bin captures .cxx .externalNativeBuild local.properties .env)
if command -v rsync >/dev/null 2>&1; then
    RSYNC_ARGS=()
    for pattern in "${EXCLUDES[@]}"; do
        RSYNC_ARGS+=("--exclude=$pattern")
    done
    rsync -a "${RSYNC_ARGS[@]}" --exclude='/scripts/init_project.sh' "$TEMPLATE_DIR/" "$TARGET_DIR/"
else
    cp -R "$TEMPLATE_DIR/." "$TARGET_DIR/"
    for pattern in "${EXCLUDES[@]}"; do
        find "$TARGET_DIR" -name "$pattern" -prune -exec rm -rf {} +
    done
    rm -f "$TARGET_DIR/scripts/init_project.sh"
fi

cd "$TARGET_DIR"

# README.md and CLAUDE.md describe this script, which the copy doesn't have: drop their template-only blocks.
for doc in README.md CLAUDE.md; do
    if [ -f "$doc" ]; then
        perl -0pi -e 's/^<!-- template-only[^\n]*-->\n.*?^<!-- \/template-only -->\n//msg' "$doc"
    fi
done

# 2. Rewrite file contents and paths.
# Order matters: feature names first (so a new package containing "todo" is left alone), then the
# package in dotted (Kotlin, schemas dir) and slashed (source dirs, baseline profiles) form, then the
# project name and the remaining lowercase "androidtemplate" (plugin ids, database name).
# Feature names are matched as identifier parts, case-sensitively: Todos/Todo (TodosScreen, AddTodoViewModel),
# todos/todo (:feature:todos, todoDao, todo_title), and the "addtodo" package. "TODO" comments are untouched.
echo "Rewriting file contents and paths..."
export OLD_PACKAGE="com.aragabz.androidtemplate"
export OLD_PROJECT_NAME="AndroidTemplate"
export NEW_PACKAGE NEW_PROJECT_NAME NEW_FEATURE NEW_FEATURE_SINGULAR
export LAST_PART="${NEW_PACKAGE##*.}"

perl -e '
use strict;
use warnings;
use File::Find;
use File::Path qw(make_path);
use File::Basename qw(dirname);

my %e = %ENV;
my ($old_pkg, $new_pkg) = ($e{OLD_PACKAGE}, $e{NEW_PACKAGE});
(my $old_pkg_path = $old_pkg) =~ tr{.}{/};
(my $new_pkg_path = $new_pkg) =~ tr{.}{/};
my ($plural, $singular) = ($e{NEW_FEATURE}, $e{NEW_FEATURE_SINGULAR});
my ($plural_cap, $singular_cap) = (ucfirst $plural, ucfirst $singular);
my $add_pkg = "add" . lc $singular;

sub rewrite {
    my ($s) = @_;
    $s =~ s/addtodo/$add_pkg/g;
    $s =~ s/Todos(?![a-z])/$plural_cap/g;
    $s =~ s/Todo(?![a-z])/$singular_cap/g;
    $s =~ s/(?<![A-Za-z])todos(?![a-z])/$plural/g;
    $s =~ s/(?<![A-Za-z])todo(?![a-z])/$singular/g;
    $s =~ s/\Q$old_pkg\E/$new_pkg/g;
    $s =~ s/\Q$old_pkg_path\E/$new_pkg_path/g;
    $s =~ s/\Q$e{OLD_PROJECT_NAME}\E/$e{NEW_PROJECT_NAME}/g;
    $s =~ s/(?<![A-Za-z0-9])androidtemplate/$e{LAST_PART}/g;
    return $s;
}

# Renamed packages can change import order; re-sort each import block the way ktlint expects
# (ij_kotlin_imports_layout = *,java.**,javax.**,kotlin.**,^ in .editorconfig).
sub import_key {
    my ($line) = @_;
    (my $path = $line) =~ s/^import\s+|\s+$//g;
    my $group = $path =~ / as / ? 4 : $path =~ /^java\./ ? 1 : $path =~ /^javax\./ ? 2 : $path =~ /^kotlin\./ ? 3 : 0;
    return "$group $path";
}

sub sort_imports {
    my ($s) = @_;
    $s =~ s{((?:^import [^\n]*\n)+)}{
        my $block = $1;
        join "", sort { import_key($a) cmp import_key($b) } ($block =~ /^(import [^\n]*\n)/mg);
    }mge;
    return $s;
}

my @files;
find({ no_chdir => 1, wanted => sub { push @files, $File::Find::name if -f $_ && !-l $_ } }, ".");

for my $file (@files) {
    next if -B $file;
    open my $in, "<:raw", $file or die "read $file: $!";
    my $content = do { local $/; <$in> };
    close $in;
    my $updated = rewrite($content);
    next if $updated eq $content;
    $updated = sort_imports($updated) if $file =~ /\.kts?$/;
    open my $out, ">:raw", $file or die "write $file: $!";
    print $out $updated;
    close $out;
}

for my $file (@files) {
    my $target = rewrite($file);
    next if $target eq $file;
    die "Refusing to overwrite $target\n" if -e $target;
    make_path(dirname($target));
    rename $file, $target or die "rename $file -> $target: $!";
}

finddepth({ no_chdir => 1, wanted => sub { rmdir $_ if -d $_ } }, ".");
'
chmod +x scripts/*.sh 2>/dev/null || true

echo -e "\nProject successfully initialized at: $TARGET_DIR"
echo "Next steps:"
echo "1. cd $TARGET_DIR && git init"
echo "2. ./gradlew assembleDebug (or open the project in Android Studio and sync Gradle)"
echo "3. ./gradlew createModuleGraph to refresh the module graph in README.md"
