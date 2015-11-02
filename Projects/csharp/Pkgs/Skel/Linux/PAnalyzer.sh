#!/bin/sh

MONO=`which mono`
if [ $? -ne 0 ]; then
    echo "Mono .NET runtime is required for running PAnalyzer"
    echo "Do you want to install it (y/n)?"
    read OPT
    if [ "$OPT" = "y" -o "$OPT" = "Y" ]; then
        sudo apt-get install mono-runtime
        if [ $? -ne 0 ]; then
            exit
        fi
    else
        exit
    fi
fi
$MONO PAnalyzer.exe
