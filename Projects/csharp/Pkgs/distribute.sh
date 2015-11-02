#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Usage: $0 <X.Y>"
    exit
fi
VER="$1"

ZIP="PAnalyzer.zip"
UNZIP="PAnalyzer"
if [ ! -f "$ZIP" ]; then
    echo "$ZIP not found"
    exit
fi

DIR="PAnalyzer-v$VER"
SKEL="../Skel"
if [ -d "$DIR" -o -f "$DIR.zip" ]; then
    echo "Packages already exist"
    exit
fi
mkdir -p "$DIR"

create-macosx() {
    local BASE="$SKEL/MacOSX"
    local DST="PAnalyzer-v$VER-MacOSX"

    echo -e "\n\n ----- MacOS X -----\n"
    svn export "$BASE" "$DST" &&
    cp -f "$UNZIP"/*.dll "$UNZIP"/*.exe "$DST"/PAnalyzer.app/Contents/MacOS &&
    zip -r "$DST.zip" "$DST" &&
    rm -rf "$DST"

    if [ $? -ne 0 ]; then
        echo "Error creating $DST.zip"
        false
    else
        echo "$DST.zip created successfully!!"
    fi
}

create-windows() {
    local BASE="$SKEL/Windows"
    local DST="PAnalyzer-v$VER-Windows"

    echo -e "\n\n ----- Windows -----\n"
    svn export "$BASE" "$DST" &&
    cp -f "$UNZIP"/* "$DST" &&
    zip -r "$DST.zip" "$DST" &&
    rm -rf "$DST"

    if [ $? -ne 0 ]; then
        echo "Error creating $DST.zip"
        false
    else
        echo "$DST.zip created successfully!!"
    fi
}

create-linux() {
    local BASE="$SKEL/Linux"
    local DST="PAnalyzer-v$VER-Linux"

    echo -e "\n\n ----- Linux -----\n"
    svn export "$BASE" "$DST" &&
    cp -f "$UNZIP"/* "$DST" &&
    tar -czvf "$DST.tar.gz" "$DST" &&
    rm -rf "$DST"

    if [ $? -ne 0 ]; then
        echo "Error creating $DST.tar.gz"
        false
    else
        echo "$DST.tar.gz created successfully!!"
    fi
}

cd "$DIR" &&
unzip "../$ZIP" &&
create-macosx &&
create-windows &&
create-linux &&
rm -rf "$UNZIP" &&
cd .. &&
mv "$ZIP" "$DIR.zip"

if [ $? -ne 0 ]; then
    echo -e "\n\nError creating packages"
else
   echo -e "\n\nPackages created successfully!!"
fi
