#!/bin/bash
set -e

# --- Configuration Helper Functions ---

# Function to read value from .jules.yaml
# Usage: get_yaml_value "key" "default_value"
get_yaml_value() {
    local key=$1
    local default_val=$2
    local yaml_file=".jules.yaml"

    if [ -f "$yaml_file" ]; then
        # Grep the key, look for the value after the colon, remove quotes and surrounding whitespace
        # We use awk to handle the separator and sed to remove quotes
        local val=$(grep "^[[:space:]]*${key}:" "$yaml_file" | awk -F': ' '{print $2}' | sed 's/^"//;s/"$//' | sed "s/^'//;s/'$//")
        if [ -n "$val" ]; then
            echo "$val"
            return
        fi
    fi
    echo "$default_val"
}

# --- Resolve Configuration ---

# Precedence: Env Var > .jules.yaml > Default

# Android Home
if [ -z "$ANDROID_HOME" ]; then
    export ANDROID_HOME="$HOME/android-sdk"
fi

# Cmdline Tools URL
DEFAULT_CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"
YAML_CMDLINE_TOOLS_URL=$(get_yaml_value "cmdline_tools_url" "$DEFAULT_CMDLINE_TOOLS_URL")
CMDLINE_TOOLS_URL="${ANDROID_CMDLINE_TOOLS_URL:-$YAML_CMDLINE_TOOLS_URL}"

# Platform Version
DEFAULT_PLATFORM_VERSION="android-36"
YAML_PLATFORM_VERSION=$(get_yaml_value "platform_version" "$DEFAULT_PLATFORM_VERSION")
PLATFORM_VERSION="${ANDROID_PLATFORM_VERSION:-$YAML_PLATFORM_VERSION}"

# Build Tools Version
DEFAULT_BUILD_TOOLS_VERSION="34.0.0"
YAML_BUILD_TOOLS_VERSION=$(get_yaml_value "build_tools_version" "$DEFAULT_BUILD_TOOLS_VERSION")
BUILD_TOOLS_VERSION="${ANDROID_BUILD_TOOLS_VERSION:-$YAML_BUILD_TOOLS_VERSION}"


# --- Setup Environment ---

export CMDLINE_TOOLS_ROOT="$ANDROID_HOME/cmdline-tools"
export PATH="$CMDLINE_TOOLS_ROOT/latest/bin:$ANDROID_HOME/platform-tools:$PATH"

echo "=== Android Environment Setup ==="
echo "ANDROID_HOME: $ANDROID_HOME"
echo "Platform: $PLATFORM_VERSION"
echo "Build Tools: $BUILD_TOOLS_VERSION"
echo "================================="

# Create directory structure
mkdir -p "$CMDLINE_TOOLS_ROOT"

# --- Install Command Line Tools ---

if [ -f "$CMDLINE_TOOLS_ROOT/latest/bin/sdkmanager" ]; then
    echo "Command Line Tools already installed in $CMDLINE_TOOLS_ROOT/latest"
else
    echo "Downloading Android Command Line Tools..."
    curl -o cmdline-tools.zip "$CMDLINE_TOOLS_URL"

    echo "Extracting..."
    unzip -q cmdline-tools.zip -d "$CMDLINE_TOOLS_ROOT"

    # Restructure: move content of 'cmdline-tools' to 'latest'
    if [ -d "$CMDLINE_TOOLS_ROOT/cmdline-tools" ]; then
        # Ensure 'latest' does not exist before moving to avoid nesting or errors
        rm -rf "$CMDLINE_TOOLS_ROOT/latest"
        mv "$CMDLINE_TOOLS_ROOT/cmdline-tools" "$CMDLINE_TOOLS_ROOT/latest"
    fi

    rm cmdline-tools.zip
    echo "Command Line Tools installed."
fi

# --- Install SDK Components ---

echo "Verifying SDK components..."

# Check if platform is installed
PLATFORM_PATH="$ANDROID_HOME/platforms/$PLATFORM_VERSION"
BUILD_TOOLS_PATH="$ANDROID_HOME/build-tools/$BUILD_TOOLS_VERSION"
PLATFORM_TOOLS_PATH="$ANDROID_HOME/platform-tools"

COMPONENTS_TO_INSTALL=""

if [ ! -d "$PLATFORM_TOOLS_PATH" ]; then
    COMPONENTS_TO_INSTALL="$COMPONENTS_TO_INSTALL platform-tools"
else
    echo "  - platform-tools: Installed"
fi

if [ ! -d "$PLATFORM_PATH" ]; then
    COMPONENTS_TO_INSTALL="$COMPONENTS_TO_INSTALL platforms;$PLATFORM_VERSION"
else
    echo "  - platforms;$PLATFORM_VERSION: Installed"
fi

if [ ! -d "$BUILD_TOOLS_PATH" ]; then
    COMPONENTS_TO_INSTALL="$COMPONENTS_TO_INSTALL build-tools;$BUILD_TOOLS_VERSION"
else
    echo "  - build-tools;$BUILD_TOOLS_VERSION: Installed"
fi

if [ -n "$COMPONENTS_TO_INSTALL" ]; then
    echo "Installing missing components: $COMPONENTS_TO_INSTALL"
    yes | sdkmanager --licenses > /dev/null
    # shellcheck disable=SC2086
    sdkmanager $COMPONENTS_TO_INSTALL
else
    echo "All required components are already installed."
fi

echo ""
echo "Android SDK setup complete at $ANDROID_HOME"
echo "To configure your current shell, run:"
echo "export ANDROID_HOME=$ANDROID_HOME"
echo "export PATH=\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools:\$PATH"
