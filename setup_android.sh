#!/bin/bash
set -e

# Define paths
export ANDROID_HOME=$HOME/android-sdk
export CMDLINE_TOOLS_ROOT=$ANDROID_HOME/cmdline-tools
export PATH=$CMDLINE_TOOLS_ROOT/latest/bin:$ANDROID_HOME/platform-tools:$PATH

# Create directory structure
mkdir -p $CMDLINE_TOOLS_ROOT

# Download Command Line Tools
echo "Downloading Android Command Line Tools..."
curl -o cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip

# Unzip and restructure
echo "Extracting..."
unzip -q cmdline-tools.zip -d $CMDLINE_TOOLS_ROOT
# The zip contains a 'cmdline-tools' folder at the root. We need to move its content to 'latest'.
mv $CMDLINE_TOOLS_ROOT/cmdline-tools $CMDLINE_TOOLS_ROOT/latest

# Clean up zip
rm cmdline-tools.zip

# Install SDK components
echo "Installing SDK components..."
yes | sdkmanager --licenses > /dev/null
sdkmanager "platform-tools" "platforms;android-36" "build-tools;34.0.0"

echo "Android SDK setup complete at $ANDROID_HOME"
echo "Export environment variables with:"
echo "export ANDROID_HOME=$HOME/android-sdk"
echo "export PATH=\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools:\$PATH"
